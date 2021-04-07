package Main;

import Controllers.Clock;
import Controllers.Scheduler;
import Helpers.FileManager;
import Helpers.Parser;

public class Main {
    public static void main(String[] args){
        Parser parser = new Parser(new FileManager("res/inputs/processes.txt"));
        Scheduler scheduler = new Scheduler(parser);
        Thread clockThread = new Thread(Clock.INSTANCE);
        Thread mainThread = new Thread(scheduler);

        clockThread.start();
        mainThread.start();



        try {
            clockThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
