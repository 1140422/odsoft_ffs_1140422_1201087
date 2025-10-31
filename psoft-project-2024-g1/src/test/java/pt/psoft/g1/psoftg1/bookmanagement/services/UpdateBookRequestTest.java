package pt.psoft.g1.psoftg1.bookmanagement.services;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UpdateBookRequestTest {

    private static final String VALID_ISBN = "9783161484100";
    private static final String VALID_TITLE = "O Senhor dos Anéis";
    private static final String VALID_DESCRIPTION = "Um clássico de fantasia.";
    private static final String VALID_GENRE = "Fantasia";
    private static final String VALID_PHOTO_URI = "imagem.jpg";

    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        // Arrange
        List<Long> authors = List.of(1L, 2L);

        // Act
        UpdateBookRequest request = new UpdateBookRequest(
                VALID_ISBN,
                VALID_TITLE,
                VALID_GENRE,
                authors,
                VALID_DESCRIPTION
        );

        // Assert
        assertEquals(VALID_ISBN, request.getIsbn());
        assertEquals(VALID_TITLE, request.getTitle());
        assertEquals(VALID_GENRE, request.getGenre());
        assertEquals(authors, request.getAuthors());
        assertEquals(VALID_DESCRIPTION, request.getDescription());
        assertNull(request.getPhoto());
        assertNull(request.getPhotoURI());
        assertNull(request.getGenreObj());
        assertNull(request.getAuthorObjList());
    }

    @Test
    void settersAndGetters_shouldUpdateFieldsCorrectly() {
        // Arrange
        UpdateBookRequest request = new UpdateBookRequest();

        // Act
        request.setIsbn(VALID_ISBN);
        request.setDescription(VALID_DESCRIPTION);
        request.setPhotoURI(VALID_PHOTO_URI);
        request.setPhoto(new MockMultipartFile("file", "foto.jpg", "image/jpeg", new byte[0]));

        Genre genreMock = mock(Genre.class);
        request.setGenreObj(genreMock);

        // Assert
        assertEquals(VALID_ISBN, request.getIsbn());
        assertEquals(VALID_DESCRIPTION, request.getDescription());
        assertEquals(VALID_PHOTO_URI, request.getPhotoURI());
        assertNotNull(request.getPhoto());
        assertEquals(genreMock, request.getGenreObj());
    }

    @Test
    void defaultConstructor_shouldInitializeNullFields() {
        // Act
        UpdateBookRequest request = new UpdateBookRequest();

        // Assert
        assertNull(request.getIsbn());
        assertNull(request.getDescription());
        assertNull(request.getPhoto());
        assertNull(request.getPhotoURI());
        assertNull(request.getGenreObj());
    }
}