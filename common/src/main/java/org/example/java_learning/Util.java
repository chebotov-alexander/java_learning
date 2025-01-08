package org.example.java_learning;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


import static java.lang.Double.parseDouble;
import static java.lang.System.currentTimeMillis;

public class Util {
    private static final Random RANDOM = new Random(0);
    private static final long DELAY_MILLIS = 1000;
    private static final DecimalFormat formatter = new DecimalFormat(
"#.##",
        new DecimalFormatSymbols(Locale.US)
    );

    public static void println(Object obj) {
        System.out.println(obj);
    }

    public static void print(Object obj) {
        System.out.print(obj);
    }

    public static void dumbLoopForMillis(long durationInMillis) {
        long end = currentTimeMillis() + durationInMillis;
        while (currentTimeMillis() < end) {}
    }

    public static void delay() {
        //long delay = 500 + RANDOM.nextInt(2000);
        delay(DELAY_MILLIS);
    }

    public static void randomDelay() {
        delay(500 + RANDOM.nextInt(2000));
    }

    public static void delay(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static double format(double number) {
        synchronized (formatter) {
            return parseDouble(formatter.format(number));
        }
    }

    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
    /*
        CompletableFuture<Void> allDoneFuture =
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
            futures.stream()
                .map(future -> future.join())
                .collect(Collectors.<T>toList())
        );
    */
        return CompletableFuture.supplyAsync(() -> futures.stream()
            .map(future -> future.join())
            .collect(Collectors.<T>toList()));
    }
}
