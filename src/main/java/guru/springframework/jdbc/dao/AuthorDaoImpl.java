package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class AuthorDaoImpl implements AuthorDao {
    private static final Logger log = LoggerFactory.getLogger(AuthorDaoImpl.class);
    private final DataSource dataSource;

    public AuthorDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Author getById(Long id) {
        String query = "SELECT * FROM author WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return getAuthorFromRS(resultSet);
            }
        } catch (SQLException e) {
            log.error("ERROR: ", e);
        }

        return null;
    }

    @Override
    public Author getByName(String firstName, String lastName) {
        String query = "SELECT * FROM author WHERE first_name = ? AND last_name = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return getAuthorFromRS(resultSet);
            }
        } catch (SQLException e) {
            log.error("ERROR: ", e);
        }

        return null;
    }

    @Override
    public Author saveNewAuthor(Author author) {
        String query = "INSERT INTO author (first_name, last_name) VALUES (?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.execute();


            try (
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = preparedStatement.executeQuery();
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
    public void deleteAuthorById(Long id) {
        String query = "DELETE FROM author WHERE id = ?";
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

    @Override
    public Author updateAuthor(Author author) {
        String query = "UPDATE author SET first_name = ?, last_name = ? WHERE author.id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.setLong(3, author.getId());
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("ERROR: ", e);
        }

        return this.getById(author.getId());
    }

    private Author getAuthorFromRS(ResultSet resultSet) throws SQLException {
        Author author = new Author();
        author.setId(resultSet.getLong("id"));
        author.setFirstName(resultSet.getString("first_name"));
        author.setLastName(resultSet.getString("last_name"));

        return author;
    }
}
