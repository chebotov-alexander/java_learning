package org.example.java_learning;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Records (record classes) and Record Patterns.
 */
public class Records {
/*
https://openjdk.org/jeps/395
Records are classes that act as transparent carriers for immutable data. Records can be thought of as nominal tuples. The moto is "the state, the whole state, and nothing but the state".

Goals {
 Devise an object-oriented construct that expresses a simple aggregation of values.
 Help developers to focus on modeling immutable data rather than extensible behavior.
 Automatically implement data-driven methods such as equals and accessors.
}

Motivation {
 It is a common complaint that "Java is too verbose" or has "too much ceremony". Some of the worst offenders are classes that are nothing more than immutable data carriers for a handful of values. Properly writing such a data-carrier class involves a lot of low-value, repetitive, error-prone code: constructors, accessors, equals, hashCode, toString, etc. For example, a class to carry x and y coordinates inevitably ends up like this:
*/
    class PointClass {
        private final int x;
        private final int y;

        PointClass(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x() { return x; }
        int y() { return y; }

        public boolean equals(Object o) {
            if (!(o instanceof PointClass)) return false;
            PointClass other = (PointClass) o;
            return other.x == x && other.y == y;
        }

        public int hashCode() {
            return Objects.hash(x, y);
        }

        public String toString() {
            return String.format("Point[x=%d, y=%d]", x, y);
        }
    }
    // PointRecord is equivalent to PointClass.
    record PointRecord(int x, int y) { }
/*
}

Description {
 Record classes are a new kind of class in the Java language. Record classes help to model plain data aggregates with less ceremony than normal classes.
 The declaration of a record class primarily consists of a declaration of its state. A record class declaration consists of a name, optional type parameters, a header, and a body. The header lists the components of the record class, which are the variables that make up its state. (This list of components is sometimes referred to as the state description).
 Constructors for record classes {
  The rules for constructors in a record class are different than in a normal class. A normal class without any constructor declarations is automatically given a default constructor. In contrast, a record class without any constructor declarations is automatically given a canonical constructor that assigns all the private fields to the corresponding arguments of the new expression which instantiated the record.
  The canonical constructor may be declared explicitly with a list of formal parameters which match the record header, as shown above. It may also be declared more compactly, by eliding the list of formal parameters. In such a compact canonical constructor the parameters are declared implicitly, and the private fields corresponding to record components cannot be assigned in the body but are automatically assigned to the corresponding formal parameter (this.x = x;) at the end of the constructor. The compact form helps developers focus on validating and normalizing parameters without the tedious work of assigning parameters to fields.
*/
    // A compact canonical constructor that validates its implicit formal parameters:
    record Range(int lo, int hi) {
        Range {
            if (lo > hi)  // referring here to the implicit constructor parameters
                throw new IllegalArgumentException(String.format("(%d,%d)", lo, hi));
        }
    }
    // A compact canonical constructor that normalizes its formal parameters:
    record Rational(int num, int denom) {
        Rational {
            int gcd = gcd(num, denom);
            num /= gcd;
            denom /= gcd;
        }
        // This declaration is equivalent to the conventional constructor form:
        /*
        record Rational(int num, int denom) {
            Rational(int num, int demon) {
                // Normalization
                int gcd = gcd(num, denom);
                num /= gcd;
                denom /= gcd;
                // Initialization
                this.num = num;
                this.denom = denom;
            }
        }
        */
        private int gcd(int num, int denom) { return 1;}
    }
  /*
  Record classes with implicitly declared constructors and methods satisfy important, and intuitive, semantic properties. For example, consider a record class R declared as follows:
    record R(T1 c1, ..., Tn cn){ }
  If an instance r1 of R is copied in the following way:
    R r2 = new R(r1.c1(), r1.c2(), ..., r1.cn());
   then, assuming r1 is not the null reference, it is always the case that the expression r1.equals(r2) will evaluate to true. Explicitly declared accessor and equals methods should respect this invariant. However, it is not generally possible for a compiler to check that explicitly declared methods respect this invariant.
  As an example, the following declaration of a record class should be considered bad style because its accessor methods "silently" adjust the state of a record instance, and the invariant above is not satisfied:
    record SmallPoint(int x, int y) {
      public int x() { return this.x < 100 ? this.x : 100; }
      public int y() { return this.y < 100 ? this.y : 100; }
    }
  In addition, for all record classes the implicitly declared equals method is implemented so that it is reflexive and that it behaves consistently with hashCode for record classes that have floating point components. Again, explicitly declared equals and hashCode methods should behave similarly.
 }

 Rules for record classes {
  There are numerous restrictions on the declaration of a record class in comparison to a normal class:
   - A record class declaration does not have an extends clause. The superclass of a record class is always java.lang.Record, similar to how the superclass of an enum class is always java.lang.Enum. Even though a normal class can explicitly extend its implicit superclass Object, a record cannot explicitly extend any class, even its implicit superclass Record.
   - A record class is implicitly final, and cannot be abstract. These restrictions emphasize that the API of a record class is defined solely by its state description, and cannot be enhanced later by another class.
   - The fields derived from the record components are final. This restriction embodies an immutable by default policy that is widely applicable for data-carrier classes.
   - A record class cannot explicitly declare instance fields, and cannot contain instance initializers. These restrictions ensure that the record header alone defines the state of a record value.
   - Any explicit declarations of a member that would otherwise be automatically derived must match the type of the automatically derived member exactly, disregarding any annotations on the explicit declaration. Any explicit implementation of accessors or the equals or hashCode methods should be careful to preserve the semantic invariants of the record class.
   - A record class cannot declare native methods. If a record class could declare a native method then the behavior of the record class would by definition depend on external state rather than the record class's explicit state. No class with native methods is likely to be a good candidate for migration to a record.
  Beyond the restrictions above, a record class behaves like a normal class:
   - Instances of record classes are created using a new expression.
   - A record class can be declared top level or nested, and can be generic.
   - A record class can declare static methods, fields, and initializers.
   - A record class can declare instance methods.
   - A record class can implement interfaces. A record class cannot specify a superclass since that would mean inherited state, beyond the state described in the header. A record class can, however, freely specify superinterfaces and declare instance methods to implement them. Just as for classes, an interface can usefully characterize the behavior of many records. The behavior may be domain-independent (e.g., Comparable) or domain-specific, in which case records can be part of a sealed hierarchy which captures the domain (see below).
    - A record class can declare nested types, including nested record classes. If a record class is itself nested, then it is implicitly static; this avoids an immediately enclosing instance which would silently add state to the record class.
    - A record class, and the components in its header, may be decorated with annotations. Any annotations on the record components are propagated to the automatically derived fields, methods, and constructor parameters, according to the set of applicable targets for the annotation. Type annotations on the types of record components are also propagated to the corresponding type uses in the automatically derived members.
    - Instances of record classes can be serialized and deserialized. However, the process cannot be customized by providing writeObject, readObject, readObjectNoData, writeExternal, or readExternal methods. The components of a record class govern serialization, while the canonical constructor of a record class governs deserialization.
 }
 Local record classes {
  A program that produces and consumes instances of a record class is likely to deal with many intermediate values that are themselves simple groups of variables. It will often be convenient to declare record classes to model those intermediate values. One option is to declare "helper" record classes that are static and nested, much as many programs declare helper classes today. A more convenient option would be to declare a record inside a method, close to the code which manipulates the variables. Accordingly we define local record classes, akin to the existing construct of local classes.
  In the following example, the aggregation of a merchant and a monthly sales figure is modeled with a local record class, MerchantSales. Using this record class improves the readability of the stream operations which follow:
  */
    List<Merchant> findTopMerchants(List<Merchant> merchants, int month) {
      // Local record
        record MerchantSales(Merchant merchant, double sales) {}
        return merchants.stream()
            .map(merchant -> new MerchantSales(merchant, computeSales(merchant, month)))
            .sorted((m1, m2) -> Double.compare(m2.sales(), m1.sales()))
            .map(MerchantSales::merchant)
            .collect(toList());
    }
    private double computeSales(Merchant merchant, int month) { return 0;}
    private class Merchant {}
  /*
  Local record classes are a particular case of nested record classes. Like nested record classes, local record classes are implicitly static. This means that their own methods cannot access any variables of the enclosing method; in turn, this avoids capturing an immediately enclosing instance which would silently add state to the record class. The fact that local record classes are implicitly static is in contrast to local classes, which are not implicitly static. In fact, local classes are never static — implicitly or explicitly — and can always access variables in the enclosing method.
 }

 Local enum classes and local interfaces {
  Nested enum classes and nested interfaces are already implicitly static, so for consistency we define local enum classes and local interfaces, which are also implicitly static.
 }

 Static members of inner classes {
  It is currently specified (https://docs.oracle.com/javase/specs/jls/se14/html/jls-8.html#jls-8.1.3) to be a compile-time error if an inner class declares a member that is explicitly or
  implicitly static, unless the member is a constant variable. This means that, for example, an inner class cannot declare a record class member, since nested record classes are implicitly static.
  We relax this restriction in order to allow an inner class to declare members that are either explicitly or implicitly static. In particular, this allows an inner class to declare a static member that is a record class.
 }

 Annotations on record components {
  When a component is annotated all of the elements to which this particular annotation is applicable are annoteted. This enables classes that use annotations on their fields, constructor parameters, or accessor methods to be migrated to records without having to redundantly declare these members. For example, a class such as the following
    public final class Card {
        private final @MyAnno Rank rank;
        private final @MyAnno Suit suit;
        @MyAnno Rank rank() { return this.rank; }
        @MyAnno Suit suit() { return this.suit; }
        ...
    }
   can be migrated to the equivalent, and considerably more readable, record declaration:
    public record Card(@MyAnno Rank rank, @MyAnno Suit suit) { ... }
  More on this topic in https://openjdk.org/jeps/395
 }

 Reflection API {
  Added two public methods to java.lang.Class:
    - RecordComponent[] getRecordComponents() — Returns an array of java.lang.reflect.RecordComponent objects. The elements of this array correspond to the record's components, in the same order as they appear in the record declaration. Additional information can be extracted from each element of the array, including its name, annotations, and accessor method.
    - boolean isRecord() — Returns true if the given class was declared as a record. (Compare with isEnum.)
 }
}

Alternatives {
 Record classes can be considered a nominal form of tuples. Instead of record classes, we could implement structural tuples. However, while tuples might offer a lightweight means of expressing some aggregates, the result is often inferior aggregates:
  - A central aspect of Java's design philosophy is that names matter. Classes and their members have meaningful names, while tuples and tuple components do not. That is, a Person record class with components firstName and lastName is clearer and safer than an anonymous tuple of two strings.
  - Classes allow for state validation through their constructors; tuples typically do not. Some data aggregates (such as numeric ranges) have invariants that, if enforced by the constructor, can thereafter be relied upon. Tuples do not offer this ability.
  - Classes can have behavior that is based on their state; co-locating the state and behavior makes the behavior more discoverable and easier to access. Tuples, being raw data, offer no such facility.
}

Dependencies {
 The combination of record classes and sealed classes is sometimes referred to as algebraic data types. Record classes allow us to express products, and sealed classes allow us to express sums.
    public sealed interface Expr
    permits ConstantExpr, PlusExpr, TimesExpr, NegExpr {...}

    public record ConstantExpr(int i)       implements Expr {...}
    public record PlusExpr(Expr a, Expr b)  implements Expr {...}
    public record TimesExpr(Expr a, Expr b) implements Expr {...}
    public record NegExpr(Expr e)           implements Expr {...}
}
*/
}

class RecordPatterns {
    void see() {
        PatternMatching.PatternMatchingForRecords patternMatchingForRecords = new PatternMatching.PatternMatchingForRecords();
    }
}

