package de.ufomc.config.benchmark;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

public class Benchmark {

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.freeMemory();
    }



    private static long getGCCount() {
        long totalGCCount = 0;
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            long count = gcBean.getCollectionCount();
            if (count != -1) {
                totalGCCount += count;
            }
        }
        return totalGCCount;
    }

    public static BenchmarkResult run(Runnable runnable, long iterations) {
        long memoryUsedBefore = getUsedMemory();

        long peakMemoryUsed = memoryUsedBefore;
        long startTime = System.nanoTime();

        for (long i = 0; i < iterations; i++) {
            runnable.run();

            long currentUsedMemory = getUsedMemory();
            if (currentUsedMemory > peakMemoryUsed) {
                peakMemoryUsed = currentUsedMemory;
            }
        }

        long endTime = System.nanoTime();
        long memoryUsedAfter = getUsedMemory();

        long totalTime = endTime - startTime;

        return new BenchmarkResult(totalTime, iterations, memoryUsedBefore, memoryUsedAfter);

    }

}
