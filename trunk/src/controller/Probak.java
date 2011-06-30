package controller;

import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.JDBCWrapper;

import view.PredictionPanel;


public class Probak {
	private static JDBCWrapper connection;
	private static HashMap<Character,ArrayList<Word>> words;
	public class Word{
		private int id;
		private String lemma;
		
		public Word(int id, String lemma){
			this.id = id;
			this.lemma = lemma;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getLemma() {
			return lemma;
		}
		public void setLemma(String lemma) {
			this.lemma = lemma;
		}
		
	}
	public Probak(){
		PredictionPanel app = PredictionPanel.getMainPanel();
		try {
			connection = JDBCWrapper.getConnectionInstance();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	 public static void main(String args[]) {
		 Probak tries = new Probak();
		 int id;
		 String lemma;
		 char firstC;
		 Word word;
		 words = new HashMap<Character,ArrayList<Word>>();
			String sql = "select * from words ";
//				log.debug("execute:" + sql);
			// Executing
			try {
				connection.execute(sql);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				while (connection.next()){
					id =connection.getInt("wordid");
					lemma =connection.getString("lemma");
					firstC = lemma.charAt(0);
					word = tries.new Word(id, lemma);
					if(words.containsKey(firstC)){
						words.get(firstC).add(word);
					}
					else{
						ArrayList<Word> arrayList = new ArrayList<Word>();
						arrayList.add(word);
						words.put(new Character(firstC), arrayList);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
//				System.err.println();
//				Iterator<Character> i = words.keySet().iterator();
//				Character c;
//				ArrayList<Word> _words;
//				for(;i.hasNext();){
//					c = i.next();
//					System.err.println("************************************************");
//					System.err.println("letter::"+c);
//					System.err.println("************************************************");
//					_words = words.get(c);
//					for(int j=0;j<_words.size();j++)
//						System.err.print(_words.get(j).getLemma()+"::");
//					System.err.println();
//					System.err.println("************************************************");
//				}
			}
	    }
	public static HashMap<Character, ArrayList<Word>> getWords() {
		return words;
	}
	public static void setWords(HashMap<Character, ArrayList<Word>> words) {
		Probak.words = words;
	}
	 
	 
}
