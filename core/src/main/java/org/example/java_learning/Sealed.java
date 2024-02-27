package org.example.java_learning;

/**
 * Sealed Classes
 */
public class Sealed {
/*
https://openjdk.org/jeps/360 Sealed Classes (Preview)
https://openjdk.org/jeps/397 Sealed Classes (Second Preview)
https://openjdk.org/jeps/409 Sealed Classes

Sealed classes and interfaces restrict which other classes or interfaces may extend or implement them.
In summary, it should be possible for a superclass to be widely accessible (since it represents an important abstraction for users) but not widely extensible (since its subclasses should be restricted to those known to the author). At the same time, the superclass should not unduly constrain its subclasses by, e.g., forcing them to be final or preventing them from defining their own state.

Goals {
    - Allow the author of a class or interface to control which code is responsible for implementing it.
    - Provide a more declarative way than access modifiers to restrict the use of a superclass.
    - Support future directions in pattern matching by providing a foundation for the exhaustive analysis of patterns.
}

Motivation {
 Java supports enum classes to model the situation where a given class has only a fixed number of instances. In the following code, an enum class lists a fixed set of planets. They are the only values of the class, therefore you can switch over them exhaustively — without having to write a default clause:
 */
    enum EPlanet { MERCURY, VENUS, EARTH }
    void enumTest1() {
        EPlanet p = EPlanet.MERCURY;
        switch (p) {
            case MERCURY: {}
            case VENUS: {}
            case EARTH: {}
        }
    }
 /*
 What if we want to model a fixed set of kinds of values. We can do this by using a class hierarchy not as a mechanism for code inheritance and reuse but, rather, as a way to list kinds of values.
 */
    interface Celestial {}
    final class CPlanet implements Celestial {}
    final class CStar   implements Celestial {}
    final class CComet  implements Celestial {}
 /*
 This hierarchy does not, however, reflect the important domain knowledge that there are only three kinds of celestial objects in our model. In these situations, restricting the set of subclasses or subinterfaces can streamline the modeling.
 Java provides limited tools in this area: either make a class final, so it has zero subclasses, or make the class or its constructor package-private, so it can only have subclasses in the same package.
 The package-private approach is useful when the goal is code reuse, such as having the subclasses of AbstractStringBuilder share its code for append. However, the approach is useless when the goal is modeling alternatives, since user code cannot access the key abstraction — the superclass — in order to switch over it. Allowing users to access the superclass without also allowing them to extend it cannot be specified without resorting to brittle tricks involving non-public constructors — which do not work for interfaces.
}

Description {
 */
    public interface Letter {}
    public abstract sealed class A implements Letter permits A1, A2, A3 {}
 /*
 A sealed class imposes three constraints on its permitted subclasses:
  1. The sealed class and its permitted subclasses must belong to the same module, and, if declared in an unnamed module, to the same package.
  The classes specified by permits must be located near the superclass: either in the same module (if the superclass is in a named module) or in the same package (if the superclass is in the unnamed module). For example, in the following declaration of Shape its permitted subclasses are all located in different packages of the same named module:
    package com.example.geometry;
    public abstract sealed class Shape permits
        com.example.polar.Circle,
        com.example.quad.Rectangle,
        com.example.quad.simple.Square { ... }
  When the permitted subclasses are small in size and number, it may be convenient to declare them in the same source file as the sealed class. When they are declared in this way, the sealed class may omit the permits clause and the Java compiler will infer the permitted subclasses from the declarations in the source file. (The subclasses may be auxiliary or nested classes.) For example, if the following code is found in Root.java then the sealed class Root is inferred to have three permitted subclasses:
 */
    abstract sealed class Root {
        final class A extends Root {}
        final class B extends Root {}
        final class C extends Root {}
    }
 /*
  2. Every permitted subclass must directly extend the sealed class.
  3. Every permitted subclass must use a modifier to describe how it propagates the sealing initiated by its superclass:
  3.1. A permitted subclass may be declared final to prevent its part of the class hierarchy from being extended further.
  3.2. A permitted subclass may be declared sealed to allow its part of the hierarchy to be extended further than envisaged by its sealed superclass, but in a restricted fashion.
  3.3. A permitted subclass may be declared non-sealed so that its part of the hierarchy reverts to being open for extension by unknown subclasses. A sealed class cannot prevent its permitted subclasses from doing this.
 */
    public non-sealed class A1 extends A {}
    public sealed class A2 extends A permits A21 {}
    public final class A3 extends A {}
    public final class A21 extends A2 {}
    public class A11 extends A1 {}
    public interface Word {}
    public sealed interface Sentence permits A111 {}
    public abstract non-sealed class A111 extends A11 implements Word, Sentence {}
    public abstract class A112 extends A11 {}
 /*
   Exactly one of the modifiers final, sealed, and non-sealed must be used by each permitted subclass. It is not possible for a class to be both sealed (implying subclasses) and final (implying no subclasses), or both non-sealed (implying subclasses) and final (implying no subclasses), or both sealed (implying restricted subclasses) and non-sealed (implying unrestricted subclasses).
   A class which is sealed or non-sealed may be abstract, and have abstract members. A sealed class may permit subclasses which are abstract, providing they are then sealed or non-sealed, rather than final.
 */
    public abstract sealed class Shape permits Circle, Rectangle, Square, WeirdShape {}
    public final class Circle extends Shape {}
    public sealed class Rectangle extends Shape permits TransparentRectangle, FilledRectangle {}
    public final class TransparentRectangle extends Rectangle {}
    public final class FilledRectangle extends Rectangle {}
    public final class Square extends Shape {}
    public non-sealed class WeirdShape extends Shape {}
 /*
  Even though the WeirdShape is open to extension by unknown classes, all instances of those subclasses are also instances of WeirdShape. Therefore code written to test whether an instance of Shape is either a Circle, a Rectangle, a Square, or a WeirdShape remains exhaustive.

 Classes specified by permits must have a canonical name, so anonymous classes and local classes cannot be permitted subtypes of a sealed class

 Class accessibility {
  Because extends and permits clauses make use of class names, a permitted subclass and its sealed superclass must be accessible to each other. However, permitted subclasses need not have the same accessibility as each other, or as the sealed class. In particular, a subclass may be less accessible than the sealed class. This means that, in a future release when pattern matching is supported by switches, some code will not be able to exhaustively switch over the subclasses unless a default clause (or other total pattern) is used. Java compilers will be encouraged to detect when switch is not as exhaustive as its original author imagined it would be, and customize the error message to recommend a default clause.
 }

 Sealed interfaces. Sealing and record classes (algebraic data types) {
  As for classes, an interface can be sealed by applying the sealed modifier to the interface. After any extends clause to specify superinterfaces, the implementing classes and subinterfaces are specified with a permits clause.
  */
    public sealed interface Expr permits ConstantExpr, PlusExpr, TimesExpr, NegExpr {}
    public final class ConstantExpr implements Expr {}
    public final class PlusExpr     implements Expr {}
    public final class TimesExpr    implements Expr {}
    public final class NegExpr      implements Expr {}
  // Record classes are implicitly final.
    public sealed interface RExpr permits RConstantExpr, RPlusExpr, RTimesExpr, RNegExpr {}
    public record RConstantExpr(int i)         implements RExpr {}
    public record RPlusExpr(RExpr a, RExpr b)  implements RExpr {}
    public record RTimesExpr(RExpr a, RExpr b) implements RExpr {}
    public record RNegExpr(RExpr e)            implements RExpr {}
  // The combination of sealed classes and record classes is sometimes referred to as algebraic data types: Record classes allow us to express product types, and sealed classes allow us to express sum types.
  /*
 }

 Sealed classes and conversions {
  */
    interface I {}
    sealed class C permits D {}
    final class D extends C {}
    void testSealedConversion (C c) {
        //if (c instanceof I) // Compile-time error!
        // Class C does not implement I, and is not final, so by the existing rules we might conclude that a conversion is possible. C is sealed, however, and there is one permitted direct subclass of C, namely D. By the definition of sealed types, D must be either final, sealed, or non-sealed. In this example, all the direct subclasses of C are final and do not implement I. This program should therefore be rejected, since there cannot be a subtype of C that implements I.
    }
  /*
  Supporting sealed classes leads to a change in the definition of narrowing reference conversion to navigate sealed hierarchies to determine at compile time which conversions are not possible. https://docs.oracle.com/javase/specs/jls/se15/html/jls-5.html#jls-5.1.6.1
 }

 Sealed classes and pattern matching {
  For more see here
  */
  PatternMatching.PatternMatchingForSwitch sealedPMSw = new PatternMatching.PatternMatchingForSwitch();
  PatternMatching.PatternMatchingForRecords sealedPMR = new PatternMatching.PatternMatchingForRecords();
    Shape rotate(Shape shape, double angle) {
        return switch (shape) {
            case Circle c     -> c; // could be //shape.rotate(angle);
            case Rectangle r  -> r;
            case Square s     -> s;
            case WeirdShape w -> w;
            // no default needed!
        };
    }
  /*
 }
}
*/
}
