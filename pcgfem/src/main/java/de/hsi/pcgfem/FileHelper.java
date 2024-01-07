package de.hsi.pcgfem;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {

    public static void appendToFile(String fileName, String data) {
        try {
            File file = new File(fileName);

            // If file doesn't exist, create a new file
            if (!file.exists()) {
                file.createNewFile();
            }

            // Open the file in append mode
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            // Write data to the file
            bw.write(data);
            bw.newLine(); // Add a new line for each entry

            // Close resources
            bw.close();
            fw.close();

            System.out.println("& appended to file.");

        } catch (IOException e) {
            System.err.println("Error appending data to file: " + e.getMessage());
        }
    }
}
