package utils;

public class ConfigUtils {

    public static int[] parseWindowSize(String windowSize) {
        try {
            String[] size = windowSize.split(",");
            if (size.length != 2) throw new IllegalArgumentException("Invalid window.size format. Use 'width,height'");
            return new int[]{Integer.parseInt(size[0].trim()), Integer.parseInt(size[1].trim())};
        } catch (Exception e) {
            System.out.println("⚠️ Invalid window.size provided. Falling back to default 1920x1080.");
            return new int[]{1920, 1080};
        }
    }
}
