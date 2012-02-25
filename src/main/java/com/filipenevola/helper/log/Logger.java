/*
 * Filipe Nevola (@FilipeNevola)
 * Site: filipenevola.tumblr.com
 * Email: filipenevola@gmail.com
 * Work at Tecsinapse (@Tecsinapse)
 */
package com.filipenevola.helper.log;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Priority;

/**
 * @author Filipe Névola
 * 
 *         Logger from anywhere without declaring
 */
public class Logger extends SecurityManager {
	private static ConcurrentHashMap<String, org.apache.log4j.Logger> loggerMap = new ConcurrentHashMap<String, org.apache.log4j.Logger>();

	public static org.apache.log4j.Logger getLog() {
		String className = new Logger().getClassName();
		if (!loggerMap.containsKey(className)) {
			loggerMap.put(className,
					org.apache.log4j.Logger.getLogger(className));
		}
		return loggerMap.get(className);
	}

	public String getClassName() {
		return getClassContext()[3].getName();
	}

	public static void trace(Object message) {
		getLog().trace(message);
	}

	public static void trace(Object message, Throwable t) {
		getLog().trace(message, t);
	}

	public static boolean isTraceEnabled() {
		return getLog().isTraceEnabled();
	}

	public static void debug(Object message) {
		getLog().debug(message);
	}

	public static void debug(Object message, Throwable t) {
		getLog().debug(message, t);
	}

	public static void error(Object message) {
		getLog().error(message);
	}

	public static void error(Object message, Throwable t) {
		getLog().error(message, t);
	}

	public static void fatal(Object message) {
		getLog().fatal(message);
	}

	public static void fatal(Object message, Throwable t) {
		getLog().fatal(message, t);
	}

	public static void info(Object message) {
		getLog().info(message);
	}

	public static void info(Object message, Throwable t) {
		getLog().info(message, t);
	}

	public static boolean isDebugEnabled() {
		return getLog().isDebugEnabled();
	}

	public static boolean isEnabledFor(Priority level) {
		return getLog().isEnabledFor(level);
	}

	public static boolean isInfoEnabled() {
		return getLog().isInfoEnabled();
	}

	public static void setLevel(org.apache.log4j.Level level) {
		getLog().setLevel(level);
	}

	public static void warn(Object message) {
		getLog().warn(message);
	}

	public static void warn(Object message, Throwable t) {
		getLog().warn(message, t);
	}
}