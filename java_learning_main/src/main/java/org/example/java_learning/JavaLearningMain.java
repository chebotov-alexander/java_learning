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

        DataTypes.testLocalClasses();

        SwitchStatementsExpressions.testFallingThrough();

        System.out.println("Java learning END   ---------------------");
    }
}

class Terminology {
/*
Polymorphism {
 Java offers several mechanisms for polymorphism; parametric polymorphism (generics), where we can share code across a family of instantiations that vary only by type, and inclusion polymorphism (subtyping with inheritance hierarchies and virtual methods), where we can share an API across a family of instantiations that differ more broadly.
}

Statements {
 The sequence of execution of a program is controlled by statements, which are executed for their effect and do not have values.
 Some statements contain other statements as part of their structure; such other statements are substatements of the statement. We say that statement S immediately contains statement U if there is no statement T different from S and U such that S contains T and T contains U. In the same manner, some statements contain expressions as part of their structure.
}
Expression Statements  {
 An expression statement is executed by evaluating the expression; if the expression has a value, the value is discarded.
    System.out.println("Hello world");
 {
    ExpressionStatement:
        StatementExpression;
    StatementExpression:
        Assignment
        PreIncrementExpression
        PreDecrementExpression
        PostIncrementExpression
        PostDecrementExpression
        MethodInvocation
        ClassInstanceCreationExpression
 }
}
Block {
 A block is a sequence of statements, local variable declaration statements, and local class and interface declarations within braces.
}
Expressions {
}
Constant Expressions {
 https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.29
 A constant expression is an expression denoting a value of primitive type or a String that does not complete abruptly and is composed using only the following:
}
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

A pattern is TOTAL on a type if it matches all possible values of that type—including null (for reference types).

UnannType {
 UnannType is unannotated type.
 UnannType is used when a pre-Java 8 annotation could appear before the type, and annotations are parsed as separately modifiers.
 Java 7 grammar doesn't have UnannType, because type annotations were introduced in Java 8. The need for the UnannType comes from the way how annotations worked in pre-Java 8 versions. In the Java 7 grammar annotations are essentially modifiers. Which means this is legal Java:
    public class Test {
        public @NonNull volatile @AnotherAnnotation String test;
    }
 Notice how the annotations can be mixed freely with other modifiers, and an annotation may appear right before the type. Type annotations also appear before the type, so in Java 8 an annotation before a type can be "ambiguous":
    public class Test {
        @NonNull String test;
    }
 Is @NonNull a type annotation for String, or a field annotation for test? In practice it makes no difference, but in the grammar these are two separate cases. What about this:
    public class Test {
        @NonNull @AnotherAnnotation String test;
    }
 Both could be type annotations, field annotations, or we could have a mix of both. So, in the grammar they decided to force unannotated types (UnannType) in positions where an annotation may appear before the type in pre-Java 8 (at least fields, local variables, parameters). This means type annotations are parsed as "modifiers" from the grammar's point of view. Normal type productions (= can have annotations) are used in places where an annotation cannot appear before the type in Java 7. For example:
    return new ArrayList<@NonNull String>();

 UnannType includes all of:
  int
  char
  boolean
  Object
  MyClass
  T, K, V, (and other type aliases)
  But doesn't distinguish between @NonNull Object and Object.
}

*/
}

class Links {
    // Java Platform, Standard Edition Documentation https://docs.oracle.com/en/java/javase/index.html
    // Java Microbenchmark Harness (JMH)

    // async/await pattern https://en.wikipedia.org/wiki/Async/await
    // HotSpot Virtual Machine Garbage Collection Tuning Guide / Introduction to Garbage-First (G1) Garbage Collector https://docs.oracle.com/en/java/javase/18/gctuning/garbage-first-g1-garbage-collector1.html#GUID-0394E76A-1A8F-425E-A0D0-B48A3DC82B42

    // https://nipafx.dev/

}