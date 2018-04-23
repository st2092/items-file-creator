import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.*;

public class ItemsFileCreator {

    public static void main(String[] args) throws SQLException {
    	RsPriceManager priceManager = new RsPriceManager();
    	long sharkId = 383;
    	long bluePhat = 1042;
    	long steadfastBoots = 21787;
    	long emberkeenBoots = 34978;
    	long thirdAgeAmmy = 10344;
    	long thirdAgeDruidicWreath = 19314;
    	long serenGodBow = 37632;
    	long staffOfSliske = 37636;
    	long hollyWreath = 33628;
    	long start = 0;
    	long end = 1;
    	
    	ArrayList<Long> testItems = new ArrayList<Long>();
    	testItems.add(sharkId);
    	//testItems.add(bluePhat);
    	//testItems.add(steadfastBoots);
    	//testItems.add(emberkeenBoots);
    	//testItems.add(thirdAgeAmmy);
    	//testItems.add(thirdAgeDruidicWreath);
    	//testItems.add(serenGodBow);
    	//testItems.add(staffOfSliske);
    	//testItems.add(hollyWreath);
    	
    	try {
    		/*
			for (long i = start; i < end; i++ ) {
				priceManager.queryItem(hollyWreath);
			}*/
    		for (Long itemId : testItems) {
    			priceManager.queryItem(itemId);
    		}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
