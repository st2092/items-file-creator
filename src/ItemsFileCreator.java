import java.net.*;
import java.io.*;

public class ItemsFileCreator {

    public static void main(String[] args) {
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
    	
    	try {
			for (long i = start; i < end; i++ ) {
				priceManager.queryItem(hollyWreath);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
