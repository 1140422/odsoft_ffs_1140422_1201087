package pt.psoft.g1.psoftg1.authormanagement.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional // ensures DB is rolled back after each test
public class AuthorServiceIntegrationTest {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;

    @MockBean
    private PhotoRepository photoRepository;

    @Test
    void shouldCreateAndRetrieveAuthor() {

        CreateAuthorRequest request = new CreateAuthorRequest(
                "John Doe",
                "Writes testable books",
                null, // no photo
                null
        );

        Author created = authorService.create(request);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("John Doe");

        Author dbAuthor = authorRepository.findByAuthorNumber(created.getId()).orElseThrow();
        assertThat(dbAuthor.getBio()).isEqualTo("Writes testable books");
    }

    @Test
    void shouldUpdateAuthorBioAndName() {
        CreateAuthorRequest createReq = new CreateAuthorRequest("Alice", "Old bio", null, null);
        Author created = authorService.create(createReq);
        Long authorId = created.getId();

        UpdateAuthorRequest updateReq = new UpdateAuthorRequest("New bio", "Alice Updated", null, null);

        Author updated = authorService.partialUpdate(authorId, updateReq, created.getVersion());

        assertThat(updated.getBio()).isEqualTo("New bio");
        assertThat(updated.getName()).isEqualTo("Alice Updated");
    }

    @Test
    void shouldRemovePhotoFromAuthor() {
        MultipartFile VALID_PHOTO = mock(MultipartFile.class);
        CreateAuthorRequest createReq = new CreateAuthorRequest("Tom", "Has photo", VALID_PHOTO, "file123.jpg");
        Author created = authorService.create(createReq);

        authorRepository.save(created);

        authorService.removeAuthorPhoto(created.getAuthorNumber(), created.getVersion());

        Mockito.verify(photoRepository).deleteByPhotoFile("file123.jpg");
    }
}
