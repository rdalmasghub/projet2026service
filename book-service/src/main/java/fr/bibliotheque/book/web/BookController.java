package fr.bibliotheque.book.web;

import java.net.URI;
import java.util.List;

import fr.bibliotheque.book.domain.Book;
import fr.bibliotheque.book.domain.BookRequest;
import fr.bibliotheque.book.domain.BookService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** API REST du catalogue, exposée derrière la gateway sous /api/books. */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> list(@RequestParam(name = "q", required = false) String query) {
        return service.search(query).stream().map(BookResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BookResponse get(@PathVariable Long id) {
        return BookResponse.from(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book book = service.create(request);
        return ResponseEntity.created(URI.create("/api/books/" + book.getId())).body(BookResponse.from(book));
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return BookResponse.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
