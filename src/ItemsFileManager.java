import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ItemsFileManager {
	private JTable itemsTable = null;
	private RsPriceManager rsPriceManager = null;
	private String filename = "items.json";
	
	/* Table constants */
	private final int NAME_COL = 0;
	private final int ID_COL = 1;
	private final int BUY_PRICE_COL = 2;
	private final int BUY_MARGIN_COL = 3;
	private final int SELL_PRICE_COL = 4;
	private final int SELL_MARGIN_COL = 5;
	
	private final String NAME = "name";
	private final String ID = "id";
	private final String BUY_PRICE = "buyPrice";
	private final String SELL_PRICE = "sellPrice";
	private final String BUY_MARGIN = "buyMargin";
	private final String SELL_MARGIN = "sellMargin";
	
	public ItemsFileManager() {
		itemsTable = null;
	}
	
	public ItemsFileManager(JTable table, RsPriceManager rsPriceManager) {
		setItemsTable(table);
		setRsPriceManager(rsPriceManager);
	}
	
	public void setItemsTable(JTable table) {
		itemsTable = table;
	}
	
	public void setRsPriceManager(RsPriceManager rsPriceManager) {
		this.rsPriceManager = rsPriceManager;
	}
	
	/**
	 * Generates the items json file based on the table's content. The file will be placed in the same directory as the program.
	 */
	public void writeFile() {
		if (itemsTable == null || rsPriceManager == null) {
			return;
		}
		
		try {
			print("Creating " + filename + "...");
			FileOutputStream fileOutputStream = new FileOutputStream(filename, false);
			JsonGeneratorFactory jsonFactory = Json.createGeneratorFactory(null);
			JsonGenerator jsonGenerator = jsonFactory.createGenerator(fileOutputStream);
			jsonGenerator.writeStartObject();
			
			int count = 1;
			int row = itemsTable.getRowCount();
			int col = itemsTable.getColumnCount();
			for (int i = 0; i < row; i++) {
				jsonGenerator.writeStartObject("item" + count);
				for (int j = 0; j < col; j++) {
					DefaultTableModel itemModel = (DefaultTableModel) itemsTable.getModel();
					Object value = itemModel.getValueAt(i, j);
					String colName = getColumnName(j);
					
					// cast to the correct type; only name would result in a string the rest are integers
					if (colName.equals(NAME)) {
						jsonGenerator.write(colName, (String) value);
					} else {
						jsonGenerator.write(colName, Integer.valueOf((String) value));
					}
				}
				if (i < row) {
					jsonGenerator.writeEnd();
				}
				count++;
			}
			jsonGenerator.writeEnd();
			jsonGenerator.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getColumnName(int col) {
		switch (col) {
		case NAME_COL:
			return NAME;
		case ID_COL:
			return ID;
		case BUY_PRICE_COL:
			return BUY_PRICE;
		case BUY_MARGIN_COL:
			return BUY_MARGIN;
		case SELL_PRICE_COL:
			return SELL_PRICE;
		case SELL_MARGIN_COL:
			return SELL_MARGIN;
		default:
			return null;
		}
	}
	
	/* TODO: Add ability to parse an existing items file and load into GUI */
	public JTable parseItemsFileAt(String fullPathToItemsFile) {
		
		return null;
	}
	
	private <T> void print(T toPrint) {
		System.out.println(toPrint);
	}
}
