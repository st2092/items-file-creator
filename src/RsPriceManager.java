import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.lang.ref.SoftReference;

import javax.json.*;

public class RsPriceManager {
    protected static final long minDelayBetweenApiCall = 5;	// in seconds
	private static String RS_GE_API_URL = "http://services.runescape.com/m=itemdb_rs/api/catalogue/detail.json?item=";
    private static String RS_RUNEDATE_URL = "http://secure.runescape.com/m=itemdb_rs/api/info.json";
    private static String RS_CATEGORY_API_URL = "http://services.runescape.com/m=itemdb_rs/api/catalogue/category.json?category=";
    private static String RS_ITEMS_API_BASE_URL = "http://services.runescape.com/m=itemdb_rs/api/catalogue/items.json?category=X&alpha=Y&page=Z";
    protected static HashMap<Integer, String> categoriesMap;
    private static int NUM_CATEGORIES = 37;
    private static int NATURE_RUNE_ID = 561;
    private static int GE_UPDATE_CHECK_ITEM = NATURE_RUNE_ID;
    protected static String API_RESPONSE_ID = "id";
    protected static String API_RESPONSE_PRICE = "price";
    protected static String API_RESPONSE_NAME = "name";
    protected static String API_RESPONSE_ICON = "icon";
    protected static String API_RESPONSE_ICON_LARGE = "icon_large";
    protected static String API_RESPONSE_CURRENT_PRICE = "current";
    protected RsDatabase database;
    protected static int ITEMS_PER_PAGE = 12;
    protected int expectedTotalItems = 0;
    protected SoftReference<DatabaseUpdateProgressBar> progressUi;
    
    /* RS category constants */
    private final static String CATEGORY_MISC = "Miscellaneous";
    private final static String CATEGORY_AMMO = "Ammo";
    private final static String CATEGORY_ARROWS = "Arrows";
    private final static String CATEGORY_BOLTS = "Bolts";
    private final static String CATEGORY_CONSTRUCT_MATS = "Construction materials";
    private final static String CATEGORY_CONSTRUCT_PROJS = "Construction projects";
    private final static String CATEGORY_COOKING_INGRED = "Cooking ingredients";
    private final static String CATEGORY_COSTUMES = "Costumes";
    private final static String CATEGORY_CRAFT_MATS = "Crafting materials";
    private final static String CATEGORY_FAMILIARS = "Familiars";
    private final static String CATEGORY_FARM_PRODUCE = "Farming produce";
    private final static String CATEGORY_FLETCH_MATS = "Fletching materials";
    private final static String CATEGORY_FOOD_DRINK = "Food and drink";
    private final static String CATEGORY_HERB_MATS = "Herblore Materials";
    private final static String CATEGORY_HUNT_EQUIPMENT = "Hunting equipment";
    private final static String CATEGORY_HUNT_PRODUCE = "Hunting produce";
    private final static String CATEGORY_JEWELLERY = "Jewellery";
    private final static String CATEGORY_MAGE_ARMOR = "Mage armour";
    private final static String CATEGORY_MAGE_WEAPONS = "Mage weapons";
    private final static String CATEGORY_MELEE_ARM_LOW = "Melee armour - low level";
    private final static String CATEGORY_MELEE_ARM_MID = "Melee armour - mid level";
    private final static String CATEGORY_MELEE_ARM_HIGH = "Melee armour - high level";
    private final static String CATEGORY_MELEE_WEAPS_LOW = "Melee weapons - low level";
    private final static String CATEGORY_MELEE_WEAPS_MID = "Melee weapons - mid level";
    private final static String CATEGORY_MELEE_WEAPS_HIGH = "Melee weapons - high level";
    private final static String CATEGORY_MINING_SMITHING = "Mining and smithing";
    private final static String CATEGORY_POTIONS = "Potions";
    private final static String CATEGORY_PRAYER_ARM = "Prayer armour";
    private final static String CATEGORY_PRAYER_MATS = "Prayer materials";
    private final static String CATEGORY_RANGE_ARM = "Range armour";
    private final static String CATEGORY_RANGE_WEAPS = "Range weapons";
    private final static String CATEGORY_RUNECRAFTING = "Runecrafting";
    private final static String CATEGORY_RUNES_SPELLS_TELE = "Runes, Spells, and Teleports";
    private final static String CATEGORY_SEEDS = "Seeds";
    private final static String CATEGORY_SUMMON_SCROLLS = "Summoning scrolls";
    private final static String CATEGORY_TOOLS_CONTAINERS = "Tools and container";
    private final static String CATEGORY_WOODCUT_PRODUCT = "Woodcutting product";
    private final static String CATEGORY_POCKET_ITEM = "Pocket items";
    
    private final static int CATEGORY_MISC_ID = 0;
    private final static int CATEGORY_AMMO_ID = 1;
    private final static int CATEGORY_ARROWS_ID = 2;
    private final static int CATEGORY_BOLTS_ID = 3;
    private final static int CATEGORY_CONSTRUCT_MATS_ID = 4;
    private final static int CATEGORY_CONSTRUCT_PROJS_ID = 5;
    private final static int CATEGORY_COOKING_INGRED_ID = 6;
    private final static int CATEGORY_COSTUMES_ID = 7;
    private final static int CATEGORY_CRAFT_MATS_ID = 8;
    private final static int CATEGORY_FAMILIARS_ID = 9;
    private final static int CATEGORY_FARM_PRODUCE_ID = 10;
    private final static int CATEGORY_FLETCH_MATS_ID = 11;
    private final static int CATEGORY_FOOD_DRINK_ID = 12;
    private final static int CATEGORY_HERB_MATS_ID = 13;
    private final static int CATEGORY_HUNT_EQUIPMENT_ID = 14;
    private final static int CATEGORY_HUNT_PRODUCE_ID = 15;
    private final static int CATEGORY_JEWELLERY_ID = 16;
    private final static int CATEGORY_MAGE_ARMOR_ID = 17;
    private final static int CATEGORY_MAGE_WEAPONS_ID = 18;
    private final static int CATEGORY_MELEE_ARM_LOW_ID = 19;
    private final static int CATEGORY_MELEE_ARM_MID_ID = 20;
    private final static int CATEGORY_MELEE_ARM_HIGH_ID = 21;
    private final static int CATEGORY_MELEE_WEAPS_LOW_ID = 22;
    private final static int CATEGORY_MELEE_WEAPS_MID_ID = 23;
    private final static int CATEGORY_MELEE_WEAPS_HIGH_ID = 24;
    private final static int CATEGORY_MINING_SMITHING_ID = 25;
    private final static int CATEGORY_POTIONS_ID = 26;
    private final static int CATEGORY_PRAYER_ARM_ID = 27;
    private final static int CATEGORY_PRAYER_MATS_ID = 28;
    private final static int CATEGORY_RANGE_ARM_ID = 29;
    private final static int CATEGORY_RANGE_WEAPS_ID = 30;
    private final static int CATEGORY_RUNECRAFTING_ID = 31;
    private final static int CATEGORY_RUNES_SPELLS_TELE_ID = 32;
    private final static int CATEGORY_SEEDS_ID = 33;
    private final static int CATEGORY_SUMMON_SCROLLS_ID = 34;
    private final static int CATEGORY_TOOLS_CONTAINERS_ID = 35;
    private final static int CATEGORY_WOODCUT_PRODUCT_ID = 36;
    private final static int CATEGORY_POCKET_ITEM_ID = 37;
    
    public RsPriceManager() throws SQLException {
        database = new RsDatabase(RsDatabase.RS3);
        loadCategoriesMap();
    }

    public static String getApiReponseId() {
    	return API_RESPONSE_ID;
    }
    
    public static String getApiReponsePrice() {
    	return API_RESPONSE_PRICE;
    }
    
    public static String getApiResponseName() {
    	return API_RESPONSE_NAME;
    }
    
    public static String getApiResponseIcon() {
    	return API_RESPONSE_ICON;
    }
    
    public static String getApiResponseIconLarge() {
    	return API_RESPONSE_ICON_LARGE;
    }
    
    public static String getApiResponseCurrentPrice() {
    	return API_RESPONSE_CURRENT_PRICE;
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
     * Returns an Item object that holds detailed information about the item pertaining to the passed in name.
     * @param itemName		name of the item to find out about
     * @return Item			object containing detailed information about the item from the RS API
     */
    public Item getInfoOnItem(String itemName) {
    	try {
			if (database.containsItem(itemName)) {
				
				JsonObject rsApiResponse = getItemDataFromApi(database.getId(itemName));
				if (rsApiResponse != null) {
					JsonObject itemInfoObj = rsApiResponse.getJsonObject("item");
					Item item = new Item(itemInfoObj);
					return item;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
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
    protected JsonObject getItemDataFromApi(int itemID) throws MalformedURLException {
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
    protected void printItemSpecifics(JsonObject jsonDataFromApi) {
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
    protected <T> void print(T message) {
    	System.out.println(message);
    }
    
    /**
     * Iterate through every item in RS database and add it into local database.
     * Note: The total items found is going to be more than the expected because of duplicates.
     * When a category and alphabet have multiple pages page 0 and 1 tend to be duplicate.
     * @throws MalformedURLException
     * @throws InterruptedException
     * @throws SQLException 
     */
    public void update() throws MalformedURLException, InterruptedException, SQLException {
    	print("Updating database...");
    	int totalItemsFound = 0;
    	for (int category = 0; category <= NUM_CATEGORIES; category++) {
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
    
    protected void updateStatusWithCategory(int categoryId) {
    	if (getProgressUi().get() != null) {
    		String statusUpdate = "Updating " + categoriesMap.get(categoryId) + "...";
    		getProgressUi().get().setStatusText(statusUpdate);
    	}
    }
    
    protected void signalUpdateCompleteForUi() {
    	if (getProgressUi().get() != null) {
    		getProgressUi().get().complete();
    	}
    }
    
    /**
     * Obtain the total number of items in the specified category. The categoryBreakDownMap will be updated to contain 
     * how many items starts with each letter in the alphabet.
     * @param categoryNum					the category type to find out about (this information can be found at RS Wiki)
     * @return HashMap<String, Integer>		the hashmap that holds the number of items for each starting alphabet
     * @throws MalformedURLException
     */
    protected HashMap<String, Integer> getItemsInCategoryMap(int categoryNum) throws MalformedURLException {
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
    	
    	print("Found " + totalItemsInCategory + " items in category #" + categoryNum);
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
    protected ArrayList<Item> findEveryItemInCategory(int categoryNum) throws MalformedURLException, InterruptedException {
    	ArrayList<Item> items = new ArrayList<Item>();
    	HashMap<String, Integer> categoryAlphaMap = getItemsInCategoryMap(categoryNum);

    	for(String key : categoryAlphaMap.keySet()) {
    		int itemCount = categoryAlphaMap.get(key);
    		if (itemCount > 0) {
    			print("Category #" + categoryNum + ", Item Count: " + itemCount + ", Alpha: " + key);
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
    protected ArrayList<Item> findItemsWithAlphaFromApi(int category, int totalItems, String alpha) throws MalformedURLException, InterruptedException {
    	int pages = 0;
    	if (totalItems > ITEMS_PER_PAGE) {
    		// round up by one page
    		pages = (int) Math.ceil((double)totalItems / (double)ITEMS_PER_PAGE);
    	}
    	
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
    protected String getCategoryAlphaUrl(int category, String alpha, int pageNumber) {
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
    protected JsonObject getCategoryResultFromApi(int categoryNum) throws MalformedURLException {
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
    protected JsonObject getItemsPageResultFromApi(int category, String alpha, int page) throws MalformedURLException {
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
    protected ArrayList<Item> extractItemsFromItemsPageResponse(JsonObject itemsPageResponseFromApi) {
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

    
    protected void loadCategoriesMap() {
    	if (categoriesMap == null) {
    		categoriesMap = new HashMap<Integer, String>();
    	}
    	
    	categoriesMap.clear();
    	
    	for (int categoryId = 0; categoryId <= NUM_CATEGORIES; categoryId++) {
    		String categoryName = getCategoryNameFromId(categoryId);
    		categoriesMap.put(categoryId, categoryName);
    	}
    }
    
    protected String getCategoryNameFromId(int categoryId) {
    	switch (categoryId) {
    	case CATEGORY_MISC_ID:
    		return CATEGORY_MISC;
    	case CATEGORY_AMMO_ID:
    		return CATEGORY_AMMO;
    	case CATEGORY_ARROWS_ID:
    		return CATEGORY_ARROWS;
    	case CATEGORY_BOLTS_ID:
    		return CATEGORY_BOLTS;
    	case CATEGORY_CONSTRUCT_MATS_ID:
    		return CATEGORY_CONSTRUCT_MATS;
    	case CATEGORY_CONSTRUCT_PROJS_ID:
    		return CATEGORY_CONSTRUCT_PROJS;
    	case CATEGORY_COOKING_INGRED_ID:
    		return CATEGORY_COOKING_INGRED;
    	case CATEGORY_COSTUMES_ID:
    		return CATEGORY_COSTUMES;
    	case CATEGORY_CRAFT_MATS_ID:
    		return CATEGORY_CRAFT_MATS;
    	case CATEGORY_FAMILIARS_ID:
    		return CATEGORY_FAMILIARS;
    	case CATEGORY_FARM_PRODUCE_ID:
    		return CATEGORY_FARM_PRODUCE;
    	case CATEGORY_FLETCH_MATS_ID:
    		return CATEGORY_FLETCH_MATS;
    	case CATEGORY_FOOD_DRINK_ID:
    		return CATEGORY_FOOD_DRINK;
    	case CATEGORY_HERB_MATS_ID:
    		return CATEGORY_HERB_MATS;
    	case CATEGORY_HUNT_EQUIPMENT_ID:
    		return CATEGORY_HUNT_EQUIPMENT;
    	case CATEGORY_HUNT_PRODUCE_ID:
    		return CATEGORY_HUNT_PRODUCE;
    	case CATEGORY_JEWELLERY_ID:
    		return CATEGORY_JEWELLERY;
    	case CATEGORY_MAGE_ARMOR_ID:
    		return CATEGORY_MAGE_ARMOR;
    	case CATEGORY_MAGE_WEAPONS_ID:
    		return CATEGORY_MAGE_WEAPONS;
    	case CATEGORY_MELEE_ARM_LOW_ID:
    		return CATEGORY_MELEE_ARM_LOW;
    	case CATEGORY_MELEE_ARM_MID_ID:
    		return CATEGORY_MELEE_ARM_MID;
    	case CATEGORY_MELEE_ARM_HIGH_ID:
    		return CATEGORY_MELEE_ARM_HIGH;
    	case CATEGORY_MELEE_WEAPS_LOW_ID:
    		return CATEGORY_MELEE_WEAPS_LOW;
    	case CATEGORY_MELEE_WEAPS_MID_ID:
    		return CATEGORY_MELEE_WEAPS_MID;
    	case CATEGORY_MELEE_WEAPS_HIGH_ID:
    		return CATEGORY_MELEE_WEAPS_HIGH;
    	case CATEGORY_MINING_SMITHING_ID:
    		return CATEGORY_MINING_SMITHING;
    	case CATEGORY_POTIONS_ID:
    		return CATEGORY_POTIONS;
    	case CATEGORY_PRAYER_ARM_ID:
    		return CATEGORY_PRAYER_ARM;
    	case CATEGORY_PRAYER_MATS_ID:
    		return CATEGORY_PRAYER_MATS;
    	case CATEGORY_RANGE_ARM_ID:
    		return CATEGORY_RANGE_ARM;
    	case CATEGORY_RANGE_WEAPS_ID:
    		return CATEGORY_RANGE_WEAPS;
    	case CATEGORY_RUNECRAFTING_ID:
    		return CATEGORY_RUNECRAFTING;
    	case CATEGORY_RUNES_SPELLS_TELE_ID:
    		return CATEGORY_RUNES_SPELLS_TELE;
    	case CATEGORY_SEEDS_ID:
    		return CATEGORY_SEEDS;
    	case CATEGORY_SUMMON_SCROLLS_ID:
    		return CATEGORY_SUMMON_SCROLLS;
    	case CATEGORY_TOOLS_CONTAINERS_ID:
    		return CATEGORY_TOOLS_CONTAINERS;
    	case CATEGORY_WOODCUT_PRODUCT_ID:
    		return CATEGORY_WOODCUT_PRODUCT;
    	case CATEGORY_POCKET_ITEM_ID:
    		return CATEGORY_POCKET_ITEM;
    	default:
    		return null;
    	}
    }
    
	public SoftReference<DatabaseUpdateProgressBar> getProgressUi() {
		return progressUi;
	}

	public void setProgressUi(SoftReference<DatabaseUpdateProgressBar> progressUi) {
		this.progressUi = progressUi;
	}
	
	public int getType() {
		return RsDatabase.RS3;
	}
	
	public RsDatabase getRsDatabaseInstance() {
		return database;
	}
}
