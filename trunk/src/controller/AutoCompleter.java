package controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import controller.Main.Word;


// @author Santhosh Kumar T - santhosh@in.fiorano.com 
public class AutoCompleter extends CompletionPopUp{ 
	public static int NUMBER_OF_CHARACTERS_CHECKED = 1;
    public static int MAXIMUM_PREDICTIONS = 8;
    public static int MAXIMUM_F_KEYS = 13;
    
	private int wordBegin;
	private int wordEnd;
	private String selectedWord;
	private static final Pattern wordSeparatorPattern = Pattern.compile(Main.WORD_SEPARATORS);// this will check if any word termination character, ",.; " exists in the input text
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
    	Matcher m = wordSeparatorPattern.matcher(text.substring(0, cursorPos));
    	String prefix = "";
    	int prefixEnd = cursorPos;
    	wordBegin = 0;
    	wordEnd = 0;
    	while(m.find()){	// to find the word where the cursor is in
    		 wordBegin = m.end();
     	 }
    	 m = wordSeparatorPattern.matcher(text.substring(wordBegin, text.length()));
    	 if(m.find()) // to find the end of the current word, the word where the cursor is in
    		 wordEnd = wordBegin + m.end();
    	 prefix = text.substring(wordBegin, prefixEnd).toLowerCase();
    	 System.err.println("prefix::" + prefix);
     	return prefix;
    }
    
    /***
     * Finds all the words in the dictionary from a given prefix and creates the list for the popup
     * @param prefix the prefix
     * @return the words list
     */
	private String[] findMatchess(String prefix){
	    String [] array = Main.getStrArry();
	    ArrayList<String> list = new ArrayList<String>();
		String []retrieved;
		int count = 1;
		if (array != null && prefix != null){
			int pos = Arrays.binarySearch(array, prefix);
			if( pos < 0 ) { pos = -pos - 1;}
			while (pos >= 0 && pos < array.length && count < MAXIMUM_F_KEYS) {
				if (array[pos].startsWith(prefix)) {
					list.add("F"+(count++) +"..."+array[pos]);
				} else {
					break;
				}
				pos++;
			}
		}
		retrieved = new String[list.size()];
		for(int i = 0; (i < list.size() && i < MAXIMUM_PREDICTIONS) ;i++){
			retrieved[i] = list.get(i);
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
		        word = findMatchess(prefix);
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
        		textComp.getDocument().remove(wordBegin, wordEnd - wordBegin);
        		textComp.getDocument().insertString(wordBegin, selectedWord +" ", null);
        	} catch(BadLocationException e){ 
        		e.printStackTrace(); 
        	} 
        }
    } 
}