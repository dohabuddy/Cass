package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
//  !!!WARNING: MAKE SURE THE JDBC CONNECTOR JAR IS IN THE BUILD PATH!!!
public class DBMS {
    //  Username and password for MySQL workbench connection
    private static final String usernameSQL = "CassCo";
    private static final String passwordSQL = "Password.";
    // Objects to be used for database access
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet result = null;
    //  Connect to the "UserDatabase" database scheme,
    //  Default port is 3306 (jdbc:mysql://<ip address>:<port>/<schema>)
    private final String schema = "sys";
    private final String table = "users";
    private final String url = "jdbc:mysql://127.0.0.1:3306/" + schema;
    //  Constructor for DBMS
    public DBMS (String username, String password) {
        try {
            // Make connection to the database
            connection = DriverManager.getConnection(url, username, password);
            // These will Send queries to the database
            statement = connection.createStatement();
            result = statement.executeQuery("SELECT VERSION()");
            if (result.next()) {
                System.out.println("MySQL version: " + result.getString(1) + "\n=====================\n");
            }   //  End If
        }   //  End Try
        catch (SQLException ex) {
            //  Handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }   //  End Catch
    }   //  End DBMS Constructor
    //  Accesses Preform Operations in MySQL Database
    public void accessDatabase() {
        try {
            // Query will return a ResultSet then get and print all records in the table
            System.out.println("Original Contents");
            String SQL = "SELECT * FROM " + table + ";";
            result = statement.executeQuery(SQL);
            printResultSet(result);
            // Insert new user into the table
            System.out.println("Inserted Contents");    //  Display Logic
            String username = "'testUsername'"; // SQL expects strings to be single quoted
            String password = "'testUsername'";
            int connected = 0;
            int loggedIn = 0;
            int strikes = 0;
            statement.executeUpdate("INSERT INTO " + table + " VALUE(" + username + ", " + password + ", " + connected + ", " + loggedIn + "," +  strikes + ");");
            // Get and print all records in the table
            result = statement.executeQuery("SELECT * FROM " + table + ";");
            printResultSet(result);
            // Modify a record in the table and get the result set of records
            result = statement.executeQuery("SELECT * FROM " + table + " WHERE Username=" + username + ";");
            //  Move the iterator to the record, if there is no record this will throw an exception???
            result.next();
            //  Get the strikes column and convert it to integer
            strikes = Integer.parseInt(result.getString(5));
            System.out.println("Updated Contents");
            statement.executeUpdate("UPDATE " + table + " SET strikes=" + (strikes + 1) + " WHERE Username=" + username + ";");
            // Get and print all records in the table
            result = statement.executeQuery("SELECT * FROM " + table + " WHERE Username=" + username + ";");
            printResultSet(result);
            //  Delete Record
            result = statement.executeQuery("DELETE * FROM " + table + " WHERE Username=" + username + ";");
            // Get and print all records in the table
            result = statement.executeQuery("SELECT * FROM " + table + " WHERE Username=" + username + ";");
            System.out.println("should be blank");
            printResultSet(result);
        }   //  End try
        catch (SQLException ex) {
            //  Handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }   //  End Catch
    }   //  End Access Database
    //  Print Result Set from SQL table
    public void printResultSet(ResultSet resultSet)
    {
        try {
            // Metadata contains how many columns in the data
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            //  Number of fields in table
            int numberOfColumns = resultSetMetaData.getColumnCount();
            System.out.println("columns: " + numberOfColumns);  //  Display Logic
            //  Loop through the ResultSet one row at a time
            //  !!NOTE: The ResultSet starts at index 1!!
            while (resultSet.next()) {
                // Loop through the columns of the ResultSet (Starting at 1)
                for (int i = 1; i < numberOfColumns; ++i) {
                    System.out.print(resultSet.getString(i) + "\t");    //  Display logic
                }   //  End For
                System.out.println(resultSet.getString(numberOfColumns));
            }   // End While
        }   //  End Try
        catch (SQLException ex) {
            //  Handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }   //  End Catch
    }   //  End Print Result
    //  Main test
    public static void LoadUserDatabase() {
        // Make the JDBC connection (Java Database Connectivity)
        new DBMS(usernameSQL, passwordSQL);
        // Perform some SQL operations
    }   //  End Main
}   //  End DBMS Class
