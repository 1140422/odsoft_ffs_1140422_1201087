package pt.psoft.g1.psoftg1.bookmanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.services.CreateBookRequest;
import pt.psoft.g1.psoftg1.bookmanagement.services.UpdateBookRequest;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BookControllerIntegrationTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private BookRepository bookRepository;
//
//    @MockBean
//    private GenreRepository genreRepository;
//
//    @MockBean
//    private AuthorRepository authorRepository;
//
//    @MockBean
//    private FileStorageService fileStorageService;
//
//    @MockBean
//    private ConcurrencyService concurrencyService;
//
//    private static final String ISBN = "9783161484100";
//    private static final String TITLE = "O Senhor dos Anéis";
//    private static final String DESCRIPTION = "Uma história épica.";
//    private static final String GENRE_NAME = "Fantasia";
//
//    private Author author;
//    private Genre genre;
//    private Book book;
//
//    @BeforeEach
//    void setup() {
//        author = new Author("Tolkien", "Autor de fantasia épica", "tolkien.jpg");
//        genre = new Genre(GENRE_NAME);
//        book = new Book(ISBN, TITLE, DESCRIPTION, genre, List.of(author), null);
//        book.setVersion(1L);
//    }
//
//    @Test
//    void whenCreateBook_thenReturns201CreatedAndBookJson() throws Exception {
//        // Arrange
//        CreateBookRequest request = new CreateBookRequest();
//        request.setTitle(TITLE);
//        request.setDescription(DESCRIPTION);
//        request.setGenre(GENRE_NAME);
//        request.setAuthors(List.of(1L));
//
//        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.empty());
//        when(authorRepository.findByAuthorNumber(1L)).thenReturn(Optional.of(author));
//        when(genreRepository.findByString(GENRE_NAME)).thenReturn(Optional.of(genre));
//        when(bookRepository.save(any(Book.class))).thenReturn(book);
//        when(fileStorageService.getRequestPhoto(any())).thenReturn(null);
//
//        // Act + Assert
//        mockMvc.perform(put("/api/books/{isbn}", ISBN)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.isbn").value(ISBN))
//                .andExpect(jsonPath("$.title").value(TITLE))
//                .andExpect(jsonPath("$.genre.genre").value(GENRE_NAME));
//    }
//
//    @Test
//    void whenGetBookByIsbn_thenReturns200AndJson() throws Exception {
//        // Arrange
//        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(book));
//
//        // Act + Assert
//        mockMvc.perform(get("/api/books/{isbn}", ISBN))
//                .andExpect(status().isOk())
//                .andExpect(header().string("ETag", "1"))
//                .andExpect(jsonPath("$.isbn").value(ISBN))
//                .andExpect(jsonPath("$.title").value(TITLE))
//                .andExpect(jsonPath("$.genre.genre").value(GENRE_NAME));
//    }
//
//    @Test
//    void whenUpdateBook_thenReturns200AndUpdatedJson() throws Exception {
//        // Arrange
//        UpdateBookRequest updateRequest = new UpdateBookRequest();
//        updateRequest.setTitle("Novo Título");
//        updateRequest.setDescription("Nova descrição");
//
//        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(book));
//        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
//        when(fileStorageService.getRequestPhoto(any())).thenReturn(null);
//        when(concurrencyService.getVersionFromIfMatchHeader("1")).thenReturn(1L);
//
//        // Act + Assert
//        mockMvc.perform(patch("/api/books/{isbn}", ISBN)
//                        .header(HttpHeaders.IF_MATCH, "1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Novo Título"))
//                .andExpect(jsonPath("$.description").value("Nova descrição"));
//    }
//
//    @Test
//    void whenUpdateBookWithoutIfMatch_thenReturns400() throws Exception {
//        UpdateBookRequest updateRequest = new UpdateBookRequest();
//        updateRequest.setTitle("Novo Título");
//
//        mockMvc.perform(patch("/api/books/{isbn}", ISBN)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isBadRequest());
//    }
}
