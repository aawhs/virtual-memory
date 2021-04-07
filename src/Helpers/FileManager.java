package Helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @Title COEN346 - Programming Assignment 3
 *
 * @author Ahmed Ali - 40102454
 * @author Petru-Andrei Vrabie - 40113236
 *
 * Helper - FileManager
 */
public class FileManager {
    /*================= Data Members ================= */
    File file;
    Scanner scanner;
    ArrayList<String> lines;

    /*================= Constructor ================= */
    public FileManager(String fileName){
        file = new File(fileName);
        try {
            scanner = new Scanner(file);
            scan();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*================= Class Methods ================= */
    public void scan(){
        lines = new ArrayList<>();
        while (scanner.hasNextLine())
            lines.add(scanner.nextLine());
    }

    /*================= Setters & Getters ================= */

    public ArrayList<String> getLines() {
        return lines;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
