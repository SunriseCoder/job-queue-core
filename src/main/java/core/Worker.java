package core;

public class Worker implements Runnable {
    private volatile Thread thread;
    private final AbstractJobQueue queue;
    private volatile boolean finishCurrentTaskAndSelfDestroy;
    private volatile boolean isInterrupted;
    private volatile long checkForTheJobIntervalMillis;
    private volatile AbstractTask currentTask;

    public Worker(AbstractJobQueue queue, long checkForTheJobIntervalMillis) {
        this.queue = queue;
        this.checkForTheJobIntervalMillis = checkForTheJobIntervalMillis;
        finishCurrentTaskAndSelfDestroy = false;
        isInterrupted = false;
    }

    public void finishAndDestroy() {
        finishCurrentTaskAndSelfDestroy = true;
    }

    public void terminate() {
        isInterrupted = true;

        if (thread != null) {
            thread.interrupt();
        }

        queue.removeWorker(this);
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        currentTask = null;
        try {
            while (!finishCurrentTaskAndSelfDestroy && !isInterrupted) {
                currentTask = queue.pop();
                if (currentTask == null) {
                    Thread.sleep(checkForTheJobIntervalMillis);
                } else {
                    currentTask.setWorker(this);
                    currentTask.run();
                    currentTask = null;
                }
            }
        } catch (InterruptedException e) {
            isInterrupted = true;
        }

        queue.removeWorker(this);
    }

    public void setCheckForTheJobIntervalMillis(long checkForTheJobIntervalMillis) {
        this.checkForTheJobIntervalMillis = checkForTheJobIntervalMillis;
    }

    public AbstractTask getCurrentTask() {
        return currentTask;
    }
}
