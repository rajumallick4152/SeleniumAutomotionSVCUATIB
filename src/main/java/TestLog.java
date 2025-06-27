import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLog {
    private static final Logger logger = LoggerFactory.getLogger(TestLog.class);

    public static void main(String[] args) {
        System.out.println("Project path: " + System.getProperty("user.dir"));
        logger.info("✅ This should go to both console and file");
        logger.warn("⚠️ Warning message");
        logger.error("❌ Error message with file log check");
    }
}
