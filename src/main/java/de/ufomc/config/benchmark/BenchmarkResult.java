package de.ufomc.config.benchmark;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter @Setter
public final class BenchmarkResult {
    private final long totalTime; //total process duration in nanoseconds
    private final long executionCount; //how many times did we run the given runnable
    private final double averageTimePerExecution; //average duration (totalTime / executionCount)
    private final long memoryUsedBefore; //runtime memory consumption BEFORE the process
    private final long memoryUsedAfter; //runtime memory consumption AFTER the process
    private final String memoryUsed; //a nicely formatted string representing the amount of memory the runtime used.

    public BenchmarkResult(long totalTime, long executionCount, long memoryUsedBefore, long memoryUsedAfter) {
        this.totalTime = totalTime;
        this.averageTimePerExecution = (double) totalTime / executionCount;
        this.executionCount = executionCount;

        this.memoryUsedBefore = memoryUsedBefore / (1024 * 1024); //Todo: ? (explain & make a static field for 1024^2)
        this.memoryUsedAfter = memoryUsedAfter / (1024 * 1024); //Todo: ? (explain & make a static field for 1024^2)

        //Todo: This is NOT how memory works, since the GC might be active during the Benchmark
        this.memoryUsed = this.memoryUsedBefore - this.memoryUsedAfter + "mb\n";
    }

    @Override
    public String toString() { //Todo: Where is this used? (explain)
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("benchmark results: \n\n");

        final Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (final Field field : fields) {
                stringBuilder.append(field.getName()).append(": ").append(field.get(this)).append("\n");
            }
        } catch (final IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return stringBuilder.toString();
    }
}