package Controllers;

import Helpers.Parser;
import Models.MemoryObject;
import Models.MemoryPage;
import Models.VirtualMemory;
import jdk.jfr.Unsigned;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static Helpers.Parser.*;

/**
 * @Title COEN346 - Programming Assignment 3
 *
 * @author Ahmed Ali - 40102454
 * @author Petru-Andrei Vrabie - 40113236
 *
 * Controller - MMU
 */

public class MMU implements Runnable{
    /*================= Data Members ================= */
    private static int mainMemorySize = 0;
    private static int mainMemoryOccupied = 0;
    private static MemoryPage[] mainMemory;
    public static VirtualMemory virtualMemory;
    private Parser parser;
    private MemConfigData memConfigData;
    public static volatile boolean readNext = false;
    public static volatile boolean closeMMU = false;
    public static String command;
    public static String result;
    public static boolean vmAccess = false;
    private static CommandsData commandsData;


    /*================= Constructor ================= */
    public MMU(Parser parser){
        this.parser = parser;
        memConfigData = parser.getMemConfigData();
        mainMemorySize = memConfigData.getNumOfPages();
        mainMemory = new MemoryPage[mainMemorySize];
        for(int i = 0 ; i <mainMemorySize; i++){
            mainMemory[i] = null;
        }
        virtualMemory = new VirtualMemory();
        commandsData = parser.getCommandsData();
    }

    /*================= API ================= */
    public synchronized static String executeCommand(String command){
        String commandType = command.split(" ")[0];
        String commandStatus;


       switch (commandType.toLowerCase(Locale.ROOT)){
           case "store"-> {
               String[] parsedCommand = command.split(" ");
               String variableId = parsedCommand[1];
               int value = Integer.valueOf(parsedCommand[2]);
               commandStatus = store(variableId, value);
               result = commandStatus;
               return commandStatus;
           }
           case "lookup"-> {
               String[] parsedCommand = command.split(" ");
               String variableId = parsedCommand[1];
               int value = lookup(variableId);
               String returnString = String.format("Lookup: Variable %s, Value %d", variableId, value);
               result = returnString;
               return returnString;
           }
           case "release"-> {
               String[] parsedCommand = command.split(" ");
               String variableId = parsedCommand[1];
               commandStatus = release(variableId);
               result = commandStatus;
               return commandStatus;
           }
           case ""-> {
               return "Command not executed - unidentified command";
           }
       }
       return "Command not executed - unidentified command";
    }

    /*================= MMU Methods ================= */
    public synchronized static String store(String variableId, int value){
        int cTime = Clock.INSTANCE.getTime();
        if(mainMemoryOccupied != mainMemorySize){
            for(int i = 0; i < mainMemorySize; i++){
                if(mainMemory[i] == null){
                    MemoryObject memoryObject = new MemoryObject(variableId, value, cTime, false);
                    MemoryPage memoryPage = new MemoryPage(i+1, memoryObject);
                    mainMemory[i] = memoryPage;
                    mainMemoryOccupied++;
                    String returnString = String.format("Store: Variable %s, Value %d", variableId, value);
                    return returnString;
                }
            }
        }else{
            MemoryObject memoryObject = new MemoryObject(variableId, value, cTime, false);
            MemoryPage memoryPage = new MemoryPage(VirtualMemory.vmPagesSize + 1, memoryObject);
            virtualMemory.getMemoryPages().add(memoryPage);
            virtualMemory.writeToVirtualMemory();
            String returnString = String.format("Store: Variable %s, Value %d", variableId, value);
            return returnString;
        }
        return "";
    }

    public synchronized static String release(String variableId){
        for(int i = 0; i <mainMemorySize; i++){
            if(mainMemory[i].getMemoryObject().getId() == variableId){
                mainMemory[i] = null;
                String returnString = String.format("Release: Variable %s", variableId);
                return returnString;
            }
        }

        if(virtualMemory.delete(variableId)){
            String returnString = String.format("Release: Variable %s", variableId);
            return returnString;
        }
        ArrayList<MemoryPage> memoryPages = virtualMemory.getMemoryPages();
        for(int i = 0; i <memoryPages.size(); i++){
            if(memoryPages.get(i).getMemoryObject().getId().equals(variableId)){
                memoryPages.remove(i);
                virtualMemory.writeToVirtualMemory();
                String returnString = String.format("Release: Variable %s", variableId);
                return returnString;
            }
        }
        String returnString = String.format("Release Failed: Variable %s", variableId);
        return returnString;
    }

    public synchronized static int lookup(String variableId){
        int cTime = Clock.INSTANCE.getTime();
        for(int i = 0; i < mainMemorySize ; i++){
            if(mainMemory[i].getMemoryObject().getId().equals(variableId)){
                int value = mainMemory[i].getMemoryObject().getValue();
                mainMemory[i].getMemoryObject().setTimeAccessed(cTime);
                return value;
            }
        }
        int compare = mainMemory[0].getMemoryObject().compareTo(mainMemory[1].getMemoryObject());
        try{
            switch (compare){
                case 0 -> throw new Throwable("Memory Object Access time is equal");
                case 1 -> {
                    swap(mainMemory[1], 1, variableId);
                    return mainMemory[1].getMemoryObject().getValue();
                }
                case -1 -> {
                    swap(mainMemory[0], 0, variableId);
                    return mainMemory[0].getMemoryObject().getValue();
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }

    public synchronized static void swap(MemoryPage memoryPage, int index, String newVariableId){
        int cTime = Clock.INSTANCE.getTime();
        String oldVariableId = memoryPage.getMemoryObject().getId();
        MemoryObject memoryObject = virtualMemory.swap(newVariableId, memoryPage);
        mainMemory[index].setMemoryObject(memoryObject);
        String returnString = String.format(
                "Clock : %d Memory Manager, SWAP: Variable %s with Variable %s",
                cTime, newVariableId,oldVariableId);
        System.out.println(returnString);

    }

    /*================= Thread Runnable ================= */
    @Override
    public void run() {
        while(!commandsData.getCommands().isEmpty()){
            while (!readNext) Thread.onSpinWait();
            executeCommand(command);
            readNext = false;
        }
    }
}
