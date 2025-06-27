package automationFramework.runner;

import org.testng.TestNG;

import java.io.*;
import java.net.URL;
import java.util.Collections;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

public class TestNGRunner {
	public static void main(String[] args) {
		// 🔧 Enable Logback internal debug logging
		System.setProperty("logback.debug", "true");

		// ✅ Ensure logs/ folder exists
		new File("logs").mkdirs();

		// ✅ Load logback.xml manually from classpath
		try {
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();

			InputStream configStream = TestNGRunner.class.getClassLoader().getResourceAsStream("logback.xml");
			if (configStream == null) {
				System.err.println("❌ logback.xml not found in classpath!");
			} else {
				configurator.doConfigure(configStream);
				StatusPrinter.print(context); // Debug info
			}
		} catch (Exception e) {
			System.err.println("❌ Error loading logback.xml manually:");
			e.printStackTrace();
		}

		// ✅ Load testng.xml from resources (inside JAR)
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL resource = classLoader.getResource("testng.xml");

			if (resource == null) {
				System.err.println("❌ testng.xml not found in classpath!");
				return;
			}

			// Copy testng.xml to a temporary file
			InputStream in = resource.openStream();
			File tempFile = File.createTempFile("testng", ".xml");
			tempFile.deleteOnExit();

			try (OutputStream out = new FileOutputStream(tempFile)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}

			// Run TestNG using the copied testng.xml
			TestNG testng = new TestNG();
			testng.setTestSuites(Collections.singletonList(tempFile.getAbsolutePath()));
			testng.run();

		} catch (Exception e) {
			System.err.println("❌ Error running TestNG:");
			e.printStackTrace();
		}
	}
}
