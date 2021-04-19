package Models;

import Controllers.Clock;
import Helpers.FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class VirtualMemory {
    FileManager fileManager;
    File diskPages;
    FileWriter fileWriter;
    Scanner fileReader;
    ArrayList<MemoryPage> memoryPages;
    public static int vmPagesSize = 0;

    public VirtualMemory() {
        diskPages = new File("res/memory/vm.txt");
        createDiskFile();
        memoryPages = new ArrayList<>();
    }

    public void createDiskFile(){
        try {
            if(diskPages.createNewFile()){
                System.out.println("Virtual Memory Disk Pages file created : " + diskPages.getPath());
            }else{
                System.out.println("Virtual Memory Disk Pages file already exists : " + diskPages.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeToVirtualMemory(MemoryPage newMemoryPage){
        memoryPages.add(newMemoryPage);
        vmPagesSize = memoryPages.size();
        try {
            fileWriter = new FileWriter(diskPages);
            for(MemoryPage memoryPage : memoryPages)
                fileWriter.write(memoryPage.getMemoryObject().toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void writeToVirtualMemory(){
        try {
            fileWriter = new FileWriter(diskPages.getParentFile());
            for(MemoryPage memoryPage : memoryPages)
                fileWriter.write(memoryPage.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean delete(String id){
        for(int i = 0; i <memoryPages.size(); i++){
            if(memoryPages.get(i).getMemoryObject().getId().equals(id)){
                memoryPages.remove(i);
                vmPagesSize--;
                writeToVirtualMemory();
                return true;
            }
        }
        return false;
    }


    public synchronized MemoryPage lookup(String id){
        try {
            int index = 0;
            int cTime = Clock.INSTANCE.getTime();
            fileReader = new Scanner(diskPages);
            while(fileReader.hasNext()){
                index++;
                String line = fileReader.next();
               if(line.contains(id)){
                   String [] parsedLine = line.split(" ");
                   if(parsedLine[0].equals(id)){
                       MemoryObject memoryObject = new MemoryObject(
                               parsedLine[0],
                               Integer.valueOf(parsedLine[1]),
                               cTime, false);
                       MemoryPage memoryPage = new MemoryPage(index+1, memoryObject);
                       return memoryPage;
                   }
               }else{
                   return null;
               }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized MemoryObject swap(String id , MemoryPage memoryPage){
        MemoryPage locatedPage = lookup(id);
        if(locatedPage != null){
            MemoryObject memoryObject = locatedPage.getMemoryObject();
            delete(id);
            writeToVirtualMemory(memoryPage);
            return memoryObject;
        }else{

        }
        return null;
    }
    public File getDiskPages() {
        return diskPages;
    }

    public void setDiskPages(File diskPages) {
        this.diskPages = diskPages;
    }

    public ArrayList<MemoryPage> getMemoryPages() {
        return memoryPages;
    }

    public void setMemoryPages(ArrayList<MemoryPage> memoryPages) {
        this.memoryPages = memoryPages;
    }

    /*  public synchronized MemoryPage swap(int id){
        if(lookup(id)){
            for(MemoryPage memoryPage : memoryPages){
                if(memoryPage.getMemoryObject().getId() == id){
                    memoryPages.remove(memoryPage);
                }
            }
        }
        return new MemoryPage();
    }*/
}
