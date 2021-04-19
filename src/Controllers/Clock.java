package Controllers;

public enum Clock implements Runnable{
    INSTANCE(0);
    private int time;
    private boolean state;
    private boolean lag;
    private static final int MS_S  = 1000;

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
            time = time + 100;
        }
    }
}
