import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.ScrollPaneConstants;


public class SwingTest {

	private JFrame frame;
	JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingTest window = new SwingTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SwingTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 424);
		frame.setTitle("DBMS_P76034193");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		final JEditorPane editorPane = new JEditorPane();
		editorPane.setBounds(42, 37, 393, 76);
		frame.getContentPane().add(editorPane);
		
		JLabel lblNewLabel = new JLabel("SQL Query");
		lblNewLabel.setBounds(43, 21, 62, 15);
		frame.getContentPane().add(lblNewLabel);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(42, 136, 507, 220);
		frame.getContentPane().add(scrollPane);

		scrollPane.setViewportView(new JTable());
		
		JButton btnNewButton = new JButton("Query");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mydbms.Conditions.clear();
				mydbms.tables.clear();
				mydbms.total_attr.clear();
				String line = editorPane.getText();
				try {
					if(line.contains(" IN")){
						mydbms.doNestingQuery(line);
						scrollPane.setViewportView(new JTable(mydbms.rowDataVector(), mydbms.ColumnNamesVector()));
					}else if(mydbms.readQuery(line)){
						mydbms.build_tables();
						mydbms.parseWHERE();
						mydbms.Retrieve();
						mydbms.parseSELECT(mydbms.result);
						scrollPane.setViewportView(new JTable(mydbms.rowDataVector(), mydbms.ColumnNamesVector()));
					}
				}catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		btnNewButton.setBounds(460, 46, 87, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Clear");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editorPane.setText("");
				scrollPane.setViewportView(new JTable());
			}
		});
		btnNewButton_1.setBounds(460, 79, 87, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		JLabel lblNewLabel_1 = new JLabel("Results");
		lblNewLabel_1.setBounds(42, 121, 46, 15);
		frame.getContentPane().add(lblNewLabel_1);
	}
}
