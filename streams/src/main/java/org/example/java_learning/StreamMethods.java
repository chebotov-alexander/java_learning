package org.example.java_learning;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collectors.*;
import static org.example.java_learning.Dish.menu;
import static org.example.java_learning.Dish.dishTags;
import static org.example.java_learning.Util.println;

public class StreamMethods {
    public static class StreamFiltering {
        // Filtering with a predicate  takes as argument a predicate (a function returning a boolean) and returns a stream including all elements that match the predicate.
        List<Dish> vegetarianMenu = menu.stream()
            .filter(Dish::isVegetarian)
            .collect(toList());

        // Filtering unique elements with a method called distinct that returns a stream with unique elements (according to the implementation of the hashcode and equals methods of the objects produced by the stream).
        void filterUniqueElements() {
            List<Integer> uniqueEvenNumbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
            uniqueEvenNumbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct() // using the equals method for the comparison
                .forEach(System.out::println);
        }
    }

    public static class SlicingStream {
        // Let's see how to select and skip elements in a stream in different ways. There are operations available that let you efficiently select or drop elements using a predicate, ignore the first few elements of a stream, or truncate a stream to a given size.

        // Slicing using a predicate (Java9).
        // Using takeWhile.
        // Imagine you have a sorted list of dishes and you want to effectively filter out all of them that's gotten fewer than 320 calories.
        List<Dish> menuOrderedByCalories = Arrays.asList(
            new Dish("season fruit", true, 120, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER));
        // With filter method like this ".filter(dish -> dish.getCalories() < 320)" you will have to iterate all the dishes, it's just how filter works, but using takeWhile method the iterate process will stop immediately after 320 has encountered, i.e. takeWhile stops once it has found an element that fails to match.
        List<Dish> menuSlicedWithTakeWhile = menuOrderedByCalories.stream()
            .takeWhile(dish -> dish.getCalories() < 320)
            .collect(toList());

        // Using dropWhile. It does the opposite.
        // The dropWhile operation is the complement of takeWhile. It throws away the elements at the start where the predicate is false. Once the predicate evaluates to true it stops and returns all the remaining elements, and it even works if there are an infinite number of remaining elements!
        // So you will get all that have greater than 320 calories.
        List<Dish> menuSlicedWithDropWhile = menuOrderedByCalories.stream()
            .dropWhile(dish -> dish.getCalories() < 320)
            .collect(toList());

        // Truncating a stream with limit method.
        List<Dish> menuSlicedWithLimit = menu.stream()
            .filter(d -> d.getCalories() > 300)
            .limit(3)
            .collect(toList());

        // Skipping elements.
        // Streams support the skip(n) method to return a stream that discards the first n elements. If the stream has fewer than n elements, an empty stream is returned. Note that limit(n) and skip(n) are complementary!
        public static void testSkippingWithLimit() {
            List<Integer> fullMenuOrderedByCalories = menu.stream()
                .sorted(comparing(Dish::getCalories))
                .map(Dish::getCalories)
                .collect(toList());
            System.out.println("Sorted menu:");
            fullMenuOrderedByCalories.forEach(System.out::println);
            List<Integer> menuSlicedWithLimitAndSkip = menu.stream()
                .filter(d -> d.getCalories() > 400)
                .skip(2)
                .limit(3)
                .map(Dish::getCalories)
                .collect(toList());
            System.out.println("Skipping and limiting unordered elements:");
            menuSlicedWithLimitAndSkip.forEach(System.out::println);
        }
    }

    public static class MappingStream {
        // A common data processing idiom is to select information from certain objects. For example, in SQL you can select a particular column from a table. The Streams API provides similar facilities through the map and flatMap methods.
        // map method, which takes a function as argument. The function is applied to each element, mapping it into a new element (the word mapping is used because it has a meaning similar to transforming but with the nuance of “creating a new version of” rather than “modifying”)
        List<Integer> dishNamesLength = menu.stream()
            .map(Dish::getName)
            .map(String::length)
            .collect(toList());

        // Now imagine you want to return a list of all the unique characters for a list of words ["Hello," "World"] like this ["H," "e," "l," "o," "W," "r," "d"].
        static List<String> words = Arrays.asList("Hello", "World");
        static List<String[]> uniqueCharactersArentThey = words.stream()
            .map(word -> word.split(""))
            .distinct()
            .collect(toList());
        public static void testGetUniqueCharactersArentThey() {
            for (String[] stringList : uniqueCharactersArentThey) {
                for (String str : stringList) {
                    System.out.println(str);
                }
            }
        }
        // The problem with this approach is that the lambda passed to the map method returns a String[] (an array of String) for each word. The stream returned by the map method is of type Stream<String[]>. What you want is Stream<String> to represent a stream of characters. So you will end up with ["Hello," "World"]. That's no good. Luckily there’s a solution to this problem using the method flatMap!
        // First, you need a stream of characters instead of a stream of arrays. There’s a method called Arrays.stream()that takes an array and produces a stream:
        String[] arrayOfWords = {"Goodbye", "World"};
        Stream<String> streamOfwords = Arrays.stream(arrayOfWords);
        List<Stream<String>> uniqueCharactersStillNot = words.stream()
            // Converts each word into an array of its individual letters
            .map(word -> word.split(""))
            // Makes each array into a separate stream
            .map(Arrays::stream)
            .distinct()
            // But you now end up with a list of streams
            .collect(toList());
        // Using flatMap
        static List<String> uniqueCharactersThereItIs = words.stream()
            .map(word -> word.split(""))
            // Flattens each generated stream into a single stream. It has the effect of mapping each array not with a stream but with the contents of that stream.
            .flatMap(Arrays::stream)
            .distinct()
            .collect(toList());
        // In a nutshell, the flatMap method lets you replace each value of a stream with another stream and then concatenates all the generated streams into a single stream.
        public static void testGetUniqueCharacters() {
            uniqueCharactersThereItIs.forEach(System.out::println);
        }

        // Returns a list of the square of each number.
        List<Integer> squares = Arrays.asList(1, 2, 3, 4, 5).stream()
            .map(n -> n * n)
            .collect(toList());

        // Returns all pairs of numbers from two lists of numbers: from this  [1, 2, 3] and [3, 4] to this [(1, 3), (1,4), (2, 3), (2, 4), (3, 3), (3, 4)].
        static List<Integer> numbers1 = Arrays.asList(1,2,3,4,5);
        static List<Integer> numbers2 = Arrays.asList(6,7,8);
        List<int[]> numbersPairs = numbers1.stream()
            // You could use two maps to iterate on the two lists and generate the pairs like this ".stream().map(j -> new int[]{i, j})". But this would return a Stream<Stream<Integer[]>>. What you need to do is flatten the generated streams to result in a Stream<Integer[]>.
            .flatMap(i -> numbers2.stream().map(j -> new int[]{i, j}))
            .collect(toList());

        // How would you extend the previous example to return only pairs whose sum is divisible by 3?
        static List<int[]> pairs = numbers1.stream()
            .flatMap(
                (Integer i) -> numbers2.stream()
                    .map((Integer j) -> new int[]{i, j})
            )
            .filter(pair -> (pair[0] + pair[1]) % 3 == 0)
            .collect(toList());
        public static void testFlatmap() {
            pairs.forEach(pair -> System.out.printf("(%d, %d)", pair[0], pair[1]));
        }
    }

    public class FindingAndMatchingStream {
        // Next four operations — anyMatch, allMatch, noneMatch and findAny — make use of what we call short-circuiting, a stream version of the familiar Java short-circuiting && and || operators. In relation to streams, certain operations such as allMatch, noneMatch, findFirst, and findAny don’t need to process the whole stream to produce a result. As soon as an element is found, a result can be produced. Similarly, limit is also a short-circuiting operation. The operation only needs to create a stream of a given size without processing all the elements in the stream. Such operations are useful (for example, when you need to deal with streams of infinite size, because they can turn an infinite stream into a stream of finite size).
        // These methods return a boolean and is therefore a terminal operation.
        // Matching at least one element.
        public static void testAnyMatch() {
            if (menu.stream().anyMatch(Dish::isVegetarian)) {
                System.out.println("The menu is (somewhat) vegetarian friendly!!");
            }
        }
        // Matching all elements with allMatch.
        public static void testAllMatch() {
            if (menu.stream().allMatch(d -> d.getCalories() < 1000)) {
                System.out.println("The menu is healthy");
            }
        }
        // Matching all elements with noneMatch.
        public static void testNoneMatch() {
            if (menu.stream().noneMatch(d -> d.getCalories() >= 1000)) {
                System.out.println("The menu is absolutely healthy");
            }
        }

        // Finding an element.
        // It can be used in conjunction with other stream operations.
        // Note, findAny method returns an arbitrary element of the current stream.
        static Optional<Dish> dish = menu.stream()
            .filter(Dish::isVegetarian)
            .findAny();
        /*
        The Optional<T> class (java.util.Optional) is a container class to represent the existence or absence of a value. In the previous code, it’s possible that findAny doesn’t find any element. Instead of returning null, which is well known for being error-prone, the Java 8 library designers introduced Optional<T>.
         There are a few methods available in Optional that force you to explicitly check for the presence of a value or deal with the absence of a value:
          - isPresent() returns true if Optional contains a value, false otherwise.
          - ifPresent(Consumer<T> block) executes the given block if a value is present.
          - T get() returns the value if present; otherwise it throws a NoSuchElementException.
          - T orElse(T other) returns the value if present; otherwise it returns a default value.
        */
        public static void testFindAnyOptional() {
            dish.ifPresent(d -> System.out.println(d.getName()));
            // or
            menu.stream()
                .filter(Dish::isVegetarian)
                .findAny()
                .ifPresent(d -> System.out.println(d.getName()));
        }

        // Finding the first element.
        // Some streams have an encounter order that specifies the order in which items logically appear in the stream.
        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> firstSquareDivisibleByThree = someNumbers.stream()
            .map(n -> n * n)
            .filter(n -> n % 3 == 0)
            .findFirst(); // 9
        // When to use findFirst and findAny.
        //You may wonder why we have both findFirst and findAny. The answer is parallelism. Finding the first element is more constraining in parallel. If you don’t care about which element is returned, use findAny because it’s less constraining when using parallel streams.
    }

    public class ReducingStream {
    // In this section, you’ll see how you can combine elements of a stream to express more complicated queries such as “Calculate the sum of all calories in the menu,” or “What is the highest calorie dish in the menu?” using the reduce operation. Such queries combine all the elements in the stream repeatedly to produce a single value such as an Integer. These queries can be classified as reduction operations (a stream is reduced to a value). In functional programming-language jargon, this is referred to as a fold because you can view this operation as repeatedly folding a long piece of paper (your stream) until it forms a small square, which is the result of the fold operation.
        static List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        int sum1 = numbers.stream().reduce(0, (a, b) -> a + b);
        // reduce takes two arguments:
        // - An initial value, here 0. Here 0 is used as the first parameter of the lambda (a), and 1 is consumed from the stream and used as the second parameter (b). 0 + 1 produces 1, and it becomes the new accumulated value. Then the lambda is called again with the accumulated value and the next element of the stream, 2, which produces the new accumulated value, 3 and so on.
        // - A BinaryOperator<T> to combine two elements and produce a new value; here you use the lambda (a, b) -> a + b.
        // Same with method reference
        int sum2 = numbers.stream().reduce(0, Integer::sum);
        // There’s also an overloaded variant of reduce that doesn’t take an initial value, but it returns an Optional object.
        Optional<Integer> sum3 = numbers.stream().reduce(Integer::sum);

        // Maximum and minimum.
        int max1 = numbers.stream().reduce(0, (a, b) -> Integer.max(a, b));
        // Or
        Optional<Integer> max2 = numbers.stream().reduce(Integer::max);
        Optional<Integer> min1 = numbers.stream().reduce(Integer::min);
        // Same as
        Optional<Integer> min2 = numbers.stream().reduce((x, y) -> x < y ? x : y);
        // How would you count the number of dishes in a stream using the map and reduce methods?
        long count1 = menu.stream().count();
        // Same result with
        int count2 = menu.stream()
            .map(d -> 1)
            .reduce(0, Integer::sum);
        // A chain of map and reduce is commonly known as the map-reduce pattern, made famous by Google’s use of it for web searching because it can be easily parallelized.
        int calories = menu.stream()
            .map(Dish::getCalories)
            .reduce(0, Integer::sum);
    }

    public class PuttingIntoPractice {
        public static void puttingIntoPractice() {
            Trader raoul = new Trader("Raoul", "Cambridge");
            Trader mario = new Trader("Mario", "Milan");
            Trader alan = new Trader("Alan", "Cambridge");
            Trader brian = new Trader("Brian", "Cambridge");
            List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
            );
            // Query 1: Find all transactions from year 2011 and sort them by value (small to high).
            List<Transaction> tr2011 = transactions.stream()
                .filter(transaction -> transaction.getYear() == 2011)
                .sorted(comparing(Transaction::getValue))
                .collect(toList());
            System.out.println(tr2011);

            // Query 2: What are all the unique cities where the traders work?
            List<String> cities1 = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .distinct()
                .collect(toList());
            // or
            Set<String> cities2 = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .collect(toSet());
            System.out.println(cities2);

            // Query 3: Find all traders from Cambridge and sort them by name.
            List<Trader> traders = transactions.stream()
                .map(Transaction::getTrader)
                .filter(trader -> trader.getCity().equals("Cambridge"))
                .distinct()
                .sorted(comparing(Trader::getName))
                .collect(toList());
            System.out.println(traders);

            // Query 4: Return a string of all traders' names sorted alphabetically.
            String traderStr1 = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted()
                // Combines the names one by one to form a String that concatenates all the names.
                .reduce("", (n1, n2) -> n1 + n2);
            // Note that this solution is inefficient (all Strings are repeatedly concatenated, which creates a new String object at each iteration). It would be more efficient to use joining() as follows (which internally makes use of a StringBuilder)
            String traderStr2 = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .collect(joining());
            System.out.println(traderStr1);

            // Query 5: Are there any trader based in Milan?
            boolean milanBased = transactions.stream()
                .anyMatch(transaction -> transaction.getTrader().getCity().equals("Milan"));
            System.out.println(milanBased);

            // Query 6: Print all transactions' values from the traders living in Cambridge.
            transactions.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity()))
                .map(Transaction::getValue)
                .forEach(System.out::println);

            // Query 7: What's the highest value in all the transactions?
            int highestValue = transactions.stream()
                .map(Transaction::getValue)
                .reduce(0, Integer::max);
            System.out.println(highestValue);

            // Query 8: Find the transaction with the smallest value
            Optional<Transaction> smallestTransaction1 = transactions.stream()
                .reduce((t1, t2) -> t1.getValue() < t2.getValue() ? t1 : t2);
            // or
            Optional<Transaction> smallestTransaction2 = transactions.stream()
                .min(comparing(Transaction::getValue));
            // Convert the found Transaction (if any) to a String so it can make use of a default String if no transactions are found (i.e. the Stream is empty).
            System.out.println(
                smallestTransaction2
                    .map(String::valueOf)
                    .orElse("No transactions found")
            );
        }
    }

    public class NumericStreams {
    // There are three primitive specialized stream interfaces to tackle this issue, IntStream, DoubleStream, and LongStream, which respectively specialize the elements of a stream to be int, long, and double—and thereby avoid hidden boxing costs. Each of these interfaces brings new methods to perform common numeric reductions, such as sum to calculate the sum of a numeric stream and max to find the maximum element. In addition, they have methods to convert back to a stream of objects when necessary. The thing to remember is that the additional complexity of these specializations isn’t inherent to streams. It reflects the complexity of boxing—the (efficiency-based) difference between int and Integer and so on.
        public static void numericStreams() {
            // This is not efficient due to Integer to int conversion right here .map(Dish::getCalories) before additive operation, which perform addition
            int caloriesInEfficient = menu.stream()
                .map(Dish::getCalories)
                .reduce(0, Integer::sum);
            // So mapToInt to the rescue
            int calories = menu.stream() // Returns a Stream<Dish>
                .mapToInt(Dish::getCalories) // Returns an IntStream, not a Stream<Integer>
                //  If the stream were empty, sum would return 0 by default
                .sum();
            System.out.println("Number of calories:" + calories);

            IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
            Stream<Integer> boxedGeneralStream = intStream.boxed();

            // Since where is no default value for max like method for the obvious reason, there’s a primitive specialized version of Optional as well for the three primitive stream specializations: OptionalInt, OptionalDouble, and OptionalLong.
            OptionalInt maxCalories = menu.stream()
                .mapToInt(Dish::getCalories)
                .max();
            if (maxCalories.isPresent()) { int max = maxCalories.getAsInt(); }
            else { }
            // or
            int max = maxCalories.orElse(1);

            // Numeric ranges, for generators. Range method is exclusive, rangeClosed is inclusive.
            IntStream evenNumbers = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0);

            // A complex example
            Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, 100).boxed()
                .flatMap(a -> IntStream.rangeClosed(a, 100)
                .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0).boxed()
                .map(b -> new int[] { a, b, (int) Math.sqrt(a * a + b * b) }));
            pythagoreanTriples.forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));

            Stream<int[]> pythagoreanTriples2 = IntStream.rangeClosed(1, 100).boxed()
                .flatMap(a -> IntStream.rangeClosed(a, 100)
                .mapToObj(b -> new double[]{a, b, Math.sqrt(a * a + b * b)})
                .filter(t -> t[2] % 1 == 0))
                .map(array -> Arrays.stream(array).mapToInt(a -> (int) a).toArray());
            pythagoreanTriples2.forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
        }

        public static boolean isPerfectSquare(int n) {
            return Math.sqrt(n) % 1 == 0;
        }
    }

    public class BuildingStreams {
    // This section shows how you can create a stream from a sequence of values, from an array, from a file, and even from a generative function to create infinite streams.
        public static void buildingStreams() throws Exception {
            // Streams from values with Stream.of
            Stream<String> stream = Stream.of("Java 8", "Lambdas", "In", "Action");
            stream.map(String::toUpperCase).forEach(System.out::println);

            // Stream.empty
            Stream<String> emptyStream = Stream.empty();

            // Stream from nullable.
            // ofNullable method lets you create a stream from a nullable object. After playing with streams, you may have encountered a situation where you extracted an object that may be null and then needs to be converted into a stream (or an empty stream for null). For example, the method System.getProperty returns null if there is no property with the given key. To use it together with a stream, you’d need to explicitly check for null as follows:
            String homeValue = System.getProperty("home");
            Stream<String> homeValueStream = homeValue == null ? Stream.empty() : Stream.of(homeValue);
            // or
            Stream<String> homeValueStreamNicer = Stream.ofNullable(System.getProperty("home"));
            // This pattern can be particularly handy in conjunction with flatMap and a stream of values that may include nullable objects:
            Stream<String> values = Stream.of("config", "home", "user")
                .flatMap(key -> Stream.ofNullable(System.getProperty(key)));

            // Streams from arrays with Arrays.stream
            int[] numbers = { 1, 2, 3, 5, 7, 11, 13 };
            System.out.println(Arrays.stream(numbers).sum());

            // Streams from functions: creating infinite streams.
            // The Streams API provides two static methods to generate a stream from a function: Stream.iterate and Stream.generate. These two operations let you create what we call an infinite stream, a stream that doesn’t have a fixed size like when you create a stream from a fixed collection. Streams produced by iterate and generate create values on demand given a function and can therefore calculate values forever! It’s generally sensible to use limit(n) on such streams to avoid printing an infinite number of values.
            // Stream.iterate.
            // This operation produces an infinite stream—the stream doesn’t have an end because values are computed on demand and can be computed forever. We say the stream is unbounded. This is a key difference between a stream and a collection. Using the limit method explicitly limit the size of the stream - select only the first 10 even numbers.
            Stream.iterate(0, n -> n + 2)
                .limit(10)
                .forEach(System.out::println);
            // The iterate method takes an initial value, here 0, and a lambda (of type UnaryOperator<T>) to apply successively on each new value produced. Here the iterate method produces a stream of all even numbers (returns the previous element added with 2 using the lambda n -> n + 2).

            // Fibonacci with iterate to generate the first 20 elements of the series of Fibonacci tuples like this (0, 1), (1, 1), (1, 2), (2, 3), (3, 5), (5, 8), (8, 13), (13, 21) ...
            Stream.iterate(new int[] { 0, 1 }, t -> new int[] { t[1], t[0] + t[1] })
                .limit(10)
                .forEach(t -> System.out.printf("(%d, %d)", t[0], t[1]));

            Stream.iterate(new int[] { 0, 1 }, t -> new int[] { t[1], t[0] + t[1] })
                .limit(10)
                // map to extract only the first element of each tuple.
                .map(t -> t[0])
                // This code will produce the Fibonacci series: 0, 1, 1, 2, 3, 5, 8, 13, 21...
                .forEach(System.out::println);

            // In Java 9, the iterate method was enhanced with support for a predicate. For example, you can generate numbers starting at 0 but stop the iteration once the number is greater than 100:
            IntStream.iterate(0, n -> n < 100, n -> n + 4)
                .forEach(System.out::println);
            // or
            IntStream.iterate(0, n -> n + 4)
                // filter(n -> n < 100) instead of takeWhile couldn't work, it can't terminate iterating.
                .takeWhile(n -> n < 100)
                .forEach(System.out::println);

            // Stream.generate.
            // Similarly to the method iterate, the method generate lets you produce an infinite stream of values computed on demand. But generate doesn’t apply successively a function on each new produced value. It takes a lambda of type Supplier<T> to provide new values. A supplier can be stateless and stateful. But it’s important to note that a supplier that’s stateful isn’t safe to use in parallel code.
            // Random stream of doubles with Stream.generate.
            Stream.generate(Math::random)
                .limit(10)
                .forEach(System.out::println);
            // Stream of 1s with Stream.generate
            IntStream.generate(() -> 1)
                .limit(5)
                .forEach(System.out::println);
            // Previous example rewritten with explicit object creation.
            IntStream.generate(new IntSupplier() {
            // The generate method will use the given supplier and repeatedly call the getAsInt method, which always returns 1. But the difference between the anonymous class used here and a lambda is that the anonymous class can define state via fields, which the getAsInt method can modify. This is an example of a side effect. All lambdas you’ve seen so far were side-effect free; they didn’t change any state.
                @Override
                public int getAsInt() { return 1; }
            })
                .limit(5)
                .forEach(System.out::println);

            // Here IntSupplier maintains its state the previous value in the series, so getAsInt can use it to calculate the next element. In addition, it can update the state of the IntSupplier for the next time it’s called. The following code shows how to create an IntSupplier that will return the next Fibonacci element when it’s called.
            IntSupplier fib = new IntSupplier() {
                //  This object has a mutable states.
                private int previous = 0;
                private int current = 1;
                @Override
                public int getAsInt() {
                    int nextValue = previous + current;
                    previous = current;
                    current = nextValue;
                    return previous;
                }
            };
            IntStream.generate(fib)
                .limit(10)
                .forEach(System.out::println);
            // Note that:
            //  1. you should always prefer an immutable approach in order to process a stream in parallel and expect a correct result;
            //  2. because you’re dealing with a stream of infinite size, you have to limit its size explicitly using the operation limit; otherwise, the terminal operation (forEach for instance) will compute forever;
            //  3. you can’t sort or reduce an infinite stream because all elements need to be processed.

            // Streams from files.
            // Java’s NIO API (non-blocking I/O), which is used for I/O operations such as processing a file, has been updated to take advantage of the Streams API. Many static methods in java.nio.file.Files return a stream. For example, a useful method is Files.lines, which returns a stream of lines as strings from a given file.
            long uniqueWords1 = Files.lines(
                    Paths.get("lambdas/src/main/resources/org/example/java_learning/data.txt")
                    , Charset.defaultCharset()
                )
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .distinct()
                .count();
            // or
            long uniqueWords2 = 0;
            // Streams are AutoCloseable, so there’s no need for try-finally
            try (
                Stream<String> lines = Files.lines(
                    Paths.get("lambdas/src/main/resources/org/example/java_learning/data.txt")
                    , Charset.defaultCharset()
                )
            ) {
                uniqueWords2 = lines.flatMap(line -> Arrays.stream(line.split(" ")))
                    .distinct()
                    .count();
            } catch (IOException ex) {
                System.out.println("File data.txt not found");
            }
            System.out.println("There are " + uniqueWords1 + " unique words in data.txt");
        }
    }

    // Collecting data with streams.
    public class CollectStreams {
        public static void collectStreams() {
            // Group transactions based on their nominal currency.
            List<TransactionCV> transactions = Arrays.asList(
                new TransactionCV(Currency.EUR, 1500.0),
                new TransactionCV(Currency.USD, 2300.0),
                new TransactionCV(Currency.GBP, 9900.0),
                new TransactionCV(Currency.EUR, 1100.0),
                new TransactionCV(Currency.JPY, 7800.0),
                new TransactionCV(Currency.CHF, 6700.0),
                new TransactionCV(Currency.EUR, 5600.0),
                new TransactionCV(Currency.USD, 4500.0),
                new TransactionCV(Currency.CHF, 3400.0),
                new TransactionCV(Currency.GBP, 3200.0),
                new TransactionCV(Currency.USD, 4600.0),
                new TransactionCV(Currency.JPY, 5700.0),
                new TransactionCV(Currency.EUR, 6800.0)
            );
            // Imperatively.
            Map<Currency, List<TransactionCV>> transactionsByCurrencies = new HashMap<>();
            for (TransactionCV transaction : transactions) {
                Currency currency = transaction.getCurrency();
                List<TransactionCV> transactionsForCurrency = transactionsByCurrencies.get(currency);
                if (transactionsForCurrency == null) {
                    transactionsForCurrency = new ArrayList<>();
                    transactionsByCurrencies.put(currency, transactionsForCurrency);
                }
                transactionsForCurrency.add(transaction);
            }
            System.out.println(transactionsByCurrencies);
            // Functionally.
            Map<Currency, List<TransactionCV>> transactionsByCurrenciesFn = transactions.stream()
                .collect(groupingBy(TransactionCV::getCurrency));
            System.out.println(transactionsByCurrenciesFn);
            // The previous example clearly shows one of the main advantages of functional-style programming over an imperative approach: you have to formulate the result you want to obtain the "what" and not the steps performed to obtain it, the "how". In the previous example, the argument passed to the collect method is an implementation of the Collector interface, which is a recipe for how to build a summary of the elements in the stream. Previously the toList recipe said, "Make a list of each element in turn". In this example, the groupingBy recipe says, "Make a Map whose keys are (currency) buckets and whose values are a list of elements in those buckets".
            // The difference between the imperative and functional versions of this example is even more pronounced if you perform multilevel groupings: in that case the imperative code quickly becomes harder to read, maintain, and modify due to the number of deeply nested loops and conditions required. In comparison, the functional-style version, as you’ll discover later, can be easily enhanced with an additional collector.

            // Collectors (the parameters to the stream method collect) like this:
            long howManyDishesVerbose = menu.stream().collect(Collectors.counting());
            // same as, but simpler
            long howManyDishes = menu.stream().count();
            // or
            List<TransactionCV> someResultDataStructure =
                    transactions.stream().collect(Collectors.toList());
            // are typically used in cases where it’s necessary to reorganize the stream’s items into a collection. But more generally, they can be used every time you want to combine all the items in the stream into a single result. This result can be of any type, as complex as a multilevel map representing a tree or as simple as a single integer, perhaps representing the sum of all the calories in the menu.

            // Finding maximum and minimum in a stream of values with maxBy and minBy.
            // Find the highest-calorie dish in the menu.
            Dish mostCaloricDish =
                menu.stream().collect(reducing(
                    (d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2)
                ).get();
            // or
            Comparator<Dish> dishCaloriesComparator =
                comparingInt(Dish::getCalories);
            BinaryOperator<Dish> moreCaloricOf =
                BinaryOperator.maxBy(dishCaloriesComparator);
            Optional<Dish> mostCalorieDish =
                    menu.stream().collect(maxBy(dishCaloriesComparator));
            // or
            Dish mostCaloricDishUsingComparator =
                menu.stream().collect(reducing(moreCaloricOf)).get();
            // putting together
            Dish mostCaloricDishAllInOne = menu.stream().collect(reducing(
                BinaryOperator.maxBy(comparingInt(Dish::getCalories))
            )).get();

            // Summarization with summingInt, summingLong, summingDouble, averagingInt, averagingLong, averagingDouble, summarizingInt, summarizingLong, summarizingDouble.
            int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
            double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));

            // It's possible to retrieve two or more of these results, and possibly you’d like to do it in a single operation. In this case, you can use the collector returned by the summarizingInt factory method. For example, you can count the elements in the menu and obtain the sum, average, maximum, and minimum of the calories contained in each dish with a single summarizing operation:
            IntSummaryStatistics menuStatistics = menu.stream()
                .collect(summarizingInt(Dish::getCalories));
            // output would be like this:
            //  IntSummaryStatistics{count=9, sum=4300, min=120, average=477.777778, max=800}

            // Joining Strings.
            // Note that joining internally makes use of a StringBuilder to append the generated strings into one.
            String shortMenu = menu.stream().map(Dish::getName).collect(joining());
            // or
            String shortMenuCommaSeparated = menu.stream()
                .map(Dish::getName).collect(joining(", "));

            // Generalized summarization with reduction.
            // All the collectors above, in reality, only convenient specializations of a reduction process that can be defined using the reducing factory method. The Collectors.reducing factory method is a generalization of all of them. For instance, it’s possible to calculate the total calories in your menu with a collector created from the reducing method as follows:
            int totalCaloriesWithLambda = menu.stream().collect(reducing(
            0, Dish::getCalories, (i, j) -> i + j));
            // It takes three arguments:
            // - The first argument is the starting value of the reduction operation and will also be the value returned in the case of a stream with no elements, so clearly 0 is the appropriate value in the case of a numeric sum.
            // - The second argument is the function to transform a dish into an int representing its calorie content.
            // - The third argument is a BinaryOperator that aggregates two items into a single value of the same type. Here, it sums two ints.

            // Similarly, you could find the highest-calorie dish using the one-argument version of reducing as follows:
            Optional<Dish> mostCalorieDishWithLambda = menu.stream().collect(reducing(
                // You can think of the collector created with the one-argument reducing factory method as a particular case of the three-argument method, which uses the first item in the stream as a starting point and an identity function (a function that returns its input argument as is) as a transformation function. This also implies that the one-argument reducing collector won’t have any starting point when passed to the collect method of an empty stream and for this reason it returns an Optional<Dish> object.
                (d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));

            // Collect vs. reduce.
            // What the differences between the collect and reduce methods of the stream interface are, because often you can obtain the same results using either method? For instance, you can achieve what is done by the toList Collector using the reduce method as follows:
            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6).stream()
                .reduce(
                    new ArrayList<Integer>(),
                    (List<Integer> l, Integer e) -> {
                        l.add(e);
                        return l;
                    },
                    (List<Integer> l1, List<Integer> l2) -> {
                        l1.addAll(l2);
                        return l1;
                    }
                );
            numbers.forEach(System.out::println);
            // This solution has two problems: a semantic one and a practical one. The semantic problem lies in the fact that the reduce method is meant to combine two values and produce a new one; it’s an immutable reduction. In contrast, the collect method is designed to mutate a container to accumulate the result it’s supposed to produce. This means that the previous snippet of code is misusing the reduce method, because it’s mutating in place the List used as accumulator. Using the reduce method with the wrong semantic is also the cause of a practical problem: this reduction process can’t work in parallel, because the concurrent modification of the same data structure operated by multiple threads can corrupt the List itself. In this case, if you want thread safety, you’ll need to allocate a new List every time, which would impair performance by object allocation. This is the main reason why the collect method is useful for expressing reduction working on a mutable container but crucially in a parallel-friendly way.

            // Collection framework flexibility: doing the same operation in different ways.
            // Here
            long dishesQuantity = menu.stream().collect(Collectors.counting());
            // looking like this:
            //public static <T> Collector<T, ?, Long> counting() {
            //  return reducing(0L, e -> 1L, Long::sum);
            //}

            int calculateTotalCaloriesWithLambda =
                menu.stream().collect(reducing(
            0, Dish::getCalories, (Integer i, Integer j) -> i + j
                ));

            int calculateTotalCaloriesWithMethodReference =
                menu.stream().collect(reducing(
            0, Dish::getCalories, Integer::sum
                ));

            int calculateTotalCaloriesWithoutCollectors =
                menu.stream().map(Dish::getCalories).reduce(Integer::sum).get();

            int calculateTotalCaloriesUsingSum =
                menu.stream().mapToInt(Dish::getCalories).sum();

            // Note that, like any one-argument reduce operation on a stream, the invocation reduce(Integer::sum) doesn’t return an int but an Optional<Integer> to manage the case of a reduction operation over an empty stream in a null-safe way. Here you extract the value inside the Optional object using its get method. In this case using the get method is safe only because you’re sure that the stream of dishes isn’t empty. In general, it’s safer to unwrap the value eventually contained in an Optional using a method that also allows you to provide a default, such as orElse or orElseGet.

            // Choosing the best solution for your situation.
            // The above examples shows that collectors are somewhat more complex to use than the methods directly available on the Streams interface, but in exchange they offer higher levels of abstraction and generalization and are more reusable and customizable.
            // How to choose? Explore the largest number of solutions possible to the problem at hand, but always choose the most specialized one that’s general enough to solve it. This is often the best decision for both readability and performance reasons. For instance, to calculate the total calories in our menu, we’d prefer the last solution (using IntStream) because it’s the most concise and likely also the most readable one. At the same time, it’s also the one that performs best, because IntStream lets us avoid all the auto-unboxing operations, or implicit conversions from Integer to int, that are useless in this case.
            // Which one of these?:
            String shortMenu1 = menu.stream().map(Dish::getName).collect(joining());
            String shortMenu2 = menu.stream().map(Dish::getName).collect(reducing(
                (s1, s2) -> s1 + s2 )
            ).get();
            String shortMenu3 = menu.stream().collect(reducing(
        "", Dish::getName, (s1, s2) -> s1 + s2
            ));

            // Grouping.
            // Suppose you want to classify the dishes in the menu according to their type, putting the ones containing meat in a group, the ones with fish in another group, and all others in a third group.
            Map<Dish.Type, List<Dish>> groupDishesByType = menu.stream().collect(
                groupingBy(Dish::getType));
            // Here Dish::getType acts as a classification function. The result of this grouping operation is a Map having as map key the value returned by the classification function and as corresponding map value a list of all the items in the stream having that classified value.
            System.out.println("Dishes grouped by type: " + groupDishesByType);
            // Sometimes author of the target class, in this case Dish class, didn’t provide such an operation as a method, so you can’t use a method reference in this case. Use lambdas.
            // For instance, classify as "diet" all dishes with 400 calories or fewer, set to "normal" the dishes having between 400 and 700 calories, and set to "fat" the ones with more than 700 calories.
            Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
                groupingBy(dish -> {
                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                    else return CaloricLevel.FAT;
                } ));

            // Manipulating grouped elements.
            // To filter only the caloric dishes, the ones with more than 500 calories, you may argue that in this case you could apply this filtering predicate before the grouping like the following:
            Map<Dish.Type, List<Dish>> caloricDishesByTypeFilterSource = menu.stream()
                .filter(dish -> dish.getCalories() > 500)
                .collect(groupingBy(Dish::getType));
            // but in our case it gives {OTHER=[french fries, pizza], MEAT=[pork, beef]}, which doesn't have the type FISH.
            // To workaround this problem the Collectors class overloads the groupingBy factory method, with one variant also taking a second argument of type Collector along with the usual classification function. In this way, it’s possible to move the filtering predicate inside this second Collector, as follows:
            Map<Dish.Type, List<Dish>> caloricDishesByTypeFilterWhileGrouping = menu.stream().collect(
                groupingBy(
                    Dish::getType
                    , filtering(
                        dish -> dish.getCalories() > 500
                        // List<Dish>, toList Collector regroups the filtered elements.
                        , toList()
                    )
                )
            );
            // This one gives: {OTHER=[french fries, pizza], MEAT=[pork, beef], FISH=[]}
            // Similarly you can manipulate grouped elements transforming them through a mapping function.
            Map<Dish.Type, List<String>> dishNamesByType = menu.stream().collect(
                groupingBy(
                    Dish::getType // Dish.Type
                    , mapping(Dish::getName, toList()) // List<String>
                )
            );
            // To develop this idea there is flatMap transformation instead of a plain map. To demonstrate how this works let’s suppose that we have a Map associating to each Dish a list of tags Dish.dishTags:
            Map<Dish.Type, Set<String>> dishTagsByType = menu.stream().collect(
                groupingBy(
                    Dish::getType
                    // flatMap flattens the resulting two-level list into a single one and toSet eliminates repetitions.
                    , flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())
                )
            );
            System.out.println("Dish tags grouped by type: " + dishTagsByType);

            // Multilevel grouping.
            // Working with more than one criterion at the same time.
            // Grouping is powerful because it composes effectively.
            Map<Dish.Type, Map<CaloricLevel, List<Dish>>> groupDishedByTypeAndCaloricLevel = menu.stream().collect(
                // This multilevel grouping operation can be extended to any number of levels, and an n-level grouping has as a result an n-level Map, modeling an n-level tree structure.
                groupingBy(Dish::getType,
                    // the next groupingBy is a downstream collector for the first groupingBy and so on.
                    groupingBy((Dish dish) -> {
                        if (dish.getCalories() <= 400) {
                            return CaloricLevel.DIET;
                        }
                        else if (dish.getCalories() <= 700) {
                            return CaloricLevel.NORMAL;
                        }
                        else {
                            return CaloricLevel.FAT;
                        }
                    })
                )
            );
            System.out.println("Dishes grouped by type and caloric level: " + groupDishedByTypeAndCaloricLevel);

            // Collecting data in subgroups.
            // By passing to groupingBy Collector another Collector (above was another groupingBy), for instance, counting, we can count the number of Dishes in the menu for each type like this {MEAT=3, FISH=2, OTHER=4}:
            Map<Dish.Type, Long> countDishesInGroups = menu.stream().collect(
                groupingBy(Dish::getType, counting()));
            // Note that the regular one-argument groupingBy(f), where f is the classification function is, in reality, shorthand for groupingBy(f, toList()).
            // Another example: find the highest-calorie dish in the menu classified by the type of dish like this {FISH=Optional[salmon], OTHER=Optional[pizza], MEAT=Optional[pork]}:
            Map<Dish.Type, Optional<Dish>> mostCaloricDishesByType = menu.stream().collect(
                groupingBy(
                    Dish::getType
                    , maxBy(comparingInt(Dish::getCalories)) // Optional<Dish>
                    // Note that the values in this Map are Optionals because this is the resulting type of the collector generated by the maxBy factory method, but in reality if there’s no Dish in the menu for a given type, that type won’t have an Optional.empty() as value; it won’t be present at all as a key in the Map. The groupingBy collector lazily adds a new key in the grouping Map only the first time it finds an element in the stream, producing that key when applying on it the grouping criteria being used. This means that in this case, the Optional wrapper isn’t useful, because it’s not modeling a value that could be possibly absent but is there incidentally, only because this is the type returned by the reducing collector.
                )
            );
            // Same as
            Map<Dish.Type, Optional<Dish>> mostCaloricDishesByTypeLambda = menu.stream().collect(
                groupingBy(Dish::getType,
                    reducing((Dish d1, Dish d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2)
                )
            );
            System.out.println("Most caloric dishes grouped by type: " + mostCaloricDishesByType);
            System.out.println("Most caloric dishes grouped by type: " + mostCaloricDishesByTypeLambda);

            // Adapting the collector result to a different type.
            // It’s quite common to use multiple nested collectors.
            Map<Dish.Type, Dish> mostCaloricDishesByTypeWithoutOprionals = menu.stream().collect(
                groupingBy(
                    // The original stream is divided into substreams according to the classification function which is Dish::getType.
                    Dish::getType
                    // Each substream independently processed by the second reducing and the first wrapped collector which is collectingAndThen.
                    , collectingAndThen(
                        // The third and the second wrapped collector, which is maxBy, returns the dishes in an Optional form.
                        maxBy(comparingInt(Dish::getCalories))
                        // Transformation function is safe because the lazy reducing collector will never return an Optional.empty().
                        , Optional::get
                    ) // collectingAndThen collector returns the value extracted from the former Optional.
                ) // Here  results of the second-level collectors become the values of the grouping map.
            );
            // Same as
            Map<Dish.Type, Dish> mostCaloricDishesByTypeWithoutOprionalsLambda = menu.stream().collect(
                groupingBy(
                    Dish::getType
                    , collectingAndThen(
                        reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2)
                        , Optional::get
                    )
                )
            );

            // Other examples of collectors used in conjunction with groupingBy.
            Map<Dish.Type, Integer> sumCaloriesByType = menu.stream().collect(
                groupingBy(
                    Dish::getType
                    , summingInt(Dish::getCalories)
                )
            );
            // There is mapping method that takes two arguments: a function transforming the elements in a stream and a further collector accumulating the objects resulting from this transformation. Its purpose is to adapt a collector accepting elements of a given type to one working on objects of a different type, by applying a mapping function to each input element before accumulating them.
            Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream().collect(
                groupingBy(
                    Dish::getType
                    , mapping(
                        // Here the transformation function passed to the mapping method maps a Dish into its CaloricLevel.
                        dish -> {
                            if (dish.getCalories() <= 400) { return CaloricLevel.DIET; }
                            else if (dish.getCalories() <= 700) { return CaloricLevel.NORMAL; }
                            else { return CaloricLevel.FAT; }
                        },
                        toSet()
                    )
                )
            );
            // Note that in the previous example, there are no guarantees about what type of Set is returned. But by using toCollection, you can have more control. For example, you can ask for a HashSet by passing a constructor reference to it:
            Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByTypeHashSet = menu.stream().collect(
                groupingBy(
                    Dish::getType
                    , mapping(
                        dish -> {
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        }
                        , toCollection(HashSet::new)
                    )
                )
            );


            // Partitioning.
            // Partitioning is a special case of grouping: having a predicate called a partitioning function as a classification function. The fact that the partitioning function returns a boolean means the resulting grouping Map will have a Boolean as a key type, and therefore, there can be at most two different groups—one for true and one for false.
            Map<Boolean, List<Dish>> partitionByVegeterian = menu.stream().collect(
                partitioningBy(Dish::isVegetarian));
            System.out.println("Dishes partitioned by vegetarian: " + partitionByVegeterian);
            // gives: {false=[pork, beef, chicken, prawns, salmon], true=[french fries, rice, season fruit, pizza]}
            List<Dish> vegetarianDishes = partitionByVegeterian.get(true);

            Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = menu.stream().collect(
                partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));

            Object mostCaloricPartitionedByVegetarian = menu.stream().collect(
                partitioningBy(
                    Dish::isVegetarian
                    , collectingAndThen(
                        maxBy(comparingInt(Dish::getCalories))
                        , Optional::get
                    )
                )
            );
            // Multilevel partitioning.
            Map<Boolean, Map<Boolean, List<Dish>>> partitionByVegeterianAndHighCaloric = menu.stream().collect(
                partitioningBy(Dish::isVegetarian,
                    partitioningBy(d -> d.getCalories() > 500)));
            System.out.println("Is dishes vegetarian and high caloric: " + partitionByVegeterianAndHighCaloric);

            // Partitioning numbers into prime and nonprime.
            // Suppose you want to write a method accepting as argument an int n and partitioning the first n natural numbers into prime and nonprime. But first, it will be useful to develop a predicate that tests to see if a given candidate number is prime or not:
            /*
            boolean isPrime(int candidate) {
                return IntStream
                    // Generates a range of natural numbers starting from and including 2, up to but excluding candidate.
                    .range(2, candidate)
                    // Returns true if the candidate isn’t divisible for any of the numbers in the stream.
                    .noneMatch(i -> candidate % i == 0);
            }
            */
            // Check if n is prime:
            int n = 100;
            Map<Boolean, List<Integer>> partitionPrimes = IntStream.rangeClosed(2, n).boxed().collect(
                partitioningBy(candidate -> Prime.isPrimeLittleOptimized(candidate)));
            System.out.println("In a range of " + n + " the primes are: " + partitionPrimes);

        }
        // The Collector interface. Custom collectors.
        /*
        The Collector interface consists of a set of methods that provide a blueprint for how to implement specific reduction operations (collectors). You’ve seen many collectors that implement the Collector interface, such as toList or groupingBy. This also implies that you’re free to create customized reduction operations by providing your own implementation of the Collector interface.
        The Collector interface defined as:
            //public interface Collector<T, A, R> {
            //    Supplier<A> supplier();
            //    BiConsumer<A, T> accumulator();
            //    Function<A, R> finisher();
            //    BinaryOperator<A> combiner();
            //    Set<Characteristics> characteristics();
            //}
         Here:
          - T is the generic type of the items in the stream to be collected.
          - A is the type of the accumulator, the object on which the partial result will be accumulated during the collection process.
          - R is the type of the object (typically, but not always, the collection) resulting from the collect operation.
         For instance, you could implement a ToListCollector<T> class that gathers all the elements of a Stream<T> into a List<T> having the following signature:
         //public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
        Each of the first four methods declared by the Collector interface returns a function that will be invoked by the collect method, whereas the fifth one, characteristics, provides a set of characteristics that’s a list of hints used by the collect method itself to know which optimizations (for example, parallelization) it’s allowed to employ while performing the reduction operation.

        Making a new result container: the supplier method.
        The supplier method has to return a Supplier of an empty accumulator—a parameterless function that when invoked creates an instance of an empty accumulator used during the collection process. Clearly, for a collector returning the accumulator itself as result, like our ToListCollector, this empty accumulator will also represent the result of the collection process when performed on an empty stream. In our ToListCollector the supplier will then return an empty List, as follows:
            //public Supplier<List<T>> supplier() {
            //    return () -> new ArrayList<T>();
            //}
         Note that you could also pass a constructor reference:
            //public Supplier<List<T>> supplier() {
            //    return ArrayList::new;
            //}

        Adding an element to a result container: the accumulator method.
        The accumulator method returns the function that performs the reduction operation. When traversing the nth element in the stream, this function is applied with two arguments, the accumulator being the result of the reduction (after having collected the first n–1 items of the stream) and the nth element itself. The function returns void because the accumulator is modified in place, meaning that its internal state is changed by the function application to reflect the effect of the traversed element. For ToListCollector, this function merely has to add the current item to the list containing the already traversed ones:
            //public BiConsumer<List<T>, T> accumulator() {
            //    return (list, item) -> list.add(item);
            //}
         You could instead use a method reference, which is more concise:
            //public BiConsumer<List<T>, T> accumulator() {
            //    return List::add;
            //}

        Applying the final transformation to the result container: the finisher method.
        The finisher method has to return a function that’s invoked at the end of the accumulation process, after having completely traversed the stream, in order to transform the accumulator object into the final result of the whole collection operation. Often, as in the case of the ToListCollector, the accumulator object already coincides with the final expected result. As a consequence, there’s no need to perform a transformation, so the finisher method has to return the identity function:
            //public Function<List<T>, List<T>> finisher() {
            //    return Function.identity();
            //}

        These first three methods are enough to execute a sequential reduction of the stream that, at least from a logical point of view, could proceed like this:
        [start 1]
        ->[A accumulator = collector.supplier().get()]
          -> while (there are more items in the stream) {
                [T next = fetch next stream's item]
                [collector.accumulator().accept(accumulator, next)]
             }
             ->[R result = collector.finisher().apply(accumulator)]
                ->[return result]
        [end 1]
        The implementation details are a bit more difficult in practice due to both the lazy nature of the stream, which could require a pipeline of other intermediate operations to execute before the collect operation, and the possibility, in theory, of performing the reduction in parallel.

        Merging two result containers: the combiner method.
        The combiner method, the last of the four methods that return a function used by the reduction operation, defines how the accumulators resulting from the reduction of different subparts of the stream are combined when the subparts are processed in parallel. In the toList case, the implementation of this method is simple; add the list containing the items gathered from the second subpart of the stream to the end of the list obtained when traversing the first subpart:
            //public BinaryOperator<List<T>> combiner() {
            //    return (list1, list2) -> {
            //        list1.addAll(list2);
            //        return list1;
            //    }
            //}
        The parallel processing could look like this:
        [start 2]
        ->[Split the stream in 2 subparts]
          ->[Keep dividing the streams until each subpart is small enough to be processed using the former sequential algorithm]
            ->[Process each subpart in parallel using the former sequential algorithm]
              [R r1 = collector.combiner().apply(acc1, acc2)]
              [R r2 = collector.combiner().apply(acc3, acc4)]
              ...
              [R rN = collector.combiner().apply(accM, accK)]
              ->[Combine the results of the independent processing of each substream]
                [A accumulator = collector.combiner().apply(r1, r2)]
                ...
                ->[R result = collector.finisher().apply(accumulator)]
                  ->[return result]
        [end 2]
          - The original stream is recursively split in substreams until a condition defining whether a stream needs to be further divided becomes false (parallel computing is often slower than sequential computing when the units of work being distributed are too small, and it’s pointless to generate many more parallel tasks than you have processing cores).
          - At this point, all substreams can be processed in parallel, each of them using the sequential reduction algorithm shown in [start 1].
          - Finally, all the partial results are combined pairwise using the function returned by the combiner method of the collector. This is done by combining results corresponding to substreams associated with each split of the original stream.
         The addition of this fourth method allows a parallel reduction of the stream. This uses the fork/join framework and the Spliterator abstraction.

        The characteristics method.
        The last method, characteristics, returns an immutable set of Characteristics, defining the behavior of the collector — in particular providing hints about whether the stream can be reduced in parallel and which optimizations are valid when doing so. Characteristics is an enumeration containing three items:
          - UNORDERED. The result of the reduction isn’t affected by the order in which the items in the stream are traversed and accumulated.
          - CONCURRENT. The accumulator function can be called concurrently from multiple threads, and then this collector can perform a parallel reduction of the stream. If the collector isn’t also flagged as UNORDERED, it can perform a parallel reduction only when it’s applied to an unordered data source.
          - IDENTITY_FINISH. This indicates the function returned by the finisher method is the identity one, and its application can be omitted. In this case, the accumulator object is directly used as the final result of the reduction process. This also implies that it’s safe to do an unchecked cast from the accumulator A to the result R.
        The ToListCollector developed so far is IDENTITY_FINISH, because the List used to accumulate the elements in the stream is already the expected final result and doesn’t need any further transformation, but it isn’t UNORDERED because if you apply it to an ordered stream you want this ordering to be preserved in the resulting List. Finally, it’s CONCURRENT, but following what we just said, the stream will be processed in parallel only if its underlying data source is unordered.

        */
        // Complete example.
        public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {
            @Override
            public Supplier<List<T>> supplier() {
                // Creates the collection operation starting point.
                //return () -> new ArrayList<T>();
                return ArrayList::new;
            }
            @Override
            public BiConsumer<List<T>, T> accumulator() {
                // Accumulates the traversed item, modifying the accumulator in place.
                //return (list, item) -> list.add(item);
                return List::add;
            }
            @Override
            public Function<List<T>, List<T>> finisher() {
                // Identifies function.
                //return i -> i;
                return Function.identity();
            }
            @Override
            public BinaryOperator<List<T>> combiner() {
                return (list1, list2) -> {
                    // Modifies the first accumulator, combining it with the content of the second one.
                    list1.addAll(list2);
                    // Returns the modified first accumulator.
                    return list1;
                };
            }
            @Override
            public Set<Characteristics> characteristics() {
                // Flags the collector as IDENTITY_FINISH and CONCURRENT.
                return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
            }
        }
        // Using custom toList.
        List<Dish> dishes1 = menu.stream().collect(new ToListCollector<Dish>());
        // Standard java toList.
        List<Dish> dishes2 = menu.stream().collect(toList());
        // The remaining difference from this and the standard formulation is that toList is a factory, whereas you have to use new to instantiate your ToListCollector.

        // Performing a custom collect without creating a collector implementation.
        // In the case of an IDENTITY_FINISH collection operation, there’s a further possibility of obtaining the same result without developing a completely new implementation of the Collector interface. Streams has an overloaded collect method accepting the three other functions— supplier, accumulator, and combiner—having exactly the same semantics as the ones returned by the corresponding methods of the Collector interface. For instance, it’s possible to collect in a List all the items in a stream of dishes, as follows:
        List<Dish> dishes = menu.stream().collect(
            ArrayList::new, // Supplier
            List::add,      // Accumulator
            List::addAll    // Combiner
        );
        //  This second form, even if more compact and concise than the former one, is rather less readable. Also, developing an implementation of your custom collector in a proper class promotes its reuse and helps avoid code duplication. It’s also worth noting that you’re not allowed to pass any Characteristics to this second collect method, so it always behaves as an IDENTITY_FINISH and CONCURRENT but not UNORDERED collector.


        /*
        Back to Primes example. Developing your own collector for better performance.
         One possible optimization is to test only if the candidate number is divisible by prime numbers. It’s pointless to test it against a divisor that’s not itself prime! You can limit the test to only the prime numbers found before the current candidate. The problem with the predefined collectors you’ve used so far, and the reason you have to develop a custom one, is that during the collecting process you don’t have access to the partial result. This means that when testing whether a given candidate number is prime or not, you don’t have access to the list of the other prime numbers found so far.
         Also, you should implement the same optimization you used before and test only with primes smaller than the square root of the candidate number. You need a way to stop testing whether the candidate is divisible by a prime as soon as the next prime is greater than the candidate’s root. You can easily do this by using the Stream’s takeWhile method.
            //public static boolean isPrimePerformant(List<Integer> primes, int candidate){
            //    int candidateRoot = (int) Math.sqrt((double) candidate);
            //    return primes.stream()
            //        .takeWhile(i -> i <= candidateRoot)
            //        .noneMatch(i -> candidate % i == 0);
            //}

        Let’s start with the class signature, remembering that the Collector interface is defined as:
            //public interface Collector<T, A, R>
         where T, A, and R are respectively the type of the elements in the stream, the type of the object used to accumulate partial results, and the type of the final result of the collect operation. In this case, you want to collect streams of Integer s while both the accumulator and the result types are Map<Boolean, List<Integer>>, having as keys true and false and as values respectively the Lists of prime and nonprime numbers:
            //public class PrimeNumbersCollector implements Collector<
            //    Integer,
            //    Map<Boolean, List<Integer>>,
            //    Map<Boolean, List<Integer>>
            //>
        Next, you need to implement the five methods declared in the Collector interface. The supplier method has to return a function that when invoked creates the accumulator:
            //public Supplier<Map<Boolean, List<Integer>>> supplier() {
            //    return () -> new HashMap<Boolean, List<Integer>>() {{
            //        put(true, new ArrayList<Integer>());
            //        put(false, new ArrayList<Integer>());
            //    }};
            //}
        The most important method of your collector is the accumulator method, because it contains the logic defining how the elements of the stream have to be collected. In this case, it’s also the key to implementing the optimization we described previously. At any given iteration you can now access the partial result of the collection process, which is the accumulator containing the prime numbers found so far:
            //public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
            //    return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            //        // Gets the list of prime or nonprime numbers depending on the result of isPrime and adds the candidate to the appropriate list.
            //        acc.get(isPrime(acc.get(true), candidate)).add(candidate);
            //    };
            //}

        Making the collector work in parallel (if possible).
        The next method has to combine two partial accumulators in the case of a parallel collection process, so in this case it has to merge the two Maps by adding all the numbers in the prime and nonprime lists of the second Map to the corresponding lists in the first Map:
            //public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
            //    return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
            //        map1.get(true).addAll(map2.get(true));
            //        map1.get(false).addAll(map2.get(false));
            //        return map1;
            //    };
            //}
         Note that in reality this collector can’t be used in parallel, because the algorithm is inherently sequential. This means the combiner method won’t ever be invoked, and you could leave its implementation empty (or better, throw an UnsupportedOperationException).

        The finisher method and the collector’s characteristic method.
        In this case the accumulator coincides with the collector’s result, so it won’t need any further transformation, and the finisher method returns the identity function:
            //public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
            //    return Function.identity();
            //}
        The characteristic method neither CONCURRENT nor UNORDERED but is IDENTITY_FINISH:
            //public Set<Characteristics> characteristics() {
            //    return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
            //}
        */
        // All together:
        public static class PrimeNumbersCollector implements Collector<
            Integer,
            Map<Boolean, List<Integer>>,
            Map<Boolean, List<Integer>>
        > {
            @Override
            public Supplier<Map<Boolean, List<Integer>>> supplier() {
                return () -> new HashMap<>() {{
                    put(true, new ArrayList<Integer>());
                    put(false, new ArrayList<Integer>());
                }};
            }
            @Override
            public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
                return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
                    acc.get(Prime.isPrimePerformant(acc.get(true), candidate)).add(candidate);
                };
            }
            @Override
            public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
                return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
                    map1.get(true).addAll(map2.get(true));
                    map1.get(false).addAll(map2.get(false));
                    return map1;
                };
            }
            @Override
            public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
                //return Function.identity();
                return i -> i;
            }
            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
            }
        }

        // Example use:
        public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
            return IntStream.rangeClosed(2, n).boxed().collect(new PrimeNumbersCollector());
        }

        // Comparing collectors' performances, the collector created with the partitioningBy factory method and the custom one.
        public static void testCollectorsPerformances() {
            System.out.println("Partitioning done in: " + execute(CollectStreams::partitionPrimes) + " msecs");
            System.out.println("Partitioning done in: " + execute(CollectStreams::partitionPrimesWithCustomCollector) + " msecs");
        }
        public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
            return IntStream.rangeClosed(2, n).boxed().collect(
                partitioningBy(candidate -> Prime.isPrimeLittleOptimized(candidate)));
        }
        private static long execute(Consumer<Integer> primePartitioner) {
            long fastest = Long.MAX_VALUE;
            for (int i = 0; i < 10; i++) {
                long start = System.nanoTime();
                primePartitioner.accept(1_000_000);
                long duration = (System.nanoTime() - start) / 1_000_000;
                if (duration < fastest) {
                    fastest = duration;
                }
                System.out.println("done in " + duration);
            }
            return fastest;
        }

        // Same custom collector without creating a completely new class.
        public Map<Boolean, List<Integer>> partitionPrimesWithInlineCollector(int n) {
            return Stream.iterate(2, i -> i + 1).limit(n).collect(
                () -> new HashMap<Boolean, List<Integer>>() {{
                    put(true, new ArrayList<Integer>());
                    put(false, new ArrayList<Integer>());
                }},
                (acc, candidate) -> {
                    acc.get(Prime.isPrimePerformant(acc.get(true), candidate)).add(candidate);
                },
                (map1, map2) -> {
                    map1.get(true).addAll(map2.get(true));
                    map1.get(false).addAll(map2.get(false));
                }
            );
        }
    }

    public class StreamGatherers {
    /*
    https://openjdk.org/jeps/461 Stream Gatherers (Preview)

    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html Stream API
    https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherer.html Stream Gatherers API

    Enhance the Stream API to support custom intermediate operations. This will allow stream pipelines to transform data in ways that are not easily achievable with the existing built-in intermediate operations.

    Gatherers vs. collectors.
    The design of the {Gatherer} interface is heavily influenced by the design of {Collector}. The main differences are:
     - {Gatherer} uses an {Integrator} instead of a {BiConsumer} for per-element processing because it needs an extra input parameter for the {Downstream} object, and because it needs to return a boolean to indicate whether processing should continue.
     - {Gatherer} uses a {BiConsumer} for its finisher instead of a {Function} because it needs an extra input parameter for its {Downstream} object, and because it cannot return a result and thus is void.

    Goals {
     - Make stream pipelines more flexible and expressive.
     - Insofar as possible, allow custom intermediate operations to manipulate streams of infinite size.
    }

    Motivation {
     The Stream API provides a reasonably rich, albeit fixed, set of intermediate and terminal operations: mapping, filtering, reduction, sorting, and so forth. It also includes an extensible terminal operation, Stream::collect, which enables the output of a pipeline to be summarized in a variety of ways.
     The use of streams in the Java ecosystem is by now pervasive, and ideal for many tasks, but the fixed set of intermediate operations means that some complex tasks cannot easily be expressed as stream pipelines. Either a required intermediate operation does not exist, or it exists but does not directly support the task.
     As an example, suppose the task is to take a stream of strings and make it distinct, but with distinctness based on string length rather than content. That is, at most one string of length 1 should be emitted, and at most one string of length 2, and at most one string of length 3, and so forth. Ideally, the code would look something like this:
        var result = Stream.of("foo", "bar", "baz", "quux")
            .distinctBy(String::length)  // Hypothetical
            .toList();                   // result ==> [foo, quux]
     Unfortunately, distinctBy is not a built-in intermediate operation. The closest built-in operation, distinct, tracks the elements it has already seen by using object equality to compare them. That is, distinct is stateful but in this case uses the wrong state: We want it to track elements based on equality of string length, not string content. We could work around this limitation by declaring a class that defines object equality in terms of string length, wrapping each string in an instance of that class and applying distinct to those instances. This expression of the task is not intuitive, however, and makes for code that is difficult to maintain:
     */
        record DistinctByLength(String str) {
            @Override public boolean equals(Object obj) {
                return obj instanceof DistinctByLength(String other)
                    && str.length() == other.length();
            }
            @Override public int hashCode() {
                return str == null ? 0 : Integer.hashCode(str.length());
            }
        }
        public static List<String> testDistinctByLength() {
            return Stream.of("foo", "bar", "baz", "quux")
                .map(DistinctByLength::new)
                .distinct()
                .map(DistinctByLength::str)
                .toList(); // result ==> [foo, quux]
        }
     /*
     As another example, suppose the task is to group elements into fixed-size groups of three, but retain only the first two groups: [0, 1, 2, 3, 4, 5, 6, ...] should produce [[0, 1, 2], [3, 4, 5]]. Ideally, the code would look like this:
        var result = Stream.iterate(0, i -> i + 1)
            .windowFixed(3) // Hypothetical
            .limit(2)
            .toList();      // result ==> [[0, 1, 2], [3, 4, 5]]
     Unfortunately, no built-in intermediate operation supports this task. The best option is to place the fixed-window grouping logic in the terminal operation, by invoking collect with a custom Collector. However, we must precede the collect operation with a fixed-size limit operation, since the collector cannot signal to collect that it is finished while new elements are appearing — which happens forever with an infinite stream. Also, the task is inherently about ordered data, so it is not feasible to have the collector perform grouping in parallel, and it must signal this fact by throwing an exception if its combiner is invoked. The resulting code is difficult to understand:
     */
        public static ArrayList<ArrayList<Integer>> testGetFirstFixedSizedGroups() {
            return Stream.iterate(0, i -> i + 1)
                .limit(3 * 2)
                .collect(Collector.of(
                    () -> new ArrayList<ArrayList<Integer>>(),
                    (groups, element) -> {
                        if (groups.isEmpty() || groups.getLast().size() == 3) {
                            var current = new ArrayList<Integer>();
                            current.add(element);
                            groups.addLast(current);
                        } else {
                            groups.getLast().add(element);
                        }
                    },
                    (left, right) -> {
                        throw new UnsupportedOperationException("Cannot be parallelized");
                    }
                )); // result ==> [[0, 1, 2], [3, 4, 5]]
        }
     /*
    }

    Description {
     Stream::gather(Gatherer) is a new intermediate stream operation that processes the elements of a stream by applying a user-defined entity called a gatherer. With the gather operation we can build efficient, parallel-ready streams that implement almost any intermediate operation. Stream::gather(Gatherer) is to intermediate operations what Stream::collect(Collector) is to terminal operations.
     Gatherers can transform elements in a one-to-one, one-to-many, many-to-one, or many-to-many fashion. They can track previously seen elements in order to influence the transformation of later elements, they can short-circuit in order to transform infinite streams to finite ones, and they can enable parallel execution. For example, a gatherer can transform one input element to one output element until some condition becomes true, at which time it starts to transform one input element to two output elements.
     A gatherer is defined by four functions that work together:
      - The optional "initializer" function provides an object that maintains private state while processing stream elements. For example, a gatherer can store the current element so that, the next time it is applied, it can compare the new element with the now-previous element and, say, emit only the larger of the two. In effect, such a gatherer transforms two input elements into one output element.
      - The "integrator" function integrates a new element from the input stream, possibly inspecting the private state object and possibly emitting elements to the output stream. It can also terminate processing before reaching the end of the input stream; for example, a gatherer searching for the largest of a stream of integers can terminate if it detects Integer.MAX_VALUE.
      - The optional "combiner" function can be used to evaluate the gatherer in parallel when the input stream is marked as parallel. If a gatherer is not parallel-capable then it can still be part of a parallel stream pipeline, but it is evaluated sequentially. This is useful for cases where an operation is inherently ordered in nature and thus cannot be parallelized.
      - The optional "finisher" function is invoked when there are no more input elements to consume. This function can inspect the private state object and, possibly, emit additional output elements. For example, a gatherer searching for a specific element amongst its input elements can report failure, say by throwing an exception, when its finisher is invoked.
     When invoked, Stream::gather performs the equivalent of the following steps:
      - Create a "Downstream" object which, when given an element of the gatherer’s output type, passes it to the next stage in the pipeline.
      - Obtain the gatherer’s private state object by invoking the get() method of its "initializer".
      - Obtain the gatherer’s "integrator" by invoking its integrator() method.
      - While there are more input elements, invoke the integrator's integrate(...) method, passing it the state object, the next element, and the downstream object. Terminate if that method returns false.
      - Obtain the gatherer’s "finisher" and invoke it with the state and downstream objects.
     Every existing intermediate operation declared in the Stream interface can be implemented by invoking gather with a gatherer that implements that operation. For example, given a stream of T-typed elements, Stream::map turns each T element into a U element by applying a function and then passes the U element downstream; this is simply a stateless one-to-one gatherer. As another example, Stream::filter takes a predicate that determines whether an input element should be passed downstream; this is simply a stateless one-to-many gatherer. In fact every stream pipeline is, conceptually, equivalent to
        source.gather(...).gather(...).gather(...).collect(...)\

     Built-in gatherers.
      1. {fold} is a stateful many-to-one gatherer which constructs an aggregate incrementally and emits that aggregate when no more input elements exist. An operation which performs an ordered, reduction-like, transformation for scenarios where no combiner-function can be implemented, or for reductions which are intrinsically order-dependent. This operation always emits a single resulting element.
      https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherers.html#fold(java.util.function.Supplier,java.util.function.BiFunction)
     */
        Optional<String> numberString = // will contain: Optional[123456789].
             Stream.of(1,2,3,4,5,6,7,8,9)
                 .gather(Gatherers.fold(() -> "", (string, number) -> string + number))
                 .findFirst();
     /*
      2. {mapConcurrent} is a stateful one-to-one gatherer which invokes a supplied function for each input element concurrently, up to a supplied limit. An operation which executes operations concurrently with a fixed window of max concurrency, using VirtualThreads. This operation preserves the ordering of the stream. In progress tasks will be attempted to be cancelled, on a best-effort basis, in situations where the downstream no longer wants to receive any more elements.
      3. {scan} is a stateful one-to-one gatherer which applies a supplied function to the current state and the current element to produce the next element, which it passes downstream. Performs a prefix scan -- an incremental accumulation, using the provided functions.
      4. {windowFixed} is a stateful many-to-many gatherer which groups input elements into lists of a supplied size, emitting the windows downstream when they are full. Gathers elements into fixed-size windows. The last window may contain fewer elements than the supplied window size.
      */
        List<List<Integer>> windowFixed = // will contain: [[1, 2, 3], [4, 5, 6], [7, 8]].
             Stream.of(1,2,3,4,5,6,7,8).gather(Gatherers.windowFixed(3)).toList();
      /*
      5. {windowSliding} is a stateful many-to-many gatherer which groups input elements into lists of a supplied size. After the first window, each subsequent window is created from a copy of its predecessor by dropping the first element and appending the next element from the input stream. Gathers elements into sliding windows, sliding out the most previous element and sliding in the next element for each subsequent window. If the stream is empty then no window will be produced. If the size of the stream is smaller than the window size then only one window will be emitted, containing all elements.
      */
      List<List<Integer>> windowSliding = // will contain: [[1, 2], [2, 3], [3, 4], [4, 5], [5, 6], [6, 7], [7, 8]].
          Stream.of(1,2,3,4,5,6,7,8).gather(Gatherers.windowSliding(2)).toList();
      /*

     Parallel evaluation.
     Parallel evaluation of a gatherer is split into two distinct modes. When a combiner is not provided, the stream library can still extract parallelism by executing upstream and downstream operations in parallel, analogous to a short-circuitable parallel().forEachOrdered() operation. When a combiner is provided, parallel evaluation is analogous to a short-circuitable parallel().reduce() operation.
     When executed in parallel, multiple intermediate results may be instantiated, populated, and merged so as to maintain isolation of mutable data structures. Therefore, even when executed in parallel with non-thread-safe data structures (such as ArrayList), no additional synchronization is needed for a parallel reduction (from java.util.stream.Stream#gather).

     Composing gatherers.
     https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherer.html#andThen(java.util.stream.Gatherer)
     Gatherers support composition via the {andThen(Gatherer)} method, which joins two gatherers where the first produces elements that the second can consume. This enables the creation of sophisticated gatherers by composing simpler ones, just like "function composition". Semantically,
        source.gather(a).gather(b).gather(c).collect(...)
        Stream<Long> stream = Stream.of(1).gather(someGatherer).gather(someOtherGatherer);
      is equivalent to
        source.gather(a.andThen(b).andThen(c)).collect(...)
        Gatherer<Integer,?,Long> gatherer = someGatherer.andThen(someOtherGatherer);
      where
        Gatherer<Integer,?,String> someGatherer = …;
        Gatherer<String,?,Long> someOtherGatherer = …;
     This makes it possible to both de-couple operations from their use-sites, as well as create more sophisticated {Gatherers} from simple ones.
     But that’s not all—by introducing a {default Collector collect(Collector)} - method it is also possible to compose a {Gatherer} with a {Collector}, and the result is another {Collector}:
        Collector<Long,?,List<Long>> someGatherer.andThen(someOtherGatherer).collect(Collectors.toList());
     All of this together, it is now possible to build Stream-processing: "source-to-destination" using Stream.gather(…).gather(…).gather(…); "intermediaries-first" using gatherer.andThen(…).andThen(…); and, "destination-to-source" using gatherer.collect(otherGatherer.collect(…)).

     Examples {
    */
      // Examples 1. Sometimes the lack of an appropriate intermediate operation forces us to evaluate a stream into a list and run our analysis logic in a loop. Suppose, for example, that we have a stream of temporally ordered temperature readings.
        public class SuspiciousReading {
            record Reading(Instant obtainedAt, int kelvins) {
                Reading(String time, int kelvins) { this(Instant.parse(time), kelvins); }
                static Stream<Reading> loadRecentReadings() {
                    // In reality these could be read from a file, a database, a service, or otherwise.
                    return Stream.of(
                        new Reading("2023-09-21T10:15:30.00Z", 310),
                        new Reading("2023-09-21T10:15:31.00Z", 312),
                        new Reading("2023-09-21T10:15:32.00Z", 350),
                        new Reading("2023-09-21T10:15:33.00Z", 310)
                    );
                }
            }
            // Suppose, further, that we want to detect suspicious changes in this stream, defined as temperature changes of more than 30° Kelvin across two consecutive readings within a five-second window of time:
            static boolean isSuspicious(Reading previous, Reading next) {
                return next.obtainedAt().isBefore(previous.obtainedAt().plusSeconds(5))
                    && (next.kelvins() - previous.kelvins() > 30
                    || next.kelvins() - previous.kelvins() < -30);
            }
            // This requires a sequential scan of the input stream, so we must eschew declarative stream processing and implement our analysis imperatively:
            static List<List<Reading>> findSuspicious(Stream<Reading> source) {
                var suspicious = new ArrayList<List<Reading>>();
                Reading previous = null;
                boolean hasPrevious = false;
                for (Reading next : source.toList()) {
                    if (!hasPrevious) {
                        hasPrevious = true;
                        previous = next;
                    } else {
                        if (isSuspicious(previous, next)) suspicious.add(List.of(previous, next));
                        previous = next;
                    }
                }
                return suspicious;
            }
            // With a gatherer, however, we can express this more succinctly:
            static List<List<Reading>> findSuspiciousWithGatherer(Stream<Reading> source) {
                return source.gather(Gatherers.windowSliding(2))
                    .filter(
                        window -> (
                            window.size() == 2 && isSuspicious(window.get(0), window.get(1))
                        )
                    )
                    .toList();
            }
            public static void testFindSuspicious() {
                println(findSuspicious(Reading.loadRecentReadings()));
                println(findSuspiciousWithGatherer(Reading.loadRecentReadings()));
                // Prints:
                //[[Reading[obtainedAt=2023-09-21T10:15:31Z, kelvins=312],
                //Reading[obtainedAt=2023-09-21T10:15:32Z, kelvins=350]],
                //[Reading[obtainedAt=2023-09-21T10:15:32Z, kelvins=350],
                //Reading[obtainedAt=2023-09-21T10:15:33Z, kelvins=310]]]
            }
        }
      // Examples 2. Custom gatherer. The windowFixed gatherer declared in the Gatherers class could be written as a direct implementation of the Gatherer interface.
        record WindowFixed<TR>(int windowSize) implements Gatherer<TR, ArrayList<TR>, List<TR>> {
            public WindowFixed {
                // Validate input.
                if (windowSize < 1) throw new IllegalArgumentException("window size must be positive");
            }
            @Override
            public Supplier<ArrayList<TR>> initializer() {
                // Create an ArrayList to hold the current open window.
                return () -> new ArrayList<>(windowSize);
            }
            @Override
            public Integrator<ArrayList<TR>, TR, List<TR>> integrator() {
                // The integrator is invoked for each element consumed.
                return Gatherer.Integrator.ofGreedy((window, element, downstream) -> {
                    // Add the element to the current open window.
                    window.add(element);
                    // Until we reach our desired window size, return true to signal that more elements are desired.
                    if (window.size() < windowSize) return true;
                    // When the window is full, close it by creating a copy.
                    var result = new ArrayList<TR>(window);
                    // Clear the window so the next can be started.
                    window.clear();
                    // Send the closed window downstream.
                    return downstream.push(result);
                });
            }
            // The combiner is omitted since this operation is intrinsically sequential, and thus cannot be parallelized.
            @Override
            public BiConsumer<ArrayList<TR>, Downstream<? super List<TR>>> finisher() {
                // The finisher runs when there are no more elements to pass from the upstream.
                return (window, downstream) -> {
                    // If the downstream still accepts more elements and the current open window is non-empty, then send a copy of it downstream.
                    if (!downstream.isRejecting() && !window.isEmpty()) {
                        downstream.push(new ArrayList<TR>(window));
                        window.clear();
                    }
                };
            }
        }
        public static void testWindowFixed() {
            println(Stream.of(1,2,3,4,5,6,7,8,9).gather(new WindowFixed(3)).toList());
            // Prints:
            //[[1, 2, 3], [4, 5, 6], [7, 8, 9]]
        }
      // Example 3. An ad-hoc gatherer. The windowFixed gatherer could, alternatively, be written in an ad-hoc manner via the Gatherer.ofSequential(...) factory method.
        /**
         * Gathers elements into fixed-size groups. The last group may contain fewer
         * elements.
         * @param windowSize the maximum size of the groups
         * @return a new gatherer which groups elements into fixed-size groups
         * @param <TR> the type of elements the returned gatherer consumes and produces
         */
        static <TR> Gatherer<TR, ?, List<TR>> getFixedWindowAdhoc(int windowSize) {
            // Validate input.
            if (windowSize < 1) throw new IllegalArgumentException("window size must be non-zero");
            // This gatherer is inherently order-dependent, so it should not be parallelized.
            return Gatherer.ofSequential(
                // The initializer creates an ArrayList which holds the current open window.
                () -> new ArrayList<TR>(windowSize),
                // The integrator is invoked for each element consumed.
                Gatherer.Integrator.ofGreedy((window, element, downstream) -> {
                    // Add the element to the current open window.
                    window.add(element);
                    // Until we reach our desired window size, return true to signal that more elements are desired.
                    if (window.size() < windowSize) return true;
                    // When window is full, close it by creating a copy.
                    var result = new ArrayList<TR>(window);
                    // Clear the window so the next can be started.
                    window.clear();
                    // Send the closed window downstream.
                    return downstream.push(result);
                }),
                // The combiner is omitted since this operation is intrinsically sequential, and thus cannot be parallelized. The finisher runs when there are no more elements to pass from the upstream.
                (window, downstream) -> {
                    // If the downstream still accepts more elements and the current open window is non-empty then send a copy of it downstream.
                    if(!downstream.isRejecting() && !window.isEmpty()) {
                        downstream.push(new ArrayList<TR>(window));
                        window.clear();
                    }
                }
            );
        }
        public static void testWindowFixedAdhoc() {
            println(Stream.of(1,2,3,4,5,6,7,8,9).gather(getFixedWindowAdhoc(3)).toList());
            // Prints:
            //[[1, 2, 3], [4, 5, 6], [7, 8, 9]]
        }
      // Example 4. A parallelizable gatherer. When used in a parallel stream, a gatherer is only evaluated in parallel if it provides a combiner function. This parallelizable gatherer, for example, emits at most one element based upon a supplied selector function.
        static <TR> Gatherer<TR, ?, TR> selectOne(BinaryOperator<TR> selector) {
            // Validate input.
            Objects.requireNonNull(selector, "selector must not be null");
            // Private state to track information across elements.
            class State {
                TR value;            // The current best value.
                boolean hasValue;    // {true} when value holds a valid value.
            }
            // Use the {of} factory method to construct a gatherer given a set of functions for {initializer}, {integrator}, {combiner}, and {finisher}.
            return Gatherer.of(
                // The initializer creates a new State instance.
                State::new,
                // The {integrator}; in this case we use {ofGreedy} to signal that this {integrator} will never short-circuit.
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    if (!state.hasValue) {
                        // The first element, just save it.
                        state.value = element;
                        state.hasValue = true;
                    } else {
                        // Select which value of the two to save, and save it.
                        state.value = selector.apply(state.value, element);
                    }
                    return true;
                }),
                // The combiner, used during parallel evaluation.
                (leftState, rightState) -> {
                    if (!leftState.hasValue) {
                        // If no value on the left, return the right.
                        return rightState;
                    } else if (!rightState.hasValue) {
                        // If no value on the right, return the left.
                        return leftState;
                    } else {
                        // If both sides have values, select one of them to keep and store it in the leftState, as that will be returned.
                        leftState.value = selector.apply(leftState.value, rightState.value);
                        return leftState;
                    }
                },
                // The finisher.
                (state, downstream) -> {
                    // Emit the selected value, if there is one, downstream.
                    if (state.hasValue) downstream.push(state.value);
                }
            );
        }
        public static void testselectOne() {
            println(
                Stream.generate(() -> ThreadLocalRandom.current().nextInt())
                    .limit(1000)          // Take the first 1000 elements.
                    .gather(selectOne(Math::max)) // Select the largest value seen.
                    .parallel()                   // Execute in parallel.
                    .findFirst()                  // Extract the largest value.
            );
            // Prints:
            //Optional[99822]
        }
      // Example 5. Implement map(mapper).
        public final static <T,R> Gatherer<T, ?, R> mapGatherer(Function<? super T, ? extends R> mapper) {
            return Gatherer.of(
                () -> (Void)null,
                (nothing, element, downstream) -> downstream.push(mapper.apply(element)),
                (l,r) -> l,
                (nothing, downstream) -> {}
            );
        }
        public static void testMapGatherer () {
            println(Stream.of(1,2,3,4).gather(mapGatherer(i -> i + 1)).toList());
            // Prints:
            //[2, 3, 4, 5]
        }
        // Example 6. Implement flatMap(mapper).
        public final static <T,R> Gatherer<T, ?, R> flatMapGatherer(Function<? super T, ? extends Stream<R>> mapper) {
            return Gatherer.of(
                () -> (Void)null,
                (nothing, element, downstream) -> {
                    try(Stream<? extends R> s = mapper.apply(element)) {
                        return s == null || s.sequential().allMatch(downstream::push);
                    }
                },
                (l,r) -> l,
                (nothing, downstream) -> {}
            );
        }
        public static void testFlatMapGatherer () {
            println(Stream.of(1,2,3,4).gather(flatMapGatherer(i -> Stream.of(i + 1))).toList());
            // Prints:
            //[2, 3, 4, 5]
        }
        // Example 7. Implement takeWhile(predicate).
        public final static <T> Gatherer<T, ?, T> takeWhileGatherer(Predicate<? super T> predicate) {
            return Gatherer.of(
                () -> (Void)null,
                (nothing, element, downstream) -> predicate.test(element) && downstream.push(element),
                (l, r) -> l,
                (nothing, downstream) -> {}
            );
        }
        public static void testTakeWhileGatherer () {
            println(Stream.of(1,2,3,4).gather(takeWhileGatherer(i -> i < 3)).toList());
            // Prints:
            //[1, 2]
        }
        // Example 8. Implement limit(N).
        public static <M> Gatherer<M, ?, M> limitGatherer(long limit) {
            if (limit < 0) throw new IllegalArgumentException("'limit' has to be non-negative");
            class At { long n = 0; }
            return Gatherer.of(
                At::new,
                (at, element, downstream) -> at.n < limit && downstream.push(element) && ++at.n < limit,
                Gatherer.defaultCombiner(),
                (at, downstream) -> {}
            );
        }
        public static void testLimitGatherer () {
            println(Stream.of(1,2,3,4).gather(limitGatherer(2)).toList());
            // Prints:
            //[1, 2]
        }
        // Example 9. Implement prefix scanLeft.
        public static <T,R> Gatherer<T,?,R> scanLeftGatherer(R initial, BiFunction<R,T,R> scanner) {
            class State { R current = initial; }
            return Gatherer.of(
                State::new,
                (state, element, downstream) -> downstream.push(state.current = scanner.apply(state.current, element)),
                Gatherer.defaultCombiner(),
                (state, downstream) -> {}
            );
        }
        public static void testScanLeftGatherer () {
            println(
                Stream.of(1,2,3,4).gather(scanLeftGatherer("'", (acc, elem) -> acc + elem + "'")).toList()
            );
            // Prints:
            //['1', '1'2', '1'2'3', '1'2'3'4']
        }
        // Example 10. Implement fold(initial, folder).
        public static <T> Gatherer<T,?,T> foldGatherer(T initial, BinaryOperator<T> folder) {
            class State { T current = initial; }
            return Gatherer.of(
                State::new,
                (state, element, downstream) -> {
                    state.current = folder.apply(state.current, element);
                    return true;
                },
                (l, r) -> {
                    l.current = folder.apply(l.current, r.current);
                    return l;
                },
                (state, downstream) -> downstream.push(state.current)
            );
        }
        public static void testFoldGatherer () {
            println(Stream.of(1,2,3,4).gather(foldGatherer(0, (acc, elem) -> acc + elem)).toList());
            // Prints:
            //[10]
        }
        // Example 11. Implement foldLeft(initial, folder).
        public static <T,R> Gatherer<T,?,R> foldLeftGatherer(R initial, BiFunction<R,T,R> folder) {
            class State { R current = initial; }
            return Gatherer.of(
                State::new,
                (state, element, downstream) -> {
                    state.current = folder.apply(state.current, element);
                    return true;
                },
                Gatherer.defaultCombiner(),
                (state, downstream) -> downstream.push(state.current)
            );
        }
        public static void testFoldLeftGatherer () {
            println(Stream.of(1,2,3,4).gather(foldLeftGatherer(0L, (acc, elem) -> acc + elem)).toList());
            // Prints:
            //[10]
        }
        // Example 12. Implement deduplicateAdjacent().
        public static <T> Gatherer<T,?,T> deduplicateAdjacentGatherer() {
            class State { T prev; boolean hasPrev; }
            return Gatherer.of(
                State::new,
                (state, element, downstream) -> {
                    if (!state.hasPrev) {
                        state.hasPrev = true;
                        state.prev = element;
                        return downstream.push(element);
                    } else if (!Objects.equals(state.prev, element)) {
                        state.prev = element;
                        return downstream.push(element);
                    } else {
                        return true; // skip duplicate
                    }
                },
                Gatherer.defaultCombiner(),
                (state, downstream) -> {}
            );
        }
        public static void testDeduplicateAdjacentGatherer () {
            println(Stream.of(1,2,2,3,2,4).gather(deduplicateAdjacentGatherer()).toList());
            // Prints:
            //[1, 2, 3, 2, 4]
        }
    /*
     }
    }

    Alternatives {
     https://cr.openjdk.org/~vklang/Gatherers.html Gathering the streams. version 0.2.
     https://bugs.openjdk.org/browse/JDK-8132369 Define an SPI to plugin new intermediate and terminal stream operations.

     Over the years, many new operations for streams have been proposed. Each of them may be useful in some situation, but many of them are too narrow to be included in the core Stream API. We’d like for people to be able to plug these operations in via an extension point, as with Stream::collect.
     Here are some examples of proposed intermediate operations that are not easily expressible as intermediate operations on Stream today.
      1. Folds. Folding is a generalization of reduction. With reduction, the result type is the same as the element type, the combiner is associative, and the initial value is an identity for the combiner. For a fold, these conditions are not required, though we give up parallelizability.
        Example: foldLeft("", (str,elem) -> str + " " + elem.toString()) over the values [1,2,3] yields ["1 2 3"].
       See https://bugs.openjdk.org/browse/JDK-8133680 & https://bugs.openjdk.org/browse/JDK-8292845.
      2. Unfolds. This takes an aggregate and decomposes it into elements. An unfold can reverse the operation of the above fold example using Scanner or String::split.
        Example: unfold(e -> new Scanner(e), Scanner::hasNextInt, Scanner::nextInt) ["1 2 3"] yields [1,2,3].
       See: https://bugs.openjdk.org/browse/JDK-8151408.
      3. Barriers. Usually all operations of a stream pipeline can run simultaneously as data is available. In some cases, we may wish to have a barrier that requires that the entirety of one operation is completed before providing any elements to the next operation.
        Example: someStream.a(...).barrier(someEffect)...
       would require that all effects from {a} are completed before executing someEffect or producing any downstream values.
       See: https://bugs.openjdk.org/browse/JDK-8294246.
      4. Windowing. Given a stream, we may wish to construct a stream which groups the original elements, either in overlapping or disjoint groups.
        Example: fixedWindow(2) over the values [1,2,3,4,5] yields [[1,2],[3,4],[5]]. Example: slidingWindow(2) over the values [1,2,3] yields [[1,2],[2,3]].
       See: https://stackoverflow.com/questions/34158634/how-to-transform-a-java-stream-into-a-sliding-window.
      5. Prefix scans. Prefix scans are a stream of incremental reductions. Perhaps surprisingly, some prefix scans are parallelizable, and we are exploring adding support for that beyond Arrays.parallelPrefix().
        Example: scan((sum, next) -> sum + next) over values [1,2,3,4] yields [1,3,6,10]
       See: https://stackoverflow.com/questions/55265797/cumulative-sum-using-java-8-stream-api.
      6. Duplicated elements. The {distinct} operation will remove duplicates; sometimes we want only the elements that are duplicated.
        Example: duplicates(Object::equals) over values [1,1,2,3,4] yields [1]
       See: https://stackoverflow.com/questions/27677256/java-8-streams-to-find-the-duplicate-elements.
      7. Running duplicate elimination. Here, we want to eliminate adjacent repeated elements.
        Example: collapseRuns(Object::equals) over values [1,1,2,3,3] yields [1,2,3]
     There is some third-party libraries manly targeted Java 8 that provide extra Stream functionality, for example:
      - Cyclops X https://github.com/aol/cyclops
      - JOOL/jOOλ https://github.com/jOOQ/jOOL https://blog.jooq.org/2016-will-be-the-year-remembered-as-when-java-finally-had-window-functions/
      - StreamEx https://github.com/amaembo/streamex

    }
    */

        /**
         * From java.util.stream.Gatherer.
         */
        public static class FromGatherer {
        /*
        An intermediate operation that transforms a stream of input elements into a stream of output elements, optionally applying a final action when the end of the upstream is reached. The transformation may be stateless or stateful, and may buffer input before producing any output.
        Gatherer operations can be performed either sequentially, or be parallelized -- if a combiner function is supplied.
        There are many examples of gathering operations, including but not limited to: grouping elements into batches (windowing functions); de-duplicating consecutively similar elements; incremental accumulation functions (prefix scan); incremental reordering functions, etc. The class Gatherers provides implementations of common gathering operations.
        A Gatherer is specified by four functions that work together to process input elements, optionally using intermediate state, and optionally perform a final action at the end of input. They are:
         - creating a new, potentially mutable, state (initializer())
         - integrating a new input element (integrator())
         - combining two states into one (combiner())
         - performing an optional final action (finisher())
        Each invocation of initializer(), integrator(), combiner(), and finisher() must return a semantically identical result.
        Implementations of Gatherer must not capture, retain, or expose to other threads, the references to the state instance, or the downstream {Gatherer.Downstream} for longer than the invocation duration of the method which they are passed to.
        Performing a gathering operation with a Gatherer should produce a result equivalent to:
            Gatherer.Downstream<? super R> downstream = ...;
            A state = gatherer.initializer().get();
            for (T t : data) {
                gatherer.integrator().integrate(state, t, downstream);
            }
            gatherer.finisher().accept(state, downstream);
        However, the library is free to partition the input, perform the integrations on the partitions, and then use the combiner function to combine the partial results to achieve a gathering operation. (Depending on the specific gathering operation, this may perform better or worse, depending on the relative cost of the integrator and combiner functions).
        In addition to the predefined implementations in Gatherers, the static factory methods of(...) and ofSequential(...) can be used to construct gatherers. For example, you could create a gatherer that implements the equivalent of Stream.map(java.util.function.Function) with:
            public static <T, R> Gatherer<T, ?, R> map(Function<? super T, ? extends R> mapper) {
                return Gatherer.of(
                    (unused, element, downstream) -> // integrator
                        downstream.push(mapper.apply(element))
                );
            }
        Gatherers are designed to be composed; two or more Gatherers can be composed into a single Gatherer using the andThen(Gatherer) method.
            // using the implementation of `map` as seen above
            Gatherer<Integer, ?, Integer> increment = map(i -> i + 1);
            Gatherer<Object, ?, String> toString = map(i -> i.toString());
            Gatherer<Integer, ?, String> incrementThenToString = increment.andThen(toString);
        As an example, a Gatherer implementing a sequential Prefix Scan could be done the following way:
            public static <T, R> Gatherer<T, ?, R> scan(
                Supplier<R> initial,
                BiFunction<? super R, ? super T, ? extends R> scanner
            ) {
                class State { R current = initial.get(); }
                return Gatherer.<T, State, R>ofSequential(
                     State::new,
                     Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                         state.current = scanner.apply(state.current, element);
                         return downstream.push(state.current);
                     })
                );
            }
        Example of usage:
            // will contain: ["1", "12", "123", "1234", "12345", "123456", "1234567", "12345678", "123456789"]
            List<String> numberStrings =
                Stream.of(1,2,3,4,5,6,7,8,9).gather(scan(() -> "", (string, number) -> string + number)).toList();

        Implementation Requirements.
        Libraries that implement transformations based on Gatherer, such as Stream.gather(Gatherer), must adhere to the following constraints:
         - Gatherers whose initializer is defaultInitializer() are considered to be stateless, and invoking their initializer is optional.
         - Gatherers whose integrator is an instance of Gatherer.Integrator.Greedy can be assumed not to short-circuit, and the return value of invoking Gatherer.Integrator.integrate(Object, Object, Gatherer.Downstream) does not need to be inspected.
         - The first argument passed to the integration function, both arguments passed to the combiner function, and the argument passed to the finisher function must be the result of a previous invocation of the initializer or combiner functions.
         - The implementation should not do anything with the result of any of the initializer or combiner functions other than to pass them again to the integrator, combiner, or finisher functions.
         - Once a state object is passed to the combiner or finisher function, it is never passed to the integrator function again.
         - When the integrator function returns false, it shall be interpreted just as if there were no more elements to pass it.
         - For parallel evaluation, the gathering implementation must manage that the input is properly partitioned, that partitions are processed in isolation, and combining happens only after integration is complete for both partitions.
         - Gatherers whose combiner is defaultCombiner() may only be evaluated sequentially. All other combiners allow the operation to be parallelized by initializing each partition in separation, invoking the integrator until it returns false, and then joining each partitions state using the combiner, and then invoking the finisher on the joined state. Outputs and state later in the input sequence will be discarded if processing an earlier partition short-circuits.
         - Gatherers whose finisher is defaultFinisher() are considered to not have an end-of-stream hook and invoking their finisher is optional.
        */
        }

        public static void testStreamGatherers() { }
        public static <T, R> Gatherer<T, ?, R> scan(
                Supplier<R> initial,
                BiFunction<? super R, ? super T, ? extends R> scanner
        ) {
            class State { R current = initial.get(); }
            return Gatherer.<T, State, R>ofSequential(
                    State::new,
                    Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                        state.current = scanner.apply(state.current, element);
                        return downstream.push(state.current);
                    })
            );
        }
        List<String> numberStrings =
                Stream.of(1,2,3,4,5,6,7,8,9).gather(scan(() -> "", (string, number) -> string + number)).toList();
    }

}
