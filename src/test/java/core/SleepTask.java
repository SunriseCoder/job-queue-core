package core;

import java.util.concurrent.atomic.AtomicInteger;

public class SleepTask extends AbstractTask {
    private final long sleepTime;

    private volatile boolean isStarted = false;
    private volatile boolean isFinished = false;
    private volatile boolean isInterrupted = false;
    private volatile boolean isFailed = false;
    private final AtomicInteger finishedTimes;
    private volatile String threadName;

    public SleepTask(long sleepTime) {
        this.sleepTime = sleepTime;
        finishedTimes = new AtomicInteger(0);
    }

    @Override
    public void run() {
        isStarted = true;
        isInterrupted = false;
        isFailed = false;
        isFinished = false;

        threadName = Thread.currentThread().getName();

        try {
            Thread.sleep(sleepTime);
            isFinished = true;
            finishedTimes.incrementAndGet();
        } catch (InterruptedException e) {
            isInterrupted = true;
        } catch (Exception e) {
            isFailed = true;
        }
    }

    @Override
    public void rollback() {
        isStarted = false;
        threadName = null;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public int getFinishedTimes() {
        return finishedTimes.get();
    }

    public String getThreadName() {
        return threadName;
    }
}
