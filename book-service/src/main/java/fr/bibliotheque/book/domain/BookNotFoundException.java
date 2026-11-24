package fr.bibliotheque.book.domain;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Livre introuvable : id=" + id);
    }
}
