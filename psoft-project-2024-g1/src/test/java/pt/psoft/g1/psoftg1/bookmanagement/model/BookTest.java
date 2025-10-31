package pt.psoft.g1.psoftg1.bookmanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.services.UpdateBookRequest;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookTest {
    private final String validIsbn = "9782826012092";
    private final String validTitle = "Encantos de contar";
    private Author validAuthor1;
    private Author validAuthor2;
    private Genre validGenre;
    private ArrayList<Author> authors = new ArrayList<>();

    @BeforeEach
    void setUp(){
        validAuthor1 = mock(Author.class);
        validAuthor2 = mock(Author.class);
        validGenre = mock(Genre.class);

        authors = new ArrayList<>();
        authors.clear();
    }

    @Test
    void ensureIsbnNotNull(){
        authors.add(validAuthor1);
        assertThrows(IllegalArgumentException.class, () -> new Book(null, validTitle, null, validGenre, authors, null));
    }

    @Test
    void ensureTitleNotNull(){
        authors.add(validAuthor1);
        assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, null, null, validGenre, authors, null));
    }

    @Test
    void ensureGenreNotNull(){
        authors.add(validAuthor1);
        assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, null,null, authors, null));
    }

    @Test
    void ensureAuthorsNotNull(){
        authors.add(validAuthor1);
        assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, null, validGenre, null, null));
    }

    @Test
    void ensureAuthorsNotEmpty(){
        assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, null, validGenre, authors, null));
    }

    @Test
    void ensureBookCreatedWithMultipleAuthors() {
        authors.add(validAuthor1);
        authors.add(validAuthor2);
        assertDoesNotThrow(() -> new Book(validIsbn, validTitle, null, validGenre, authors, null));
    }

    @Test
    void testApplyPatchUpdatesTitleAndDescription() {
        //Arrange
        authors.add(validAuthor1);
        Book book = new Book(validIsbn, validTitle, null, validGenre, authors, null);
        Long version = book.getVersion();

        UpdateBookRequest requestMock = mock(UpdateBookRequest.class);
        when(requestMock.getTitle()).thenReturn("Novo Título");
        when(requestMock.getDescription()).thenReturn("Nova Descrição");
        when(requestMock.getPhotoURI()).thenReturn("novaPhoto.jpg");
        when(requestMock.getGenreObj()).thenReturn(null);
        when(requestMock.getAuthorObjList()).thenReturn(null);

        //Act
        book.applyPatch(version, requestMock);

        //Assert
        assertEquals("Novo Título", book.getTitle().toString());
        assertEquals("Nova Descrição", book.getDescription());
        assertEquals(validGenre, book.getGenre());
        assertEquals(authors, book.getAuthors());
    }

    @Test
    void testApplyPatchUpdatesAuthorsAndGenre() {
        // Arrange
        authors.add(validAuthor1);
        Book book = new Book(validIsbn, validTitle, null, validGenre, authors, null);
        Long version = book.getVersion();

        Genre newGenre = mock(Genre.class);
        Author newAuthor = mock(Author.class);
        List<Author> newAuthors = List.of(newAuthor);

        UpdateBookRequest requestMock = mock(UpdateBookRequest.class);
        when(requestMock.getTitle()).thenReturn(null);
        when(requestMock.getDescription()).thenReturn(null);
        when(requestMock.getPhotoURI()).thenReturn(null);
        when(requestMock.getGenreObj()).thenReturn(newGenre);
        when(requestMock.getAuthorObjList()).thenReturn(newAuthors);

        // Act
        book.applyPatch(version, requestMock);

        // Assert
        assertEquals(newGenre, book.getGenre());
        assertEquals(newAuthors, book.getAuthors());
    }
}