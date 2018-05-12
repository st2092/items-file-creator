import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Item class to represent a RS item. Contains values such as the id, name, prices, and margins for buying and selling.
 */
public class Item {
	private int id;
	private String name;
	private long currentPrice;
	private String iconUrl;
	private String iconBigUrl;
	private int buyPrice;
	private int buyMargin;
	private int sellPrice;
	private int sellMargin;
	
	public Item(JsonObject itemInfoObj) {
		this.id = itemInfoObj.getInt(RsPriceManager.getApiReponseId());
		this.name = itemInfoObj.getString(RsPriceManager.getApiResponseName());
		iconUrl = itemInfoObj.getString(RsPriceManager.getApiResponseIcon());
		iconBigUrl = itemInfoObj.getString(RsPriceManager.getApiResponseIconLarge());
		
		if (priceIsInStringFormat(itemInfoObj)) {
			String priceFromApi = itemInfoObj.getJsonObject(RsPriceManager.getApiResponseCurrentPrice()).getString(RsPriceManager.getApiReponsePrice());
			this.currentPrice = convertPriceFromApiToLong(priceFromApi);
		} else {
			// already a number; no need to convert
			this.currentPrice = itemInfoObj.getJsonObject(RsPriceManager.getApiResponseCurrentPrice()).getInt(RsPriceManager.getApiReponsePrice());
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
	
	public void setBuyPrice(int newBuyPrice) {
		buyPrice = newBuyPrice;
	}
	
	public int getBuyPrice() {
		return buyPrice;
	}
	
	public void setBuyMargin(int newBuyMargin) {
		buyMargin = newBuyMargin;
	}
	
	public int getBuyMargin() {
		return buyMargin;
	}
	
	public void setSellPrice(int newSellPrice) {
		sellPrice = newSellPrice;
	}
	
	public int getSellPrice() {
		return sellPrice;
	}
	
	public void setSellMargin(int newSellMargin) {
		sellMargin = newSellMargin;
	}
	
	public int getSellMargin() {
		return sellMargin;
	}
	
	public String getInfo() {
		String info = id + ", " + name + "\n" + "Current price: " + currentPrice;
		return info;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}
	
	public String getIconBigUrl() {
		return iconBigUrl;
	}
	
	/**
	 * Determines if the price result from the RS API is in string format.
	 * @param resultFromApi		Json object results from the RS API
	 * @return boolean			true, if the price is in String format; false, otherwise
	 */
	private boolean priceIsInStringFormat(JsonObject resultFromApi) {
		return (resultFromApi.getJsonObject(RsPriceManager.getApiResponseCurrentPrice()).get(RsPriceManager.getApiReponsePrice()).getValueType() == JsonValue.ValueType.STRING);
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