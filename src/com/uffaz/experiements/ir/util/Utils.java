package com.uffaz.experiements.ir.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.List;

public class Utils {
	
	private static String LOG_TAG = "<<Utils.java>>";
	
	public static int[] toArray(List<Integer> list) {
		int[] arr = new int[list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}
	
	public static byte[] intToByteArray(int value) {
		return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value
	    };
	}
	
	public static void safeClose(BufferedReader r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				Log.error(LOG_TAG, "safeClose(BufferedReader) error. close()", e);
			}
		}
	}
	
	public static void safeClose(FileReader r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				Log.error(LOG_TAG, "safeClose(FileReader) error. close()", e);
			}
		}
	}
	
	public static void safeClose(FileWriter r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				Log.error(LOG_TAG, "safeClose(FileWriter) error. close()", e);
			}
		}
	}

	public static void safeClose(RandomAccessFile r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				Log.error(LOG_TAG, "safeClose(RandomAccessFile) error. close()", e);
			}
		}
	}
	
	public static void safeClose(FileChannel r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				Log.error(LOG_TAG, "safeClose(FileChannel) error. close()", e);
			}
		}
	}
	
}