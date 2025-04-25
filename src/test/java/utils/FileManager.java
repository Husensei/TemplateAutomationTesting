package utils;

import io.qameta.allure.Allure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileManager {

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
