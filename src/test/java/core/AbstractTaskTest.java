package core;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractTaskTest {

    @Test
    public void testParent() {
        DummyTask dummyTask = new DummyTask();
        DummyTask dummyTaskParent = new DummyTask();
        dummyTask.setParentTask(dummyTaskParent);

        assertSame(dummyTaskParent, dummyTask.getParentTask());
    }

    @Test
    public void testIterator() {
        DummyTask dummyTask = new DummyTask();
        Iterator<AbstractTask> iterator = dummyTask.getTaskIterator();
        assertTrue(iterator.hasNext());
        assertSame(dummyTask, iterator.next());

        // Checking that there is no element after the end
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testChildIterator() {
        // Checking that the task has no children at the beginning
        DummyTask dummyTask = new DummyTask();
        Iterator<AbstractTask> childTaskIterator = dummyTask.getChildTaskIterator();
        assertFalse(childTaskIterator.hasNext());

        // Adding child task
        DummyTask dummyTaskChild = new DummyTask();
        dummyTask.addChild(dummyTaskChild);

        // Checking that iterator returns the child task
        childTaskIterator = dummyTask.getChildTaskIterator();
        assertTrue(childTaskIterator.hasNext());
        assertSame(dummyTaskChild, childTaskIterator.next());

        // And the iterator should not return anything after it
        assertFalse(childTaskIterator.hasNext());
    }
}
