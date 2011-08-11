package ngrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JList;

import utilities.WordFrequencyComparator;

import controller.AutoCompleter;
import controller.Main;

import model.JDBCWrapper;
import model.NgramsDAO;

public class Algorithm {
	static NgramsDAO ngrams;

	public static void main(String[] args) throws Exception {
		Main.bootDb();
		ngrams = NgramsDAO.getInstance();
		// build_predictions("his job,");
		// recreateDb();
		// String word1 = "his";
		// String receiver = "";
		// predictWords(1, word1, receiver);
		// String lastword = "job";
		// predictWords(1, lastword, receiver);
		// String pattern2 = word1 + lastword;
		// lastword = "as";
		// predictWords(1, lastword, receiver);
		// predictWords(2, pattern2, receiver);
		// String pattern3 = pattern2 + lastword;
		// predictWords(3, pattern3, receiver);
		Main.closeDb();
		JDBCWrapper.getConnectionInstance().close();
	}

	public Algorithm() throws Exception {
		ngrams = NgramsDAO.getInstance();
	}

	// returns the minimum between 2 numbers sent as parameters
//	private static int getMin(int n1, int n2) {
//		if (n1 < n2)
//			return n1;
//		return n2;
//	}

	public boolean findWordsInDictionary(String prefix, JList list) {
		String[] word;
		if (prefix.length() == 0) {
			return false;
		}
		word = findMatches(prefix.toLowerCase(), Main.getWordFrequency());
		list.setListData(word);
		return list.getModel().getSize() > 0;
		
	}

	/***
	 * Finds all the words in the dictionary from a given prefix and creates the
	 * list for the popup
	 * 
	 * @param prefix
	 *            the prefix
	 * @return the words list
	 */
	private String[] findMatches(String prefix, HashMap<String, Integer> wordsAndFreqs) {
		String[] words = Main.getWordArry();
		// Integer[] frequencies = Main.getFrequencyArry();
//		HashMap<String, Integer> wordsAndFreqs = Main.getWordFrequency();
		HashMap<String, Integer> wordsForSort = new HashMap<String, Integer>();
		WordFrequencyComparator sortingWords = new WordFrequencyComparator(
				wordsForSort);
		TreeMap<String, Integer> sortedWords = new TreeMap<String, Integer>(
				sortingWords);

//		ArrayList<String> list = new ArrayList<String>();
		String[] retrieved;
		int count = 1;
		if (words != null && prefix != null) {
			int pos = Arrays.binarySearch(words, prefix);
			if (pos < 0) {
				pos = -pos - 1;
			}
			while (pos >= 0 && pos < words.length
					&& count < AutoCompleter.MAXIMUM_F_KEYS) {
				if (words[pos].startsWith(prefix)) {
					wordsForSort.put(words[pos], wordsAndFreqs.get(words[pos]));
				} else {
					break;
				}
				pos++;
			}
		}
		retrieved = new String[wordsForSort.size()];
		count = 0;
		sortedWords.putAll(wordsForSort);
		for (String word : sortedWords.keySet()) {
			retrieved[count] = "F" + (++count) + "..." + word;
			if (count == wordsForSort.size()
					|| count == AutoCompleter.MAXIMUM_PREDICTIONS)
				break;
		}

		return retrieved;
	}

	public void build_predictions(String text, JList list)
			throws Exception {
		String[] textWords = text.split(" ");
		String receiver = "";
		String pattern = "";
		int backwardWordsNo = textWords.length < Main.NGRAM_NO ? textWords.length : Main.NGRAM_NO;//getMin(words.length, Main.get_Ngram_No());
		for (int i = 1; i <= backwardWordsNo; i++) {
			if (pattern.equals(""))
				pattern = textWords[textWords.length - i];
			else
				pattern = textWords[textWords.length - i] + " " + pattern;
			if (!predictWords(i, pattern, receiver, list))
				break;
//			if (!predictWords(i, pattern.equals("") ? textWords[textWords.length - i]
//					: textWords[textWords.length - i] + " " + pattern, receiver, list))
//				break;
		}
	}


	private boolean predictWords(int n, String pattern, String receiver, JList list) throws Exception {

//		int predicted_words_no = 0;
		Vector<String> Ngrams_1_Words = ngrams.getRowsByPattern(n, pattern);
		System.out.println("(" + pattern + ")");
		if (Ngrams_1_Words == null)
			System.out.println("No predictions for pattern");
		else {
			Vector<String> result = Ngrams_1_Words;
//			predicted_words_no = result.size();
//			String[] words = new String[predicted_words_no];
//			int no = 0;
//			for (String l : Ngrams_1_Words) {
//				words[no] = l;
//				no++;
//				// System.out.println(l);
//
//			}
//			ac.setPopupList(words);
			list.setListData(result);
			return list.getModel().getSize() > 0;
		}
		return false;
	}

	// do we ever use this???
	/*
	 * public void alg_Ngram(String lastWord, String receiver) throws Exception
	 * {
	 * 
	 * LinkedList<String> Ngrams_1_Words = ngrams.getByPattern(1, lastWord); for
	 * (String l : Ngrams_1_Words) System.out.println(l);
	 * 
	 * LinkedList<String> Ngrams_2_Words = ngrams.getByPattern(2, lastWord,
	 * receiver); LinkedList<String> Ngrams_3_Words = ngrams.getByPattern(3,
	 * lastWord, receiver); LinkedList<String> Ngrams_4_Words =
	 * ngrams.getByPattern(4, lastWord, receiver); LinkedList<String>
	 * Ngrams_5_Words = ngrams.getByPattern(5, lastWord, receiver);
	 * 
	 * }
	 */

}
