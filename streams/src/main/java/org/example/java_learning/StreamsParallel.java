package org.example.java_learning;

import java.util.Spliterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamsParallel {
    /*
    A parallel (parallelStream) stream is a stream that splits its elements into multiple chunks, processing each chunk with a different thread. Thus, you can automatically partition the workload of a given operation on all the cores of your multicore processor and keep all of them equally busy.
    Note that, in reality, calling the method parallel on a sequential stream doesn’t imply any concrete transformation on the stream itself. Internally, a boolean flag is set to signal that you want to run in parallel all the operations that follow the invocation to parallel. Similarly, you can turn a parallel stream into a sequential one by invoking the method sequential on it. Note that you might think that by combining these two methods you could achieve finer-grained control over which operations you want to perform in parallel and which ones sequentially while traversing the stream. For example, you could do something like the following:
        stream.parallel()
            .filter(...)
            .sequential()
            .map(...)
            .parallel()
            .reduce();
    But the last call to parallel or sequential wins and affects the pipeline globally. In this example, the pipeline will be executed in parallel because that’s the last call in the pipeline.

    Measuring stream performance.
    When optimizing performance, you should always follow three golden rules: measure, measure, measure. To this purpose we will implement a microbenchmark using a library called Java Microbenchmark Harness (JMH). This is a toolkit that helps to create, in a simple, annotation-based way, reliable microbenchmarks for Java programs and for any other language targeting the Java Virtual Machine (JVM). In fact, developing correct and meaningful benchmarks for programs running on the JVM is not an easy task, because there are many factors to consider like the warm-up time required by HotSpot to optimize the bytecode and the overhead introduced by the garbage collector. If you’re using Maven as your build tool, then to start using JMH in your project you add a couple of dependencies to your pom.xml file (which defines the Maven build process).
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-core</artifactId>
            <version>1.17.4</version>
        </dependency>
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-generator-annprocess</artifactId>
            <version>1.17.4</version>
        </dependency>
    The first library is the core JMH implementation while the second contains an annotation processor that helps to generate a Java Archive (JAR) file through which you can conveniently run your benchmark once you have also added the following plugin to your Maven configuration:
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-shade-plugin</artifactId>
                    <version>3.1.1</version>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>shade</goal>
                            </goals>
                            <configuration>
                                <finalName>benchmarks</finalName>
                                <transformers>
                                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                        <mainClass>org.openjdk.jmh.Main</mainClass>
                                    </transformer>
                                </transformers>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    Compile class [ParallelStreamBenchmark] in module [streams_benchmarks] with the Maven plugin and run:
        java -jar ./target/benchmarks.jar ParallelStreamBenchmark
    The benchmark configured to use an oversized heap to avoid any influence of the garbage collector as much as possible, and for the same reason, also to enforce the garbage collector to run after each iteration of our benchmark. Despite all these precautions, it has to be noted that the results should be taken with a grain of salt. Many factors will influence the execution time, such as how many cores your machine supports.
    The output looks like this:
        Benchmark                                  Mode  Cnt   Score   Error  Units
        ParallelStreamBenchmark.iterativeSum       avgt    4   2.427 ± 0.167  ms/op
        ParallelStreamBenchmark.parallelRangedSum  avgt    4   0.335 ± 0.533  ms/op
        ParallelStreamBenchmark.parallelSum        avgt    4  65.753 ± 6.729  ms/op
        ParallelStreamBenchmark.rangedSum          avgt    4   4.862 ± 0.186  ms/op
        ParallelStreamBenchmark.sequentialSum      avgt    4  53.635 ± 1.534  ms/op
      You should expect that the iterative version [ParallelStreamBenchmark.iterativeSum] using a traditional for loop runs much faster than sequential iterative stream version [ParallelStreamBenchmark.sequentialSum] because it works at a much lower level and, more important, doesn’t need to perform any boxing or unboxing of the primitive values.
      The version using the parallel iterative stream [ParallelStreamBenchmark.parallelSum] is quite disappointing: the parallel version of the summing method isn’t taking any advantage of all CPU cores. Two issues are mixed together: first - iterate generates boxed objects, which have to be unboxed to numbers before they can be added; second - iterate is difficult to divide into independent chunks to execute in parallel. The second issue is particularly interesting because you need to keep a mental model that some stream operations are more parallelizable than others. Specifically, the iterate operation is hard to split into chunks that can be executed independently, because the input of one function application always depends on the result of the previous application. This means that in this specific case the reduction process isn’t proceeding as one could expect: the whole list of numbers isn’t available at the beginning of the reduction process, making it impossible to efficiently partition the stream in chunks to be processed in parallel. By flagging the stream as parallel, you’re adding the overhead of allocating each sum operation on a different thread to the sequential processing. This demonstrates how parallel programming can be tricky and sometimes counterintuitive. When misused (for example, using an operation that’s not parallelfriendly, like iterate) it can worsen the overall performance of your programs, so it’s mandatory to understand what happens behind the scenes when you invoke that apparently magic parallel method.
      LongStream.rangeClosed method in has two benefits compared to iterate: first - works on primitive long numbers directly so there’s no boxing and unboxing overhead; second - produces ranges of numbers, which can be easily split into independent chunks. For example, the range 1–20 can be split into 1–5, 6–10, 11–15, and 16–20. So [ParallelStreamBenchmark.rangedSum] numeric stream is much faster than the earlier sequential version, generated with the iterate factory method, because the numeric stream avoids all the overhead caused by all the unnecessary autoboxing and auto-unboxing operations performed by the nonspecialized stream. This is evidence that choosing the right data structures is often more important than parallelizing the algorithm that uses them. But what happens if you try to use a parallel stream in this new version that follows?
      [ParallelStreamBenchmark.parallelRangedSum] demonstrates that using the right data structure and then making it work in parallel guarantees the best performance. When used correctly, the functional-programming style allows us to use the parallelism of modern multicore CPUs in a simpler and more straightforward way than its imperative counterpart.

      Parallelization doesn’t come for free. The parallelization process itself requires you to recursively partition the stream, assign the reduction operation of each substream to a different thread, and then combine the results of these operations in a single value. But moving data between multiple cores is also more expensive than you might expect, so it’s important that work to be done in parallel on another core takes longer than the time required to transfer the data from one core to another. In general, there are many cases where it isn’t possible or convenient to use parallelization.
    */

    //Using parallel streams correctly.
    //Before you use a parallel stream to make your codefaster, you have to be sure that you’re using it correctly; it’s not helpful to produce a result in less time if the result will be wrong. Let’s look at a common pitfalls.
    //The main cause of errors generated by misuse of parallel streams is the use of algorithms that mutate some shared state. Here’s a way to implement the sum of the first n natural numbers by mutating a shared accumulator:
    public static class Accumulator {
        private long total = 0;
        public void add(long value) {
            total += value;
        }
    }
    public static long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).forEach(accumulator::add);
        return accumulator.total;
    }
    // It’s quite common to write this sort of code, especially for developers who are familiar with imperative programming paradigms. This code closely resembles what you’re used to doing when iterating imperatively a list of numbers: you initialize an accumulator and traverse the elements in the list one by one, adding them on the accumulator.
    // What’s wrong with this code? Unfortunately, it’s irretrievably broken because it’s fundamentally sequential. You have a data race on every access of total. And if you try to fix that with synchronization, you’ll lose all your parallelism. To understand this, let’s try to turn the stream into a parallel one:
    public static long sideEffectParallelSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }
    // Check the results it gives:
    public static void testCorrectnessSideEffect() {
        System.out.println("SideEffect sum done in: "
            + measurePerf(StreamsParallel::sideEffectSum, 10_000_000L) + " msecs" );
        System.out.println("SideEffect parallel sum done in: "
            + measurePerf(StreamsParallel::sideEffectParallelSum, 10_000_000L) + " msecs" );
    }
    // This time the performance of your method isn’t important. The only relevant thing is that each execution returns a different result, all distant from the correct value of 50000005000000. This is caused by the fact that multiple threads are concurrently accessing the accumulator and, in particular, executing total += value, which, despite its appearance, isn’t an atomic operation. The origin of the problem is that the method invoked inside the forEach block has the side effect of changing the mutable state of an object shared among multiple threads. It’s mandatory to avoid these kinds of situations if you want to use parallel streams without incurring similar bad surprises. Now you know that a shared mutable state doesn’t play well with parallel streams and with parallel computations in general.

    /*
    Using parallel streams effectively.
    In general, it’s impossible (and pointless) to try to give any quantitative hint on when to use a parallel stream, because any specific criterion such as “only when the stream contains more than a thousand elements” could be correct for a specific operation running on a specific machine, but completely wrong in a marginally different context. Nonetheless, it’s at least possible to provide some qualitative advice that could be useful when deciding whether it makes sense to use a parallel stream in a certain situation:
      - If in doubt, measure. Turning a sequential stream into a parallel one is trivial but not always the right thing to do. As already demonstrated in this section, a parallel stream isn’t always faster than the corresponding sequential version. Moreover, parallel streams can sometimes work in a counterintuitive way, so the first and most important suggestion when choosing between sequential and parallel streams is to always check their performance with an appropriate benchmark.
      - Watch out for boxing. Automatic boxing and unboxing operations can dramatically hurt performance. Java 8 includes primitive streams (IntStream , LongStream, and DoubleStream) to avoid such operations, so use them when possible.
      - Some operations naturally perform worse on a parallel stream than on a sequential stream. In particular, operations such as limit and findFirst that rely on the order of the elements are expensive in a parallel stream. For example, findAny will perform better than findFirst because it isn’t constrained to operate in the encounter order. You can always turn an ordered stream into an unordered stream by invoking the method unordered on it. For instance, if you need N elements of your stream and you’re not necessarily interested in the first N ones, calling limit on an unordered parallel stream may execute more efficiently than on a stream with an encounter order (for example, when the source is a List).
      - Consider the total computational cost of the pipeline of operations performed by the stream. With N being the number of elements to be processed and Q the approximate cost of processing one of these elements through the stream pipeline, the product of N*Q gives a rough qualitative estimation of this cost. A higher value for Q implies a better chance of good performance when using a parallel stream.
      - For a small amount of data, choosing a parallel stream is almost never a winning decision. The advantages of processing in parallel only a few elements aren’t enough to compensate for the additional cost introduced by the parallelization process.
      - The characteristics of a stream, and how the intermediate operations through the pipeline modify them, can change the performance of the decomposition process. For example, a SIZED stream can be divided into two equal parts, and then each part can be processed in parallel more effectively, but a filter operation can throw away an unpredictable number of elements, making the size of the stream itself unknown.
      - Consider whether a terminal operation has a cheap or expensive merge step (for example, the combiner method in a Collector). If this is expensive, then the cost caused by the combination of the partial results generated by each substream can outweigh the performance benefits of a parallel stream.
      - Take into account how well the data structure underlying the stream decomposes. For instance, an ArrayList can be split much more efficiently than a LinkedList, because the first can be evenly divided without traversing it, as it’s necessary to do with the second. Also, the primitive streams created with the range factory method can be decomposed quickly. Finally, as you’ll learn later, you can get full control of this decomposition process by implementing your own Spliterator.
       The parallel-friendliness of certain stream sources in terms of their decomposability:
         ArrayList - Excellent
         LinkedList - Poor
         IntStream.range - Excellent
         Stream.iterate - Poor
         HashSet - Good
         TreeSet - Good
    */

    /*
    The fork/join framework - getting under the hood.
    The infrastructure used behind the scenes by parallel streams to execute operations in parallel is the fork/join framework.
    The fork/join framework was designed to recursively split a parallelizable task into smaller tasks and then combine the results of each subtask to produce the overall result. It’s an implementation of the ExecutorService interface, which distributes those subtasks to worker threads in a thread pool, called ForkJoinPool. Let’s start by exploring how to define a task and subtasks.
    To submit tasks to this pool, you have to create a subclass of RecursiveTask<R>, where R is the type of the result produced by the parallelized task (and each of its subtasks) or of RecursiveAction if the task returns no result (it could be updating other nonlocal structures, though). To define RecursiveTasks you need only implement its single abstract method, compute: [protected abstract R compute();]
    This method defines both the logic of splitting the task at hand into subtasks and the algorithm to produce the result of a single subtask when it’s no longer possible or convenient to further divide it. For this reason an implementation of this method often resembles the following pseudocode:
        if (task is small enough or no longer divisible) {
            compute task sequentially
        } else {
            split task in two subtasks
            call this method recursively possibly further splitting each subtask
            wait for the completion of all subtasks
            combine the results of each subtask
        }
    In general, there are no precise criteria for deciding whether a given task should be further divided or not, but there are various heuristics that you can follow to help you with this decision. You will learn them in more detail later. The recursive tasksplitting process is nothing more than the parallel version of the well-known divide-and-conquer algorithm.
    To demonstrate a practical example of how to use the fork/join framework and to build on our previous examples, let’s try to calculate the sum of a range of numbers (here represented by an array of numbers long[]) using this framework. As explained, you need to first provide an implementation for the RecursiveTask class, as shown by the ForkJoinSumCalculator below:
    */
    public static class ForkJoinSumCalculator extends RecursiveTask<Long> {
        // The size threshold for splitting into subtasks.
        public static final long THRESHOLD = 10_000;
        // The array of numbers to be summed.
        private final long[] numbers;
        // The initial and final positions of the subarray processed by this subtask.
        private final int start;
        private final int end;

        // Public constructor to create the main task.
        public ForkJoinSumCalculator(long[] numbers) {
            this(numbers, 0, numbers.length);
        }

        // Private constructor to create subtasks of the main task.
        private ForkJoinSumCalculator(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            if (length <= THRESHOLD) {
                return computeSequentially();
            }
            ForkJoinSumCalculator leftTask =
                new ForkJoinSumCalculator(numbers, start, start + length / 2);
            // Asynchronously executes the newly created subtask using another thread of ForkJoinPool.
            leftTask.fork();
            ForkJoinSumCalculator rightTask =
                new ForkJoinSumCalculator(numbers, start + length / 2, end);
            // Executes this second subtask synchronously, potentially allowing further recursive splits.
            Long rightResult = rightTask.compute();
            // Reads the result of the first subtask — waiting if it isn’t ready.
            Long leftResult = leftTask.join();
            return leftResult + rightResult;
        }

        private long computeSequentially() {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += numbers[i];
            }
            return sum;
        }
    }
    // Method performing a parallel sum of the first n natural numbers.
    public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        // When you pass the ForkJoinSumCalculator task to the ForkJoinPool, this task is executed by a thread of the pool that in turn calls the compute method of the task. This method checks to see if the task is small enough to be performed sequentially; otherwise, it splits the array of numbers to be summed into two halves and assigns them to two new ForkJoinSumCalculators that are scheduled to be executed by the ForkJoinPool. As a result, this process can be recursively repeated, allowing the original task to be divided into smaller tasks, until the condition used to check if it’s no longer convenient or no longer possible to further split it is met (in this case, if the number of items to be summed is less than or equal to 10,000). At this point, the result of each subtask is computed sequentially, and the (implicit) binary tree of tasks created by the forking process is traversed back toward its root. The result of the task is then computed, combining the partial results of each subtask.
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        // The value returned by this last method is the result of the task defined by the ForkJoinSumCalculator class when executed inside the ForkJoinPool.
        return new ForkJoinPool().invoke(task);
        // Note that in a real-world application, it doesn’t make sense to use more than one ForkJoinPool . For this reason, what you typically should do is instantiate it only once and keep this instance in a static field, making it a singleton, so it could be conveniently reused by any part of your software. Here, to create it you’re using its default no-argument constructor, meaning that you want to allow the pool to use all the processors available to the JVM. More precisely, this constructor will use the value returned by Runtime.availableProcessors to determine the number of threads used by the pool. Note that the availableProcessors method, despite its name, in reality returns the number of available cores, including any virtual ones due to hyperthreading.
    }
    public static void testForkJoinSum() {
        System.out.println("ForkJoin sum done in: "
                + measurePerf(StreamsParallel::forkJoinSum, 10_000_000L) + " msecs");
        // Here, the performance is worse than the version using the parallel stream, but only because you’re obliged to put the whole stream of numbers into a long[] before being allowed to use it in the ForkJoinSumCalculator task.
    }

    /*
    Best practices for using the fork/join framework.
      - Invoking the join method on a task blocks the caller until the result produced by that task is ready. For this reason, it’s necessary to call it after the computation of both subtasks has been started. Otherwise, you’ll end up with a slower and more complex version of your original sequential algorithm because every subtask will have to wait for the other one to complete before starting.
      - The invoke method of a ForkJoinPool shouldn’t be used from within a RecursiveTask. Instead, you should always call the methods compute or fork directly; only sequential code should use invoke to begin parallel computation.
      - Calling the fork method on a subtask is the way to schedule it on the ForkJoinPool . It might seem natural to invoke it on both the left and right subtasks, but this is less efficient than directly calling compute on one of them. Doing this allows you to reuse the same thread for one of the two subtasks and avoid the overhead caused by the unnecessary allocation of a further task on the pool.
      - Debugging a parallel computation using the fork/join framework can be tricky. In particular, it’s ordinarily quite common to browse a stack trace in your favorite IDE to discover the cause of a problem, but this can’t work with a fork/join computation because the call to compute occurs in a different thread than the conceptual caller, which is the code that called fork.
      - As you’ve discovered with parallel streams, you should never take for granted that a computation using the fork/join framework on a multicore processor is faster than the sequential counterpart. We already said that a task should be decomposable into several independent subtasks in order to be parallelizable with a relevant performance gain. All of these subtasks should take longer to execute than forking a new task; one idiom is to put I/O into one subtask and computation into another, thereby overlapping computation with I/O. Moreover, you should consider other things when comparing the performance of the sequential and parallel versions of the same algorithm. Like any other Java code, the fork/join framework needs to be “warmed up,” or executed, a few times before being optimized by the JIT compiler. This is why it’s always important to run the program multiple times before to measure its performance, as we did in our harness. Also be aware that optimizations built into the compiler could unfairly give an advantage to the sequential version (for example, by performing dead code analysis—removing a computation that’s never used).
      - Choosing the criteria to decide if a given subtask should be further split or is small enough to be evaluated sequentially covered in the next section.

    Work stealing.
    In our ForkJoinSumCalculator example it was decided to stop creating more subtasks when the array of numbers to be summed contained at most 10,000 items. This is an arbitrary choice, but in most cases it’s difficult to find a good heuristic, other than trying to optimize it by making several attempts with different inputs. In above test case, with an array of 10 million items over all ForkJoinSumCalculator will fork at least 1,000 subtasks. This might seem like a waste of resources because it runs on a machine that has only a few cores. In this specific case, that’s probably true because all tasks are CPU bound and are expected to take a similar amount of time.
    But forking a quite large number of fine-grained tasks is in general a winning choice. This is because ideally you want to partition the workload of a parallelized task in such a way that each subtask takes exactly the same amount of time, keeping all the cores of your CPU equally busy. Unfortunately, especially in cases closer to real-world scenarios than the straightforward example presented here, the time taken by each subtask can dramatically vary either due to the use of an inefficient partition strategy or because of unpredictable causes like slow access to the disk or the need to coordinate the execution with external services.
    The fork/join framework works around this problem with a technique called work stealing. In practice, this means that the tasks are more or less evenly divided on all the threads in the ForkJoinPool. Each of these threads holds a doubly linked queue of the tasks assigned to it, and as soon as it completes a task it pulls another one from the head of the queue and starts executing it. For the reasons listed previously, one thread might complete all the tasks assigned to it much faster than the others, which means its queue will become empty while the other threads are still pretty busy. In this case, instead of becoming idle, the thread randomly chooses a queue of a different thread and "steals" a task, taking it from the tail of the queue. This process continues until all the tasks are executed, and then all the queues become empty. That’s why having many smaller tasks, instead of only a few bigger ones, can help in better balancing the workload among the worker threads.


    Spliterator - "splitable iterator".
    When you use parallel streams you implicitly use the Spliterator - automatic mechanism splitting the stream.
    Like Iterators, Spliterators are used to traverse the elements of a source, but they’re also designed to do this in parallel. Although you may not have to develop your own Spliterator in practice, understanding how to do so will give you a wider understanding about how parallel streams work. Java 8 provides a default Spliterator implementation for all the data structures included in its Collections Framework. The Collection interface now provides a default method spliterator() which returns a Spliterator object. The Spliterator interface defines several methods:
        public interface Spliterator<T> {
            boolean tryAdvance(Consumer<? super T> action);
            Spliterator<T> trySplit();
            long estimateSize();
            int characteristics();
        }
    As usual, T is the type of the elements traversed by the Spliterator. The tryAdvance method behaves in a way similar to a normal Iterator in the sense that it’s used to sequentially consume the elements of the Spliterator one by one, returning true if there are still other elements to be traversed. But the trySplit method is more specific to the Spliterator interface because it’s used to partition off some of its elements to a second Spliterator (the one returned by the method), allowing the two to be processed in parallel. A Spliterator may also provide an estimation of the number of the elements remaining to be traversed via its estimateSize method, because even an inaccurate but quick-to-compute value can be useful to split the structure more or less evenly.

    The splitting process.
    In the first step, trySplit is invoked on the first Spliterator and generates a second one. Then in step two, it’s called again on these two Spliterators, which results in a total of four. The framework keeps invoking the method trySplit on a Spliterator until it returns null to signal that the data structure that it’s processing is no longer divisible. Finally, this recursive splitting process terminates in step 4 when all Spliterators have returned null to a trySplit invocation.
    The splitting characteristics.
    The last abstract method declared by the Spliterator interface is characteristics, which returns an int encoding the set of characteristics of the Spliterator itself. The Spliterator clients can use these characteristics to better control and optimize its usage.
    Although these conceptually overlap with characteristics of a collector, they’re coded differently. The characteristics are int constants defined in the Spliterator interface:
      - ORDERED. Elements have a defined order (for example, a List), so the Spliterator enforces this order when traversing and partitioning them.
      - DISTINCT. For each pair of traversed elements x and y, x.equals(y) returns false.
      - SORTED. The traversed elements follow a predefined sort order.
      - SIZED. This Spliterator has been created from a source with a known size (for example, a Set), so the value returned by estimatedSize() is precise.
      - NON-NULL. It’s guaranteed that the traversed elements won’t be null.
      - IMMUTABLE. The source of this Spliterator can’t be modified. This implies that no elements can be added, removed, or modified during their traversal.
      - CONCURRENT. The source of this Spliterator may be safely, concurrently modified by other threads without any synchronization.
      - SUBSIZED. Both this Spliterator and all further Spliterators resulting from its split are SIZED.
    */

    //Implementing your own Spliterator.
    //Let’s look at a practical example of where you might need to implement your own  Spliterator. We’ll develop a simple method that counts the number of words in a String:
    static final String SENTENCE = " Nel   mezzo del cammin  di nostra  vita mi  ritrovai in una  selva oscura che la  dritta via era   smarrita ";
    // An iterative version of this method could be written as follows:
    public static int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            }
            else {
                if (lastSpace) {
                    counter++;
                }
                lastSpace = Character.isWhitespace(c);
            }
        }
        return counter;
    }
    // Rewriting the wordcounter in functional style.
    // First, you need to convert the String into a stream. Unfortunately, there are primitive streams only for int, long, and double, so you’ll have to use a Stream<Character>:
    static Stream<Character> wordStream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
    // You can calculate the number of words by performing a reduction on this stream. While reducing the stream, you’ll have to carry a state consisting of two variables: an int counting the number of words found so far and a boolean to remember if the last-encountered Character was a space or not. Because Java doesn’t have tuples (a construct to represent an ordered list of heterogeneous elements without the need of a wrapper object), you’ll have to create a new class, WordCounter, which will encapsulate this state as shown below:
    private static class WordCounter {
        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }
        // Accumulate method traverses Characters one by one as done by the iterative algorithm. Method defines how to change the state of the WordCounter, or, more precisely, with which state to create a new WordCounter because it’s an immutable class. This is important to understand. We are accumulating state with an immutable class specifically so that the process can be parallelized in the next step. The method accumulate is called whenever a new Character of the stream is traversed.
        public WordCounter accumulate(Character c) {
            if (Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            }
            else {
                return lastSpace ? new WordCounter(counter + 1, false) : this;
            }
        }
        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }
        public int getCounter() {
            return counter;
        }
    }
    // A method that will reduce the stream of Characters:
    private static int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(
            new WordCounter(0, true),
            WordCounter::accumulate,
            WordCounter::combine
        );
        return wordCounter.getCounter();
    }
    public static void testSpliterator() {
        Stream<Character> wordStream1 = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
        Stream<Character> wordStream2 = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
        System.out.println("Found " + countWords(wordStream1) + " words");
        // The output of both will be: "Found 19 words".
        System.out.println("Found " + countWords(wordStream2.parallel()) + " words");
        // Gives "Found 80 words".
    }
    // Evidently something has gone wrong, but what? The problem isn’t hard to discover. Because the original String is split at arbitrary positions, sometimes a word is divided in two and then counted twice. In general, this demonstrates that going from a sequential stream to a parallel one can lead to a wrong result if this result may be affected by the position where the stream is split. How can you fix this issue? The solution consists of ensuring that the String isn’t split at a random position but only at the end of a word. To do this, you’ll have to implement a Spliterator of Character that splits a String only between two words and then creates the parallel stream from it.
    // Making the wordcounter work in parallel.
    private static class WordCounterSpliterator implements Spliterator<Character> {
        private final String string;
        private int currentChar = 0;

        private WordCounterSpliterator(String string) {
            this.string = string;
        }
        // Method feeds the Consumer with the Character in the String at the current index position and increments this position. The Consumer passed as its argument is an internal Java class forwarding the consumed Character to the set of functions that have to be applied to it while traversing the stream, which in this case is only a reducing function, namely, the accumulate method of the WordCounter class. The tryAdvance method returns true if the new cursor position is less than the total String length and there are further Characters to be iterated.
        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            // Consumes the current character.
            action.accept(string.charAt(currentChar++));
            // Returns true if there are further characters to be consumed.
            return currentChar < string.length();
        }
        // This Spliterator is created from the String to be parsed and iterates over its Characters by holding the index of the one currently being traversed. The method is the most important one in a Spliterator, because it’s the one defining the logic used to split the data structure to be iterated.
        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            // The first thing you have to do here is set a limit under which you don’t want to perform further splits. Here, low limit of 10 Characters only used to make sure that your program will perform some splits with the relatively short String you’re parsing. But in real-world applications you’ll have to use a higher limit, as in the fork/join example, to avoid creating too many tasks.
            if (currentSize < 10) {
                // Returns null to signal that the String to be parsed is small enough to be processed sequentially.
                return null;
            }
            for (
                // Sets the candidate split position to be half of the String chunk remaining to be parsed. But you don’t use this split position directly because you want to avoid splitting in the middle of a word, so you move forward until you find a blank Character.
                int splitPos = currentSize / 2 + currentChar;
                splitPos < string.length();
                splitPos++
            ) {
                // Advances the split position until the next space
                if (Character.isWhitespace(string.charAt(splitPos))) {
                    // Creates a new Spliterator that will traverse the substring chunk going from the current position to the split one.
                    Spliterator<Character> spliterator =
                        new WordCounterSpliterator(string.substring(currentChar, splitPos));
                    // Sets the start position of the current Spliterator to the split position, because the part before it will be managed by the new Spliterator, and then you return it.
                    currentChar = splitPos;
                    // Found a space and created the new Spliterator, so exit the loop.
                    return spliterator;
                }
            }
            return null;
        }
        // The estimatedSize of elements still to be traversed is the difference between the total length of the String parsed by this Spliterator and the position currently iterated.
        @Override
        public long estimateSize() {
            return string.length() - currentChar;
        }
        // The characteristics method signals to the framework that this Spliterator is ORDERED (the order is the sequence of Character s in the String), SIZED (the value returned by the estimatedSize method is exact), SUBSIZED (the other Spliterators created by the trySplit method also have an exact size), NON-NULL (there can be no null Characters in the String), and IMMUTABLE (no further Characters can be added while parsing the String because the String itself is an immutable class).
        @Override
        public int characteristics() {
            return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
        }
    }

    public static int countWords(String str) {
        Spliterator<Character> wordSpliterator = new WordCounterSpliterator(str);
        Stream<Character> wordStream = StreamSupport.stream(wordSpliterator, true);
        return countWords(wordStream);
    }
    public static void testSpliteratorParallel() {
        System.out.println("Found " + countWords(SENTENCE) + " words");
        // Gives "Found 19 words".
    }
    // One last notable feature of Spliterators is the possibility of binding the source of the elements to be traversed at the point of first traversal, first split, or first query for estimated size, rather than at the time of its creation. When this happens, it’s called a late-binding Spliterator. More - later.

    // Some utility code.
    public static <T, R> long measurePerf(Function<T, R> f, T input) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            R result = f.apply(input);
            long duration = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Result: " + result);
            if (duration < fastest) {
                fastest = duration;
            }
        }
        return fastest;
    }
}
    /*
    Configuring the thread pool used by parallel streams.
    Looking at the stream’s parallel method, you may wonder where the threads used by the parallel stream come from, how many there are, and how you can customize the process. Parallel streams internally use the default ForkJoinPool which by default has as many threads as you have processors, as returned by Runtime.getRuntime().availableProcessors(). But you can change the size of this pool using the system property:
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");
    This is a global setting, so it will affect all the parallel streams in your code. Conversely, it currently isn’t possible to specify this value for a single parallel stream. In general, having the size of the ForkJoinPool equal to the number of processors on your machine is a meaningful default, and we strongly suggest that you not modify it unless you have a good reason for doing so.
    */