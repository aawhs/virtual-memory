package Helpers;

import Models.Process;

import java.io.File;
import java.util.*;
import java.util.concurrent.Semaphore;

import static Helpers.Parser.ProcessesData.*;
import static java.util.Arrays.sort;
/**
 * @Title COEN346 - Programming Assignment 3
 *
 * @author Ahmed Ali - 40102454
 * @author Petru-Andrei Vrabie - 40113236
 *
 * Helper - Parser
 */
public class Parser {
    /*================= Data Members ================= */
    FileManager fileManager;
    File file;
    ArrayList<String> lines;

    ArrayList<FileManager> fileManagers;
    ArrayList<File> files;
    Map<File, ArrayList<String>> fileLines;

    ProcessesData processesData;
    MemConfigData memConfigData;
    CommandsData commandsData;

    /*================= Constructors ================= */
    public Parser(FileManager fileManager) {
        this.fileManager = fileManager;
        this.file = fileManager.getFile();

        lines = fileManager.getLines();

        parse(file);
    }

    public Parser(ArrayList<FileManager> fileManagers){
        files = new ArrayList<>();
        fileLines = new Hashtable<>();
        this.fileManagers = fileManagers;
        for(FileManager fileManager: fileManagers){
            File file = fileManager.getFile();
            ArrayList<String> lines = fileManager.getLines();
            files.add(file);
            fileLines.put(file, lines);
        }
        parseFiles();
    }

    /*================= Parsing Methods ================= */
    public void parseFiles(){
        for(File file : files)
            parse(file);
    }

    /**
     * Main Parse Switch
     */
    public void parse(File file){
        String fileName = file.getName().toUpperCase();

        switch(fileName){
            case "PROCESSES.TXT" -> {
                processesData = new ProcessesData();
                parseProcesses(file);
            }
            case "MEMCONFIG.TXT" -> {
                memConfigData = new MemConfigData();
                parseMemConfig(file);
            }
            case "COMMANDS.TXT" -> {
                commandsData = new CommandsData();
                parseCommands(file);
            }
        }
    }


    /*================= SubClass Data Members ================= */
    /**
     * Process Parsing Object & Methods
     */
    public static class ProcessesData {
        int cores = 0;
        int numOfProcesses = 0;
        static Semaphore cpuSempahore;
        ArrayList<Process> processArrayList = new ArrayList<>();

        public int getCores() {
            return cores;
        }

        public void setCores(int cores) {
            this.cores = cores;
        }

        public int getNumOfProcesses() {
            return numOfProcesses;
        }

        public void setNumOfProcesses(int numOfProcesses) {
            this.numOfProcesses = numOfProcesses;
        }

        public ArrayList<Process> getProcessArrayList() {
            return processArrayList;
        }

        public void setProcessArrayList(ArrayList<Process> processArrayList) {
            this.processArrayList = processArrayList;
        }

        public static Semaphore getCpuSempahore() {
            return cpuSempahore;
        }

        public void sortProcesses(){
            processArrayList.sort(Comparator.comparingInt(Process::getReadyTime));
        }
    }
    public ProcessesData getProcessesData() {
        return processesData;
    }
    public void parseProcesses(File file){


        for(int i = 0; i < fileLines.get(file).size(); i++){
            switch (i) {
                case 0 ->{
                    processesData.cores = Integer.parseInt(fileLines.get(file).get(i));
                    cpuSempahore = new Semaphore(processesData.cores);
                }
                case 1 -> processesData.numOfProcesses = Integer.parseInt(fileLines.get(file).get(i));
                default -> {
                    String[] lineElements = fileLines.get(file).get(i).split("\\s");
                    int readyTime = Integer.parseInt(lineElements[0]) * 1000;
                    int serviceTime = Integer.parseInt(lineElements[1]) * 1000;
                    Process process = new Process("Process " + ((i - 2) + 1), readyTime, serviceTime, cpuSempahore);
                    processesData.processArrayList.add(process);
                }
            }
        }

        processesData.sortProcesses();
    }

    /**
     * MemConfig Parsing Object & Methods
     */
    public static class MemConfigData{
        int numOfPages;

        public int getNumOfPages() {
            return numOfPages;
        }

        public void setNumOfPages(int numOfPages) {
            this.numOfPages = numOfPages;
        }
    }
    public MemConfigData getMemConfigData() {
        return memConfigData;
    }
    public void parseMemConfig(File file){
        if(!fileLines.get(file).isEmpty())
            memConfigData.setNumOfPages(Integer.parseInt(fileLines.get(file).get(0)));

    }

    /**
     * Commands Parsing Object & Methods
     */
    public static class CommandsData{
        ArrayList<String> commandsList;
        Queue<String[]> commands;

        public ArrayList<String> getCommandsList() {
            return commandsList;
        }

        public void setCommandsList(ArrayList<String> commandsList) {
            this.commandsList = commandsList;
        }

        public synchronized Queue<String[]> getCommands() {
            return commands;
        }

        public void setCommands(Queue<String[]> commands) {
            this.commands = commands;
        }
    }
    public CommandsData getCommandsData() {
        return commandsData;
    }
    public void parseCommands(File file){
        commandsData.commandsList = new ArrayList<>();
        commandsData.commands = new LinkedList<>();
        for(String line: fileLines.get(file)){
            commandsData.commandsList.add(line);
            commandsData.commands.add(line.split(" "));
        }
        for(Process process : processesData.processArrayList)
            process.setCommandsData(getCommandsData());
    }
}
