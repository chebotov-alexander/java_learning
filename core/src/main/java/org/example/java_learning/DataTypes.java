package org.example.java_learning;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Gatherer;

import static java.util.stream.Collectors.toList;
import static org.example.java_learning.Util.println;

public class DataTypes {
/*
Summary:
 1. Floating-point arithmetic never throws exceptions, even when performing illegal operations, like dividing zero by zero or taking the square root of a negative number.
 2. Record classes play the role of immutable Data-carriers (aggregates like tuples).
 3. Value classes (Project Valhalla) partly solves problems (within Generics, Lambdas, Streams, etc) inducing by boxing and unboxing primitive data types, and potentially infinitely expands them by absence of identity (immutable value as new identity).
 4. The combination of sealed classes and record classes is sometimes referred to as algebraic data types: Record classes allow us to express product types, and sealed classes allow us to express sum types. Both records and sealed types have a synergy with pattern matching; records admit easy decomposition into their components, and sealed types provide the compiler with exhaustiveness information so that a switch that covers all the subtypes need not provide a default clause.


https://docs.oracle.com/javase/tutorial/java/data/index.html
https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html
https://javarush.com/groups/posts/2274-kak-ispoljhzovatjh-bigdecimal-v-java
https://openjdk.org/projects/valhalla/ Project Valhalla
https://openjdk.org/projects/amber/design-notes/records-and-sealed-classes Project Amber


The JVM type system includes eight primitive types (int, long, etc.), objects (heterogeneous aggregates with identity), and arrays (homogeneous aggregates with identity). This set of building blocks is flexible—you can model any data structure you need to. Data that does not fit neatly into the available primitive types (such as complex numbers, 3D points, tuples, decimal values, strings, etc.), can be easily modeled with objects.
Primitives represent pure values; any int value of "3" is equivalent to (and indistinguishable from) any other int value of "3". Values have no canonical location, and so are freely copyable. With the exception of the unusual treatment of NaN values for float and double, the == operator performs a substitutability test—it asks "are these two values the same value?"
Each object has a unique object identity. Because of identity, objects are not freely copyable; each object lives in exactly one place (at any given time), and to access its state we have to go to that place. But we mostly don’t notice this because objects are not manipulated or accessed directly, but instead through object references. Object references are also a kind of value—they encode the identity of the object to which they refer, and the == operator on object references asks "do these two references refer to the same object?" Accordingly, object references (like other values) can be freely copied, but the objects they refer to cannot.
The design of primitives represents various tradeoffs aimed at maximizing performance and usability of the primitive types. Reference types default to null, meaning "referring to no object"; primitives default to a usable zero value (which for most primitives is the additive identity). Reference types provide initialization safety guarantees against a certain category of data races; primitives allow tearing under race for larger-than-32-bit values. We could characterize the design principles behind these tradeoffs as "make objects safer, make primitives faster".

Inheritance {
 Can be used to achieve some very diverse goals:
  - for reusing some code written before;
  - for subtyping and dynamic dispatch;
  - for separating specification from implementation;
  - for specifying a contract between different parts of a system;
  - ...
 Two main capabilities that inheritance provides are the ability to inherit code and the ability to inherit a type.

 Design complexity.
 For example. If Student and Faculty is a subclass of Person, then objects of class Student have the type Student, but they also have the type Person. A student is a person. Both the code and the type are inherited. PhDStudent is a subclass of both Faculty (they are instructors for a class, have a room number, a payroll number, and so on) and Student (they are enrolled in a course, have a student ID number, and so on). But more importantly all of this three are subtypes of Person. In this case the inheritance graph forms a diamond shape. This way, a PhD student will have the attributes of both students and faculty. Conceptually this is straightforward. In practice, however, the language becomes more complicated if it allows multiple inheritance, because that introduces new problems: What if both superclasses have fields with the same name? What if they have methods with the same signature but different implementations? For these cases, the language must have constructs that specify some solution to the problem of ambiguity and name overloading.
 Now, consider this question: If there is a field in the top-level superclass (Person, in this case), should the class at the bottom (PhDStudent) have one copy of this field or two? It inherits this field twice, after all, once via each of its inheritance branches. The answer is: it depends. If the field in question is, say, an ID number, maybe a PhD student should have two: a student ID and a faculty/payroll ID that might be a different number. If the field is, however, the person’s family name, then you want only one (the PhD student has only one family name, even though it is inherited from both superclasses).
 Multiple code inheritance is messy, but multiple type inheritance causes no problems. This fact is coupled with another observation: multiple code inheritance is not terribly important, because you can use delegation (using a reference to another object) instead, but multiple subtyping is often very useful and not easily replaced in an elegant way.
 So Java designers arrived at a pragmatic solution: allow only single inheritance for code, but allow multiple inheritance for types.

 Prior to Java 8 there were two types of inheritance in Java:
  - inherit both the type and the code by inheriting a class using {extends} keyword - only single inheritance;
  - inherit a type only using {implements} keyword - Interfaces - permits multiple inheritance.
 Multiple inheritance allows single object being viewed from different perspectives, focusing on different aspects to group them with other similar objects or to treat them according to a certain subset of their possible behaviors.
 Since Java 8 Interfaces can have Default methods and Static methods which are implementation code.
 Since multiple code inheritance can cause problems Java designers devised the following sensible and practical rules to deal with these problems:
  - inheriting multiple abstract methods with the same name is not a problem — they are viewed as the same method (there no implementation code in there);
  - Diamond inheritance of fields — one of the difficult problems — is avoided, because interfaces are not allowed to contain fields that are not constants;
  - inheriting static methods and constants (which are also static by definition) is not a problem, because they are prefixed by the interface name when they are used, so their names do not clash;
  - inheriting from different Interfaces multiple default methods with the same signature and different implementations is a problem - Java forbids to do it - compiler just reports an error.

  Java has three ways of inheritance:
   - Interfaces up to Java 8 version must not contain any implementation code. Since Java 8 Interfaces may or may (default methods and static methods to add and evolve language features while maintain backward compatibility) not contain some implementation code and still can't be instantiated into concrete object.
   - Abstract Classes always have methods that not contain implementation code and some methods that have. These classes can't be instantiated into concrete object.
   - Classes must not contain not implemented code and can be instantiated into concrete object.
}

Interfaces {
 Interfaces are used for defining contracts between different parts of the program, defining types for dynamic dispatch, separating the definition of a type from its implementation, and allowing for multiple inheritance in Java.
 Interfaces almost always specify a Java type (the type name and the signatures of its methods) without specifying any implementation:
  - No fields.
  - No method bodies (prior Java 8).
  - Can have Default methods and static methods (since Java 8)
  - Can have constants.
  - The modifiers (public static final) for constants and public for methods are implicitly assumed.

 Empty Interfaces.
 Sometimes you come across interfaces that are empty—they define only the interface name and no methods. Serializable, mentioned previously, is such an interface. Cloneable is another. These interfaces are known as marker interfaces. They mark certain classes as possessing a specific property, and their purpose is more closely related to providing metadata than to implementing a type or defining a contract between parts of a program. Java, since version 5, has had annotations, which are a better way of providing metadata. There is little reason today to use marker interfaces in Java. If you are tempted, look instead at using annotations.
}

Classes {
 Local Class and Interface Declarations {
  Local class and interface declarations may be intermixed freely with statements (including local variable declaration statements) in the containing block.
  It is a compile-time error if a local class or interface declaration has any of the access modifiers public, protected, or private.
  It is a compile-time error if a local class or interface declaration has the modifier static, sealed, or non-sealed.
  It is a compile-time error if the direct superclass or a direct superinterface of a local class is sealed.
  It is a compile-time error if a direct superinterface of a local interface is sealed.
  A local class may be a normal class, an enum class, or a record class. Every local normal class is an inner class. Every local enum class and local record class is implicitly static, and therefore not an inner class.
  A local interface may be a normal interface, but not an annotation interface. Every local interface is implicitly static.
  Like an anonymous class, a local class or interface is not a member of any package, class, or interface. Unlike an anonymous class, a local class or interface has a simple name.
  */
    static class Cyclic { Cyclic() { println("Cyclic_1");} }
    static void testLocalClasses() {
        println("DataTypes.testLocalClasses");
        new Cyclic(); // create a DataTypes.Cyclic.
        //class Cyclic extends Cyclic {} // error: "Cyclic inheritance involving 'Cyclic'".
        // The fact that the scope of a local class declaration encompasses its whole declaration (not only its body) means that the definition of the local class Cyclic is indeed cyclic because it extends itself rather than Global.Cyclic. Consequently, the declaration of the local class Cyclic is rejected at compile time.
        class Cyclic extends DataTypes.Cyclic { Cyclic() { println("Cyclic_2");} }
        new Cyclic(); // create a Cyclic extending a DataTypes.Cyclic.
        { // block
            class Local { Local() { println("Local_1");} }
            {
                //class Local {} // error: "Duplicate class: 'Local'".
                //class Cyclic extends Cyclic {} // error, same as before.
            }
            //class Local {} // error: "Duplicate class: 'Local'".
            // Since local class names cannot be redeclared within the same method (or constructor or initializer, as the case may be), the declarations of Local result in compile-time errors. However, Local can be redeclared in the context of another, more deeply nested, class such as AnotherLocal.
            class AnotherLocal {
                AnotherLocal() {
                    new Local();
                    class Local { Local() { println("Local_2");} } // ok.
                    new Local();
                }
            }
            new AnotherLocal();
            new Local();
        }
        class Local { Local() { println("Local_3");} } // ok, not in scope of any prior Local.
        new Local();
        // Prints:
        //Cyclic_1
        //Cyclic_1
        //Cyclic_2
        //Local_1
        //Local_2
        //Local_1
        //Local_3
    }
  /*
 }


 The record class and value class features are similar, in that both are useful for working with immutable data. However, record classes are used to opt out of separate internal state, while value classes are used to opt out of identity. Each of these choices can be made orthogonally; sometimes, an identity record is the right combination of choices. In other words records embody a tradeoff: give up on decoupling the API from the representation, and in return get various syntactic and semantic benefits. Value classes embody another: give up identity, and get various semantic and performance benefits. If we are willing to give up both, we can get both sets of benefits:
    value record NameAndScore(String name, int score) { }
 Value records combine the data-carrier idiom of records with the improved scalarization and flattening benefits of value classes.
 As for other classes, record classes are identity classes by default.

 Records and value types have some obvious similarities; they are both immutable aggregates, with restrictions on extension. When we look at their semantic goals, we can see that they differ. Value types are primarily about enabling flat and dense layout of objects in memory. In exchange for giving up object identity (which in turn entails giving up mutability and layout polymorphism), the runtime gains the ability to optimize the heap layout and calling conventions for values. With records, in exchange for giving up the ability to decouple a classes API from its representation, we gain a number of notational and semantic benefits. But while some of what we give up is the same (mutability, extension), some values may still benefit from state encapsulation, and some records may still benefit from identity, so they are not the exact same trade. However, there are classes that may be willing to tolerate both restrictions, in order to gain both sets of benefits — we might call these value records. So while we wouldn’t necessarily want to only have one mechanism or the other, we certainly want the mechanisms to work together.

 Why would we introduce two new forms of declaration, value classes and primitive classes? Couldn’t one or the other be good enough?
 While we could of course get away with only one of these (we’ve been getting away with neither for 25 years), whichever one we picked would be unsatisfying for some of the desired use cases. If we picked value classes only, new numeric types would be burdened by the requirement to represent nulls (which has a footprint cost) and to manage atomicity of loads and stores. If we picked primitives only, it would be very tempting to use primitives even when they are not entirely appropriate, and users would be stuck with an inconvenient default value or with objects that cannot protect their invariants when accidentally shared under a data race. There’s a reason for the remaining rows in our primitives-vs.-objects table; sometimes you want nulls, and sometimes not; sometimes you can tolerate tearing to get maximum performance, and sometimes not.
 How would we choose between declaring an identity class, value class, or primitive? Here are some quick rules of thumb:
  - Use identity classes when we need mutability, layout extension, or locking.
  - Consider value classes when we don’t need identity, but need nullity or have cross-field invariants.
  - Consider primitives when we don’t need identity, nullity, or cross-field invariants, and can tolerate the zero default and tearability that comes with primitives.
  - Remember that the P.ref reference type for a primitive recovers the benefits of a value class.
 Regarding performance we can observe some complementary rules of thumb:
  - Identity objects usually live in the heap, except on a very good day with JIT inlining and escape analysis.
  - Value objects should tend to stay above the heap as arguments and returns, but buffer in the heap when their references are stored there.
  - Bare primitive values should appear in the heap less as separately buffered objects and more as flattened values in their containers.

 Deconstruction patterns.
 A deconstruction pattern is like a constructor in reverse; it matches instances of the specified type, and then extracts the state components.
 Java’s object model is built around the assumption that we want the representation of an object to be completely decoupled from its API; the APIs and behavior of constructors, accessor methods, and Object methods need not align directly with the object’s state, or even with each other. However, in practice, they are frequently much more tightly coupled; a Point object has fields x and y, a constructor that takes x and y and initializes those fields, accessors for x and y, and Object methods that characterize points solely by their x and y values. We claim that for a class to be “just a plain carrier for its data”, this coupling is something that can be counted upon — that we’re giving up the ability to decouple its (publicly declared) state from its API. The API for a data class models the state, the whole state, and nothing but the state. One consequence of this is that data classes are transparent; they give up their data freely to all requestors. Otherwise, their API doesn’t model their whole state. Being able to count on this coupling drives a number of advantages. We can derive sensible and correct implementations for standard class members. Clients can freely deconstruct and reconstruct aggregates, or restructure them into a more convenient form, without fear that they will discard hidden data or undermine hidden assumptions. Frameworks can safely and mechanically serialize or marshal them, without the need to provide complex mapping mechanisms. By giving up the flexibility to decouple a classes state from its API, we gain all of these benefits.
 One of most common example is algebraic data type implemented in Java with Sealed and Records classes.
 Deconstruction patterns are deceptively powerful. We can go further: we can nest other patterns inside a deconstruction pattern as well, either to further constrain what is matched or further destructure the result, as we’ll see below.

 Some typical examples for records and sealed hierarchies of records:
  - Tree nodes. The Expr (see "algebraic data types") example shows how records can make short work of tree nodes, such as those representing documents, queries, or expressions, and sealing enables developers and compilers to reason about when all the cases have been covered. Pattern matching over tree nodes offers a more direct and flexible alternative to traversal than the Visitor pattern.
  - Multiple return values. It is often desirable for a method to return more than one thing, whether for reasons of efficiency (extracting multiple quantities in a single pass may be more efficient than making two passes) or consistency (if operating on a mutable data structure, a second pass may be operating on different state).
    record MinMax(int min, int max);
    public MinMax minmax(int[] elements) {}
  - Data transfer objects. A data transfer object is an aggregate whose sole purpose is to package up related values so they can be communicated to another activity in a single operation. Data transfer objects typically have no behavior other than storage, retrieval, and marshaling of state.
  - Joins in stream operations. Suppose we have a derived quantity, and want to perform stream operations (filtering, mapping, sorting) that operate on the derived quantity. For example, suppose we want to select the Person objects whose name (normalized to uppercase) has the largest hashCode(). We can use a record to temporarily attach the derived quantity (or quantities), operate on them, and then project back to the desired result, as in:
  */
    List<Person> topThreePeople(List<Person> list) {
        // local records are OK too!
        record PersonX(Person person, int hash) {
            PersonX(Person person) {
                // Start by adjoining the Person with the derived quantity.
                this(person, person.name().toUpperCase().hashCode());
            }
        }
        return list.stream()
           .map(PersonX::new)
           .sorted(Comparator.comparingInt(PersonX::hash))
           .limit(3)
           // Throw away the wrapper and extract the Person.
           .map(PersonX::person)
           .collect(toList());
    // This could be done without materializing an extra object, but then this would potentially have to compute the hash (and the uppercase string) many more times for each element.
    }
  /*
  - Compound map keys. Sometimes we want to have a Map that is keyed on the conjunction of two distinct domain values. For example, suppose we want to represent the last time a given person was seen in a given place: we can easily model this with a HashMap whose key combines Person and Place, and whose value is a LocalDateTime. But if your system has no PersonAndPlace type, you have to write one with boilerplate implementations of construction, equals, hashCode, etc. Because the record will automatically acquire the desired constructor, equals(), and hashCode() methods, it is ready to be used as a compound map key:
    record PersonPlace(Person person, Place place) { }
    Map<PersonPlace, LocalDateTime> lastSeen = ...
    ...
    LocalDateTime date = lastSeen.get(new PersonPlace(person, place));
    ...
  - Messages. Records and sums of records are commonly useful for representing messages in actor-based systems and other message-oriented systems (e.g., Kafka.) The messages exchanged by actors are ideally described by products; if an actor responds to a set of messages, this is ideally described by a sum of products. And being able to define an entire set of messages under a sum type enables more effective type checking for messaging APIs.
  - Value wrappers. The Optional class is an algebraic data type in disguise; in languages with algebraic data types and pattern matching, Optional<T> is typically defined as a sum of a Some(T value) and a None type (a degenerate product, one with no components). Similarly, an Either<T,U> type can be described as a sum of a Left<T> and a Right<U> type. (At the time Optional was added to Java, we had neither algebraic data types nor pattern matching, so it made sense to expose it using more traditional API idioms).

 Enums {
  Anytime you have a set of known constant values, an enum is a type-safe representation that prevents common problems.
  Enumerations (or "enums" for short) are a restricted Java data type that lets a developer define a variable to be a set of predefined constants. The variable can be a member of only that set of constants.
  Enum declarations are full classes, and the values listed are constant names referring to separate instances of these classes. The enum declaration can contain fields, constructors, and methods, just like other classes:
   - Enum declarations are classes, and enum values refer to objects.
   - For every declared enum value, an instance of the class is created and assigned to that value.
   - No other instances of this class can be created later (implicitly the constructor is always private) - enums provide no constructor (to the outside) and instead provide a set of ready-made instances.
   - Every different enum value will refer to a different object, and the same value will always refer to the same object; this cannot be changed.
   - Enums create their own namespace, so different enum classes may use the same value, but these are kept separate. For example, an enum class BoardGames, the enum values BoardGames.GO and CommandWords.GO are separate and do not interfere with each other.
   - The fact that enum values are objects, not ints, is important. It means that enums provide not only identity but also state and behavior.
   - Enum instance creation is, by default, thread-safe.
 }
}

Java primitive data types {
  ______________________________________________________________________________
 | Type  |    Contains      |Default| Size  |              Range                |
 |------------------------------------------------------------------------------|
 |boolean|true or false     |false  |1 bit  |NA                                 |
 |------------------------------------------------------------------------------|
 |char   |Unicode character |\u0000 |16 bits|\u0000 (or 0) to \uffff (or 65,535)|
 |------------------------------------------------------------------------------|
 |byte   |Signed integer    |0      |8 bits |–128 to 127                        |
 | Useful in place of int where their limits help to clarify code; the fact that|
 |  a variable's range is limited can serve as a form of documentation.         |
 |------------------------------------------------------------------------------|
 |short  |Signed integer    |0      |16 bits|–32768 to 32767                    |
 | As with byte, the same guidelines apply: you can use a short to save memory  |
 |  in large arrays, in situations where the memory savings actually matters.   |
 |------------------------------------------------------------------------------|
 |int    |Signed integer    |0      |32 bits|-2,147,483,648 to 2,147,483,647    |
 |In Java SE 8 and later, the int data type can be used (through Integer class) |
 | to represent an unsigned 32-bit integer, which has a minimum value of 0 and a|
 | maximum value of (23^2)-1. Static methods like compareUnsigned,              |
 | divideUnsigned etc have been added to the Integer class to support the       |
 | arithmetic operations for unsigned integers.                                 |
 |------------------------------------------------------------------------------|
 |long   |Signed integer    |0      |64 bits|-9,223,372,036,854,775,808 to      |
 |       |                  |       |       | 9,223,372,036,854,775,807         |
 |The Long class contains methods like compareUnsigned, divideUnsigned etc to   |
 | support arithmetic operations for unsigned long.                             |
 |------------------------------------------------------------------------------|
 |float  |IEEE 754 floating |0.0|32 bits|1.4E–45 to 3.4E+38 Stores fractional   |
 |       | point            |   |       | numbers. Sufficient for storing 6 to 7|
 |       |                  |   |       | decimal digits.                       |
 | Never use for precise values, such as currency, use java.math.BigDecimal     |
 |  class instead. As with the recommendations for byte and short, use a float  |
 |  (instead of double) if you need to save memory in large arrays of floating  |
 |  point numbers.                                                              |
 |------------------------------------------------------------------------------|
 |double |IEEE 754 floating |0.0|64 bits|4.9E–324 to 1.8E+308 Stores fractional |
 |       | point            |   |       | numbers. Sufficient for storing 15    |
 |       |                  |   |       | decimal digits.                       |
 | For decimal values, this data type is generally the default choice. As       |
 |  mentioned above, this data type should never be used for precise values,    |
 |  such as currency.                                                           |
 |------------------------------------------------------------------------------|
}

Integer Types {
 Integer arithmetic in Java never produces an overflow or an underflow when you exceed the range of a given integer type. Instead, numbers just wrap around. For example,
   overflow:
    byte b1 = 127, b2 = 1;         // Largest byte is 127
    byte sum = (byte)(b1 + b2);    // Sum wraps to -128, the smallest byte
   underflow:
    byte b3 = -128, b4 = 5;        // Smallest byte is -128
    byte sum2 = (byte)(b3 - b4);   // Sum wraps to a large byte value, 123

 Integer division by zero and modulo by zero are illegal and cause an ArithmeticException to be thrown.
}

Floating-Point Types {
 A float is a 32-bit approximation, which results in at least six significant decimal digits, and a double is a 64-bit approximation, which results in at least 15 significant digits.

 Examples:
  123.45
  0.0
  .01
  1.2345E02 // 1.2345 * 10^2 or 123.45
  1e-6      // 1 * 10^-6 or 0.000001
  6.02e23   // Avogadro's Number: 6.02 * 10^23

 Floating-point literals are double values by default:
  double d = 6.02E23;
  float f = 6.02e23f;

 Infinity, zero and NaN {
  !Floating-point arithmetic never throws exceptions, even when performing illegal operations, like dividing zero by zero or taking the square root of a negative number.
  The float and double types can also represent four special values: positive and negative infinity, zero, and NaN.
  When a floating-point computation produces a value that:
   overflows - then the infinity values result.
   underflows - then a zero value results.
  The infinite floating-point values behave as you would expect. Adding or subtracting any finite value to or from infinity, for example, yields infinity. Negative zero behaves almost identically to positive zero, and, in fact, the == equality operator reports that negative zero is equal to positive zero. One way to distinguish negative zero from positive, or regular, zero is to divide by it: 1.0/0.0 yields positive infinity, but 1.0 divided by negative zero yields negative infinity. Finally, because NaN is Not a Number, the == operator says that it is not equal to any other number, including itself:
   double NaN = 0.0/0.0;   // Not a Number
   NaN == NaN;             // false
   Double.isNaN(NaN);      // true
   double inf = 1.0/0.0;       // Infinity
   double neginf = -1.0/0.0;   // Negative infinity
   double negzero = -1.0/inf;  // Negative zero
   double NaN = 0.0/0.0;       // Not a Number
  Float and Double classes have constants: MIN_VALUE, MAX_VALUE, NEGATIVE_INFINITY, POSITIVE_INFINITY, and NaN.
 }

Primitive Type Conversions {
 Boolean is the only primitive type that cannot be converted to or from another primitive type in Java.
 javac complains when you attempt any narrowing conversion, even if the value being converted would in fact fit in the narrower range of the specified type:
  int i = 13;
  byte b = i; // Incompatible types: possible lossy conversion from int to byte
 The one exception to this rule is that you can assign an integer literal (an int value) to a byte or short variable if the literal falls within the range of the variable.
  byte b = 13;
 Casting to force Java to perform the conversion:
  int i = 13;
  byte b = (byte) i;
  i = (int) 13.456;
 Casts of primitive types are most often used to convert floating-point values to integers. When you do this, the fractional part of the floating-point value is simply truncated (i.e., the floating-point value is rounded toward zero, not toward the nearest integer). The static methods Math.round(), Math.floor(), and Math.ceil() perform other types of rounding.
 Char type is unsigned, so it behaves differently than the short type, even though both are 16 bits wide:
  short s = (short) 0xffff; // These bits represent the number -1
  char c = '\uffff';        // The same bits, as a Unicode character
  int i1 = s;               // Converting the short to an int yields -1
  int i2 = c;               // Converting the char to an int yields 65535
}

Type Conversions. Type Casting {
 instanceof {
  A type instanceof expression tests a value against a type. A cast expression converts a value to a type.
    interface I {}
    class C {} // does not implement I
    void test (C c) { if (c instanceof I) System.out.println("It's an I"); }
    class B extends C implements I {}
    test(new B()); // Prints "It's an I"
  The Java type system does not assume a closed world. Classes and interfaces can be extended at some future time, and casting conversions compile to runtime tests, so we can safely be flexible. However, at the other end of the spectrum the conversion rules do address the case where a class can definitely not be extended, i.e., when it is a final class.
    final class D {}
    // Compile-time error!
    void test (D d) { if (d instanceof I) }
 }
 Narrowing Reference Conversion and Disjoint Types {
  Narrowing reference conversion is one of the conversions used in type checking cast expressions. It enables an expression of a reference type S to be treated as an expression of a different reference type T, where S is not a subtype of T. A narrowing reference conversion may require a test at run time to validate that a value of type S is a legitimate value of type T. However, there are restrictions that prohibit conversion between certain pairs of types when it can be statically proven that no value can be of both types.
 */
    public interface Polygon {}
    public class Rectangle implements Polygon {}
    public class Triangle {}
    public void work(Rectangle r) { Polygon p = (Polygon) r; }
    public void work(Triangle t) { Polygon p = (Polygon) t; }
    // Even though the class Triangle and the interface Polygon are unrelated, the cast expression Polygon p = (Polygon) t is also allowed because at run time these types could be related. A developer could declare the following class:
    class MeshElement extends Triangle implements Polygon {}
    // Compiler can deduce that there are no values (other than the null reference) shared between two types; these types are considered disjoint.
    public final class UtahTeapot {}
    public void work(UtahTeapot u) {
        //Polygon p = (Polygon) u;
        // Error: "Inconvertible types; cannot cast 'UtahTeapot' to 'Polygon'".
        // Because the class UtahTeapot is final, it's impossible for a class to be a descendant of both Polygon and UtahTeapot. Therefore, Polygon and UtahTeapot are disjoint, and the cast statement Polygon p = (Polygon) u isn't allowed.
    }
    public sealed interface Shape permits NewPolygon { }
    public non-sealed interface NewPolygon extends Shape { }
    public final class NewUtahTeapot { }
    public class Ring { }
    public void work(Shape s) {
        //NewUtahTeapot u = (NewUtahTeapot) s;  // Error
        Ring r = (Ring) s;                // Permitted
    }
 /*
 }
}
*/

    /**
     * Non-denotable types
     */
    public static class NonDenotableTypes {
    /*
     {
     Java types that you can use in a program, like int, Byte, Comparable, or String, are called denotable types.
     Non-denotable types, that you can't write in your program, are the types used by a compiler internally:
      - capture variable type;
      - intersection type;
      - anonymous class type.
      Capture variable type.
      The capture of a wildcard type is a type that is used by the compiler represent the type of a specific instance of the wildcard type, in one specific place. Example: Take for example a method with two wildcard parameters, void m(Ex<?> e1, Ex<?> e2). The declared types of e1 and e2 are written exactly the same, Ex<?>. But e1 and e2 could have different and incompatible runtime types. The type checker must not consider the types equal even if they are written the same way. Therefore, during compilation the type parameters of e1 and e2 are given specific types, new ones for each place they are used. These new types are called the capture of their declared types. The capture of a wildcard is an unknown, but normal and concrete type. It can be used the same way as other types. For more details see "Capture conversion" or "Wildcard capture" in Generics.
      Intersection type.
      The intersection types is a feature that is not widely used in Java. However, it is very powerful as it allows to write very tiny interfaces and combine them on demand. There is no excuse to write big fat interfaces that have dozens of totally unrelated methods. Writing tiny interfaces is good to enforce (The Interface Segregation Principle (ISP) - interfaces should contain the least amount of methods as possible) and lower the coupling of the code. However, what happens when a client wants to read a file and write at the same time? The following code uses intersection types to solve the issue of needing an object that implements several interfaces:
      */
        interface FileReader { Collection<String> readLines(); }
        interface FileWriter { void write(String line); }
        interface FileDestroyer { void deleteFile(); }
        class LocalFile implements FileReader, FileWriter, FileDestroyer {
            @Override
            public Collection<String> readLines() { return null; }
            @Override
            public void write(String line) { }
            @Override
            public void deleteFile() { }
        }
        // The & symbol means that the method expects a type T that implements both the FileReader and FileWriter interfaces.
        <T extends FileReader & FileWriter> void readAndWrite(T file) {
            file.readLines();
            file.write("Hello");
        }
      /*
     Non-denotable types are inferred as the types of the expressions that are assigned.
     In such cases, we have a choice of whether to:
      - infer the type;
      - reject the expression;
      - infer a denotable supertype.
    */
        public static void testNonDenotableTypes() {
            // Type of variable a and b isn't a type that you would have read before. But that doesn't stop them from being inferred. The compiler infers them to a non-denotable type: both are inferred type java.util.ImmutableCollections$ListN.
            // List<java.io.Serializable & Comparable<? extends java.io.Serializable & Comparable<?>>> by IDE.
            var a = List.of(1, "2", new StringBuilder());
            // List<java.io.Serializable> by IDE.
            var b = List.of(new ArrayList<String>(), LocalTime.now());

            // The type is effectively Object but extended with a method called quack().
            var duck = new Object() {
                void quack() {
                    println("Quack!");
                }
            };
            duck.quack();
            // Prints:
            //Quack!
        }
    }

    /**
     * TO-DO:
     *  1. Null Annotations.
     *  2. Objects.Nulls. Objects.requireNonNull
     */
    public static class Nulls {

        OptionalMain nullsWithOptional = new OptionalMain();

        // (Void)null.
        public final static <T,R> Gatherer<T, ?, R> mapGatherer(Function<? super T, ? extends R> mapper) {
            return Gatherer.of(
                () -> (Void)null,
                (nothing, element, downstream) -> downstream.push(mapper.apply(element)),
                (l,r) -> l,
                (nothing, downstream) -> {}
            );
        }
    }

}
