package de.ufomc.config.benchmark;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

@UtilityClass
public final class Benchmark {

    /**
     * Used for getting the amount of memory this runtime is using at this moment.
     * @return amount of memory as long
     */
    private static long getUsedMemory() {
        final Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory(); //subtract free memory from total
    }

    private static long getGCCount() {
        long totalGCCount = 0;
        final List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
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
