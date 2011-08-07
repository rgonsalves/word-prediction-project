package ngrams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import controller.Main;

import model.JDBCWrapper;
import model.NgramsDAO;


public class Ngrams {
	private JDBCWrapper connection;
	public static void main(String[] args) throws Exception {
		ngrams.Ngrams _ngrams = new Ngrams();
		NgramsDAO.getInstance().clearNgrams();
		_ngrams.buildNgrams();
	}

	public Ngrams() throws Exception{
		Main.bootDb();
	}
	private String[] cleanWords(String[] words){
		String[] result = new String[words.length];
		for(int i=0;i<words.length;i++){
			result[i] = words[i].replaceAll(Main.WORD_SEPARATORS, "");
			result[i] = result[i].replaceAll("'", "''");
			
		}
		return result;
	}
	
	void buildNgrams() throws Exception {
		String receiver = "";
		String word = "";
		String sql;
		NgramsDAO ngramDao = NgramsDAO.getInstance();
//		ngramDao.fillEmptyNgram();
		String contents = readFileAsString("A Maze Of Death v1.0.txt");
		LinkedList<String[]> l = new LinkedList<String[]>();
		String[] a1 = ngrams(contents, 2);
		String[] a2 = ngrams(contents, 3);
		String[] a3 = ngrams(contents, 4);
		String[] a4 = ngrams(contents, 5);
		l.add(a1);
		l.add(a2);
		l.add(a3);
		l.add(a4);
		int count = 0;
		receiver = cleanWords(new String[]{receiver})[0];
		for (String[] w : l) {
			count++;
			for (int n = 0; n < w.length; n++) {
				String[] pattern = w[n].split(" ");
				String p = w[n].substring(0, w[n].length() - 1
						- pattern[pattern.length - 1].length());
				
				System.out.println(w[n] + " the pattern :" + p + " the word : "
						+ pattern[pattern.length - 1]);
				String[] strWOapostrophes = cleanWords(new String[]{p, pattern[pattern.length - 1]});
				p = strWOapostrophes[0];
				word = strWOapostrophes[1];
				try {
					ngramDao.getNgrams(p, word, receiver, count);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static String readFileAsString(String filePath)
			throws java.io.IOException {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}

	public static String[] ngrams(String s, int len) {
		String[] parts = s.split(" ");
		String[] result = new String[parts.length - len + 1];
		for (int i = 0; i < parts.length - len + 1; i++) {
			StringBuilder sb = new StringBuilder();
			for (int k = 0; k < len; k++) {
				if (k > 0)
					sb.append(' ');
				sb.append(parts[i + k]);
			}
			result[i] = sb.toString();
		}
		return result;
	}

}
