package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for configuration-related helpers.
 */
public class ConfigUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    /**
     * Parses a window size string (e.g., "1920,1080") into an integer array.
     *
     * @param windowSize the window size string in the format "width,height"
     * @return an int array of size 2 where index 0 is width and index 1 is height.
     *         Returns default [1920, 1080] if parsing fails.
     */
    public static int[] parseWindowSize(String windowSize) {
        try {
            String[] size = windowSize.split(",");
            if (size.length != 2) {
                throw new IllegalArgumentException("Invalid window.size format. Use 'width,height'");
            }
            return new int[]{
                    Integer.parseInt(size[0].trim()),
                    Integer.parseInt(size[1].trim())
            };
        } catch (Exception e) {
            logger.warn("⚠️ Invalid window.size provided ('{}'). Falling back to default 1920x1080.", windowSize);
            return new int[]{1920, 1080};
        }
    }
}
