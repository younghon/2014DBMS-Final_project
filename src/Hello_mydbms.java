import java.io.IOException;
import java.security.PublicKey;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.omg.CORBA.PUBLIC_MEMBER;

public class Hello_mydbms {
	private static Text txtInputSqlQuery;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(629, 536);
		shell.setText("Hello mydbms~");
		
		txtInputSqlQuery = new Text(shell, SWT.BORDER);
		txtInputSqlQuery.setBounds(10, 30, 434, 151);
		
		Label lblNewLabel = new Label(shell, SWT.BORDER);
		lblNewLabel.setBounds(10, 208, 581, 265);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mydbms.Conditions.clear();
				mydbms.tables.clear();
				mydbms.total_attr.clear();
				String line = txtInputSqlQuery.getText();
				try {
					if(line.contains(" IN")){
						mydbms.doNestingQuery(line);
						lblNewLabel.setText(mydbms.resultToWindow+"Answer by nested query.");
					}else if(mydbms.readQuery(line)){
						mydbms.build_tables();
						mydbms.parseWHERE();
						mydbms.Retrieve();
						mydbms.parseSELECT(mydbms.result);
						lblNewLabel.setText(mydbms.resultToWindow+"Answer by single query.");
					}
				}catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(470, 97, 106, 33);
		btnNewButton.setText("Query");
		
		
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lblNewLabel.setText("");
				txtInputSqlQuery.setText("");
			}
		});
		btnNewButton_1.setBounds(470, 148, 106, 33);
		btnNewButton_1.setText("Clear");
		
		Label lblInputQuery = new Label(shell, SWT.NONE);
		lblInputQuery.setBounds(10, 9, 70, 15);
		lblInputQuery.setText("SQL query");
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(10, 187, 60, 15);
		lblNewLabel_1.setText("Result");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
