package org.example.java_learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generics Introduction
 */
public class GenericsIntro {
    // !!!
    // it's a generic method
    <T> void genericMethodPrintCollection(Collection<T> c) {}
    // or
    // it's not a generic method and must be declared in a generic class with a type parameter T like this
    class SomeClass<T> {
        void nonGenericMethodPrintCollection(Collection<T> c) {}
    }

    // !!Generics were added to the Java language to enforce type safety at compile time.

    // !Java generics are one of the most complex parts of the language specification with a lot of potential corner cases, which not every developer needs to fully understand. A major design goal was to be backwards compatible with earlier releases. As a result, Java generics have some uncomfortable limitations.

    // The history under generics. Before them one has to cast an object stored in some collection before using it as an instance of a concrete class. A Collection for example, List doesn’t know what type of objects it contains. So, it’s actually possible to put different types of objects into the same collection and everything will work fine until an illegal cast is used, and the program crashes.
    public void executeGenericHistory() {
        // raw type
        List shapes = new ArrayList();
        // Create a List to hold shapes
        // Create some centered shapes, and store them in the list
        shapes.add(0, new CenteredCircle(1.0, 1.0, 1.0));
        // This is legal Java-but is a very bad design choice
        shapes.add(1, new CenteredSquare(2.5, 2, 3));
        // List::get() returns Object, so to get back a CenteredCircle we must cast
        CenteredCircle c1 = (CenteredCircle) shapes.get(0);
        // Next line will cause a runtime failure with ClassCastException
        CenteredCircle c2 = (CenteredCircle) shapes.get(1);
    }

    // So generics comes. The resulting types, which combine an enclosing container type and a payload type, are usually called generic types — and they are declared like this:
    interface GenericInterface<T> {
        void set(T t);
        T get();
    }
    // This indicates that the interface is a general construct, which can hold any type of payload. It isn’t really a complete interface by itself — it’s more like a general description of a whole family of interfaces, one for each type that can be used in place of T.

    // The syntax <T> has a special name — it’s called a type parameter, and another name for a generic type is a parameterized type. This should convey the sense that the container type (e.g., List) is parameterized by another type (the payload type). When we write a type like Map<String, Integer>, we are assigning concrete values to the type parameters.
    // !!Type parameters always stand in for reference types. It is not possible to use a primitive type as a value for a type parameter.

    // A generic class is a class with one or more type variables.
     class Pair<T> {
         private T first;
         private T second;
         public Pair() { first = null; second = null; }
         public Pair(T first, T second) { this.first = first; this.second = second; }
         public T getFirst() { return first; }
         public T getSecond() { return second; }
         public void setFirst(T newValue) { first = newValue; }
         public void setSecond(T newValue) { second = newValue; }
     }
    //	A generic class can have more than one type variable.
    class PairDouble<T, U> {private T first; private U second;}
    //	Instantiate the generic type by substituting types for the type variables
    Pair<String> pairSingle = new Pair<>();
    Pair<String> pairDouble = new Pair<>("", "");
    //	and methods
    String pairSingleFirst = pairSingle.getFirst();
    String pairSingleSecond = pairSingle.getSecond();
    //	void pairDouble.setFirst("");
    //	void pairDouble.setSecond("");
    //	In other words, the generic class acts as a factory for ordinary classes.

    //	It is possible to define a method(s) with type parameters both inside ordinary classes and inside generic	classes.
    class ArrayAlg {
        public static <T> T getMiddle(T... a) {
            return a[a.length / 2];
        }
    }
    //	and use it like this
    String middleStringExplicit = ArrayAlg.<String>getMiddle("John", "Q.", "Public");
    //	In most cases, you can omit the <String> type parameter from the method call. The compiler has enough information to infer the method that you want.
    String middleStringInfered = ArrayAlg.getMiddle("John", "Q.", "Public");
    //	But sometimes it can't
    //	// Error
    //	double middleDouble = ArrayAlg.getMiddle(3.14, 1729, 0);
    // The error message complains, in cryptic terms that vary from one compiler version to another, that there are two ways of interpreting this code, both equally valid. In a nutshell, the compiler autoboxed the parameters into a Double and two Integer objects, and then it tried to find a common supertype of these classes. It actually found two: Number and the Comparable interface, which is itself a generic type. In this case, the remedy is to write all parameters as double values.
}
