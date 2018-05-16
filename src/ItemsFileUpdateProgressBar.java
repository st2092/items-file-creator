import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JEditorPane;

public class ItemsFileUpdateProgressBar extends JFrame {

	private JPanel contentPane;
	private int numberOfItems;
	JProgressBar progressBar;
	JEditorPane statusMessageEditorPane;

	/**
	 * Create the frame.
	 */
	public ItemsFileUpdateProgressBar() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 150);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setForeground(new Color(0, 204, 51));
		progressBar.setBounds(10, 64, 244, 14);
		contentPane.add(progressBar);
		
		statusMessageEditorPane = new JEditorPane();
		statusMessageEditorPane.setText("Generating items file. Please wait...");
		statusMessageEditorPane.setEditable(false);
		statusMessageEditorPane.setBounds(10, 11, 244, 42);
		statusMessageEditorPane.setBackground(SystemColor.menu);
		statusMessageEditorPane.setFont(new Font("Tahoma", Font.PLAIN, 13));
		contentPane.add(statusMessageEditorPane);
	}
	
	public ItemsFileUpdateProgressBar(int numOfItems) {
		this();		// call default constructor
		numberOfItems = numOfItems;
		progressBar.setMaximum(numberOfItems);
	}
	
	public void updateProgress(int currentItem) {
		if (currentItem <= numberOfItems) {
			progressBar.setValue(currentItem);
			statusMessageEditorPane.setText("Processing item #" + currentItem + "...");
		}
	}
}
