package dev.ufo.ufodata.core;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class QueuedAsyncExecution {
    private static final ExecutorService SERVICE;

    static { //initialize our thread pool (min 1, max 8 threads, can hold 100 tasks, free idle threads after 30 seconds)
        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100); //only 100 tasks can be queued at once
        SERVICE = new ThreadPoolExecutor(1, 8, 30, TimeUnit.SECONDS, queue);
    }

    public static synchronized void queue(Runnable runnable) {
        tasks.add(runnable);

        if (!isExecuting) {
            executeNext();
        }
    }

    private static void executeNext() {
        Runnable task = tasks.get(0);
        tasks.remove(0);

        if (task != null) {
            isExecuting = true;

            executorService.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    throw new RuntimeException("Error during a task in the que", e);
                } finally {
                    taskFinished();
                }
            });
        }
    }
}
