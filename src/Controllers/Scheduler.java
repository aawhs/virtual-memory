package Controllers;


import Helpers.Parser;
import Models.Process;
import Models.State;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Scheduler implements Runnable {
    Parser parser;
    ArrayList<Process> processes;
    Semaphore semaphore;

    public Scheduler(Parser parser){
        this.parser = parser;
        processes = parser.getProcessesData().getProcessArrayList();
        semaphore = new Semaphore(1,true);
    }

    void cycle(){
        int i = 0;
        List <Thread> processesThreadsList = new LinkedList<>();
        for(Process process: processes) {
            processesThreadsList.add(new Thread(process));
        }

            int clock = Clock.INSTANCE.getTime();
            int readyTime = processes.get(i).getReadyTime()*1000;
            //System.out.println(clock);
            if(readyTime == clock){
                processes.get(i).setState(State.STARTED);
                System.out.println(clock + " : " + processes.get(i));
                processes.get(i).setState(State.READY);
                System.out.println(clock + " : " + processes.get(i));
                processesThreadsList.get(i).start();
                if(i < processes.size()){
                    i++;
                }
                if(i == processes.size())
                    i = 0;

            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        processesThreadsList.stream().forEach((process) -> {
            try {
                process.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void schedule(){

    }

    private void FCFS(){

    }

    private Boolean isFinished(){
        Map<Process, State> processesStates= new Hashtable<>();

        for(Process process : processes)
            processesStates.put(process, process.getState());

        int processesFinished = 0;
        for(State state : processesStates.values())
            if(state.equals(State.FINISHED))
                processesFinished++;

       if(processesFinished == processes.size()-1)
           return true;

        return false;
    }


    @Override
    public void run() {
        while (!isFinished())
            cycle();
        Clock.INSTANCE.setState(false);
    }
}
