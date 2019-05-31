package com.nh.micro.logutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author ninghao
 *
 */
public class LogUtil4Api {

	private static String lineChar = System.getProperty("line.separator");

	public static void saveMsg(String linkId, String msg, String info, Object... args) {
		Logger logger = LogUtil.getLogger(LogUtil4Api.class);
		logger.info(info + lineChar + msg, args);
	}
}
