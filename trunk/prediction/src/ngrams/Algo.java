package ngrams;

import java.util.LinkedList;

import controller.Main;

import model.NgramsDAO;

public class Algo {
	static NgramsDAO ngrams;
	public static void main(String[] args)throws Exception{
		Main.bootDb();
//		new Ngrams().buildNgrams();
		ngrams =NgramsDAO.getInstance();
		String word1="his";
		predictWords(1,word1);
		String lastword= "job";
		predictWords(1,lastword);
		String pattern2 = word1+lastword;
		lastword="as";
		predictWords(1,lastword);
		predictWords(2,pattern2);
		String pattern3 = pattern2+lastword;
		predictWords(3,pattern3);
		
	}
	
private static void predictWords(int n, String pattern) throws Exception{
	LinkedList<String> Ngrams_1_Words = ngrams.getByPattern(n, pattern);
	 for(String l : Ngrams_1_Words)
		 System.out.println(l);
}
public void alg_Ngram(String lastWord) throws Exception{
	
	
	 LinkedList<String> Ngrams_1_Words = ngrams.getByPattern(1, lastWord);
	 for(String l : Ngrams_1_Words)
	 System.out.println(l);
			 
	 LinkedList<String> Ngrams_2_Words = ngrams.getByPattern(2, lastWord);
	 LinkedList<String> Ngrams_3_Words = ngrams.getByPattern(3, lastWord);
	 LinkedList<String> Ngrams_4_Words = ngrams.getByPattern(4, lastWord);
	 LinkedList<String> Ngrams_5_Words = ngrams.getByPattern(5, lastWord);
	 
}
}
