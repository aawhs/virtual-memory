package Helpers;

import java.io.*;
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
    private static final File outputFile = new File("res/outputs/output.txt");
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

    public static void setOutputFile(){
        try {
            if(outputFile.createNewFile()){
                System.out.println("Output file created : " + outputFile.getPath());
            }else{
                System.out.println("Output file already exists : " + outputFile.getPath());
            }
            PrintStream output;
            output = new PrintStream(outputFile);
            System.setOut(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
