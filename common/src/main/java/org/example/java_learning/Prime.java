package org.example.java_learning;

import java.util.List;
import java.util.stream.IntStream;

public class Prime {
    public static boolean isPrime(int candidate) {
        return IntStream
            // Generates a range of natural numbers starting from and including 2, up to but excluding candidate.
            .range(2, candidate)
                // Returns true if the candidate isn’t divisible for any of the numbers in the stream.
                .noneMatch(i -> candidate % i == 0);
    }

    public static boolean isPrimeLittleOptimized(int candidate) {
        return IntStream.rangeClosed(2, candidate - 1)
                .limit((long) Math.floor(Math.sqrt(candidate)) - 1)
                .noneMatch(i -> candidate % i == 0);
    }

    public static boolean isPrimePerformant(List<Integer> primes, int candidate){
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return primes.stream()
            .takeWhile(i -> i <= candidateRoot)
            .noneMatch(i -> candidate % i == 0);
    }
}
