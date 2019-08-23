package com.uffaz.experiements.ir.index;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Used for in-memory lookup
 */
public class PostingsList extends ArrayList<Posting> { // ArrayList has better lookup time get(i) then LinkedList
													   // which has to traverse O(n)

	private static final long serialVersionUID = -2297154267958540384L;
	
	private String term; // Used for pretty strings/debugging
	
	public PostingsList(String term) {
		this.term = term;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[").append(term).append("] ");
		
		for (int i = 0; i < this.size(); i++) {
			Posting posting = this.get(i);
			sb.append("(")
			  .append(posting.getDocId())
			  .append(",")
			  .append(posting.getTermFreq())
			  .append("),");
		}
		return sb.toString();
	}
}