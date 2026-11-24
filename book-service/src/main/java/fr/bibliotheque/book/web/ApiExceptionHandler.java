package fr.bibliotheque.book.web;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.bibliotheque.book.domain.BookNotFoundException;
import fr.bibliotheque.book.domain.BusinessRuleException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Transforme les exceptions en réponses HTTP au format standard "Problem Details" (RFC 9457). */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ProblemDetail notFound(BookNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Livre introuvable", ex.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail conflict(BusinessRuleException ex) {
        return problem(HttpStatus.CONFLICT, "Règle métier non respectée", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Données invalides", "Certains champs sont invalides");
        problem.setProperty("errors", errors);
        return problem;
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
