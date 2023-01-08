import java.util.Map;
import java.util.TreeMap;

/**
 * The model class to implement n-gram language model that keeps track
 * of a dictionary of q sequence of n linguistic items along with their
 * count as read from a text document.
 *  
 * @author
 *
 */
public class NGram {

	// count of n sequence items
	private int n;
	// source language of the items
	private String langauge;
	// frequency of all n-gram items  
	private Map<String, Integer> freqMap;
	
	/**
	 * Constructor that creates a new instance of NGram.
	 * 
	 * @param language source language of items
	 * @param n value of n-gram
	 */
	public NGram(String language, int n) {
		this.n = n;
		this.langauge = language;
		freqMap = new TreeMap<String, Integer>();
	}
	
	/**
	 * Extract all n-gram items from given word and update
	 * the frequency distribution. This method is thread-safe for concurrent
	 * access by multiple threads.
	 * 
	 * @param word input word
	 */
	public synchronized void update(String word) {
		int wordLen = word.length();
		for(int i = 0; i <= (wordLen - n); i++) {
			String item = word.substring(i, i+n);
			if(freqMap.containsKey(item)) {
				freqMap.put(item, freqMap.get(item) + 1);
			}
			else {
				freqMap.put(item, 1);
			}
		}
	}
	
	/**
	 * Compute the distance of this language model from other
	 * language model.
	 * 
	 * @param other
	 * @return
	 */
	public synchronized double distanceFrom(NGram other) {
		double dotProduct = 0;
		for(String item : freqMap.keySet()) {
			int freq1 = freqMap.get(item);
			int freq2 = other.getItemFreq(item);
			dotProduct += freq1 * freq2;
		}
		double norm1 = Math.sqrt(this.sumOfFreqSquares());
		double norm2 = Math.sqrt(other.sumOfFreqSquares());
		return dotProduct/(norm1 * norm2);
	}
	
	/**
	 * Get the source language.
	 * 
	 * @return the source language
	 */
	public String getLangauge() {
		return langauge;
	}
	
	/**
	 * Get the value of n of this n-gram
	 * @return the value of n
	 */
	public int getN() {
		return n;
	}
	
	/**
	 * Get the frequency of given item.
	 * @param item given item
	 * @return the frequency of item, zero if item not found
	 */
	public synchronized int getItemFreq(String item) {
		if(freqMap.containsKey(item)) {
			return freqMap.get(item);
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Get the sum of squares of frequency of all items.
	 * @return sum of squares of frequency of items
	 */
	public synchronized int sumOfFreqSquares() {
		int sum = 0;
		for(String item : freqMap.keySet()) {
			int freq = freqMap.get(item);
			sum += freq*freq;
		}
		return sum;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Lang: " + langauge + "\n");
		sb.append("ItemCount: " + freqMap.keySet().size() + "\n");
		sb.append("FreqDist:" + "\n");
		sb.append(freqMap.toString());
		return sb.toString();
	}
	
}
