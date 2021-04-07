package Controllers;


import Helpers.Parser;
import Models.Process;

import java.util.ArrayList;

public class Scheduler implements Runnable {
    Parser parser;
    ArrayList<Process> processes;

    public Scheduler(Parser parser){
        this.parser = parser;
        processes = parser.getProcessesData().getProcessArrayList();
    }

    void cycle(){
        while(Clock.INSTANCE.getTime() <= 3000){
            int i = 0;
            if(Clock.INSTANCE.getTime() == 3000){
                Clock.INSTANCE.setState(false);
            }

            int clock = Clock.INSTANCE.getTime();
            int readyTime = processes.get(i).getReadyTime()*1000;
            System.out.println(clock);
            if(readyTime == clock){
                System.out.println(clock + " : " + processes.get(i));
                i++;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }


    @Override
    public void run() {
        cycle();
    }
}
