package de.ufomc.config.benchmark;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter
@Setter
public class BenchmarkResult {

    private long totalTime;   // Gesamtzeit in Nanosekunden
    private long executionCount;  // Anzahl der Ausführungen
    private double averageTimePerExecution;  // Durchschnittliche Zeit pro Ausführung

    private String memoryUsed;

    private long memoryUsedBefore;  // Speicherverbrauch vor dem Benchmark
    private long memoryUsedAfter;   // Speicherverbrauch nach dem Benchmark

    public BenchmarkResult(long totalTime, long executionCount, long memoryUsedBefore, long memoryUsedAfter) {

        this.totalTime = totalTime;
        this.averageTimePerExecution = (double) totalTime / executionCount;
        this.executionCount = executionCount;

        this.memoryUsedBefore = memoryUsedBefore / (1024 * 1024);
        this.memoryUsedAfter = memoryUsedAfter / (1024 * 1024);

        this.memoryUsed = this.memoryUsedBefore - this.memoryUsedAfter + "mb\n";

    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append("benchmark results: \n\n");

        for (Field field : this.getClass().getDeclaredFields()){

            try {
                s.append(field.getName()).append(": ").append(field.get(this)).append("\n");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

        return s.toString();


    }
}