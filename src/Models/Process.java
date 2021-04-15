package Models;

import Controllers.Clock;

import java.util.concurrent.Semaphore;

public class Process implements Runnable{
    /*================= Data Members ================= */
    String name;
    int readyTime;
    int serviceTime;
    State state;
    Semaphore semaphore;

    int cpuTime;
    int timeLeft;


    /*================= Constructor ================= */
    public Process(String name, int readyTime, int duration) {
        this.name = name;
        this.readyTime = readyTime;
        this.serviceTime = duration;
        this.state = State.NOTSTARTED;
        this.semaphore = new Semaphore(1);
        timeLeft = serviceTime*1000;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public void setCpuTime(int cpuTime) {
        this.cpuTime = cpuTime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    public String toString() {
        return "Process{" +
                "name='" + name + '\'' +
                ", readyTime=" + readyTime +
                ", serviceTime=" + serviceTime +
                ", state=" + state +
                ", cpuTime=" + cpuTime +
                ", timeLeft=" + timeLeft +
                '}';
    }

    @Override
    public void run() {
        int clock = Clock.INSTANCE.getTime();
        System.out.println(clock + " : " + name + " Thread has started");
        if(state == State.READY){
            try {
                System.out.println(clock + " : " + name + " is waiting to acquire lock");
                semaphore.acquire();
                System.out.println(clock + " : " + name + " Acquired lock");

                state = State.RUNNING;

                while(state == State.RUNNING){
                    if(cpuTime <= (serviceTime*1000)){
                        System.out.println(clock + " : " + name + " is executing");
                        System.out.println(clock + " : " + name + toString());
                        cpuTime = cpuTime + 100;
                        timeLeft = (serviceTime*1000) - cpuTime;
                        System.out.println(clock + " : " + name + toString());
                    }
                    if((cpuTime/1000) == serviceTime){
                        System.out.println(clock + " : " + name + " finished executing");
                        state = State.FINISHED;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(name + " released lock");
            semaphore.release();
        }

    }
}


