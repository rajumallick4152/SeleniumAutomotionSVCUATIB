package automationFramework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
	private static final Properties properties = new Properties();

	static {
		try {
			FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
			properties.load(fis);
		} catch (IOException e) {
			System.err.println("⚠️ Failed to load config.properties: " + e.getMessage());
		}
	}

	public static String get(String key) {
		return properties.getProperty(key);
	}

	public static boolean getBoolean(String key) {
		String value = properties.getProperty(key);
		return value != null && value.equalsIgnoreCase("true");
	}
}
