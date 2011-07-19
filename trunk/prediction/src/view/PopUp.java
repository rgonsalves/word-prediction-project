package view;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class PopUp extends JFrame{
	private JLabel label;
	  public static void main(String[] args) {
		  PopUp p = new PopUp("apa");
	  }

	  public PopUp(String msg){
		  super("Character pressed");
		  label = new JLabel(msg,JLabel.CENTER);
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 add(label);
		  
		 setSize(400,100);
		 setVisible(true);
	  }

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}


	}
