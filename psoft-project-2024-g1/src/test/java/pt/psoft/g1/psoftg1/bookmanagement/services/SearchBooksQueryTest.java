package pt.psoft.g1.psoftg1.bookmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SearchBooksQueryTest {

    private static final String VALID_TITLE = "O Hobbit";
    private static final String VALID_GENRE = "Fantasia";
    private static final String VALID_AUTHOR = "Tolkien";

    @Test
    void allArgsConstructor_shouldInitializeAllFieldsCorrectly() {
        // Act
        SearchBooksQuery query = new SearchBooksQuery(VALID_TITLE, VALID_GENRE, VALID_AUTHOR);

        // Assert
        assertEquals(VALID_TITLE, query.getTitle());
        assertEquals(VALID_GENRE, query.getGenre());
        assertEquals(VALID_AUTHOR, query.getAuthorName());
    }

    @Test
    void noArgsConstructor_shouldInitializeFieldsAsNull() {
        // Act
        SearchBooksQuery query = new SearchBooksQuery();

        // Assert
        assertNull(query.getTitle());
        assertNull(query.getGenre());
        assertNull(query.getAuthorName());
    }

    @Test
    void setters_shouldUpdateFieldsCorrectly() {
        // Arrange
        SearchBooksQuery query = new SearchBooksQuery();

        // Act
        query.setTitle(VALID_TITLE);
        query.setGenre(VALID_GENRE);
        query.setAuthorName(VALID_AUTHOR);

        // Assert
        assertEquals(VALID_TITLE, query.getTitle());
        assertEquals(VALID_GENRE, query.getGenre());
        assertEquals(VALID_AUTHOR, query.getAuthorName());
    }

    @Test
    void equalsAndHashCode_shouldWorkProperly() {
        // Arrange
        SearchBooksQuery q1 = new SearchBooksQuery(VALID_TITLE, VALID_GENRE, VALID_AUTHOR);
        SearchBooksQuery q2 = new SearchBooksQuery(VALID_TITLE, VALID_GENRE, VALID_AUTHOR);
        SearchBooksQuery q3 = new SearchBooksQuery("Outro Título", VALID_GENRE, VALID_AUTHOR);

        // Assert
        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
        assertNotEquals(q1, q3);
    }

    @Test
    void toString_shouldContainFieldValues() {
        // Arrange
        SearchBooksQuery query = new SearchBooksQuery(VALID_TITLE, VALID_GENRE, VALID_AUTHOR);

        // Act
        String result = query.toString();

        // Assert
        assertTrue(result.contains(VALID_TITLE));
        assertTrue(result.contains(VALID_GENRE));
        assertTrue(result.contains(VALID_AUTHOR));
    }
}