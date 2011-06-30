package view;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

public class MyTextListener implements KeyListener {
			String source;
			boolean separator;
			PopUp popup;
			private static java.util.List<Character> separators =  java.util.Arrays.<Character>asList(',',';','.',' ');

			public MyTextListener() {

			}
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void keyTyped(KeyEvent k) {
				char c = k.getKeyChar();
				if(popup!=null)
					popup.setVisible(false);
				if(separators.contains(c))
					if(popup == null)
						popup = new PopUp("Separator!");
					else{
						popup.getLabel().setText("Separator!");
						popup.setVisible(true);
					}
			}
			public boolean isSeparator() {
				return separator;
			}
			public void setSeparator(boolean separator) {
				this.separator = separator;
			}
}
