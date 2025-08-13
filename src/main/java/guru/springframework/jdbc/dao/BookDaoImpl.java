package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;

@Component
public class BookDaoImpl implements BookDao {
    private static final Logger log = LoggerFactory.getLogger(BookDaoImpl.class);
    private final DataSource dataSource;
    private final AuthorDao authorDao;

    public BookDaoImpl(DataSource dataSource, AuthorDao authorDao) {
        this.dataSource = dataSource;
        this.authorDao = authorDao;
    }

    @Override
    public Book getById(Long id) {
        String query = "SELECT * FROM book WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return getBookFromRS(resultSet);
            }
        } catch (SQLException e) {
            log.error("ERROR: ", e);
        }

        return null;
    }

    @Override
    public Book getByTitle(String title) {
        String query = "SELECT * FROM book WHERE title = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, title);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return getBookFromRS(resultSet);
            }
        } catch (SQLException e) {
            log.error("ERROR: ", e);
        }

        return null;
    }

    @Override
    public Book saveNewBook(Book book) {
        String query = "INSERT INTO book (title, publisher, isbn, author_id) VALUES (?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getPublisher());
            preparedStatement.setString(3, book.getIsbn());
            if (!Objects.isNull(book.getAuthor()))
                preparedStatement.setLong(4, book.getAuthor().getId());
            else // -5 is the code for BIGINT
                preparedStatement.setNull(4, -5);

            preparedStatement.execute();

            try (
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
            ) {
                if (resultSet.next()) {
                    Long savedId = resultSet.getLong(1);
                    return this.getById(savedId);
                }
            }
        } catch (SQLException e) {
            log.error("ERROR: ", e);
        }

        return null;
    }

    @Override
    public Book updateBook(Book book) {
        String query = "UPDATE book SET title = ?, isbn = ?, publisher = ?, author_id = ? WHERE book.id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setString(3, book.getPublisher());
            if (!Objects.isNull(book.getAuthor()))
                preparedStatement.setLong(4, book.getAuthor().getId());

            preparedStatement.setLong(5, book.getId());
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("ERROR: ", e);
        }

        return this.getById(book.getId());
    }

    @Override
    public void deleteBookById(Long id) {
        String query = "DELETE FROM book WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("ERROR: ", e);
        }
    }

    private Book getBookFromRS(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getLong("id"));
        book.setTitle(resultSet.getString("title"));
        book.setIsbn(resultSet.getString("isbn"));
        book.setPublisher(resultSet.getString("publisher"));

        book.setAuthor(authorDao.getById(resultSet.getLong("author_id")));

        return book;
    }
}
