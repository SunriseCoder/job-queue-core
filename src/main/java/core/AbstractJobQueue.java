package core;

import java.util.ArrayList;
import java.util.List;

public class AbstractJobQueue {
    private static final long DEFAULT_CHECK_FOR_THE_JOB_INTERVAL_MILLIS = 1000;

    private volatile long checkForTheJobIntervalMillis;

    private final List<Worker> workers;
    private volatile int numberOfWorkers;
    private volatile AbstractTask firstTask;
    private volatile AbstractTask lastTask;

    public AbstractJobQueue() {
        workers = new ArrayList<Worker>();
        checkForTheJobIntervalMillis = DEFAULT_CHECK_FOR_THE_JOB_INTERVAL_MILLIS;
    }

    public synchronized void push(AbstractTask task) {
        if (firstTask == null) {
            firstTask = task;
        } else {
            lastTask.setNextTask(task);
        }

        lastTask = task;
    }

    public synchronized AbstractTask pop() {
        if (firstTask == null) {
            return null;
        } else {
            AbstractTask result = firstTask;
            firstTask = firstTask.getNextTask();
            return result;
        }
    }

    public synchronized int getNumberOfWorkers() {
        return numberOfWorkers;
    }

    public synchronized int getActualNumberOfWorkers() {
        return workers.size();
    }

    public synchronized void setNumberOfWorkersWaitToFinish(int numberOfWorkers) {
        addWorkersIfRequired(numberOfWorkers);
        for (int i = numberOfWorkers; i < workers.size(); i++) {
            workers.get(i).finishAndDestroy();
        }
    }

    public synchronized void setNumberOfWorkersWithTermination(int numberOfWorkers) {
        addWorkersIfRequired(numberOfWorkers);

        AbstractTask savedTaskChainFirstTask = null;
        AbstractTask savedTaskChainLastTask = null;
        // If the Workers to be terminated, are executing a Task,
        // trying to save the Tasks and put them back at the beginning of the Queue
        for (int i = numberOfWorkers; i < workers.size(); i++) {
            Worker worker = workers.get(i);
            AbstractTask currentTask = worker.getCurrentTask();

            if (currentTask != null) {
                currentTask.rollback();

                if (savedTaskChainFirstTask == null) {
                    savedTaskChainFirstTask = currentTask;
                }

                if (savedTaskChainLastTask != null) {
                    savedTaskChainLastTask.setNextTask(currentTask);
                    currentTask.setPreviousTask(savedTaskChainLastTask);
                }
                savedTaskChainLastTask = currentTask;
            }

            worker.terminate();
        }
        if (savedTaskChainFirstTask != null) {
            savedTaskChainLastTask.setNextTask(firstTask);
            firstTask.setPreviousTask(savedTaskChainLastTask);
            firstTask = savedTaskChainFirstTask;
        }
    }

    private void addWorkersIfRequired(int numberOfWorkers) {
        this.numberOfWorkers = numberOfWorkers;
        while (workers.size() < numberOfWorkers) {
            Worker worker = new Worker(this, checkForTheJobIntervalMillis);
            workers.add(worker);
            worker.start();
        }
    }

    public synchronized void setCheckForTheJobIntervalMillis(long checkForTheJobIntervalMillis) {
        this.checkForTheJobIntervalMillis = checkForTheJobIntervalMillis;
        for (Worker worker : workers) {
            worker.setCheckForTheJobIntervalMillis(checkForTheJobIntervalMillis);
        }
    }

    public synchronized void removeWorker(Worker worker) {
        workers.remove(worker);
    }
}
