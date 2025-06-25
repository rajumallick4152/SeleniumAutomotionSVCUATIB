package automationFramework.runner;

import org.testng.TestNG;

import java.io.*;
import java.net.URL;
import java.util.Collections;

public class TestNGRunner {
	public static void main(String[] args) {
		try {
			// Load testng.xml from resources (inside JAR)
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL resource = classLoader.getResource("testng.xml");

			if (resource == null) {
				System.err.println("testng.xml not found in classpath!");
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

			// Run TestNG using temp testng.xml
			TestNG testng = new TestNG();
			testng.setTestSuites(Collections.singletonList(tempFile.getAbsolutePath()));
			testng.run();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
