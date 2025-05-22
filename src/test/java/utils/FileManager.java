package utils;

import io.qameta.allure.Allure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * Utility class for handling file operations.
 */
public class FileManager {

    /**
     * Attaches a file to the Allure report.
     * <p>
     * Given a file path and a name, this method checks if the file exists,
     * then reads it and attaches its content to the current Allure test report.
     *
     * @param filePath the path to the file to attach
     * @param name     the display name for the attachment in the Allure report
     */
    public static void attachFileToAllure(String filePath, String name) {
        File file = new File(filePath);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Allure.addAttachment(name, fis);
            } catch (IOException e) {
                System.err.println("Failed to attach file to Allure: " + filePath);
                e.printStackTrace();
            }
        } else {
            System.err.println("File not found: " + filePath);
        }
    }
}
