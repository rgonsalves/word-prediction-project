package controller;

//import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hsqldb.Server;

import model.GenericWrapper;
import model.JDBCConnection;

import ds.tree.RadixTree;

//import model.JDBCWrapper;

import utilities.MyTrie;
import utilities.WordFrequencyComparator;

public class Main {
	public static final String WORD_SEPARATORS = "[\\s,.;]";
	public static final String WORD_ENDS = "[\\W]";
	// private MyTrie<String> words;//HashMap<Character,ArrayList<String>>
	// private ArrayList<Integer> frequencyArray;
	// private static Integer[] frequencyArry;
	private ArrayList<String> wordArray;
	private static HashMap<String, Integer> wordFrequency;
	private static String[] wordArry;
	private static TreeMap<String, Integer> sortedWords;

	public static void main(String args[]) {
		new Main("jdbc:hsqldb:hsql://localhost/wordsdb").init();
	}

	public Main(String fileString) {		
		org.hsqldb.Server.main(new String[]{"-database.0", fileString, "-dbname.0", "wordsdb"});
	}

	public String init(){
		JDBCConnection connection = new JDBCConnection();
		wordFrequency = new HashMap<String, Integer>();
		int frequency;
		String lemma = "";
		wordArray = new ArrayList<String>();
		String sql = "SELECT * FROM DICTIONARY";
		try {
			connection.execute(sql);
		} catch (Exception e1) {
			e1.printStackTrace();
			return e1.getMessage();
		}
		try {
			while (connection.next()) {
				frequency = connection.getInt("FREQUENCY");
				lemma = connection.getString("WORD");
				this.wordArray.add(lemma);
				// predictor.frequencyArray.add(frequency);
				wordFrequency.put(lemma, frequency);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			// predictor.words = new MyTrie<String>();//new
			// HashMap<Character,ArrayList<String>>//new
			// HashMap<Character,ArrayList<String>>();
			if (!this.wordArray.isEmpty()) {
				wordArry = new String[this.wordArray.size()];
				// frequencyArry = new Integer[predictor.wordArray.size()];
				// predictor.frequencyArray.toArray(frequencyArry);
				this.wordArray.toArray(wordArry);
				WordFrequencyComparator sortingWords = new WordFrequencyComparator(
						wordFrequency);
				sortedWords = new TreeMap<String, Integer>(sortingWords);
				sortedWords.putAll(wordFrequency);
				// System.err.println("Completed database part");
				// end = System.currentTimeMillis();
				// System.err.println("time::" + (end - start));
				// PredictionPanel app = PredictionPanel.getMainPanel();
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
					return e.getMessage();
				}
				return "database loaded";
			}
			//
		}
		return "it should not be here";
	}

	public static String[] getWordArry() {
		return wordArry;
	}

	public static HashMap<String, Integer> getWordFrequency() {
		return wordFrequency;
	}

//	public static TreeMap<String, Integer> getSortedWords() {
//		return sortedWords;
//	}
//
//	public static void setSortedWords(TreeMap<String, Integer> sortedWords) {
//		Main.sortedWords = sortedWords;
//	}
}
