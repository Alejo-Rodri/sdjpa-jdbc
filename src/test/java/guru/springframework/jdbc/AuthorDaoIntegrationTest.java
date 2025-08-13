package guru.springframework.jdbc;

import guru.springframework.jdbc.dao.AuthorDao;
import guru.springframework.jdbc.domain.Author;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("local")
@Transactional
@DataJpaTest
@ComponentScan(basePackages = {"guru.springframework.jdbc.dao"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorDaoIntegrationTest {
    @Autowired
    AuthorDao authorDao;

    @Test
    void testGetAuthorById() {
        Author author = authorDao.getById(1L);

        assertThat(author).isNotNull();
    }

    @Test
    void testGetAuthorByName() {
        Author author = authorDao.getByName("Craig", "Walls");

        assertThat(author).isNotNull();
        assertThat(author.getFirstName()).isEqualTo("Craig");
        assertThat(author.getLastName()).isEqualTo("Walls");
    }

    @Test
    void testSaveAuthor() {
        Author author = new Author();
        author.setFirstName("Ale");
        author.setLastName("Ro");

        Author saved = authorDao.saveNewAuthor(author);
        assertThat(saved).isNotNull();
    }

    @Test
    void testUpdateAuthor() {
        Author author = new Author();
        author.setFirstName("Ale");
        author.setLastName("Ro");

        Author saved = authorDao.saveNewAuthor(author);

        saved.setFirstName("Ro");
        Author updated = authorDao.updateAuthor(saved);

        assertThat(updated.getFirstName()).isEqualTo("Ro");
    }

    @Test
    void testDeleteAuthor() {
        Author author = new Author();
        author.setFirstName("Ale");
        author.setLastName("Ro");

        Author saved = authorDao.saveNewAuthor(author);

        authorDao.deleteAuthorById(saved.getId());

        Author deleted = authorDao.getById(saved.getId());
        assertThat(deleted).isNull();
    }
}
