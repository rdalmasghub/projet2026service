package fr.bibliotheque.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Un livre du catalogue. Le stock est suivi avec deux compteurs :
 * totalCopies (exemplaires possédés) et availableCopies (exemplaires en rayon).
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String genre;

    private Integer publicationYear;

    @Column(length = 2000)
    private String summary;

    @Column(nullable = false)
    private int totalCopies;

    @Column(nullable = false)
    private int availableCopies;

    protected Book() {
        // requis par JPA
    }

    public Book(String isbn, String title, String author, String genre, Integer publicationYear,
                String summary, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.summary = summary;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    /** Nombre d'exemplaires actuellement empruntés. */
    public int borrowedCopies() {
        return totalCopies - availableCopies;
    }

    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public Integer getPublicationYear() { return publicationYear; }
    public String getSummary() { return summary; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}
