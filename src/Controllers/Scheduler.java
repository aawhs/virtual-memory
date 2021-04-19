package Controllers;


import Helpers.Parser;
import Helpers.Parser.ProcessesData;
import Models.Process;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Scheduler implements Runnable {
    Parser parser;

    int cores;
    Map<Integer, Boolean> coresState;
    Semaphore coresSemaphore;
    Semaphore mmuSemaphore;

    ArrayList<Process> processes;
    ArrayList<Thread> processesThreads = new ArrayList<>();

    Queue<Process> arrivalQueue;
    Queue<Process> readyQueue;



    public Scheduler(Parser parser){
        this.parser = parser;

        ProcessesData processesData = parser.getProcessesData();
        mmuSemaphore = new Semaphore(1,true);
        Process.mmuSemaphore = mmuSemaphore;

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

    private void createProcessThreads(){
        processesThreads = new ArrayList<>();
        for(Process process : processes)
            processesThreads.add(new Thread(process));
    }

    private void startThreads(){
        //processesThreads.get(0).start();
        for(Thread thread : processesThreads)
            thread.start();
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

    public synchronized static int getTime(){
        return Clock.INSTANCE.getTime();
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
        Clock.INSTANCE.setState(false);
    }
}





/*
    void cycle(){
        int i = 0;
        List <Thread> processesThreadsList = new LinkedList<>();
        for(Process process: processes) {
            processesThreadsList.add(new Thread(process));
        }

        long clock = Clock.INSTANCE.getTime();
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
    }*/
