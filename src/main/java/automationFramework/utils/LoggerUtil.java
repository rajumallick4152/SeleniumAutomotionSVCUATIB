package automationFramework.utils;

import automationFramework.listeners.TestListener;
import com.aventstack.extentreports.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
	private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

	public static void log(String message) {
		logger.info(message); // SLF4J log
		if (TestListener.getLogger() != null) {
			TestListener.getLogger().log(Status.INFO, message); // Extent log
		}
	}

	public static void error(String message, Throwable t) {
		logger.error(message, t);
		if (TestListener.getLogger() != null) {
			TestListener.getLogger().log(Status.FAIL, message + " - " + t.getMessage());
		}
	}
}
