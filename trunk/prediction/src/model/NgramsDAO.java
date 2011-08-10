package model;

import java.util.LinkedList;

import ngrams.Ngrams;

import controller.Main;

public class NgramsDAO {

	static private NgramsDAO instance = null;
	private static JDBCWrapper connection = null;
	public static void main(String[] args) throws Exception{
		ngrams.Ngrams ngrams = new Ngrams();
		NgramsDAO.getInstance().clearNgrams();
		ngrams.buildNgrams();
	}

	/**
	 * Constructor
	 */
	protected NgramsDAO() throws Exception {
		try {
			connection = JDBCWrapper.getConnectionInstance();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * Singleton
	 */
	public static NgramsDAO getInstance() throws Exception {
		if (instance == null) {
			synchronized (NgramsDAO.class) {
				if (instance == null) {
					instance = new NgramsDAO();
				}
			}
		}
		return instance;
	}

	public static String[] cleanWords(String[] words){
		String[] result = new String[words.length];
		for(int i=0;i<words.length;i++){
//			result[i] = words[i].replaceAll(Main.WORD_SEPARATORS, "");
			if(words[i].length() >0)
				result[i] = words[i].replaceAll("'", "''");
			
		}
		return result;
	}
	

	public void clearNgrams() throws Exception {
		for (int i = 1; i < 7; i++)
			clearNgrams(i);
	}

	public void test() throws Exception{
		String sql = "SELECT * FROM NGRAM_1";
		NgramVO ngram = null;
		connection.executeQuery(sql);
		while (connection.next()){
			ngram = NgramVO.getNgramDb(connection);
			System.err.println("id::"+ ngram.getId());
			if(connection.getRowsNumber() > 0){
				ngram = NgramVO.getNgramDb(connection);
				System.err.println("id::"+ ngram.getId());
			}
		}
		Main.closeDb();
	}
	public void clearNgrams(int n) throws Exception {
		String sql = "";
		sql = "DELETE FROM NGRAM_" + n;
		connection.execute(sql);
	}

	public void insertDbLine(int n, String pattern, String word, int frequency,
			String receiver) throws Exception {
		String sql = " INSERT INTO NGRAM_" + n
				+ " (PATTERN, WORD, FREQUENCY, RECEIVER) VALUES ('" + pattern
				+ "' ," + "'" + word + "', " + frequency + ", '" + receiver
				+ "' )";
		connection.execute(sql);
	}

	private boolean selectPatternAndWords(int n, String pattern, String word,
			String receiver) throws Exception {
		String sql = " SELECT * FROM NGRAM_" + n + " WHERE PATTERN ='"
				+ pattern + "'" + " AND WORD ='" + word + "'";
		connection.executeQuery(sql);
		return connection.getRowsNumber() > 0;
	}
	public LinkedList<String> getByPattern(int n, String pattern) throws Exception {
		String sql = " SELECT * FROM NGRAM_" + n + " WHERE PATTERN ='"
				+ pattern + "'";
		connection.executeQuery(sql);
		NgramVO ngram =null;
		LinkedList<String> list = new LinkedList<String>();
		if(connection.getRowsNumber() > 0){
			while(connection.next()){
				ngram = NgramVO.getNgramDb(connection);
				list.add(ngram.getWord());
			}
			return list;
		}
		return null;
	}
	
	

	private void updateDbFrequency(int n, int frequency, int id) throws Exception {
		String sql = " UPDATE NGRAM_" + n + " SET FREQUENCY=" + (frequency) + " WHERE ID ="
				+ id;
		connection.execute(sql);
	}

	public void getNgrams(String pattern, String word, String receiver, int n)
			throws Exception {
		NgramVO ngram = new NgramVO();
		if(selectPatternAndWords(n, pattern, word, receiver)){
			ngram = NgramVO.getNgramDb(connection);
			if (!ngram.getPattern().equals(pattern) || !ngram.getWord().equals(word)
					|| !ngram.getReceiver().equals(receiver)) {
				insertDbLine(n, pattern, word, 1, receiver);
			} else {
				if (ngram.getPattern().equals(pattern) && ngram.getWord().equals(word)
						&& ngram.getReceiver().equals(receiver)) {
					updateDbFrequency(n, ngram.getFrequency() + 1, ngram.getId());
				}
			}
		} else insertDbLine(n, pattern, word, 1, receiver);

	}
}
