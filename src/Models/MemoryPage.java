package Models;

public class MemoryPage {
    private int id;
    private MemoryObject memoryObject;

    public MemoryPage(int id, MemoryObject memoryObject) {
        this.id = id;
        this.memoryObject = memoryObject;
    }

    public MemoryPage(int id) {
        this.id = id;
        memoryObject = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MemoryObject getMemoryObject() {
        return memoryObject;
    }

    public void setMemoryObject(MemoryObject memoryObject) {
        this.memoryObject = memoryObject;
    }

    @Override
    public String toString() {
        return memoryObject.toString();
    }
}
