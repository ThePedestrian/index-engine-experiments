package com.uffaz.experiements.ir.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.uffaz.experiements.ir.Config;

public class Log {
	
	private static SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

	private static String getTimestamp() {
		return sfd.format(new Date());
	}
	
	public static void trace(String TAG, String message) {
		if (Config.LOG_TRACE) {
			log("TRACE", TAG, message);
		}
	}
	
	public static void debug(String TAG, String message) {
		if (Config.LOG_DEBUG) {
			log("DEBUG", TAG, message);
		}
	}
	
	public static void info(String TAG, String message) {
		if (Config.LOG_INFO) {
			log("INFO", TAG, message);
		}
	}
	
	public static void error(String TAG, String message, Throwable e) {
		if (Config.LOG_ERROR) {
			log("ERROR", TAG, message + " " + e);
			e.printStackTrace();
		}
	}
	
	public static void log(String logLevel, String TAG, String message) {
		System.out.println(getTimestamp() + " [" + logLevel + "] " + TAG + " " + message);
	}
}