import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class OSRsPriceManager extends RsPriceManager {
	private final static String OSRS_GE_API_URL = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=";
	private final static String OSRS_CATEGORY_API_URL = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/category.json?category=";
	private final static String OSRS_ITEMS_API_BASE_URL = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/items.json?category=X&alpha=Y&page=Z";
	private final static int OSRS_NUM_CATEGORIES = 1;
	
	public OSRsPriceManager() throws SQLException {
		database = new RsDatabase(RsDatabase.OLD_SCHOOL_RS);
        loadCategoriesMap();
	}

	@Override
	protected JsonObject getItemDataFromApi(int itemID) throws MalformedURLException {
		String itemQueryURL = OSRS_GE_API_URL + itemID;
        URL requestURL = new URL(itemQueryURL);
		
        try {
        	InputStream responseData = requestURL.openStream();
            JsonReader jsonDataReader = Json.createReader(responseData);

            JsonObject jsonDataObject = jsonDataReader.readObject();
            return jsonDataObject;
        } catch (IOException e) {
        	// item ID is invalid
        	print(itemID + " is an invalid ID.");
        }
        
        return null;
	}
	
	 /**
     * Iterate through every item in RS database and add it into local database.
     * Note: The total items found is going to be more than the expected because of duplicates.
     * When a category and alphabet have multiple pages page 0 and 1 tend to be duplicate.
     * @throws MalformedURLException
     * @throws InterruptedException
     * @throws SQLException 
     */
	@Override
    public void update() throws MalformedURLException, InterruptedException, SQLException {
		print("Updating database...");
    	int totalItemsFound = 0;
    	for (int category = 1; category <= OSRS_NUM_CATEGORIES; category++) {
    		print("Processing category #" + category);
    		updateStatusWithCategory(category);
    		
	    	ArrayList<Item> itemsInCategory = findEveryItemInCategory(category);
	    	print("Found " + itemsInCategory.size() + " items.");
	    	for (Item item : itemsInCategory) {
	    		database.addItem(item.getName(), item.getId());
	    		totalItemsFound++;
	    	}
    	}
    	signalUpdateCompleteForUi();
    	print("Found a total of " + totalItemsFound + " items out of " + expectedTotalItems + " items.");
	}
	
	/**
     * Get the category response from the RS API.
     * @param categoryNum	category number to obtain information about
     * @return JsonObject	raw json object that holds the response from the RS API category request
     * @throws MalformedURLException
     */
	@Override
    protected JsonObject getCategoryResultFromApi(int categoryNum) throws MalformedURLException {
    	String categoryQueryUrl = OSRS_CATEGORY_API_URL + categoryNum;
    	URL requestUrl = new URL (categoryQueryUrl);
    	
    	try {
    		InputStream responseData = requestUrl.openStream();
    		JsonReader jsonDataReader = Json.createReader(responseData);
    		JsonObject jsonDataObject = jsonDataReader.readObject();
    		
    		return jsonDataObject;
    	} catch (IOException e) {
    		print("[Invalid URL] " + categoryQueryUrl);
    	}
    	
    	return null;
    }
	
	@Override
	protected void loadCategoriesMap() {
    	if (categoriesMap == null) {
    		categoriesMap = new HashMap<Integer, String>();
    	}
    	
    	categoriesMap.clear();
    	
    	for (int categoryId = 1; categoryId <= OSRS_NUM_CATEGORIES; categoryId++) {
    		String categoryName = getCategoryNameFromId(categoryId);
    		categoriesMap.put(categoryId, categoryName);
    	}
    }
	
	/**
     * Create a valid URL to query the RS items API based on the category, alpha, and page number.
     * @param category		the category number
     * @param alpha			the starting letter
     * @param pageNumber	the page number in the items that starts with alpha and in category
     * @return String		string version of the URL for RS API request
     */
    protected String getCategoryAlphaUrl(int category, String alpha, int pageNumber) {
    	String categoryAlphaQuery = OSRS_ITEMS_API_BASE_URL;
    	if (alpha.equals("#")) {
    		alpha = "%23";
    	}
    	categoryAlphaQuery = categoryAlphaQuery.replace("X", String.valueOf(category));
    	categoryAlphaQuery = categoryAlphaQuery.replace("Y", alpha);
    	categoryAlphaQuery = categoryAlphaQuery.replace("Z", String.valueOf(pageNumber));
    	
    	return categoryAlphaQuery;
    }
	
	@Override
	protected String getCategoryNameFromId(int categoryId) {
		return "database";
	}
	
	@Override
	public int getType() {
		return RsDatabase.OLD_SCHOOL_RS;
	}
}
