package com.uffaz.experiements.ir;

import java.util.Map;

import com.uffaz.experiements.ir.index.Index;
import com.uffaz.experiements.ir.parser.FileParser;
import com.uffaz.experiements.ir.util.Log;

public class EntryPoint_CreateIndex {
	
	private static String LOG_TAG = "<<EntryPoint_CreateIndex.java>>";
	
	/**
	 * Read the corpus for processing later on
	 * @return
	 */
	private static Map<Integer, String> readCorpus() {
		return FileParser.readCorpus(Config.CORPUS_FILE_PATH);
	}
	
	
	/**
	 * Build the index. A HashSet if maintained by index of the documents
	 * that are already processed. Thus this inverted file will be built
	 * only once. Rest of the time will be read from the Inverted File.
	 * 
	 * @return
	 */
	private static Index buildIndex() {
		
		Log.info(LOG_TAG, "buildIndex() called.");
		
		Map<Integer, String> corpus = readCorpus();
		Index index = new Index(Config.INDEX_NAME);
		
		int progress = 0;
		
		for (Map.Entry<Integer, String> entry : corpus.entrySet()) {
			int docId 		= entry.getKey();
			String document = entry.getValue();
			index.process(docId, document);
			
			progress++;
			if (progress % 10000 == 0) {
				Log.info(LOG_TAG, "Processed " + progress + "/" + corpus.size() + " -- " + (progress*100.0f/corpus.size()) + "%.");
			}
		}
		
		index.saveIVAndDictFileLocally();
		
		return index;
	}
	
	
	/**
	 * Entry point 
	 * @param args
	 */
	public static void main(String[] args) {
		Index index = buildIndex();
		
		Log.info(LOG_TAG, "Done...");
	}
}