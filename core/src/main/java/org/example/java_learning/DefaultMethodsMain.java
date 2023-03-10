package org.example.java_learning;

public class DefaultMethodsMain {
/*
Traditionally, a Java interface groups related methods together into a contract. Any (nonabstract) class that implements an interface must provide an implementation for each method defined by the interface or inherit the implementation from a superclass. But this requirement causes a problem when library designers need to update an interface to add a new method. Indeed, existing concrete classes (which may not be under the interface designers’ control) need to be modified to reflect the new interface contract. Java 8 introduced a new mechanism to tackle this problem. Since then interfaces can declare methods with implementation code in two ways. First, Java 8 allowed static methods inside interfaces. Second, Java 8 introduced a new feature called default methods that allows you to provide a default implementation for methods in an interface. In other words, interfaces can now provide concrete implementation for methods. As a result, existing classes implementing an interface automatically inherit the default implementations if they don’t provide one explicitly, which allows you to evolve interfaces nonintrusively.

Summary:
 1. Default methods provide a means of evolving interfaces without modifying existing implementations.
 2. Default methods can help structure your programs by providing a flexible mechanism for multiple inheritance of behavior; a class can inherit default methods from several interfaces.
 3. Adding a new method to an interface is binary compatible, which means that existing class file implementations still run without the implementation of the new method if no attempt is made to recompile them.


Here's some among others:
 In the List interface:
    default void sort(Comparator<? super E> c){
        Collections.sort(this, c);
    }
   Note that Comparator.naturalOrder is a static method in the Comparator interface returns a Comparator object to sort the elements in natural order:
 In the Collection interface:
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
   Note that the spliterator is also a default method of the Collection interface.
 In the Iterator interface to ignore the remove method:
    interface Iterator<T> {
        boolean hasNext();
        T next();
        default void remove() {
            throw new UnsupportedOperationException();
        }
    }


Static methods and interfaces.
 A common pattern in Java is to define both an interface and a utility companion class defining many static methods for working with instances of the interface. Collections is a companion class to deal with Collection objects, for example. Now that static methods can exist inside interfaces, such utility classes in your code can go away, and their static methods can be moved inside an interface. These companion classes remain in the Java API to preserve backward compatibility.

Different types of compatibilities: binary, source, and behavioral.
 There are three main kinds of compatibility when introducing a change to a Java program: binary, source, and behavioral compatibilities (see https://blogs.oracle.com/darcy/entry/kinds_of_compatibility).
  Binary compatibility means that existing binaries running without errors continue to link (which involves verification, preparation, and resolution) without error after introducing a change. Adding a method to an interface is binary compatible, for example, because if it’s not called, existing methods of the interface can still run without problems.
  In its simplest form, source compatibility means that an existing program will still compile after introducing a change. Adding a method to an interface isn’t source compatible; existing implementations won’t recompile because they need to implement the new method.
  Finally, behavioral compatibility means running a program after a change with the same input results in the same behavior. Adding a method to an interface is behavioral compatible because the method is never called in the program (or gets overridden by an implementation).

Abstract classes vs. interfaces.
 Both can contain abstract methods and methods with a body.
 First, a class can extend only from one abstract class, but a class can implement multiple interfaces.
 Second, an abstract class can enforce a common state through instance variables (fields). An interface can’t have instance variables.


Multiple inheritance of behavior.
    public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {}
 As a result, an ArrayList is a direct subtype of seven types: AbstractList, List, RandomAccess, Cloneable, Serializable , Iterable, and Collection.


Minimal interfaces with orthogonal functionalities.
 This technique is somewhat related to the template design pattern, in which a skeleton algorithm is defined in terms of other methods that need to be implemented.
 Suppose that you need to define several shapes with different characteristics for the game you’re creating. Some shapes should be resizable but not rotatable; some should be rotatable and movable but not resizable. You can start by defining a stand-alone Rotatable interface with two abstract methods: setRotationAngle and getRotationAngle. The interface also declares a default rotateBy method that you can implement by using the setRotationAngle and getRotationAngle methods as follows:
    public interface Rotatable {
        void setRotationAngle(int angleInDegrees);
        int getRotationAngle();
        default void rotateBy(int angleInDegrees){
            setRotationAngle((getRotationAngle () + angleInDegrees) % 360);
        }
    }
 Now any class that implements Rotatable will need to provide an implementation for setRotationAngle and getRotationAngle but will inherit the default implementation of rotateBy for free.


Composing interfaces.
    public class Monster implements Rotatable, Moveable, Resizable {}
    public class Sun implements Moveable, Rotatable {}


Resolution rules.
 What if a class implements two interfaces that have the same default method signature? Which method is the class allowed to use? You have three rules to follow when a class inherits a method with the same signature from multiple places (such as another class or interface):
  1. Classes always win. A method declaration in the class or a superclass takes priority over any default method declaration.
  2. Otherwise, subinterfaces win: the method with the same signature in the most specific default-providing interface is selected. (If B extends A, B is more specific than A.)
  3. Finally, if the choice is still ambiguous, the class inheriting from multiple interfaces has to explicitly select which default method implementation to use by overriding it and calling the desired method explicitly.
*/

    public static void defaultMethodsMain() {
        secondRule1();
        secondRule2();
        firstRule1();
        ambiguousExplicitResolution();
        diamondProblem();
    }

    // Let's examine some cases.
    public static interface A1 {
        default void hello() { System.out.println("Hello from A1"); }
    }
    public static interface B1 extends A1 {
        default void hello() { System.out.println("Hello from B1"); }
    }
    public static class C1 implements B1, A1 {}
    public static void secondRule1() {
        new C1().hello();
        // Prints "Hello from B1".
        // B1 is more specific than A1.
    }
    public static class D1 implements A1 {}
    public static class C2 extends D1 implements B1, A1 {}
    public static void secondRule2() {
        new C2().hello();
        // Prints "Hello from B1".
        // Rule 1 says that a method declaration in the class takes priority. But D1 doesn’t override hello; it implements interface A1. Consequently, it has a default method from interface A1. Rule 2 says that if there are no methods in the class or superclass, the method with the most specific default-providing interface is selected. The compiler, therefore, has a choice between the hello method from interface A1 and the hello method from interface B1. Because B1 is more specific, the program prints "Hello from B1" again.
    }
    public static class D2 implements A1 {
        public void hello() { System.out.println("Hello from D2"); }
    }
    public static class C3 extends D2 implements B1, A1 {}
    public static void firstRule1() {
        new C3().hello();
        // Prints "Hello from D2".
        // A method declaration from a superclass has priority, as stated by rule 1.
        // Note that if D2 were declared as follows,
        //public abstract class D2 implements A1 {
        //    public abstract void hello();
        //}
        // C3 would be forced to implement the method hello itself, even though default implementations exist elsewhere in the hierarchy.
    }

    // Conflicts and explicit disambiguation.
    public static interface A2 {
        public default void hello() { System.out.println("Hello from A2"); }
    }
    public static interface B2 {
        public default void hello() { System.out.println("Hello from B2"); }
    }
    // Compile-time error: "class C4 inherits unrelated defaults for hello() from types B2 and A2". Both hello methods from A2 and B2 could be valid options. Rule 2 doesn’t help you now because there’s no more-specific interface to select.
    //public static class C4 implements B2, A2 {}
    public static class C5 implements B2, A2 {
        @Override
        public void hello() { A2.super.hello(); }
    }
    public static void ambiguousExplicitResolution() {
        new C5().hello();
        // Prints "Hello from A2"
    }

    // Diamond problem.
    public static interface B3 extends A1 {}
    public static interface C6 extends A1 {}
    public static class D3 implements B3, C6 {}
    public static void diamondProblem () {
        new D3().hello();
        // Prints "Hello from A1".
        // Only A1 declares a default method. Because the interface is a superinterface of D3.
        // What happens if B3 also has a default hello method with the same signature? Rule 2 says that you select the most specific default-providing interface. Because B3 is more specific than A1, the default method declaration from B3 is selected. If both B3 and C6 declare a hello method with the same signature, you have a conflict and need to solve it explicitly.
    }
    public static interface C7 extends A1 {
        void hello();
    }
    // Since C7 is more specific compile-time error occurs: "Class 'D4' must either be declared abstract or implement abstract method 'hello()' in 'C7'". D4 needs to provide an explicit implementation for the hello method.
    //public static class D4 implements B3, C7 {}
}
