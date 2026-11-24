package fr.bibliotheque.book.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    List<Book> findAllByOrderByTitleAsc();

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByTitleAsc(String title, String author);
}
