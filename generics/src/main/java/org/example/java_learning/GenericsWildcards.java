package org.example.java_learning;

import java.util.*;

import static org.example.java_learning.Util.println;

/**
 * Wildcards
 *
 * Links:
 *  - http://bayou.io/draft/Capturing_Wildcards.html
 *
 */
public class GenericsWildcards {
    public static void keyPointsOnGenericsWildcards() {
/*
Some key points:
 - Reifiable type is a type whose type information is fully available at runtime. Reification is representing type parameters and arguments of generic types and methods at runtime. Reification is the opposite of type erasure. The reifiable types in Java are only those types for which reification does not make a difference, that is, the types that do not need any runtime representation of type arguments, this includes: primitives, non-generic (or non-parameterized) reference types, raw types, invocations (instantiations) of unbound wildcards, arrays of any of the above.
 - Non-reifiable types are types where information has been removed at compile-time by type erasure — invocations of generic types that are not defined as unbounded wildcards. A non-reifiable type does not have all of its information available at runtime. Examples of non-reifiable types are List<String> and List<Number>; the JVM cannot tell the difference between these types at runtime.
 - Raw type - the generic type without any type arguments, like Collection, is called raw type. The raw type is assignment compatible with all instantiations of the generic type. Assignment of an instantiation of a generic type to the corresponding raw type is permitted without warnings; assignment of the raw type to an instantiation yields an "unchecked conversion" warning.
    ArrayList         rawList    = new ArrayList();         // Instantiations of raw type.
    ArrayList<String> stringList = new ArrayList<String>(); // Instantiations of the generic type.
    rawList    = stringList; // Compatible without warning.
    stringList = rawList; // Unchecked warning.
   The "unchecked" warning indicates that the compiler does not know whether the raw type ArrayList really contains strings.  A raw type ArrayList can in principle contain any type of object and is similar to a ArrayList<Object>.
   See "Type erasure" for more details.
   Never use raw type:
 */
    GenericsIntro.GenericsRestrictions.ArraysOfGenerics.testGetArrayItem();
 /*
 - String, List<Number>, etc - is a concrete type. List<Number> is not a supertype of List<Integer>; they are two independent types, best considered as siblings. Concrete parameterized types are almost like regular types, there are only a few restrictions, they can NOT be used for the following purposes:
    -- for creation of arrays like this:
        Pair<Integer>[] intPairArr = new Pair<Integer>[10];);
    -- in exception handling like this:
        class IllegalArgumentException<T> extends Exception
       or like this:
        catch (IllegalArgumentException<String> e) { ... }
       or like this:
        throws IllegalArgumentException<String>, IllegalArgumentException<Long>
    -- in a class literal. As a side effect of type erasure, all instantiations of a generic type share the same runtime representation, namely that of the corresponding raw type. In other words, parameterized types do not have type representation of their own. Consequently, there is no point in forming class literals such as List<String>.class, List<Long>.class and List<?>.class, since no such Class objects exist. Only the raw type List has a Class object that represents its runtime type. A class literal denotes a Class object that represents a given type. For instance, the class literal String.class denotes the Class object that represents the type String and is identical to the Class object that is returned when method getClass is invoked on a String object. A class literal can be used for runtime type checks and for reflection;
    -- in an instanceof expression;
 - interface GenericInterface<T> { } - is a generic type. This indicates that the interface/class is a general construct, which can hold any type of payload. It isn’t really a complete interface/class by itself — it’s more like a general description of a whole family of interfaces/classes, one for each type that can be used in place of T.
    Moreover, it is a unbounded type parameter, it means any reference type can be used as type argument to replace the unbounded type parameter in an instantiation of a generic type.
 - <T>, <E>, etc - the identifier T as well as E is a type parameter. It is a placeholder for a type argument. Each type parameter is replaced by a type argument when an instantiation of the generic type is used (Generic type List<T> and its instantiation List<String>).
 - List<Number> - is a parameterized type. Generic types are instantiated to form parameterized types by providing actual type arguments that replace the formal type parameters. A class like LinkedList<E> is a generic type, that has a type parameter E . Instantiations, such as LinkedList<String> or a LinkedList<Integer> , are called parameterized types, and String and Integer are the respective actual type arguments.
 - <T extends Number>, <T extends Comparable<T>>, etc. - is a bounded type parameter.
    Specification of a bound has two effects:
     - It gives access to the non-static methods that the bound specifies. The bound {Comparable<T>} gives access to the {compareTo} method that we want to invoke in the implementation of our class.
     - It restricts the set of types that can be used as type arguments, only types "within bounds" can be used for instantiation of the generic type.
    Several bounds can be specified this way:
        <TypeParameter extends Class & Interface 1 & ... & Interface N>.
    In this case the type argument that replaces the bounded type parameter in an instantiation of a generic type must be a subtype of all bounds. For example:
    class Pair<T extends Comparable<T> & Cloneable, E extends Comparable<E> & Cloneable> { }
  implements Comparable<Pair<A,B>>, Cloneable { }
 - <? extends Number>, <? super Number>, Map<?, Number>, Map<?, ?>, etc - is a wild type aka parameterized type with wildcard arguments.
 - List<?>, Map<?,?>, etc. - unbounded wildcard parameterized type. The raw type and the unbounded wildcard parameterized type have a lot in common. Both act as kind of a supertype of all instantiations of the corresponding generic type. Both are so-called reifiable types. Reifiable types can be used in instanceof expressions and as the component type of arrays, where non-reifiable types (such as concrete and bounded wildcard parameterized type) are not permitted. In other words, the raw type and the unbounded wildcard parameterized type are semantically equivalent. The only difference is that the compiler applies stricter rules to the unbounded wildcard parameterized type than to the corresponding raw type. Certain operations performed on the raw type yield "unchecked" warnings. The same operations, when performed on the corresponding  unbounded wildcard parameterized type, are rejected as errors.
   The unbounded wildcard parameterized type is assignment compatible with all instantiations of the correspinding generic type. Assignment of another instantiation to the unbounded wildcard instantiation is permitted without warnings; assignment of the unbounded wildcard instantiation to another instantiation is illegal.
    ArrayList <?> anyList = new ArrayList<Long>();
    ArrayList<String> stringList = new ArrayList<String>();
    anyList = stringList;
    stringList = anyList; // Error. "Subtypes" can be assigned to the "unbounded supertype", not vice versa.
 - <?> - is a wildcard. A wildcard is only a syntactic component in denoting a wild type, it has no meaning on its own.
 - <?> is like null, something that is unknown (see Three-valued logic).
 - Wildcard does not mean "any type", it means "some unknown type". Or some sources (http://www.angelikalanger.com/GenericsFAQ/FAQSections/ParameterizedTypes.html) states "A wildcard is a syntactic construct with a "?" that denotes not just one type, but a family of types. In its simplest form a wildcard is just a question mark and stands for "all types". A wildcard parameterized type denotes a family of types comprising concrete instantiations of a generic type. The kind of the wildcard being used determines which concrete parameterized types belong to the family. For instance, the wildcard parameterized type Collection<?> denotes the family of all instantiations of the Collection interface regardless of the type argument. The wildcard parameterized type List<? extends Number> denotes the family of all list types where the element type is a subtype of Number. The wildcard parameterized type Comparator<? super String> is the family of all instantiations of the Comparator interface for type argument types that are supertypes of String.
 - Cannot new a wild type, like: new ArrayList<?>().
 - Cannot inherit from a wild type, like {interface MyList extends List<?>}.
   If it were allowed to subtype from a wildcard instantiation of {Comparable} for example, neither developer nor the compiler would know what the signature of the {compareTo} method would be. The signatures of methods of a wildcard parameterized type are undefined. We do not know what type of argument the {compareTo} method is supposed to accept:
        class MyClass implements Comparable<?> {
            public int compareTo(??? arg) { }
        }
   We can only subtype from concrete instantiations of the {Comparable} interface, so that the signature of the {compareTo} method is well-defined:
        class MyClass implements Comparable<MyClass> {
            public int compareTo(MyClass arg) { }
        }
   Raw type is acceptable as a supertype:
        class MyClass implements Comparable {
            public int compareTo(Object arg) { }
        }
 - Wildcard is not a type; it cannot be used in substitutions - it makes no sense to call this.<?>newList(), and it makes no sense to generate a "concrete" List<?> from template List<T>. However:
      List<?> mysteryList = unknownList();   // (1)
      List<?> unknowns = new ArrayList<?>(); // (2)
  The second one won't compile - instantiating a container object with the unknown type as payload is forbidden, it makes no sense.
  But the first one is perfectly valid Java (it's a complete type that a variable can have, unlike List<T>) as long as "unknownList()" method gives collection with type of its payload known to compile, it could be String (i.e. List<String>), Number or something else, it's just us who not know or don't want to know the details. Although we have lost some type information and return type of get() is now effectively Object, that expression really means that List<String> is a subtype of List<?> (but List<?> is not a subtype of any List<T>, for any value of T) — that's what is important. The need to have subtyping relationships between generic types essentially requires us to have a notion of the unknown type.
  See details below.
  Another interesting case of using just "?" in declaration is telling Java to keep some type information in runtime during type erasure, see:
 */
    GenericsTypeErasure.keepRuntimeInformation();
 /*
 - Why we ever want to use a wild type. If it's all about API, why not just use T, i.e. concrete type like List<T>. The answer is what if we want limit our API function to only use numbers and not strings.









   - Collection<? extends T> reads as "get something of some type no higher than type T (T itself and higher) from Collection, so that I can cast it to type T" or "get no more abstract than T" - upper bound. Nothing can go into that collection, only out of type T or Object. Again, it's forbidden to add values in a collection because at compile time a payload time is unknown and eventually someone could write into the collection inappropriate typed value and then another one could  read that value. For example, there is collection typed Double (List<? extends Number> -> List<Double>) and that collection passed to method wildcardCaptureSubtype(List<? extends Number> numberList) inside some program module, then that collection altered with Double values. Then the same collection passed into another program module to method with parameter type List<? extends Number>, where the collection altered with Integer values. If this behavior possible then program prone to ClassCastException errors. To prevent this altering collections is forbidden inside wildcard methods.
   - Collection<? super T> reads as "put something of some type no !higher! than type T (i.e. T and lower) to Collection" or "put no more !abstract! than T, so anyone can cast it to type T later" - lower bound. Nothing can go out of that collection, only into of type T or its subtype.

List<? extends Employee> employeesExtendedEmployeeOfAll =
                Arrays.asList(employee1, manager1, manager2, operator1);
but only can modify the generic upper bounded collection
employeesExtendedEmployeeOfAll.add(null);


Wildcard capture or capture conversion - the compiler converts every wild type (List<? ...>) to a concrete type by replacing each wildcard with a type-variable.



 A parameterized type, such as ArrayList<T>, is not instantiable; we cannot create instances of them. This is because <T> is just a type parameter — merely a placeholder for a genuine type. It is only when we provide a concrete value for the type parameter (e.g., ArrayList<String>) that the type becomes fully formed, and we can create objects of that type. This poses a problem if the type that we want to work with is unknown at compile time. Fortunately, the Java type system is able to accommodate this concept. It does so by having an explicit concept of the unknown type — which is represented as <?>.
 This is the simplest example of Java’s wildcard types. We can write expressions that involve the unknown type:
    ArrayList<?> mysteryList = unknownList();
    Object o = mysteryList.get(0);
 This is perfectly valid Java - ArrayList<?> is a complete type that a variable can have, unlike ArrayList<T>.
 We don’t know anything about mysteryList’s payload type, but that may not be a problem for our code. For example, when we get an item out of mysteryList, it has a completely unknown type. However, we can be sure that the object is assignable to Object — because all valid values of a generic type parameter are reference types (not scalar like "int") and all reference values can be assigned to a variable of type Object.
 On the other hand, when we’re working with the unknown type, there are some limitations on its use in user code.
  For example, this code will not compile:
    mysteryList.add(new Object());
 The reason for this is simple — we don’t know what the payload type of mysteryList is! For example, if mysteryList was really an instance of ArrayList<String>, then we wouldn’t expect to be able to put an Object into it. The only value that we know we can always insert into a container is null — as we know that null is a possible value for any reference type.
 !! This isn’t that useful, and for this reason, the Java language spec also rules out instantiating a container object with the unknown type as payload, for example:
  Won't compile
    List<?> unknowns = new ArrayList<?>();
 One very important use for it is as a starting point for resolving the covariance question. We can use the unknown type if we want to have a subtyping relationship for containers, like this perfectly legal:
    List<?> objectsWithLostTypeInformation = new ArrayList<String>();
 !! This means that List<String> is a subtype of List<?> — although when we use an assignment like the preceding one, we have lost some type information. For example, the return type of get() is now effectively Object. But List<?> is not a subtype of any List<T>, for any value of T.
 !! The need to have subtyping relationships between generic types essentially requires us to have a notion of the unknown type.

*/
    }
    public static void testWildcards() {
        WildcardCapture.captureHelperTest();
        Boundedwildcards.testSubTypeBoundWildcardExample();
        //WildcardCapture.wildcardCaptureSubtype(Arrays.asList(1, 2, 3));
        UnboundedWildcards.testUnboundedWildcards();

    }
}
/**
 * Restriction on using an object through a reference variable of a wildcard parameterized type.
 */
class RestrictionUsingWildcards {
    static class Box<T> {
        private T t;
        public Box(T t) { this.t = t; }
        public Box(Box<? extends T> box) { t = box.t; }
        public void put(T t) { this.t = t; }
        public T take() { return t; }
        public boolean equalTo(Box<T> other) { return this.t.equals(other.t); }
        public Box<T> copy() { return new Box<T>(t); }
        public PairDistinct<T, T> makePair() { return new PairDistinct<T, T>(t,t); }
        public Class<? extends T> getContentType() { return (Class<? extends T>) Box.class.componentType(); }
        public int compareTo(Comparable<? super T> other) { return other.compareTo(t); }
    }
    public static void testRestrictionUsingWildcards() {
        // Unbounded wildcards.
        Box<?> unbounded_1_1 = new Box<String>("abc");
        // Wildcards with an upper bound.
        Box<? extends Number> upperBounded_2_1 = new Box<Long>(0L);
        // Wildcards with a lower bound.
        Box<? super Long> lowerBounded_3_1 = new Box<Number>(0L);
        // Prepare some objects.
        String string_1 = "abc";
        Number number_1 = Long.valueOf(1);
        Long long_1 = Long.valueOf(1);
        Object object_1 = new Object();
        boolean equal_1;
        int compare_1;
        // Read operations:
        //string_1 = unbounded_1_1.t; // Compile-time error: "Required type: 'String'; Provided: 'capture of ?'".
        //string_1 = unbounded_1_1.take(); // Compile-time error: "Required type: 'String'; Provided: 'capture of ?'".
        number_1 = upperBounded_2_1.t;
        number_1 = upperBounded_2_1.take();
        //long_1 = upperBounded_2_1.t; // Compile-time error: "Required type: 'Long'; Provided: 'capture of ? extends Number'".
        //long_1 = upperBounded_2_1.take(); // Compile-time error: "Required type: 'Long'; Provided: 'capture of ? extends Number'".
        //number_1 = lowerBounded_3_1.t; // Compile-time error: "Required type: 'Number'; Provided: 'capture of ? super Long'".
        //number_1 = lowerBounded_3_1.take(); // Compile-time error: "Required type: 'Number'; Provided: 'capture of ? super Long'".
        //long_1 = lowerBounded_3_1.t; // Compile-time error: "Required type: 'Long'; Provided: 'capture of ? super Long'".
        //long_1 = lowerBounded_3_1.take(); // Compile-time error: "Required type: 'Long'; Provided: 'capture of ? super Long'".
        object_1 = unbounded_1_1.t;
        object_1 = unbounded_1_1.take();
        object_1 = upperBounded_2_1.t;
        object_1 = upperBounded_2_1.take();
        object_1 = lowerBounded_3_1.t;
        object_1 = lowerBounded_3_1.take();
        // Write (assignment) operations:
        //unbounded_1_1.t = string_1; // Compile-time error: "Required type: 'capture of ?'; Provided: 'String'".
        //upperBounded_2_1.t = number_1; // Compile-time error: "Required type: 'capture of ? extends Number'; Provided: 'Number'".
        //lowerBounded_3_1.t = number_1; // Compile-time error: "Required type: 'capture of ? super Long'; Provided: 'Number'".
        lowerBounded_3_1.t = long_1;
        //unbounded_1_1.t = "abc"; // Compile-time error: "Required type: 'capture of ?'; Provided: 'String'".
        //upperBounded_2_1.t = 1L; // Compile-time error: "Required type: 'capture of ? extends Number'; Provided: 'long'".
        lowerBounded_3_1.t = 1L;
        //unbounded_1_1.put("abc"); // Compile-time error: "Required type: 'capture of ?'; Provided: 'String'".
        //upperBounded_2_1.put(1L); // Compile-time error: "Required type: 'capture of ? extends Number'; Provided: 'long'".
        lowerBounded_3_1.put(1L);
        unbounded_1_1.t = null;
        upperBounded_2_1.t = null;
        lowerBounded_3_1.t = null;
        // Other operations:
        //equal_1 = unbounded_1_1.equalTo(unbounded_1_1); // Compile-time error: "Required type: 'Box<capture of ?>'; Provided: 'Box<capture of ?>'".
        //equal_1 = unbounded_1_1.equalTo(new Box<String>("abc")); // Compile-time error: "Required type: 'Box<capture of ?>'; Provided: 'String'".
        //equal_1 = upperBounded_2_1.equalTo(upperBounded_2_1); // Compile-time error: "Required type: 'Box<capture of ? extends Number>'; Provided: 'Box<capture of ? extends Number>'".
        //equal_1 = upperBounded_2_1.equalTo(new Box<Long>(0L)); // Compile-time error: "Required type: 'Box<capture of ? extends Number>'; Provided: 'Long'".
        //equal_1 = lowerBounded_3_1.equalTo(lowerBounded_3_1); // Compile-time error: "Required type: 'Box<capture of ? super Long>'; Provided: 'Box<capture of ? super Long>'".
        //equal_1 = lowerBounded_3_1.equalTo(new Box<Long>(0L)); // Compile-time error: "Required type: 'Box<capture of ? super Long>'; Provided: 'Long'".
        //Box<String> unbounded_1_3 = unbounded_1_1.copy(); // Compile-time error: "Required type: 'String'; Provided: 'Box<capture of ?>'".
        Box<?> unbounded_1_2 = unbounded_1_1.copy();
        //Box<Long> unbounded_2_3 = upperBounded_2_1.copy(); // Compile-time error: "Required type: 'Long'; Provided: 'Box<capture of ? extends Number>'".
        Box<? extends Number> upperBounded_2_2 = upperBounded_2_1.copy();
        //Box<Number> unbounded_3_3 = lowerBounded_3_1.copy(); // Compile-time error: "Required type: 'Number'; Provided: 'Box<capture of ? super Long>'".
        Box<? super Long> upperBounded_3_2 = lowerBounded_3_1.copy();
        //PairDistinct<String, String> pair_1_1 = unbounded_1_1.makePair(); // Compile-time error: "Required type: 'PairDistinct<String, String>'; Provided: 'PairDistinct<capture of ?, capture of ?>'".
        PairDistinct<?, ?> pair_1_2 = unbounded_1_1.makePair();
        //PairDistinct<Number, Number> pair_2_1 = upperBounded_2_1.makePair(); // Compile-time error: "Required type: 'PairDistinct<Number, Number>'; Provided: 'PairDistinct<capture of ? extends Number, capture of ? extends Number>'".
        PairDistinct<? extends Number, ? extends Number> pair_2_2 = upperBounded_2_1.makePair();
        //PairDistinct<Long, Long> pair_3_1 = lowerBounded_3_1.makePair(); // Compile-time error: "Required type: 'PairDistinct<Long, Long>'; Provided: 'PairDistinct<capture of ? super Long, capture of ? super Long>'".
        PairDistinct<? super Long, ? super Long> pair_3_2 = lowerBounded_3_1.makePair();
        //Class<? extends String> contentType_1_1 = unbounded_1_1.getContentType(); // Compile-time error: "Required type: 'Class<? extends String>'; Provided: 'Class<capture of ? extends capture of ?>'".
        Class<?> contentType_1_2 = unbounded_1_1.getContentType();
        //Class<Number> contentType_2_1 = upperBounded_2_1.getContentType(); // Compile-time error: "Required type: 'Class<Number>'; Provided: 'Class<capture of ? extends capture of ? extends Number>'".
        Class<? extends Number> contentType_2_2 = upperBounded_2_1.getContentType();
        //Class<Long> contentType_3_1 = lowerBounded_3_1.getContentType(); // Compile-time error: "Required type: 'Class<Long>'; Provided: 'Class<capture of ? extends capture of ? super Long>'".
        //Class<? super Long> contentType_3_2 = lowerBounded_3_1.getContentType(); // Compile-time error: "Required type: 'Class<? super Long>'; Provided: 'Class<capture of ? extends capture of ? super Long>'".
    /*
    In a wildcard parameterized type such as Box<?> the type of the field and the argument and the return types of the methods would be unknown. It is like the field t would be of type "?" and the put method would take an argument of type "?" and the take method would return a "?" and so on.
    In this situation the compiler does not let us assign anything to the field or pass anything to the put method. The reason is that the compiler cannot make sure that the object that we are trying to pass as an argument to a method is of the expected type, since the expected type is unknown. Similarly, the compiler does not know of which type the field is and cannot check whether we are assigning an object of the correct type, because the correct type is not known.
    In contrast, the take method can be invoked and it returns an object of an unknown type, which we can assign to a reference variable of type Object.
    Similar effects can be observed for methods such as like equalTo and copy , which  have a parameterized argument or return type and the type parameter T appears as type argument of the parameterized argument or return type.

    Which methods can or must not be invoked through a wildcard instantiation depends on:
     - the type of the wildcard instantiation (unbounded or bounded with upper or lower bound);
     - the use of the type parameter (as type argument or as wildcard bound).
    The type parameter can appear as:
     - the type argument of a parameterized argument or return type: like the type parameter T in method makePair, which returns a PairDistinct<T, T>.
     - part of the type argument of a parameterized argument or return type, namely as bound of a wildcard, like in method geteContentType, which returns a value of type Class<? extends T>.

    In a wildcard parameterized type a field whose type is the type parameter of the enclosing generic class is of unknown type. Depending on the wildcard (whether it is unbounded or has an upper or lower bound) different kinds of access are permitted or disallowed.

    */
    }
}

/**
 * Bounded wildcards
 */
class Boundedwildcards {
    /**
     * Introducing Variance
     */
    void introducingVariance() {
    /*
    Some key points:
      - !Generic types are invariant, not covariant or contrvariant, i.e. List<String> is not a subtype of List<Object>.
      - !Bounded wildcards are used to describe the inheritance hierarchy of a mostly unknown type — effectively making statements like, for example, "I don’t know anything about this type, except that it must implement List". In the type parameter it would be written as: <? extends List>.
      - !The extends keyword is always used, regardless of whether the constraining type is a class or interface type.
      - !Intuitively speaking, wildcards with supertype bounds let you write to a generic object, while wildcards with subtype bounds let you read from a generic object. it is very commonplace to see these types of generic constructions with types that act as producers or consumers of payload types. This is codified in the Producer Extends, Consumer Super (PECS) principle, i.e.:
        void copy(List<? extends T> src, List<? super T> dst)

     Concept called type variance is the general theory of how inheritance between container types relates to the inheritance of their payload types:
      - Type covariance. This means that the container types have the same relationship to each other as the  payload types do. This is expressed using the extends keyword: <? extends Pet>.
    */
        List<Cat> cats = new ArrayList<Cat>();
        // Cat extends Pet.
        List<? extends Pet> pets = cats;
        // won't compile, we don't know what hides under ?
        //pets.add(new Cat());
        // won't compile, we don't know what hides under ?
        //pets.add(new Pet());
        // will compile.
        cats.add(new Cat());
    /*
      - Type contravariance. This means that the container types have the inverse relationship to each other as the payload types. This is expressed using the super keyword: <? super Cat>.
     The usefulness of covariant arrays led to them being seen as a necessary evil in the very early days of the platform, despite the hole in the static type system that the feature exposes.
    */
        String[] someWords = {"Hello", "World!"};
        // Could but really shouldn't.
        Object[] objectsFormerlyStrings = someWords;
        // Runtime error ArrayStoreException.
        objectsFormerlyStrings[0] = Integer.valueOf(42);
    /*
    Is this legal?
        List<Object> stringsInObjectSkin = new ArrayList<String>();
    What do we do about this?
        stringsInObjectSkin.add(new Object());
    As the type of objects was declared to be List<Object>, then it should be legal to add an Object instance to it. However, as the actual instance holds strings, then trying to add an Object would not be compatible, and so this would fail at runtime.
    Although this is legal:
        Object o = new String("X");
    But it does not mean that the corresponding statement for generic container types is also true, and as a result won't compile:
        List<Object> stringsInObjectSkin = new ArrayList<String>();
    */
    }

    /**
     * A wildcard with a subtype bound (Covariance)
     */
    void subTypeBoundWildcard() {
    // What ? extends is upper bound class/interface
        var ceo = new Manager("Gus Greedy", 800000, 2003, 12, 15);
        var cfo = new Manager("Sid Sneaky", 600000, 2003, 12, 15);
        var ops = new Operator("Beca Shy", 400000, 2003, 12, 15);
        var managerBuddies = new Pair<Manager>(ceo, cfo);
        Pair<? extends Employee> wildcardBuddies = managerBuddies; // Ok
        // getFirst() signature - in: null; out: <? extends Employee>
        Employee someEmployee1 = managerBuddies.getFirst(); // Ok
        Employee someEmployee2 = wildcardBuddies.getFirst(); // Ok
        // setFirst signature - in: <? extends Employee>; out: void
        //wildcardBuddies.setFirst(ops); // Compile-time error.
        //wildcardBuddies.setFirst(cfo); // Compile-time error.
        wildcardBuddies.setFirst(null); // Ok.
    /*
    The compiler knows that the parameter of setFirst has some specific type, which extends Employee. Is that specific type Employee? Is it Manager, or some other subclass? There is no way for the compiler to know. Therefore, the compiler cannot accept Operator ops. For the same reason, the call wildcardBuddies.setFirst(cfo), where cfo is a Manager instance, also fails. The compiler must reject all arguments to setFirst other than null.
     The getFirst method continues to work. The return value of getFirst is an instance of some specific type, which is a subtype of Employee. The compiler doesn’t know what that specific type is, but it can guarantee that the assignment to an Employee reference is safe.
     ! This is the key idea behind bounded wildcards. We now have a way of distinguishing between the safe accessor methods and the unsafe mutator methods.
    */
    }

    // Another example:
    // 1st implementation
    static boolean subTypeBoundWildcardExampleFindWithWildcard(
        List<? extends Employee> l, Employee e
    ) { return l.contains(e); }
    // 2nd implementation
    static <T extends Employee> boolean subTypeBoundWildcardExampleFindWithPlainGeneric (
        List<T> l, T e
    ) { return l.contains(e); }
    // 3nd implementation
    static <T extends Employee> boolean subTypeBoundWildcardExampleFindWithPlainGenericLoop (
        List<T> l, T e
    ) {
        boolean result = false;
        for (T eInner : l) { result = eInner.equals(e); }
        return result;
    }
    static void testSubTypeBoundWildcardExample() {
        var employee1 = new Employee("Tar TheIron", 900000, 2003, 12, 15);
        var manager1 = new Manager("Gus Greedy", 800000, 2003, 12, 15);
        var manager2 = new Manager("Sid Sneaky", 600000, 2003, 12, 15);
        var operator1 = new Operator("Beca Shy", 400000, 2003, 12, 15);
        List<Employee> employeesEmployeeOfManager =
                Arrays.asList(manager1, manager2);
        List<Manager> employeesManagerOfManager =
                Arrays.asList(manager1, manager2);
        List<? extends Employee> employeesExtendedEmployeeOfManager =
                Arrays.asList(manager1, manager2);
        List<Employee> employeesEmployeeOfAll =
                Arrays.asList(employee1, manager1, manager2, operator1);
        List<? extends Employee> employeesExtendedEmployeeOfAll =
                Arrays.asList(employee1, manager1, manager2, operator1);

        var result_1_1 = subTypeBoundWildcardExampleFindWithWildcard(
            employeesEmployeeOfManager, employee1
        );
        var result_1_2 = subTypeBoundWildcardExampleFindWithWildcard(
            employeesManagerOfManager, employee1
        );
        var result_1_3 = subTypeBoundWildcardExampleFindWithWildcard(
            employeesExtendedEmployeeOfManager, employee1
        );
        var result_1_4 = subTypeBoundWildcardExampleFindWithWildcard(
            employeesEmployeeOfAll, employee1
        );
        var result_1_5 = subTypeBoundWildcardExampleFindWithWildcard(
            employeesExtendedEmployeeOfAll, employee1
        );
        var result_1_6 = subTypeBoundWildcardExampleFindWithWildcard(
            employeesExtendedEmployeeOfAll, operator1
        );
        var result_2_1 = subTypeBoundWildcardExampleFindWithPlainGeneric(
            employeesEmployeeOfManager, employee1
        );
        var result_2_2 = subTypeBoundWildcardExampleFindWithPlainGeneric(
            employeesManagerOfManager, manager1
        );
        // Fail to compile, because T must be the same.
        //var result_2_3 = subTypeBoundWildcardExampleFindWithPlainGeneric(
        //  employeesManagerOfManager, employee1
        //);
        // Fail to compile, because T must be the same.
        //var result_2_4 = subTypeBoundWildcardExampleFindWithPlainGeneric(
        //  employeesExtendedEmployeeOfManager, employee1
        //);
        var result_2_5 = subTypeBoundWildcardExampleFindWithPlainGeneric(
            employeesEmployeeOfAll, employee1
        );
        // Fail to compile, because T must be the same.
        //var result_2_6 = subTypeBoundWildcardExampleFindWithPlainGeneric(
        //    employeesExtendedEmployeeOfAll, employee1
        //);
        // Fail to compile, because T must be the same.
        //var result_2_7 = subTypeBoundWildcardExampleFindWithPlainGeneric(
        //    employeesExtendedEmployeeOfAll, operator1
        //);
        // Fail to compile, because T must be the same.
        //var result_2_8 = subTypeBoundWildcardExampleFindWithPlainGeneric(
        //    employeesExtendedEmployeeOfAll, manager1
        //);
        // Fail to compile, because T must be the same.
        //var result_2_9 = subTypeBoundWildcardExampleFindWithPlainGeneric(
        //    employeesExtendedEmployeeOfManager, manager1
        //);
        var result_2_10 = subTypeBoundWildcardExampleFindWithPlainGeneric(
            (List<Manager>)employeesExtendedEmployeeOfManager, manager1
        );
        var result_3_1 = subTypeBoundWildcardExampleFindWithPlainGenericLoop(
            employeesManagerOfManager, manager1
        );
        //println(result_2_10+"\n"+result_3_1);
    }

    /**
     * A wildcard with a supertype bound (Contrvariance)
     */
    void superTypeBoundWildcard() {
    /*
    Imagine there is a method that uses type extending Comparable interface. Comparable interface is itself a generic type. The type variable indicates the type of the other parameter. For example, the String class implements Comparable<String>, and its compareTo method is declared as "public int compareTo(String other)".
        public interface Comparable<T> {
            public int compareTo(T other);
        }
     Here is the method:
        public static <T extends Comparable<T>> Pair<T> minmax(T[] a) {...}
     This looks more thorough than just using T extends Comparable, and it would work fine for many classes. For example, if you compute the minimum of a String array, then T is the type String, and String is a subtype of Comparable<String>. But we run into a problem when processing an array of LocalDate objects. LocalDate implements ChronoLocalDate, and ChronoLocalDate extends Comparable<ChronoLocalDate>.
     Thus, LocalDate implements Comparable<ChronoLocalDate> but not Comparable<LocalDate>.
     In a situation such as this one, supertypes come to the rescue:
        public static <T extends Comparable<? super T>> Pair<T> minmaxSuper(T[] a) {...}
     Now the compareTo method has the form:
        int compareTo(? super T)
     Maybe it is declared to take an object of type T, or — for example, when T is LocalDate — a supertype of T. So, it is safe to pass an object of type T to the compareTo method.

    Another common use for supertype bounds is an argument type of a functional interface. For example, the Collection interface has a method that removes all elements that fulfill the given predicate:
        default boolean removeIf(Predicate<? super E> filter)
        ArrayList<Employee> staff = ...;
        Predicate<Object> oddHashCode = obj -> obj.hashCode() %2 != 0;
        staff.removeIf(oddHashCode);
     You want to be able to pass a Predicate<Object>, not just a Predicate<Employee>. The super wildcard makes that possible.
    */
    }
}

/**
 * Unbounded Wildcards
 */
class UnboundedWildcards {
    // Unbounded wildcard parameterized type is like an interface type: you can declare reference variables of the type, but you cannot create objects of the type. A reference variable of an interface type or a wildcard parameterized type can refer to an object of a compatible type. For an interface, the compatible types are the class (or enum) types that implement the interface. For a wildcard parameterized type, the compatible types are the concrete instantiations of the corresponding generic type that belong to the family of instantiations that the wildcard denotes.
    static class Restrictions<T> {
        private T t;
        public Restrictions(T t) { this.t = t; }
        public void put(T t) { this.t = t;}
        public T take() { return t; }
        public boolean equalTo(Restrictions<T> other) { return this.t.equals(other.t); }
        public Restrictions<T> copy() { return new Restrictions<T>(t); }

        static void testRestrictions() {
            Restrictions<?> restriction_1 = new Restrictions<String>("abc");
            //restriction_1.put("xyz"); // Compile-time error: "Required type: capture of ?; Provided: String".
            restriction_1.put(null);
            //String s = restriction_1.take(); // Compile-time error: "Required type: String; Provided: capture of ?".
            Object o = restriction_1.take();
            //boolean equal = restriction_1.equalTo(restriction_1); // Compile-time error: "Required type: capture of ?; Provided: capture of ?".
            //equal = restriction_1.equalTo(new Restrictions<String>("abc")); // Compile-time error: "Required type: Restrictions<capture of ?>; Provided: Restrictions<String>".
            Restrictions<?> restriction_2 = restriction_1.copy();
            //Restrictions<String> restriction_3 = restriction_2.copy(); // Compile-time error: "Required type: Restrictions<String>; Provided: Restrictions<capture of ?>".
        // In a wildcard parameterized type such as {Restrictions<?>} the type of the field and the argument and the return types of the methods would be unknown. It is like the field t would be of type "?" and the {put} method would take an argument of type "?" and the {take} method would return a "?" and so on.
        // In this situation the compiler does not let us assign anything to the field or pass anything to the {put} method. The reason is that the compiler cannot make sure that the object that we are trying to pass as an argument to a method is of the expected type, since the expected type is unknown. Similarly, the compiler does not know of which type the field is and cannot check whether we are assigning an object of the correct type, because the correct type is not known.
        // In contrast, the {take} method can be invoked and it returns an object of an unknown type, which we can assign to a reference variable of type {Object}.
        // For more details see:
            RestrictionUsingWildcards.testRestrictionUsingWildcards();
        }
    }

    static void testUnboundedWildcards() {
        PairDistinct<String, Long> pairConcrete; // Declaration with concrete type (concrete parameterized type), concrete instantiations.
        //PairDistinct<T, U> pairGeneric; // Not allowed since class UnboundedWildcards is concrete type.
        class UnboundedWildcardsGeneric<T, U> {
            //PairDistinct<T, U> pairGeneric = new PairDistinct<>("maximum", 1024L); // Compile-time error: "no instance(s) of type variable(s) exist so that String conforms to T inference variable X has incompatible bounds: equality constraints: T lower bounds: String inference variable Y has incompatible bounds: equality constraints: U lower bounds: Long"
            //PairDistinct<T, U> pairGeneric = (PairDistinct<T, U>) new PairDistinct<>("maximum", 1024L); // Allowed.
            PairDistinct<T, U> pairGeneric;
            //static PairDistinct<T, U> pairGeneric; // Compile-time error: "'UnboundedWildcardsGeneric.this' cannot be referenced from a static context" (the static context is independent of the type parameters and exists only once per raw type).
        }
        PairDistinct<?,?> pairWildcard; // Declaration with wildcard (wildcard parameterized type), wildcard instantiations.
        PairDistinct<?, Long> pairMix; // Declaration with concrete type and wildcard.
        pairConcrete    = new PairDistinct<>("maximum", 1024L);
        pairWildcard    = new PairDistinct<>("maximum", 1024L);
        pairMix         = new PairDistinct<>("maximum", 1024L);
        //UnboundedWildcardsGeneric.pairGeneric = new PairDistinct<>("maximum", 1024L); // Not allowed in static context.
        UnboundedWildcardsGeneric uwg = new UnboundedWildcardsGeneric();
        uwg.pairGeneric = new PairDistinct<>("maximum", 1024L);
        var s1 = toStringPairTypeParameter(pairWildcard);
        var s2 = toStringPairTypeParameter(pairConcrete);
        var s3 = toStringPairTypeParameter(pairMix);
        //var s4 = toStringPairConcrete(pairWildcard); // Same error.
        var s5 = toStringPairConcrete(pairConcrete);
        //var s6 = toStringPairConcrete(pairMix); // Same error.
        var s7 = toStringPairMix1(pairWildcard);
        var s8 = toStringPairMix1(pairConcrete);
        var s9 = toStringPairMix1(pairMix);
        //var s10 = toStringPairMix2(pairWildcard); // Same error.
        var s11 = toStringPairMix2(pairConcrete);
        var s12 = toStringPairMix2(pairMix);
        var s13 = toStringPairWildcard(pairWildcard);
        var s14 = toStringPairWildcard(pairConcrete);
        var s15 = toStringPairWildcard(pairMix);
        var s16 = toStringPair(pairWildcard);
        var s17 = toStringPair(pairConcrete);
        var s18 = toStringPair(pairMix);
        println(
            "All the same: " + (
            //1 == Arrays.asList(s1, s2, s3, s5, s7, s8, s9, s11, s12, s13, s14, s15, s16, s17, s18).stream()
            //    .distinct()
            //    //.forEach((s) -> println("Unique" + s));
            //    .collect(Collectors.toList()).size()
                new HashSet(Arrays.asList(s1, s2, s3, s5, s7, s8, s9, s11, s12, s13, s14, s15, s16, s17, s18)).size() == 1
            )
        );
        printPair(pairConcrete);
        printPair(pairWildcard);
        printPair(pairMix);
        printPair(uwg.pairGeneric);
        //pairConcrete = pairWildcard; // Compile-time error. Required type: PairDistinct<String, Long>; Provided: PairDistinct<capture of ?, capture of ?>
        //pairMix = pairWildcard; // Compile-time error. Required and Provided types are not matched.
        uwg.pairGeneric = pairWildcard;
        pairWildcard = pairConcrete;
        pairWildcard = pairMix;
        pairMix = pairConcrete;
    }
    static <T, U, R> R toStringPairTypeParameter(PairDistinct<T, U> pair) {
        return (R) ("(" + pair.getFirst() + ", " + pair.getSecond() + ")");
    }
    static String toStringPairConcrete(PairDistinct<String, Long> pair) {
        return "(" + pair.getFirst() + ", " + pair.getSecond() + ")";
    }
    static <U, R> R toStringPairMix1(PairDistinct<?, U> pair) {
        return (R) ("(" + pair.getFirst() + ", " + pair.getSecond() + ")");
    }
    static <R> R toStringPairMix2(PairDistinct<?, Long> pair) {
        return (R) ("(" + pair.getFirst() + ", " + pair.getSecond() + ")");
    }
    // Can return some type.
    static <R> R toStringPairWildcard(PairDistinct<?, ?> pair) {
        return (R) ("(" + pair.getFirst() + ", " + pair.getSecond() + ")");
    }
    // Cannot return some unknown type, i.e. wildcard.
    static String toStringPair(PairDistinct<?, ?> pair) {
        return toStringPairWildcard(pair);
    }
    static void printPair(PairDistinct<?, ?> pair) {
        println(toStringPair(pair));
    }
/*
The declaration PairDistinct<?,?> is an example of a wildcard parameterized type, where both type arguments are wildcards. Each question mark stands for a separate representative from the family of "all types". The resulting family of instantiations comprises all instantiations of the generic type Pair. Note, the concrete type arguments of the family members need not be identical; each "?" stands for a separate type. A reference variable or method parameter whose type is a wildcard parameterized type, can refer to any member of the family of types that the wildcard denotes.
A wildcard is not a type variable, so we can’t write code that uses ? as a type. In particular, a wildcard is not a type; it cannot be used in substitutions - it makes no sense to call this.<?>newList(), and it makes no sense to generate a "concrete" List<?> from template List<T>.
 If there is a method like this:
    public static void swap(Pair<?> p)
  the following would be illegal:
    ? t = p.getFirst(); // ERROR
    p.setFirst(p.getSecond());
    p.setSecond(t);
 The return value of getFirst can only be assigned to an Object. The setFirst method can never be called, not even with an Object (only setFirst(null)). That’s the essential difference between Pair<?> and Pair (raw one): you can call the setFirst method of the raw Pair class with any Object:
    public static boolean hasNulls(Pair<?> p) {
        return p.getFirst() == null || p.getSecond() == null;
    }
  same result with:
	public static <T> boolean hasNulls(Pair<T> p)
 You cannot new a wild type, like new ArrayList<?>().
 You cannot inherit from a wild type, like interface MyList extends List<?>.
*/
}

/**
 * Wildcard Capture (Capture Conversion)
 */
class WildcardCapture {
/*
Two basic points to understand wild types:
 - Supertype - List<? extends Number> is the supertype of every concrete List<X> where X is a subtype of Number. Wild types are primarily used as variable types.
 - Capture Conversion - if the type of a value is List<? extends Number>, the compiler converts the type to a concrete List<X>, where X stands for an unknown subtype of Number.

Concrete Type {
 A generic class or interface can be viewed as a code template - we can substitute its type-parameters with actual types to generate a concrete class or interface:
    interface List<T>              T → Number          interface List<Number>
    {                                                  {
        int   size();                                      int     size();
        T     get(int);                                    Number  get(int);
        void  add(T);                                      void    add(Number);
    }                                                  }
 The concrete List<Number> is easy to understand and use, just like any non-generic types.
 Such concrete types are mutually exclusive, meaning List<A> and List<B> (A≠B) share no objects. For example, an object cannot be both an instance of List<Number> and List<Integer>.
 Every object is an instance of some concrete class type (e.g. ArrayList<Number>), which has concrete super-classes and concrete super-interfaces (e.g. List<Number>).
 Here we are not concerned about type erasure and not consider raw types (e.g List).
}

Generic method {
 A generic method can also be viewed as a code template:
   <T> List<T> newList()           T → Number   <Number> List<Number> newList()
   {                                            {
       return new ArrayList<T>();                   return new ArrayList<Number>();
   }                                            }
 To invoke a generic method, its type-parameters must be substituted with actual types, either explicitly or by inference:
   List<Number> list = this.<Number>newList();
   List<Number> list = this.newList();  // <Number> is inferred.
}

Wild Type {
 List<Number> is not a supertype of List<Integer>; they are two independent types, best considered as siblings. If we want to design a method that adds up a list of numbers, the following method signature won't work very well:
    double sum(List<Number> numberList) // add all numbers as `double`
 This method can only accept List<Number> arguments, but not List<Integer>, List<Double> etc. To fix that problem, we want to declare numberList in a supertype of all of List<Number>, List<Integer>, List<Double>, ...
 That is exactly what wild types are designed for:
    List<? extends Number> is the supertype of all List<X> where X <: Number.
 The symbol "<:" means "is a subtype of". Note that every type is a subtype of itself, e.g. Number <: Number.
 By declaring parameter numberList in the wild supertype, the method now can accept various lists:
    double sum(List<? extends Number> numberList)
 For example, we can call this method with a List<Integer> argument, precisely because the argument type is a subtype of the parameter type, i.e. List<Integer> <: List<? extends Number>.
 Wild types can be viewed as "horizontal" supertypes, as opposed to "vertical" supertypes through inheritance.
 Wild types are primarily used for declaring types of variables, so that a variable may store objects of various concrete types.
 Wild types are quite different from concrete types. We cannot new a wild type, like new ArrayList<?>(). We cannot inherit from a wild type, like interface MyList extends List<?>. We must use concrete types in these places.
 Notice how we focus on "wild type" instead of wildcard. A wildcard is only a syntactic component in denoting a wild type, it has no meaning on its own. In particular, a wildcard is not a type; it cannot be used in substitutions - it makes no sense to call this.<?>newList(), and it makes no sense to generate a "concrete" List<?> from template List<T>.
}

Capture Conversion {
 If an object's static type is List<? extends Number>, what are the instance methods that we can invoke on the object, and what are the signatures of these methods? We cannot answer that by template substitution with wildcard:
    interface List<?> {
        ?    get(int);   // nonsense
        void add(?);   // nonsense
        ...
    }
 However, we do know that the object must be in a concrete type List<X> where X<:Number. At compile time, the exact type of X is unknown, so we use a type-variable X to represent it. The concrete type List<X> looks like:
    interface List<X> { // X <: Number
        X    get(int);
        void add(X);
        ...
    }
 It would be easier to handle the object as the concrete List<X> - and that is exactly what the compiler does, in a process called wildcard capture or capture conversion - wherever an object's type is a wild type (List<? ...>), it is converted to a concrete type, by replacing each wildcard with a type-variable. Compiler infers a particular type from the code. By doing that, the compiler only needs to handle objects in concrete types. In other words, if the type of a value is List<? extends Number>, the compiler converts the type to a concrete List<X>, where X stands for an unknown subtype of Number.
 For example, given an object numberList in type List<? extends Number>, the compiler converts its type to List<X> where X<:Number. Now we know the object has a method "X get(int)", therefore we can do:
    Number number = numberList.get(i); // assign X to Number
 The object also has a method add(X) which can be invoked with an X argument. A trivial case is add(null), since null is assignable to any type. A better example would be an X argument retrieved from get(i); we'll discuss that later.
 ! Note that we cannot call add(number) with a Number argument, since it's not true that Number<:X.
 Here "X" represents type-variables introduced by capture conversions. Compilers use names like "CAP#1", "capture#1" for those type-variables; we may see these names in compiler messages, for example:
    numberList.add(number); // compile error:  add(capture#1) is not applicable to Number
}

Capture Everywhere {
 The compiler applies capture conversion on every expression that yields a value in wild type.
 This is very important to understand, so let's exercise with some mind-numbing examples.
 In the following code, capture conversion is applied on every %marked% expression.
 Example 1.
          List<? extends Number> foo(List<? extends Number> numberList)
          {
    #1       for(Number number : %numberList%)
    #2           assert %numberList%.contains(number);
    #3       numberList = %numberList%;
    #4       return %numberList%;
          }
  - Line#1 - The type of numberList is capture converted to List<X1>. It is a subtype of Iterable<X1>, therefore it can be used in the for statement. Variable number can be declared as Number, because X1<:Number.
  - Line#2 - numberList is converted to List<X2>. The compiler searches contains() method in List<X2>.
  - Line#3 - Right-hand numberList is converted to List<X3> first. Then, the compiler checks whether List<X3> is assignable to the left-hand type. The left-hand numberList denotes a variable, not a value; it's not subject to capture conversion. The assignment is legal because List<X3> <: List<? extends Number>.
  - Line#4 - Similar to #3, except List<X4> is checked against the return type.
 Example 2.
          void bar(List<? extends Number> numberList)
          {
    #a        %numberList%.stream().map(n -> n.intValue());
    #b        %foo% ( %foo% ( %numberList% )).stream();
    #c        %numberList%.add( %numberList%.get(0)); // compile error
          }
  - Line#a - numberList is converted to List<Xa>; the stream() method returns Stream<Xa>. In map(n->...), the type of n is inferred as Xa, which inherits the method intValue() from Number.
  - Line#b involves 3 capture conversions:
    - numberList is converted to List<Xb1> first; then, the compiler checks it against the method parameter type.
    - foo(numberList)'s type is List<? extends Number>, which is then converted to List<Xb2>.
    - Type of foo(foo(numberList)) is converted to List<Xb3>; stream() returns Stream<Xb3>.
  - Line#c - There are two numberList expressions; the compiler applies capture conversion on them individually, resulting in List<Xc1> and List<Xc2>. The get() method returns Xc2, and the add() method accepts Xc1. The code fails to compile, because Xc2<:Xc1 is not true.
*/
    static List<? extends Number> wildcardCaptureGenericMethodSubtype(List<? extends Number> numberList) {
        for (Number number : numberList) { // 1
            assert numberList.contains(number); // 2
        }
        numberList = numberList; // 3
        return numberList; // 4
    }
    static void wildcardCaptureSubtype(List<? extends Number> numberList) {
        numberList.stream().map(n -> n.intValue()); // a
        wildcardCaptureGenericMethodSubtype(wildcardCaptureGenericMethodSubtype(numberList)).stream(); // b
        // Compile error.
        //numberList.add(numberList.get(0)); // c
        var value_1 = numberList.get(0); // Inferred as Number.
        Number value_2 = numberList.get(0);
        Integer value_3 = (Integer) numberList.get(0); // Runtime-exception alert: at index 0 might be Long or some other Number subtype.
        Object value_4 = (Object) numberList.get(0);
        // Compile error. Only null can be added.
        //numberList.add(value_1);
        //numberList.add(value_2);
        //numberList.add(value_3);
        //numberList.add(value_4);
    }
 /*
 Capture Helper.
  Type-variables introduced by capture conversions are "undenotable" - their names are given arbitrarily, whether by the compiler (like "CAP#1") or by our mind (like "X1"). They don't have proper names that we can reference in source code. If we could reference them in source code, it would be very useful in some cases. Imagine if we could do:
     void bar(List<? extends Number> numberList) {
         numberList.add(numberList.get(0)); // compile error
         List<X> list = numberList;  // *imaginary* code
         X number = list.get(0);     // get() returns X
         list.add(number);           // add() accepts X
     }
  Fortunately, there is a way to approach that. We can introduce a generic method with named type-variables:
     <T extends Number> void bar2(List<T> list) {
         T number = list.get(0);
         list.add(number);
   then we can call the method as:
     bar2(numberList);
  Here numberList is capture converted to List<X>; then, the compiler infers T → X for bar2(). Essentially, the capture helper method bar2 assigns a name to the type-variable introduced by the capture conversion. This technique is useful whenever you find it impossible or unpleasant to work with wild types.
  Again, it's forbidden to add values in a collection because at compile time a payload time is unknown and eventually someone could write into the collection inappropriate typed value and then another one could  read that value. For example, there is collection typed Double (List<? extends Number> -> List<Double>) and that collection passed to method wildcardCaptureSubtype(List<? extends Number> numberList) inside some program module, then that collection altered with Double values. Then the same collection passed into another program module to method with parameter type List<? extends Number>, where the collection altered with Integer values. If this behavior possible then program prone to ClassCastException errors. To prevent this altering collections is forbidden inside wildcard methods.
    */
    <T extends Number> void wildcardCaptureGenericMethodSubtypeWithoutWildcard(List<T> numberList) {
        numberList.stream().map(n -> n.intValue());
        wildcardCaptureGenericMethodSubtype(wildcardCaptureGenericMethodSubtype(numberList)).stream();
        numberList.add(numberList.get(0));
        var value_1 = numberList.get(0); // Inferred as T.
        Number value_2 = numberList.get(0);
        Integer value_3 = (Integer) numberList.get(0);
        Object value_4 = (Object) numberList.get(0);
        numberList.add(value_1); // 1
        //numberList.add(value_2);
        // But this dirty trick may cause ClassCastException runtime-error.
        numberList.add((T) value_2);
        //numberList.add(value_3);
        //numberList.add(value_4);
    }
    public static void captureHelperTest() {
        var ceo = new Manager("Gus Greedy", 800000, 2003, 12, 15);
        var cfo = new Manager("Sid Sneaky", 600000, 2003, 12, 15);
        var managerBuddies = new Pair<Manager>(ceo, cfo);
        printBuddies(managerBuddies);
        ceo.setBonus(1000000);
        cfo.setBonus(500000);
        Manager[] managers = {ceo, cfo};
        var result = new Pair<Employee>();
        minmaxBonus(managers, result);
        println("first: " + result.getFirst().getName() + ", second: " + result.getSecond().getName());
        maxminBonus(managers, result);
        println("first: " + result.getFirst().getName() + ", second: " + result.getSecond().getName());
    }

    public static void printBuddies(Pair<? extends Employee> p) {
        Employee first = p.getFirst();
        Employee second = p.getSecond();
        println(first.getName() + " and " + second.getName() + " are buddies.");
    }
    public static void minmaxBonus(Manager[] a, Pair<? super Manager> result) {
        if (a.length == 0) return;
        Manager min = a[0];
        Manager max = a[0];
        for (int i = 1; i < a.length; i++) {
            if (min.getBonus() > a[i].getBonus()) min = a[i];
            if (max.getBonus() < a[i].getBonus()) max = a[i];
        }
        result.setFirst(min); // public void setFirst(T newValue) { first = newValue; }
        result.setSecond(max);
    }
    public static void maxminBonus(Manager[] a, Pair<? super Manager> result) {
        minmaxBonus(a, result);
        swapHelper(result); // SwapHelper captures wildcard type.
    }
    // Can't write public static <T super Manager> , but <T extends Manager> can.
    public static <T> void swapHelper(Pair<T> p) {
        //? t = p.getFirst(); // "?" not allowed in this context, "?" is not a type.
        T t = p.getFirst();
        p.setFirst(p.getSecond());
        p.setSecond(t);
    }
    public static boolean hasNulls(Pair<?> p) {
        return p.getFirst() == null || p.getSecond() == null;
    }
    public static void swap(Pair<?> p) {
        swapHelper(p);
    }
//}
}

/**
 * Bounds of Wildcards
 */
class BoundsOfWildcards {
/*
Capture conversion on wild types depends on bounds of wildcards and bounds of type-parameters.
 A wildcard is either upper-bounded or lower-bounded, but not both. By default, a wildcard is upper-bounded by Object.
 In Java, type-parameters (T) have no lower bounds.
    public interface List<T>  // i.e. List<T extends Object>
    public class Fooo<T extends Appendable> implements List<T> {}
  Fooo<StringBuilder> is legal, because StringBuilder<:Appendable. But Fooo<String> would be illegal.

Upper-bounded wildcard.
    Fooo<? extends CharSequence>
    List<?> i.e. List<? extends Object>
 During capture conversion, an upper-bounded wildcard is replaced by a new type-variable, which takes the upper bound of the wildcard, and the upper bound of the type-parameter (symbol "⇒c" to indicate capture conversion).
    Fooo<? extends CharSequence> ⇒c Fooo<X>
  where X <: CharSequence & Appendable, example: X = StringBuilder
    List<?> ⇒c List<X>
  where X <: Object & Object, i.e. X <: Object, i.e. any X

Lower-bounded wildcard.
    Fooo<? super StringBuilder>
    List<? super String>
 During capture conversion, a lower-bounded wildcard is replaced by a new type-variable, which takes the lower bound of the wildcard, and the upper bound of the type-parameter.
    Fooo<? super FileWriter> ⇒c Fooo<X>
  where FileWriter <: X <: Appendable
    List<? super String> ⇒c List<X>
  where String <: X <: Object

Capture conversion explains how we can operate on values of such wild types:
    void test(Fooo<? super FileWriter> fooo, List<? super String> list) {
        Appendable a = fooo.get(0); // returns X1, X1<:Appendable
        list.add("abc");            // add(X2), String<:X2
    }

A wild type may contain one or more wildcard arguments; each wildcard is captured separately:
    Map<String, ? super Integer> ⇒c Map<String, X>
  where Integer<:X
    Function<? super Integer, ? extends CharSequence> ⇒c Function<X1, X2>
  where Integer<:X1 and X2<:CharSequence

*/
}

class Subtyping {
/*
concrete <: concrete
 A concrete type is a subtype of another concrete type, if there's an inheritance relationship, or if they are the same type:
    List<A> <: List<B>  iff  A=B
    ArrayList<A> <: List<A> <: Iterable<A>   due to inheritance

concrete <: wild
 A concrete type is a subtype of a wild type, if it satisfies the capture conversion.
    Fooo<? super FileWriter> ⇒c Fooo<X>
  where FileWriter <: X <: Appendable
 Actually, there is a simpler way, we only need to test the bound of the wildcard:
    G<B> <: G<? extends A>  iff  B <: A
    G<A> <: G<?  super  B>  iff  B <: A

wild <: concrete
 A wild type is a subtype of a concrete type, if the capture conversion is. That may sound odd, but consider:
    ArrayList<?> ⇒c ArrayList<X>
  where X<:Object
    ArrayList<?> <: Cloneable only because ArrayList<X> <: Cloneable
 Or consider this example:
    public class MyList<T, V> extends ArrayList<T>{}
    --
         MyList<Integer, X>  <:  Iterable<Integer>
    =>   MyList<Integer, ?>  <:  Iterable<Integer>
 The capture conversion represents every concrete type in the wild type; if every concrete type is a subtype of some type Z, the wild type must be a subtype of Z. This reasoning applies to the next section too.

wild <: wild
 Wild type Wa is a subtype of Wb, if the capture conversion of Wa is a subtype of Wb.
 For example, List<? extends Exception> <: Iterable<? extends Throwable>, because:
    List<? extends Exception> ⇒c List<X>
  where X<:Exception
    List<X> <: Iterable<X> <: Iterable<? extends Throwable>
 If both wild types are of the same generic class/interface, we can simply check the bounds of the wildcards:
    G<? extends B> <: G<? extends A>  if (if, not iff) B<:A
    G<?  super  A> <: G<?  super  B>  if (if, not iff) B<:A
    G<?  super  B> <: G<?> for any B
*/
}

/**
 * Nested Wildcards
 */
class NestedWildcards {
/*
Capture conversion applies only on top level wildcards, not on nested wildcards.
    List<? extends Set<? extends Number>> ⇒c List<X>
  where X <: Set<? extends Number>
 List<Set<?>> is a concrete type with well defined methods; it's not a wild type that requires capture conversion. List<Set<?>> is not the supertype of all List<Set<X>>; such supertype does not exist in Java.
 A wild type is a type, therefore it can be used in substitutions. For example, substitute T→Set<?> on List<T>.
    interface List<Set<?>>
    {
        int     size();
        Set<?>  get(int);
        void    add(Set<?>);
      ...
    }
*/
}

/**
 * Variance
 */
class Variance {
/*
We say that upper-bounded wildcards are co-variant, and lower-bounded wildcards are contra-variant, in the sense that:
    B <: A   ⇒   G<? extends B> <: G<? extends A>
    B <: A   ⇒   G<?  super  A> <: G<?  super  B>

Co-variance.
 Intuitively, a Supplier (https://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html) of Integer is kind of a Supplier of Number, because when it supplies an Integer, it supplied a Number too. We say that Supplier is "intuitively co-variant". Unfortunately in Java, it is not the case that:
    B <: A   ⇒   Supplier<B> <: Supplier<A>  // nope!
 Therefore we cannot use a Supplier<Integer> where a Supplier<Number> is expected. That is very counter-intuitive.
 The workaround is to use upper-bounded wildcard for its co-variant nature:
    B <: A ⇒ Supplier<? extends B> <: Supplier<? extends A>
 Intuitively co-variant types are almost always used with upper-bounded wildcards, particularly in public APIs. If you see a concrete type Supplier<Something> in an API, it is very likely a mistake.

Contra-variance.
 Intuitively, a Consumer (https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html) of Number is kind of a Consumer of Integer, because if it can consume any Number, it can consume any Integer too. We say that Consumer is "intuitively contra-variant".
 Intuitively contra-variant types are almost always used with lower-bounded wildcards, particularly in public APIs:
    B <: A ⇒ Consumer<? super A> <: Consumer<? super B>

Variance on type-parameters.
 More precisely speaking, "intuitively variant" is a property on type-parameters - Supplier<R> is intuitively co-variant on R, Consumer<T> is intuitively contra-variant on T.
 Function<T,R> (https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html) consumes T and supplies R, therefore it is intuitively contra-variant on T and co-variant on R. The Function type is almost always used with two wildcards correspondingly, particularly in public APIs:
    Function<? super Foo, ? extends Bar>

Use-site variance.
 List<T> both consumes T and supplies T; it is neither intuitively co-variant nor intuitively contra-variant.
 But, we can use List<? extends Foo> to use the type in a co-variant sense, i.e. to see the list only as a supplier of Foo.
 Or, we can use List<? super Foo> to use the type in a contra-variant sense, i.e. to see the list only as a consumer of Foo.
 Therefore in Java, it is the use-site that chooses whether to use a type in a co-variant or contra-variant sense.
 Of course, if a type is intuitively co-variant or contra-variant, the use-site generally shouldn't make an opposite choice. It rarely makes sense to write types like Supplier<? super Foo> or Consumer<? extends Foo>.
*/
}

/**
 * Wildcard Hell
 */
class WildcardHell {
/*
Java Generics was designed with a heavy biased towards Collection framework, leading to some controversial decisions, one of which being use-site variance through wildcard. The decision did make a lot of sense back then - use-site variance is pretty neat for collection interfaces; wildcard usages were moderate and manageable.
Since Java 8, functional interfaces (Consumer, Function, etc.) are used in a lot of APIs; most of these interfaces are intuitively variant, therefore they are almost always used with wildcards. Consequently, wildcard usages exploded.
The syntax of wildcard is quite ugly and distracting, especially for types that are slightly more complex, for example:
    Function<? super A, ? extends Function<? super B, ? extends C>>
What's worse, these wildcards are frivolous - they are required, yet they convey no real meanings. Use-site variance is of very little value for intuitively variant types; it just becomes a nuisance.
*/
}

/**
 *  Wildcard Case Studies
 */
class WildcardCaseStudies {
    class CaptureHelper {
        // Some API function upper limited to Number
        void reverse(List<? extends Number> list)
        {
            // difficult to work with wild type
            // forward it to helper method

            rev(list);
        }
        // to be able to use it as Supplier and Consumer, i.e. to be able to get from and add to the List we use helper function rev. So say someone (API client) is using the reverse function with type Integer like this "reverse(List<Integer> list)". From now on we know that what type it is and we can pass it to the rev function which accept only one possible type, not a range of them.
        <T> void rev(List<T> listT)
        {
            // read from and write back to `listT`
            // which is easy on a concrete type
            //...
        }
    }

    class MapEntrySet {
        /*
        Given a (Map<?,?> map), we can iterate its entries like:
            for(Entry<?,?> entry : map.entrySet())
        This is deceptively simple, but we know it's more complex under the hood. First, map is capture converted to Map<X1, X2>, therefore, entrySet() returns Set<Entry<X1, X2>>. The entry variable can be declared as Entry<?,?>, because it's a supertype of Entry<X1, X2>.
        Following that example, someone may try this:
            Set<Entry<?,?>> entrySet = map.entrySet(); // compile error
         It doesn't work because Set<Entry<X1, X2>> is not a subtype of Set<Entry<?,?>>.
         We can add more wildcards to solve the problem, see Nested Wildcards and Subtyping.
            Set<? extends Entry<?,?>> entrySet = map.entrySet();
            Iterator<? extends Entry<?,?>> iterator = entrySet.iterator();
         But it is becoming too messy. A simpler way is to use a capture helper to do away with wildcards.
        */
        void foo(Map<?,?> map)
        {
            for (Map.Entry<?,?> entry : map.entrySet()) {}
            foo2(map);
        }
        <K,V> void foo2(Map<K,V> map)
        {
            Set<Map.Entry<K,V>> entrySet = map.entrySet();
            Iterator<Map.Entry<K,V>> iterator = entrySet.iterator();
            //...
        }
    }

    class InferredWildType {
        /*
        The compiler often needs to infer a common supertype from two or more subtypes. For example, the type of a conditional expression (without target typing):
            condition ? expr1 : expr2
         For Exception and Error, the supertype is inferred as Throwable.
         If the subtypes are parameterized types, the supertype may be inferred as a wild type. For example, if expr1 is List<Exception>, and expr2 is List<Error>, the type of the conditional expression is inferred as List<? extends Throwable> (which is then immediately capture converted).
        The algorithm to infer the supertype is Least Upper Bound (https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.10.4) which is very complicated, and may yield a very  complicated wild type. However in most use cases, the programmer doesn't need to know the exact outcome of the algorithm.
        Let's see another example, if we invoke the method Arrays.asList:
            public static <T> List<T> asList(T... a)
         with two arguments, one is Set<Exception> and another is Set<Error>, T will be inferred as Set<? extends Throwable>, and the method will return List<Set<? extends Throwable>>.
        */
    }

    class SupertypesOfInteger {
        /*
        What are the supertypes of java.lang.Integer? From the class definition, Integer inherits Object, Number, Serializable, Comparable<Integer>; these are all supertypes of Integer. Integer is also a supertype of itself.
         But that's not all. Comparable<Integer> has supertypes that are wild types. In fact:
            Comparable<Integer> <: Comparable<? extends A>  iff  Integer<:A
         Therefore, for every supertype of Integer, we can construct another supertype, and so on.
        It is also the case that:
            Comparable<Integer> <: Comparable<? super B>  iff  B<:Integer
         however, since Integer is final, B can only be Integer itself.
        Therefore the following examples are all supertypes of Integer:
            Comparable<? extends Integer>
            Comparable<? extends Comparable<? extends Number>>
            Comparable<? extends Comparable<? extends Comparable<? super Integer>>>
        */
    }

    class CaptureEverywhere {
        /*
        This is a contrived example demonstrating pervasive capture conversions:
            List<?> list = ...;
            ( condition ? list : list ).get(0);
        The two list sub-expressions are capture converted first, to List<X1> and List<X2> respectively. The type of the parent conditional expression is then derived as the least upper bound of these two concrete types, which is List<? extends Object>. This type is then capture converted to List<X3>, therefore get(0) returns X3.
        */
    }

    class UnexpectedCapture {
        /*
        Capture conversion is applied everywhere. This may surprise the programmer sometimes, because the type of an expression may not be what it seems to be. For example, the following code does not compile:
            Class<?> c1 = ...;
            Class<?> c2 = ...;
            Collections.singleton(c1).add(c2); // compile error
        Collections.<T>singleton takes a T argument and returns a Set<T>. Naively, for (Class<?> c1) argument, we may expect that the method returns Set<Class<?>>, therefore add(c2) should work.
        But actually, the c1 argument undergoes capture conversion first, changing its type to Class<X1>. Therefore T is inferred as Class<X1>, and singleton(c1) returns Set<Class<X1>>. The add() method then has the signature add(Class<X1>), which does not accept the c2 argument, which has the type (after capture) Class<X2>.
        There is no way to suppress capture conversion here. A workaround is to explicitly specify T→Class<?>:
            Collections.<Class<?>>singleton(c1).add(c2);  // compiles! (but fails at runtime:)
        https://stackoverflow.com/questions/30991884/java-generics-method-not-applicable-to-mockito-generated-stub
        */
    }

    class UnexpectedNonCapture {
        /*
        Capture conversion only affects wild types; on any other type, the type is left as is. For example:
                <T> void rev(List<T> list){ }
                <L extends List<?>> void rev(L list)
                {
            #a      rev((List<?>)list);  // ok

            #b      rev(list);           // compile error
                }
         Line#a compiles, because the argument type is List<?>, which is capture converted to List<X>, and type inference concludes that T=X for rev().
         Line#b does not compile, because the argument type is L, which is not subject to capture conversion, even though it is-a List<?>. Type inference then fails -- no T can be found so that L <: List<T>.
        This is a curious example where a subtype (L) cannot be used where a supertype (List<?>) works; an upcast is required. This might be an oversight of the spec and may be fixed in future.
        https://stackoverflow.com/questions/30622759/inference-variable-has-incompatible-bounds-java-8-compiler-regression/30624513#30624513
        */
    }

    class CaptureConversionComplexType {
        /*
        In most cases, capture conversion is pretty simple, like List<?> ⇒c List<X>.
        However, it gets a little more complicated for more complex type parameters. The classic example is Enum<E>:
            class Enum<E extends Enum<E>>
        The bound of E references itself. During capture conversion, the bound of E needs to be substituted too:
            Enum<? extends Runnable> ⇒c Enum<X>
          where X <: Runnable & Enum<X>
        */
    }

    class SubtypingAnalysis {
        /*
        Subtyping analysis requires capture conversion on wild types. For example, given this generic declaration:
            interface Data<T> extends List<List<T>>
        To analyze subtyping between wild types of Data and List:
            Data<? extends Number> ⇒c Data<X>
          where X<:Number
          Data<X>
            <: List<List<X>>
                <: List<? extends List<X>>
                    <: List<? extends List<? extends Number>>
          Data<? extends Number>
            <: List<? extends List<? extends Number>>
        Note that Data<?> is not a List<List<?>>, see Nested Wildcard.
        */
    }

    class CastingBetweenConcreteTypes {
        /*
        Suppose for some reason we have to cast a Supplier<Integer> to Supplier<Number>, which we know is safe at runtime because of type erasure. Direct casting is forbidden:
            Supplier<Integer> supplier = ...;
            (Supplier<Number>)supplier // error!
         which makes perfect sense because the two types are mutually exclusive, therefore casting is provably incorrect.
        Concrete types are mutually exclusive, meaning List<A> and List<B> (A≠B) share no objects. For example, an object cannot be both an instance of List<Number> and List<Integer>.
        But we want to force the cast anyway; this can be done by up-casting to a common supertype first, followed by down-casting. Obviously, we can always use Object as the supertype between any two types:
            (Apple)(Object)orange
            (Supplier<Number>)(Object)supplier
        But it's probably a good practice to use a supertype that's closer to the two types:
            (Supplier<Number>)(Supplier<? extends Number>)supplier
        If that's too ugly, maybe use an unbounded wildcard:
            (Supplier<Number>)(Supplier<?>)supplier
        This is about on-site casting. For other ways of conversions, see Missing Wildcards.
        */
    }

    class MissingWildcards {
        /*
        It is not uncommon that a method signature forgets to include proper wildcards. Such mistakes exist even in core Java APIs which were designed under a lot of scrutiny. Interestingly, it seldom causes any problems to API users; and, when problems do arise, there are simple workarounds.
        This observation can be used to justify deliberately omitting wildcards in APIs, to escape from Wildcard Hell.
        Consider the following method signature which lacks proper wildcards:
            void foo(Function<String, Number> func)
         we won't be able to pass compatible functions to it:
            Function<CharSequence, Integer> f = ...;
            foo(f); // error!
        However, it usually isn't a problem, because in most use cases, the argument is a lambda expression or method reference, and compiler type inference takes care of providing the desired target type:
            Integer str2int(CharSequence chars){ }
            foo(str->str2int(str));  // ok!
            foo(this::str2int);      // ok!
        If the argument is an expression of a fixed static type, in majority cases it happens to be the right type - the target type.
        In rare cases, like foo(f), the argument type doesn't match, and we need to convert it. On-site casting is possible, but way too ugly. For functional interfaces, conversion can be done simply by method reference:
            Function<CharSequence, Integer> f = ...;
            foo(f);         // error!
            foo(f::apply);  // ok!
        However, the purpose of f::apply might be unclear to readers of the code. Personally, I'd like to use a help method to make the intent clear that I'm doing a variant cast:
            foo(vary(f));
            @SuppressWarnings("unchecked")
            public static <T,R> Function<T,R> vary(Function<? super T, ? extends R> f0) {
                return (Function<T,R>)f0;  // erasure
            }
        The implementation of vary() depends on type erasure; it is perfectly safe at runtime, because method f0.apply() indeed accepts any T and returns an R, therefore f0 can be safely used as a Function<T,R>.
        Similarly, we can define a vary() method for each functional interface that's intuitively variant, like Consumer etc.
        For interfaces like List, we'll need two helper methods, one for covariant cast, and one for contravariant.
            public static <T> List<T> covary(List<? extends T> list) { return (List<T>)list; }
            double sum(List<Number> list) // no wildcard!
            // usage
            List<Integer> intList = ...;
            sum(covary(intList));
         If we are sure that sum() wants a list of Number in a covariant sense, we can safely pass it a List<Integer> this way.
        */
    }

    class TightBound {
        /*
        ? extends String.
        Occasionally we may encounter types like List<? extends String>, with a wildcard upper-bounded by a final class. It's unlikely that the programmer voluntarily constructs a type like that. Most likely it's imposed by a List<? extends T> in some generic API which happens to be substituted with T→String.
        List<? extends String> seems odd. Since String is a final class, the only subtype is String itself. Therefore it's tempting to think that List<? extends String> is the same as List<String>.
        However, as far as the compiler is concerned, they are two distinct types. This is because final is not final; it is possible that someday String class removes the final modifier, and new subclasses of String are introduced.
        Therefore, List<? extends String> ≠ List<String>; in particular, this subtyping is false:
            List<? extends String> <: List<String>
        Although, as a practical matter, if we have to, we may cast List<? extends String> to List<String>, reasoning that it must be safe at runtime, as long as String stays final.

        ? super Object.
        On the other hand, what about List<? super Object>? The only supertype of Object is Object itself, therefore, aren't List<? super Object> and List<Object> the same type?
        If two types are subtypes of each other, they are the same type, or at least, they are semantically equivalent. So the question is whether the subtyping algorithm agrees that:
            List<? super Object> <: List<Object>
         which depends on whether the subtyping is true on the capture conversion:
            List<X> <: List<Object> for any X that Object<:X
        The reasonable answer is yes. And javac seems to agree that List<? super Object> = List<Object>. For example the following code compiles without any problem:
            void test(Set<List<? super Object>> x, Set<List<Object>> y) {
                x = y;
                y = x;
            }
        */
    }

    class LooseBound {
        /*
        We have the following subtyping property:
            G<? extends B> <: G<? extends A>  if  B<:A
        However, the converse may not be true, because we also have to consider the bound of the type-parameter. For example, consider this generic declaration:
            class Foo<T extends Exception>
        The following wild types are all equivalent, and they are subtypes of each other:
            Foo<? extends Object>
            Foo<? extends Throwable>
            Foo<? extends Serializable>
            Foo<? extends Exception>
         because, the wildcard bounds provide no more restriction than the type-parameter bound. During capture conversion,
            Exception = Exception&Throwable = Exception&Serializable
         In essence, what's really important to a wild type is its capture conversion, not its syntactic form.
         */
    }
}