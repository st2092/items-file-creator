import java.net.*;
import java.io.*;
import javax.json.*;

public class RsPriceManager {
    private static String RS_GE_API_URL = "http://services.runescape.com/m=itemdb_rs/api/catalogue/detail.json?item=";
    private static String RS_RUNEDATE_URL = "http://secure.runescape.com/m=itemdb_rs/api/info.json";
    private static int NATURE_RUNE_ID = 561;
    private static int GE_UPDATE_CHECK_ITEM = NATURE_RUNE_ID;
    private static String API_RESPONSE_ID = "id";
    private static String API_RESPONSE_PRICE = "price";
    private static String API_RESPONSE_NAME = "name";
    private static String API_RESPONSE_CURRENT_PRICE = "current";
    
    public RsPriceManager() {
        // do nothing;
    }

    /**
     * Sends a query for the item associated with the item ID to the RS API.
     * @param itemID	the id of the item to get information about
     * @throws MalformedURLException	happens when a bad URL is created for the RS API
     */
    public void queryItem(long itemID) throws MalformedURLException {
        String itemQueryURL = RS_GE_API_URL + itemID;
        URL requestURL = new URL(itemQueryURL);
		
        try {
        	InputStream responseData = requestURL.openStream();
            JsonReader jsonDataReader = Json.createReader(responseData);

            JsonObject jsonDataObject = jsonDataReader.readObject();
            
            printItemSpecifics(jsonDataObject);
        } catch (IOException e) {
        	// item ID is invalid
        }
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
     * Item class to represent a RS item. Contains values such as the id, name, prices, and margins for buying and selling.
     */
    private class Item {
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
    		print("Convert priceStr: " + priceStr);
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
