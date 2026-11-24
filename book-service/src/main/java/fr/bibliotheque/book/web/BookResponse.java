package fr.bibliotheque.book.web;

import fr.bibliotheque.book.domain.Book;

/** Représentation JSON d'un livre renvoyée par l'API REST. */
public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        String genre,
        Integer publicationYear,
        String summary,
        int totalCopies,
        int availableCopies
) {
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getPublicationYear(), book.getSummary(), book.getTotalCopies(), book.getAvailableCopies());
    }
}
