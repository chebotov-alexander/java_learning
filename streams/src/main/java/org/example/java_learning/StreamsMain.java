package org.example.java_learning;

/**
 *
 */
public class StreamsMain {
    public static void main(String[] args) {

        System.out.println("Streams theory START---------------------");

        // Chapter 1
        System.out.println("simpleExampleStreamVsNonStream" + Const.printlnMainDelimeter);
        StreamsIntro.simpleExampleStreamVsNonStream();
        System.out.println("lazyIntermediateOperations" + Const.printlnMainDelimeter);
        StreamsIntro.StreamOperations.lazyIntermediateOperations();
        System.out.println("testSkippingWithLimit" + Const.printlnMainDelimeter);
        // Chapter 2
        StreamMethods.SlicingStream.testSkippingWithLimit();
        System.out.println("testGetUniqueCharactersArentThey" + Const.printlnMainDelimeter);
        StreamMethods.MappingStream.testGetUniqueCharactersArentThey();
        System.out.println("testGetUniqueCharacters" + Const.printlnMainDelimeter);
        StreamMethods.MappingStream.testGetUniqueCharacters();
        System.out.println("collectStreams" + Const.printlnMainDelimeter);
        StreamMethods.CollectStreams.collectStreams();
        // Could be resource intensive, uncomment when needed.
        //System.out.println("testCollectorsPerformances" + Const.printlnMainDelimeter);
        //StreamMethods.CollectStreams.testCollectorsPerformances();
        // Chapter 3
        System.out.println("testCorrectnessSideEffect" + Const.printlnMainDelimeter);
        StreamsParallel.testCorrectnessSideEffect();
        System.out.println("testForkJoinSum" + Const.printlnMainDelimeter);
        StreamsParallel.testForkJoinSum();
        System.out.println("testSpliterator" + Const.printlnMainDelimeter);
        StreamsParallel.testSpliterator();
        StreamsParallel.testSpliteratorParallel();
        System.out.println("Streams theory END---------------------");
    }
}
