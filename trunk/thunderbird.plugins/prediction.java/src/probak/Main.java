package probak;

	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Random;
	import java.util.Set;
	import java.util.TreeSet;

import utilities.MyTrie;
import utilities.Trie;

	import ds.tree.RadixTree;
import ds.tree.RadixTreeImpl;

	public class Main {
		Set<String> valueSet = new TreeSet<String>();
		Trie<String> trie = new MyTrie<String>();
		RadixTree<String> radixTree = new RadixTreeImpl<String>();

		/**
		 * Load up the trie with lots of random strings
		 */
		public void loadTrie() {
			// Load the Trie and a Set
			for (int i = 0; i < 51000; i++) {
				String val = getRandomString();
				valueSet.add(val);
				trie.add(val, val);
			}

			// Load the RadixTree
			String[] strArry = new String[valueSet.size()];
			valueSet.toArray(strArry);
			for (int i = 0; i < strArry.length; i++) {
				radixTree.insert(strArry[i], strArry[i]);
			}
		}

		/**
		 * Test a prefix search
		 */
		public void testSearch() {
			String[] strArry = new String[valueSet.size()];
			valueSet.toArray(strArry);

			// Test Trie Access
			long start = System.currentTimeMillis();
			for(int i = 0; i < 5000; i++) {
				trie.search(strArry[i].substring(0, 3));
			}
			long end = System.currentTimeMillis();
			long duration = end - start;
			System.out.println("Trie Access = " + duration);

			// Test RadixTree Access
			start = System.currentTimeMillis();
			for(int i = 0; i < 5000; i++) {
				radixTree.searchPrefix(strArry[i].substring(0, 3), 10000);
			}
			end = System.currentTimeMillis();
			duration = end - start;
			System.out.println("RadixTrie Access = " + duration);

			// Test Binary Search Access
			start = System.currentTimeMillis();
			for(int i = 0; i < 5000; i++) {
				List<String> list = new ArrayList<String>();
				String prefix = strArry[i].substring(0, 3);
				int pos = Arrays.binarySearch(strArry, prefix);
				if( pos < 0 ) { pos = -pos - 1;}
				while (pos >= 0 && pos < strArry.length) {
					if (strArry[pos].startsWith(prefix)) {
						list.add(strArry[pos]);
					} else {
						break;
					}
					pos++;
				}
			}
			end = System.currentTimeMillis();
			duration = end - start;
			System.out.println("Binary Search Access = " + duration);
		}

		/**
		 * Returns a random string with length between
		 * 5 and 10.
		 */
		private String getRandomString() {
			StringBuilder sb = new StringBuilder();
			int len = getRandomNumber(5, 10);
			for (int i = 0; i < len; i++) {
				sb.append(getRandomChar());
			}

			return sb.toString();
		}

		/**
		 * Returns a random number between start and end
		 */
		private int getRandomNumber(int start, int end) {
			//get the range, casting to long to avoid overflow problems
			long range = (long)end - (long)start + 1;

			// compute a fraction of the range, 0 <= frac < range
			long fraction = (long)(range * new Random().nextDouble());
			return (int)(fraction + start);
		}

		/**
		 * Returns a random letter of the alphabet.
		 */
		private char getRandomChar() {
			String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			int pos = (int)(Math.random()*26);
			return alphabet.charAt(pos);
		}

	    public static void main(String[] args) {
			Main main = new Main();
			main.loadTrie();
			main.testSearch();
	    }
	}

