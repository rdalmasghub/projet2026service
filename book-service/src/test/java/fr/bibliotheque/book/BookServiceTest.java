package fr.bibliotheque.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.bibliotheque.book.domain.Book;
import fr.bibliotheque.book.domain.BookNotFoundException;
import fr.bibliotheque.book.domain.BookRequest;
import fr.bibliotheque.book.domain.BookService;
import fr.bibliotheque.book.domain.BusinessRuleException;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {"app.seed-data=false", "spring.datasource.url=jdbc:h2:mem:service-test"})
@Transactional
class BookServiceTest {

    @Autowired
    BookService service;

    private Book createBook(String isbn, int copies) {
        return service.create(new BookRequest(isbn, "Titre " + isbn, "Auteur", "Roman", 2000, null, copies));
    }

    @Test
    void newBookHasAllCopiesAvailable() {
        Book book = createBook("978-0000000001", 3);
        assertThat(book.getIsbn()).isEqualTo("9780000000001");
        assertThat(book.getAvailableCopies()).isEqualTo(3);
    }

    @Test
    void duplicateIsbnIsRejected() {
        createBook("9780000000002", 1);
        assertThatThrownBy(() -> createBook("978-0000000002", 1)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void updateChangesTitleAndCopies() {
        Book book = createBook("9780000000003", 2);
        BookRequest changes = new BookRequest("9780000000003", "Nouveau titre", "Auteur", null, null, null, 5);
        Book updated = service.update(book.getId(), changes);
        assertThat(updated.getTitle()).isEqualTo("Nouveau titre");
        assertThat(updated.getAvailableCopies()).isEqualTo(5);
    }

    @Test
    void deleteRemovesBook() {
        Book book = createBook("9780000000004", 1);
        service.delete(book.getId());
        assertThatThrownBy(() -> service.findById(book.getId())).isInstanceOf(BookNotFoundException.class);
    }
}
