package pt.psoft.g1.psoftg1.readermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.services.UpdateReaderRequest;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReaderDetailsTest {

    private Reader mockReader;
    private Genre mockGenre1;
    private Genre mockGenre2;
    private UpdateReaderRequest mockRequest;

    @BeforeEach
    void setup() {
        mockReader = mock(Reader.class);
        mockGenre1 = mock(Genre.class);
        mockGenre2 = mock(Genre.class);
        mockRequest = mock(UpdateReaderRequest.class);
    }

    @Test
    void testConstructorWithValidArguments() {
        List<Genre> genres = List.of(mockGenre1, mockGenre2);
        ReaderDetails details = new ReaderDetails(
                10, mockReader, "2000-01-01", "912345678",
                true, true, false, "photo.png", genres
        );

        assertEquals(mockReader, details.getReader());
        assertEquals("912345678", details.getPhoneNumber());
        assertEquals("2000-1-1", details.getBirthDate().toString());
        assertTrue(details.isGdprConsent());
        assertTrue(details.isMarketingConsent());
        assertFalse(details.isThirdPartySharingConsent());
        assertEquals(genres, details.getInterestList());
        //assertEquals("photo.png", details.getPhotoURI());
    }

    @Test
    void testConstructorThrowsWhenReaderIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new ReaderDetails(1, null, "1990-01-01", "900000000",
                        true, false, false, null, List.of(mockGenre1))
        );
    }

    @Test
    void testConstructorThrowsWhenPhoneIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new ReaderDetails(1, mockReader, "1990-01-01", null,
                        true, false, false, null, List.of(mockGenre1))
        );
    }

    @Test
    void testConstructorThrowsWhenGdprIsFalse() {
        assertThrows(IllegalArgumentException.class, () ->
                new ReaderDetails(1, mockReader, "1990-01-01", "912345678",
                        false, false, false, null, List.of(mockGenre1))
        );
    }

    @Test
    void testGetReaderNumber() {
        ReaderDetails details = new ReaderDetails(
                5, mockReader, "1995-05-05", "911111111",
                true, false, false, null, List.of(mockGenre1)
        );
        assertTrue(details.getReaderNumber().contains("5"));
    }

    @Test
    void testApplyPatchSuccessfullyUpdatesFields() {
        List<Genre> newGenres = List.of(mockGenre1);
        ReaderDetails details = new ReaderDetails(
                1, mockReader, "1990-01-01", "911111111",
                true, false, false, "oldPhoto.jpg", List.of(mockGenre2)
        );

        // definir versão para simular update
        setVersion(details, 1L);

        when(mockRequest.getUsername()).thenReturn("newUser");
        when(mockRequest.getPassword()).thenReturn("newPass");
        when(mockRequest.getFullName()).thenReturn("Novo Nome");
        when(mockRequest.getBirthDate()).thenReturn("2000-12-12");
        when(mockRequest.getPhoneNumber()).thenReturn("933333333");
        when(mockRequest.getMarketing()).thenReturn(true);
        when(mockRequest.getThirdParty()).thenReturn(true);

        details.applyPatch(1L, mockRequest, "newPhoto.jpg", newGenres);

        verify(mockReader).setUsername("newUser");
        verify(mockReader).setPassword("newPass");
        verify(mockReader).setName("Novo Nome");

        assertEquals("933333333", details.getPhoneNumber());
        assertEquals("2000-12-12", details.getBirthDate().toString());
        //assertEquals("newPhoto.jpg", details.getPhoto().getPhotoFile());
        assertEquals(newGenres, details.getInterestList());
        assertTrue(details.isMarketingConsent());
        assertTrue(details.isThirdPartySharingConsent());
    }

    @Test
    void testApplyPatchThrowsConflictWhenVersionMismatch() {
        ReaderDetails details = new ReaderDetails(
                1, mockReader, "2000-01-01", "912345678",
                true, false, false, null, List.of(mockGenre1)
        );
        setVersion(details, 1L);

        assertThrows(ConflictException.class, () ->
                details.applyPatch(2L, mockRequest, "photo.jpg", null)
        );
    }

    @Test
    void testApplyPatchHandlesInvalidPhotoURIGracefully() {
        ReaderDetails details = new ReaderDetails(
                1, mockReader, "2000-01-01", "912345678",
                true, false, false, null, List.of(mockGenre1)
        );
        setVersion(details, 1L);

        when(mockRequest.getUsername()).thenReturn("user");

        assertDoesNotThrow(() ->
                details.applyPatch(1L, mockRequest, "??invalid_path", null)
        );
    }

    @Test
    void testRemovePhotoSuccessfullyRemovesPhoto() {
        ReaderDetails details = new ReaderDetails(
                1, mockReader, "2000-01-01", "912345678",
                true, false, false, "photo.png", List.of(mockGenre1)
        );
        setVersion(details, 1L);

        details.removePhoto(1L);

        assertNull(details.getPhoto());
    }

    @Test
    void testRemovePhotoThrowsConflictOnVersionMismatch() {
        ReaderDetails details = new ReaderDetails(
                1, mockReader, "2000-01-01", "912345678",
                true, false, false, "photo.png", List.of(mockGenre1)
        );
        setVersion(details, 1L);

        assertThrows(ConflictException.class, () ->
                details.removePhoto(2L)
        );
    }

    // HELPER PARA INJETAR VERSÃO PRIVADA
    private void setVersion(ReaderDetails details, long version) {
        try {
            var field = ReaderDetails.class.getDeclaredField("version");
            field.setAccessible(true);
            field.set(details, version);
        } catch (Exception e) {
            fail("Erro ao definir versão: " + e.getMessage());
        }
    }
}