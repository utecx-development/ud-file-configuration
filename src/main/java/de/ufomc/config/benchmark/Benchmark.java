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

    /**
     * Counts the amount of objects removed by the GC
     * @return gc count
     */
    private static long getGCCount() {
        final List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
        long totalGCCount = 0;
        for (final GarbageCollectorMXBean bean : beans) {
            final long count = bean.getCollectionCount();
            if (count >= 0) {
                totalGCCount += count;
            }
        }
        return totalGCCount;
    }

    @NonNull
    public static BenchmarkResult run(final Runnable runnable, final long iterations) {
        final long memoryUsedBefore = getUsedMemory(); //runtime memory before the benchmark
        long peakMemoryUsed = memoryUsedBefore;

        //run tests & track time
        final long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            runnable.run();

            final long usedMemory = getUsedMemory();
            if (usedMemory > peakMemoryUsed) { //change if memory limit has been stepped up
                peakMemoryUsed = usedMemory;
            }
        }
        final long totalTime = System.nanoTime() - startTime; //benchmark duration in nanoseconds

        final long memoryUsedAfter = getUsedMemory(); //runtime memory after the benchmark

        return new BenchmarkResult(totalTime, iterations, memoryUsedBefore, memoryUsedAfter);
    }
}
