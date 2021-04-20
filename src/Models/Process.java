package Models;

import Controllers.Clock;
import Controllers.MMU;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import static Helpers.Parser.*;
import static Models.Process.ProcessState.*;

/**
 * @Title COEN346 - Programming Assignment 3
 *
 * @author Ahmed Ali - 40102454
 * @author Petru-Andrei Vrabie - 40113236
 *
 * Model - Process
 */
public class Process implements Runnable{
    /*================= Data Members ================= */
    String name;
    int readyTime;
    int serviceTime;
    ProcessState state;
    public static Semaphore cpuSempahore;

    int timeInCPU = 0;
    int remainingTime;
    int startTime;

    CommandsData commandsData = new CommandsData();
    public static Semaphore commandSemaphore;
    public static Semaphore mmuSemaphore;


    /*================= Constructor ================= */
    public Process(String name, int readyTime, int duration, Semaphore cpuSempahore) {
        this.name = name;
        this.readyTime = readyTime;
        this.serviceTime = duration;
        this.state = ProcessState.NOTSTARTED;
        this.cpuSempahore = cpuSempahore;
        remainingTime = serviceTime;
    }

    /*================= Setters & Getters ================= */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(int readyTime) {
        this.readyTime = readyTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public int getTimeInCPU() {
        return timeInCPU;
    }

    public void setTimeInCPU(int timeInCPU) {
        this.timeInCPU = timeInCPU;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void decTimeLeft(){
        remainingTime = remainingTime - timeInCPU;
    }

    public void incTimeInCPU(){
        timeInCPU = timeInCPU + 100;
    }

    public CommandsData getCommandsData() {
        return commandsData;
    }

    public void setCommandsData(CommandsData commandsData) {
        this.commandsData = commandsData;
    }



    @Override
    public String toString() {
        return "Process{" +
                "name='" + name + '\'' +
                ", readyTime=" + readyTime +
                ", serviceTime=" + serviceTime +
                ", state=" + state +
                ", cpuTime=" + timeInCPU +
                ", timeLeft=" + remainingTime +
                '}';
    }

    @Override
    public void run() {
        ProcessState previousState = null;
        Boolean run = true;
        while(run){
            int cTime = Clock.INSTANCE.getTime();

            switch (state){
                case NOTSTARTED -> {
                    state = STARTED;
                }
                case STARTED, READY -> {
                    startTime = cTime;
                    System.out.println(String.format("Clock : %d , %s , Started", cTime, name));
                    try {
                        cpuSempahore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    state = RUNNING;
                    previousState = READY;
                }
                case RUNNING -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeInCPU += 100;
                    if(previousState == BLOCKED){
                        System.out.println(String.format("Clock : %d , %s , Resumed", cTime, name));
                        previousState = RUNNING;
                    }

                    if(previousState == RUNNING){
                        previousState = null;
                    }

                    try {
                        mmuSemaphore.acquire();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    executeCommands();
                    mmuSemaphore.release();

                    remainingTime -= timeInCPU;

                    if(timeInCPU == serviceTime){
                        state = FINISHED;
                    }
                }
                case BLOCKED -> {
                    previousState = BLOCKED;
                    if(remainingTime <= 0){
                        if(cTime >= startTime+serviceTime){
                            state = FINISHED;
                        }
                    }else{
                        System.out.println(String.format("Clock : %d , %s , Paused", cTime, name));
                        state = BLOCKED;
                    }
                }
                case FINISHED -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(String.format("Clock : %d , %s , Finished", cTime, name));
                    run = false;
                    cpuSempahore.release();
                }
                default -> throw new IllegalStateException("Unexpected value: " + state);
            }
        }
    }

    public synchronized void executeCommands(){
        Random rand = new Random();
        int low = 1;
        int high = 1000;
        int bound = (high-low) + 1;

        while(state == RUNNING){
            if(!commandsData.getCommands().isEmpty()){
                int commandRunTime = Math.min(remainingTime, rand.nextInt(high));
                if(commandRunTime <= remainingTime){
                    try {
                        commandSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String command = String.join(" ", commandsData.getCommands().poll());
                    MMU.command = command;
                    MMU.readNext = true;
                    timeInCPU += commandRunTime;
                    try {
                        Thread.sleep(commandRunTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int cTime = Clock.INSTANCE.getTime() + commandRunTime;

                    String result = MMU.result;
                    System.out.println(String.format("Clock : %d , %s , %s", cTime, name, result));
                    remainingTime -= commandRunTime;
                    commandSemaphore.release();
                    if(remainingTime == 0){
                        state = BLOCKED;
                    }
                }
            }else{
                state = FINISHED;
            }
        }
    }

    public enum ProcessState {
        NOTSTARTED,
        STARTED,
        READY,
        RUNNING,
        BLOCKED,
        FINISHED
    }
}