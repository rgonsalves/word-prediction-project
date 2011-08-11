package controller;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import ngrams.Algorithm;

import utilities.TextToVoice;
//import SpeechRecognition.TextToSpeechController;


// @author Santhosh Kumar T - santhosh@in.fiorano.com 
public class AutoCompleter extends CompletionPopUp {
	public static final int NUMBER_OF_CHARACTERS_CHECKED = 1;
	public static final int MAXIMUM_PREDICTIONS = 8;
	public static final int MAXIMUM_F_KEYS = 13;

	private int wordBegin;
	private int wordEnd;
	private String selectedWord;
	private static final Pattern wordSeparatorPattern = Pattern
			.compile(Main.WORD_SEPARATORS);// this will check if any word
											// termination character, ",.; "
											// exists in the input text
	private static final Pattern wordEndPattern = Pattern
			.compile(Main.WORD_ENDS);// this will find the end of the current
										// word
	// private TextToSpeechController speech;
	protected Algorithm algo;

	public AutoCompleter(JTextComponent comp) throws Exception {
		super(comp);
		algo = new Algorithm();
	}

	/***
	 * This one will find the word where the cursor is, begining from the
	 * nearest separator character. For that the position of the cursor is get,
	 * last
	 * 
	 * @param text
	 *            all the input text
	 * @return the last word
	 */
	private String findCursorWord(String text) {
		int cursorPos = textComp.getCaret().getDot();// cursor position
		if (text.length() < cursorPos) // if the user is removing characters
			cursorPos = text.length();
		Matcher m1 = wordSeparatorPattern.matcher(text.substring(0, cursorPos));
		String prefix = "";
		int prefixEnd = cursorPos;
		wordBegin = 0;
		wordEnd = 1;
		ArrayList<Integer> wordEndPos = new ArrayList<Integer>();
		wordEndPos.add(0);
		while (m1.find()) { // to find the word where the cursor is in
			wordBegin = m1.end();
			wordEndPos.add(wordBegin);
		}
		if (wordBegin == prefixEnd) {// word completed
			int len = wordEndPos.size();
			String word = "";
			if (len >= 2)
				word = text.substring(wordEndPos.get(len - 2),
						wordEndPos.get(len - 1));
			// reaches the end of the word

//			 if(Main.SPEECH_WORD)
//				 utilities.TextToVoice.speech(word);

		}

		Matcher m2 = wordEndPattern.matcher(text.substring(wordBegin,
				text.length()));
		while (m2.find())
			// to find the end of the current word, the word where the cursor is
			// in
			wordEnd = wordBegin + m2.start();
		if (wordEnd < wordBegin)
			wordEnd = text.length();
		prefix = text.substring(wordBegin, prefixEnd).toLowerCase();
		System.err.println("prefix::" + prefix);
		return prefix;
	}

	/****
	 * This procedure will update the data in the popup, filling List list with
	 * the data that's to show in the popup. It will return true if the popup is
	 * shown, false otherwise.
	 */
	protected boolean updateListData() {
		String allText = textComp.getText();
		String word[];
		String lastKey = "";
		if (allText.length() == 0) {
			return false;
		}
		if (allText.length() > 1)
			lastKey = allText.substring(allText.length() - 1, allText.length());

		Matcher m = wordSeparatorPattern.matcher(lastKey);

		String prefix = "";
		if (!m.find()) {// if last character input is not a space
			prefix = findCursorWord(allText);
			return algo.findWordsInDictionary(prefix, list);
		}
		// prediction from ngrams database
		// list.setListData(pretdictedWords());
		else {
			try {
				algo.build_predictions(allText, list);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list.getModel().getSize() > 0;
		}
	}

	public void setPopupList(String[] words) {
		list.setListData(words);
	}

	protected void acceptedListItem(String pressedKey) {
		if (pressedKey == null || pressedKey.length() == 0)
			return;
		String lineStr = pressedKey.replaceAll("F", "");
		int line = Integer.parseInt(lineStr) - 1;
		if (list.getModel().getSize() >= line) {
			String _selected = (String) list.getModel().getElementAt(line);
			selectedWord = _selected.split("\\.\\.\\.")[1];
			try {
				textComp.removeCaretListener(caretListener);
				textComp.getDocument().remove(wordBegin, wordEnd - wordBegin);
				textComp.getDocument().insertString(wordBegin,
						selectedWord/* +" " */, null);
				textComp.addCaretListener(caretListener);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
}
