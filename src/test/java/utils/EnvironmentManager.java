package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * EnvironmentManager is responsible for loading and managing configuration properties
 * from a general `config.properties` file and an environment-specific file (e.g., `dev.properties`, `prod.properties`).
 * <p>
 * It also allows retrieving configuration values with support for:
 * <ul>
 *     <li>System properties (e.g., passed via JVM args)</li>
 *     <li>Config file values (loaded from resources)</li>
 *     <li>Default values if none are found</li>
 * </ul>
 * <p>
 * The active environment is determined by the system property {@code env}, defaulting to {@code dev}.
 */
public class EnvironmentManager {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentManager.class);
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

    /**
     * Loads the specified properties file from the classpath into the properties store.
     *
     * @param filePath the relative path to the properties file (e.g., "config/dev.properties")
     * @throws RuntimeException if the file cannot be found or loaded
     */
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

    /**
     * Retrieves a configuration value by key. The method checks for:
     * <ol>
     *     <li>System property (e.g., passed via -Dkey=value)</li>
     *     <li>Value from loaded properties file</li>
     *     <li>Fallback to the provided default value</li>
     * </ol>
     *
     * @param key          the configuration key
     * @param defaultValue the default value to return if no system or file-based property is found
     * @return the resolved configuration value
     */
    public static String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        String fileValue = properties.getProperty(key);

        if (systemValue != null && !systemValue.trim().isEmpty()) {
//            logger.info("📦 [System Property] {} = {}", key, systemValue);
            return systemValue;
        } else if (fileValue != null && !fileValue.trim().isEmpty()) {
//            logger.info("📂 [Config File] {} = {}", key, fileValue);
            return fileValue;
        } else {
            logger.warn("❓ [Missing Property] {} - using default: {}", key, defaultValue);
            return defaultValue;
        }
    }
}
