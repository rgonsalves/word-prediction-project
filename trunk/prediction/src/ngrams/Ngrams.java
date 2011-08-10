package ngrams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import controller.Main;

import model.JDBCWrapper;
import model.NgramVO;
import model.NgramsDAO;

public class Ngrams {
	private JDBCWrapper connection;

	public static void main(String[] args) throws Exception {
		Main.bootDb();
		ngrams.Ngrams ngrams = new Ngrams();
		NgramsDAO.getInstance().clearNgrams();
		ngrams.buildNgrams();
		Main.closeDb();
	}

	public Ngrams() throws Exception {
		Main.bootDb();
	}

	public static void buildNgrams() throws Exception {
		String receiver = "";
		String word = "";
		String sql;

		// ngramDao.fillEmptyNgram();
		String contents = readFileAsString("text.txt");
		contents = preproc(contents);
		LinkedList<String[]> l = new LinkedList<String[]>();
		String[] a1 = ngrams(contents, 2);
		String[] a2 = ngrams(contents, 3);
		String[] a3 = ngrams(contents, 4);
		String[] a4 = ngrams(contents, 5);
		l.add(a1);
		l.add(a2);
		l.add(a3);
		l.add(a4);
		int n = 1;

		for (String[] words : l) {
			fillTable(words, n);
			n++;
		}
	}

	private static String preproc(String s) {
		if (s.charAt(0) == '\t' || s.charAt(0) == ' ') {
			s = s.substring(1);
		}

		s = s.replaceAll("'.'", ". ");
		s = s.replaceAll(",", ", ");
		s = s.replaceAll("'?'", "? ");
		s = s.replaceAll(";", "; ");
		s = s.replaceAll("!", "! ");
		s = s.replaceAll("\\s\\s+|\\t+", " ");
//		s = s.replaceAll("\\t+", " ");
		
		return s;
	}

	public static void fillTable(String[] words, int tableN) throws Exception {
		String word;
		String receiver = "";
		NgramsDAO ngramDao = NgramsDAO.getInstance();
		for (int n = 0; n < words.length; n++) {
			String[] patterns = words[n].split(" ");
			if (patterns.length > 1) {
				String pattern = words[n].substring(0, words[n].length() - 1
						- patterns[patterns.length - 1].length());
				String[] strWOapostrophes = NgramsDAO.cleanWords(new String[] {
						pattern, patterns[patterns.length - 1] });
				pattern = strWOapostrophes[0];
				word = strWOapostrophes[1];
				try {
					ngramDao.getNgrams(pattern, word, receiver, tableN);
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
