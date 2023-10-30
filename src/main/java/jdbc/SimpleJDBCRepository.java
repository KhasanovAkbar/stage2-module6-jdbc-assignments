package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstName, lastName, age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstName=?, lastName=?, age=? WHERE id=?";
    private static final String deleteUserSQL = "DELETE FROM myusers WHERE id=?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id=?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstName=?";
    private static final String findAllUsersSQL = "SELECT * FROM myusers";


    public Long createUser(String firstName, String lastName, int age) throws SQLException {
        //
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, age);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained");
            }

        } finally {
            closeResources();
        }
    }

    public User findUserById(Long userId) throws SQLException {
        //
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                //
                return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getInt("age")
                );
            }

        } finally {
            closeResources();
        }
        return null;
    }

    public User findUserByName(String userName) throws SQLException {
        //
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getInt("age")
                );
            }
        } finally {
            closeResources();
        }
        return null;
    }

    public List<User> findAllUser() throws SQLException {
        //
        List<User> userList = new ArrayList<>();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            st = connection.createStatement();
            ResultSet resultSet = st.executeQuery(findAllUsersSQL);

            while (resultSet.next()) {
                userList.add(
                        new User(
                                resultSet.getLong("id"),
                                resultSet.getString("firstName"),
                                resultSet.getString("lastName"),
                                resultSet.getInt("age")
                        )
                );
            }
        } finally {
            closeResources();
        }
        return userList;
    }

    public User updateUser(Long userId, String newFirstName, String newLastName, int newAge) throws SQLException {
        //
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, newFirstName);
            ps.setString(2, newLastName);
            ps.setInt(3, newAge);
            ps.setLong(4, userId);
            int updatedRows = ps.executeUpdate();

            if (updatedRows > 0) {
                return new User(userId, newFirstName, newFirstName, newAge);
            }
        } finally {
            closeResources();
        }
        //update failed
        return null;
    }

    private void deleteUser(Long userId) throws SQLException {
        //
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(deleteUserSQL);
            ps.setLong(1, userId);
            ps.executeUpdate();
        } finally {
            closeResources();
        }

    }

    private void closeResources() {
        try {
            if (ps != null) {
                ps.close(); // Close the PreparedStatement
            }
            if (st != null) {
                st.close(); // Close the Statement
            }
            if (connection != null) {
                connection.close(); // Close the Connection
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log any exceptions that may occur during resource closing
        }
    }

}
