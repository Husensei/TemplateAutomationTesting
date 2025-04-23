package utils;

import org.slf4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class EnvironmentManager {

    private static final Logger logger = LoggerUtils.getLogger(EnvironmentManager.class);
    private static final Properties properties = new Properties();

    static {
        // Load general config
        loadProperties("config/config.properties");

        // Load environment-specific config
        String env = System.getProperty("env", "dev").toLowerCase();
        String envFile = String.format("config/%s.properties", env);
        logger.info("🌐 Active environment: {}", env);
        loadProperties(envFile);
    }

    private static void loadProperties(String filePath) {
        try (InputStream input = EnvironmentManager.class.getClassLoader().getResourceAsStream(filePath)) {
            if (input != null) {
                properties.load(input);
                logger.info("✅ Loaded properties from {}", filePath);
            } else {
                logger.error("❌ Could not find config file: {}", filePath);
                throw new RuntimeException("Could not find " + filePath);
            }
        } catch (Exception e) {
            logger.error("❌ Error loading properties from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Error loading properties from " + filePath, e);
        }
    }

    public static String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        String fileValue = properties.getProperty(key);

        if (systemValue != null && !systemValue.trim().isEmpty()) {
            logger.info("📦 [System Property] {} = {}", key, systemValue);
            return systemValue;
        } else if (fileValue != null && !fileValue.trim().isEmpty()) {
            logger.info("📂 [Config File] {} = {}", key, fileValue);
            return fileValue;
        } else {
            logger.warn("❓ [Missing Property] {} - using default: {}", key, defaultValue);
            return defaultValue;
        }
    }
}
