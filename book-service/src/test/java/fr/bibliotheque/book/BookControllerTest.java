package fr.bibliotheque.book;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:controller-test")
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void listsSeededBooks() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Served-By", startsWith("book-service@")))
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(10)));
    }

    @Test
    void searchesByAuthor() throws Exception {
        mvc.perform(get("/api/books").param("q", "hugo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Les Misérables"));
    }

    @Test
    void createsBook() throws Exception {
        String json = """
                {"isbn":"978-2-07-000000-1","title":"Le Rouge et le Noir","author":"Stendhal",
                 "genre":"Roman","publicationYear":1830,"totalCopies":2}
                """;
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/books/")))
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void rejectsInvalidBook() throws Exception {
        String json = """
                {"isbn":"abc","title":"","author":"X","totalCopies":0}
                """;
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.isbn").exists())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.totalCopies").exists());
    }

    @Test
    void unknownBookReturns404() throws Exception {
        mvc.perform(get("/api/books/424242"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Livre introuvable"));
    }

    @Test
    void updatesThenDeletesBook() throws Exception {
        String json = """
                {"isbn":"9782070000002","title":"Bel-Ami","author":"Maupassant","totalCopies":1}
                """;
        String location = mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON)
                        .content(json.replace("\"totalCopies\":1", "\"totalCopies\":3")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCopies").value(3));

        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }
}
