package Helpers;

import Models.Process;

import java.io.File;
import java.util.ArrayList;

public class Parser {
    FileManager fileManager;
    File file;
    ArrayList<String> lines;

    ProcessesData processesData;


    public Parser(FileManager fileManager) {
        this.fileManager = fileManager;
        this.file = fileManager.getFile();
        lines = fileManager.getLines();

        processesData = new ProcessesData();

        parse();
    }

    public void parse(){
        switch(file.getName().toUpperCase()){
            case "PROCESSES.TXT":
                parseProcesses();
                break;
            case "MEMCONFIG.TXT":
                parseMemConfig();
                break;
            case "COMMANDS.TXT":
                parseCommands();
                break;
            default:
                break;
        }
    }

    public void parseProcesses(){
        for(int i = 0; i < lines.size(); i++){
            switch (i) {
                case 0 -> processesData.cores = Integer.parseInt(lines.get(i));
                case 1 -> processesData.numOfProcesses = Integer.parseInt(lines.get(i));
                default -> {
                    String[] lineElements = lines.get(i).split("\\s");
                    int readyTime = Integer.parseInt(lineElements[0]);
                    int serviceTime = Integer.parseInt(lineElements[1]);
                    Process process = new Process("Process " + ((i - 2) + 1), readyTime, serviceTime);
                    processesData.processArrayList.add(process);
                }
            }
        }
    }

    public void parseMemConfig(){

    }

    public void parseCommands(){

    }

    public static class ProcessesData {
        int cores = 0;
        int numOfProcesses = 0;
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
    }

    static class MemConfigData{
        int numOfPages;
    }

    public ProcessesData getProcessesData() {
        return processesData;
    }
}
