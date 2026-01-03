package pt.psoft.g1.psoftg1.authormanagement.services;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Set;

public class UpdateAuthorRequestTest {

    private static Validator validator;
    private static final String VALID_NAME = "Name";
    private static final String VALID_BIO = "Bio";
    private static final String VALID_PHOTO_URI = "uri";
    private static final MultipartFile VALID_PHOTO = mock(MultipartFile.class);

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        UpdateAuthorRequest request = new UpdateAuthorRequest();
        assertNull(request.getName());
        assertNull(request.getBio());
        assertNull(request.getPhoto());
        assertNull(request.getPhotoURI());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        MockMultipartFile mockPhoto = new MockMultipartFile("photo", "photo.png", "image/png", new byte[]{1, 2, 3});
        String bio = "This is a short bio";
        String name = "John Doe";
        String photoURI = "/images/john.png";

        UpdateAuthorRequest request = new UpdateAuthorRequest(bio, name, mockPhoto, photoURI);

        assertThat(request.getBio()).isEqualTo(bio);
        assertThat(request.getName()).isEqualTo(name);
        assertThat(request.getPhoto()).isEqualTo(mockPhoto);
        assertThat(request.getPhotoURI()).isEqualTo(photoURI);
    }


    @Test
    void testGettersAndSetters() {
        UpdateAuthorRequest request = new UpdateAuthorRequest();

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
        UpdateAuthorRequest request1 = new UpdateAuthorRequest(VALID_BIO, VALID_NAME, null, VALID_PHOTO_URI);
        UpdateAuthorRequest request2 = new UpdateAuthorRequest(VALID_BIO, VALID_NAME, null, VALID_PHOTO_URI);

        assertEquals(request1, request2);
    }

    @Test
    void testHashCode() {
        UpdateAuthorRequest request1 = new UpdateAuthorRequest(VALID_BIO, VALID_NAME, null, VALID_PHOTO_URI);
        UpdateAuthorRequest request2 = new UpdateAuthorRequest(VALID_BIO, VALID_NAME, null, VALID_PHOTO_URI);

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void shouldHandleMaxLengthStrings() {
        String maxName = "Z".repeat(150);
        String maxBio = "X".repeat(4096);

        UpdateAuthorRequest request = new UpdateAuthorRequest(maxBio, maxName, null, null);

        assertThat(request.getName()).hasSize(150);
        assertThat(request.getBio()).hasSize(4096);
    }

    @Test
    void shouldDetectViolationWhenNameTooLong() {
        String longName = "A".repeat(152);
        UpdateAuthorRequest request = new UpdateAuthorRequest("bio", longName, null, null);

        Set<ConstraintViolation<UpdateAuthorRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void shouldDetectViolationWhenBioTooLong() {
        String longBio = "B".repeat(4097);
        UpdateAuthorRequest request = new UpdateAuthorRequest(longBio, "Name", null, null);

        Set<ConstraintViolation<UpdateAuthorRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("bio"));
    }

    @Test
    void shouldNotBeEqualWhenNameDiffers() {
        UpdateAuthorRequest request1 = new UpdateAuthorRequest(VALID_BIO, VALID_NAME, null, VALID_PHOTO_URI);
        UpdateAuthorRequest request2 = new UpdateAuthorRequest(VALID_BIO, "different", null, VALID_PHOTO_URI);

        assertNotEquals(request1, request2);
    }
}
