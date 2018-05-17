import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AddItemGui extends JFrame {

	private SoftReference<ItemsFileCreatorGui> mainInterfaceRef; 
	private JPanel contentPane;
	private JTextField searchForItemTextField;

	private JTextField buyPriceTextField;
	private JTextField buyMarginTextField;
	private JTextField sellPriceTextField;
	private JTextField sellMarginTextField;

	JEditorPane itemNameEditorPane;
	private JLabel itemLabel;
	
	private RsPriceManager rsPriceManager;
	private RsDatabase rsDatabase;
	
	private String previousSearchedItemName = "";
	private JEditorPane currentPriceEditorPane;
	
	/**
	 * Create the frame.
	 */
	public AddItemGui() {
		try {
			rsPriceManager = new RsPriceManager();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		rsDatabase = new RsDatabase();
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showItemDetails();
			}
		});
		btnSelect.setBounds(322, 17, 89, 23);
		contentPane.add(btnSelect);
		
		itemLabel = new JLabel("New label");
		itemLabel.setBounds(23, 50, 50, 50);
		contentPane.add(itemLabel);
		
		JTextArea textBuyPrice = new JTextArea();
		textBuyPrice.setForeground(Color.WHITE);
		textBuyPrice.setBackground(Color.DARK_GRAY);
		textBuyPrice.setEditable(false);
		textBuyPrice.setText("Buy Price");
		textBuyPrice.setBounds(10, 111, 85, 22);
		contentPane.add(textBuyPrice);
		
		JTextArea textBuyMargin = new JTextArea();
		textBuyMargin.setForeground(Color.WHITE);
		textBuyMargin.setBackground(Color.DARK_GRAY);
		textBuyMargin.setEditable(false);
		textBuyMargin.setText("Buy Margin");
		textBuyMargin.setBounds(10, 154, 85, 22);
		contentPane.add(textBuyMargin);
		
		JTextArea textSellPrice = new JTextArea();
		textSellPrice.setForeground(Color.WHITE);
		textSellPrice.setBackground(Color.DARK_GRAY);
		textSellPrice.setEditable(false);
		textSellPrice.setText("Sell Price");
		textSellPrice.setBounds(223, 111, 85, 22);
		contentPane.add(textSellPrice);
		
		JTextArea textSellMargin = new JTextArea();
		textSellMargin.setForeground(Color.WHITE);
		textSellMargin.setBackground(Color.DARK_GRAY);
		textSellMargin.setEditable(false);
		textSellMargin.setText("Sell Margin");
		textSellMargin.setBounds(223, 154, 90, 22);
		contentPane.add(textSellMargin);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCurrentItem();
			}
		});
		btnAdd.setBounds(96, 210, 100, 30);
		contentPane.add(btnAdd);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setBounds(223, 210, 100, 30);
		contentPane.add(btnCancel);
		
		searchForItemTextField = new JTextField();
		searchForItemTextField.setToolTipText("Type item name");
		searchForItemTextField.setText("Type item name");
		searchForItemTextField.setBackground(Color.LIGHT_GRAY);
		searchForItemTextField.setBounds(31, 17, 292, 22);
		contentPane.add(searchForItemTextField);
		searchForItemTextField.setColumns(10);
		
		AutoSuggestor autoSuggestor = new AutoSuggestor(searchForItemTextField, this, null, Color.WHITE.brighter(), Color.DARK_GRAY, Color.RED, 0.80f) {
			@Override
			boolean wordTyped(String typedWord) {
				try {
					setDictionary(rsDatabase.getAllItemNames());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return  super.wordTyped(typedWord);
			}
		};
		
		buyPriceTextField = new JTextField();
		buyPriceTextField.setToolTipText("Enter buy price");
		buyPriceTextField.setBackground(Color.LIGHT_GRAY);
		buyPriceTextField.setText("e.g. 1821");
		buyPriceTextField.setBounds(96, 111, 106, 22);
		contentPane.add(buyPriceTextField);
		buyPriceTextField.setColumns(10);
		
		buyMarginTextField = new JTextField();
		buyMarginTextField.setToolTipText("Enter buy margin");
		buyMarginTextField.setBackground(Color.LIGHT_GRAY);
		buyMarginTextField.setText("e.g. 100");
		buyMarginTextField.setBounds(96, 154, 106, 22);
		contentPane.add(buyMarginTextField);
		buyMarginTextField.setColumns(10);
		
		sellPriceTextField = new JTextField();
		sellPriceTextField.setToolTipText("Enter sell price");
		sellPriceTextField.setBackground(Color.LIGHT_GRAY);
		sellPriceTextField.setText("e.g. 2018");
		sellPriceTextField.setBounds(318, 111, 106, 22);
		contentPane.add(sellPriceTextField);
		sellPriceTextField.setColumns(10);
		
		sellMarginTextField = new JTextField();
		sellMarginTextField.setToolTipText("Enter sell margin");
		sellMarginTextField.setText("e.g. 100");
		sellMarginTextField.setBackground(Color.LIGHT_GRAY);
		sellMarginTextField.setBounds(318, 154, 106, 22);
		contentPane.add(sellMarginTextField);
		sellMarginTextField.setColumns(10);
		
		itemNameEditorPane = new JEditorPane();
		itemNameEditorPane.setFont(new Font("Monospaced", Font.BOLD, 13));
		itemNameEditorPane.setBackground(Color.DARK_GRAY);
		itemNameEditorPane.setEditable(false);
		itemNameEditorPane.setForeground(Color.WHITE);
		itemNameEditorPane.setText("Item Name");
		itemNameEditorPane.setBounds(96, 50, 200, 22);
		contentPane.add(itemNameEditorPane);
		
		currentPriceEditorPane = new JEditorPane();
		currentPriceEditorPane.setText("Price: N/A");
		currentPriceEditorPane.setForeground(Color.WHITE);
		currentPriceEditorPane.setFont(new Font("Monospaced", Font.BOLD, 13));
		currentPriceEditorPane.setEditable(false);
		currentPriceEditorPane.setBackground(Color.DARK_GRAY);
		currentPriceEditorPane.setBounds(96, 73, 200, 22);
		contentPane.add(currentPriceEditorPane);
	}
	
	AddItemGui(ItemsFileCreatorGui mainInterface) {
		this();	// call default constructor
		mainInterfaceRef = new SoftReference(mainInterface);
	}
	
	/**
	 * Shows the detail of the item, such as the current price, icon and name.
	 */
	private void showItemDetails() {
		String itemName = searchForItemTextField.getText().trim();
		if (previousSearchedItemName.equals(itemName)) {
			return;
		}
		try {
			if (rsDatabase.containsItem(itemName)) {
				itemNameEditorPane.setText(itemName);
				loadItemIconAndPrice(itemName);
				previousSearchedItemName = itemName;
			} else {
				print( itemName + " not found!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the item current price and icon into the GUI.
	 * @param itemName	the name of the item
	 */
	private void loadItemIconAndPrice(String itemName) {
		try {
			if (!rsDatabase.containsItem(itemName)) {
				return;
			}
			
			Item item = rsPriceManager.getInfoOnItem(itemName);
			if (item != null) {
				String iconUrl = item.getIconUrl();
				URL url = new URL(iconUrl);
				Image icon = ImageIO.read(url);
				itemLabel.setIcon(new ImageIcon(icon));
				
				long currentPrice = item.getPrice();
				currentPriceEditorPane.setText("" + currentPrice);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the item icon into the GUI.
	 * @param itemName	the name of the item
	 */
	private void loadItemIcon(String itemName) {
		try {
			if (!rsDatabase.containsItem(itemName)) {
				return;
			}
			
			Item item = rsPriceManager.getInfoOnItem(itemName);
			if (item != null) {
				String iconUrl = item.getIconUrl();
				URL url = new URL(iconUrl);
				Image icon = ImageIO.read(url);
				itemLabel.setIcon(new ImageIcon(icon));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Add the current item as an entry in the items json file.
	 */
	private void addCurrentItem() {
		if (!checkValidEntries()) {
			JOptionPane.showMessageDialog(null, "One or more prices have not been set. Please check the prices entered.");
			return;
		}
		String itemName = searchForItemTextField.getText().trim();
		try {
			if (!rsDatabase.containsItem(itemName)) {
				return;
			}
			Item currentItem = rsPriceManager.getInfoOnItem(itemName);
			if (currentItem != null) {
				currentItem.setBuyPrice(Integer.valueOf(buyPriceTextField.getText())); 
				currentItem.setBuyMargin(Integer.valueOf(buyMarginTextField.getText()));
				currentItem.setSellPrice(Integer.valueOf(sellPriceTextField.getText()));
				currentItem.setSellMargin(Integer.valueOf(sellMarginTextField.getText()));
				
				
				if (mainInterfaceRef.get() != null) {
					mainInterfaceRef.get().addNewRowToTable(currentItem);
				}
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean checkValidEntries() {
		return checkValidItemName()
			&& checkValidBuyPrice()
			&& checkValidBuyMargin()
			&& checkValidSellPrice()
			&& checkValidSellMargin();
	}
	
	private boolean checkValidItemName() {
		String defaultName = "Item Name";
		return !itemNameEditorPane.getText().trim().equals(defaultName);
	}
	
	private boolean checkValidBuyPrice() {
		try {
			Integer.parseInt(buyPriceTextField.getText().trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean checkValidBuyMargin() {
		try {
			Integer.parseInt(buyMarginTextField.getText().trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean checkValidSellMargin() {
		try {
			Integer.parseInt(sellPriceTextField.getText().trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean checkValidSellPrice() {
		try {
			Integer.parseInt(sellMarginTextField.getText().trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private <T> void print(T toPrint) { 
		System.out.println(toPrint);
	}
}
