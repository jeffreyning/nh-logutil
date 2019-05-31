package com.nh.micro.logutil;

import org.slf4j.Logger;

/**
 * 
 * @author ninghao
 *
 */
public class LogUtil4Error {
	private static String lineChar = System.getProperty("line.separator");

	public static void saveError(String msg, Throwable throwable) {
		Logger logger = LogUtil.getLogger(LogUtil4Error.class);
		logger.error(msg, throwable);
	}
}
