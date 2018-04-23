import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RsDatabase {
	private static String name = "rsitems";
	private String url = "jdbc:mysql://localhost/?useSSL=false";
	private String SHOW_DATABASES_FOR_RS = "SHOW DATABASES LIKE '" + name + "'";
	private String CREATE_DATABASE = "CREATE DATABASE " + name;
	private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private Connection databaseConnection = null;
	
	public RsDatabase() {
		try {
			Class.forName(JDBC_DRIVER);
			try {
				if(databaseExists()) {
					print("Database " + name + " exists");
				} else {
					print("Database " + name + " DNE");
					createDatabase();
					print("Created database " + name);
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if the database for RS exists.
	 * @return boolean	true, if the database exists; false, otherwise
	 * @throws SQLException
	 */
	private boolean databaseExists() throws SQLException {
		if (databaseConnection == null) {
			establishDatabaseConnection();
		}
		
		ResultSet allDatabasesSet = databaseConnection.getMetaData().getCatalogs();
		while (allDatabasesSet.next()) {
			String databaseName = allDatabasesSet.getString(1);
			if (databaseName.equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Establishes the connection to RS database.
	 */
	private void establishDatabaseConnection() throws SQLException {
		if (databaseConnection == null) {
			databaseConnection = DriverManager.getConnection(url, "root", "brightdevelopers");
		}
	}
	
	/**
	 * Creates the database for RS items if it does not already exist.
	 * @throws SQLException
	 */
	private void createDatabase() throws SQLException {
		if (databaseConnection == null) {
			establishDatabaseConnection();
		}
		
		Statement sqlStatement = databaseConnection.createStatement();
		
		if (!databaseExists()) {
			sqlStatement.executeUpdate(CREATE_DATABASE);
		}
	}
	
	private <T> void print(T message) {
		System.out.println(message);
	}
}
