package dev.ufo.ufodata.core;

import lombok.NonNull;
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

    /**
     * Queues this runnable to be executed by a thread within our threadpool
     * @param runnable The runnable you want to execute.
     */
    public static void queue(final @NonNull Runnable runnable) {
        SERVICE.submit(runnable);
    }
}
