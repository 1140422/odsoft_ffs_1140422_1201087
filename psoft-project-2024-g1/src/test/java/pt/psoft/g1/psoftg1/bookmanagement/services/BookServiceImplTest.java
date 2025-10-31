package pt.psoft.g1.psoftg1.bookmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private ReaderRepository readerRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private static final String VALID_ISBN = "9783161484100";
    private static final String VALID_TITLE = "O Senhor dos Anéis";
    private static final String VALID_DESCRIPTION = "Uma história épica.";
    private static final String VALID_GENRE_NAME = "Fantasia";
    private static final Long VALID_AUTHOR_ID = 1L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookServiceImpl(bookRepository, genreRepository, authorRepository, photoRepository, readerRepository);
    }

    @Test
    void create_shouldThrowConflict_whenIsbnAlreadyExists() {
        // Arrange
        CreateBookRequest request = mock(CreateBookRequest.class);
        when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.of(mock(Book.class)));

        // Act + Assert
        assertThrows(ConflictException.class, () -> bookService.create(request, VALID_ISBN));
    }

    @Test
    void create_shouldThrowNotFound_whenGenreNotExists() {
        // Arrange
        CreateBookRequest request = mock(CreateBookRequest.class);
        when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.empty());
        when(request.getAuthors()).thenReturn(List.of(VALID_AUTHOR_ID));
        when(authorRepository.findByAuthorNumber(VALID_AUTHOR_ID)).thenReturn(Optional.of(mock(Author.class)));
        when(request.getGenre()).thenReturn(VALID_GENRE_NAME);
        when(genreRepository.findByString(VALID_GENRE_NAME)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> bookService.create(request, VALID_ISBN));
    }

    @Test
    void create_shouldSaveBook_whenValidRequest() {
        // Arrange
        CreateBookRequest request = mock(CreateBookRequest.class);
        when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.empty());
        when(request.getAuthors()).thenReturn(List.of(VALID_AUTHOR_ID));

        Author author = mock(Author.class);
        when(authorRepository.findByAuthorNumber(VALID_AUTHOR_ID)).thenReturn(Optional.of(author));

        Genre genre = mock(Genre.class);
        when(request.getGenre()).thenReturn(VALID_GENRE_NAME);
        when(genreRepository.findByString(VALID_GENRE_NAME)).thenReturn(Optional.of(genre));
        when(request.getTitle()).thenReturn(VALID_TITLE);
        when(request.getDescription()).thenReturn(VALID_DESCRIPTION);
        when(request.getPhoto()).thenReturn(null);
        when(request.getPhotoURI()).thenReturn(null);

        Book savedBook = mock(Book.class);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Act
        Book result = bookService.create(request, VALID_ISBN);

        // Assert
        assertEquals(savedBook, result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void update_shouldThrowNotFound_whenGenreNotExists() {
        // Arrange
        UpdateBookRequest request = mock(UpdateBookRequest.class);
        when(request.getIsbn()).thenReturn(VALID_ISBN);
        Book book = mock(Book.class);
        when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.of(book));
        when(request.getGenre()).thenReturn(VALID_GENRE_NAME);
        when(genreRepository.findByString(VALID_GENRE_NAME)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> bookService.update(request, "1"));
    }

    @Test
    void update_shouldApplyPatchAndSaveBook() {
        // Arrange
        UpdateBookRequest request = mock(UpdateBookRequest.class);
        Book book = mock(Book.class);
        when(request.getIsbn()).thenReturn(VALID_ISBN);
        when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        // Act
        Book result = bookService.update(request, "1");

        // Assert
        verify(book).applyPatch(1L, request);
        verify(bookRepository).save(book);
        assertEquals(book, result);
    }

    @Test
    void findByGenre_shouldReturnBooks() {
        List<Book> books = List.of(mock(Book.class));
        when(bookRepository.findByGenre(VALID_GENRE_NAME)).thenReturn(books);
        assertEquals(books, bookService.findByGenre(VALID_GENRE_NAME));
    }

    @Test
    void findByTitle_shouldReturnBooks() {
        List<Book> books = List.of(mock(Book.class));
        when(bookRepository.findByTitle(VALID_TITLE)).thenReturn(books);
        assertEquals(books, bookService.findByTitle(VALID_TITLE));
    }

    @Test
    void findByAuthorName_shouldReturnBooks() {
        List<Book> books = List.of(mock(Book.class));
        when(bookRepository.findByAuthorName("Autor%")).thenReturn(books);
        assertEquals(books, bookService.findByAuthorName("Autor"));
    }

    @Test
    void findByIsbn_shouldThrowNotFound_whenNotExists() {
        when(bookRepository.findByIsbn("9999999999999")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.findByIsbn("9999999999999"));
    }

    @Test
    void findByIsbn_shouldReturnBook_whenExists() {
        Book book = mock(Book.class);
        when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.of(book));
        assertEquals(book, bookService.findByIsbn(VALID_ISBN));
    }


    @Test
    void removeBookPhoto_shouldThrowNotFound_whenNoPhoto() {
        Book book = mock(Book.class);
        when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.of(book));
        when(book.getPhoto()).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookService.removeBookPhoto(VALID_ISBN, 1L));
    }

    @Test
    void searchBooks_shouldHandleNullPageAndQuery() {
        List<Book> books = List.of(mock(Book.class));
        when(bookRepository.searchBooks(any(Page.class), any(SearchBooksQuery.class))).thenReturn(books);
        assertEquals(books, bookService.searchBooks(null, null));
    }
}