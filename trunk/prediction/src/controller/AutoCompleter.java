package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import utilities.TextToVoice;
//import SpeechRecognition.TextToSpeechController;

import utilities.WordFrequencyComparator;





// @author Santhosh Kumar T - santhosh@in.fiorano.com 
public class AutoCompleter extends CompletionPopUp{ 
	public static int NUMBER_OF_CHARACTERS_CHECKED = 1;
    public static int MAXIMUM_PREDICTIONS = 8;
    public static int MAXIMUM_F_KEYS = 13;
    
	private int wordBegin;
	private int wordEnd;
	private String selectedWord;
	private static final Pattern wordSeparatorPattern = Pattern.compile(Main.WORD_SEPARATORS2);// this will check if any word termination character, ",.; " exists in the input text
	private static final Pattern wordEndPattern = Pattern.compile(Main.WORD_ENDS);// this will find the end of the current word
//    private TextToSpeechController speech;
	public AutoCompleter(JTextComponent comp){ 
        super(comp);
    } 
 
    /***
     * This one will find the word where the cursor is, begining from the nearest separator character. For that the position of the cursor is get, last
     * @param text all the input text
     * @return the last word
     */
	private String findCursorWord(String text){
    	int cursorPos =textComp.getCaret().getDot();//cursor position
    	if(text.length() < cursorPos) //if the user is removing characters
    		cursorPos = text.length();
    	Matcher m1 = wordSeparatorPattern.matcher(text.substring(0, cursorPos));
    	String prefix = "";
    	int prefixEnd = cursorPos;
    	wordBegin = 0;
    	wordEnd = 1;
    	ArrayList<Integer> wordEndPos = new ArrayList<Integer>();
    	wordEndPos.add(0);
    	while(m1.find()){	// to find the word where the cursor is in
    		 wordBegin = m1.end();
    		 wordEndPos.add(wordBegin);
     	 }
    	if(wordBegin == prefixEnd){// word completed
    		int len = wordEndPos.size();
    		String word = "";
    		if (len >= 2)
    			word = text.substring(wordEndPos.get(len - 2), wordEndPos.get(len - 1));
    		if(Main.SPEECH_WORD)
    			utilities.TextToVoice.speech(word);
    		
    	}
    		
    	 Matcher m2 = wordEndPattern.matcher(text.substring(wordBegin, text.length()));
    	 while(m2.find()) // to find the end of the current word, the word where the cursor is in
    		 wordEnd = wordBegin + m2.start();
    	 if(wordEnd < wordBegin)
    		 wordEnd = text.length();
    	 prefix = text.substring(wordBegin, prefixEnd).toLowerCase();
    	 System.err.println("prefix::" + prefix);
     	return prefix;
    }
    
    /***
     * Finds all the words in the dictionary from a given prefix and creates the list for the popup
     * @param prefix the prefix
     * @return the words list
     */
	private String[] findMatches(String prefix){
	    String [] words = Main.getWordArry();
//	    Integer[] frequencies = Main.getFrequencyArry();
	    HashMap<String, Integer> wordFreq = Main.getWordFrequency();
	    HashMap<String,Integer> wordsForSort = new HashMap<String, Integer>();
	    WordFrequencyComparator sortingWords =  new WordFrequencyComparator(wordsForSort);
        TreeMap<String, Integer> sortedWords = new TreeMap<String, Integer>(sortingWords);
	    

	    ArrayList<String> list = new ArrayList<String>();
		String []retrieved;
		int count = 1;
		if ( words != null && prefix != null){
			int pos = Arrays.binarySearch(words, prefix);
			if( pos < 0 ) { pos = -pos - 1;}
			while (pos >= 0 && pos < words.length && count < MAXIMUM_F_KEYS) {
				if (words[pos].startsWith(prefix)) {
//					list.add("F"+(count++) +"..."+words[pos]);
					wordsForSort.put(words[pos], wordFreq.get(words[pos]));
						// speaks the words 
//					TextToSpeech.ConvertTextToSpeech(words[pos]);
				} else {
					break;
				}
				pos++;
			}
		}
		retrieved = new String[wordsForSort.size()];
		count = 0;
		sortedWords.putAll(wordsForSort);
//		for(int i = 0; (i < sortedWords.size() && i < MAXIMUM_PREDICTIONS) ;i++){
		for(String word: sortedWords.keySet()){
			retrieved[count] = "F" + (++count) + "..."+word;//list.get(i);
			if (count == wordsForSort.size() || count == MAXIMUM_PREDICTIONS)
				break;
		}
	
		
		return retrieved;
	}
	/****
	 * This procedure will update the data in the popup, filling List list with the data that's to show in the popup.
	 * It will return true if the popup is shown, false otherwise.
	 */
    protected boolean updateListData(){ 
        String allText = textComp.getText();
        String word[];
        char lastKey;
        if(allText.length() == 0){
        	return false;
        }

        lastKey = getLastKey();

        Matcher m = wordSeparatorPattern.matcher(""+lastKey);
        
        String prefix = "";
        if(!m.find()){//if last character input is not a space
        	    prefix = findCursorWord(allText);//.substring(0, limit));
		        if(prefix.length() == 0){
		        	return false;
		        }
		        word = findMatches(prefix.toLowerCase());
		        if(word != null && word.length>0){
		        	list.setListData(word);        	
		        	return true;
		        }
        }
	    return false;
    } 
 
    protected void acceptedListItem(String pressedKey){ 
        if(pressedKey==null || pressedKey.length() == 0) 
            return;
        String lineStr = pressedKey.replaceAll("F", "");
        int line = Integer.parseInt(lineStr) -1 ;
        if(list.getModel().getSize() >= line){
        	String _selected = (String) list.getModel().getElementAt(line);
        	selectedWord = _selected.split("\\.\\.\\.")[1];
        	try{
        		textComp.removeCaretListener(caretListener);
        		textComp.getDocument().remove(wordBegin, wordEnd - wordBegin);
        		textComp.getDocument().insertString(wordBegin, selectedWord/* +" "*/, null);
        		textComp.addCaretListener(caretListener);
        	} catch(BadLocationException e){ 
        		e.printStackTrace(); 
        	} 
        }
    } 
}
