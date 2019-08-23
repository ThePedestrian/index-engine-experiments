package com.uffaz.experiements.ir.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.uffaz.experiements.ir.Config;

/**
 * Simple implementation of a tokenizer that
 * 
 * 	1. Lowercases the string
 *  2. Strips away punctation
 *  3. Splits at whitespace
 *  4. Stems the words
 * 
 */
public class SimpleTokenizer implements Tokenizer {
	
	public SimpleTokenizer() { }
	
	@Override
	public String[] tokenize(String str) {
		// Lowercase
		str = str.toLowerCase();
		
		// Strip punctuation
		str = str.replaceAll("'", ""); // remove apostrophe
		// This will not replace digits. See: https://stackoverflow.com/a/18831009
		str = str.replaceAll("\\W", " ");
		
		// Split at whitespace
		String[] splits = str.split("\\s");
		List<String> tokens = new ArrayList<>();
		
		// Stem words
		for (int i = 0; i < splits.length; i++) {
			String word = splits[i];
			
			if (Config.STEM_WORDS) {
				word = stemWord(word);
			}
			
			if (!StringUtils.isWhitespace(word)) { // ignore whitespace
				tokens.add(word);
			}
		}
		
		// Save the tokens
		return toArray(tokens);
	}
	
	
	/**
	 * Converts list to array
	 * @param list
	 * @return
	 */
	private static String[] toArray(List<String> list) {
		String[] arr = new String[list.size()];
		arr = list.toArray(arr);
		return arr;
	}
	
	
	/**
	 * Stem words using the Porter stemmer
	 * @param word
	 * @return
	 */
	private static String stemWord(String word) {
		PorterStemmer stemmer = new PorterStemmer();
		for (int i = 0; i < word.length(); i++) {
			stemmer.add(word.charAt(i));
		}
		stemmer.stem();
		return stemmer.toString();
	}
}