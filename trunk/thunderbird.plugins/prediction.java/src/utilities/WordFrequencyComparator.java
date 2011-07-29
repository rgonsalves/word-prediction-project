package utilities;

import java.util.Comparator;
import java.util.Map;


/*****
 * Compares frequencies of words. Its a comparator class
 * Used in a Map with words and its frequencies in the general written use 
 * 
 * @author Aitor
 *
 */
	public class WordFrequencyComparator implements Comparator {

		  Map<String, Integer> base;
		  public WordFrequencyComparator(Map<String, Integer> base) {
		      this.base = base;
		  }

		  public int compare(Object a, Object b) {

		    if((Integer)base.get(a) < (Integer)base.get(b)) {
		      return 1;
		    } else if((Integer)base.get(a) == (Integer)base.get(b)) {
		      return 0;
		    } else {
		      return -1;
		    }
		  }
		}

