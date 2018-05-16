import java.net.MalformedURLException;
import java.sql.SQLException;

public class UpdateDatabaseRunnable  implements Runnable {
	private RsPriceManager rsPriceManager = null;
	
	public void run() {
		if (rsPriceManager != null) {
			try {
				rsPriceManager.update();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setRsPriceManager(RsPriceManager priceManager) {
		rsPriceManager = priceManager;
	}
}
