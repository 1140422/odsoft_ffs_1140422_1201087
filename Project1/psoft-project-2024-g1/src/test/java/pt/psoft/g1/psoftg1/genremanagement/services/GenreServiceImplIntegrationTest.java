package pt.psoft.g1.psoftg1.genremanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GenreServiceImplIntegrationTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should find genre after saving")
    void shouldFindGenreAfterSaving() {
        // given
        Genre genre = new Genre("Science Fiction");
        genreRepository.save(genre);

        // when
        Optional<Genre> result = genreService.findByString("Science Fiction");

        // then
        assertTrue(result.isPresent());
        assertEquals("Science Fiction", result.get().getGenre());
    }


    @Test
    void shouldNotFindNonExistentGenre() {
        // when
        Optional<Genre> result = genreService.findByString("NonExistent");

        // then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldPersistGenreToDatabase() {
        // given
        Genre genre = new Genre("Mystery");

        // when
        Genre saved = genreService.save(genre);

        // then
        assertNotNull(saved);
        Optional<Genre> found = genreRepository.findByString("Mystery");
        assertTrue(found.isPresent());
        assertEquals("Mystery", found.get().getGenre());
    }

    @Test
    void shouldCalculateAverageLendingsForSpecificMonth() {
        // given
        GetAverageLendingsQuery query = new GetAverageLendingsQuery(2024, 1);
        Page page = new Page(1, 10);

        // when
        List<GenreLendingsDTO> result = genreService.getAverageLendings(query, page);

        // then
        assertNotNull(result);
    }

}
