package controller;

import java.io.ObjectInputStream.GetField;
import java.sql.SQLException;

import model.JDBCWrapper;

import view.PredictionPanel;

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
			String sql = "select * from words ";
//				log.debug("execute:" + sql);
			// Executing
			try {
				Probak.connection.execute(sql);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				while (connection.next()){
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
}
