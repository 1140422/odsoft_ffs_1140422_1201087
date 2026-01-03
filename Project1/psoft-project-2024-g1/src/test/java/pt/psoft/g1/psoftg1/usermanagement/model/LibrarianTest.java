package pt.psoft.g1.psoftg1.usermanagement.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarianTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void shouldCreateLibrarianWithProtectedConstructor() throws Exception {
        // when
        var constructor = Librarian.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Librarian librarian = constructor.newInstance();

        // then
        assertNotNull(librarian);
    }

    @Test
    void shouldCreateLibrarianWithLibrarianRole() {
        Librarian librarian = new Librarian("john_doe", "Password123!");

        assertNotNull(librarian);
        assertEquals("john_doe", librarian.getUsername());

        String encodedPassword = librarian.getPassword();
        assertNotNull(encodedPassword);
        assertTrue(encoder.matches("Password123!", encodedPassword));
    }

    @Test
    void shouldCreateLibrarianUsingFactoryMethod() {
        Librarian librarian = Librarian.newLibrarian("alice", "Password123!", "Alice Doe");

        assertNotNull(librarian);
        assertEquals("alice", librarian.getUsername());
        assertEquals("Alice Doe", librarian.getName().toString());
        assertTrue(encoder.matches("Password123!", librarian.getPassword()));

        assertTrue(librarian.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.LIBRARIAN)));
    }


}
