package upsimulator.interfaces;

import org.apache.log4j.Logger;

public abstract class UPSLogger {
	protected static UPSLogger upsLogger = null;
	protected static boolean logEnable = true;

	public static void debug(Object who, String msg) {
		if (upsLogger == null) {
			if (logEnable)
				Logger.getLogger(who.getClass()).debug(msg);
		} else {
			upsLogger.debugLog(who, msg);
		}
	}

	public static void error(Object who, String msg) {
		if (upsLogger == null) {
			if (logEnable)
				Logger.getLogger(who.getClass()).debug(msg);
		} else {
			upsLogger.errorLog(who, msg);
		}
	}

	public static void result(Object who, String msg) {
		if (upsLogger == null) {
			if (logEnable)
				Logger.getLogger(who.getClass()).debug(msg);
		} else {
			upsLogger.resultLog(who, msg);
		}
	}

	public abstract void debugLog(Object who, String msg);

	public abstract void errorLog(Object who, String msg);

	public abstract void resultLog(Object who, String msg);

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
