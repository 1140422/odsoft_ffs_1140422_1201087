package pt.psoft.g1.psoftg1.bookmanagement.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("integrationtest")
@Transactional
public class BookSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Author savedAuthor;
    private Genre savedGenre;

    private final String validIsbn = "9783161484100";
    private final String validTitle = "Assassinato no Expresso Oriente";
    private final String validDescription = "Um clássico de Agatha Christie.";
    private final String validGenreName = "Mistério";

    @BeforeEach
    void setup() {
        savedGenre = new Genre(validGenreName);
        genreRepository.save(savedGenre);

        savedAuthor = new Author("Agatha Christie", "Escritora britânica de romances policiais", null);
        authorRepository.save(savedAuthor);
    }

    @Test
    void whenCreateAndFetchBook_thenBookIsPersistedAndReturned() throws Exception {
        mockMvc.perform(put("/api/books/{isbn}", validIsbn)
                        .param("title", validTitle)
                        .param("genre", validGenreName)
                        .param("description", validDescription)
                        .param("authors", String.valueOf(savedAuthor.getAuthorNumber()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().exists("ETag"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString(validIsbn)));

        mockMvc.perform(get("/api/books/{isbn}", validIsbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(validTitle))
                .andExpect(jsonPath("$.description").value(validDescription))
                .andExpect(jsonPath("$.genre").value(validGenreName));
    }

    @Test
    void whenSearchBookByGenre_thenReturnsList() throws Exception {
        mockMvc.perform(put("/api/books/{isbn}", validIsbn)
                        .param("title", validTitle)
                        .param("genre", validGenreName)
                        .param("description", validDescription)
                        .param("authors", String.valueOf(savedAuthor.getAuthorNumber()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/books")
                        .param("genre", validGenreName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title").value(validTitle));
    }

    @Test
    void whenDeleteBookPhoto_thenReturnsOkOrNotFound() throws Exception {
        mockMvc.perform(put("/api/books/{isbn}", validIsbn)
                        .param("title", validTitle)
                        .param("genre", validGenreName)
                        .param("description", validDescription)
                        .param("authors", String.valueOf(savedAuthor.getAuthorNumber()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/books/{isbn}/photo", validIsbn))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateBook_thenBookIsUpdatedAndVersionIncrements() throws Exception {
        mockMvc.perform(put("/api/books/{isbn}", validIsbn)
                        .param("title", validTitle)
                        .param("genre", validGenreName)
                        .param("description", validDescription)
                        .param("authors", String.valueOf(savedAuthor.getAuthorNumber()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().exists("ETag"));

        String etag = mockMvc.perform(get("/api/books/{isbn}", validIsbn))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("ETag");

        Genre newGenre = new Genre("Romance Policial");
        genreRepository.save(newGenre);

        String updatedTitle = "O Misterioso Caso em Londres";
        String updatedDescription = "Uma nova aventura da autora.";

        mockMvc.perform(patch("/api/books/{isbn}", validIsbn)
                        .header("If-Match", etag)
                        .param("title", updatedTitle)
                        .param("description", updatedDescription)
                        .param("genre", newGenre.getGenre())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"))
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.description").value(updatedDescription))
                .andExpect(jsonPath("$.genre").value(newGenre.getGenre()));

        String newEtag = mockMvc.perform(get("/api/books/{isbn}", validIsbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andReturn()
                .getResponse()
                .getHeader("ETag");

        assert !etag.equals(newEtag);
    }

}