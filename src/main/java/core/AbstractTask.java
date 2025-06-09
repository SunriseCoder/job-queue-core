package core;

import java.util.Iterator;

public abstract class AbstractTask {
    protected Worker worker;

    protected AbstractTask parentTask;
    protected AbstractTask previousTask;
    protected AbstractTask nextTask;
    protected AbstractTask fistChildTask;

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public AbstractTask getParentTask() {
        return parentTask;
    }

    public void setParentTask(AbstractTask parentTask) {
        this.parentTask = parentTask;
    }

    public AbstractTask getPreviousTask() {
        return previousTask;
    }

    public void setPreviousTask(AbstractTask previousTask) {
        this.previousTask = previousTask;
    }

    public AbstractTask getNextTask() {
        return nextTask;
    }

    public void setNextTask(AbstractTask nextTask) {
        this.nextTask = nextTask;
    }

    public AbstractTask getFistChildTask() {
        return fistChildTask;
    }

    public Iterator<AbstractTask> getTaskIterator() {
        return new TaskIterator(this);
    }

    public Iterator<AbstractTask> getChildTaskIterator() {
        return new TaskIterator(fistChildTask);
    }

    public void addChild(AbstractTask childTask) {
        if (fistChildTask == null) {
            fistChildTask = childTask;
        } else {
            fistChildTask.setNextTask(childTask);
        }
    }

    public abstract void run();

    public abstract void rollback();
}
