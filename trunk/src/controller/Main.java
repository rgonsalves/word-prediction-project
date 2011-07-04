package controller;

import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ds.tree.RadixTree;

import model.JDBCWrapper;

import utilities.MyTrie;
import view.PredictionPanel;


public class Main {
	private static JDBCWrapper connection;
	public static final String WORD_ENDS = "[\\s,.;]";
	private MyTrie<String> words;//HashMap<Character,ArrayList<String>> words;
	private Set<String> valueSet;
	private static String[] strArry;
//	static long start,end;
	
	
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
	public Main(){
//		start = System.currentTimeMillis();
		try {
			connection = JDBCWrapper.getConnectionInstance();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	 public static void main(String args[]) {
		 Main proba = new Main();
		 int id;
		 String lemma;
		 char firstC;
		 proba.valueSet = new TreeSet<String>();
//		 Word word;
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
//					firstC = lemma.charAt(0);
//					word = tries.new Word(id, lemma);
//					if(words.containsKey(firstC)){
//						words.get(firstC).add(lemma);
//					}
//					else{
//						ArrayList<String> arrayList = new ArrayList<String>();
//						arrayList.add(lemma);
//						words.put(new Character(firstC), arrayList);
//					}
//					words.add(lemma, lemma);
					proba.valueSet.add(lemma);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				proba.words = new MyTrie<String>();//new HashMap<Character,ArrayList<String>>//new HashMap<Character,ArrayList<String>>();
				if(!proba.valueSet.isEmpty()){
					strArry = new String[proba.valueSet.size()];
					proba.valueSet.toArray(strArry);
//					System.err.println("Completed database part");
//					end = System.currentTimeMillis();
//					System.err.println("time::" + (end - start));
					PredictionPanel app = PredictionPanel.getMainPanel();
				}

			}
	    }

	public static String[] getStrArry() {
		return strArry;
	}
	public static void setStrArry(String[] _strArry) {
		strArry = _strArry;
	}
	 
	 
}
