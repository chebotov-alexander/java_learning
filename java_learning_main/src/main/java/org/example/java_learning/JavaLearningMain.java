package org.example.java_learning;

/**
 * Java learning
 */
public class JavaLearningMain {
    public static void main( String[] args)     {
        System.out.println("Java learning START ---------------------");

        // Part 1
        GenericsMain.main(new String[]{""});
        // Part 2
        LambdasMain.main(new String[]{""});
        // Part 3
        StreamsMain.main(new String[]{""});

        System.out.println("Java learning END   ---------------------");
    }
}

class Terminology {
    /*
    Collections that have all elements of the same type are called homogeneous, while the collections that can have elements of potentially different types are called heterogeneous (or sometimes  "mystery meat collections").

    Values which can be passed around during program execution are first-class Java citizens, but various other Java concepts, such as methods and classes, exemplify second-class citizens.

    Filtering a list on two CPUs could be done by asking one CPU to process the first half of a list and the second CPU to process the other half of the list. This is called the forking step.

    External interation - explicitly loop through.
    Internal interation - loop with Stream API.

    Predicate is a function that returns a boolean.

    Behavior parameterization means: the ability to tell a method to take multiple behaviors (or strategies) as parameters and use them inter nally to accomplish different behaviors - Strategy pattern.

    Anonymous classes are like the local classes (a class defined in a block) that you’re already familiar with in Java. But anonymous classes don’t have a name. They allow you to declare and instantiate a class at the same time. In short, they allow you to create ad hoc implementations.
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Whoooo a click!!");
            }
        });

    Every Java type is either a reference type (for example, Byte, Integer, Object, List) or a primitive type (for example, int, double, byte, char). But generic parameters (for example, the T in Consumer<T>) can be bound only to reference types. This is due to how generics are internally implemented. As a result, in Java there’s a mechanism to convert a primitive type into a corresponding reference type. This mechanism is called boxing.
     The opposite approach (converting a reference type into a corresponding primitive type) is called unboxing.
     Java also has an autoboxing mechanism to facilitate the task for programmers: boxing and unboxing operations are done automatically.

    The type of a lambda is deduced from the context in which the lambda is used. The type expected for the lambda expression inside the context (for example, a method parameter that it’s passed to or a local variable that it’s assigned to) is called the target type.

    Tagging interfaces is interfaces without methods.


    */
}

class Links {
    // Java Microbenchmark Harness (JMH)

    // async/await pattern https://en.wikipedia.org/wiki/Async/await
    // HotSpot Virtual Machine Garbage Collection Tuning Guide / Introduction to Garbage-First (G1) Garbage Collector https://docs.oracle.com/en/java/javase/18/gctuning/garbage-first-g1-garbage-collector1.html#GUID-0394E76A-1A8F-425E-A0D0-B48A3DC82B42
}