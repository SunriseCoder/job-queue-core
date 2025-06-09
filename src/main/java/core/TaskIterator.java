package core;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class TaskIterator implements Iterator<AbstractTask> {
    private AbstractTask task;

    public TaskIterator(AbstractTask task) {
        this.task = task;
    }

    @Override
    public boolean hasNext() {
        return task != null;
    }

    @Override
    public AbstractTask next() {
        if (task == null) {
            throw new NoSuchElementException();
        }

        AbstractTask currentTask = task;
        task = task.getNextTask();
        return currentTask;
    }
}
