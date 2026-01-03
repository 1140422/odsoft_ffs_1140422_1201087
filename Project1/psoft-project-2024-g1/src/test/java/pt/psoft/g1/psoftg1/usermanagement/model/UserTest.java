package pt.psoft.g1.psoftg1.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static final String VALID_USERNAME = "user@example.com";
    private static final String VALID_PASSWORD = "Password123!";
    private static final String VALID_NAME = "John Doe";
    private static final String VALID_ROLE = "READER";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(VALID_USERNAME, VALID_PASSWORD);
    }

    @Test
    void shouldCreateUserWithUsernameAndPassword() {
        assertNotNull(user);
        assertEquals(VALID_USERNAME, user.getUsername());
        assertNotNull(user.getPassword());
        assertTrue(user.isEnabled());
    }

    @Test
    void shouldInitializeEnabledToTrueByDefault() {
        assertTrue(user.isEnabled());
    }

    @Test
    void shouldInitializeEmptyAuthoritiesSet() {
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    void shouldCreateUserWithProtectedConstructor() throws Exception {
        // when
        var constructor = User.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        User user = constructor.newInstance();

        // then
        assertNotNull(user);
    }

    @Test
    void shouldCreateUserWithUsernamePasswordAndName() {
        // when
        User user = User.newUser(VALID_USERNAME, VALID_PASSWORD, VALID_NAME);

        // then
        assertNotNull(user);
        assertEquals(VALID_USERNAME, user.getUsername());
        assertNotNull(user.getPassword());
        assertNotNull(user.getName());
        assertEquals(VALID_NAME, user.getName().toString());
    }

    @Test
    void shouldBeEnabledByDefault() {
        // when
        User user1 = User.newUser(VALID_USERNAME, VALID_PASSWORD, VALID_NAME);

        // then
        assertTrue(user1.isEnabled());
    }

    @Test
    void shouldCreateUserWithUsernamePasswordNameAndRole() {
        // when
        User user = User.newUser(VALID_USERNAME, VALID_PASSWORD, VALID_NAME, VALID_ROLE);

        // then
        assertNotNull(user);
        assertEquals(VALID_USERNAME, user.getUsername());
        assertNotNull(user.getPassword());
        assertNotNull(user.getName());
        assertEquals(VALID_NAME, user.getName().toString());
        assertFalse(user.getAuthorities().isEmpty());
        assertEquals(1, user.getAuthorities().size());
    }

    @Test
    void shouldCreateUserWithLibrarianRole() {
        // when
        User user = User.newUser(VALID_USERNAME, VALID_PASSWORD, VALID_NAME, "LIBRARIAN");

        // then
        assertEquals(1, user.getAuthorities().size());
        Role role = user.getAuthorities().iterator().next();
        assertEquals("LIBRARIAN", role.getAuthority());
    }

    @Test
    void shouldCreateUserWithAdminRole() {
        // when
        User user = User.newUser(VALID_USERNAME, VALID_PASSWORD, VALID_NAME, "ADMIN");

        // then
        assertEquals(1, user.getAuthorities().size());
        Role role = user.getAuthorities().iterator().next();
        assertEquals("ADMIN", role.getAuthority());
    }

    @Test
    void shouldGetId() {
        // when
        Long id = user.getId();

        // then (id is null until persisted)
        assertNull(id);
    }

    @Test
    void shouldGetCreatedAt() {
        // when
        LocalDateTime createdAt = user.getCreatedAt();

        // then (null until persisted)
        assertNull(createdAt);
    }

    @Test
    void shouldGetModifiedAt() {
        // when
        LocalDateTime modifiedAt = user.getModifiedAt();

        // then (null until persisted)
        assertNull(modifiedAt);
    }

    @Test
    void shouldGetCreatedBy() {
        // when
        String createdBy = user.getCreatedBy();

        // then (null until persisted)
        assertNull(createdBy);
    }

    @Test
    void shouldSetUserName() {
        // given
        String newUserName = "Jane Smith";

        // when
        user.setUsername(newUserName);

        // then
        assertNotNull(user.getUsername());
        assertEquals(newUserName, user.getUsername());
    }

    @Test
    void isAccountNonExpiredShouldReturnEnabledStatus() {
        // when enabled
        user.setEnabled(true);
        assertTrue(user.isAccountNonExpired());

        // when disabled
        user.setEnabled(false);
        assertFalse(user.isAccountNonExpired());
    }

    @Test
    void isAccountNonLockedShouldReturnEnabledStatus() {
        // when enabled
        user.setEnabled(true);
        assertTrue(user.isAccountNonLocked());

        // when disabled
        user.setEnabled(false);
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpiredShouldReturnEnabledStatus() {
        // when enabled
        user.setEnabled(true);
        assertTrue(user.isCredentialsNonExpired());

        // when disabled
        user.setEnabled(false);
        assertFalse(user.isCredentialsNonExpired());
    }

}
