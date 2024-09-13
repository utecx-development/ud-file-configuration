package de.ufomc.config.core;

import de.ufomc.config.io.Config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueuedAsyncExecution {

    private static final List<Runnable> tasks = new LinkedList<>();
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static boolean isExecuting = false;

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

    private static void taskFinished() {
        isExecuting = false;

        if (!tasks.isEmpty()) {
            executeNext();
        } else {
            shutdown();
        }
    }

    public static void shutdown() {
        executorService.shutdown();
    }
}
