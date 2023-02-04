package org.example.java_learning;

/**
 *
 */
public class StreamsMain {
    public static void main(String[] args) {
        System.out.println("Streams theory START ---------------------");

        // Chapter 1
        System.out.println("simpleExampleStreamVsNonStream START ---------------------");
        StreamsIntro.simpleExampleStreamVsNonStream();
        System.out.println("lazyIntermediateOperations START ---------------------");
        StreamsIntro.StreamOperations.lazyIntermediateOperations();


        System.out.println("Streams theory END   ---------------------");
    }
}
