package automationFramework.utils;

import java.io.File; // read data from external file
import java.io.FileInputStream; // read data from external file
import java.io.InputStream; // read data from classpath
import java.util.Properties; // read key-value pair data from properties file

public class ConfigReader {
	private static final String CONFIG_FILE_NAME = "config.properties";
	private static final Properties properties = new Properties();

	static {
		boolean loaded = false;

		// 1️⃣ Try to load from external path (for JAR run)
		try {
			File configFile = new File(CONFIG_FILE_NAME); // same folder as JAR
			if (configFile.exists()) {
				FileInputStream fis = new FileInputStream(configFile);
				properties.load(fis);
				System.out.println("✅ Loaded config.properties from external file.");
				loaded = true;
			}
		} catch (Exception e) {
			System.err.println("⚠️ Failed to load config from external file: " + e.getMessage());
		}

		// 2️⃣ If not found, load from classpath (for Eclipse)
		if (!loaded) {
			try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
				if (input != null) {
					properties.load(input);
					System.out.println("✅ Loaded config.properties from classpath.");
					loaded = true;
				}
			} catch (Exception e) {
				System.err.println("⚠️ Failed to load config from classpath: " + e.getMessage());
			}
		}

		if (!loaded) {
			System.err.println("❌ config.properties not found in external or classpath!");
		}
	}
//   Methods for fetching data

	public static String get(String key) {
		return properties.getProperty(key);
	}

	public static boolean getBoolean(String key) {
		String value = get(key);
		return value != null && value.equalsIgnoreCase("true");
	}

	public static int getInt(String key) {
		try {
			return Integer.parseInt(get(key));
		} catch (NumberFormatException e) {
			System.err.println("⚠️ Invalid int for key: " + key);
			return 0;
		}
	}

	public static String getAppURL() {
		String env = get("env");
		String key = env + ".url";
		return get(key);
	}

	public static String getBrowser() {
		return get("browser");
	}

	public static String getEnv() {
		return get("env");
	}
}
