package fr.bibliotheque.book.config;

import java.util.List;

import fr.bibliotheque.book.domain.Book;
import fr.bibliotheque.book.domain.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/** Remplit le catalogue au premier démarrage (base vide) pour avoir des données de démonstration. */
@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final BookRepository repository;

    public DataInitializer(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }
        try {
            repository.saveAll(List.of(
                    new Book("9782070360024", "L'Étranger", "Albert Camus", "Roman", 1942,
                            "Meursault, employé de bureau à Alger, commet un meurtre absurde sur une plage.", 3),
                    new Book("9782070413119", "Le Petit Prince", "Antoine de Saint-Exupéry", "Conte", 1943,
                            "Un aviateur échoué dans le désert rencontre un petit prince venu d'une autre planète.", 4),
                    new Book("9782253006329", "Les Misérables", "Victor Hugo", "Roman", 1862,
                            "Le destin de Jean Valjean, ancien bagnard, dans la France du XIXe siècle.", 2),
                    new Book("9782070368228", "1984", "George Orwell", "Science-fiction", 1949,
                            "Winston Smith tente de résister à Big Brother dans un État totalitaire.", 3),
                    new Book("9782253004226", "Germinal", "Émile Zola", "Roman", 1885,
                            "La révolte des mineurs du Nord menée par Étienne Lantier.", 2),
                    new Book("9782070584628", "Harry Potter à l'école des sorciers", "J. K. Rowling", "Fantasy", 1997,
                            "Un jeune orphelin découvre qu'il est sorcier et entre à Poudlard.", 5),
                    new Book("9782266282369", "Dune", "Frank Herbert", "Science-fiction", 1965,
                            "Sur la planète désertique Arrakis, Paul Atréides affronte son destin.", 2),
                    new Book("9782070409341", "Madame Bovary", "Gustave Flaubert", "Roman", 1857,
                            "Emma Bovary fuit l'ennui de sa vie provinciale dans les rêves et les dettes.", 1),
                    new Book("9782253002864", "Le Comte de Monte-Cristo", "Alexandre Dumas", "Aventure", 1844,
                            "Edmond Dantès, injustement emprisonné, prépare sa vengeance.", 2),
                    new Book("9782016265635", "Clean Code", "Robert C. Martin", "Informatique", 2008,
                            "Les principes pour écrire un code lisible et maintenable.", 3),
                    new Book("9781492034025", "Kubernetes: Up and Running", "Brendan Burns", "Informatique", 2019,
                            "Les bases pour déployer et gérer des applications sur Kubernetes.", 2),
                    new Book("9782081510432", "Le Seigneur des anneaux", "J. R. R. Tolkien", "Fantasy", 1954,
                            "Frodon doit détruire l'Anneau unique avant que Sauron ne le récupère.", 2)
            ));
            log.info("Catalogue initialisé avec {} livres de démonstration", repository.count());
        } catch (DataIntegrityViolationException e) {
            // Deux réplicas peuvent démarrer en même temps : l'autre a déjà inséré les données.
            log.info("Données de démonstration déjà insérées par un autre réplica");
        }
    }
}
