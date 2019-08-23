package com.uffaz.experiements.ir.index;

import org.json.JSONObject;

public class DictionaryDetails {
	
	private String term; // Not needed but good for convenience/printing in one shot
	private int docFreq;
	private int offset;
	private PostingsList pl;
	
	public DictionaryDetails(String term) {
		this.term = term;
		this.pl = new PostingsList(term);
	}
	
	public DictionaryDetails(String term, int docFreq, int offset) {
		this(term);
		this.docFreq = docFreq;
		this.offset = offset;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getDocFreq() {
		return docFreq;
	}
	
	public void setDocFreq(int docFreq) {
		this.docFreq = docFreq;
	}
	
	public void incrementDocFreq() {
		this.docFreq++;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public PostingsList getPostingsList() {
		return pl;
	}

	public void setPostingsList(PostingsList pl) {
		this.pl = pl;
	}

	/**
	 * Get the total memory take by the postings list 
	 */
	public int getTotalMemoryTakenInBytes() {
		return (Posting.POSTING_SIZE_BYTES) * docFreq;
	}
	
	/**
	 * Create a string that can be stored to a file
	 * @return
	 */
	public String toSerializableString() {
		JSONObject json = new JSONObject();
		json.put("term", term);
		json.put("df", docFreq);
		json.put("ot", offset);
		return json.toString(); 
	}
	
	/**
	 * Given a serialized string, create DictionaryDetails objejct
	 * @param s
	 * @return
	 */
	public static DictionaryDetails fromSerializedString(String s) {
		JSONObject json = new JSONObject(s);
		DictionaryDetails d = new DictionaryDetails(json.getString("term"));
		d.setDocFreq(json.getInt("df"));
		d.setOffset(json.getInt("ot"));
		return d;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.pl.size(); i++) {
			Posting posting = this.pl.get(i);
			sb.append("(")
			  .append(posting.getDocId())
			  .append(",")
			  .append(posting.getTermFreq())
			  .append("),");
		}
		
		return "DictionaryDetails [term=" + term + ", docFreq=" + docFreq + ", offset=" + offset + ", postingsList=" + sb.toString() + "]";
	}
}