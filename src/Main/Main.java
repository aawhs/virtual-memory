package Main;

import Controllers.Clock;
import Controllers.MMU;
import Controllers.Scheduler;
import Helpers.FileManager;
import Helpers.Parser;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import static Helpers.Parser.*;

public class Main {
    public static void main(String[] args){
        String[] fileNames = new String[] {
                "res/inputs/processes.txt",
                "res/inputs/memconfig.txt",
                "res/inputs/commands.txt"
        };

        ArrayList<FileManager> fileManagers = new ArrayList<>();
        //FileManager.setOutputFile();
        for(String fileName : fileNames)
            fileManagers.add(new FileManager(fileName));


        Parser parser = new Parser(fileManagers);
        MMU mmu = new MMU(parser);
        Scheduler scheduler = new Scheduler(parser);

        Thread clockThread = new Thread(Clock.INSTANCE);
        Thread mmuThread = new Thread(mmu);
        Thread schedulerThread = new Thread(scheduler);

        clockThread.setName("Clock Thread");
        mmuThread.setName("MMU Thread");
        schedulerThread.setName("Scheduler Thread");

        mmuThread.start();
        schedulerThread.start();
        clockThread.start();

        try {
            schedulerThread.join();
            clockThread.join();
            mmuThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
