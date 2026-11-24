package fr.bibliotheque.book.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Logique métier du catalogue, utilisée par l'API REST (BookController).
 */
@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> search(String query) {
        if (!StringUtils.hasText(query)) {
            return repository.findAllByOrderByTitleAsc();
        }
        String q = query.trim();
        return repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByTitleAsc(q, q);
    }

    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public Book create(BookRequest request) {
        String isbn = normalizeIsbn(request.isbn());
        if (repository.existsByIsbn(isbn)) {
            throw new BusinessRuleException("Un livre avec l'ISBN " + isbn + " existe déjà");
        }
        Book book = new Book(isbn, request.title().trim(), request.author().trim(), request.genre(),
                request.publicationYear(), request.summary(), request.totalCopies());
        return repository.save(book);
    }

    @Transactional
    public Book update(Long id, BookRequest request) {
        Book book = findById(id);
        String isbn = normalizeIsbn(request.isbn());
        if (repository.existsByIsbnAndIdNot(isbn, id)) {
            throw new BusinessRuleException("Un autre livre utilise déjà l'ISBN " + isbn);
        }
        int borrowed = book.borrowedCopies();
        if (request.totalCopies() < borrowed) {
            throw new BusinessRuleException(borrowed + " exemplaire(s) sont empruntés : impossible de descendre à "
                    + request.totalCopies() + " exemplaire(s)");
        }
        book.setIsbn(isbn);
        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setGenre(request.genre());
        book.setPublicationYear(request.publicationYear());
        book.setSummary(request.summary());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.totalCopies() - borrowed);
        return book;
    }

    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        if (book.borrowedCopies() > 0) {
            throw new BusinessRuleException("Impossible de supprimer « " + book.getTitle() + " » : "
                    + book.borrowedCopies() + " exemplaire(s) en cours d'emprunt");
        }
        repository.delete(book);
    }

    private static String normalizeIsbn(String isbn) {
        return isbn.replace("-", "").trim().toUpperCase();
    }
}
