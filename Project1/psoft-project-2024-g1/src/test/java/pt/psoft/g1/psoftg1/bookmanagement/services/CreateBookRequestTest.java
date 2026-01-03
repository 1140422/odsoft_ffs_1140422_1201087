package pt.psoft.g1.psoftg1.bookmanagement.services;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CreateBookRequestTest {

    private static final String VALID_DESCRIPTION = "Um clássico de fantasia.";
    private static final String VALID_PHOTO_URI = "http://imagens.com/foto.jpg";

    @Test
    void shouldInitializeFieldsCorrectlyWithSetters() {
        // Arrange
        CreateBookRequest request = new CreateBookRequest();
        MultipartFile photoMock = mock(MultipartFile.class);

        // Act
        request.setDescription(VALID_DESCRIPTION);
        request.setPhoto(photoMock);
        request.setPhotoURI(VALID_PHOTO_URI);

        assertNull(request.getTitle());
        assertNull(request.getGenre());
        assertNull(request.getAuthors());

        // Assert
        assertEquals(VALID_DESCRIPTION, request.getDescription());
        assertEquals(photoMock, request.getPhoto());
        assertEquals(VALID_PHOTO_URI, request.getPhotoURI());
    }

    @Test
    void shouldHandleMockMultipartFileCorrectly() {
        // Arrange
        MockMultipartFile mockPhoto = new MockMultipartFile("file", "livro.jpg", "image/jpeg", new byte[]{1, 2, 3});
        CreateBookRequest request = new CreateBookRequest();

        // Act
        request.setPhoto(mockPhoto);

        // Assert
        assertNotNull(request.getPhoto());
        assertEquals("livro.jpg", request.getPhoto().getOriginalFilename());
    }

    @Test
    void defaultConstructor_shouldInitializeNullFields() {
        // Act
        CreateBookRequest request = new CreateBookRequest();

        // Assert
        assertNull(request.getDescription());
        assertNull(request.getPhoto());
        assertNull(request.getPhotoURI());
        assertNull(request.getAuthors());
        assertNull(request.getTitle());
        assertNull(request.getGenre());
    }

    @Test
    void setters_shouldUpdateDescriptionAndPhotoURI() {
        // Arrange
        CreateBookRequest request = new CreateBookRequest();

        // Act
        request.setDescription(VALID_DESCRIPTION);
        request.setPhotoURI(VALID_PHOTO_URI);

        // Assert
        assertEquals(VALID_DESCRIPTION, request.getDescription());
        assertEquals(VALID_PHOTO_URI, request.getPhotoURI());
    }
}
