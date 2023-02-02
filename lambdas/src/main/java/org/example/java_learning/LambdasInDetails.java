package org.example.java_learning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.*;

import static java.util.Comparator.comparing;

/**
 *  Lambdas, Method references, Default methods in interfaces and Functional-style programming in Details
 */
public class LambdasInDetails {
/*
Think of lambda expressions as anonymous functions, methods without declared names, but which can also be passed as arguments to a method as you can with an anonymous class or stored in a variable. Lambda isn’t associated with a particular class like a method is. But like a method, a lambda has a list of parameters, a body, a return type, and a possible list of exceptions that can be thrown.
No need to write a lot of boilerplate like you do for anonymous classes:
  1. Anonymous classes:
    Comparator<Apple> byWeight = new Comparator<Apple>() {
        public int compare(Apple a1, Apple a2) {
            return a1.getWeight().compareTo(a2.getWeight());
        }
    };

  2. Lambda:
    Comparator<Apple> byWeight = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

    (int x, int y) -> {
        System.out.println("Result:");
        System.out.println(x + y);
    }

    () -> 42

  3. expression-style lambda:
    (parameters) -> expression

  4. block-style lambda:
    (parameters) -> {statements;}

  Which is valid:
    1.	() -> {} // burger lambda
    2.	() -> "Raoul"
    3.	() -> {return "Mario";}
    4.	(Integer i) -> return "Alan" + i;
    5.  (Integer i) -> {return "Alan" + i;}
    6.	(String s) -> {"Iron Man";}
    7.  (String s) -> {return "Iron Man";}
    8.	public Callable<String> fetch() {return () -> "Tricky example ;-)";}
    9.	Predicate<Apple> p = (Apple a) -> a.getWeight();

    1. Valid
    2. Valid
    3. Valid
    4. Invalid. Return is a control-flow statement. To make this lambda valid, curly braces are required as in 5.
    5. Valid
    6. Invalid. "Iron Man" is an expression, not a statement. To make this lambda valid, you can remove the curly braces and semicolon as follows: (String s) -> "Iron Man". Or if you prefer, you can use an explicit return statement as in 7.
    7. Valid
    8. Valid. Indeed, the return type of the method fetch is Callable<String>. When T is replaced with String, Callable<String> defines a method with the signature () -> String. Because the lambda () -> "Tricky example ;-)" has the signature () -> String, the lambda can be used in this context.
    9. Invalid. The lambda expression (Apple a) -> a.getWeight() has the signature (Apple) -> Integer, which is different from the signature of the method test defined in Predicate<Apple>: (Apple) -> boolean.
*/
    /**
     * Functional interface
     */
    public class Functionalinterface {
        // Functional interface is an interface that specifies exactly one abstract method, such as: Predicate, Comparator, Runnable, Callable, ActionListener, PrivilegedAction, and other inside inside the java.util.function. You can use a lambda expression in the context of a functional interface An interface is still a functional interface if it has many default methods as long as it specifies only one abstract method.
        // functional
        @FunctionalInterface
        public interface mockAdderFunctional {
            int add(int a, int b);
        }

        // isn’t a functional
        public interface mockAdderNotFunctional extends mockAdderFunctional {
            int add(double a, double b);
        }

        // isn’t a functional
        public interface mockNothingNotFunctional {
        }

        // Lambda expressions let you provide the implementation of the abstract method of a functional interface directly inline and treat the whole expression as an instance of a functional interface (more technically speaking, an instance of a concrete implementation of the functional interface), same thing with an anonymous inner class, although it’s clumsier.
        public static void mockProcess() {
            Runnable r1 = () -> System.out.println("Hello World 1");
            Runnable r2 = new Runnable() {
                public void run() {
                    System.out.println("Hello World 2");
                }
            };
            process(r1);
            process(r2);
            process(() -> System.out.println("Hello World 3"));
        }

        public static void process(Runnable r) {
            r.run();
        }

        /*
        !!The signature of the abstract method of the functional interface describes the signature of the lambda expression. This abstract method called a function descriptor. The notation (T, U) -> R shows how to think about a function descriptor. The left side of the arrow is a list representing the types of the arguments, and the right side represents the types of the results. In this case, it represents a function with two arguments of respectively generic type T and U and that has a return type of R.

        Functional interfaces are generally annotated with @FunctionalInterface. This annotation is used to indicate that the interface is intended to be a functional interface and is therefore useful for documentation. In addition, the compiler will return a meaningful error if you define an interface using the @FunctionalInterface annotation, and it isn’t a functional interface. For example, an error message could be "Multiple non-overriding abstract methods found in interface Foo" to indicate that more than one abstract method is available. Note that the @FunctionalInterface annotation isn’t mandatory, but it’s good practice to use it when an interface is designed for that purpose. You can think of it like the @Override notation to indicate that a method is overridden.

        Some other examples:
          1. Predicate Interface (java.util.function.Predicate<T>):
            @FunctionalInterface
            public interface Predicate<T> {
                boolean test(T t);
            }
        */
        public <T> List<T> mockFilterWithPredicate(List<T> list, Predicate<T> p) {
            List<T> results = new ArrayList<>();
            for (T t : list) {
                if (p.test(t)) {
                    results.add(t);
                }
            }
            return results;
        }

        Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
        List<String> nonEmptyList = mockFilterWithPredicate(
                Arrays.asList("a", "b", "c", "", "", "d", "e"),
                nonEmptyStringPredicate
        );

        /*
          2. Consumer (java.util.function.Consumer<T>):
            The interface defines an abstract method named accept that takes an object of generic type T and returns no result (void). You might use this interface when you need to access an object of type T and perform some operations on it. For example, you can use it to create a method forEach, which takes a list of Integers and applies an operation on each element of that list.
            @FunctionalInterface
            public interface Consumer<T> {
                void accept(T t);
            }
        */
        public <T> void mockForEachConsumer(List<T> list, Consumer<T> c) {
            for (T t : list) {
                c.accept(t);
            }
        }

        public void mockForEachConsumer() {
            mockForEachConsumer(Arrays.asList(1, 2, 3, 4, 5), (Integer i) -> System.out.println(i));
        }

        /*
          3. Function (java.util.function.Function<T, R>):
            The interface defines an abstract method named apply that takes an object of generic type T as input and returns an object of generic type R. You might use this interface when you need to define a lambda that maps information from an input object to an output (for example, extracting the weight of an apple or mapping a string to its length).
            @FunctionalInterface
            public interface Function<T, R> {
                R apply(T t);
            }
        */
        public <T, R> List<R> mockMapFunction(List<T> list, Function<T, R> f) {
            List<R> result = new ArrayList<>();
            for (T t : list) {
                result.add(f.apply(t));
            }
            return result;
        }

        // [7, 2, 6]
        List<Integer> mockMapFunctionResult = mockMapFunction(
                Arrays.asList("lambdas", "in", "action"),
                (String s) -> s.length()
        );
        /*
        Java 8 also added a specialized version of the functional interfaces in order to avoid autoboxing operations when the inputs or outputs are primitives. For example, IntPredicate, DoublePredicate, IntToDoubleFunction etc.
        Common functional interfaces added in Java 8, one can add her own:
            1. Predicate<T>
            	T -> boolean
            		IntPredicate, LongPredicate, DoublePredicate
            2. Consumer<T>
            	T -> void
            		IntConsumer, LongConsumer, DoubleConsumer
            3. Function<T, R>
            	T -> R
            		IntFunction<R>, IntToDoubleFunction, IntToLongFunction, LongFunction<R>,
            		LongToDoubleFunction, LongToIntFunction, DoubleFunction<R>, DoubleToIntFunction, DoubleToLongFunction, ToIntFunction<T>, ToDoubleFunction<T>, ToLongFunction<T>
      		4. Supplier<T>
      			() -> T
      				BooleanSupplier, IntSupplier, LongSupplier, DoubleSupplier
      		5. UnaryOperator<T>
      			T -> T
      				IntUnaryOperator, LongUnaryOperator, DoubleUnaryOperator
      		6. BinaryOperator<T>
      			(T, T) -> T
      				IntBinaryOperator, LongBinaryOperator, DoubleBinaryOperator
      		7. BiPredicate<T, U>
      			(T, U) -> boolean
      		8. BiConsumer<T, U>
      			(T, U) -> void
      				ObjIntConsumer<T>, ObjLongConsumer<T>, ObjDoubleConsumer<T>
      		9. BiFunction<T, U, R>
      			(T, U) -> R
      				ToIntBiFunction<T, U>, ToLongBiFunction<T, U>, ToDoubleBiFunction<T, U>

         Examples of lambdas with functional interfaces:
            1. A boolean expression
                Predicate<List<String>>
                    (List<String> list) -> list.isEmpty()
            2. Creating objects
                Supplier<Apple>
                    () -> new Apple(10)
            3. Consuming from an object
                Consumer<Apple>
                    (Apple a) -> System.out.println(a.getWeight())
            4. Select/extract from an object
                Function<String, Integer> or ToIntFunction<String>
                    (String s) -> s.length()
            5. Combine two values
                IntBinaryOperator
                    (int a, int b) -> a * b
            6. Compare two objects
                Comparator<Apple> or BiFunction<Apple, Apple, Integer> or ToIntBiFunction<Apple, Apple>
                    (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
        */
    }

    /**
     * Pattern: Execute-around
     */
    public class ExecuteAround {
    // Open a resource, do some processing on it, and then close the resource (for example, dealing with files or databases). The setup and cleanup phases are always similar and surround the important code doing the processing, for example:
        public String processFileLimited() throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                return br.readLine();
            }
        }
        //  This current code is limited. You can read only the first line of the file. What if you’d like to return the first two lines instead or even the word used most frequently? Ideally, you’d like to reuse the code doing setup and cleanup and tell the processFile method to perform different actions on the file. Does this sound familiar? Yes, you need to parameterize the behavior of processFile. You need a way to pass behavior to processFile so it can execute different behaviors using a BufferedReader. For example, here’s how to print two lines of a BufferedReader:
        // String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
        // Lambdas can be used only in the context of a functional interface. You need to create one that matches the signature BufferedReader -> String and that may throw an IOException.
        @FunctionalInterface
        public interface BufferedReaderProcessor {
            String process(BufferedReader b) throws IOException;
        }
        // Now use this interface as the argument to new processFile method:
        public static String processFileFunctional(BufferedReaderProcessor p) throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                return p.process(br);
            }
        }
        //	!!Any lambdas of the form BufferedReader -> String can be passed as arguments, because they match the signature of the process method defined in the BufferedReaderProcessor interface. Remember, lambda expressions let you provide the implementation of the abstract method of a functional interface directly inline, and they treat the whole expression as an instance of a functional interface. You can therefore call the method process on the resulting BufferedReaderProcessor object inside the processFile body to perform the processing:
        public void mockProcessFile() {
            try {
                String oneLine =
                        processFileFunctional((BufferedReader br) -> br.readLine());
                String twoLines =
                        processFileFunctional((BufferedReader br) -> br.readLine() + br.readLine());
            }
            catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    /**
     * Exceptions inside lambdas
     */
    public class LambdasWithExceptions {
    // None of the functional interfaces allow for a checked exception to be thrown. Where is two options to throw an exception from within the body of a lambda expression: define your own functional interface that declares the checked exception, or wrap the lambda body with a try/catch block:
        @FunctionalInterface
        public interface UserFunctionalInterfaces {
            String process(BufferedReader b) throws IOException;
        }
        UserFunctionalInterfaces userFunctionalInterfacesResult = (BufferedReader br) -> br.readLine();
        //	or
        Function<BufferedReader, String> wrappedLambdaBodyWithTryCatch =
            (BufferedReader b) -> {
                try {return b.readLine();}
                catch(IOException e) {throw new RuntimeException(e);}
            };
    }

    /**
     * Type checking, type inference, and restrictions
     */
    public class LambdaTypeChecking {
    //Lambda expression itself doesn’t contain the information about which functional interface it’s implementing.
    // Type checking.
    // !! The type of a lambda is deduced from the context in which the lambda is used. The type expected for the lambda expression inside the context (for example, a method parameter that it’s passed to or a local variable that it’s assigned to) is called the target type. One can get her target type from an assignment context, method-invocation context (parameters and return), and a cast context.
    // For example:
    // Callable<Integer> is the target type
        Callable<Integer> callableTargetType = () -> 42;
    // Step-by-step example:
        List<Apple> lambdaTypeCheckingExample = LambdasIntro.filterApples(
            inventory,
            (Apple apple) -> apple.getWeight() > 150
        );
    /*
    The type-checking process is deconstructed as follows:
     - First, what’s the context in which the lambda is used? Look up the declaration of the filterApples method.
     - Second, it expects, as the second formal parameter, an object of type Predicate<Apple> (the target type).
     - Third, Predicate<Apple> is a functional interface defining a single abstract method called test.
     - Fourth, the test method describes a function descriptor that accepts an Apple and returns a boolean.
     - Finally, any argument to the filter method needs to match this requirement.
       Note that if the lambda expression was throwing an exception, then the declared throws clause of the abstract method would also have to match.
       Type checking — why won’t the following code compile?
       How could you fix the problem?
       Target type of a lambda conversion must be an interface
        Object o = () -> { System.out.println("Tricky example"); };
       Answer:
        The context of the lambda expression is Object (the target type). But Object isn’t a functional interface. To fix this you can change the target type to Runnable, which represents a function descriptor "() -> void":
    */
        Runnable mockRunnableSolution = () -> { System.out.println("Tricky example"); };
    //	You could also fix the problem by casting the lambda expression to Runnable, which explicitly provides a target type.
        Object mockCastingRunnableSolution = (Runnable) () -> { System.out.println("Tricky example"); };
    // This technique can be useful in the context of overloading with a method taking two different functional interfaces that have the same function descriptor. You can cast the lambda in order to explicitly disambiguate which method signature should be selected.
    // For example, the call execute(() -> {}) using the method execute, as shown in the following, would be ambiguous, because both Runnable and Action have the same function descriptor:
        public void execute(Runnable runnable) {
            runnable.run();
        }
        public void execute(Action action) {
            action.act();
        }
        @FunctionalInterface
        interface Action {
            void act();
        }
    // But, you can explicitly disambiguate the call by using a cast expression:
        void mockDisambiguateCastingLambda() {
            // Ambiguous method call. Both execute (Runnable) in LambdaTypeChecking and execute (Action) in
            // LambdaTypeChecking match
            //execute(() -> {});
            // Casting resolves it
            execute((Action) () -> {});
        }
    //
    // Same lambda, different functional interfaces.
    // Because of the idea of target typing, the same lambda expression can be associated with different functional interfaces if they have a compatible abstract method signature. For example, both interfaces Callable and PrivilegedAction described earlier represent functions that accept nothing and return a generic type T.
        Callable<Integer> callable =
            () -> 42;
        PrivilegedAction<Integer> privilegedAction =
            () -> 42;
        Comparator<Apple> comparator =
            (Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight());
        ToIntBiFunction<Apple, Apple> toIntBiFunction =
            (Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight());
        BiFunction<Apple, Apple, Integer> biFunction =
            (Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight());

    // Special void-compatibility rule.
        // Predicate has a boolean return
        Predicate<String> svcr1 = (String s) ->
            Arrays.asList("1","2").add(s); // it's expression. Return nothing, i.e. void
        Predicate<String> svcr2 = (String s) ->
            {return Arrays.asList("1","2").add(s);}; // statement. Return boolean, but compiler ignores that
        // Consumer has a void return
        Consumer<String> svcr3 = (String s) ->
            Arrays.asList("1","2").add(s);
        //Consumer<String> svcr3 = (String s) ->
        //    {return Arrays.asList("1","2").add(s);}; // incorrect: unexpected return value
    // If a lambda has a statement expression as its body, it’s compatible with a function descriptor that returns void (provided the parameter list is compatible, too). For example, even though the method add of a List returns a boolean and not void as expected in the Consumer context (T -> void).
    }

    public class TypeInference {
    // The Java compiler deduces what functional interface to associate with a lambda expression from its surrounding context (the target type), meaning it can also deduce an appropriate signature for the lambda because the function descriptor is available through the target type. The benefit is that the compiler has access to the types of the parameters of a lambda expression, and they can be omitted in the lambda syntax. The Java compiler infers the types of the parameters of a lambda as shown here (note that when a lambda has a single parameter whose type is inferred, the parentheses surrounding the parameter name can also be omitted):
    // Here, readability is a rule of thumb
        List<Apple> listInferedTypes = LambdasIntro.filterApples(inventory,
            apple -> Color.GREEN.equals(apple.getColor()));
        Comparator<Apple> comparatorExplicitTypes =
            (Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight());
        Comparator<Apple> comparatorInferedTypes =
            (a1, a2) -> Integer.compare(a1.getWeight(), a2.getWeight());

    }

    public class UsingLocalVariables {
    // Lambda expressions are also allowed to use free variables (variables that aren’t the parameters and are defined in an outer scope) like anonymous classes can. They’re called capturing lambdas.
        int portNumber = 1337;
        Runnable mockRunnable = () -> System.out.println(portNumber);
    // There are some restrictions on what you can do with these variables. Lambdas are allowed to capture (to reference in their bodies) instance variables and static variables without restrictions. But when local variables are captured, they have to be explicitly declared final or be effectively final. Lambda expressions can capture local variables that are assigned to only once. (Note: capturing an instance variable can be seen as capturing the final local variable "this"). The following code doesn’t compile because the variable portNumber is assigned to twice:
        void mockUsingLocalVariables() {
            int portNumberAssignedTwice = 1337;
            // Variable used in lambda expression should be final or effectively final
            //Runnable mockRunnableAssignedTwice = () -> System.out.println(portNumberAssignedTwice);
            portNumberAssignedTwice = 31337;
        }
    // Closure. You may have heard of the term closure and may be wondering whether lambdas meet the definition of a closure (not to be confused with the Clojure programming language). To put it scientifically, a closure is an instance of a function that can reference nonlocal variables of that function with no restrictions. For example, a closure could be passed as argument to another function. It could also access and modify variables defined outside its scope. Now, Java 8 lambdas and anonymous classes do something similar to closures: they can be passed as argument to methods and can access variables outside their scope. But they have a restriction: they can’t modify the content of local variables of a method in which the lambda is defined.
    // Those variables have to be implicitly final. It helps to think that lambdas close over values rather than variables. As explained previously, this restriction exists because local variables live on the stack and are implicitly confined to the thread they’re in. Allowing capture of mutable local variables opens new threadunsafe possibilities, which are undesirable (instance variables are fine because they live on the heap, which is shared across threads).
    }

    /**
     * Method references
     */
    public class MethodReferences {
    // Method references can be considered as shorthand versions of certain lambdas.
    // Method references let you reuse existing method definitions and pass them like lambdas. In some cases they appear more readable and feel more natural than using lambda expressions. You can think of method references as syntactic sugar for lambdas that refer only to a single method because you write less to express the same thing.
        void mockMethodReferencesSimplifies() {
            inventory.sort((Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight()));
            inventory.sort(comparing(Apple::getWeight));
        }
        /*
        Another examples:
            () -> Thread.currentThread().dumpStack()
                Thread.currentThread()::dumpStack
            (str, i) -> str.substring(i)
                String::substring
            (String s) -> System.out.println(s)
                System.out::println
            (String s) -> this.isValidName(s)
                this::isValidName
            str.sort((s1, s2) -> s1.compareToIgnoreCase(s2));
                str.sort(String::compareToIgnoreCase);
                // Comparator describes a function descriptor with the signature (T, T) -> int.
        There are three main kinds of method references:
         1. A method reference to a static method.
          For example, the method parseInt of Integer, written:
            ToIntFunction<String> stringToInt = (String s) -> Integer.parseInt(s);
            ToIntFunction<String> stringToInt = Integer::parseInt;
         2. A method reference to an instance method of an arbitrary type.
          For example, the method length of a String, written:
            String::length
          Another one:
            BiPredicate<List<String>, String> contains = (list, element) -> list.contains(element);
                BiPredicate<List<String>, String> contains = List::contains;
          This is because the target type describes a function descriptor (List<String>, String) -> boolean, and List::contains can be unpacked to that function descriptor.
          The idea is that you’re referring to a method to an object that will be supplied as one of the parameters of the lambda. For example, the lambda expression:
            (String s) -> s.toUpperCase()
           can be rewritten as:
            String::toUpperCase.
         3. A method reference to an instance method of an existing object or expression.
          For example, suppose you have a local variable expensiveTransaction that holds an object of type Transaction, which supports an instance method getValue; you can write lambda expression:
            () -> expensiveTransaction.getValue()
           can be rewritten as:
            expensiveTransaction::getValue
          This third kind of method reference is particularly useful when you need to pass around a method defined as a private helper. For example, say you defined a helper method isValidName:
            private boolean isValidName(String string) {
                return Character.isUpperCase(string.charAt(0));
            }
          You can now pass this method around in the context of a Predicate<String> using a method reference:
            filter(words, this::isValidName)
          Another one:
            Predicate<String> startsWithNumber = (String string) -> this.startsWithNumber(string);
            Predicate<String> startsWithNumber = this::startsWithNumber;
            // startsWithNumber ia a private helper method

         Note that there are also special forms of method references for constructors, array constructors, and super-calls.
        */
    }

    /**
     * Constructor references
     */
    public class ConstructorReferences {
    // You can create a reference to an existing constructor using its name and the keyword new as follows: ClassName::new. It works similarly to a reference to a static method. For example, suppose there’s a zero-argument constructor. This fits the signature "() -> Apple of Supplier"; you can do the following:
        Supplier<Apple> noParametersWithLambda = () -> new Apple();
        Supplier<Apple> noParameters = Apple::new;
        Apple noParametersAppple = noParameters.get();
    // If you have a constructor with signature Apple(Integer weight), it fits the signature of the Function interface, so you can do this:
        Function<Integer, Apple> oneParametersWithLambda = (weight) -> new Apple(weight);
        Function<Integer, Apple> oneParameters = Apple::new;
        Apple oneParametersAppple = oneParameters.apply(110);
    // In the following code, each element of a List of Integers is passed to the constructor of Apple using a similar map method, defined earlier, resulting in a List of apples with various weights:
        List<Integer> listAppleWeights = Arrays.asList(7, 3, 4, 10);
        List<Apple> listApples = mapWeightsForApples(listAppleWeights, Apple::new);
        public List<Apple> mapWeightsForApples(List<Integer> list, Function<Integer, Apple> f) {
            List<Apple> result = new ArrayList<>();
            for(Integer i: list) {
                result.add(f.apply(i));
            }
            return result;
        }
    // For a two-argument constructor, Apple (Color color, Integer weight), it fits the signature of the BiFunction interface, so you can do this:
        BiFunction<Integer, Color, Apple> twoParametersWithLambda = (weight, color) -> new Apple(weight, color);
        BiFunction<Integer, Color, Apple> twoParameters = Apple::new;
        Apple twoParametersAppple = twoParameters.apply(110, Color.GREEN);
    // The capability of referring to a constructor without instantiating it enables interesting applications. For example, you can use a Map to associate constructors with a string value. You can then create a method giveMeFruit that, given a String and an Integer, can create different types of fruits with different weights, as follows:
        static Map<String, Function<Integer, Fruit>> fruitBuilder = new HashMap<>();
        static {
            fruitBuilder.put("apple", Apple::new);
            fruitBuilder.put("orange", Orange::new);
            // etc...
        }
        public static Fruit giveMeFruit(String fruit, Integer weight) {
            return fruitBuilder.get(fruit.toLowerCase()).apply(weight);
        }
        Fruit myOrange = giveMeFruit("orange", 5);
    }

    /**
     * Once again. Putting lambdas and method references into practice
     */
    public class LambdasPracticeALittle {
    	public class AppleComparator implements Comparator<Apple> {
    		public int compare(Apple a1, Apple a2){
    			return Integer.compare(a1.getWeight(), a2.getWeight());
    		}
    	}
        void mockLambdasPracticeALittle() {
            // Instantiate comparator object
            inventory.sort(new AppleComparator());
            // Or use anonymous class
            inventory.sort(new Comparator<Apple>() {
                public int compare(Apple a1, Apple a2) {
                    return Integer.compare(a1.getWeight(), a2.getWeight());
                }
            });
            // Or lambda expression
            inventory.sort((Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight()));
            // Even simplier
            inventory.sort((a1, a2) -> Integer.compare(a1.getWeight(), a2.getWeight()));
            // Even more
            inventory.sort(comparing(apple -> apple.getWeight()));
            // And finally
            inventory.sort(comparing(Apple::getWeight));
            // The code reads like the problem statement "sort inventory comparing the weight of the apples".

            // Comparator includes a static helper method called comparing that takes a Function extracting a Comparable key and produces a Comparator object. You now pass a lambda with only one argument; the lambda specifies how to extract the key for comparison from an Apple:
            Comparator<Apple> comparator = Comparator.comparing((Apple a) -> a.getWeight());
            inventory.sort(comparator);
        }
    }
    //
    /**
     * Useful methods to compose lambda expressions
     */
    public static class LambdasUsefulMethods {
    // Several functional interfaces provide methods that allow composition. It means you can combine several simple lambda expressions to build more complicated ones. For example, you can combine two predicates into a larger predicate that performs an OR operation between the two predicates. Moreover, you can also compose functions such that the result of one becomes the input of another function. Its possible due to default methods.
        public static void lambdasCompositionExamples() {
        // Comparator example:
            Comparator<Apple> comparator = Comparator.comparing(Apple::getWeight);
            inventory.sort(comparing(Apple::getWeight).reversed());
    // What if you find two apples that have the same weight? Which apple should have priority in the sorted list? You may want to provide a second Comparator to further refine the comparison. For example, after two apples are compared based on their weight, you may want to sort them by country of origin (or by Color).
    		inventory
    			.sort(comparing(Apple::getWeight)
    			.reversed()
    			.thenComparing(Apple::getColor));
    // Predicate example:
            Predicate<Apple> redAppleFilter = apple -> Color.RED.equals(apple.getColor());
    		Predicate<Apple> notRedAppleFilter = redAppleFilter.negate();
    		Predicate<Apple> redAndHeavyAppleFilter = notRedAppleFilter.and(apple -> apple.getWeight() > 150);
            LambdasIntro.Predicate<Apple> convertedToApplePredicate = redAndHeavyAppleFilter::test;
            List<Apple> redApples = LambdasIntro.filterApples(inventory, convertedToApplePredicate);
            System.out.println("redApples:" + redApples);
    // Note that the precedence of methods AND and OR in the chain is from left to right — there is no equivalent of bracketing. So a.or(b).and(c) must be read as (a || b) && c. Similarly, a.and(b).or(c) must be  read as (a && b) || c.
    		Predicate<Apple> redAndHeavyAppleOrGreen =
                redAppleFilter
    		        .and(apple -> apple.getWeight() > 150)
    		        .or(apple -> Color.GREEN.equals(apple.getColor()));
    // Function example:
    // In mathematics you’d write g(f(x)) or (g o f)(x)
            Function<Integer, Integer> f = x -> x + 1;
            Function<Integer, Integer> g = x -> x * 2;
            Function<Integer, Integer> h1 = f.andThen(g);
            int result = h1.apply(1);
            System.out.println(result);
    // In mathematics you’d write f(g(x)) or (f o g)(x)
            Function<Integer, Integer> h2 = f.compose(g);
            result = h2.apply(1);
            System.out.println(result);
    // Less abstract
    		Function<String, String> addHeader = LambdasMain.Letter::addHeader;
    		Function<String, String> transformationPipeline =
    			addHeader
    				.andThen(LambdasMain.Letter::checkSpelling)
    				.andThen(LambdasMain.Letter::addFooter);
            System.out.println(transformationPipeline.apply(" labda"));
        }
    }

    /**
     * Similar ideas from mathematics
     */
    public class LambdasAndMathematics {
    /*
    Suppose you have a (mathematical, not Java) function f, perhaps defined by f(x) = x + 10. Find the area beneath the function when drawn on paper. In this example, the function f is a straight line, and so you can easily work out this area by the trapezium method (drawing triangles and rectangles) to discover the solution:
        1/2 × ((3 + 10) + (7 + 10)) × (7 – 3) = 60
     In java you want something like this:
        integrate(f, 3, 7)
     but you can't do this:
    	integrate(x + 10, 3, 7)
     for two reasons.
     First, the scope of x is unclear, and second, this would pass a value of x+10 to integrate instead of passing the function f. Indeed, the secret role of dx in mathematics is to say "that function taking argument x whose result is x + 10".
     Java uses the notation (a lambda expression):
        (double x) -> x + 10
     for exactly this purpose, hence you can write:
    	integrate((double x) -> x + 10, 3, 7)
     or
        integrate((double x) -> f(x), 3, 7)
     or, using a method reference as mentioned earlier
    	integrate(C::f, 3, 7)
     if C is a class containing f as a static method. The idea is that you’re passing the code for f to the method integrate. You may now wonder how you’d write the method integrate itself. Continue to suppose that f is a linear function (straight line). You’d probably like to write in a form similar to mathematics:
    */
        public static double integrate(Function<Double, Double> f, double a, double b) {
            return (f.apply(a) + f.apply(b)) * (b - a) / 2.0;
        }
    // But because lambda expressions can be used only in a context expecting a functional interface (DoubleFunction is more efficient than using Function<Double,Double> as it avoids boxing the result), you have to write it the following way:
        public static double integrate(DoubleFunction<Double> f, double a, double b) {
            return (f.apply(a) + f.apply(b)) * (b - a) / 2.0;
        }
    // or using DoubleUnaryOperator, which also avoids boxing the result:
        public static double integrate(DoubleUnaryOperator f, double a, double b) {
            return (f.applyAsDouble(a) + f.applyAsDouble(b)) * (b - a) / 2.0;
        }
    // use it
        public static void lambdasAndMathematicsTest() {
            System.out.println(integrate((Double x) -> x + Double.parseDouble("10"), 3, 7));
            System.out.println(integrate((DoubleFunction)(double x) -> x + 10, 3, 7));
            System.out.println(integrate((double x) -> x + 10, 3, 7));
        }
    }

    /**
     * Tips and Best Practices
     */
    public class BestPractices {
    // 1. Prefer Standard Functional Interfaces
    // 2. Use the @FunctionalInterface Annotation
    // 3. Don't Overuse Default Methods in Functional Interfaces. Adding too many default methods to the interface is not a very good architectural decision. This should be considered a compromise, only to be used when required for upgrading existing interfaces without breaking backward compatibility.
        @FunctionalInterface
        public interface FooExtended extends Baz, Bar {
            // Overwise would be compile-time error: interface FooExtended inherits unrelated defaults for
            //  defaultCommon() from types Baz and Bar...
            @Override
            default String defaultCommon() {
                return Bar.super.defaultCommon();
            }
        }
        @FunctionalInterface
        public interface Baz {
            String method(String string);
            default String defaultBaz() {
                return null;
            }
            default String defaultCommon(){
                return null;
            }
        }
        @FunctionalInterface
        public interface Bar {
            String method(String string);
            default String defaultBar() {
                return null;
            }
            default String defaultCommon() {
                return null;
            }
        }
    // 4. Avoid Overloading Methods With Functional Interfaces as Parameters
        public interface Processor {
            String process(Callable<String> c) throws Exception;
            String process(Supplier<String> s);
        }

        public class ProcessorImpl implements Processor {
            @Override
            public String process(Callable<String> c) throws Exception {
                return "";
            }

            @Override
            public String process(Supplier<String> s) {
                return "";
            }
        }
    /*
        At first glance, this seems reasonable, but any attempt to execute either of the ProcessorImpl‘s methods:
            // Compile error: "Ambiguous method call. Both ..."
            //String result = ProcessorImpl.process(() -> "abc");
        To solve this problem, there are two options:
         The first option is to use methods with different names:
         - use methods with different names, for example: processWithCallable, processWithSupplier
         - perform casting manually, which is not preferred:
            //String result = ProcessorImpl.process((Supplier<String>) () -> "abc");
      5. Don’t Treat Lambda Expressions as Inner Classes. Their scope behavior is different:
        - Inner class a new scope. Inner class new local variables are hidden from the enclosing scope. Keyword this inside an inner class is a reference to its instance.
        - Lambda expressions. Variables are not hidden from the enclosing scope inside the lambda’s body. Keyword this is a reference to an enclosing instance.
    */
        @FunctionalInterface
        public interface Foo {
            String method(String string);
        }
        private String value = "Enclosing scope value";
        public String scopeExperiment() {
            Foo fooIC = new Foo() {
                String value = "Inner class value";
                @Override
                public String method(String string) {
                    return this.value;
                }
            };
            String resultIC = fooIC.method("");
            Foo fooLambda = parameter -> {
                String value = "Lambda value";
                return this.value;
            };
            String resultLambda = fooLambda.method("");
            return "Results: resultIC = " + resultIC + ", resultLambda = " + resultLambda;
        }
        // Results: resultIC = Inner class value, resultLambda = Enclosing scope value
        public void mockScopeExperiment() {
            System.out.println(scopeExperiment());
        }
    // 6. Keep Lambda Expressions Short and Self-explanatory.
    //    - If possible, use one line constructions instead of a large block of code. Remember, lambdas should be an expression, not a narrative. Despite its concise syntax, lambdas should specifically express the functionality they provide. Do not use this "one-line lambda" rule as dogma. It may not be valuable to extract a code into another method, if the code is two or three lines in lambda's definition:
            Foo foo1 = parameter -> buildString(parameter);
            private String buildString(String parameter) {
                String result = "Something " + parameter;
                //many lines of code
                return result;
            }
            // Instead of:
            Foo foo = parameter -> {
                String result = "Something " + parameter;
                //many lines of code
                return result;
            };
    //    - Avoid Specifying Parameter Types:
    //       (a, b) -> a.toLowerCase() + b.toLowerCase();
    //       instead of this:
    //       (String a, String b) -> a.toLowerCase() + b.toLowerCase();
    //    - Avoid Parentheses Around a Single Parameter:
    //       a -> a.toLowerCase();
    //       instead of this:
    //       (a) -> a.toLowerCase();
    //       but
    //       () -> run(); // because where are no parameters
    //    - Avoid Return Statement and Braces if lambda is one-liner:
    //       a -> a.toLowerCase();
    //       instead of this:
    //       a -> {return a.toLowerCase()};
    //    - Use Method References
            Foo foo2 = parameter -> buildString(parameter);
            Foo foo3 = this::buildString;
    }

    // Some preparations
    private static final String FILE = LambdasMain.FILE;
    private static List<Apple> inventory = LambdasMain.inventory;
}