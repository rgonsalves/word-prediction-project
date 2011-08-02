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

import model.JDBCWrapper;

import ds.tree.RadixTree;

//import model.JDBCWrapper;

import utilities.MyTrie;
import utilities.WordFrequencyComparator;
import view.PredictionPanel;


public class Main {
	private static JDBCWrapper connection;
//	public static final char[] SEPARATORS={'\\s',',','.',';'};
	public static final String WORD_SEPARATORS = "[\\s,.;]";
	public static final String WORD_ENDS = "[\\W]";
//	private MyTrie<String> words;//HashMap<Character,ArrayList<String>> words;
	private ArrayList<String> wordArray;
	private ArrayList<Integer> frequencyArray;
	private static HashMap<String, Integer> wordFrequency;
//	private static Integer[] frequencyArry;
	private static String[] wordArry;
    private static TreeMap<String, Integer> sortedWords;
	
//    public static boolean SPEECH = true;
    public static boolean SPEECH_WORD = true;
    public static boolean SPEECH_TEXT = false;
	public static boolean SPEECH_FULLTEXT = false;
	
	
	public Main(){
//		start = System.currentTimeMillis();
		SpeechRecognition.TextToSpeech.ConvertTextToSpeech("hi mam");
		org.hsqldb.Server.main(new String[]{"-database.0", "file:hsql/words", "-dbname.0", "wordsdb"});
		try {
			connection = JDBCWrapper.getConnectionInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordFrequency = new HashMap<String, Integer>();
		
	}
	 public static void main(String args[]) {
		 Main predictor = new Main();
		 int frequency;
		 String lemma;
		 predictor.wordArray = new ArrayList<String>();

//		 predictor.frequencyArray = new ArrayList<Integer>();
			String sql = "SELECT * FROM DICTIONARY";
			try {
				connection.execute(sql);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				while (connection.next()){
					frequency =connection.getInt("FREQUENCY");
					lemma = connection.getString("WORD");
				
					predictor.wordArray.add(lemma);
//					predictor.frequencyArray.add(frequency);
					wordFrequency.put(lemma, frequency);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
//				predictor.words = new MyTrie<String>();//new HashMap<Character,ArrayList<String>>//new HashMap<Character,ArrayList<String>>();
				if(!predictor.wordArray.isEmpty()){
					wordArry = new String[predictor.wordArray.size()];
//					frequencyArry = new Integer[predictor.wordArray.size()];
//					predictor.frequencyArray.toArray(frequencyArry);
					predictor.wordArray.toArray(wordArry);
					WordFrequencyComparator sortingWords =  new WordFrequencyComparator(wordFrequency);
				    sortedWords = new TreeMap<String, Integer>(sortingWords);
				    sortedWords.putAll(wordFrequency);
//					System.err.println("Completed database part");
//					end = System.currentTimeMillis();
//					System.err.println("time::" + (end - start));
					PredictionPanel app = PredictionPanel.getMainPanel();
				}

			}
	    }

	public static String[] getWordArry() {
		return wordArry;
	}
	public static HashMap<String, Integer> getWordFrequency() {
		return wordFrequency;
	}
//	public static Integer[] getFrequencyArry() {
//		return frequencyArry;
//	}
	public static TreeMap<String, Integer> getSortedWords() {
		return sortedWords;
	}
	public static void setSortedWords(TreeMap<String, Integer> sortedWords) {
		Main.sortedWords = sortedWords;
	}
}
