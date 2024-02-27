package org.example.java_learning;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Stream;

public class StreamsIntro {
/*
Streams behave like collections but have several advantages that enable new styles of programming. First, like database-query language such as SQL, it enables queries to be written in a few lines that would take many lines in Java. Second, streams are designed so that not all their data needs to be in memory (or even computed) at once. Thus, it's possible to process streams that are too big to fit in computer memory. Java 8 can optimize operations on streams in a way that Java can’t do for collections — for example, it can group together several operations on the same stream so that the data is traversed only once instead of expensively traversing it multiple times. Even better, Java can automatically parallelize stream operations (unlike collections), as a result, it avoids the need to write code that uses synchronized, which is not only highly error-prone but also more expensive on multicore CPUs.
The addition of Streams in Java 8 can be seen as a direct cause of the two other additions to Java 8: concise techniques to pass code to methods (method references, lambdas) and default methods in interfaces.

To summarize:
 The Streams API lets you write code that’s:
  - Declarative—More concise and readable
  - Composable—Greater flexibility
  - Parallelizable—Better performance
 Working with streams in general involves three items:
  - A data source (such as a collection) to perform a query on
  - A chain of intermediate operations that form a stream pipeline
  - A terminal operation that executes the stream pipeline and produces a result
 The idea behind a stream pipeline is similar to the builder pattern. In the builder pattern, there’s a chain of calls to set up a configuration (for streams this is a chain of intermediate operations), followed by a call to a build method (for streams this is a terminal operation).
 Some operations such as filter and map are stateless: they don’t store any state. Some operations such as reduce store state to calculate a value. Some operations such as sorted and distinct also store state because they need to buffer all the elements of a stream before returning a new stream. Such operations are called stateful operations.
 Prefer an immutable (stateless) approach in order to process a stream in parallel and expect a correct result.
 Dealing with a stream of infinite size limit its size explicitly using the operation limit; otherwise, the terminal operation such as forEach will compute forever. Sort or reduce can't be applied on an infinite stream because all elements need to be processed.
 Reduce method (.stream().reduce(...)) is meant to combine two values and produce a new one - it’s an immutable reduction. In contrast, the collect method (.stream().collect(...)) is designed to mutate a container to accumulate the result it’s supposed to produce. So be aware of parallelism issues.
 Terminate streams:
  - collect is a terminal operation that takes as argument various recipes (called collectors) for accumulating the elements of a stream into a summary result.
  - Predefined collectors include reducing and summarizing stream elements into a single value, such as calculating the minimum, maximum, or average. Those collectors are summarized in table below.
  - Predefined collectors let you group elements of a stream with groupingBy and partition elements of a stream with partitioningBy.
  - Collectors compose effectively to create multilevel groupings, partitions, and reductions.
  - You can develop your own collectors by implementing the methods defined in the Collector interface.
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

The streams interface in java.util.stream.Stream.
 _______________________________________________________________________________________________
|This table shows some Stream API operations.                                                   |
|SU - stateful-unbounded                                                                        |
|SB - stateful-bounded                                                                          |
|_______________________________________________________________________________________________|
|             |                  |                |      Type/functional       |    Function    |
|  Operation  |      Type        |  Return type   |      interface used        |   descriptor   |
|_____________|__________________|________________|____________________________|________________|
| filter      | Intermediate     | Stream<T>      | Predicate<T>               | T -> boolean   |
| distinct    | Intermediate SO  | Stream<T>      |                            |                |
| takeWhile   | Intermediate     | Stream<T>      | Predicate<T>               | T -> boolean   |
| dropWhile   | Intermediate     | Stream<T>      | Predicate<T>               | T -> boolean   |
| skip        | Intermediate SB  | Stream<T>      | long                       |                |
| limit       | Intermediate SB  | Stream<T>      | long                       |                |
| map         | Intermediate     | Stream<R>      | Function<T, R>             | T -> R         |
| flatMap     | Intermediate     | Stream<R>      | Function<T, Stream<R>>     | T -> Stream<R> |
| sorted      | Intermediate SU  | Stream<T>      | Comparator<T>              | (T, T) -> int  |
| anyMatch    | Terminal         | boolean        | Predicate<T>               | T -> boolean   |
| noneMatch   | Terminal         | boolean        | Predicate<T>               | T -> boolean   |
| allMatch    | Terminal         | boolean        | Predicate<T>               | T -> boolean   |
| findAny     | Terminal         | Optional<T>    |                            |                |
| findFirst   | Terminal         | Optional<T>    |                            |                |
| forEach     | Terminal         | void           | Consumer<T>                | T -> void      |
| collect     | Terminal         | R              | Collector<T, A, R>         |                |
| reduce      | Terminal SB      | Optional<T>    | BinaryOperator<T>          | (T, T) -> T    |
| count       | Terminal         | long           |                            |                |
|_____________|__________________|________________|____________________________|________________|


 _______________________________________________________________________________________________
|This table shows the main static factory methods of the Collectors class.                      |
|SU - stateful-unbounded                                                                        |
|----------------- ------------------------- ---------------------------------------------------|
| Factory method  |     Returned type       |                  Used to                          |
|-----------------|-------------------------|---------------------------------------------------|
|toList           |List<T>                  |Gather all the stream’s items in a List.           |
|List<Dish> dishes = menuStream.collect(toList());                                              |
|-----------------------------------------------------------------------------------------------|
|toSet            |Set<T>                   |Gather all the stream’s items in a Set, eliminating|
|                 |                         |duplicates.                                        |
|Set<Dish> dishes = menuStream.collect(toSet());                                                |
|-----------------------------------------------------------------------------------------------|
|toCollection     |Collection<T>            |Gather all the stream’s items in the collection    |
|                 |                         |created by the provided supplier.                  |
|Collection<Dish> dishes = menuStream.collect(toCollection(), ArrayList::new);                  |
|-----------------------------------------------------------------------------------------------|
|counting         |Long                     |Count the number of items in the stream.           |
|long howManyDishes = menuStream.collect(counting());                                           |
|-----------------------------------------------------------------------------------------------|
|summingInt       |Integer                  |Sum the values of an Integer property of the items |
|                 |                         |in the stream.                                     |
|int totalCalories = menuStream.collect(summingInt(Dish::getCalories));                         |
|-----------------------------------------------------------------------------------------------|
|averagingInt     |Double                   |Calculate the average value of an Integer property |
|                 |                         |of the items in the stream.                        |
|double avgCalories = menuStream.collect(averagingInt(Dish::getCalories));                      |
|-----------------------------------------------------------------------------------------------|
|summarizingInt   |IntSummaryStatistics     |Collect statistics regarding an Integer property of|
|                 |                         |the items in the stream, such as the maximum,      |
|                 |                         |minimum, total, and average.                       |
|IntSummaryStatistics menuStatistics = menuStream.collect(summarizingInt(Dish::getCalories));   |
|-----------------------------------------------------------------------------------------------|
|joining          |String                   |Concatenate the strings resulting from the         |
|                 |                         |invocation of the toString method on each item of  |
|                 |                         |the stream.                                        |
|String shortMenu = menuStream.map(Dish::getName).collect(joining(", "));                       |
|-----------------------------------------------------------------------------------------------|
|maxBy / minBy    |Optional<T>              |An Optional wrapping the maximal/minimal element   |
|                 |                         |in this stream according to the given comparator or|
|                 |                         |Optional.empty() if the stream is empty.           |
|Optional<Dish> fattest = menuStream.collect(maxBy(comparingInt(Dish::getCalories)));           |
|-----------------------------------------------------------------------------------------------|
|reducing         |The type produced by the |Reduce the stream to a single value starting from  |
|                 |reduction operation      |an initial value used as accumulator and           |
|                 |                         |iteratively combining it with each item of the     |
|                 |                         |stream using a BinaryOperator.                     |
|int totalCalories = menuStream.collect(reducing(0, Dish::getCalories, Integer::sum));          |
|-----------------------------------------------------------------------------------------------|
|collectingAndThen|The type returned by the |Wrap another collector and apply a transformation  |
|                 |transforming function    |function to its result.                            |
|int howManyDishes = menuStream.collect(collectingAndThen(toList(), List::size));               |
|-----------------------------------------------------------------------------------------------|
|groupingBy       |Map<K, List<T>>          |Group the items in the stream based on the value of|
|                 |                         |one of their properties and use those values as    |
|                 |                         |keys in the resulting Map.                         |
|Map<Dish.Type,List<Dish>> dishesByType = menuStream.collect(groupingBy(Dish::getType));        |
|-----------------------------------------------------------------------------------------------|
|partitioningBy   |Map<Boolean, List<T>>    |Partition the items in the stream based on the     |
|                 |                         |result of the application of a predicate to each of|
|                 |                         |them.                                              |
|Map<Boolean,List<Dish>> vegiDishes = menuStream.collect(partitioningBy(Dish::isVegetarian));   |
|_______________________________________________________________________________________________|

*/
    // Here is a function that returns names of dishes that are low in calories, sorted by number of calories.
    // Non Stream way
    static List<String> getLowCaloriesDishesNamesSortedNonStream(List<Dish> dishes) {
        // "Garbage variable" lowCaloricDishes. Its only purpose is to act as an intermediate throwaway container.
        List<Dish> lowCaloricDishes = new ArrayList<>();
        // Filters the elements using an accumulator
        for (Dish d : dishes) {
            if (d.getCalories() < 400) {
                lowCaloricDishes.add(d);
            }
        }
        List<String> lowCaloricDishesName = new ArrayList<>();
        // Sorts the dishes with an anonymous class
        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish d1, Dish d2) {
                return Integer.compare(d1.getCalories(), d2.getCalories());
            }
        });
        // Processes the sorted list to select the names of dishes
        for (Dish d : lowCaloricDishes) {
            lowCaloricDishesName.add(d.getName());
        }
        return lowCaloricDishesName;
    }
    // Stream way
    static List<String> getLowCaloriesDishesNamesSortedStream (List<Dish> dishes) {
        return
            //dishes.parallelStream() instead of dishes.stream() to execute this code in parallel
            // init chain return stream
            dishes.stream()
                // filter, sorted, map and limit chains also return streams
                .filter(d -> d.getCalories() < 400)
                .sorted(comparing(Dish::getCalories))
                // map transforms an element into another one or to extract information
                .map(Dish::getName) // d -> d.getName()
                .limit(2)
                // No result is produced up here yet. And indeed no element from menu is even selected, until collect is invoked. You can think of it as if the method invocations in the chain are queued up until collect is called
                .collect(toList());
                // at the end collect operation closes the stream
    }

    public static void simpleExampleStreamVsNonStream () {
        getLowCaloriesDishesNamesSortedNonStream(Dish.menu).forEach(System.out::println);
        System.out.println("---");
        getLowCaloriesDishesNamesSortedStream(Dish.menu).forEach(System.out::println);

        // Out of the blue, bam!
        Map<Dish.Type, List<Dish>> dishesByType =
                Dish.menu.stream().collect(groupingBy(Dish::getType));
    }

    public static class StreamsALittleDeeer {
    /*
    Streams is a sequence of elements from a source that supports data-processing operations:
    - Sequence of elements. Like a collection, a stream provides an interface to a sequenced set of values of a specific element type. Because collections are data structures, they’re mostly about storing and accessing elements with specific time/space complexities (for example, an ArrayList versus a LinkedList). But streams are about expressing computations such as filter, sorted, and map, which you saw earlier.
    - Source. Streams consume from a data-providing source such as collections, arrays, or I/O resources. Note that generating a stream from an ordered collection preserves the ordering. The elements of a stream coming from a list will have the same order as the list.
    - Data-processing operations. Streams support database-like operations and common operations from functional programming languages to manipulate data, such as filter, map, reduce, find, match, sort, and so on. Stream operations can be executed either sequentially or in parallel.

    Stream operations have two important characteristics:
    -  Pipelining. Many stream operations return a stream themselves, allowing operations to be chained to form a larger pipeline. This enables certain optimizations such as laziness and short-circuiting. A pipeline of operations can be viewed as a database-like query on the data source.
    - Internal iteration. In contrast to collections, which are iterated explicitly using an iterator, stream operations do the iteration behind the scenes for you.

    Later we’ll show how simple it is to construct a stream containing all the prime numbers (2, 3, 5, 7, 11, . . .) even though there are an infinite number of them. The idea is that a user will extract only the values they require from a stream and these elements are produced—invisibly to the user—only as and when required. This is a form of a producer-consumer relationship. Another view is that a stream is like a lazily constructed collection: values are computed when they’re solicited by a consumer (in management speak this is demand driven, or even just-in-time, manufacturing).
    In contrast, a collection is eagerly constructed (supplier-driven: fill your warehouse before you start selling, like a Christmas novelty that has a limited life). Imagine applying this to the primes example. Attempting to construct a collection of all prime numbers would result in a program loop that forever computes a new prime—adding it to the collection—but could never finish making the collection, so the consumer would never get to see it.
    */

    // Traversable only once.
    // Note that, similarly to iterators, a stream can be traversed only once. After that a stream is said to be consumed. You can get a new stream from the initial data source to traverse it again as you would for an iterator (assuming it’s a repeatable source like a collection; if it’s an I/O channel, you’re out of luck).
        void streamsTraversableOnlyOnce() {
            List<String> names = Arrays.asList("Java8", "Lambdas", "In", "Action");
            Stream<String> s = names.stream();
            s.forEach(System.out::println);
            // uncommenting this line will result in an IllegalStateException
            // because streams can be consumed only once
            //s.forEach(System.out::println);
        }

    // External vs. internal iteration.
    // Using the Collection interface requires iteration to be done by the user (for example, using for-each); this is called external iteration. The for-each construct is syntactic sugar that translates into something much uglier using an Iterator object.
        List<String> names1 = new ArrayList<>();
        void externalIteration() {
            for(Dish dish: Dish.menu) { names1.add(dish.getName()); }
        }
        // under the hood
        void externalIterationUnderTheHood() {
            Iterator<Dish> iterator = Dish.menu.iterator();
            while (iterator.hasNext()) {
                Dish dish = iterator.next();
                names1.add(dish.getName());
            }
        }
        // streams way
        List<String> names2 = Dish.menu.stream().map(Dish::getName).collect(toList());
    }

    public static class StreamOperations {
    /*
    The streams interface in java.util.stream.Stream defines many operations. They can be classified into two categories:
     1. Intermediate operations. They can be connected together to form a pipeline: filter, map, and limit.
     2. Terminal operations. They cause the pipeline to be executed and closes it.
    */

    // Intermediate operations are lazy, i.e. they can usually be merged and processed into a single pass by the terminal operation.
        public static void lazyIntermediateOperations() {
            List<String> names = Dish.menu.stream()
                .filter(dish -> {
                    System.out.println("filtering " + dish.getName());
                    return dish.getCalories() > 300;
                })
                .map(dish -> {
                    System.out.println("mapping " + dish.getName());
                    return dish.getName();
                })
                .limit(3)
                .collect(toList());
            System.out.println(names);
            // First, despite the fact that many dishes have more than 300 calories, only the first three are selected! This is because of the  limit operation and a technique called short-circuiting, as we’ll explain in the next chapter. Second, despite the fact that filter and map are two separate operations, they were merged into the same pass (compiler experts call this technique loop fusion).
        }

    // Terminal operations produce a result from a stream pipeline. A result is any non-stream value such as a List, an Integer, or even void. For example, in the following pipeline, forEach is a terminal operation that returns void and applies a lambda to each dish in the source. Passing System.out.println to forEach asks it to print every Dish in the stream created from menu:
        public static void streamTerminalOperations() {
            Dish.menu.stream().forEach(System.out::println);

            long count = Dish.menu.stream()
                .filter(dish -> dish.getCalories() > 300)
                .distinct()
                .limit(3)
                .count();

            DoubleAdder totalCalories = new DoubleAdder();
            List<Dish> names = Dish.menu;
            names.stream().forEach(item -> {
                String name = item.getName();
                double calories = item.getCalories();
                totalCalories.add(calories);
                System.out.printf("%25s | %15s | %10.2d\n", name, item.getCaloricLevel(), (calories > 0.0d) ? calories : "-");
            });
        }
    }
}
