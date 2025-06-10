/*
 * package automationFramework.runner; // You can put it in a 'runner' package
 * 
 * import org.testng.TestNG; import java.util.ArrayList; import java.util.List;
 * import java.io.File; // Needed for File operations
 * 
 * public class TestNgRunner {
 * 
 * public static void main(String[] args) { TestNG testng = new TestNG();
 * List<String> suites = new ArrayList<>();
 * 
 * // Assuming testng.xml is in the same directory as the executable JAR // When
 * you run the JAR, Java's current working directory will be where the JAR is
 * located. String testNgXmlPath = "testng.xml"; File testNgXmlFile = new
 * File(testNgXmlPath);
 * 
 * if (!testNgXmlFile.exists()) {
 * System.err.println("Error: testng.xml not found at " +
 * testNgXmlFile.getAbsolutePath()); System.err.
 * println("Please ensure testng.xml is in the same folder as the JAR file.");
 * return; // Exit if XML is not found }
 * 
 * suites.add(testNgXmlFile.getAbsolutePath()); // Add the absolute path to your
 * testng.xml
 * 
 * testng.setTestSuites(suites);
 * System.out.println("Running TestNG suite from: " +
 * testNgXmlFile.getAbsolutePath()); testng.run(); // This line executes your
 * TestNG tests System.out.println("TestNG execution finished."); } }
 */