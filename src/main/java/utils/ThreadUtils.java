package utils;

public class ThreadUtils {

    public static void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // Just ignore the exception
        }
    }
}
