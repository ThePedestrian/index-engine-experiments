package com.uffaz.experiements.ir;

import java.util.Arrays;

import com.uffaz.experiements.ir.index.Index;
import com.uffaz.experiements.ir.util.Log;

public class EntryPoint_Test {
	
	private static String LOG_TAG = "<<EntryPoint_Test.java>>";
	
	/**
	 * Get the saved index
	 * @return
	 */
	private static Index getIndex() {
		Log.info(LOG_TAG, "getIndex() called.");
		
		Index index = new Index(Config.INDEX_NAME);
		return index;
	}
	
	
	/**
	 * stats
	 * @param index
	 */
	private static void stats(Index index) {
		System.out.println();
		System.out.println("--- Stats -----------------------");
		System.out.println("Number of documents:            " + index.getNumDocs());
		System.out.println("Size of the vocabulary:         " + index.getNumTerms());
		System.out.println("Total number of terms observed: " + index.getTotalNumberOfTermsObserved());
		System.out.println("---------------------------------");
	}
	
	
	
	/**
	 * Testcase 1
	 * @param index
	 */
	private static void test1(Index index) {
		String[] words = new String[] {
			"deidelberg",
			"plutonium",
			"omarosa",
			"octopus"
		};
		
		System.out.println();
		System.out.println("--- Testcase 1 ------------------");
		
		for (String word : words) {
			System.out.println(word + ":   " + index.getDictionary().get(word));
		}
		System.out.println("---------------------------------");
	}
	
	
	/**
	 * Testcase 2
	 * @param index
	 */
	private static void test2(Index index) {
		String[] words = new String[] {
			"hopkins",
			"harvard",
			"stanford",
			"college"
		};
		
		System.out.println();
		System.out.println("--- Testcase 2 ------------------");
		
		for (String word : words) {
			System.out.println("DocumentFrequency(" + word + "): " + index.getDictionary().get(word).getDocFreq());
		}
		
		System.out.println("---------------------------------");
	}
	
	
	/**
	 * Testcase 3
	 * @param index
	 */
	private static void test3(Index index) {
		int[] docIds = index.search("Jeff Bezos");
		
		System.out.println();
		System.out.println("--- Testcase 3 ------------------");
		
		System.out.println("Jeff Bezos doc ids:");
		System.out.println(Arrays.toString(docIds));
		System.out.println("---------------------------------");
	}
	
	
	/**
	 * Entry point 
	 * @param args
	 */
	public static void main(String[] args) {
		Index index = getIndex();
		
		stats(index);
		test1(index);
		test2(index);
		test3(index);
		
		Log.info(LOG_TAG, "Done...");
	}
}