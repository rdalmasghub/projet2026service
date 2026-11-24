package fr.bibliotheque.book.domain;

/** Violation d'une règle métier (ISBN en double, suppression d'un livre emprunté...). Renvoyée en HTTP 409. */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
