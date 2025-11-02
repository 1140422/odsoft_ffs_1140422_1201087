package pt.psoft.g1.psoftg1.authormanagement.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Based on https://www.baeldung.com/spring-boot-testing
 * <p>Adaptations to Junit 5 with ChatGPT
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AuthorRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void whenFindByName_thenReturnAuthor() {
        // given
        Author alex = new Author("Alex", "O Alex escreveu livros", null);
        entityManager.persist(alex);
        entityManager.flush();

        // when
        List<Author> list = authorRepository.searchByNameName(alex.getName());

        // then
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getName())
                .isEqualTo(alex.getName());
    }

    @Test
    void whenFindByNonExistentName_thenReturnEmpty() {
        // given
        Author alex = new Author("Alex", "Bio", null);
        entityManager.persist(alex);
        entityManager.flush();

        // when
        List<Author> list = authorRepository.searchByNameName("NonExistent");

        // then
        assertThat(list).isEmpty();
    }

    @Test
    void whenMultipleAuthorsWithSimilarNames_thenReturnAll() {
        // given
        Author john1 = new Author("John Smith", "First John", null);
        Author john2 = new Author("John Doe", "Second John", null);
        Author jane = new Author("Jane Doe", "Not John", null);

        entityManager.persist(john1);
        entityManager.persist(john2);
        entityManager.persist(jane);
        entityManager.flush();

        // when - assuming partial match works
        List<Author> johns = authorRepository.searchByNameNameStartsWith("John");

        // then
        assertThat(johns).hasSize(2);
        assertThat(johns).extracting(Author::getName)
                .contains("John Smith", "John Doe");
    }

    @Test
    void whenNameContainsSpecialCharacters_thenSearchWorks() {
        // given
        Author author = new Author("O'Connor-Smith", "Irish author", null);
        entityManager.persist(author);
        entityManager.flush();

        // when
        List<Author> result = authorRepository.searchByNameName("O'Connor-Smith");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("O'Connor-Smith");
    }
}
