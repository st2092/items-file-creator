import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.Font;

public class ItemsFileCreatorGui extends JFrame {
	private JTable table;
	private DefaultTableModel tableModel;
	static AddItemGui addItemFrame;
	
	private JButton btnDelete;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ItemsFileCreatorGui frame = new ItemsFileCreatorGui();	
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ItemsFileCreatorGui() {
		setBackground(Color.DARK_GRAY);
		setTitle("Item Files Creator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 420);
		getContentPane().setLayout(null);
		
		String[][] data = {{"N/A", "0", "0", "0", "0"}};
		String[] columns = {"Name","Buy Price","Buy Margin", "Sell Price", "Sell Margin"};
		
		tableModel = new DefaultTableModel(null, columns);
		table = new JTable(tableModel);
		table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		table.setForeground(Color.BLACK);
		table.setEnabled(false);
		table.setRowSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBounds(10, 12, 400, 200);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 0, 584, 248);
		getContentPane().add(scrollPane);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openAddItemGui();
			}
		});
		btnAdd.setBounds(10, 279, 100, 30);
		getContentPane().add(btnAdd);
		
		JButton btnUpdateDatabase = new JButton("Update Database");
		btnUpdateDatabase.setToolTipText("THIS TAKES A LONG TIME.  RUN THIS ONLY FOR FIRST TIME USE OR NEW ITEMS ADDED INTO GAME.");
		btnUpdateDatabase.setEnabled(false);
		btnUpdateDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnUpdateDatabase.setBounds(474, 319, 100, 30);
		getContentPane().add(btnUpdateDatabase);
		
		btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.setForeground(Color.BLACK);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeLastRowInTable();
			}
		});
		btnDelete.setBackground(Color.RED);
		btnDelete.setBounds(120, 279, 100, 30);
		getContentPane().add(btnDelete);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(474, 279, 100, 30);
		getContentPane().add(btnGenerate);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmInstructions = new JMenuItem("Instructions");
		mnHelp.add(mntmInstructions);
	}
	
	public void addNewRowToTable(String name, String buyPrice, String buyMargin, String sellPrice, String sellMargin) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		print(name + " " + buyPrice + " " + buyMargin + " " + sellPrice + " " + sellMargin);
		model.addRow(new Object[] {name, buyPrice, buyMargin, sellPrice, sellMargin});
	}
	
	public void addNewRowToTable(Item item) {
		if (item != null) {
			String name = item.getName();
			String buyPrice = "" + item.getBuyPrice();
			String buyMargin = "" + item.getBuyMargin();
			String sellPrice = "" + item.getSellPrice();
			String sellMargin = "" + item.getSellMargin();
			print(name + " " + buyPrice + " " + buyMargin + " " + sellPrice + " " + sellMargin);
			addNewRowToTable(name, buyPrice, buyMargin, sellPrice, sellMargin);
			checkToEnableDeleteButton();
		}
	}
	
	private void checkToEnableDeleteButton() {
		int rowCount = table.getRowCount();
		if (rowCount >= 1) {
			btnDelete.setEnabled(true);
		}
	}
	
	private void openAddItemGui() {
		addItemFrame = new AddItemGui(this);
		addItemFrame.setVisible(true);
		addItemFrame.setAlwaysOnTop(true);
	}
	
	private void removeLastRowInTable() {
		int rowCount = table.getRowCount();
		if (rowCount >= 1) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.removeRow(rowCount-1);
			
			if (rowCount <= 1) {
				btnDelete.setEnabled(false);
			}
		}
	}
	
	private void removeRowInTable(int rowNumber) {
		int rowCount = table.getRowCount();
		if (rowNumber > rowCount) {
			return;
		}
		
		if (rowCount >= 1) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.removeRow(rowNumber);
			
			if (rowCount <= 1) {
				btnDelete.setEnabled(false);
			}
		}
	}
	
	private <T> void print(T toPrint) {
		System.out.println(toPrint);
	}
}
