package guru.springframework.jdbc;

import guru.springframework.jdbc.dao.AuthorDao;
import guru.springframework.jdbc.dao.BookDao;
import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("local")
// this annotation is for doing rollback and closing the connection to the db
@Transactional
@DataJpaTest
@ComponentScan(basePackages = {"guru.springframework.jdbc.dao"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookDaoIntegrationTest {
    @Autowired
    AuthorDao authorDao;

    @Autowired
    BookDao bookDao;

    @Test
    void testSaveBook() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setPublisher("Addison-Wesley Professional");
        book.setIsbn("978-0134685991");

        Author author = new Author();
        author.setFirstName("Joshua");
        author.setLastName("Bloch");
        Author savedAuthor = authorDao.saveNewAuthor(author);
        book.setAuthor(savedAuthor);

        Book saved = bookDao.saveNewBook(book);
        assertThat(saved).isNotNull();
    }

    @Test
    void testGetById() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setPublisher("Addison-Wesley Professional");
        book.setIsbn("978-0134685991");

        Author author = new Author();
        author.setFirstName("Joshua");
        author.setLastName("Bloch");
        Author savedAuthor = authorDao.saveNewAuthor(author);
        book.setAuthor(savedAuthor);

        Book saved = bookDao.saveNewBook(book);
        Book fetched = bookDao.getById(saved.getId());

        assertThat(fetched).isNotNull();
    }

    @Test
    void testGetByTitle() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setPublisher("Addison-Wesley Professional");
        book.setIsbn("978-0134685991");

        Author author = new Author();
        author.setFirstName("Joshua");
        author.setLastName("Bloch");
        Author savedAuthor = authorDao.saveNewAuthor(author);
        book.setAuthor(savedAuthor);

        Book saved = bookDao.saveNewBook(book);
        Book fetched = bookDao.getByTitle(saved.getTitle());

        assertThat(fetched).isNotNull();
    }

    @Test
    void testUpdateBook() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setPublisher("Addison-Wesley Professional");
        book.setIsbn("978-0134685991");

        Author author = new Author();
        author.setFirstName("Joshua");
        author.setLastName("Bloch");
        Author savedAuthor = authorDao.saveNewAuthor(author);
        book.setAuthor(savedAuthor);

        Book saved = bookDao.saveNewBook(book);

        saved.setTitle("Java Concurrency In Practice");
        Book updated = bookDao.updateBook(saved);

        assertThat(updated.getTitle()).isEqualTo("Java Concurrency In Practice");
    }

    @Test
    void deleteBookById() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setPublisher("Addison-Wesley Professional");
        book.setIsbn("978-0134685991");

        Author author = new Author();
        author.setFirstName("Joshua");
        author.setLastName("Bloch");
        Author savedAuthor = authorDao.saveNewAuthor(author);
        book.setAuthor(savedAuthor);

        Book saved = bookDao.saveNewBook(book);
        bookDao.deleteBookById(saved.getId());
        Book deleted = bookDao.getById(saved.getId());

        assertThat(deleted).isNull();
    }
}
