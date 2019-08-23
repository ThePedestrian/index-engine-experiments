package com.uffaz.experiements.ir;

public class Config {
	
	/**
	 * Name of the saved index
	 */
	public final static String INDEX_NAME = "headlines";
	

	/**
	 * Where the corpus is stored
	 */
	public final static String CORPUS_FILE_PATH = "./corpus/headlines.txt";
	
	
	/**
	 * Where the inverted files should be stored
	 */
	public final static String INVERTED_FILE_DIR = "./inverted_files/";
	
	
	/**
	 * Whether to stem words when using the SimpleTokenizer
	 */
	public final static boolean STEM_WORDS = false;
	
	
	/**
	 * Logging levels
	 */
	public final static boolean LOG_TRACE = false;
	public final static boolean LOG_DEBUG = true;
	public final static boolean LOG_INFO  = true;
	public final static boolean LOG_ERROR = true;
	
}