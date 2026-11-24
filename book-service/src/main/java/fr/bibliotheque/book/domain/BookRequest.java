package fr.bibliotheque.book.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Données reçues pour créer ou modifier un livre (validées par Bean Validation). */
public record BookRequest(
        @NotBlank(message = "L'ISBN est obligatoire")
        @Pattern(regexp = "^[0-9Xx-]{10,17}$", message = "ISBN invalide (10 à 17 caractères : chiffres, X ou tirets)")
        String isbn,

        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 255)
        String title,

        @NotBlank(message = "L'auteur est obligatoire")
        @Size(max = 255)
        String author,

        @Size(max = 100)
        String genre,

        @Min(value = 0, message = "Année invalide")
        @Max(value = 2100, message = "Année invalide")
        Integer publicationYear,

        @Size(max = 2000)
        String summary,

        @Min(value = 1, message = "Il faut au moins un exemplaire")
        @Max(value = 1000, message = "1000 exemplaires maximum")
        int totalCopies
) {
}
