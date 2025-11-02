package pt.psoft.g1.psoftg1.usermanagement.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class ReaderTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void shouldCreateReaderWithProtectedConstructor() throws Exception {
        // when
        var constructor = Reader.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Reader reader = constructor.newInstance();

        // then
        assertNotNull(reader);
    }

    @Test
    void shouldCreateReaderWithReaderRole() {
        Reader reader = new Reader("john_doe", "Password123!");

        assertNotNull(reader);
        assertEquals("john_doe", reader.getUsername());

        String encodedPassword = reader.getPassword();
        assertNotNull(encodedPassword);
        assertTrue(encoder.matches("Password123!", encodedPassword),
                "Stored password should be a BCrypt hash of the original password");

        // Verify READER role added
        assertTrue(reader.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.READER)));
    }

    @Test
    void shouldCreateReaderUsingFactoryMethod() {
        Reader reader = Reader.newReader("alice", "Password123!", "Alice Doe");

        assertNotNull(reader);
        assertEquals("alice", reader.getUsername());
        assertEquals("Alice Doe", reader.getName().toString());
        assertTrue(encoder.matches("Password123!", reader.getPassword()));

        assertTrue(reader.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.READER)));
    }

}
