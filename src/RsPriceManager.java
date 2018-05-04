import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.io.*;
import javax.json.*;

public class RsPriceManager {
    private static final long minDelayBetweenApiCall = 5;	// in seconds
	private static String RS_GE_API_URL = "http://services.runescape.com/m=itemdb_rs/api/catalogue/detail.json?item=";
    private static String RS_RUNEDATE_URL = "http://secure.runescape.com/m=itemdb_rs/api/info.json";
    private static String RS_CATEGORY_API_URL = "http://services.runescape.com/m=itemdb_rs/api/catalogue/category.json?category=";
    private static String RS_ITEMS_API_BASE_URL = "http://services.runescape.com/m=itemdb_rs/api/catalogue/items.json?category=X&alpha=Y&page=Z";
    private static int NUM_CATEGORIES = 37;
    private static int NATURE_RUNE_ID = 561;
    private static int GE_UPDATE_CHECK_ITEM = NATURE_RUNE_ID;
    private static String API_RESPONSE_ID = "id";
    private static String API_RESPONSE_PRICE = "price";
    private static String API_RESPONSE_NAME = "name";
    private static String API_RESPONSE_CURRENT_PRICE = "current";
    private RsDatabase database;
    private static int ITEMS_PER_PAGE = 12;
    private int expectedTotalItems = 0;
    
    public RsPriceManager() throws SQLException {
        database = new RsDatabase();
    }

    /**
     * Sends a query for the item associated with the item ID to the RS API.
     * @param itemID	the id of the item to get information about
     * @throws MalformedURLException	happens when a bad URL is created for the RS API
     * @throws SQLException 
     */
    public void queryItem(int itemID) throws MalformedURLException, SQLException {
    	if (database.containsItem(itemID)) {
    		print("Item #" + itemID + " already in database.");
    		return;
    	}
        JsonObject rsApiResponse = getItemDataFromApi(itemID);
        
        if (rsApiResponse != null) {
            printItemSpecifics(rsApiResponse);
        } else  {
        	// item ID is invalid
        	print(itemID + " is an invalid ID.");
        }
    }
    
    /**
     * Sends a query for the item associated with the item id to the RS API. If the item does exists add it into the database.
     * @param itemID	the id of the item to get information about
     * @throws MalformedURLException	occurs when a bad URL is created for the RS API
     * @throws SQLException				occurs when there is an error writing to database
     */
    public void queryItemAndAddToDatabase(int itemID) throws MalformedURLException, SQLException {
    	if (database.containsItem(itemID)) {
    		print("Item #" + itemID + " already in database.");
    		return;
    	}
    	JsonObject rsApiResponse = getItemDataFromApi(itemID);
    	
    	if (rsApiResponse != null) {
    		JsonObject itemInfoObj = rsApiResponse.getJsonObject("item");
    		Item item = new Item (itemInfoObj);
    		try {
				database.addItem(item.getName(), item.getId());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Queries the RS API for information about the item pertaining to the id.
     * @param itemID	the id of the item to get information about
     * @return JsonObject	the response from the RS API
     * @throws MalformedURLException
     */
    private JsonObject getItemDataFromApi(int itemID) throws MalformedURLException {
    	String itemQueryURL = RS_GE_API_URL + itemID;
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
     * Prints out to console the information of the item retrieved from RS API.
     * @param jsonDataFromApi
     */
    private void printItemSpecifics(JsonObject jsonDataFromApi) {
    	JsonObject itemInfoObj = jsonDataFromApi.getJsonObject("item");
    	
    	if (itemInfoObj != null) {
    		Item item = new Item(itemInfoObj);
    		print(item.getInfo());
    	}
    }
    
    /**
     * Generic print function to output to System.out.
     * @param message	what to print to System.out
     */
    private <T> void print(T message) {
    	System.out.println(message);
    }
    
    /**
     * Iterate through every item in RS database and add it into local database.
     * @throws MalformedURLException
     * @throws InterruptedException
     * @throws SQLException 
     */
    public void update() throws MalformedURLException, InterruptedException, SQLException {
    	print("Updating database...");
    	int totalItemsFound = 0;
    	for (int category = 0; category <= NUM_CATEGORIES; category++) {
    		print("Processing category #" + category);
	    	ArrayList<Item> itemsInCategory = findEveryItemInCategory(category);
	    	print("Found " + itemsInCategory.size() + " items.");
	    	for (Item item : itemsInCategory) {
	    		database.addItem(item.getName(), item.getId());
	    		totalItemsFound++;
	    	}
    	}
    	
    	print("Found a total of " + totalItemsFound + " items out of " + expectedTotalItems + " items.");
    }
    
    /**
     * Obtain the total number of items in the specified category. The categoryBreakDownMap will be updated to contain 
     * how many items starts with each letter in the alphabet.
     * @param categoryNum					the category type to find out about (this information can be found at RS Wiki)
     * @return HashMap<String, Integer>		the hashmap that holds the number of items for each starting alphabet
     * @throws MalformedURLException
     */
    private HashMap<String, Integer> getItemsInCategoryMap(int categoryNum) throws MalformedURLException {
    	HashMap<String, Integer> categoryBreakDownMap = new HashMap<String, Integer>();
    	JsonObject categoryResponse = getCategoryResultFromApi(categoryNum);
    	int totalItemsInCategory = 0;
    	
    	if (categoryResponse != null) {
    		JsonArray alphaFrequencyResponse = categoryResponse.getJsonArray("alpha");
    		for (JsonValue alphaEntry: alphaFrequencyResponse) {
    			JsonObject alphaInfo = (JsonObject) alphaEntry;
    			String alpha = alphaInfo.getString("letter");
    			int frequency = alphaInfo.getInt("items");
    			
    			if(!categoryBreakDownMap.containsKey(alpha)) {
    				categoryBreakDownMap.put(alpha, frequency);
    				totalItemsInCategory += frequency;
    			}
    		}
    	}
    	
    	expectedTotalItems += totalItemsInCategory;
    	return categoryBreakDownMap;
    }
    
    /**
     * Find every item in the category.
     * @param categoryNum		the category number
     * @return ArrayList<Item> 	array list of items that are in the category
     * @throws MalformedURLException
     * @throws InterruptedException
     */
    private ArrayList<Item> findEveryItemInCategory(int categoryNum) throws MalformedURLException, InterruptedException {
    	ArrayList<Item> items = new ArrayList<Item>();
    	HashMap<String, Integer> categoryAlphaMap = getItemsInCategoryMap(categoryNum);
    	
    	for(String key : categoryAlphaMap.keySet()) {
    		int itemCount = categoryAlphaMap.get(key);
    		if (itemCount > 0) {
    			ArrayList<Item> itemsWithAlpha = findItemsWithAlphaFromApi(categoryNum, itemCount, key);
    			items.addAll(itemsWithAlpha);
    		}
     	}
    	
    	return items;
    }
    
    /**
     * Find the items that start with alpha from the RS API.
     * @param category		the category number
     * @param totalItems	the total number of items starting with alpha and in category
     * @param alpha			the starting letter
     * @return ArrayList<Item>	array list of items that is in the category and starts with alpha
     * @throws MalformedURLException
     * @throws InterruptedException
     */
    private ArrayList<Item> findItemsWithAlphaFromApi(int category, int totalItems, String alpha) throws MalformedURLException, InterruptedException {
    	int pages = (int) Math.ceil((double)totalItems / (double)ITEMS_PER_PAGE);
    	ArrayList<Item> itemsWithAlpha = new ArrayList<Item>();
    	
    	for (int page = 0; page <= pages; page++) {
    		JsonObject result = getItemsPageResultFromApi(category, alpha, page);
    		ArrayList<Item> itemsFromPage = extractItemsFromItemsPageResponse(result);
    		itemsWithAlpha.addAll(itemsFromPage);
    		
    		// pause for some time to ensure we don't get throttle by Jagex
			TimeUnit.SECONDS.sleep(minDelayBetweenApiCall);
    	}
    	
    	return itemsWithAlpha;
    }
    
    /**
     * Create a valid URL to query the RS items API based on the category, alpha, and page number.
     * @param category		the category number
     * @param alpha			the starting letter
     * @param pageNumber	the page number in the items that starts with alpha and in category
     * @return String		string version of the URL for RS API request
     */
    private String getCategoryAlphaUrl(int category, String alpha, int pageNumber) {
    	String categoryAlphaQuery = RS_ITEMS_API_BASE_URL;
    	if (alpha.equals("#")) {
    		alpha = "%23";
    	}
    	categoryAlphaQuery = categoryAlphaQuery.replace("X", String.valueOf(category));
    	categoryAlphaQuery = categoryAlphaQuery.replace("Y", alpha);
    	categoryAlphaQuery = categoryAlphaQuery.replace("Z", String.valueOf(pageNumber));
    	
    	return categoryAlphaQuery;
    }
    
    /**
     * Get the category response from the RS API.
     * @param categoryNum	category number to obtain information about
     * @return JsonObject	raw json object that holds the response from the RS API category request
     * @throws MalformedURLException
     */
    private JsonObject getCategoryResultFromApi(int categoryNum) throws MalformedURLException {
    	String categoryQueryUrl = RS_CATEGORY_API_URL + categoryNum;
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
    
    /**
     * Obtains the item page category response from RS API.
     * @param category		the category number
     * @param alpha			the starting letter
     * @param page			the page number of items that start with alpha and in category
     * @return JsonObject	the raw json object response from the API item page category request
     * @throws MalformedURLException
     */
    private JsonObject getItemsPageResultFromApi(int category, String alpha, int page) throws MalformedURLException {
    	String categoryAlphaUrl = getCategoryAlphaUrl(category, alpha, page);
    	URL requestUrl = new URL(categoryAlphaUrl);
    	
    	try {
    		InputStream responseData = requestUrl.openStream();
    		JsonReader jsonDataReader = Json.createReader(responseData);
    		JsonObject jsonDataObject = jsonDataReader.readObject();
    		
    		return jsonDataObject;
    	} catch (IOException e) {
    		print("[Invaid URL] " + categoryAlphaUrl);
    	}
    	
    	return null;
    }
    
    /**
     * Extract the items from the page response from RS API.
     * @param itemsPageResponseFromApi	JsonObject of the response from RS API for a category, alpha, page query
     * @return	ArrayList<Item>		array list of items from the API response
     */
    private ArrayList<Item> extractItemsFromItemsPageResponse(JsonObject itemsPageResponseFromApi) {
    	ArrayList<Item> items = new ArrayList<Item>();
    	if (itemsPageResponseFromApi != null) {
    		JsonArray itemsFromResponse = itemsPageResponseFromApi.getJsonArray("items");
    		for (JsonValue itemValue : itemsFromResponse) {
    			JsonObject itemObject = (JsonObject) itemValue;
    			items.add(new Item(itemObject));
    		}
    	}
    	
    	return items;
    }
    
    /**
     * Item class to represent a RS item. Contains values such as the id, name, prices, and margins for buying and selling.
     */
    class Item {
    	private int id;
    	private String name;
    	private long currentPrice;
    	private int buyPrice;
    	private int buyMargin;
    	private int sellPrice;
    	private int sellMargin;
    	
    	public Item(JsonObject itemInfoObj) {
    		this.id = itemInfoObj.getInt(API_RESPONSE_ID);
    		this.name = itemInfoObj.getString(API_RESPONSE_NAME);
    		
    		if (priceIsInStringFormat(itemInfoObj)) {
    			String priceFromApi = itemInfoObj.getJsonObject(API_RESPONSE_CURRENT_PRICE).getString(API_RESPONSE_PRICE);
    			this.currentPrice = convertPriceFromApiToLong(priceFromApi);
    		} else {
    			// already a number; no need to convert
    			this.currentPrice = itemInfoObj.getJsonObject(API_RESPONSE_CURRENT_PRICE).getInt(API_RESPONSE_PRICE);
    		}
    	}
    	
    	public int getId() {
    		return id;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public long getPrice() {
    		return currentPrice;
    	}
    	
    	public void setId(int newId) {
    		id = newId;
    	}
    	
    	public void setName(String newName) {
    		name = newName;
    	}
    	
    	public void setPrice(String newPrice) {
    		currentPrice = Long.parseLong(newPrice);
    	}
    	
    	public String getInfo() {
    		String info = id + ", " + name + "\n" + "Current price: " + currentPrice;
    		return info;
    	}
    	
    	/**
    	 * Determines if the price result from the RS API is in string format.
    	 * @param resultFromApi		Json object results from the RS API
    	 * @return boolean			true, if the price is in String format; false, otherwise
    	 */
    	private boolean priceIsInStringFormat(JsonObject resultFromApi) {
    		return (resultFromApi.getJsonObject(API_RESPONSE_CURRENT_PRICE).get(API_RESPONSE_PRICE).getValueType() == JsonValue.ValueType.STRING);
    	}
    	
    	/**
    	 * Converts the price from the API into an long.
    	 * @param priceStr	the price result from the RS API
    	 * @return long		number representation of the price result from RS API
    	 */
    	private long convertPriceFromApiToLong(String priceStr) {
    		long price = -1;
    		if (priceStr.contains(".") && priceStr.contains("k")) {
    			// e.g. 10.1k
    			price = Long.parseLong(priceStr.replace(".", "").replace(",", "").replace("k", "00"));
    		} else if (priceStr.contains(",")) {
    			// e.g. 9,700
    			price = Long.parseLong(priceStr.replace(",", ""));
    		} else if (priceStr.contains(".") && priceStr.contains("m")) {
    			// e.g. 12.8m
    			price = Long.parseLong(priceStr.replace(".", "").replace(",", "").replace("m", "00000"));
    		} else if (priceStr.contains(".") && priceStr.contains("b")) {
    			// e.g. 2.1b
    			price = Long.parseLong(priceStr.replace(".", "").replace(",", "").replace("b", "00000000").trim());
    		} else {
    			// e.g 100
    			price = Long.parseLong(priceStr);
    		}
    		
    		return price;
    	}
    }
}
