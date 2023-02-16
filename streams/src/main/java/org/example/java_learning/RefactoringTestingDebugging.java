package org.example.java_learning;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileReader;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.example.java_learning.Dish.menu;



public class RefactoringTestingDebugging {
    /* Most of the time you have to deal with an existing code base written in an older version of Java. Here is several recipes that show you how to refactor existing code to use lambda expressions to gain readability and flexibility. You will learn how several object-oriented design patterns (including strategy, template method, observer, chain of responsibility, and factory) can be made more concise thanks to lambda expressions. You will explore how you can test and debug code that uses lambda expressions and the Streams API.
     */
    // Should you use lambda expressions all the time? The answer is no. In the example we described, lambda expressions work great because the behavior to execute is simple, so they’re helpful for removing boilerplate code. But the observers may be more complex; they could have state, define several methods, and the like. In those situations, you should stick with classes.

    /**
     * Refactoring anonymous classes to lambda expressions
     */
    public static class AnonymousToLambda {
        // Converting anonymous classes to lambda expressions can be a difficult process in certain situations.
        public static void anonymousToLambda() {
            // The meanings of [this] and [super] keywords are different for anonymous classes and lambda expressions. Inside an anonymous class, this refers to the anonymous class itself, but inside a lambda, it refers to the enclosing class. Second, anonymous classes are allowed to shadow variables from the enclosing class. Lambda expressions can’t (they’ll cause a compile error).
            int a = 10;
            Runnable r1 = () -> {
                // Compile-time error: Variable 'a' is already defined in the scope.
                //int a = 2;
                System.out.println(a);
            };
            Runnable r2 = new Runnable() {
                public void run() {
                    int a = 2;
                    System.out.println(a);
                }
            };

        }

        // Converting an anonymous class to a lambda expression can make the resulting code ambiguous in the context of overloading. Indeed, the type of anonymous class is explicit at instantiation, but the type of the lambda depends on its context. Here’s an example of how this situation can be problematic. Suppose that you’ve declared a functional interface with the same signature as Runnable, here called Task (as might occur when you need more-meaningful interface names in your domain model):
        interface Task {
            public void execute();
        }

        public static void doSomething(Runnable r) {
            r.run();
        }

        public static void doSomething(Task t) {
            t.execute();
        }

        // Now you can pass an anonymous class implementing Task without a problem:
        public static void testAnonymousUnambiguous() {
            doSomething(new Task() {
                public void execute() {
                    System.out.println("Danger danger!!");
                }
            });
        }

        // But converting this anonymous class to a lambda expression results in an ambiguous method call, because both Runnable and Task are valid target types:
        public static void testLambdaAmbiguous() {
            // Compile-time error.
            //doSomething(() -> System.out.println("Danger danger!!"));
            // You can solve the ambiguity by providing an explicit cast (Task):
            doSomething((Task) () -> System.out.println("Danger danger!!"));
        }
    }

    /**
     * From lambda expressions to method references
     */
    public static class LambdaToMethodReference {
        // Lambda expressions are great for short code that needs to be passed around. But consider using method references whenever possible to improve code readability. A method name states the intent of your code more clearly.
        public static void lambdaToMethodReference() {
            Map<CaloricLevel, List<Dish>> dishesByCaloricLevel1 = menu.stream().collect(
                    groupingBy(dish -> {
                        if (dish.getCalories() <= 400)
                            return CaloricLevel.DIET;
                        else if (dish.getCalories() <= 700)
                            return CaloricLevel.NORMAL;
                        else
                            return CaloricLevel.FAT;
                    }));
            // You can extract the lambda expression into a separate method and pass it as an argument to groupingBy. The code becomes more concise, and its intent is more explicit:
            Map<CaloricLevel, List<Dish>> dishesByCaloricLevel2 = menu.stream().collect(
                    groupingBy(Dish::getCaloricLevel));

            // In addition, consider using helper static methods such as comparing and maxBy whenever possible. These methods were designed for use with method references! Indeed, this code states much more clearly its intent than its counterpart using a lambda expression.
            inventory.sort(
                (Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight()));
            inventory.sort(comparing(Apple::getWeight));

            // Moreover, for many common reduction operations such as sum, maximum, there are built-in helper methods that can be combined with method references.
            int totalCalories1 = menu.stream()
                .map(Dish::getCalories)
                .reduce(0, (c1, c2) -> c1 + c2);
            int totalCalories2 = menu.stream()
                .collect(summingInt(Dish::getCalories));
        }
    }

    /**
     * From imperative data processing to Streams
     */
    public static class ImperativeToStreams {
        // Ideally, you should try to convert all code that processes a collection with typical data processing patterns with an iterator to use the Streams API instead. Why? The Streams API expresses more clearly the intent of a data processing pipeline. In addition, streams can be optimized behind the scenes, making use of short-circuiting and laziness as well as leveraging multicore architecture. Converting imperative code to the Streams API can be a difficult task, because you need to think about control-flow statements such as break, continue, and return and then infer the right stream operations to use.
        public static void imperativeToStreams() {
            // The following imperative code expresses two patterns (filtering and extracting) that are mangled together, forcing the programmer to carefully figure out the whole implementation before figuring out what the code does.
            List<String> dishNames = new ArrayList<>();
            for (Dish dish: menu) {
                if (dish.getCalories() > 300) {
                    dishNames.add(dish.getName());
                }
            }
            menu.parallelStream()
                .filter(d -> d.getCalories() > 300)
                .map(Dish::getName)
                .collect(toList());
        }
    }

    /**
     * Improving code flexibility
     */
    public static class CodeFlexibility {
        // Lambda expressions encourage the style of behavior parameterization. You can represent multiple behaviors with different lambdas that you can then pass around to execute. This style lets you cope with requirement changes (creating multiple ways of filtering with a Predicate or comparing with a Comparator, for example). Next we look at a couple of patterns that you can apply to your code base to benefit immediately from lambda expressions.
        public static void codeFlexibility() throws IOException {
            // First, you can’t use lambda expressions without functional interfaces; therefore, you should start introducing them in your code base. Two common code patterns that can be refactored to leverage lambda expressions: conditional deferred execution and execute around.

            // Conditional deferred execution.
            // It’s common to see control-flow statements mangled inside business-logic code. Typical scenarios include security checks and logging.
            Logger logger = Logger.getLogger("");
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Problem: " + "generateDiagnostic()");
            }
            // What’s wrong with it? A couple of things:
            //  - The state of the logger (what level it supports) is exposed in the client code through the method isLoggable.
            //  - Why should you have to query the state of the logger object every time before you log a message? It clutters your code.
            // A better alternative is to use the log method, which checks internally to see whether the logger object is set to the right level before logging the message:
            logger.log(Level.FINER, "Problem: " + "generateDiagnostic()");
            // Unfortunately, this code still has an issue: the logging message is always evaluated, even if the logger isn’t enabled for the message level passed as an argument.
            // What you need is a way to defer the construction of the message so that it can be generated only under a given condition (here, when the logger level is set to FINER). There is an overloaded alternative to log that takes a Supplier as an argument. This alternative log method has the following signature:
            //public void log(Level level, Supplier<String> msgSupplier)
            logger.log(Level.FINER, () -> "Problem: " + "generateDiagnostic()");
            // The internal implementation of the log method:
            //public void log(Level level, Supplier<String> msgSupplier) {
            //    if(logger.isLoggable(level)){
            //        log(level, msgSupplier.get()); // Executing the lambda.
            //    }
            //}
            // What’s the takeaway from the story? If you see yourself querying the state of an object (such as the state of the logger) many times in client code, only to call some method on this object with arguments (such as to log a message), consider introducing a new method that calls that method, passed as a lambda or method reference, only after internally checking the state of the object. Your code will be more readable (less cluttered) and better encapsulated, without exposing the state of the object in client code.

            // Execute around.
            // If you find yourself surrounding different code with the same preparation and cleanup phases, you can often pull that code into a lambda. The benefit is that you can reuse the logic dealing with the preparation and cleanup phases, thus reducing code duplication.
            try {
                String oneLine = processFile((BufferedReader b) -> b.readLine());
                String twoLines = processFile((BufferedReader b) -> b.readLine() + b.readLine());
            } catch (IOException e) { throw new RuntimeException(e); }
        }
    }

    /**
     * Refactoring object-oriented design patterns with lambdas
     */
    public static class DesignPatternsWithLambdas {
        // You will learn how lambda expressions can provide an alternative way to solve the problem that following design pattern is intended to solve:
        //  - Strategy
        //  - Template method
        //  - Observer
        //  - Chain of responsibility
        //  - Factory
        public static class StrategyWithLambdas {
            // The strategy pattern is a common solution for representing a family of algorithms and letting you choose among them at runtime in various application, such as:
            //  - filtering with different predicates;
            //  - validating an input with different criteria;
            //  - using different ways of parsing;
            //  - formatting an input and others
            // The strategy pattern consists of three parts:
            //  - An interface to represent some algorithm (the interface Strategy).
            //  - One or more concrete implementations of that interface to represent multiple algorithms (the concrete classes ConcreteStrategyA , ConcreteStrategyB).
            //  - One or more clients that use the strategy objects.
            // [Client]-->[Strategy.execute()]<--[ConcreteStrategy(A|B|C...)]
            //
            // Suppose that you’d like to validate whether a text input is properly formatted for different criteria (consists of only lowercase letters or is numeric, for example). You start by defining an interface to validate the text (represented as a String).
            interface ValidationStrategy {
                boolean execute(String s);
            }
            // Second, you define one or more implementation(s) of that interface.
            static private class IsAllLowerCase implements ValidationStrategy {
                @Override
                public boolean execute(String s) {
                    return s.matches("[a-z]+");
                }
            }
            static private class IsNumeric implements ValidationStrategy {
                @Override
                public boolean execute(String s) {
                    return s.matches("\\d+");
                }
            }
            // Then you can use these different validation strategies in your program.
            static private class Validator {
                private final ValidationStrategy strategy;
                public Validator(ValidationStrategy v) {
                    strategy = v;
                }
                public boolean validate(String s) {
                    return strategy.execute(s);
                }
            }
            public static void strategyWithLambdas() {
                Validator v1 = new Validator(new IsNumeric());
                System.out.println(v1.validate("aaaa"));
                Validator v2 = new Validator(new IsAllLowerCase());
                System.out.println(v2.validate("bbbb"));
                // The ValidationStrategy is a functional interface. In addition, it has the same function descriptor as Predicate<String>. As a result, instead of declaring new classes to implement different strategies, you can pass more concise lambda expressions directly.
                Validator v3 = new Validator((String s) -> s.matches("\\d+"));
                System.out.println(v3.validate("aaaa"));
                Validator v4 = new Validator((String s) -> s.matches("[a-z]+"));
                System.out.println(v4.validate("bbbb"));
                // Lambda expressions encapsulate a piece of code (or strategy), which is what the strategy design pattern was created for, so it's recommended to use lambda expressions instead for similar problems.
            }
        }

        public static class TemplateMethod {
            // The template method design pattern is a common solution when you need to represent the outline of an algorithm and have the additional flexibility to change certain parts of it. In other words, the template method pattern is useful when you find yourself saying "I’d love to use this algorithm, but I need to change a few lines so it does what I want".
            // As an example, suppose that you need to write a simple online banking application. Users typically enter a customer ID; the application fetches the customer’s details from the bank’s database and does something to make the customer happy. Different online banking applications for different banking branches may have different ways of making a customer happy (such as adding a bonus to his account or sending him less paperwork). You can write the following abstract class to represent the online banking application.
            abstract class OnlineBanking {
                public void processCustomer(int id) {
                    Customer c = Database.getCustomerWithId(id);
                    makeCustomerHappy(c);
                }
                abstract void makeCustomerHappy(Customer c);
            }
            // Now different branches can provide different implementations of the makeCustomerHappy method by subclassing the OnlineBanking class.

            // With lambdas.
            public static class OnlineBankingLambda {
                public static void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
                    Customer c = Database.getCustomerWithId(id);
                    makeCustomerHappy.accept(c);
                }
            }
            public static void templateMethodLambda() {
                new OnlineBankingLambda().processCustomer(1337, (Customer c) -> System.out.println("Hello!"));
            }

            // dummy Customer class
            static class Customer {}
            // dummy Database class
            static class Database {
                static Customer getCustomerWithId(int id) {
                    return new Customer();
                }
            }
        }

        public static class ObserverPattern {
            // The observer design pattern is a common solution when an object (called the subject) needs to automatically notify a list of other objects (called observers) when some event happens (such as a state change).
            // As an example suppose there are several newspaper agencies (The New York Times, The Guardian, and Le Monde) are subscribed to a feed of news tweets and may want to receive a notification if a tweet contains a particular keyword.
            // First, you need an Observer interface that groups the observers. It has one method, called notify, that will be called by the subject (Feed) when a new tweet is available:
            interface Observer {
                void notify(String tweet);
            }
            // Now you can declare different observers (here, the three newspapers) that produce a different action for each different keyword contained in a tweet:
            static class NYTimes implements Observer {
                @Override
                public void notify(String tweet) {
                    if (tweet != null && tweet.contains("money")) {
                        System.out.println("Breaking news in NY!" + tweet);
                    }
                }
            }
            static class Guardian implements Observer {
                @Override
                public void notify(String tweet) {
                    if (tweet != null && tweet.contains("queen")) {
                        System.out.println("Yet another news in London... " + tweet);
                    }
                }
            }
            static private class LeMonde implements Observer {
                @Override
                public void notify(String tweet) {
                    if (tweet != null && tweet.contains("wine")) {
                        System.out.println("Today cheese, wine and news! " + tweet);
                    }
                }
            }
            // Define an interface for the subject. The subject can register a new observer using the registerObserver method and notify his observers of a tweet with the notifyObservers method:
            interface Subject {
                void registerObserver(Observer o);
                void notifyObservers(String tweet);
            }
            // Now implement the Feed class:
            static class Feed implements Subject {
                private final List<Observer> observers = new ArrayList<>();
                @Override
                public void registerObserver(Observer o) {
                    observers.add(o);
                }
                @Override
                public void notifyObservers(String tweet) {
                    observers.forEach(o -> o.notify(tweet));
                }
            }
            // Use it:
            public static void observer() {
                Feed feed = new Feed();
                feed.registerObserver(new NYTimes());
                feed.registerObserver(new Guardian());
                feed.registerObserver(new LeMonde());
                feed.notifyObservers("The queen said her favourite book is Java 8 & 9 in Action!");
                // Using lambdas:
                Feed feedLambda = new Feed();
                feedLambda.registerObserver((String tweet) -> {
                    if (tweet != null && tweet.contains("money")) {
                        System.out.println("Breaking news in NY! " + tweet);
                    }
                });
                feedLambda.registerObserver((String tweet) -> {
                    if (tweet != null && tweet.contains("queen")) {
                        System.out.println("Yet another news in London... " + tweet);
                    }
                });
                feedLambda.notifyObservers("Money money money, give me money!");
            }
        }

        public static class ChainOfResponsibility {
            // The chain of responsibility pattern is a common solution to create a chain of processing objects (such as a chain of operations). One processing object may do some work and pass the result to another object, which also does some work and passes it on to yet another processing object, and so on.
            // Generally, this pattern is implemented by defining an abstract class representing a processing object that defines a field to keep track of a successor. When it finishes its work, the processing object hands over its work to its successor.
            private static abstract class ProcessingObject<T> {
                protected ProcessingObject<T> successor;
                public void setSuccessor(ProcessingObject<T> successor) {
                    this.successor = successor;
                }
                // Here, you may recognize the template method design pattern. The handle method provides an outline for dealing with a piece of work. You can create different kinds of processing objects by subclassing the ProcessingObject class and by providing an implementation for the handleWork method.
                public T handle(T input) {
                    T r = handleWork(input);
                    if (successor != null) { return successor.handle(r); }
                    return r;
                }
                abstract protected T handleWork(T input);
            }
            private static class HeaderTextProcessing extends ProcessingObject<String> {
                @Override
                public String handleWork(String text) {
                    return "From Raoul, Mario and Alan: " + text;
                }
            }
            private static class SpellCheckerProcessing extends ProcessingObject<String> {
                @Override
                public String handleWork(String text) {
                    return text.replaceAll("labda", "lambda");
                }
            }

            public static void chainOfResponsibility() {
                // Connect two processing objects to construct a chain of operations:
                ProcessingObject<String> p1 = new HeaderTextProcessing();
                ProcessingObject<String> p2 = new SpellCheckerProcessing();
                p1.setSuccessor(p2);
                String result1 = p1.handle("Aren't labdas really sexy?!!");
                // This pattern looks like chaining (that is, composing) functions. You can represent the processing objects as an instance of Function<String, String>, or (more precisely) a UnaryOperator<String>. To chain them, compose these functions by using the andThen method:
                UnaryOperator<String> headerProcessing =
                    (String text) -> "From Raoul, Mario and Alan: " + text;
                UnaryOperator<String> spellCheckerProcessing =
                    (String text) -> text.replaceAll("labda", "lambda");
                Function<String, String> pipeline =
                    headerProcessing.andThen(spellCheckerProcessing);
                String result2 = pipeline.apply("Aren't labdas really sexy?!!");
            }
        }

        public static class FactoryPattern {
            // The factory design pattern lets you create objects without exposing the instantiation logic to the client.
            // Suppose that you’re working for a bank that needs a way of creating different financial products: loans, bonds, stocks, and so on.
            // Typically, you’d create a Factory class with a method that’s responsible for the creation of different objects.
            static private class ProductFactory {
                private final static Map<String, Supplier<Product>> map = new HashMap<>();
                static {
                    map.put("loan", Loan::new);
                    map.put("stock", Stock::new);
                    map.put("bond", Bond::new);
                }

                public static Product createProduct(String name) {
                    switch (name) {
                        case "loan": return new Loan();
                        case "stock": return new Stock();
                        case "bond": return new Bond();
                        default: throw new RuntimeException("No such product " + name);
                    }
                }
                // Using lambdas
                public static Product createProductLambda(String name) {
                    Supplier<Product> p = map.get(name);
                    if (p != null) { return p.get(); }
                    throw new RuntimeException("No such product " + name);
                }
                // This technique doesn’t scale well if the factory method createProduct needs to take multiple arguments to pass to the product constructors. You’d have to provide a functional interface other than a simple Supplier. Suppose that you want to refer to constructors for products that take three arguments (two Integers and a String); you need to create a special functional interface TriFunction to support such constructors. As a result, the signature of the Map becomes more complex:
                public interface TriFunction<T, U, V, R> {
                    R apply(T t, U u, V v);
                }
                Map<String, TriFunction<Integer, Integer, String, Product>> mapTri = new HashMap<>();
            }
            static private interface Product {}
            static private class Loan implements Product {}
            static private class Stock implements Product {}
            static private class Bond implements Product {}

            public static void FactoryPattern() {
                // The createProduct method could have additional logic to configure each created product. But the benefit is that you can create these objects without exposing the constructor and the configu ration to the client, which makes the creation of products simpler for the client:
                Product p1 = ProductFactory.createProduct("loan");
                System.out.printf("p1: %s%n", p1.getClass().getSimpleName());

                Supplier<Product> loanSupplier = Loan::new;
                Product p2 = loanSupplier.get();
                System.out.printf("p2: %s%n", p2.getClass().getSimpleName());

                Product p3 = ProductFactory.createProductLambda("loan");
                System.out.printf("p3: %s%n", p3.getClass().getSimpleName());
            }
        }
    }

    /**
     * Testing lambdas
     */
    public static class TestingLambdas {
        // Generally, good software engineering practice involves using unit testing to ensure that your program behaves as intended. You write test cases, which assert that small individual parts of your source code are producing the expected results. Consider a simple Point class for a graphical application:
        public class SomePoint {
            // ...
        }
        // The following unit test checks whether the method moveRightBy behaves as expected:
        //@Test
        //public void testMoveRightBy() throws Exception {
        //    SomePoint p1 = new SomePoint(5, 5);
        //    SomePoint p2 = p1.moveRightBy(10);
        //    assertEquals(15, p2.getX());
        //    assertEquals(5, p2.getY());
        //}
        // This code works nicely because the moveRightBy method is public and, therefore, can be tested inside the test case. But lambdas don’t have names (they’re anonymous functions, after all), and testing them in your code is tricky because you can’t refer to them by name.
        // Sometimes, you have access to a lambda via a field so that you can reuse it, and you’d like to test the logic encapsulated in that lambda. What can you do? You could test the lambda as you do when calling methods. Suppose that you add a static field compareByXAndThenY in the Point class that gives you access to a Comparator object generated from method references:
        public static class Point {
            private final int x;
            private final int y;
            private Point(int x, int y) {
                this.x = x;
                this.y = y;
            }
            public int getX() { return x; }
            public int getY() { return y; }
            public Point moveRightBy(int x) {
                return new Point(this.x + x, this.y);
            }
            public final static Comparator<Point> compareByXAndThenY =
                comparing(Point::getX).thenComparing(Point::getY);
        }
        // Remember that lambda expressions generate an instance of a functional interface. As a result, you can test the behavior of that instance. Here, you can call the compare method on the Comparator object compareByXAndThenY with different arguments to test whether its behavior is as intended:
        //@Test
        //public void testComparingTwoPoints() throws Exception {
        //    Point p1 = new Point(10, 15);
        //    Point p2 = new Point(10, 20);
        //    int result = Point.compareByXAndThenY.compare(p1 , p2);
        //    assertTrue(result < 0);
        //}

        // Focusing on the behavior of the method using a lambda.
        // The purpose of lambdas is to encapsulate a one-off piece of behavior to be used by another method. Therefore, you shouldn’t make lambda expressions available publicly, they’re only implementation details. So you should test the behavior of the method that uses a lambda expression. Consider the moveAllPointsRightBy method shown here:
        public List<Point> moveAllPointsRightBy(List<Point> points, int x) {
            return points.stream()
                .map(p -> new Point(p.getX() + x, p.getY()))
                .collect(toList());
        }
        // There’s no point in testing the lambda p -> new Point(p.getX() + x, p.getY()); it’s only an implementation detail for the moveAllPointsRightBy method. Instead, you should focus on testing the behavior of the moveAllPointsRightBy method:
        //@Test
        //public void testMoveAllPointsRightBy() throws Exception {
        //    List<Point> points = Arrays.asList(new Point(5, 5), new Point(10, 5));
        //    List<Point> expectedPoints = Arrays.asList(new Point(15, 5), new Point(20, 5));
        //    List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);
        //    assertEquals(expectedPoints, newPoints);
        //}
        // However, What do you do when you come across a really complicated lambda expression that contains a lot of logic (such as a technical pricing algorithm with corner cases)? You can’t refer to the lambda expression inside your test. One strategy is to convert the lambda expression to a method reference (which involves declaring a new regular method). Then you can test the behavior of the new method as you would that of any regular method.

        // Testing high-order functions.
        // Methods that take a function as an argument or return another function (so-called higher-order functions) are a little harder to deal with. One thing you can do if a method takes a lambda as an argument is test its behavior with different lambdas. For example, you can test the filter method with different predicates:
        //@Test
        //public void testFilter() throws Exception {
        //    List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        //    List<Integer> even = filter(numbers, i -> i % 2 == 0);
        //    List<Integer> smallerThanThree = filter(numbers, i -> i < 3);
        //    assertEquals(Arrays.asList(2, 4), even);
        //    assertEquals(Arrays.asList(1, 2), smallerThanThree);
        //}
        // What if the method that needs to be tested returns another function? You can test the behavior of that function by treating it as an instance of a functional interface, as we showed you earlier with a Comparator.
    }

    /**
     * Debugging
     */
    public static class Debugging {
        // Due to the fact that lambda expressions don’t have names, stack traces can be slightly puzzling. Consider the following simple code, which is made to fail on purpose:
        public static void debugging() {
            List<TestingLambdas.Point> points =
                Arrays.asList(new TestingLambdas.Point(12, 2), null);
            points.stream().map(p -> p.getX()).forEach(System.out::println);
            /*
            Running this code produces a stack trace along the lines of the following (depending on your javac version; you may not have the same stack trace):

                Exception in thread "main" java.lang.NullPointerException
                    at Debugging.lambda$main$0(Debugging.java:6)
                    at Debugging$$Lambda$5/284720968.apply(Unknown Source)
                    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
                    at java.util.Spliterators$ArraySpliterator.forEachRemaining(Spliterators.java:948)
                    ...

            The program fails, of course, because the second element of the list of points is null. You try to process a null reference. Because the error occurs in a stream pipeline, the whole sequence of method calls that make a stream pipeline work is exposed to you. But notice that the stack trace produces the following cryptic lines:

                at Debugging.lambda$main$0(Debugging.java:6)
                at Debugging$$Lambda$5/284720968.apply(Unknown Source)

            These lines mean that the error occurred inside a lambda expression. Unfortunately, because lambda expressions don’t have names, the compiler has to make up a name to refer to them. In this case, the name is lambda$main$0 , which isn’t intuitive and can be problematic if you have large classes containing several lambda expressions.
            Even if you use method references, it’s still possible that the stack won’t show you the name of the method you used. Changing the preceding lambda p -> p.getX() to the method reference Point::getX also results in a problematic stack trace:

                points.stream().map(Point::getX).forEach(System.out::println);
                Exception in thread "main" java.lang.NullPointerException
                    at Debugging$$Lambda$5/284720968.apply(Unknown Source)
                    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline
                    .java:193)
                    ...
            Note that if a method reference refers to a method declared in the same class where it’s used, it appears in the stack trace. In the following example:
                //public class Debugging{
                //    public static void main(String[] args) {
                //        List<Integer> numbers = Arrays.asList(1, 2, 3);
                //        numbers.stream().map(Debugging::divideByZero).forEach(System.out::println);
                //    }
                //    public static int divideByZero(int n) { return n / 0; }
                //}
            // The divideByZero method is reported correctly in the stack trace:

                Exception in thread "main" java.lang.ArithmeticException: / by zero
                    at Debugging.divideByZero(Debugging.java:10)
                    at Debugging$$Lambda$1/999966131.apply(Unknown Source)
                    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline
                    .java:193)
                    ...
            */
        }

        // How to debug a pipeline of operations in a stream? forEach isn't much of the help because after you call forEach, the whole stream is consumed.
        // Examining values flowing in a stream pipeline with peek. The purpose of peek is to execute an action on each element of a stream as it’s consumed. It doesn’t consume the whole stream the way forEach does, however; it forwards the element on which it performed an action to the next operation in the pipeline.
        public static void logging() {
            List<Integer> result = Stream.of(2, 3, 4, 5)
                .peek(x -> System.out.println("taking from stream: " + x))
                .map(x -> x + 17)
                .peek(x -> System.out.println("after map: " + x))
                .filter(x -> x % 2 == 0)
                .peek(x -> System.out.println("after filter: " + x))
                .limit(3)
                .peek(x -> System.out.println("after limit: " + x))
                .collect(toList());
        }
    }


    static List<Apple> inventory = Arrays.asList(
        new Apple(80, Color.GREEN),
        new Apple(155, Color.GREEN),
        new Apple(120, Color.RED)
    );
    static String processFile(BufferedReaderProcessor p) throws IOException {
        try(BufferedReader br = new BufferedReader(new
            FileReader("/org/example/java_learning/data.txt"))) {
            return p.process(br);
        }
    }
    interface BufferedReaderProcessor {
        String process(BufferedReader b) throws IOException;
    }
}
