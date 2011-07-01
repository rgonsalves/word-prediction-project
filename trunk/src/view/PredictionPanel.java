package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.io.*;

public class PredictionPanel extends JFrame implements ActionListener{
	private TextArea textArea = new TextArea("", 0,0, TextArea.SCROLLBARS_VERTICAL_ONLY);
	public TextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(TextArea textArea) {
		this.textArea = textArea;
	}
	private JPanel predictionPanel = new JPanel();
	private JPanel mPanel = new JPanel();
	private MenuBar menuBar = new MenuBar(); // first, create a MenuBar item
	private Menu file = new Menu(); // our File menu
	// what's going in File? let's see...
	private MenuItem openFile = new MenuItem();  // an open option
	private MenuItem saveFile = new MenuItem(); // a save option
	private MenuItem close = new MenuItem(); // and a close option!
	private  JLabel warning;
	
	public static PredictionPanel mainPanel;
	public static PredictionPanel getMainPanel(){
		if(mainPanel == null)
			mainPanel = new PredictionPanel();
		mainPanel.setVisible(true);
		return mainPanel;
	}
	
	private PredictionPanel() {
		this.setSize(700, 450); // set the initial size of the window
		this.setTitle("Word Prediction"); // set the title of the window
		setDefaultCloseOperation(EXIT_ON_CLOSE); // set the default close operation (exit when it gets closed)
		this.textArea.setFont(new Font("Century Gothic", Font.BOLD, 12)); // set a default font for the TextArea
		this.textArea.setColumns(20);
		this.textArea.setRows(5);
		
		this.setMinimumSize(new Dimension(1000, 450));
		predictionPanel.setBackground(new java.awt.Color(204, 204, 204));
		predictionPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
//		 javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(predictionPanel);
//	        predictionPanel.setLayout(jPanel2Layout);
//	        jPanel2Layout.setHorizontalGroup(
//	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//	            .addGap(0, 673, Short.MAX_VALUE)
//	        );
//	        jPanel2Layout.setVerticalGroup(
//	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//	            .addGap(0, 105, Short.MAX_VALUE)
//	        );

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(mPanel);
	        mPanel.setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	                    .addComponent(textArea, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
	                    .addComponent(predictionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(textArea, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
	                .addComponent(predictionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(21, 21, 21))
	        );

//	        predictionTable = new JTable();
//	        JScrollPane jScrollPane1 = new JScrollPane();
//	        predictionTable.setModel(new javax.swing.table.DefaultTableModel(
//	            new Object [][] {
//	                {null},
//	                {null},
//	                {null},
//	                {null}
//	            },
//	            new String [] {
//	                "Prediction"
//	            }
//	        ));
//	        jScrollPane1.setViewportView(predictionTable);
//
//	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(predictionPanel);
//	        predictionPanel.setLayout(jPanel1Layout);
//	        jPanel1Layout.setHorizontalGroup(
//	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//	            .addGap(0, 399, Short.MAX_VALUE)
//	            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//	                .addGroup(jPanel1Layout.createSequentialGroup()
//	                    .addContainerGap()
//	                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
//	                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
//	        );
//	        jPanel1Layout.setVerticalGroup(
//	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//	            .addGap(0, 220, Short.MAX_VALUE)
//	            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//	                .addGroup(jPanel1Layout.createSequentialGroup()
//	                    .addContainerGap()
//	                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
//	                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
//	        );


	        
	    warning = new JLabel("messages",JLabel.LEFT);
//	    warning.setOpaque(true);
//	    warning.setVisible(true);
//	    warning.setBackground(Color.red);
	    mPanel.add(textArea);
	    mPanel.add(predictionPanel);
	    predictionPanel.add(warning);
//	    mainPanel.setLayout(new BorderLayout());
	    textArea.addKeyListener(new MyTextListener());
	        pack();

		// this is why we didn't have to worry about the size of the TextArea!
		this.getContentPane().setLayout(new BorderLayout()); // the BorderLayout bit makes it fill it automatically
		this.getContentPane().add(mPanel);
		
		
		// add our menu bar into the GUI
		this.setMenuBar(this.menuBar);
		this.menuBar.add(this.file); // we'll configure this later
		
		// first off, the design of the menuBar itself. Pretty simple, all we need to do
		// is add a couple of menus, which will be populated later on
		this.file.setLabel("File");
		
		// now it's time to work with the menu. I'm only going to add a basic File menu
		// but you could add more!
		
		// now we can start working on the content of the menu~ this gets a little repetitive,
		// so please bare with me!
		
		// time for the repetitive stuff. let's add the "Open" option
		this.openFile.setLabel("Open"); // set the label of the menu item
		this.openFile.addActionListener(this); // add an action listener (so we know when it's been clicked
		this.openFile.setShortcut(new MenuShortcut(KeyEvent.VK_O, false)); // set a keyboard shortcut
		this.file.add(this.openFile); // add it to the "File" menu
		
		// and the save...
		this.saveFile.setLabel("Save");
		this.saveFile.addActionListener(this);
		this.saveFile.setShortcut(new MenuShortcut(KeyEvent.VK_S, false));
		this.file.add(this.saveFile);
		
		// and finally, the close option
		this.close.setLabel("Close");
		// along with our "CTRL+F4" shortcut to close the window, we also have
		// the default closer, as stated at the beginning of this tutorial.
		// this means that we actually have TWO shortcuts to close:
		// 1) the default close operation (example, Alt+F4 on Windows)
		// 2) CTRL+F4, which we are about to define now: (this one will appear in the label)
		this.close.setShortcut(new MenuShortcut(KeyEvent.VK_F4, false));
		this.close.addActionListener(this);
		this.file.add(this.close);
		this.setBackground(Color.red);
	}
	
	public void actionPerformed (ActionEvent e) {
		// if the source of the event was our "close" option
		if (e.getSource() == this.close)
			this.dispose(); // dispose all resources and close the application
		
		// if the source was the "open" option
		else if (e.getSource() == this.openFile) {
			JFileChooser open = new JFileChooser(); // open up a file chooser (a dialog for the user to browse files to open)
			int option = open.showOpenDialog(this); // get the option that the user selected (approve or cancel)
			// NOTE: because we are OPENing a file, we call showOpenDialog~
			// if the user clicked OK, we have "APPROVE_OPTION"
			// so we want to open the file
			if (option == JFileChooser.APPROVE_OPTION) {
				this.textArea.setText(""); // clear the TextArea before applying the file contents
				try {
					// create a scanner to read the file (getSelectedFile().getPath() will get the path to the file)
					Scanner scan = new Scanner(new FileReader(open.getSelectedFile().getPath()));
					while (scan.hasNext()) // while there's still something to read
						this.textArea.append(scan.nextLine() + "\n"); // append the line to the TextArea
				} catch (Exception ex) { // catch any exceptions, and...
					// ...write to the debug console
					System.out.println(ex.getMessage());
				}
			}
		}
		
		// and lastly, if the source of the event was the "save" option
		else if (e.getSource() == this.saveFile) {
			JFileChooser save = new JFileChooser(); // again, open a file chooser
			int option = save.showSaveDialog(this); // similar to the open file, only this time we call
			// showSaveDialog instead of showOpenDialog
			// if the user clicked OK (and not cancel)
			if (option == JFileChooser.APPROVE_OPTION) {
				try {
					// create a buffered writer to write to a file
					BufferedWriter out = new BufferedWriter(new FileWriter(save.getSelectedFile().getPath()));
					out.write(this.textArea.getText()); // write the contents of the TextArea to the file
					out.close(); // close the file stream
				} catch (Exception ex) { // again, catch any exceptions and...
					// ...write to the debug console
					System.out.println(ex.getMessage());
				}
			}
		}
	}
    // the main method, for actually creating our notepad and setting it to visible.
    public static void main(String args[]) {
        PredictionPanel app = getMainPanel();
//        app.setVisible(true);
    }

	public JLabel getWarning() {
		return warning;
	}

	public void setWarning(JLabel warning) {
		this.warning = warning;
	}
}
