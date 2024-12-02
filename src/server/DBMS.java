package server;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//  !!!WARNING: MAKE SURE THE JDBC CONNECTOR JAR IS IN THE BUILD PATH!!!
public class DBMS {
    //  Username and password for MySQL workbench connection
    private static final String USERNAME = "CassCo";
    private static final String PASSWORD = "Password.";
    // Objects to be used for database access
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet result = null;
    //  Connect to the "UserDatabase" database scheme,
    //  Default port is 3306 (jdbc:mysql://<ip address>:<port>/<SCHEMA>)
    private final String SCHEMA = "sys";
    private final String TABLE = "users";
    private final String URL = "jdbc:mysql://127.0.0.1:3306/" + SCHEMA;
    public static ArrayList<String> userList = new ArrayList<>();
    //  Constructor for DBMS
    public DBMS () {
        try {
            // Make connection to the database
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // These will Send queries to the database
            statement = connection.createStatement();
            result = statement.executeQuery("SELECT VERSION()");
            if (result.next()) {
                System.out.println("MySQL version: " + result.getString(1) + "\n=====================\n");
            }   //  End If
        }   //  End Try
        catch (SQLException ex) {
            //  Handle any errors
            handleSQLException(ex);
        }   //  End Catch
    }   //  End DBMS Constructor
    //  Accesses Preform Operations in MySQL Database
    public void accessDatabase() {
        try {
            // Query will return a ResultSet then get and print all records in the table
            System.out.println("Original Contents");
            String SQL = "SELECT * FROM " + TABLE + ";";
            result = statement.executeQuery(SQL);
            printResultSet(result);
        }   //  End try
        catch (SQLException ex) {
            //  Handle any errors
            handleSQLException(ex);
        }   //  End Catch
    }   //  End Access Database
    //  Print Result Set from SQL table

    public String selectUser(String username) {
        String query = "SELECT * FROM " + TABLE + " WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Username");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return "User not found";
    }
    public boolean deleteUser(String username) {
        String query = "DELETE FROM " + TABLE + " WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }
    public boolean registerUser(String username, String password) {
        String query = "INSERT INTO " + TABLE + " (Username, Password, Connected, LoggedIn, Strikes) VALUES (?, ?, 0, 0, 0)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }
    public boolean updateUserPassword(String username, String password) {
        String querySelect = "SELECT Password FROM " + TABLE + " WHERE Username = ?";
        String queryUpdate = "UPDATE " + TABLE + " SET Password = ? WHERE Username = ?";
        try (PreparedStatement psSelect = connection.prepareStatement(querySelect);
             PreparedStatement psUpdate = connection.prepareStatement(queryUpdate)) {

            // Check if the user exists by selecting their current password
            psSelect.setString(1, username);
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    // If user exists, update their password
                    psUpdate.setString(1, password); // Set the new password
                    psUpdate.setString(2, username); // Set the username condition
                    psUpdate.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false; // Return false if the user doesn't exist or the update fails
    }
    public boolean updateUserStrikes(String username) {
        String querySelect = "SELECT Strikes FROM " + TABLE + " WHERE Username = ?";
        String queryUpdate = "UPDATE " + TABLE + " SET Strikes = ? WHERE Username = ?";
        try (PreparedStatement psSelect = connection.prepareStatement(querySelect);
             PreparedStatement psUpdate = connection.prepareStatement(queryUpdate)) {

            psSelect.setString(1, username);
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    int strikes = rs.getInt("Strikes") + 1;
                    psUpdate.setInt(1, strikes);
                    psUpdate.setString(2, username);
                    psUpdate.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }

    public void printResultSet(ResultSet resultSet) {
        try {
            // Metadata contains how many columns in the data
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            //  Number of fields in table
            int numberOfColumns = resultSetMetaData.getColumnCount();
            System.out.println("columns: " + numberOfColumns);  //  Display Logic
            //  Loop through the ResultSet one row at a time
            //  !!NOTE: The ResultSet starts at index 1!!
            while (resultSet.next()) {
                ArrayList<String> userData = new ArrayList<>();
                // Loop through the columns of the ResultSet (Starting at 1)
                for (int i = 1; i < numberOfColumns; ++i) {
                    String columnData = resultSet.getString(i);
                    userData.add(columnData);
                    System.out.print(columnData + "\t");  // Optional display logic
                }   //  End For
                System.out.println(); // End of row
                logUser(userData);
            }   // End While
        }   //  End Try
        catch (SQLException ex) {
            //  Handle any errors
            handleSQLException(ex);
        }   //  End Catch
    }   //  End Print Result
    public void logUser(ArrayList<String> userData){
        try {
            if (userData.size() >= 3) {  // Ensure enough fields are available
                User newUser = new User(userData.get(0), userData.get(1), userData.get(2));
                User.userList.add(newUser);  // Add the user to the static list
            } else {
                System.out.println("Insufficient user data to log.");
            }
        } catch (Exception e) {
            System.out.println("Failed to create or log user: " + e.getMessage());
        }
    }
    public void printUserList() {
        if (User.userList.isEmpty()) {
            System.out.println("No users available.");
        } else {
            System.out.println("Current Users:");
            for (User user : User.userList) {
                System.out.println(user.getUserDetails());
            }
        }
    }
    public void loadUserList() {
        String query = "SELECT * FROM " + TABLE;
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet resultSet = ps.executeQuery()) {
            // Clear existing list to prevent duplicates
            User.userList.clear();
            while (resultSet.next()) {
                // Retrieve user data from columns
                String username = resultSet.getString("Username");
                String password = resultSet.getString("Password");
                String email = resultSet.getString("Email");
                // Create a new User object and add it to the list
                User user = new User(username, password, email);
                User.userList.add(user);
            }
            System.out.println("User list loaded successfully!");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }   //  End If
        }   //  End Try
        catch (SQLException e) {
            System.out.println(e);
        }   //  End Catch
    }   //  --  End Close Method    --
    //  --  Handle SQL Exceptions Method    --
    private void handleSQLException(SQLException e) {
        System.err.println("SQLException: " + e.getMessage());
        System.err.println("SQLState: " + e.getSQLState());
        System.err.println("VendorError: " + e.getErrorCode());
    }   //  --  End Handle SQL Exception Method --
}   //  END DBMS CLASS
