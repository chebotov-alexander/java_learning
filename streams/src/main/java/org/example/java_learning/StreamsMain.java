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
        System.out.println("testSkippingWithLimit START ---------------------");
        // Chapter 2
        StreamMethods.SlicingStream.testSkippingWithLimit();
        System.out.println("testGetUniqueCharactersArentThey START ---------------------");
        StreamMethods.MappingStream.testGetUniqueCharactersArentThey();
        System.out.println("testGetUniqueCharacters START ---------------------");
        StreamMethods.MappingStream.testGetUniqueCharacters();
        System.out.println("collectStreams START ---------------------");
        StreamMethods.CollectStreams.collectStreams();
        // Could be resource intensive, uncomment when needed.
        //System.out.println("testCollectorsPerformances START ---------------------");
        //StreamMethods.CollectStreams.testCollectorsPerformances();
        // Chapter 3
        System.out.println("testCorrectnessSideEffect START ---------------------");
        StreamParallel.testCorrectnessSideEffect();
        System.out.println("testForkJoinSum START ---------------------");
        StreamParallel.testForkJoinSum();

        System.out.println("Streams theory END   ---------------------");
    }
}
