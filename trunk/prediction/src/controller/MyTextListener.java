//package controller;
//
//import java.awt.TextComponent;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.awt.event.TextEvent;
//import java.awt.event.TextListener;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//import java.util.TreeSet;
//
//import javax.swing.JFrame;
//
////import utilities.Trie;
////import view.PopUp;
//import view.PredictionPanel;
//
//
//public class MyTextListener implements KeyListener {
//			String word;
//			boolean first;
////			PopUp popup;
////			Trie words;
//		
//			
//
//			public MyTextListener() {
//				first = true;
//				word = "";
//				
//			}
////			private String findWordTrie(String word){
////				String result = "";
////				words = Probak.getWords();
////				List<String> retrieved = words.search(word);
////				for (String ret:retrieved){
////					result += ret +", ";
////				}
////				return result;//(String)retrieved.get(0);
////			}
//			public static String[] findWord(String word){
//			    String [] array = Main.getStrArry();
//			    ArrayList<String> list = new ArrayList<String>();
//				String []retrieved;
//				if (array != null && word != null){
//					int pos = Arrays.binarySearch(array, word);
//					if( pos < 0 ) { pos = -pos - 1;}
//					while (pos >= 0 && pos < array.length) {
//						if (array[pos].startsWith(word)) {
//							list.add(array[pos]);
//						} else {
//							break;
//						}
//						pos++;
//					}
//				}
//				retrieved = new String[list.size()];
//				for(int i = 0; (i < list.size() && i < 8) ;i++){
//					retrieved[i] = list.get(i);
//				}
//				return retrieved;
//			}
//			@Override
//			public void keyPressed(KeyEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			@Override
//			public void keyReleased(KeyEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			@Override
//			public void keyTyped(KeyEvent k) {
//				char c = k.getKeyChar();
//				word += c;
//				String predicted = "";
////				if(first){
//					first = false;
////					predicted = findWord(word);
//					PredictionPanel.getMainPanel().getWarning().setText(predicted);
////					if(popup == null)
////						popup = new PopUp(predicted);
////					else{
////						popup.getLabel().setText(predicted);
////						popup.setVisible(true);
////					}
////				}
////				
////				if(Main.separators.contains(c)){
////					
////					first =true;
////				}
//			}
//			
//}
