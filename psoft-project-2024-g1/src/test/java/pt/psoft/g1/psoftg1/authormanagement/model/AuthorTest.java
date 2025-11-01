package pt.psoft.g1.psoftg1.authormanagement.model;

import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;
import pt.psoft.g1.psoftg1.authormanagement.services.UpdateAuthorRequest;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;
import pt.psoft.g1.psoftg1.shared.model.Photo;


import static org.junit.jupiter.api.Assertions.*;

class AuthorTest {
    private final String validName = "João Alberto";
    private final String validBio = "O João Alberto nasceu em Chaves e foi pedreiro a maior parte da sua vida.";
    private final String validPhotoURI = "authorPhoto.jpg";

    private final UpdateAuthorRequest request = new UpdateAuthorRequest(validName, validBio, null, null);

    private static class EntityWithPhotoImpl extends EntityWithPhoto { }
    @BeforeEach
    void setUp() {
    }
    @Test
    void ensureNameNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new Author(null,validBio, null));
    }

    @Test
    void ensureNameCannotBeBlank() { assertThrows(IllegalArgumentException.class, () -> new Author("", validBio, validPhotoURI)); }

    @Test
    void ensureBioNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new Author(validName,null, null));
    }

    @Test
    void ensureBioCannotBeBlank(){
        assertThrows(IllegalArgumentException.class, () -> new Author(validName,"", null));
        assertThrows(IllegalArgumentException.class, () -> new Author(validName,"    ", null));
    }

    @Test
    void whenVersionIsStaleItIsNotPossibleToPatch() {
        final var subject = new Author(validName,validBio, null);

        assertThrows(StaleObjectStateException.class, () -> subject.applyPatch(999, request));
    }

    @Test
    void testCreateAuthorWithoutPhoto() {
        Author author = new Author(validName, validBio, null);
        assertNotNull(author);
        assertNull(author.getPhoto());
    }

    @Test
    void testCreateAuthorRequestWithPhoto() {
        CreateAuthorRequest request = new CreateAuthorRequest(validName, validBio, null, "photoTest.jpg");
        Author author = new Author(request.getName(), request.getBio(), "photoTest.jpg");
        assertNotNull(author);
        assertEquals(request.getPhotoURI(), author.getPhoto().getPhotoFile());
    }

    @Test
    void testCreateAuthorRequestWithoutPhoto() {
        CreateAuthorRequest request = new CreateAuthorRequest(validName, validBio, null, null);
        Author author = new Author(request.getName(), request.getBio(), null);
        assertNotNull(author);
        assertNull(author.getPhoto());
    }

    @Test
    void testEntityWithPhotoSetPhotoInternalWithValidURI() {
        EntityWithPhoto entity = new EntityWithPhotoImpl();
        String validPhotoURI = "photoTest.jpg";
        entity.setPhoto(validPhotoURI);
        assertNotNull(entity.getPhoto());
    }

    @Test
    void ensurePhotoCanBeNull_AkaOptional() {
        Author author = new Author(validName, validBio, null);
        assertNull(author.getPhoto());
    }

    @Test
    void ensureValidPhoto() {
        Author author = new Author(validName, validBio, "photoTest.jpg");
        Photo photo = author.getPhoto();
        assertNotNull(photo);
        assertEquals("photoTest.jpg", photo.getPhotoFile());
    }

    @Test
    void shouldCoverVersionMismatchCondition() {
        // Covers: if (version != currentVersion)
        Author author = new Author("Name", "Bio", null);
        UpdateAuthorRequest request = new UpdateAuthorRequest("New", null, null, null);

        assertThrows(StaleObjectStateException.class,
                () -> author.applyPatch(999L, request));
    }

    @Test
    void shouldTransitionFromInitialToUpdatedState() {
        Author author = new Author("Initial Name", "Initial Bio", "Initial Photo");
        long version = author.getVersion();

        UpdateAuthorRequest request = new UpdateAuthorRequest(
                "Updated Bio", "Updated Name", null, "Updated Photo");

        author.applyPatch(version, request);

        assertEquals("Updated Name", author.getName());
        assertEquals("Updated Bio", author.getBio());
    }

    @Test
    void shouldRejectTransitionWithInvalidVersion() {
        Author author = new Author("Name", "Bio", null);
        UpdateAuthorRequest request = new UpdateAuthorRequest("New", "New", null, null);

        assertThrows(StaleObjectStateException.class,
                () -> author.applyPatch(999L, request));
    }
}

