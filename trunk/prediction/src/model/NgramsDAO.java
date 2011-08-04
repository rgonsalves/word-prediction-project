package model;


public class NgramsDAO {

	static private NgramsDAO instance = null;
	private static JDBCWrapper connection = null;

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
	
	public void clearNgrams() throws Exception{
		for (int i=1;i<7;i++)
			clearNgrams(i);
	}

	public void clearNgrams(int n) throws Exception{
		String sql ="";
		sql = "DELETE FROM NGRAM_" + n;
		connection.dmlExecute(sql);
	}

	private void insertDbLine(int n, String pattern, String word,
			int frequency, String receiver) throws Exception {
		String sql = " INSERT INTO NGRAM_" + n
				+ " (PATTERN, WORD, FREQUENCY, RECEIVER) VALUES ('" + pattern
				+ "' ," + "'" + word + "', " + frequency + ", '" + receiver
				+ "' )";
		connection.dmlExecute(sql);
	}

	private void checkDbLine(int n, String pattern, String word, int frequency,
			String receiver) throws Exception {
		String sql = " SELECT * FROM NGRAM_" + n + " WHERE PATTERN ='"
				+ pattern + "'" + " AND WORD ='" + word + "'";
		connection.execute(sql);
	}

	private void updateDbFrequency(int frequency, int id) throws Exception{
		String sql = " UPDATE FREQUENCY SET "
				+ (frequency) + " WHERE ID =" + id;
		connection.dmlExecute(sql);
	}
	public void getNgrams(String pattern, String word, String receiver, int n)
			throws Exception {
		NgramVO ngram = new NgramVO();
		int frequency = 0;
		checkDbLine(n, pattern, word, frequency, receiver);

		if (!connection.next())
			insertDbLine(n, pattern, word, frequency, receiver);
		else
			while (connection.next()) {
				ngram = NgramVO.getNgramDb(connection);
				if (ngram.getPattern() == null || ngram.getWord() == null
						|| ngram.getReceiver() == null) {
					insertDbLine(n, pattern, word, 1, receiver);
				} else {
					if (ngram.getPattern() == pattern
							&& ngram.getWord() == word
							&& ngram.getReceiver() == receiver) {
						updateDbFrequency(ngram.getFrequency() + 1, ngram.getId());
					} else if (ngram.getPattern() == pattern
							&& ngram.getWord() == word
							&& ngram.getReceiver() != receiver)
						insertDbLine(n, pattern, word, 1, receiver);
				}
			}
	}
}
