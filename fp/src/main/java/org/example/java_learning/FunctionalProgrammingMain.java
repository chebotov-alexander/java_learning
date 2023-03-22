package org.example.java_learning;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FunctionalProgrammingMain {
/*
Summary:
  - First-class functions are functions that can be passed as arguments, returned as results, and stored in data structures.
  - A higher-order function takes one or more functions as input or returns another function. Typical higher-order functions in Java include comparing, andThen, and compose.
  - Currying is a technique that lets you modularize functions and reuse code.
  - A persistent data structure preserves the previous version of itself when it’s modified. As a result, it can prevent unnecessary defensive copying.
  - A lazy list is a more-expressive version of a Java stream. A lazy list lets you produce elements of the list on demand by using a supplier that can create more of the data structure.
  - Referential transparency allows computations to be cached.
  - Combinators are functional ideas that combine two or more functions or other data structures.


Java streams allows you to exploit parallelism without worrying about locking, provided that you embrace stateless behaviors, that is, functions in your stream-processing pipeline don’t interact, with one function reading from or writing to a variable that’s written by another.
Coupling - how interdependent parts of the system are.
Cohesion - how related the various parts of the system are.

Shared mutable data.
Suppose that several classes keep a reference to a list. You need to establish answers to the following questions:
  - Who owns this list?
  - What happens if one class modifies the list?
  - Do other classes expect this change?
  - How do those classes learn about this change?
  - Do the classes need to be notified of this change to satisfy all assumptions in this list, or should they make defensive copies for themselves?
In other words, shared mutable data structures make it harder to track changes in different parts of your program.
Consider a system that doesn’t mutate any data structures. This system would be a dream to maintain because you wouldn’t have any bad surprises about some object somewhere that unexpectedly modifies a data structure. A method that modifies neither the state of its enclosing class nor the state of any other objects and returns its entire results by using return is called pure or side-effect-free.
What constitutes a side effect? In a nutshell, a side effect is an action that’s not totally enclosed within the function itself. Here are some examples:
  - Modifying a data structure in place, including assigning to any field, apart from initialization inside a constructor (such as setter methods).
  - Throwing an exception.
  - Performing I/O operations such as writing to a file.
Another way to look at the idea of no side effects is to consider immutable objects. An immutable object is an object that can’t change its state after it’s instantiated, so it can’t be affected by the actions of a function. When immutable objects are instantiated, they can never go into an unexpected state. You can share them without having to copy them, and they’re thread-safe because they can’t be modified.

Functional programming is based declarative programming.
There are two ways of thinking about implementing a system by writing a program:
  - Imperative programming - it's all about "HOW" - first do this, then update that, and so on. This style of programming is an excellent match for classic object-oriented programming, because it has instructions that mimic the low-level vocabulary of a computer (such as assignment, conditional branching, and loops).
        Transaction mostExpensive = transactions.get(0);
        if(mostExpensive == null) throw new IllegalArgumentException("Empty list of transactions");
        for(Transaction t: transactions.subList(1, transactions.size())) {
            if(t.getValue() > mostExpensive.getValue()) {
                mostExpensive = t;
            }
        }
  - Declarative programming - it's all about "WHAT" - you provide rules saying what you want, and you expect the system to decide how to achieve that goal. This type of programming is great because it reads closer to the problem statement.
        Optional<Transaction> mostExpensive = transactions.stream().max(comparing(Transaction::getValue));
Functional programming exemplifies this idea of declarative programming (say what you want using expressions that don’t interact, and for which the system can choose the implementation) and side-effect-free computation.
In the context of functional programming, a function corresponds to a mathematical function: it takes zero or more arguments, returns one or more results, and has no side effects. You can see a function as being a black box that takes some inputs and produces some outputs.
The distinction between this sort of function and the methods in programming languages such as Java is central - the idea that mathematical functions such as log or sin might have such side effects in unthinkable. In particular, mathematical functions always return the same results when they’re called repeatedly with the same arguments. This characterization rules out methods such as Random.nextInt (see referential transparency).
Say functional, mean like mathematics, with no side effects. Is every function built only with functions and mathematical ideas such as if-then-else? Or might a function do nonfunctional things internally as long as it doesn’t expose any of these side effects to the rest of the system? In other words, if programmers perform a side effect that can’t be observed by callers, does that side effect exist? The callers don’t need to know or care, because it can’t affect them. So, to emphasize the difference, there's pure functional programming and functional-style programming.

Functional-style Java.
In practice, you can’t completely program in pure functional style in Java. Calling Scanner.nextLine has the side effect of consuming a line from a file, so calling it twice typically produces different results. Nonetheless, it’s possible to write core components of your system as though
they were purely functional.
There’s a further subtlety about no one seeing your side effects and, hence, in the meaning of functional. Suppose that a function or method has no side effects except for incrementing a field after entry and decrementing it before exit. From the point of view of a program that consists of a single thread, this method has no visible side effects and can be regarded as functional style. On the other hand, if another thread could inspect the field—or could call the method concurrently—the method wouldn’t be functional. You could hide this issue by wrapping the body of this method with a lock, which would enable you to argue that the method is functional. But in doing so, you’d lose the ability to execute two calls to the method in parallel by using two cores on your multicore processor. Your side effect may not be visible to a program, but it’s visible to the programmer in terms of slower execution.
There’s an additional requirement to being functional, that a function or method shouldn’t throw any exceptions. A justification is that throwing an exception would mean that a result is being signaled other than via the function returning a value. There’s scope for debate here, with some authors arguing that uncaught exceptions representing fatal errors are okay and that it’s the act of catching an exception that represents nonfunctional control flow. Such use of exceptions still breaks the simple “pass arguments, return result” metaphor pictured in the black-box model. However, in mathematics, a function is required to give exactly one result for each possible argument value. But many common mathematical operations are what should properly be called partial functions. That is, for some or most input values, they give exactly one result, but for other input values, they’re undefined and don’t give a result at all. An example is division when the second operand is zero or sqrt when its argument is negative. In Java this behaviour often modeled by throwing an exception. Another way is to use types like Optional<T>. You may choose to use exceptions locally but not expose them via large-scale interfaces, thereby gaining the advantages of functional style without the risk of code bloat.
So, this guideline is that to be regarded as functional style, a function or method can mutate only local variables. In addition, the objects that it references should be immutable — that is, all fields are final, and all fields of reference type refer transitively to other immutable objects. Later, you may permit updates to fields of objects that are freshly created in the method, so they aren’t visible from elsewhere and aren’t saved to affect the result of a subsequent call.

Referential transparency
The restrictions on no visible side effects (no mutating structure visible to callers, no I/O, no exceptions) encode the concept of referential transparency. A function is referentially transparent if it always returns the same result value when it’s called with the same argument value, for example, the method String.replace. Referential transparency is a great property for program understanding. It also encompasses save-instead-of-recompute optimization for expensive or long-lived operations, a process that goes by the name memoization or caching.

Recursion vs. iteration
Recursion is a technique promoted in functional programming to let you think in terms of what-to-do style. Pure functional programming languages typically don’t include iterative constructs such as while and for loops. Such constructs are often hidden invitations to use mutation (forEach in Java). The condition in a while loop needs to be updated, for example; otherwise, the loop would execute zero times or an infinite number of times. In many cases, however, loops are fine if no one can see mutating, it’s acceptable to mutate local variables. Pure functional programming languages such as Haskell omit such side-effecting operations - theoretically every program can be rewritten to prevent iteration by using recursion, which doesn’t require mutability. Using recursion lets you get rid of iteration variables that are updated step by step. But in Java like languages recursion with a large input is too expensive and leads to error like [java.lang.StackOverflowError].
Java compiler doesn't support tail-call optimization yet (Scala, Groovy, and Kotlin do), but it is very resource efficient recursion approach:
    static long factorialTailRecursive(long n) {
        return factorialHelper(1, n);
    }
    static long factorialHelper(long acc, long n) {
        return n == 1 ? acc : factorialHelper(acc * n, n-1);
    }
 The intermediate results (the partial results of the factorial) are passed directly as arguments to the function. There’s no need to keep track of the intermediate result of each recursive call on a separate stack frame; it’s accessible directly as the first argument of factorialHelper
*/


/*
Functional programming techniques.
More generally functions in functional language are first-class functions meaning that functions may be used like other values: passed as arguments, returned as results, and stored in data structures:
    - [Function<String, Integer> strToInt = Integer::parseInt] - storing function in a variable.
    - [Apple::isGreen] predicate in [filterApples] - function as a parameter.
    - [Comparator<Apple> c = comparing(Apple::getWeight)] - the static method Comparator.comparing takes a function as a parameter and returns another function (a Comparator).
    - [Function<String, String> transformationPipeline = addHeader.andThen(Letter::checkSpelling).andThen(Letter::addFooter)] - pipeline of operations.
    - [Map<String, Function<Double, Double>>] - could map the String "sin" to Function<Double, Double> to hold the method reference Math::sin.
    - [Function<Function<Double,Double>, Function<Double,Double>>] - could be used to take a function as an argument (such as (Double x) -> x * x) and returns a function as a result (in this example, (Double x) -> 2 * x). It can be used like this [Function<Double,Double> differentiate(Function<Double,Double> func)]
Higher-order functions (such as Comparator.comparing) are functions that can do at least one of the following:
  - take one or more functions as a parameter;
  - return a function as a result.
Functions passed to stream operations generally are side effect-free. This principle also applies in general when you use higher-order functions. When you’re writing a higher-order function or method, you don’t know in advance what arguments will be passed to it and, if the arguments have side effects, what these side effects might do. It becomes far too complicated to reason about what your code does if it uses functions passed as arguments that make unpredictable changes in the state of your program; such functions might even interfere with your code in some hard-to-debug way. It’s a good design principle to document what side effects you’re willing to accept (none is best of all) from functions passed as parameters.
*/
    /**
     * Currying. A technique that can help you modularize functions and reuse code.
     */
    public static class Currying {
    /*
    Currying a is a technique in which a function f of two arguments (such as x and y) is instead viewed as a function g of one argument that returns a function also of one argument. The value returned by the latter function is the same as the value of the original function—that is, f(x,y) = (g(x))(y).
    This definition generalizes, of course. You can curry a six-argument function to first take arguments numbered 2, 4, and 6, which returns a function taking argument 5, which returns a function taking the remaining arguments, 1 and 3.
    Converting from one set of units to another set is a problem that comes up repeatedly and it's perfectly fits the currying technique application. Unit conversion always involves a conversion factor and, from time to time, a baseline adjustment factor. The formula to convert Celsius to Fahrenheit, for example, is CtoF(x) = x*9/5 + 32. The basic pattern of all unit conversion is as follows: multiply by the conversion factor and then adjust the baseline if relevant.
    */
        static double converter(double x, double y, double z) {
            return x * y + z;
        }
        // Here, x is the quantity you want to convert, f is the conversion factor, and b is the baseline. But this method is a bit too general. Typically, you require a lot of conversions between the same pair of units, such as kilometers to miles. You could call the converter method with three arguments on each occasion, but supplying the factor and baseline each time would be tedious, and you might accidentally mistype them. You could write a new method for each application, but doing so would miss the reuse of the underlying logic. You can define a factory that manufactures one-argument conversion functions to exemplify the idea of currying:
        static DoubleUnaryOperator curriedConverter(double y, double z) {
            return (double x) -> x * y + z;
        }
        static DoubleUnaryOperator expandedCurriedConverter(double w, double y, double z) {
            return (double x) -> (x + w) * y + z;
        }
        // Now all you have to do is pass curriedConverter the conversion factor and baseline (f and b), and it obligingly returns a function (of x) to do what you asked for. Then you can use the factory to produce any converter you require, as follows:
        public static void currying() {
            DoubleUnaryOperator convertCtoF = curriedConverter(9.0 / 5, 32);
            DoubleUnaryOperator convertUSDtoGBP = curriedConverter(0.6, 0);
            DoubleUnaryOperator convertKmtoMi = curriedConverter(0.6214, 0);

            System.out.printf("24 °C = %.2f °F%n", convertCtoF.applyAsDouble(24));
            System.out.printf("US$100 = £%.2f%n", convertUSDtoGBP.applyAsDouble(100));
            System.out.printf("20 km = %.2f miles%n", convertKmtoMi.applyAsDouble(20));

            DoubleUnaryOperator convertFtoC = expandedCurriedConverter(-32, 5.0 / 9, 0);
            System.out.printf("98.6 °F = %.2f °C", convertFtoC.applyAsDouble(98.6));
        }
        // So, instead of passing all the arguments x, f, and b all at once to the converter method, you ask only for the arguments f and b and return another function—which, when given an argument x, returns x * f + b. This two-stage process enables you to reuse the conversion logic and create different functions with different conversion factors.

        // Another example.
        // From this:
        static int multiply(int x, int y) {
            return x * y;
        }
        int r = multiply(2, 10);
        // To this:
        static Function<Integer, Integer> multiplyCurry(int x) {
            return (Integer y) -> x * y;
        }
        void testmultiplyCurry() {
            Stream.of(1, 3, 5, 7).map(multiplyCurry(2)).forEach(System.out::println);
        }
    }

    /**
     * Persistent data structures.
     */
    public static class PersistentDataStructures {
    // Data structures used in functional-style programs have various names, such as functional data structures and immutable data structures, but perhaps the most common is persistent data structures. Unfortunately, this terminology clashes with the notion of persistent in databases, meaning "outliving one run of the program".
    // Now suppose that you have separate TrainJourney objects representing a journey from X to Y and from Y to Z. You may want to create one journey that links the two TrainJourney objects (that is, X to Y to Z)
        // Here is a simple traditional imperative method to link these train journeys.
        static TrainJourney imperativeLink(TrainJourney a, TrainJourney b) {
            if (a == null) { return b; }
            TrainJourney t = a;
            // Finding the last leg in the train journey starting at TrainJourney a.
            while (t.onward != null) { t = t.onward; }
            // Replacing the null marking the end of a’s list/legs with b's legs.
            t.onward = b;
            return a;
        }
        public static void testmperativeLink() {
            TrainJourney XtoY = new TrainJourney(1, null);
            TrainJourney YtoZ = new TrainJourney(2, null);
            TrainJourney XtoZ = imperativeLink(XtoY, YtoZ);
            System.out.printf("X to Y = %s%n", XtoY);
            System.out.printf("Y to Z = %s%n", YtoZ);
            System.out.printf("X to Z = %s%n", XtoZ);
            // prints:
            //   X to Y = TrainJourney[1] -> TrainJourney[2] -> null
            //   Y to Z = TrainJourney[2] -> null
            //   X to Z = TrainJourney[1] -> TrainJourney[2] -> null
            // Here’s the problem: suppose that the first journey contains the route from X to Y and that the second journey contains the route from Y to Z. If you call imperativeLink(XtoY, YtoZ), this code destructively updates first journey to also contain second journey, so in addition to the single user who requests a trip from X to Z seeing the combined journey as intended, the journey from X to Y has been updated destructively. Indeed, the first journey is no longer a route from X to Y, but one from X to Z, which breaks code that depends on first journey’s not being modified! Suppose that first journey represented the early-morning London-to-Brussels train, which all subsequent users trying to get to Brussels will be surprised to see requires an onward leg, perhaps to Cologne.
            // Where is more, if you call [imperativeLink(XtoY, YtoZ)] twice StackOverflowError exception occurs, because of the endless recursive calls on the tj2 part since first call already altered XtoY.
        }
        // Again, if you need a data structure to represent the result of a computation, you should make a new one, not mutate an existing data structure.
        // The functional-style approach is if you need a data structure to represent the result of a computation, you should make a new one, not mutate an existing data structure. Doing so is often a best practice in standard object-oriented programming, too. A common objection to the functional approach is that it causes excess copying, and the programmer says, "I’ll remember" or "I’ll document" the side effects. But such optimism is a trap for maintenance programmers who have to deal with your code later.
        static TrainJourney functionalAppend(TrainJourney a, TrainJourney b) {
            return a == null ? b : new TrainJourney(a.price, functionalAppend(a.onward, b));
            // Note, however, that the code creates a new TrainJourney(s). If a is a sequence of n elements and b is a sequence of m elements, the code returns a sequence of n+m elements, in which the first n elements are new nodes and the final m elements share with TrainJourney b.
        }

        // Another example with Trees.
        // Consider another data structure: a binary search tree that might be used to implement a similar interface to a HashMap. The idea is that a Tree contains a String representing a key and an int representing its value, perhaps names and ages:
        public static Tree update(String k, int newval, Tree t) {
            if (t == null) { t = new Tree(k, newval, null, null); }
            else if (k.equals(t.key)) { t.val = newval; }
            else if (k.compareTo(t.key) < 0) { t.left = update(k, newval, t.left); }
            else { t.right = update(k, newval, t.right); }
            return t;
        }
        // Functional version of the update method creates a new node for the new key-value pair and also creates new nodes on the path from the root of the tree to the new node.
        public static Tree functionalUpdate(String k, int newval, Tree t) {
            return (t == null) ?
                new Tree(k, newval, null, null) :
                k.equals(t.key) ?
                    new Tree(k, newval, t.left, t.right) :
                    k.compareTo(t.key) < 0 ?
                        new Tree(t.key, t.val, functionalUpdate(k,newval, t.left), t.right) :
                        new Tree(t.key, t.val, t.left, functionalUpdate(k,newval, t.right));
            // In general, this code isn’t expensive. If the tree is of depth d and reasonably well-balanced, it can have around 2^d entries, so you recreate a small fraction of it.
            // This code is written as a single conditional expression instead of using if-then-else to emphasize the idea that the body is a single expression with no side effects. But you may prefer to write an equivalent if-then-else chain, each containing a return.
        }
        // What’s the difference between update and functionalUpdate?
        // The [update] method assumes that every user wants to share the data structure and see updates caused by any part of the program. Therefore, it’s vital (but often overlooked) in nonfunctional code that whenever you add some form of structured value to a tree, you copy it, because someone may assume later that he can update it.
        // By contrast, functionalUpdate is purely functional, it creates a new Tree as a result but shares as much as it can with its argument. Calling the method doesn’t modify the existing tree, it creates new nodes "living at the side of" the tree without harming the existing data structure. Such functional data structures are often called persistent — their values persist and are isolated from changes happening elsewhere, so as a programmer, you’re sure that an action won’t mutate the data structures passed as its arguments. There’s one proviso: the other side of the treaty requires all users of persistent data structures to follow the do-not-mutate requirement. If not, a programmer who disregards this proviso might mutate the result of functionalUpdate (by changing Emily’s 20, for example). Then this mutation would be visible as an (almost certainly unwanted) unexpected and delayed change to the data structure passed as argument to functionalUpdate. Viewed in these terms, functionalUpdate can be more efficient. The "no mutation of existing structure" rule allows structures that differ only slightly (such as the Tree seen by user A and the modified version seen by user B) to share storage for common parts of their structure. You can get the compiler to help enforce this rule by declaring fields key, val, left, and right of class Tree to be final. But remember that final protects only a field, not the object pointed to, which may need its own fields to be final to protect it, and so on.
        // If you want updates to the tree to be seen by some users but not all of them, you have two choices:
        //   - One choice is the classical Java solution - be careful when updating something to check whether you need to copy it first.
        //   - The other choice is the functional-style solution: you logically make a new data structure whenever you do an update (so that nothing is ever mutated) and arrange to pass the correct version of the data structure to users as appropriate. This idea could be enforced through an API. If certain clients of the data structure need to have updates visible, they should go through an API that returns the latest version. Clients who don’t want updates visible (such as for long-running statistical analysis) use whatever copy they retrieve, knowing that it can’t be mutated from under them.
        // This technique heavily used in some databases by name versioning system.
        public static void testTreeUpdate() {
            Tree t = new Tree("Mary", 22,
                new Tree("Emily", 20,
                    new Tree("Alan", 50, null, null),
                    new Tree("Georgie", 23, null, null)
                ),
                new Tree("Tian", 29,
                    new Tree("Raoul", 23, null, null),
                    null
                )
            );
            // found = 23
            System.out.printf("Raoul: %d%n", Tree.lookup("Raoul", -1, t));
            // not found = -1
            System.out.printf("Jeff: %d%n", Tree.lookup("Jeff", -1, t));

            Tree f = functionalUpdate("Jeff", 80, t);
            // found = 80
            System.out.printf("Jeff: %d%n", Tree.lookup("Jeff", -1, f));

            Tree u = update("Jim", 40, t);
            // t was not altered by functionalUpdate, so Jeff is not found = -1
            System.out.printf("Jeff: %d%n", Tree.lookup("Jeff", -1, u));
            // found = 40
            System.out.printf("Jim: %d%n", Tree.lookup("Jim", -1, u));

            Tree f2 = functionalUpdate("Jeff", 80, t);
            // found = 80
            System.out.printf("Jeff: %d%n", Tree.lookup("Jeff", -1, f2));
            // f2 built from t altered by update() above, so Jim is still present = 40
            System.out.printf("Jim: %d%n", Tree.lookup("Jim", -1, f2));
        }
    }

    /**
     * Lazy evaluation with streams.
     */
    public static class LazyEvaluation {
    // One limitation using Java Strem API is that you can’t define a stream recursively because a stream can be consumed only once - this situation can be problematic.
    // To understand this idea of a recursive stream consider computation on a stream of prime numbers as follows:
        public static Stream<Integer> primes(int n) {
            return Stream.iterate(2, i -> i + 1)
                .filter(Prime::isPrimeLittleOptimized)
                .limit(n);
        }
    // But this solution isn't optimal. You have to iterate through every number every time to see whether it can be divided by a candidate number (in fact, you need only test numbers that have been already classified as prime). Ideally, the stream should filter out numbers that are divisible by the prime that the stream is producing on the go. Here’s how this process might work:
    //  1. You need a stream of numbers from which you’ll select prime numbers.
    //  2. From that stream, take the first number (the head of the stream), which will be a prime number (in the initial step, this number is 2).
    //  3. Filter all the numbers that are divisible by that number from the tail of the stream.
    //  4. The resulting tail is the new stream of numbers that you can use to find prime numbers. Essentially, you go back to step 1, so this algorithm is recursive.
    // Despite this algorithm is poor for a few reasons it’s simple to reason about algorithms for the purpose of working with streams.
    // Next an algorithm implementation by using the Streams API step-by-step.
    //  Step1: get a stream of numbers.
        static IntStream numbers() {
            return IntStream.iterate(2, n -> n + 1);
        }
    //  Step2: take the head.
        static int head(IntStream numbers){
            return numbers.findFirst().getAsInt();
        }
    //  Step3: filter the tail.
        static IntStream tail(IntStream numbers){
            return numbers.skip(1);
        }
    //      Given the head of the stream, you can filter the numbers as follows:
        IntStream numbers = numbers();
        int head = head(numbers);
        IntStream filtered = tail(numbers).filter(n -> n % head != 0);
    //  Step4: recursively create a stream of primes.
        static IntStream primes(IntStream numbers) {
            int head = head(numbers);
            return IntStream.concat(
                IntStream.of(head),
                primes(tail(numbers).filter(n -> n % head != 0))
            );
        }
        // Unfortunately, if you run the code in Step4, you get the following error: "java.lang.IllegalStateException: stream has already been operated upon or closed". Indeed, you’re using two terminal operations to split the stream into its head and tail: findFirst and skip.
        // There’s an additional, more important problem: the static method IntStream.concat expects two instances of a stream, but its second argument is a direct recursive call to primes, resulting in an infinite recursion. It’s good to reflect for a moment about how the arguments are evaluated. In Java, when you call a method, all its arguments are fully evaluated immediately. But when you use #:: in Scala, the concatenation returns immediately, and the elements are evaluated only when needed.
        // Here to generate an infinite list of prime numbers implementing the algorithm we described earlier you are going to use the lazy list concept.
        // Lazy lists form a concept similar to stream but in more general sense.
        // Java 8 streams are often described as being lazy. They’re lazy in one particular aspect: a stream behaves like a black box that can generate values on request. When you apply a sequence of operations to a stream, these operations are merely saved up. Only when you apply a terminal operation to a stream is anything computed. This delaying has a great advantage when you apply several operations (perhaps a filter and a map followed by a terminal operation reduce) to a stream: the stream has to be traversed only once instead of for each operation.
        // Lazy lists provide an excellent way of thinking about higher-order functions.
        // By contrast, elements of a LinkedList exist (are spread out) in memory. But elements of a LazyList are created on demand by a Function (you can see them as being spread out in time), you place a function value in a data structure so that most of the time, it can sit there unused, but when it’s called (on demand), it can create more of the data structure.

        // You can define a simple linked-list-style class called MyLinkedList in Java by writing it as follows (with a minimal MyList interface):
        interface MyList<T> {
            T head();
            MyList<T> tail();
            default boolean isEmpty() { return true; }
            MyList<T> filter(Predicate<T> p);
        }
        static class MyLinkedList<T> implements MyList<T> {
            final T head;
            final MyList<T> tail;
            public MyLinkedList(T head, MyList<T> tail) { this.head = head; this.tail = tail; }
            @Override
            public T head() { return head; }
            @Override
            public MyList<T> tail() { return tail; }
            @Override
            public boolean isEmpty() { return false; }
            @Override
            public MyList<T> filter(Predicate<T> p) {
                return isEmpty() ?
                    this :
                    p.test(head()) ?
                        new MyLinkedList<>(head(), tail().filter(p)) :
                        tail().filter(p);
            }
        }
        static class Empty<T> implements MyList<T> {
            @Override
            public T head() { throw new UnsupportedOperationException(); }
            @Override
            public MyList<T> tail() { throw new UnsupportedOperationException(); }
            @Override
            public MyList<T> filter(Predicate<T> p) { return this; }
        }
        public static void testMyLinkedList() {
            MyList<Integer> l = new MyLinkedList<>(5, new MyLinkedList<>(10, new Empty<>()));
            System.out.println(l.head());
        }
        // An easy way to adapt this class to the concept of a lazy list is to cause the tail not to be present in memory all at once, but to have the Supplier<T> (you can see it as being a factory with a function descriptor void -> T) produce the next node of the list. This design leads to the following code:
        static class LazyList<T> implements MyList<T> {
            final T head;
            final Supplier<MyList<T>> tail;
            public LazyList(T head, Supplier<MyList<T>> tail) { this.head = head; this.tail = tail; }
            @Override
            public T head() { return head; }
            @Override
            // Calling the method get from the Supplier causes the creation of a node of the LazyList (as a factory would create a new object).
            public MyList<T> tail() { return tail.get(); }
            @Override
            public boolean isEmpty() { return false; }
            @Override
            public MyList<T> filter(Predicate<T> p) {
                return isEmpty() ?
                    // [this] as good as [Empty<>()]
                    this :
                    p.test(head()) ?
                        new LazyList<>(head(), () -> tail().filter(p)) :
                        tail().filter(p);
            }
        }
        // Creates the infinite lazy list of numbers starting at n.
        public static LazyList<Integer> from(int n) {
            // Pass a Supplier as the tail argument of the LazyList constructor, which creates the next element in the series of numbers.
            return new LazyList<Integer>(n, () -> from(n + 1));
        }
        //. To check, insert System.out.println appropriately or note that
        //from(2)would run forever if it tried to calculate all the numbers starting from 2:
        public static void testLazyList() {
            LazyList<Integer> numbers = from(2);
            int two = numbers.head();
            int three = numbers.tail().head();
            int four = numbers.tail().tail().head();
            // Indeed, the numbers are generated on demand
            // Prints: "2 3 4"
            System.out.println(two + " " + three + " " + four);
            // Prints endlessly until stackoverflow occur.
            //System.out.println(from(2));
        }

        // Generating primes again.
        public static MyList<Integer> primes(MyList<Integer> numbers) {
            return new LazyList<>(
                numbers.head(),
                () -> primes(
                    numbers.tail().filter(n -> n % numbers.head() != 0)
                )
            );
        }
        public static void testPrimesWithLazyList() {
            LazyList<Integer> numbers = from(2);
            int prime_two = primes(numbers).head();
            int prime_three = primes(numbers).tail().head();
            int prime_five = primes(numbers).tail().tail().head();
            // Prints: "2 3 5"
            System.out.println(prime_two + " " + prime_three + " " + prime_five);
            // Prints all the prime numbers. This will run until a stackoverflow occur because Java does not support tail call elimination.
            //printAll(primes(from(2)));
        }
        static <T> void printAll(MyList<T> numbers) {
            if (numbers.isEmpty()) { return; }
            System.out.println(numbers.head());
            printAll(numbers.tail());
            //while (!list.isEmpty()){
            //  System.out.println(list.head());
            //  list = list.tail();
            //}
        }
        // There remains the question of performance. It’s easy to assume that doing things lazily is better than doing things eagerly. Surely, it’s better to calculate only the values and data structures needed by a program on demand than to create all those values (and perhaps more), as done in traditional execution. Unfortunately, the real world isn’t so simple. The overhead of doing things lazily (such as the additional Suppliers between items in a LazyList) outweighs the notional benefit unless you explore, say, less than 10 % of the data structure. Finally, there’s a subtle way in which a LazyList values aren’t truly lazy. If you traverse a LazyList value such as from(2), perhaps up to the 10th item, it also creates all the nodes twice, creating 20 nodes rather than 10. This result is hardly lazy. The issue is that the Supplier in tail is repeatedly called on each on-demand exploration of the LazyList. You can fix this problem by arranging for the Supplier in tail to be called only on the first on-demand exploration, with the resulting value being cached, in effect solidifying the list at that point. To achieve this goal, add a private Optional<LazyList<T>> alreadyComputed field to your definition of LazyList and arrange for the tail method to consult and update it appropriately. The pure functional language Haskell arranges that all its data structures are properly lazy in the latter sense.
        // Lazy data structures can be useful weapons in a programming armory. Use these structures when they make an application easier to program; rewrite them in more traditional style if they cause unacceptable inefficiency.
    }

    /**
     * Pattern matching.
     */
    public static class PatternMatching {
    /*
    There’s one other important aspect to what’s generally regarded as functional programming: (structural) pattern matching, which isn’t to be confused with pattern matching and regex.
    In mathematics you can write definitions such as:
        f(0) = 1
        f(n) = n*f(n-1) otherwise
    whereas in Java, you have to write an if-then-else or a switch statement. As data types become more complex, the amount of code (and clutter) needed to process them increases. Using pattern matching can reduce this clutter.
    To illustrate, take a tree structure that you’d like to traverse. Consider a simple arithmetic language consisting of numbers and binary operations:
        class Expr { ... }
        class Number extends Expr { int val; ... }
        class BinOp extends Expr { String opname; Expr left, right; ... }
    Suppose that you’re asked to write a method to simplify some expressions. 5 + 0 can be simplified to 5, for example. Using our Expr class, new BinOp("+", new Number(5), new Number(0)) could be simplified to Number(5). You might traverse an Expr structure as follows:
        Expr simplifyExpression(Expr expr) {
            if (expr instanceof BinOp
                && ((BinOp)expr).opname.equals("+"))
                && ((BinOp)expr).right instanceof Number
                && ... // it's all getting very clumsy
                && ... ) {
                return (Binop)expr.left;
            }
            ...
        }
    This code rapidly gets ugly!

    Visitor design pattern.
    Another way to unwrap the data type in Java is to use the visitor design pattern. In essence, you create a separate class that encapsulates an algorithm to visit a specific data type. The visitor class works by taking as input a specific instance of the data type; then it can access all its members.
    Here’s an example. First, add the method [accept] to [BinOp], which takes [SimplifyExprVisitor] as argument and passes itself to it (and add a similar method for Number):
        class BinOp extends Expr {
            ...
            public Expr accept(SimplifyExprVisitor v) {
                return v.visit(this);
            }
        }
    Now the [SimplifyExprVisitor] can access a [BinOp] object and unwrap it:
        public class SimplifyExprVisitor {
            ...
            public Expr visit(BinOp e) {
                if("+".equals(e.opname) && e.right instanceof Number && ...) {
                    return e.left;
                }
                return e;
            }
        }

    Pattern matching to the rescue.
    A simpler solution uses a feature called pattern matching. Scala-like pattern matching emerged in Java 17, so first here's the implementation Scala-like pattern matching using Java 8. Given data type Expr representing arithmetic expressions, in the Scala programming language (which we use because its syntax is closest to Java), you can write the following code to decompose an expression:
        def simplifyExpression(expr: Expr): Expr = expr match {
            case BinOp("+", e, Number(0)) => e  // Adding zero
            case BinOp("*", e, Number(1)) => e  // Multiplying by one
            case BinOp("/", e, Number(1)) => e  // Dividing by one
            case _ => expr  // Can't simplify expr
        }
    This use of pattern matching gives you an extremely concise and expressive way to manipulate many treelike data structures. Typically, this technique is useful for building compilers or engines for processing business rules.
    Note that the Scala syntax:
        Expression match { case Pattern => Expression ... }
    is similar to the Java syntax
        switch (Expression) { case Constant : Statement ... }
    with Scala's wildcard pattern [_] making the final [case _] play the role of [default:] in Java. The main visible syntactic difference is that Scala is expression-oriented, whereas Java is more statement-oriented. But for the programmer, the main expressiveness difference is the fact that Java patterns in case labels are restricted to a couple of primitive types, enumerations, a few special classes that wrap certain primitive types, and Strings. One of the biggest practical advantages of using languages with pattern matching is that you can avoid using big chains of switch or if-then-else statements interleaved with field-selection operations. It’s clear that Scala’s pattern matching wins on ease of expressiveness over Java.
    Java 8 lambdas can provide an alternative way of achieving pattern-matching-like code in Java.
    First, consider how rich Scala's pattern-matching [match] expression form is. The case:
        def simplifyExpression(expr: Expr): Expr = expr match {
            case BinOp("+", e, Number(0)) => e
            ...
    means "Check that expr is a BinOp, extract its three components (opname, left, right), and then pattern-match these components — the first against the String +, the second against the variable e (which always matches), and the third against the pattern Number(0)". In other words, pattern matching in Scala (and many other functional languages) is multilevel. Simulation of pattern matching with Java 8 lambdas produces only single-level pattern matching. In the preceding example, the simulation would express cases such as BinOp(op, l, r) or Number(n) but not BinOp("+", e, Number(0)).
    With lambdas you could if-then-else code such as [condition ? e1 : e2] with a method call, as follows: [myIf(condition, () -> e1, () -> e2);], where myIf is:
        static <T> T myIf(boolean b, Supplier<T> truecase, Supplier<T> falsecase) {
            return b ? truecase.get() : falsecase.get();
        }
    You can perform similar tricks with other control-flow constructs such as switch and while.
    Returning to pattern-matching values of class Expr (which has two subclasses, BinOp and Number), you can define a method patternMatchExpr (generic in T, the result type of the pattern match):
    */
        static <T> T patternMatchExpr(
            Expr e,
            TriFunction<String, Expr, Expr, T> binopcase,
            Function<Integer, T> numcase,
            Supplier<T> defaultcase
        ) {
            if (e instanceof BinOp) {
                return binopcase.apply(((BinOp) e).opname, ((BinOp) e).left, ((BinOp) e).right);
            }
            else if (e instanceof Number) { return numcase.apply(((Number) e).val); }
            else { return defaultcase.get(); }
        }
        static interface TriFunction<S, T, U, R> { R apply(S s, T t, U u); }
        static class Expr {}
        static class Number extends Expr {
            int val;
            public Number(int val) { this.val = val; }
            @Override
            public String toString() { return "" + val; }
        }
        static class BinOp extends Expr {
            String opname;
            Expr left, right;
            public BinOp(String opname, Expr left, Expr right) {
                this.opname = opname; this.left = left; this.right = right;
            }
            @Override
            public String toString() { return "(" + left + " " + opname + " " + right + ")"; }
        }
        /*
        The result is that the method call:
            patternMatchExpr(
                e,
                (op, l, r) -> {return binopcode;},
                (n) -> {return numcode;},
                () -> {return defaultcode;}
            );
        determines whether e is a BinOp (and if so, runs binopcode , which has access to the fields of the BinOp via identifiers op, l, r ) or a Number (and if so, runs numcode, which has access to the value n). The method even makes provision for defaultcode, which would be executed if someone later created a tree node that was neither a BinOp nor a Number.
        Back to simplification task:
        */
        private static Expr simplifyExpression(Expr e) {
            Supplier<Expr> defaultcase = () -> new Number(0);
            Function<Integer, Expr> numcase = val -> new Number(val);
            TriFunction<String, Expr, Expr, Expr> binopcase = (opname, left, right) -> {
                if ("+".equals(opname)) {
                    if (left instanceof Number && ((Number) left).val == 0) { return right; }
                    if (right instanceof Number && ((Number) right).val == 0) { return left; }
                }
                if ("*".equals(opname)) {
                    if (left instanceof Number && ((Number) left).val == 1) { return right; }
                    if (right instanceof Number && ((Number) right).val == 1) { return left; }
                }
                return new BinOp(opname, left, right);
            };

            return patternMatchExpr(e, binopcase, numcase, defaultcase);
        }
        public static void testSimplifyExpression() {
            Expr expr = simplifyExpression(new BinOp("+", new Number(5), new Number(0)));
            if (expr instanceof Number) { System.out.println("Number: " + expr); }
            else if (expr instanceof BinOp) { System.out.println("BinOp: " + expr); }
        }
        // To evaluate expression you can implement pattern matching like this:
        private static Integer evaluate(Expr e) {
            Supplier<Integer> defaultcase = () -> 0;
            Function<Integer, Integer> numcase = val -> val;
            TriFunction<String, Expr, Expr, Integer> binopcase = (opname, left, right) -> {
                if ("+".equals(opname)) {
                    if (left instanceof Number && right instanceof Number) {
                        return ((Number) left).val + ((Number) right).val;
                    }
                    if (right instanceof Number && left instanceof BinOp) {
                        return ((Number) right).val + evaluate(left);
                    }
                    if (left instanceof Number && right instanceof BinOp) {
                        return ((Number) left).val + evaluate(right);
                    }
                    if (left instanceof BinOp && right instanceof BinOp) {
                        return evaluate(left) + evaluate(right);
                    }
                }
                if ("*".equals(opname)) {
                    if (left instanceof Number && right instanceof Number) {
                        return ((Number) left).val * ((Number) right).val;
                    }
                    if (right instanceof Number && left instanceof BinOp) {
                        return ((Number) right).val * evaluate(left);
                    }
                    if (left instanceof Number && right instanceof BinOp) {
                        return ((Number) left).val * evaluate(right);
                    }
                    if (left instanceof BinOp && right instanceof BinOp) {
                        return evaluate(left) * evaluate(right);
                    }
                }
                return defaultcase.get();
            };

            return patternMatchExpr(e, binopcase, numcase, defaultcase);
        }
        public static void testEvaluateExpression() {
            Expr expr = new BinOp("+", new Number(5), new BinOp("*", new Number(3), new Number(4)));
            Integer result = evaluate(expr);
            System.out.println(expr + " = " + result);
        }
    }

    /**
     * Miscellany.
     */
    public static class Miscellany {
    /*
    In functional programming, it’s common and natural to write a higher-order function (perhaps written as a method) that accepts, say, two functions and produces another function that somehow combines these functions. The term combinator generally is used for this idea. Much of the new Java 8 API is inspired by this idea, such as thenCombine in the CompletableFuture class. You can give this method two CompletableFutures and a BiFunction to produce another CompletableFuture.
    */
        public static class Combinators {
            // The following method encodes the idea of function composition. This method takes functions f and g as arguments and returns a function whose effect is to do f first and then g.
            static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, B> f) {
                return x -> g.apply(f.apply(x));
            }
            // Then you can define an operation that captures internal iteration as a combinator. Suppose that you want to take data and apply function f to it repeatedly, n times, as in a loop. Your operation (call it repeat) takes a function, f, saying what happens in one iteration and returning a function that says what happens in n iterations. A call such as [repeat(3, (Integer x) -> 2*x);] returns [x -> (2*(2*(2*x))] or equivalently [x -> 8*x].
            static <A> Function<A, A> repeat(int n, Function<A, A> f) {
                return n == 0 ? x -> x : compose(f, repeat(n - 1, f));
            }
        }
        public static void testCombinators() {
            // Prints: 10
            System.out.println(Combinators.repeat(0, (Integer x) -> 2 * x).apply(10));
            // Prints: 80
            System.out.println(Combinators.repeat(3, (Integer x) -> 2 * x).apply(10));
        }
    }
}
