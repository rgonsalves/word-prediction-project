package ngrams;

import java.util.LinkedList;

import controller.Main;

import model.NgramsDAO;

public class Algo {
	static NgramsDAO ngrams;
	public static void main(String[] args)throws Exception{
//		Main.bootDb();
		new Ngrams().buildNgrams();
		ngrams =NgramsDAO.getInstance();
		String word1="his";
		String receiver ="";
		predictWords(1,word1, receiver);
		String lastword= "job";
		predictWords(1,lastword, receiver);
		String pattern2 = word1+lastword;
		lastword="as";
		predictWords(1,lastword, receiver);
		predictWords(2,pattern2, receiver);
		String pattern3 = pattern2+lastword;
		predictWords(3,pattern3, receiver);
		
	}
	
private static void predictWords(int n, String pattern, String receiver) throws Exception{
	
	LinkedList<String> Ngrams_1_Words = ngrams.getByPattern(n, pattern, receiver);
	 for(String l : Ngrams_1_Words)
		 System.out.println(l);
}
public void alg_Ngram(String lastWord, String receiver) throws Exception{
	
	
	 LinkedList<String> Ngrams_1_Words = ngrams.getByPattern(1, lastWord, receiver);
	 for(String l : Ngrams_1_Words)
	 System.out.println(l);
			 
	 LinkedList<String> Ngrams_2_Words = ngrams.getByPattern(2, lastWord, receiver);
	 LinkedList<String> Ngrams_3_Words = ngrams.getByPattern(3, lastWord, receiver);
	 LinkedList<String> Ngrams_4_Words = ngrams.getByPattern(4, lastWord, receiver);
	 LinkedList<String> Ngrams_5_Words = ngrams.getByPattern(5, lastWord, receiver);
	 
}
}
