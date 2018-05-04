import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.*;
import java.util.concurrent.*;

public class ItemsFileCreator {

    public static void main(String[] args) throws SQLException, InterruptedException {
    	RsPriceManager priceManager = new RsPriceManager();
    	long minDelayBetweenApiCall = 5;	// 4 seconds get roughly 1k API calls before limit; 5 seconds get roughly 7k before limit
    	int start = 57532;
    	int end = 100000;

    	try {
    		/*
    		for (int i = start; i < end; i++) {
    			priceManager.queryItemAndAddToDatabase(i);
    			
    			TimeUnit.SECONDS.sleep(minDelayBetweenApiCall);
    		}*/
    		priceManager.update();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void testRun(RsPriceManager priceManager) throws MalformedURLException, SQLException {
    	int sharkId = 383;
    	int bluePhat = 1042;
    	int steadfastBoots = 21787;
    	int emberkeenBoots = 34978;
    	int thirdAgeAmmy = 10344;
    	int thirdAgeDruidicWreath = 19314;
    	int serenGodBow = 37632;
    	int staffOfSliske = 37636;
    	int hollyWreath = 33628;

    	ArrayList<Integer> testItems = new ArrayList<Integer>();
    	testItems.add(sharkId);
    	testItems.add(bluePhat);
    	testItems.add(steadfastBoots);
    	testItems.add(emberkeenBoots);
    	testItems.add(thirdAgeAmmy);
    	testItems.add(thirdAgeDruidicWreath);
    	testItems.add(serenGodBow);
    	testItems.add(staffOfSliske);
    	testItems.add(hollyWreath);
    	
    	for (int item : testItems) {
    		priceManager.queryItem(item);
    	}
    }
}
