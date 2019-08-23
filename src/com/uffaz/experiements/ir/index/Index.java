package com.uffaz.experiements.ir.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.uffaz.experiements.ir.Config;
import com.uffaz.experiements.ir.parser.SimpleTokenizer;
import com.uffaz.experiements.ir.parser.Tokenizer;
import com.uffaz.experiements.ir.util.Log;
import com.uffaz.experiements.ir.util.Utils;

public class Index {
	
	private static String LOG_TAG = "<<Index.java>>";
	
	private String indexName; // The name of this index
	
	private File invertedFile;   // Reference to the inverted file
	private File dictionaryFile; // Reference to the dictionary file
	
	private Tokenizer tokenizer = new SimpleTokenizer(); // Tokenizer to use
	
	private Set<Integer> processedDocIds = new HashSet<>(); // Processed ids
	
	TreeMap<String, DictionaryDetails> dict = new TreeMap<>(); // Sorted dictionary
	
	public Index(String indexName) {
		Log.info(LOG_TAG, "Creating a new instance of Index. indexName=" + indexName);
		
		this.indexName = indexName;
		
		// Create inverted and dictionary files if they don't exist
		createInvertedFileIfNotExist();
		createDictionaryFileIfNotExist();
		
		// Read the files back
		readDictionaryFile();
	}
	
	// ## Public methods #########################################
	
	/**
	 * Get the unique name of the index
	 * @return
	 */
	public String getIndexName() {
		return this.indexName;
	}
	
	
	/**
	 * Get the number of documents in this index
	 * @return
	 */
	public int getNumDocs() {
		return processedDocIds.size();
	}
	
	
	/**
	 * Get the number of terms in this index
	 * @return
	 */
	public int getNumTerms() {
		return dict.keySet().size();
	}
	
	
	/**
	 * Get the size of collection in words
	 * @return
	 */
	public int getTotalNumberOfTermsObserved() {
		int count = 0;
		for (String key : this.dict.keySet()) {
			for (Posting p : this.dict.get(key).getPostingsList()) {
				count += p.getTermFreq();
			}
		}
		return count;
	}

	
	/**
	 * Get the default tokenizer used by the indexer
	 * @return
	 */
	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	
	/**
	 * Use a custom tokenizer for the index
	 * @param tokenizer
	 */
	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	
	/**
	 * Returns the dictionary of this index
	 * @return
	 */
	public TreeMap<String, DictionaryDetails> getDictionary() {
		return this.dict;
	}
	
	
	/**
	 * Search based on a string
	 * @param str The string to search. Will be tokenized automatically.
	 * @return
	 */
	public int[] search(String str) {
		return search(tokenizer.tokenize(str));
	}
	
	
	/**
	 * search based on tokens
	 * @param tokens
	 * @return
	 */
	public int[] search(String[] tokens) {
		List<List<Integer>> lists = new ArrayList<>();
		
		// Get the matching postings list
		for (String token : tokens) {
			DictionaryDetails dd = this.dict.get(token);
			if (dd != null) {
				PostingsList pl = dd.getPostingsList();
				List<Integer> l = new ArrayList<>(pl.size());
				for (Posting p : pl) {
					l.add(p.getDocId());
				}
				lists.add(l);
			}
		}
		
		// Intersect the lists
		if (lists.size() == 0) {
			return new int[]{};
		} else if (lists.size() == 1) {
			return Utils.toArray(lists.get(0));
		} else {
			List<Integer> firstList = lists.get(0);
			for (int i = 1; i < lists.size(); i++) {
				firstList = intersect(firstList, lists.get(i));
			}
			return Utils.toArray(firstList);
		}
	}
	
	
	/**
	 * Process a document
	 * @param docId    The identifier of the document
	 * @param contents The document contents
	 */
	public void process(int docId, String contents) {
		
		// Document is already processed. Return.
		if (processedDocIds.contains(docId)) {
			return;
		}
		
		// Document is processed
		processedDocIds.add(docId);
		
		//Log.info(LOG_TAG, docId + " not processed");
		
		// Tokenize
		String[] tokens = tokenizer.tokenize(contents);
		
		//Log.trace(LOG_TAG, "process() called. docId=" + docId + ", tokens=" + Arrays.toString(tokens));
		
		// Algorithm A
		for (String token : tokens) {
			
			// Get the dictionary details
			DictionaryDetails dd = dict.get(token);
			
			// New term seen. Add to dictionary.
			// Also compute the offset of the new posting list
			if (dd == null) {
				dd = new DictionaryDetails(token);
				//dd.setOffset(0);
				dict.put(token, dd);
			}
			
			// Increment the DocFreq or add the new document
			incrementOrAddToPostingListInMemory(docId, dd);
		}
		
		// Write to file
		//saveIVAndDictFileLocally();
	}
	
	
	// ## Private methods #################################################################
	
	
	/**
	 * Create the inverted file if it doesn't current exist
	 */
	private void createInvertedFileIfNotExist() {
		
		Log.info(LOG_TAG, "createInvertedFileIfNotExist() called");
		
		try {
			final String IV_FILE_NAME = "index-" + indexName + ".iv";
			
			invertedFile = new File(Config.INVERTED_FILE_DIR + IV_FILE_NAME);
			invertedFile.createNewFile();
		} catch (IOException e) {
			Log.error(LOG_TAG, "createInvertedFileIfNotExist()", e);
		}
	}
	
	
	/**
	 * Create the inverted file if it doesn't current exist
	 */
	private void createDictionaryFileIfNotExist() {
		
		Log.info(LOG_TAG, "createDictionaryFileIfNotExist() called");
		
		try {
			final String DICT_FILE_NAME = "index-" + indexName + ".dict";
			
			dictionaryFile = new File(Config.INVERTED_FILE_DIR + DICT_FILE_NAME);
			dictionaryFile.createNewFile();
		} catch (IOException e) {
			Log.error(LOG_TAG, "createDictionaryFileIfNotExist()", e);
		}
	}
	
	/**
	 * Read a postings list from file
	 * @param dd
	 * @return
	 */
	private PostingsList readPostingsListFromFile(DictionaryDetails dd) {
		PostingsList pl = new PostingsList(dd.getTerm());
		
		Log.trace(LOG_TAG, "readPostingsListFromFile() called. dd=" + dd);
			
		RandomAccessFile file = null;
		
		try {
			// Random file
			file = new RandomAccessFile(this.invertedFile, "rw");
			
			// Go to the correct position
			file.seek(dd.getOffset());
			
			// Build list of postings
			for (int i = 0; i < dd.getDocFreq(); i++) {
				int docId = file.readInt();
				int termFreq = file.readInt();
				pl.add(new Posting(docId, termFreq));
			}
			
			file.close();
			
		} catch (IOException e) {
			Log.error(LOG_TAG, "readPostingsListFromFile() error. dd=" + dd, e);
		} finally {
			Utils.safeClose(file);
		}
		
		return pl;
	}
	
	
	/**
	 * Read the contents of the dictionary file back
	 */
	private void readDictionaryFile() {

		Log.info(LOG_TAG, "readDictionaryFile() called.");

		FileReader fileReader = null;
		BufferedReader bufferReader = null;

		try {
			fileReader = new FileReader(this.dictionaryFile);
			bufferReader = new BufferedReader(fileReader);

			String line;
			while ((line = bufferReader.readLine()) != null) {
				DictionaryDetails dd = DictionaryDetails.fromSerializedString(new String(line));
				dd.setPostingsList(readPostingsListFromFile(dd));
				
				//System.out.println(dd);
				
				// Add to processed doc ids
				for (Posting p : dd.getPostingsList()) {
					processedDocIds.add(p.getDocId());
				}
				
				this.dict.put(dd.getTerm(), dd);
			}

		} catch (IOException e) {
			Log.error(LOG_TAG, "readDictionaryFile() error.", e);
		} finally {
			Utils.safeClose(bufferReader);
			Utils.safeClose(fileReader);
		}
		
		Log.info(LOG_TAG, "readDictionaryFile() finished.");
	}
	
	
	/**
	 * Add the item to the postings list in-memory
	 * @param docId The document id
	 * @param dd	The details of the dictionary
	 * @return TRUE when a new docid was inserted. FALSE
	 * 		   when just the count is incremented
	 */
	private boolean incrementOrAddToPostingListInMemory(int docId, DictionaryDetails dd) {
		boolean newItemAddedToPl = false;
		PostingsList pl = dd.getPostingsList();
		
		Posting postingToInsert = new Posting(docId, 1);
		
		boolean wasInserted = false;
		for (int i = 0; i < pl.size(); i++) {
			Posting searchPosting = pl.get(i);
			
			// Increment the count if found
			if (docId == searchPosting.getDocId()) {
				searchPosting.incrementTermFrequency();
				return newItemAddedToPl;
			}
			
			if (docId < searchPosting.getDocId()) {
				pl.add(i, postingToInsert);
				wasInserted = true;
				break;
			}
		}
		
		// It should go at the end, the append it
		if (!wasInserted) {
			pl.add(postingToInsert);
		}
		
		newItemAddedToPl = true;
		
		dd.incrementDocFreq();
		
		return newItemAddedToPl;
	}
	
	
	/**
	 * Save the inverted and dictionary files
	 */
	public void saveIVAndDictFileLocally() {
		
		Log.trace(LOG_TAG, "saveIVAndDictFileLocally() called");
		
		int offset = 0;
		
		// Compute the offsets
		for (String key : this.dict.keySet()) {
			DictionaryDetails dd = this.dict.get(key);
			dd.setOffset(offset);
			offset = offset + dd.getTotalMemoryTakenInBytes();
		}
		
		// Write to file
		writeInvertedFile();
		writeDictionaryFile();
	}
	
	
	/**
	 * Write the dictionary file
	 */
	private void writeDictionaryFile() {
		Log.trace(LOG_TAG, "writeDictionaryFile() called");
		
		// File write
		FileWriter writer = null;
		
		try {
			writer = new FileWriter(this.dictionaryFile, false);
			
			for (String key : this.dict.keySet()) {
				DictionaryDetails dd = this.dict.get(key);
				String str = dd.toSerializableString();
				
				writer.append(str);
				writer.append("\n");
			}
			
			writer.flush();
			
		} catch (IOException e) {
			Log.error(LOG_TAG, "writeInvertedFile() error.", e);
		} finally {
			Utils.safeClose(writer);
		}
	}
	
	
	/**
	 * Write the postings list to the inverted file
	 */
	private void writeInvertedFile() {
		
		Log.trace(LOG_TAG, "writeInvertedFile() called");
		
		// Random file
		RandomAccessFile file = null;
		
		try {
			file = new RandomAccessFile(this.invertedFile, "rw");
			file.seek(0);
			
			for (String key : this.dict.keySet()) {
				DictionaryDetails dd = this.dict.get(key);
				
				// Go to the correct position
				//file.seek(dd.getOffset());
				
				for (Posting posting : dd.getPostingsList()) {
					file.writeInt(posting.getDocId());
					file.writeInt(posting.getTermFreq());
				}
			}
			
		} catch (IOException e) {
			Log.error(LOG_TAG, "writeInvertedFile() error.", e);
		} finally {
			Utils.safeClose(file);
		}
	}
	
	
	/**
	 * Intersect two lists A and B and return shared elements
	 * @param A List 1
	 * @param B List 2
	 * @return New intersected list
	 */
	private static List<Integer> intersect(List<Integer> A, List<Integer> B) {
	    List<Integer> rtnList = new LinkedList<>();
	    for (Integer target : A) {
	        if (B.contains(target)) {
	            rtnList.add(target);
	        }
	    }
	    return rtnList;
	}
}
