package de.ufomc.config.benchmark;

public class Benchmark {

    public static long benchMark(Runnable runnable) {

        long start = System.nanoTime();
        runnable.run();
        return System.nanoTime() - start;

    }

}
