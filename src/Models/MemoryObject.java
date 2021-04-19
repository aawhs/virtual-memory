package Models;

import java.util.Comparator;

public class MemoryObject implements Comparable<MemoryObject> {
    private String id;
    private int value;
    private int timeAccessed;
    private boolean lock;

    public MemoryObject(String id, int value, int timeAccessed, boolean lock) {
        this.id = id;
        this.value = value;
        this.timeAccessed = timeAccessed;
        this.lock = lock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTimeAccessed() {
        return timeAccessed;
    }

    public void setTimeAccessed(int timeAccessed) {
        this.timeAccessed = timeAccessed;
    }

    public boolean isLock() {
        return lock;
    }

    public synchronized void setLock(boolean lock) {
        this.lock = lock;
    }

    public synchronized boolean isLocked(){
        return lock;
    }

    @Override
    public String toString() {
        String object = String.format("%s,%d,%d", id,value,timeAccessed);
        return object;
    }

    @Override
    public int compareTo(MemoryObject o) {
        return Integer.compare(this.timeAccessed, o.timeAccessed);
    }
}
