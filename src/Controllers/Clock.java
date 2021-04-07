package Controllers;

public enum Clock implements Runnable{
    INSTANCE(1000);
    private int time;
    private boolean state;

    Clock(int i){
        time = i;
        state = true;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public void run() {
        while(state){
            try{
                Thread.sleep(100);
            }catch (Exception e){
                System.out.println("Exception in Clock Thread");
                e.printStackTrace();
            }
            time = time + 10;
        }
    }
}
