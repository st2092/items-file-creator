import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class DatabaseUpdateProgressBar extends JFrame {

	private JPanel contentPane;
	private JEditorPane statusMessageEditorPane;
	private JProgressBar progressBar;
	private String defaultStatusText = "Updating database in progress...";
	

	/**
	 * Create the frame.
	 */
	public DatabaseUpdateProgressBar() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 150);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		statusMessageEditorPane = new JEditorPane();
		statusMessageEditorPane.setEditable(false);
		statusMessageEditorPane.setText("Updating database in progress...");
		statusMessageEditorPane.setFont(new Font("Tahoma", Font.PLAIN, 13));
		statusMessageEditorPane.setBackground(SystemColor.menu);
		statusMessageEditorPane.setBounds(10, 11, 244, 42);
		contentPane.add(statusMessageEditorPane);
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setForeground(new Color(0, 204, 51));
		progressBar.setBounds(10, 64, 244, 14);
		contentPane.add(progressBar);
	}
	
	public void setStatusText(String status) {
		if (status != null) {
			statusMessageEditorPane.setText(status);
		}
	}
	
	public String getDefaultStatusText() {
		return defaultStatusText;
	}
	
	public void complete() {
		dispose();
	}
}
