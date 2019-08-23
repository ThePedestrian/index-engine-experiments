package com.uffaz.experiements.ir.index;

public class Posting implements Comparable<Posting> {
	
	// A posting contains the tuple(docId, termFreq)
	// each of which are 4-bytes each (int in Java is 4 bytes).
	// Thus in total, it takes 8 bytes 
	public static final int POSTING_SIZE_BYTES = 4 + 4;
	
	private int docId;
	private int termFreq;
	
	public Posting(int docId) {
		this(docId, 0);
	}
	
	public Posting(int docId, int termFreq) {
		this.docId = docId;
		this.termFreq = termFreq;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getTermFreq() {
		return termFreq;
	}

	public void setTermFreq(int termFreq) {
		this.termFreq = termFreq;
	}
	
	public void incrementTermFrequency() {
		this.termFreq++;
	}
	
	@Override
	public int compareTo(Posting o) {
		return Integer.compare(this.docId, o.docId);
	}

	@Override
	public String toString() {
		return "Posting [docId=" + docId + ", termFreq=" + termFreq + "]";
	}	
}