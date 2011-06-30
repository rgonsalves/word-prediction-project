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

import controller.Probak;

public class MyTextListener implements KeyListener {
			String source;
			boolean first;
			PopUp popup;
			private static java.util.List<Character> separators =  java.util.Arrays.<Character>asList(',',';','.',' ');

			public MyTextListener() {
				first = true;
			}
			private String findWord(char c){
				return Probak.getWords().get(c).get(1).getLemma();
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
				String word;
				if(first){
					first = false;
					word = findWord(c);
					if(popup == null)
						popup = new PopUp(word);
					else{
						popup.getLabel().setText(word);
						popup.setVisible(true);
					}
				}
				
				if(separators.contains(c)){
					
					first =true;
				}
			}
			
}
