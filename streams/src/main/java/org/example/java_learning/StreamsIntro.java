package org.example.java_learning;

public class StreamsIntro {
    /*
    Streams behave like collections but have several advantages that enable new styles of programming. First, like database-query language such as SQL, it enables queries to be written in a few lines that would take many lines in Java. Second, streams are designed so that not all their data needs to be in memory (or even computed) at once. Thus, it's possible to process streams that are too big to fit in computer memory. Java 8 can optimize operations on streams in a way that Java can’t do for collections — for example, it can group together several operations on the same stream so that the data is traversed only once instead of expensively traversing it multiple times. Even better, Java can automatically parallelize stream operations (unlike collections), as a result, it avoids the need to write code that uses synchronized, which is not only highly error-prone but also more expensive on multicore CPUs.
    The addition of Streams in Java 8 can be seen as a direct cause of the two other additions to Java 8: concise techniques to pass code to methods (method references, lambdas) and default methods in interfaces.

    */
}
