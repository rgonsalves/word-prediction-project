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
	public static final int NUMBER_OF_CHARACTERS_CHECKED = 1; 
	private int wordBegin;
	private int wordEnd;
	private int posX;
	private static final Pattern p = Pattern.compile(Main.WORD_ENDS);// this will check if any word terminatio character, ",.; " exists in the input text
    public AutoCompleter(JTextComponent comp){ 
        super(comp);
    } 
 
    /***
     * This one will find the word where the cursor is, begining from the nearest separator character. For that the position of the cursor is get, last
     * @param text all the input text
     * @return the last word
     */
    private String findCursorWord(String text){
    	posX =textComp.getCaret().getDot();
    	Matcher m = p.matcher(text.substring(0, posX));
    	Pattern _p = Pattern.compile("\\b"); //word boundary matching
    	wordBegin = 0;
    	 while(m.find()){
    		 wordBegin = m.end();
    	 }
    	String word = text.substring(wordBegin, posX + 1).toLowerCase();
//    	m = _p.matcher(text.substring(posX));
//    	while (m.find())
//    		wordEnd = posX + m.end();
     	return word;
    }
    
    /***
     * Finds all the words in the dictionary from a given prefix
     * @param prefix the prefix
     * @return the words list
     */
	private String[] findWords(String prefix){
	    String [] array = Main.getStrArry();
	    ArrayList<String> list = new ArrayList<String>();
		String []retrieved;
		if (array != null && prefix != null){
			int pos = Arrays.binarySearch(array, prefix);
			if( pos < 0 ) { pos = -pos - 1;}
			while (pos >= 0 && pos < array.length) {
				if (array[pos].startsWith(prefix)) {
					list.add(array[pos]);
				} else {
					break;
				}
				pos++;
			}
		}
		retrieved = new String[list.size()];
		for(int i = 0; (i < list.size() && i < 8) ;i++){
			retrieved[i] = list.get(i);
		}
		return retrieved;
	}
	
    protected boolean updateListData(){ 
        String value = textComp.getText();
       
        String word[];
        if(value == ""){
        	System.err.println("value length eq 0");
        	return false;
        }
      
        String lastKey = value.substring(value.length() - NUMBER_OF_CHARACTERS_CHECKED);
        Matcher m = p.matcher(lastKey);
        String prefix = "";
        if(!m.find()){//if last character input is not a space
        	    prefix = findCursorWord(value);
		        if(prefix.length() == 0){
		        	System.err.println("prefix length eq 0");
		        	return false;
		        }
		        word = findWords(prefix);
		        if(word != null && word.length>0){
		        	list.setListData(word);        	
		        	return true;
		        }
        }
	    return false;
    } 
 
    protected void acceptedListItem(String selected){ 
        if(selected==null) 
            return;
        
        try{
//        	textComp.getDocument().remove(posX, wordEnd);
            textComp.getDocument().insertString(textComp.getCaretPosition(), selected, null); 
        } catch(BadLocationException e){ 
            e.printStackTrace(); 
        } 
    } 
}