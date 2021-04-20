package Controllers;

/**
 * @Title COEN346 - Programming Assignment 3
 *
 * @author Ahmed Ali - 40102454
 * @author Petru-Andrei Vrabie - 40113236
 *
 * Controller - Scheduler
 */
import Helpers.Parser;
import Helpers.Parser.ProcessesData;
import Models.Process;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Scheduler implements Runnable {
    /*================= Data Members ================= */
    Parser parser;

    int cores;
    Map<Integer, Boolean> coresState;
    Semaphore coresSemaphore;
    Semaphore mmuSemaphore;
    Semaphore commandSemaphore;

    ArrayList<Process> processes;
    ArrayList<Thread> processesThreads = new ArrayList<>();

    Queue<Process> arrivalQueue;
    Queue<Process> readyQueue;


    /*================= Constructor ================= */
    public Scheduler(Parser parser){
        this.parser = parser;

        ProcessesData processesData = parser.getProcessesData();
        mmuSemaphore = new Semaphore(1);
        Process.mmuSemaphore = mmuSemaphore;
        commandSemaphore = new Semaphore(1);
        Process.commandSemaphore = commandSemaphore;



        processes = processesData.getProcessArrayList();
        cores = processesData.getCores();
        coresState = new Hashtable<>();
        for(int i = 1; i <= cores ; i++)
            coresState.put(i, false);
        coresSemaphore = ProcessesData.getCpuSempahore();

        arrivalQueue = new LinkedList<>();
        readyQueue = new LinkedList<>();

        setArrivalQueue();

    }

    /*================= Scheduling Methods ================= */
    private void schedule(){
        while(!isFinished()){
            if(!arrivalQueue.isEmpty()){
                int cTime = Clock.INSTANCE.getTime();
                if(arrivalQueue.peek().getReadyTime() == cTime){
                    Process readyProcess = arrivalQueue.poll();
                    readyQueue.add(readyProcess);
                    if(!readyQueue.isEmpty()){
                        Thread process = new Thread(readyQueue.poll());
                        processesThreads.add(process);
                        process.setName(readyProcess.getName());
                        process.start();
                    }
                }else{
                    if(!readyQueue.isEmpty()){
                        Thread process = new Thread(readyQueue.poll());
                        process.start();
                    }
                }
            }
        }
    }


    private void setArrivalQueue(){
        for(Process process : processes)
            arrivalQueue.add(process);
    }


    private Boolean isFinished(){
        Map<Process, Process.ProcessState> processesStates= new Hashtable<>();

        for(Process process : processes)
            processesStates.put(process, process.getState());

        int processesFinished = 0;
        for(Process.ProcessState state : processesStates.values())
            if(state.equals(Process.ProcessState.FINISHED))
                processesFinished++;

       if(processesFinished == processes.size())
           return true;

        return false;
    }


    @Override
    public void run() {
        schedule();
        for(Thread thread : processesThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MMU.closeMMU = true;
        Clock.INSTANCE.setState(false);
    }
}