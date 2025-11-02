package pt.psoft.g1.psoftg1.authormanagement.services;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CreateAuthorRequestTest {

    private static final String VALID_NAME = "Name";
    private static final String VALID_BIO = "Bio";
    private static final String VALID_PHOTO_URI = "uri";
    private static final MultipartFile VALID_PHOTO = mock(MultipartFile.class);

    @Test
    void testNoArgsConstructor() {
        CreateAuthorRequest request = new CreateAuthorRequest();
        assertNull(request.getName());
        assertNull(request.getBio());
        assertNull(request.getPhoto());
        assertNull(request.getPhotoURI());
    }

    @Test
    void testAllArgsConstructor() {
        CreateAuthorRequest request = new CreateAuthorRequest(VALID_NAME, VALID_BIO, VALID_PHOTO, VALID_PHOTO_URI);

        assertEquals("Name", request.getName());
        assertEquals("Bio", request.getBio());
        assertEquals("uri", request.getPhotoURI());
    }

    @Test
    void testGettersAndSetters() {
        CreateAuthorRequest request = new CreateAuthorRequest();

        request.setName(VALID_NAME);
        assertEquals("Name", request.getName());

        request.setBio(VALID_BIO);
        assertEquals("Bio", request.getBio());

        request.setPhoto(VALID_PHOTO);
        assertEquals(VALID_PHOTO, request.getPhoto());

        request.setPhotoURI(VALID_PHOTO_URI);
        assertEquals("uri", request.getPhotoURI());
    }

    @Test
    void testEquals() {
        CreateAuthorRequest request1 = new CreateAuthorRequest(VALID_NAME, VALID_BIO, null, VALID_PHOTO_URI);
        CreateAuthorRequest request2 = new CreateAuthorRequest(VALID_NAME, VALID_BIO, null, VALID_PHOTO_URI);

        assertEquals(request1, request2);
    }

    @Test
    void testHashCode() {
        CreateAuthorRequest request1 = new CreateAuthorRequest(VALID_NAME, VALID_BIO, null, VALID_PHOTO_URI);
        CreateAuthorRequest request2 = new CreateAuthorRequest(VALID_NAME, VALID_BIO, null, VALID_PHOTO_URI);

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void shouldHandleMaxLengthStrings() {
        String maxName = "Z".repeat(150);
        String maxBio = "X".repeat(4096);

        CreateAuthorRequest request = new CreateAuthorRequest(maxName, maxBio, null, null);

        assertThat(request.getName()).hasSize(150);
        assertThat(request.getBio()).hasSize(4096);
    }

    @Test
    void shouldNotBeEqualWhenBioDiffers() {
        CreateAuthorRequest request1 = new CreateAuthorRequest(VALID_NAME, VALID_BIO, null, VALID_PHOTO_URI);
        CreateAuthorRequest request2 = new CreateAuthorRequest(VALID_NAME, "different", null, VALID_PHOTO_URI);

        assertNotEquals(request1, request2);
    }

}
