import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RsDatabase {
	private static String name = "rsitems";
	private static String tableName = "items";
	private final String sqliteUrl = "jdbc:sqlite:rsitems.db";
	private final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
	private String url = "jdbc:mysql://localhost/?useSSL=false";
	private String SHOW_DATABASES_FOR_RS = "SHOW DATABASES LIKE '" + name + "'";
	private String USE_DATABASE = "USE " + name;
	private String CREATE_TABLE = "CREATE TABLE ITEMS (id INTEGER not NULL, name VARCHAR(255), PRIMARY KEY (id))";
	private String CREATE_DATABASE = "CREATE DATABASE " + name;
	private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private Connection databaseConnection = null;
	private final boolean IS_USING_MYSQL = false;
	
	public RsDatabase() {
		try {
			Class.forName(SQLITE_JDBC_DRIVER);
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
		
		
		if (IS_USING_MYSQL) {
			ResultSet allDatabasesSet = databaseConnection.getMetaData().getCatalogs();
			while (allDatabasesSet.next()) {
				String databaseName = allDatabasesSet.getString(1);
				if (databaseName.equals(name)) {
					return true;
				}
			}
		} else {
			if (databaseConnection != null) {
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
	private boolean tableExists() throws SQLException{
		if (databaseConnection == null) {
			establishDatabaseConnection();
		}
		
		if (IS_USING_MYSQL) {
			ResultSet allTablesSet = databaseConnection.getMetaData().getTables(null, null, "ITEMS", new String[] {"TABLE"});
			while(allTablesSet.next()) {
				String nameOfTable = allTablesSet.getString("TABLE_NAME");
				if (nameOfTable.equals(tableName)) {
					return true;
				}
			}
		} else {
			try {
				Statement sqlStatement = databaseConnection.createStatement();
				String query = "SELECT * FROM " + tableName;
				sqlStatement.executeQuery(query);
				return true;
			} catch (SQLException e) {
				// sqlite throws an exception if table does not exist
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Establishes the connection to RS database.
	 */
	private void establishDatabaseConnection() throws SQLException {
		if (databaseConnection == null) {
			if (IS_USING_MYSQL) {
				databaseConnection = DriverManager.getConnection(url, "root", "brightdevelopers");
			} else {
				databaseConnection = DriverManager.getConnection(sqliteUrl);
			}
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
		
		if (IS_USING_MYSQL) {
			Statement sqlStatement = databaseConnection.createStatement();
			sqlStatement.executeUpdate(USE_DATABASE);
		}
	}
	
	public void addItem(String name, int id) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		if (databaseExists() && tableExists() && !containsItem(id)) {
			String query = " insert into items (id, name)" + " values (?, ?)";
			PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);
			
			preparedStatement.execute();
			
			print("Added (" + id + ", " + name + ") into table.");
		}
	}
	
	/**
	 * Adds an entry into the database table pertaining to the id item and name.
	 * @param name			name of the item
	 * @param id			id of the item
	 * @param iconUrl		the link to the item icon
	 * @param iconBigUrl	the link to the item big icon
	 * @throws SQLException 
	 */
	public void addItem(String name, int id, String iconUrl, String iconBigUrl) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		if (databaseExists() && tableExists() && !containsItem(id)) {
			String query = " insert into items (id, name, icon, icon_big)" + " values (?, ?, ?, ?)";
			PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, iconUrl);
			preparedStatement.setString(4, iconBigUrl);
			
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
		ResultSet results;
		if (IS_USING_MYSQL) {
			results = idQuery.executeQuery(query);
		} else {
			results = idQuery.executeQuery();
		}
		while (results.next()) {
			int id = results.getInt(1);
			if (id == itemId) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns whether the item is already in the database.
	 * @param itemName		the name of the item
	 * @return boolean		true, if the item is in the database; false, otherwise
	 * @throws SQLException
	 */
	public boolean containsItem(String itemName) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		String itemNameQuery = itemName;
		// special case; some items have ' in their name which need to be set to a literal
		if (itemName.contains("'")) {
			itemNameQuery = itemName.replace("'", "''");
		}

		String query = "SELECT name FROM items WHERE name = '" + itemNameQuery + "'";
		PreparedStatement idQuery = databaseConnection.prepareStatement(query);
		ResultSet results;
		if (IS_USING_MYSQL) {
			results = idQuery.executeQuery(query);
		} else {
			results = idQuery.executeQuery();
		}
		while (results.next()) {
			String name = results.getString(1);
			if (name.equals(itemName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public int getId(String itemName) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		// special case; some items have ' in their name which need to be set to a literal 
		if (itemName.contains("'")) {
			itemName = itemName.replace("'", "''");
		}
		String query = "SELECT id FROM items WHERE name = '" + itemName + "'";
		PreparedStatement idQuery = databaseConnection.prepareStatement(query);
		ResultSet results;
		if (IS_USING_MYSQL) {
			results = idQuery.executeQuery(query);
		} else {
			results = idQuery.executeQuery();
		}
		while (results.next()) {
			int id = results.getInt(1);
			return id;
		}
		
		return -1;
	}
	
	/**
	 * Note: The icon url is based on time of access. So, the url in database will become outdated. That is there is no need to 
	 * store the icon url in the database at all. To get the image, we will need to get it from a fresh URL.
	 */
	public String getItemIconUrl(int itemId) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		String query = "SELECT icon FROM ITEMS WHERE id = " + itemId;
		PreparedStatement iconUrlQuery = databaseConnection.prepareStatement(query);
		ResultSet results = iconUrlQuery.executeQuery(query);
		while (results.next()) {
			return results.getString(1);
		}
		
		return null;
	}
	
	public String getItemIconUrl(String itemName) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		String query = "SELECT icon FROM items WHERE name = '" + itemName + "'";
		PreparedStatement iconUrlQuery = databaseConnection.prepareStatement(query);
		ResultSet results = iconUrlQuery.executeQuery(query);
		while (results.next()) {
			return results.getString(1);	
		}
		
		return null;
	}
	
	public String getItemIconBigUrl(int itemId) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		String query = "SELECT icon_big FROM ITEMS WHERE id = " + itemId;
		PreparedStatement iconUrlQuery = databaseConnection.prepareStatement(query);
		ResultSet results = iconUrlQuery.executeQuery(query);
		while (results.next()) {
			return results.getString(1);
		}
		
		return null;
	}
	
	public String getItemIconBigUrl(String itemName) throws SQLException {
		if (databaseConnection != null) {
			establishDatabaseConnection();
			selectRsDatabase();
		}
		
		String query = "SELECT icon_big FROM items WHERE name = '" + itemName + "'";
		PreparedStatement iconUrlQuery = databaseConnection.prepareStatement(query);
		ResultSet results = iconUrlQuery.executeQuery(query);
		while (results.next()) {
			return results.getString(1);
		}
		
		return null;
	}
	
	/**
	 * Get a list of all the items in the database.
	 * @return ArrayList<String>	an array list of all the items in the RS database
	 * @throws SQLException
	 */
	public ArrayList<String> getAllItemNames() throws SQLException {
		ArrayList<String> items = new ArrayList<String>();
		
		if (databaseExists() && tableExists()) {
			String query = "SELECT name from " + tableName;
			PreparedStatement getAllItemsQuery = databaseConnection.prepareStatement(query);
			
			ResultSet results;
			if (IS_USING_MYSQL) {
				results = getAllItemsQuery.executeQuery(query);
			} else {
				results = getAllItemsQuery.executeQuery();
			}
			
			while(results.next()) {
				String name = results.getString(1);
				items.add(name);
			}
		}
		
		return items;
	}
	
	private <T> void print(T message) {
		System.out.println(message);
	}
}
