package com.nh.micro.logutil;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * 
 * @author ninghao
 *
 */
public class LogUtil {

	private static LoggerContext loggerContext = null;
	private static ConcurrentMap logHolder = new ConcurrentHashMap();
	private static String msgTag = "|+|";
	private static Boolean startDebug = false;
	public static Integer rollNum=30;

	public static void initLogContext(String rootDir, Boolean startDebug) {
		LogUtil.startDebug = startDebug;
		String logPath = rootDir + "/infoLog.log";
		String apilogPath = rootDir + "/apiLog.log";
		String errorlogPath = rootDir + "/errorLog.log";
		String logPathHistory = rootDir + "/infoLog.log.%d";
		String apilogPathHistory = rootDir + "/apiLog.log.%d";
		String errorlogPathHistory = rootDir + "/errorLog.log.%d";
		loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		// console
		ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
		consoleAppender.setContext(loggerContext);
		consoleAppender.setName("nhLogAppender4Console");

		// 4info
		RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
		rollingFileAppender.setContext(loggerContext);
		rollingFileAppender.setAppend(true);
		rollingFileAppender.setName("nhLogAppender");
		rollingFileAppender.setFile(logPath);

		TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
		rollingPolicy.setFileNamePattern(logPathHistory);
		rollingPolicy.setMaxHistory(rollNum);
		rollingPolicy.setContext(loggerContext);
		rollingPolicy.setParent(rollingFileAppender);
		rollingPolicy.start();
		rollingFileAppender.setRollingPolicy(rollingPolicy);

		// 4api
		RollingFileAppender<ILoggingEvent> rollingFileAppender4Api = new RollingFileAppender<ILoggingEvent>();
		rollingFileAppender4Api.setContext(loggerContext);
		rollingFileAppender4Api.setAppend(true);
		rollingFileAppender4Api.setName("nhLogAppender4Api");
		rollingFileAppender4Api.setFile(apilogPath);

		TimeBasedRollingPolicy rollingPolicy4Api = new TimeBasedRollingPolicy();
		rollingPolicy4Api.setFileNamePattern(apilogPathHistory);
		rollingPolicy4Api.setMaxHistory(rollNum);
		rollingPolicy4Api.setContext(loggerContext);
		rollingPolicy4Api.setParent(rollingFileAppender4Api);

		rollingPolicy4Api.start();
		rollingFileAppender4Api.setRollingPolicy(rollingPolicy4Api);

		// 4error
		RollingFileAppender<ILoggingEvent> rollingFileAppender4Error = new RollingFileAppender<ILoggingEvent>();
		rollingFileAppender4Error.setContext(loggerContext);
		rollingFileAppender4Error.setAppend(true);
		rollingFileAppender4Error.setName("nhLogAppender4Error");
		rollingFileAppender4Error.setFile(errorlogPath);

		TimeBasedRollingPolicy rollingPolicy4Error = new TimeBasedRollingPolicy();
		rollingPolicy4Error.setFileNamePattern(errorlogPathHistory);
		rollingPolicy4Error.setMaxHistory(rollNum);
		rollingPolicy4Error.setContext(loggerContext);
		rollingPolicy4Error.setParent(rollingFileAppender4Error);

		rollingPolicy4Error.start();
		rollingFileAppender4Error.setRollingPolicy(rollingPolicy4Error);

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setPattern(
				"%d{yyyy-MM-dd HH:mm:ss.SSS} |  %-5level | [%-15thread] | %replace(%caller{1..2}){'\t|Caller.{1}1|\r\n| at ', ''} | --[LOGUTIL]-- %msg%n");
		encoder.setCharset(Charset.forName("UTF-8"));
		encoder.setContext(loggerContext);
		encoder.start();
		PatternLayoutEncoder encoder4Api = new PatternLayoutEncoder();
		encoder4Api.setPattern(
				"%d{yyyy-MM-dd HH:mm:ss.SSS} |  %-5level | [%-15thread] | %replace(%caller{2..3}){'\t|Caller.{1}2|\r\n| at ', ''} | --[LOGUTIL]-- %msg%n");
		encoder4Api.setCharset(Charset.forName("UTF-8"));
		encoder4Api.setContext(loggerContext);
		encoder4Api.start();

		PatternLayoutEncoder encoder4Console = new PatternLayoutEncoder();
		encoder4Console.setPattern(
				"%d{yyyy-MM-dd HH:mm:ss.SSS} |  %-5level | [%-15thread] | %replace(%caller{1..2}){'\t|Caller.{1}1|\r\n| at ', ''} | --[LOGUTIL]-- %msg%n");
		encoder4Console.setCharset(Charset.forName("UTF-8"));
		encoder4Console.setContext(loggerContext);
		encoder4Console.start();

		PatternLayoutEncoder encoder4Error = new PatternLayoutEncoder();
		encoder4Error.setPattern(
				"%d{yyyy-MM-dd HH:mm:ss.SSS} |  %-5level | [%-15thread] | %replace(%caller{2..3}){'\t|Caller.{1}2|\r\n| at ', ''} | --[LOGUTIL]-- %msg%n");
		encoder4Error.setCharset(Charset.forName("UTF-8"));
		encoder4Error.setContext(loggerContext);
		encoder4Error.start();

		rollingFileAppender.setEncoder(encoder);
		rollingFileAppender4Api.setEncoder(encoder4Api);
		consoleAppender.setEncoder(encoder4Console);
		rollingFileAppender4Error.setEncoder(encoder4Error);

		consoleAppender.start();
		rollingFileAppender.start();
		rollingFileAppender4Api.start();
		rollingFileAppender4Error.start();

		ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.setLevel(Level.INFO);
		rootLogger.detachAndStopAllAppenders();
		rootLogger.addAppender(rollingFileAppender);
		rootLogger.addAppender(consoleAppender);

		ch.qos.logback.classic.Logger fileLogger = (ch.qos.logback.classic.Logger) getLogger(LogUtil4Api.class);
		fileLogger.addAppender(rollingFileAppender4Api);
		fileLogger.setAdditive(false);

		ch.qos.logback.classic.Logger fileLogger4Error = (ch.qos.logback.classic.Logger) getLogger(LogUtil4Error.class);
		fileLogger4Error.addAppender(rollingFileAppender4Error);
		fileLogger4Error.setAdditive(false);
		StatusPrinter.print(loggerContext);
	}

	public static Logger getLogger(Class cls) {
		String clsName = cls.getName();
		return getLogger(clsName);

	}

	private static Logger getLogger(String clsName) {

		Logger logger = (Logger) logHolder.get(clsName);
		if (logger != null) {
			return logger;
		} else {
			Logger newLogger = loggerContext.getLogger(clsName);
			Logger oldLogger = (Logger) logHolder.putIfAbsent(clsName, newLogger);
			Logger retLogger = oldLogger == null ? newLogger : oldLogger;
			if (startDebug == true) {
				((ch.qos.logback.classic.Logger) retLogger).setLevel(Level.DEBUG);
			}
			return retLogger;
		}

	}

	private static String getParentCls(Throwable throwable) {

		StackTraceElement[] ste = throwable.getStackTrace();
		return ste[1].getClassName();
	}

	public static void info(String info, Object... args) {
		Throwable throwable = new Throwable();
		String parentClsName = getParentCls(throwable);
		Logger logger = getLogger(parentClsName);

		logger.info(info, args);
	}

	public static void debug(String info, Object... args) {
		Throwable throwable = new Throwable();
		String parentClsName = getParentCls(throwable);
		Logger logger = getLogger(parentClsName);

		//check
		if(logger.isDebugEnabled()) {
			logger.debug(info, args);
		}
	}

	private static String createMsg(Map params) {
		if (params == null || params.isEmpty()) {
			return "";
		}
		Set entrySet = params.entrySet();
		Iterator it = entrySet.iterator();
		StringBuffer sb = new StringBuffer("{");
		int index = 0;
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			if (index > 0) {
				sb.append(",");
			}
			index++;
			sb.append("\"").append(key).append("\"");
			sb.append(":");
			String val = value.toString().replace("\"", "\\\"");
			sb.append("\"").append(value == null ? "" : value.toString()).append("\"");

		}
		sb.append("}");
		return sb.toString();
	}

	public static void trackPoint(Enum trackType, String userId, Map<String, Object> params, String info,
			Object... args) {
		Throwable throwable = new Throwable();
		String parentClsName = getParentCls(throwable);
		Logger logger = getLogger(parentClsName);
		String trackStr = trackType.toString();

		Map msgMap = new LinkedHashMap();
		msgMap.put("ar_trackpoint", trackStr);
		msgMap.put("ar_userid", userId);
		if (params != null) {
			msgMap.putAll(params);
		}
		String trackMsg = createMsg(msgMap);
		logger.info(info + " " + msgTag + " " + trackMsg, args);
	}

	public static void trackApi(Enum sourceSysId, Enum targetSysId, Boolean sendFlag, String callId, String bizId,
			String apiMsg, String info, Object... args) {
		Throwable throwable = new Throwable();
		String parentClsName = getParentCls(throwable);
		Logger logger = getLogger(parentClsName);

		Map msgMap = new LinkedHashMap();
		msgMap.put("ar_source_sysid", sourceSysId.toString());
		msgMap.put("ar_target_sysid", targetSysId.toString());
		msgMap.put("ar_sendflag", sendFlag.toString());
		msgMap.put("ar_callid", callId);
		msgMap.put("ar_bizid", bizId);
		String uuid = UUID.randomUUID().toString();
		msgMap.put("ar_linkid", uuid);

		String trackMsg = createMsg(msgMap);
		logger.info(info + " " + msgTag + " " + trackMsg, args);
		LogUtil4Api.saveMsg(uuid, apiMsg, info + " " + msgTag + " " + trackMsg, args);
	}

	public static void error(String msg, Throwable throwable) {
		Throwable tempThrowable = new Throwable();
		String parentClsName = getParentCls(tempThrowable);
		Logger logger = getLogger(parentClsName);
		logger.error(msg, throwable);
		LogUtil4Error.saveError(msg, throwable);
	}
}
