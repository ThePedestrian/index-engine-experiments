package com.uffaz.experiements.ir.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class FileParser {
	
	/**
	 * Read the contents of a given file
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static List<String> readFileContents(String filePath) throws FileNotFoundException, IOException {
		List<String> results = new ArrayList<>();
		
		// Credits: https://stackoverflow.com/a/4716623
		try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line = br.readLine();
		    while (line != null) {
		    	results.add(line);
		        line = br.readLine();
		    }
		}
		return results;
	}
	
	
	/**
	 * Build a mapping of DocId->text
	 * @param fileContents
	 * @return
	 */
	private static Map<Integer, String> parseStringIntoParagraphs(List<String> fileContents) {
		
		Map<Integer, String> map = new HashMap<>();
		int lastParagraphId = -1;
		
		// Iterate through all the lines
		for (String line : fileContents) {
			line = StringUtils.trim(line);
			if (!StringUtils.isWhitespace(line)) {
				String lineLower = line.toLowerCase();
				
				// Line starts with paragraph tag
				if (lineLower.startsWith("<p id")) {
					String id = line.substring(6, line.indexOf(">"));
					lastParagraphId = Integer.parseInt(id);
				}
				// Line ends with paragraph tag
				else if (lineLower.startsWith("</p>")) {
					lastParagraphId = -1;
				}
				// process the text
				else {
					map.put(lastParagraphId, line);
				}
			}
		}
		
		return map;
	}
	
	
	/**
	 * Read the corpus for processing later on
	 * @return
	 */
	public static Map<Integer, String> readCorpus(String filePath) {
		try {
			List<String> fileContents = readFileContents(filePath);
			return parseStringIntoParagraphs(fileContents);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}