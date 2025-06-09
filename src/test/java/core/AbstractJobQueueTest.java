package core;

import org.junit.jupiter.api.Test;
import utils.ThreadUtils;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractJobQueueTest {

    @Test
    public void testPushPopSimple() {
        // Testing that the queue is empty after creation
        AbstractJobQueue queue = new AbstractJobQueue();
        queue.setCheckForTheJobIntervalMillis(10);
        assertNull(queue.pop());

        // Testing a simple task pushing and retrieving
        DummyTask originalTask = new DummyTask();
        queue.push(originalTask);
        DummyTask retrievedTask = (DummyTask) queue.pop();
        assertSame(originalTask, retrievedTask);

        // Testing that the queue is empty after retrieving the only task
        assertNull(queue.pop());
    }

    @Test
    public void testPushPopThreadSafe() {
        AbstractJobQueue queue = new AbstractJobQueue();
        queue.setCheckForTheJobIntervalMillis(10);
        queue.setNumberOfWorkersWaitToFinish(2);
        ThreadUtils.sleep(100);

        SleepTask task1 = new SleepTask(100);
        SleepTask task2 = new SleepTask(200);
        SleepTask task3 = new SleepTask(100);
        queue.push(task1);
        queue.push(task2);
        queue.push(task3);
        ThreadUtils.sleep(300);

        assertTrue(task1.isStarted());
        assertTrue(task1.isFinished());
        assertEquals(1, task1.getFinishedTimes());

        assertTrue(task2.isStarted());
        assertTrue(task2.isFinished());
        assertEquals(1, task2.getFinishedTimes());

        assertTrue(task3.isStarted());
        assertTrue(task3.isFinished());
        assertEquals(1, task3.getFinishedTimes());

        assertNotEquals(task1.getThreadName(), task2.getThreadName());
        assertNotEquals(task2.getThreadName(), task3.getThreadName());
    }

    @Test
    public void testTrimWorkerPoolWaitingSoftlyToFinish() {
        // Creating a Queue with 3 Workers, Worker request interval is 10 ms
        AbstractJobQueue queue = new AbstractJobQueue();
        queue.setCheckForTheJobIntervalMillis(10);
        queue.setNumberOfWorkersWaitToFinish(3);
        ThreadUtils.sleep(100);

        // Creating tasks
        SleepTask task1 = new SleepTask(100);
        SleepTask task2 = new SleepTask(100);
        SleepTask task3 = new SleepTask(100);
        SleepTask task4 = new SleepTask(100);
        SleepTask task5 = new SleepTask(100);
        SleepTask task6 = new SleepTask(100);
        queue.push(task1);
        queue.push(task2);
        queue.push(task3);
        queue.push(task4);
        queue.push(task5);
        queue.push(task6);

        // Wait for the Tasks to be started (3 Tasks by 3 Workers, 3 Tasks are waiting)
        ThreadUtils.sleep(30);
        assertTrue(task1.isStarted());
        assertTrue(task2.isStarted());
        assertTrue(task3.isStarted());
        assertFalse(task4.isStarted());
        assertFalse(task5.isStarted());
        assertFalse(task6.isStarted());
        assertFalse(task1.isFinished());
        assertFalse(task2.isFinished());
        assertFalse(task3.isFinished());
        assertFalse(task4.isFinished());
        assertFalse(task5.isFinished());
        assertFalse(task6.isFinished());

        // Checking that we have 3 Workers
        assertEquals(3, queue.getActualNumberOfWorkers());

        // Waiting a bit more and asking 1 Worker to shut down after it finish its Task
        ThreadUtils.sleep(20);
        queue.setNumberOfWorkersWaitToFinish(2);
        ThreadUtils.sleep(20);

        // After 70 ms all the Tasks are not finished yet,
        // so we are checking that the Worker was not killed immediately
        // We will check the completeness of all the Tasks later
        assertEquals(3, queue.getActualNumberOfWorkers());
        assertTrue(task1.isStarted());
        assertTrue(task2.isStarted());
        assertTrue(task3.isStarted());
        assertFalse(task4.isStarted());
        assertFalse(task5.isStarted());
        assertFalse(task6.isStarted());
        assertFalse(task1.isFinished());
        assertFalse(task2.isFinished());
        assertFalse(task3.isFinished());
        assertFalse(task4.isFinished());
        assertFalse(task5.isFinished());
        assertFalse(task6.isFinished());

        // Waiting for the first 3 Tasks to be finished
        ThreadUtils.sleep(50);

        // Checking that we have only 2 Workers left
        assertEquals(2, queue.getActualNumberOfWorkers());

        // Checking that the last Task is NOT started yet, because
        // We finished first 3 Tasks already and Task 4 and 5 are in progress,
        // and the Task 6 should wait for its turn
        assertTrue(task1.isStarted());
        assertTrue(task2.isStarted());
        assertTrue(task3.isStarted());
        assertTrue(task4.isStarted());
        assertTrue(task5.isStarted());
        assertFalse(task6.isStarted());
        assertTrue(task1.isFinished());
        assertTrue(task2.isFinished());
        assertTrue(task3.isFinished());
        assertFalse(task4.isFinished());
        assertFalse(task5.isFinished());
        assertFalse(task6.isFinished());

        // Waiting for all the Tasks to finish
        ThreadUtils.sleep(200);

        // Checking that all the Tasks are finished
        assertTrue(task1.isStarted());
        assertTrue(task2.isStarted());
        assertTrue(task3.isStarted());
        assertTrue(task4.isStarted());
        assertTrue(task5.isStarted());
        assertTrue(task6.isStarted());
        assertTrue(task1.isFinished());
        assertTrue(task2.isFinished());
        assertTrue(task3.isFinished());
        assertTrue(task4.isFinished());
        assertTrue(task5.isFinished());
        assertTrue(task6.isFinished());
    }

    @Test
    public void testTrimWorkerPoolWithTermination() {
        // Creating a Queue with 3 Workers, Worker request interval is 10 ms
        AbstractJobQueue queue = new AbstractJobQueue();
        queue.setCheckForTheJobIntervalMillis(10);
        queue.setNumberOfWorkersWaitToFinish(3);
        ThreadUtils.sleep(100);

        // Creating tasks
        SleepTask task1 = new SleepTask(100);
        SleepTask task2 = new SleepTask(100);
        SleepTask task3 = new SleepTask(100);
        SleepTask task4 = new SleepTask(100);
        SleepTask task5 = new SleepTask(100);
        SleepTask task6 = new SleepTask(100);
        queue.push(task1);
        queue.push(task2);
        queue.push(task3);
        queue.push(task4);
        queue.push(task5);
        queue.push(task6);

        // Wait for the Tasks to be started (3 Tasks by 3 Workers, 3 Tasks are waiting)
        ThreadUtils.sleep(30);
        assertTrue(task1.isStarted());
        assertTrue(task2.isStarted());
        assertTrue(task3.isStarted());
        assertFalse(task4.isStarted());
        assertFalse(task5.isStarted());
        assertFalse(task6.isStarted());
        assertFalse(task1.isFinished());
        assertFalse(task2.isFinished());
        assertFalse(task3.isFinished());
        assertFalse(task4.isFinished());
        assertFalse(task5.isFinished());
        assertFalse(task6.isFinished());

        // Checking that we have 3 Workers
        assertEquals(3, queue.getActualNumberOfWorkers());

        // Waiting a bit more and asking 1 Worker to shut down after it finish its Task
        ThreadUtils.sleep(20);
        queue.setNumberOfWorkersWithTermination(2);
        ThreadUtils.sleep(20);

        // 70ms from start
        // After 70 ms all the Tasks are not finished yet,
        // so we are checking that the Worker was killed immediately
        // and the Task was returned back to the Queue
        // We will check the completeness of all the Tasks later
        assertEquals(2, queue.getActualNumberOfWorkers());

        // Here we expect that no Tasks was finished yet
        // 3 Tasks has been started and 1 Task should be rolled back
        // So actually there should be 2 Tasks that has been started
        int startedTasks = 0;
        startedTasks += task1.isStarted() ? 1 : 0;
        startedTasks += task2.isStarted() ? 1 : 0;
        startedTasks += task3.isStarted() ? 1 : 0;
        assertEquals(2, startedTasks);

        assertFalse(task4.isStarted());
        assertFalse(task5.isStarted());
        assertFalse(task6.isStarted());

        // No tasks should be finished at this moment (at 70 ms after the start)
        assertFalse(task1.isFinished());
        assertFalse(task2.isFinished());
        assertFalse(task3.isFinished());
        assertFalse(task4.isFinished());
        assertFalse(task5.isFinished());
        assertFalse(task6.isFinished());

        // Waiting for the first 3 (actually 2 due to one Worker termination) Tasks to be finished
        ThreadUtils.sleep(50);

        // 120 ms from start
        // Checking that we have only 2 Workers left
        assertEquals(2, queue.getActualNumberOfWorkers());

        // Checking that the last 2 Tasks are NOT started yet, because
        // We finished first 2 Tasks already and the Tasks 3 and 4 are in progress,
        // and the Tasks 5 and 6 should wait for their turn
        assertTrue(task1.isStarted());
        assertTrue(task2.isStarted());
        assertTrue(task3.isStarted());
        assertTrue(task4.isStarted());
        assertFalse(task5.isStarted());
        assertFalse(task6.isStarted());

        // Here we expect that one of the Tasks (we don't know which one exactly)
        // was NOT finished, but was returned back to the queue
        int finishedTasks = 0;
        finishedTasks += task1.isFinished() ? 1 : 0;
        finishedTasks += task2.isFinished() ? 1 : 0;
        finishedTasks += task3.isFinished() ? 1 : 0;
        assertEquals(2, finishedTasks);

        assertFalse(task4.isFinished());
        assertFalse(task5.isFinished());
        assertFalse(task6.isFinished());

        // Waiting for all the Tasks to finish
        ThreadUtils.sleep(200);

        // 320 ms from start
        // Checking that all the Tasks are finished
        assertTrue(task1.isStarted());
        assertTrue(task2.isStarted());
        assertTrue(task3.isStarted());
        assertTrue(task4.isStarted());
        assertTrue(task5.isStarted());
        assertTrue(task6.isStarted());
        assertTrue(task1.isFinished());
        assertTrue(task2.isFinished());
        assertTrue(task3.isFinished());
        assertTrue(task4.isFinished());
        assertTrue(task5.isFinished());
        assertTrue(task6.isFinished());
    }
}
