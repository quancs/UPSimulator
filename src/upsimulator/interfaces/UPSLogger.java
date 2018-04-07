package upsimulator.interfaces;

import org.apache.log4j.Logger;

public abstract class UPSLogger {
	protected static UPSLogger upsLogger = null;
	protected static boolean logEnable = true;

	public static void debug(Object who, Object msg) {
		if (upsLogger == null) {
			if (logEnable)
				Logger.getLogger(who.getClass()).debug(msg);
		} else {
			upsLogger.debugLog(who, msg);
		}
	}

	public static void error(Object who, Object msg) {
		if (upsLogger == null) {
			if (logEnable)
				Logger.getLogger(who.getClass()).debug(msg);
		} else {
			upsLogger.errorLog(who, msg);
		}
	}

	public static void result(Object who, Object msg) {
		if (upsLogger == null) {
			if (logEnable)
				Logger.getLogger(who.getClass()).debug(msg);
		} else {
			upsLogger.resultLog(who, msg);
		}
	}

	/**
	 * Log debug informations
	 * 
	 * @param who
	 *            who want to send the information
	 * @param msg
	 *            the msg object
	 */
	public abstract void debugLog(Object who, Object msg);

	/**
	 * Log exceptions or errors
	 * 
	 * @param who
	 *            where the error occurred
	 * @param msg
	 *            the msg object, can be exception or string
	 */
	public abstract void errorLog(Object who, Object msg);

	/**
	 * Log results
	 * 
	 * @param who
	 *            who want to send to msg
	 * @param msg
	 *            the msg object
	 */
	public abstract void resultLog(Object who, Object msg);

	public static UPSLogger getLogger() {
		return upsLogger;
	}

	public static void setLogger(UPSLogger logger) {
		if (UPSLogger.upsLogger != logger) {
			UPSLogger.upsLogger = logger;
		}
	}

	public static boolean isEnable() {
		return logEnable;
	}

	public static void setEnable(boolean logEnable) {
		UPSLogger.logEnable = logEnable;
	}
}
