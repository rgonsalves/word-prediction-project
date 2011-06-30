package controller;

import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import model.JDBCWrapper;

import view.PredictionPanel;

class Word{
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
public class Probak {
	private static JDBCWrapper connection;
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
		 ArrayList<Word> words = new ArrayList<Word>();
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
					words.add(new Word(id,lemma));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				System.err.println();
				if(words.size()>0)
					for (Word w : words){
						System.err.print(w.getLemma()+"::");
					}
			}
	    }
	 
	 
}
