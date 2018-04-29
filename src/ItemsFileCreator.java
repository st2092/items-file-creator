import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.*;

public class ItemsFileCreator {

    public static void main(String[] args) throws SQLException {
    	RsPriceManager priceManager = new RsPriceManager();
    	int sharkId = 383;
    	int bluePhat = 1042;
    	int steadfastBoots = 21787;
    	int emberkeenBoots = 34978;
    	int thirdAgeAmmy = 10344;
    	int thirdAgeDruidicWreath = 19314;
    	long serenGodBow = 37632;
    	long staffOfSliske = 37636;
    	long hollyWreath = 33628;
    	int start = 564;
    	int end = 100000;
    	
    	ArrayList<Integer> testItems = new ArrayList<Integer>();
    	//testItems.add(sharkId);
    	//testItems.add(bluePhat);
    	//testItems.add(steadfastBoots);
    	//testItems.add(emberkeenBoots);
    	//testItems.add(thirdAgeAmmy);
    	//testItems.add(thirdAgeDruidicWreath);
    	//testItems.add(serenGodBow);
    	//testItems.add(staffOfSliske);
    	//testItems.add(hollyWreath);
    	
    	try {
    		for (int i = start; i < end; i++) {
    			priceManager.queryItemAndAddToDatabase(i);
    		}
    		/*for (Integer itemId : testItems) {
    			priceManager.queryItemAndAddToDatabase(itemId);
    		}*/
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
