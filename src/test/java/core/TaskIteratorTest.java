package core;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class TaskIteratorTest {

    @Test
    public void testTaskIterator() {
        DummyTask dummyTask = new DummyTask();
        Iterator<AbstractTask> iterator = dummyTask.getTaskIterator();
        assertTrue(iterator.hasNext());
        assertSame(dummyTask, iterator.next());

        // Checking that there is no element after the end
        assertFalse(iterator.hasNext());
    }
}
