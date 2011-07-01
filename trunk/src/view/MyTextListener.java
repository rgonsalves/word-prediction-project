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

import utilities.Trie;

import controller.Probak;

public class MyTextListener implements KeyListener {
			String word;
			boolean first;
			PopUp popup;
			Trie words;
			private static java.util.List<Character> separators =  java.util.Arrays.<Character>asList(',',';','.',' ');

			public MyTextListener() {
				first = true;
				word = "";
			}
			private String findWord(String word){
				String result = "";
				words = Probak.getWords();
				List<String> retrieved = words.search(word);
				for (String ret:retrieved){
					result += ret +", ";
				}
				return result;//(String)retrieved.get(0);
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
				word += c;
				String predicted = "";
//				if(first){
					first = false;
					predicted = findWord(word);
					PredictionPanel.getMainPanel().getWarning().setText(predicted);
//					if(popup == null)
//						popup = new PopUp(predicted);
//					else{
//						popup.getLabel().setText(predicted);
//						popup.setVisible(true);
//					}
//				}
				
				if(separators.contains(c)){
					
					first =true;
				}
			}
			
}
