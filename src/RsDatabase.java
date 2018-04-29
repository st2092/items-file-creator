import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RsDatabase {
	private static String name = "rsitems";
	private static String tableName = "items";
	private String url = "jdbc:mysql://localhost/?useSSL=false";
	private String SHOW_DATABASES_FOR_RS = "SHOW DATABASES LIKE '" + name + "'";
	private String USE_DATABASE = "USE " + name;
	private String CREATE_TABLE = "CREATE TABLE ITEMS (id INTEGER not NULL, name VARCHAR(255), PRIMARY KEY (id))";
	private String CREATE_DATABASE = "CREATE DATABASE " + name;
	private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private Connection databaseConnection = null;
	
	public RsDatabase() {
		try {
			Class.forName(JDBC_DRIVER);
			try {
				if(databaseExists()) {
					selectRsDatabase();
					if (!tableExists()) {
						createTable();
					}
				} else {
					print("Database " + name + " DNE");
					createDatabase();
					createTable();
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
	 * Checks if the table exists in the database.
	 * @return boolean	true, if the table exists; false, otherwise
	 * @throws SQLException
	 */
	private boolean tableExists() throws SQLException {
		if (databaseConnection == null) {
			establishDatabaseConnection();
		}
		
		ResultSet allTablesSet = databaseConnection.getMetaData().getTables(null, null, "ITEMS", new String[] {"TABLE"});
		while(allTablesSet.next()) {
			String nameOfTable = allTablesSet.getString("TABLE_NAME");
			if (nameOfTable.equals(tableName)) {
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
	
	/**
	 * Creates the table for items if the table does not exist in the database yet.
	 * @throws SQLException
	 */
	private void createTable() throws SQLException {
		if (databaseConnection == null) {
			establishDatabaseConnection();
		}
		
		Statement sqlStatement = databaseConnection.createStatement();
		
		if (!tableExists()) {
			sqlStatement.executeUpdate(CREATE_TABLE);
			print("Created table for items.");
		}
	}
	
	/**
	 * Selects the RS item database.
	 * @throws SQLException
	 */
	private void selectRsDatabase() throws SQLException {
		if (databaseConnection == null) {
			establishDatabaseConnection();
		}
		
		Statement sqlStatement = databaseConnection.createStatement();
		sqlStatement.executeUpdate(USE_DATABASE);
	}
	
	/**
	 * Adds an entry into the database table pertaining to the id item and name.
	 * @param name	name of the item
	 * @param id	id of the item
	 * @throws SQLException 
	 */
	public void addItem(String name, int id) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		if (databaseExists() && tableExists() && !containsItem(id)) {
			String query = " insert into items (id, name)" + " values (?, ?)";
			PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
			preparedStatement.setLong(1, id);
			preparedStatement.setString(2, name);
			
			preparedStatement.execute();
			
			print("Added (" + id + ", " + name + ") into table.");
		}
	}
	
	/**
	 * Returns whether the item is already in the database.
	 * @param itemId	the id of the item
	 * @return boolean	true, if the item id is in the database table; false, otherwise
	 * @throws SQLException
	 */
	public boolean containsItem(int itemId) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		String query = "SELECT id FROM ITEMS WHERE id = " + itemId;
		PreparedStatement idQuery = databaseConnection.prepareStatement(query);
		ResultSet results = idQuery.executeQuery(query);
		while (results.next()) {
			int id = results.getInt(1);
			if (id == itemId) {
				return true;
			}
		}
		
		return false;
	}
	
	private <T> void print(T message) {
		System.out.println(message);
	}
}
