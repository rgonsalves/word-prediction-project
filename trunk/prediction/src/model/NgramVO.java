package model;






/**
 * @author jdominguez
 * 
 */
public class NgramVO {

	/**
	 * 
	 */



	private int id;
	private String pattern;
	private String word;
	private int frequency;
	private String receiver;
	private String time;
	
	public NgramVO(){
		
	}
	

	/***************************************************************************
	 * Loads the content of the BDD query into the Value Object
	 * 
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	/*
	 * ('', '', '', '', '', '')
	 */
	public static NgramVO getNgramDb(JDBCWrapper connection)
			throws Exception {
		NgramVO ngram = new NgramVO();
		try {
			ngram.id = connection.getInt("ID");
			ngram.pattern = connection.getString("PATTERN");
			ngram.word = connection.getString("WORD");
			ngram.frequency = connection.getInt("FREQUENCY");
			ngram.receiver = connection.getString("RECEIVER");
			ngram.time = connection.getString("TIME");
//			conveyorVO.setModifDate(connection.getDateTimeStamp("PoumConveyorModifDate"));

		} catch (Exception ex) {
			throw ex;
		}
		return ngram;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getPattern() {
		return pattern;
	}


	public void setPattern(String pattern) {
		this.pattern = pattern;
	}


	public String getWord() {
		return word;
	}


	public void setWord(String word) {
		this.word = word;
	}


	public int getFrequency() {
		return frequency;
	}


	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}


	public String getReceiver() {
		return receiver;
	}


	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


}
