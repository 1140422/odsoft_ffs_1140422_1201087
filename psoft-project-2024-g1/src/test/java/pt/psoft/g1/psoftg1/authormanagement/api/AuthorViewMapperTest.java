package pt.psoft.g1.psoftg1.authormanagement.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookShortView;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class AuthorViewMapperTest {


    private final AuthorViewMapper authorViewMapper = Mappers.getMapper(AuthorViewMapper.class);

    private Author author;
    private Book book;

    private static final String AUTHOR_NAME = "John Doe";
    private static final String ISBN = "978-3-16-148410-0";


    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        author = mock(Author.class);
        when(author.getId()).thenReturn(1L);
        when(author.getAuthorNumber()).thenReturn(1L);
        when(author.getName()).thenReturn("John Doe");
        when(author.getBio()).thenReturn("A great author");

        book = mock(Book.class);
        when(book.getIsbn()).thenReturn("978-3-16-148410-0");
    }

    @Test
    void shouldMapAuthorToAuthorView() {
        // when
        AuthorView result = authorViewMapper.toAuthorView(author);

        // then
        assertNotNull(result);
        assertNotNull(result.get_links());
        assertTrue(result.get_links().containsKey("author"));
        assertTrue(result.get_links().containsKey("photo"));
        assertTrue(result.get_links().containsKey("booksByAuthor"));
    }

    @Test
    void shouldMapListOfAuthors() {
        List<AuthorView> list = authorViewMapper.toAuthorView(List.of(author));

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(AUTHOR_NAME, list.get(0).getName());
    }


    @Test
    void shouldMapBookToBookShortView() {
        BookShortView shortView = authorViewMapper.toBookShortView(book);
        assertNotNull(shortView);
        assertTrue(shortView.get_links().contains("/api/books/" + ISBN));
    }

    @Test
    void shouldMapAuthorToCoAuthorView() {
        List<Book> books = List.of(book);
        CoAuthorView coAuthorView = authorViewMapper.toCoAuthorView(author, books);

        assertNotNull(coAuthorView);
        assertEquals(AUTHOR_NAME, coAuthorView.getName());
        assertNotNull(coAuthorView.get_links());
        assertTrue(coAuthorView.get_links().containsKey("author"));
        assertNotNull(coAuthorView.getBooks());
        assertEquals(1, coAuthorView.getBooks().size());
    }

    @Test
    @DisplayName("Should map Author and CoAuthors to AuthorCoAuthorBooksView correctly")
    void shouldMapToAuthorCoAuthorBooksView() {
        CoAuthorView coAuthorView = new CoAuthorView(AUTHOR_NAME, Map.of("author", "link"), List.of());
        AuthorCoAuthorBooksView view = authorViewMapper.toAuthorCoAuthorBooksView(author, List.of(coAuthorView));

        assertNotNull(view);
        assertNotNull(view.getAuthor());
        assertEquals(AUTHOR_NAME, view.getCoauthors().get(0).getName());
    }

    @Test
    @DisplayName("Should map book short link correctly")
    void shouldMapShortBookLink() {
        String link = authorViewMapper.mapShortBookLink(book);
        assertTrue(link.contains("/api/books/" + ISBN));
    }

    @Test
    @DisplayName("AuthorLendingView constructors and fields work correctly")
    void testAuthorLendingView() {
        AuthorLendingView lendingView = new AuthorLendingView("John Doe", 5L);

        assertEquals("John Doe", lendingView.getAuthorName());
        assertEquals(5L, lendingView.getLendingCount());

        // Test no-args constructor and setters
        AuthorLendingView empty = new AuthorLendingView();
        empty.setAuthorName("Jane Doe");
        empty.setLendingCount(10L);

        assertEquals("Jane Doe", empty.getAuthorName());
        assertEquals(10L, empty.getLendingCount());

        assertNotEquals(lendingView, empty);
        assertTrue(lendingView.toString().contains("John Doe"));
    }



}
