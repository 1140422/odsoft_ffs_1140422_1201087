package pt.psoft.g1.psoftg1.authormanagement.api;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.services.*;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookView;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.shared.api.ListResponse;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.junit.jupiter.api.Assertions.*;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorControllerTest {

    @Mock private AuthorService authorService;

    @Mock private AuthorViewMapper authorViewMapper;

    @Mock private ConcurrencyService concurrencyService;

    @Mock private FileStorageService fileStorageService;

    @Mock private BookViewMapper bookViewMapper;

    @InjectMocks private AuthorController authorController;

    @Mock private WebRequest webRequest;

    @Mock private MultipartFile multipartFile;

    @InjectMocks private AuthorController controller;

    private static final Long AUTHOR_NUMBER = 1L;
    private static final String AUTHOR_NAME = "John Doe";
    private static final String AUTHOR_BIO = "A great author";
    private static final String PHOTO_URI = "photo.jpg";
    private static final Long VERSION = 1L;
    private static final String IF_MATCH_HEADER = "1";

    private Author author;
    private AuthorView authorView;
    private CreateAuthorRequest createRequest;
    private UpdateAuthorRequest updateRequest;

    @BeforeEach
    void setUp() {
        author = mock(Author.class);
        authorView = mock(AuthorView.class);
        createRequest = new CreateAuthorRequest();
        updateRequest = new UpdateAuthorRequest();

        //when(author.getVersion()).thenReturn(VERSION);
        //when(author.getAuthorNumber()).thenReturn(AUTHOR_NUMBER);
    }

    @Test
    void shouldCreateAuthorWithoutPhoto() {
        // given
        createRequest.setName(AUTHOR_NAME);
        createRequest.setBio(AUTHOR_BIO);
        createRequest.setPhoto(null);

        when(fileStorageService.getRequestPhoto(null)).thenReturn(null);
        when(authorService.create(any(CreateAuthorRequest.class))).thenReturn(author);
        when(authorViewMapper.toAuthorView(author)).thenReturn(authorView);
        when(author.getVersion()).thenReturn(VERSION);

        // simulate HTTP request context
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        ResponseEntity<AuthorView> response = authorController.create(createRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(authorView, response.getBody());
        assertEquals("\""+ VERSION + "\"", response.getHeaders().getETag());
        assertNull(createRequest.getPhotoURI());

        verify(fileStorageService).getRequestPhoto(null);
        verify(authorService).create(createRequest);
        verify(authorViewMapper).toAuthorView(author);

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldCreateAuthorWithPhoto() {
        // given
        createRequest.setName(AUTHOR_NAME);
        createRequest.setBio(AUTHOR_BIO);
        createRequest.setPhoto(multipartFile);
        createRequest.setPhotoURI("old-uri"); // Should be overridden

        when(fileStorageService.getRequestPhoto(multipartFile)).thenReturn(PHOTO_URI);
        when(authorService.create(any(CreateAuthorRequest.class))).thenReturn(author);
        when(authorViewMapper.toAuthorView(author)).thenReturn(authorView);

        // simulate HTTP request context
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        ResponseEntity<AuthorView> response = authorController.create(createRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(authorView, response.getBody());
        assertEquals(PHOTO_URI, createRequest.getPhotoURI());

        verify(fileStorageService).getRequestPhoto(multipartFile);
        verify(authorService).create(createRequest);

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldUpdateAuthorSuccessfully() {
        // given
        updateRequest.setName("Updated Name");

        when(webRequest.getHeader(ConcurrencyService.IF_MATCH)).thenReturn(IF_MATCH_HEADER);
        when(concurrencyService.getVersionFromIfMatchHeader(IF_MATCH_HEADER)).thenReturn(VERSION);
        when(fileStorageService.getRequestPhoto(null)).thenReturn(null);
        when(authorService.partialUpdate(eq(AUTHOR_NUMBER), any(), eq(VERSION))).thenReturn(author);
        when(authorViewMapper.toAuthorView(author)).thenReturn(authorView);
        when(author.getVersion()).thenReturn(VERSION);

        // simulate HTTP request context
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        ResponseEntity<AuthorView> response = authorController.partialUpdate(
                AUTHOR_NUMBER, webRequest, updateRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authorView, response.getBody());
        assertEquals("\""+ VERSION + "\"", response.getHeaders().getETag());

        verify(authorService).partialUpdate(AUTHOR_NUMBER, updateRequest, VERSION);

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldUpdateAuthorWithPhoto() {
        // given
        updateRequest.setPhoto(multipartFile);

        when(webRequest.getHeader(ConcurrencyService.IF_MATCH)).thenReturn(IF_MATCH_HEADER);
        when(concurrencyService.getVersionFromIfMatchHeader(IF_MATCH_HEADER)).thenReturn(VERSION);
        when(fileStorageService.getRequestPhoto(multipartFile)).thenReturn(PHOTO_URI);
        when(authorService.partialUpdate(eq(AUTHOR_NUMBER), any(), eq(VERSION))).thenReturn(author);
        when(authorViewMapper.toAuthorView(author)).thenReturn(authorView);

        // when
        ResponseEntity<AuthorView> response = authorController.partialUpdate(
                AUTHOR_NUMBER, webRequest, updateRequest);

        // then
        assertEquals(PHOTO_URI, updateRequest.getPhotoURI());
        verify(fileStorageService).getRequestPhoto(multipartFile);
    }

    @Test
    void shouldThrowExceptionWhenIfMatchIsNull() {
        // given
        when(webRequest.getHeader(ConcurrencyService.IF_MATCH)).thenReturn(null);

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authorController.partialUpdate(AUTHOR_NUMBER, webRequest, updateRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("if-match"));

        verify(authorService, never()).partialUpdate(anyLong(), any(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenIfMatchIsNullString() {
        // given
        when(webRequest.getHeader(ConcurrencyService.IF_MATCH)).thenReturn("null");

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authorController.partialUpdate(AUTHOR_NUMBER, webRequest, updateRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    @Test
    void shouldFindAuthorByNumber() {
        // given
        when(authorService.findByAuthorNumber(AUTHOR_NUMBER)).thenReturn(Optional.of(author));
        when(authorViewMapper.toAuthorView(author)).thenReturn(authorView);
        when(author.getVersion()).thenReturn(VERSION);

        // when
        ResponseEntity<AuthorView> response = authorController.findByAuthorNumber(AUTHOR_NUMBER);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authorView, response.getBody());
        assertEquals("\""+ VERSION + "\"", response.getHeaders().getETag());

        verify(authorService).findByAuthorNumber(AUTHOR_NUMBER);
        verify(authorViewMapper).toAuthorView(author);
    }

    @Test
    void shouldFindAuthorsByName() {
        // given
        List<Author> authors = List.of(author);
        List<AuthorView> authorViews = List.of(authorView);

        when(authorService.findByName(AUTHOR_NAME)).thenReturn(authors);
        when(authorViewMapper.toAuthorView(authors)).thenReturn(authorViews);

        // when
        ListResponse<AuthorView> response = authorController.findByName(AUTHOR_NAME);

        // then
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(authorViews, response.getItems());

        verify(authorService).findByName(AUTHOR_NAME);
        verify(authorViewMapper).toAuthorView(authors);
    }

    @Test
    void shouldReturnEmptyListWhenNoAuthorsFound() {
        // given
        when(authorService.findByName(AUTHOR_NAME)).thenReturn(new ArrayList<>());
        when(authorViewMapper.toAuthorView(anyList())).thenReturn(new ArrayList<>());

        // when
        ListResponse<AuthorView> response = authorController.findByName(AUTHOR_NAME);

        // then
        assertNotNull(response);
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void shouldReturnTop5Authors() {
        // given
        List<AuthorLendingView> lendingViews = List.of(
                mock(AuthorLendingView.class),
                mock(AuthorLendingView.class)
        );

        when(authorService.findTopAuthorByLendings()).thenReturn(lendingViews);

        // when
        ListResponse<AuthorLendingView> response = authorController.getTop5();

        // then
        assertNotNull(response);
        assertEquals(2, response.getItems().size());
        assertEquals(lendingViews, response.getItems());

        verify(authorService).findTopAuthorByLendings();
    }


    @Test
    void shouldThrowNotFoundExceptionWhenNoAuthors() {
        // given
        when(authorService.findTopAuthorByLendings()).thenReturn(new ArrayList<>());

        // when & then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> authorController.getTop5());

        assertTrue(exception.getMessage().contains("No authors to show"));
    }

    @Test
    void shouldReturnPhotoWhenAuthorHasPhoto() {
        // given
        byte[] imageBytes = new byte[]{1, 2, 3};
        pt.psoft.g1.psoftg1.shared.model.Photo photo = mock(pt.psoft.g1.psoftg1.shared.model.Photo.class);

        when(authorService.findByAuthorNumber(AUTHOR_NUMBER)).thenReturn(Optional.of(author));
        when(author.getPhoto()).thenReturn(photo);
        when(photo.getPhotoFile()).thenReturn("photo.jpg");
        when(fileStorageService.getFile("photo.jpg")).thenReturn(imageBytes);
        when(fileStorageService.getExtension("photo.jpg")).thenReturn(Optional.of("jpg"));

        // when
        ResponseEntity<byte[]> response = authorController.getSpecificAuthorPhoto(AUTHOR_NUMBER);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(imageBytes, response.getBody());

        verify(fileStorageService).getFile("photo.jpg");
        verify(fileStorageService).getExtension("photo.jpg");
    }

    @Test
    void shouldReturnOKWithoutBodyWhenNoPhoto() {
        // given
        when(authorService.findByAuthorNumber(AUTHOR_NUMBER)).thenReturn(Optional.of(author));
        when(author.getPhoto()).thenReturn(null);

        // when
        ResponseEntity<byte[]> response = authorController.getSpecificAuthorPhoto(AUTHOR_NUMBER);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(fileStorageService, never()).getFile(anyString());
    }

    @Test
    void shouldReturnAuthorWithCoAuthors() {
        // given
        Author coAuthor = mock(Author.class);
        Book book = mock(Book.class);
        List<Author> coAuthors = List.of(coAuthor);
        List<Book> books = List.of(book);
        CoAuthorView coAuthorView = mock(CoAuthorView.class);
        AuthorCoAuthorBooksView expectedView = mock(AuthorCoAuthorBooksView.class);

        when(authorService.findByAuthorNumber(AUTHOR_NUMBER)).thenReturn(Optional.of(author));
        when(authorService.findCoAuthorsByAuthorNumber(AUTHOR_NUMBER)).thenReturn(coAuthors);
        when(coAuthor.getAuthorNumber()).thenReturn(2L);
        when(authorService.findBooksByAuthorNumber(2L)).thenReturn(books);
        when(authorViewMapper.toCoAuthorView(coAuthor, books)).thenReturn(coAuthorView);
        when(authorViewMapper.toAuthorCoAuthorBooksView(eq(author), anyList()))
                .thenReturn(expectedView);

        // when
        AuthorCoAuthorBooksView response = authorController.getAuthorWithCoAuthors(AUTHOR_NUMBER);

        // then
        assertNotNull(response);
        assertEquals(expectedView, response);

        verify(authorService).findByAuthorNumber(AUTHOR_NUMBER);
        verify(authorService).findCoAuthorsByAuthorNumber(AUTHOR_NUMBER);
        verify(authorService).findBooksByAuthorNumber(2L);
        verify(authorViewMapper).toCoAuthorView(coAuthor, books);
    }

    @Test
    void shouldDeletePhotoSuccessfully() {
        // given
        pt.psoft.g1.psoftg1.shared.model.Photo photo = mock(pt.psoft.g1.psoftg1.shared.model.Photo.class);

        when(authorService.findByAuthorNumber(AUTHOR_NUMBER)).thenReturn(Optional.of(author));
        when(author.getPhoto()).thenReturn(photo);
        when(photo.getPhotoFile()).thenReturn(PHOTO_URI);
        doNothing().when(fileStorageService).deleteFile(PHOTO_URI);
        when(author.getAuthorNumber()).thenReturn(AUTHOR_NUMBER);
        when(author.getVersion()).thenReturn(VERSION);

        // when
        ResponseEntity<Void> response = authorController.deleteBookPhoto(AUTHOR_NUMBER);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(fileStorageService).deleteFile(PHOTO_URI);
        verify(authorService).removeAuthorPhoto(AUTHOR_NUMBER, VERSION);
    }

    @Test
    void shouldReturnBooksForExistingAuthor() {
        // given
        Book book = mock(Book.class);
        BookView bookView = mock(BookView.class);
        List<Book> books = List.of(book);
        List<BookView> bookViews = List.of(bookView);

        when(authorService.findByAuthorNumber(AUTHOR_NUMBER)).thenReturn(Optional.of(author));
        when(authorService.findBooksByAuthorNumber(AUTHOR_NUMBER)).thenReturn(books);
        when(bookViewMapper.toBookView(books)).thenReturn(bookViews);

        // when
        ListResponse<BookView> response = authorController.getBooksByAuthorNumber(AUTHOR_NUMBER);

        // then
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(bookViews, response.getItems());

        verify(authorService).findByAuthorNumber(AUTHOR_NUMBER);
        verify(authorService).findBooksByAuthorNumber(AUTHOR_NUMBER);
    }

}
