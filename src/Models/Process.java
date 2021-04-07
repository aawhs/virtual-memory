package Models;

public class Process {
    /*================= Data Members ================= */
    String name;
    int readyTime;
    int serviceTime;
    State state;

    int cpuTime;
    int timeLeft;


    /*================= Constructor ================= */
    public Process(String name, int readyTime, int duration) {
        this.name = name;
        this.readyTime = readyTime;
        this.serviceTime = duration;
        this.state = State.NOTSTARTED;
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

    /*================= Helper Methods ================= */
    @Override
    public String toString() {
        return "Process{" +
                "name='" + name + '\'' +
                ", readyTime=" + readyTime +
                ", serviceTime=" + serviceTime +
                '}';
    }
}


