package com.example.hello;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class FileReader {

    public static List<String> ReadFile(String path) {


        String encoding = detectFileEncoding(path);
        if (encoding == null) {
            encoding = "UTF-8"; // default encoding
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), Charset.forName(encoding))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage()); // Printing concise error message
        }

        return lines;
    }

    // Detects file encoding using UniversalDetector
    private static String detectFileEncoding(String filePath) {
        byte[] buf = new byte[4096];
        try (FileInputStream fis = new FileInputStream(filePath)) {
            UniversalDetector detector = new UniversalDetector(null);

            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }

            detector.dataEnd();

            String encoding = detector.getDetectedCharset();
            detector.reset();

            return encoding;
        } catch (IOException e) {
            System.err.println("Error detecting file encoding: " + e.getMessage()); // Printing concise error message
            return null;
        }
    }
}
