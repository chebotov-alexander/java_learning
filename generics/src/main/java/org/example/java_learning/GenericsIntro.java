package org.example.java_learning;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static org.example.java_learning.Util.println;

/**
 * Generics Introduction
 */
public class GenericsIntro {
    // !!!
    // This is a generic class.
    class GenericClass<T> {
    //  and this is not a generic method, so it must be declared in a generic class with a type parameter T like it is.
        void notGenericMethod(Collection<T> c) {}
    }
    // This generic method returns nothing.
    <T> void genericMethodRetunrsNothing(Collection<T> c) {}
    // This returns something
    static <T> T genericMethod(T... a) { return a[0]; }
    // Generic methods are invoked like regular non-generic methods. Type arguments for generic methods need not be provided explicitly; they are almost always automatically inferred from the invocation context.
    // If the compiler can infer the type, the invocation would be:
    String implicit = genericMethod("John", "Q.", "Public");
    // But if the compiler struggles to infer its type, the invocation would be like this:
    Double explicit = GenericsIntro.<Double>genericMethod(3.14, 1729D, 0D);
    // Here is the example of a generic max method that computes the greatest value in a collection of elements of an unknown type T. The max method has one type parameter, named T. It is a placeholder for the element type of the collection that the method works on. The type parameter has a bound; it must be a type T that is a subtype of Comparable<T>, i.e., a type that can be compared to elements of itself.
    public static <T extends Comparable<T>> T max(Collection<T> xs) {
        Iterator< T > xi = xs.iterator();
        T w = xi.next();
        while (xi.hasNext()) {
            T x = xi.next();
            if (w.compareTo(x) < 0) w = x;
        }
        return w;
    }
    LinkedList<Long> list = new LinkedList<>(Arrays.asList(0L, 1L));
    // The compiler would automatically invoke an instantiation of the max method with the type argument {Long}, that is, the formal type parameter {T} is replaced by type {Long}. The compiler automatically infers the type argument by taking a look at the type of the arguments provided to the method invocation. The compiler finds that a {Collection<T>} is asked for and that a {LinkedList<Long>} is provided. From this information the compiler concludes at compile time that {T} must be replaced by {Long}.
    Long maxElement = GenericsIntro.max(list);

    // !!Generics were added to the Java language to enforce type safety at compile time.
    // !Java generics are one of the most complex parts of the language specification with a lot of potential corner cases, which not every developer needs to fully understand. A major design goal was to be backwards compatible with earlier releases. As a result, Java generics have some uncomfortable limitations.
    // The history under generics. Before them one has to cast an object stored in some collection before using it as an instance of a concrete class. A Collection for example, List doesn’t know what type of objects it contains. So, it’s actually possible to put different types of objects into the same collection and everything will work fine until an illegal cast is used, and the program crashes.
    public static void executeGenericHistory() {
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
    // The syntax <T> has a special name — it’s called a type parameter. This should convey the sense that the container type (e.g., List) is parameterized by another type (the payload type). When we write a type like Map<String, Integer>, we are assigning concrete values to the type parameters.
    // !!Type parameters always stand in for reference types. It is not possible to use a primitive type as a value for a type parameter.

    // A generic class is a class with one or more type variables.
    static class Pair<T> {
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

    //	It is possible to define a method(s) with type parameters both inside ordinary classes and inside generic classes.
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

    static class GenericsRestrictions {
    /*
    Restrictions:
     1. Cannot Instantiate Generic Types with Primitive Types.
     2. Cannot Create Instances of Type Parameters.
     3. Cannot Declare Static Fields Whose Types are Type Parameters.
     4. Cannot Use Casts or instanceof With Parameterized Types.
     5. Cannot Create Arrays of Parameterized Types.
     6. Cannot Create, Catch, or Throw Objects of Parameterized Types.
     7. Cannot Overload a Method Where the Formal Parameter Types of Each Overload Erase to the Same Raw Type.
    */
    // 1. Cannot instantiate generic types with primitive types.
        static void noPrimitives() {
            //PairDistinct<int, char> pair_1 = new PairDistinct<>(8, 'a'); // Compile-time error: "Type argument cannot be of primitive type".
            PairDistinct<Integer, Character> pair_2 = new PairDistinct<>(8, 'a');
            // Under the hood it's actually autoboxed like this:
            PairDistinct<Integer, Character> pair_3 = new PairDistinct<>(Integer.valueOf(8), Character.valueOf('a'));
        }
    // 2. Cannot create instances of type parameters.
        static <E> void noTypeParameterInstance(List<E> list) {
            //E elem = new E(); // Compile-time error "Type parameter 'E' cannot be instantiated directly".
            //list.add(elem);
        }
    //  As a workaround (very limited use if ever), creating an object of a type parameter through reflection:
        static <E> void typeParameterInstanceWithReflection(List<E> list, Class<E> cls) throws Exception {
            E elem = cls.newInstance();
            list.add(elem);
        }
        static void testTypeParameterInstanceWithReflection() throws Exception {
            List<String> ls = new ArrayList<>();
            typeParameterInstanceWithReflection(ls, String.class);
        }
    // 3. Cannot declare static fields whose types are type parameters.
    /*  A class's static field is a class-level variable shared by all non-static objects of the class. Hence, static fields of type parameters are not allowed. Consider the following class:
        public class MobileDevice<T> {
            private static T os;
            // ...
        }
       If static fields of type parameters were allowed, then the following code would be confused:
        MobileDevice<Smartphone> phone = new MobileDevice<>();
        MobileDevice<Pager> pager = new MobileDevice<>();
        MobileDevice<TabletPC> pc = new MobileDevice<>();
       Because the static field os is shared by phone, pager, and pc, what is the actual type of os? It cannot be Smartphone, Pager, and TabletPC at the same time. You cannot, therefore, create static fields of type parameters.
    */
    // For more see:
        static void staticMembers() {
            StaticMembers.testStaticMembers();
        }
    // 4. Cannot use casts or instanceof with parameterized types.
    //  instanceof requires a reifiable type. Because of type erasure mechanism the runtime does not keep track of type parameters, so it cannot tell the difference between an ArrayList<Integer> and an ArrayList<String>.
        static <E> void noInstanceof(List<E> list) {
            //if (list instanceof ArrayList<Integer>) {} // Compile-time error: "'List<E>' cannot be safely cast to 'ArrayList<Integer>'".
        }
    //  The most you can do is to use an unbounded wildcard to verify that the list is an ArrayList:
        static void instanceofWithUnboundedWildcard(List<?> list) {
            if (list instanceof ArrayList<?>) { }
        }
    //  Typically, you cannot cast to a parameterized type unless it is parameterized by unbounded wildcards:
        static void castMore() {
            List<Integer> list_1 = new ArrayList<>();
            //List<Number> list_2 = (List<Number>) list_1; // Compile-time error: "Inconvertible types; cannot cast 'List<Integer>' to 'List<Number>'".
            // However, in some cases the compiler knows that a type parameter is always valid and allows the cast:
            List<String> list_3 = new ArrayList<>();
            ArrayList<String> list_4 = (ArrayList<String>) list_3;
            // More on inheritance and casting see:
            GenericsInheritance.testGenericsInheritance();
        }
    // 5. Cannot create arrays of parameterized types.
    //  It is not type-safe, so no way. For mo see:
        static void noArraysOfGenerics() {
            ArraysOfGenerics.testArraysOfGenerics();
        }
    // 6. Cannot create, catch, or throw objects of parameterized types.
        /*
        Exception and error types must not be generic. It is illegal to define generic type that are directly or indirectly derived from class Throwable. Consequently, no parameterized types appear anywhere in exception handling.
        The virtual machine cannot distinguish between different instantiations of a generic exception type.
           ...
           catch (IllegalArgumentException<String> e) { ... } // illegal
           catch (IllegalArgumentException<Long> e) { ... } // illegal
           ...
        Taking into account that generic Java source code is translated to Java byte code by type erasure, it should be clear that the method's catch clauses do not make any sense.  Both parameterized exception types have the same runtime type and the mechanism for catching exceptions is a runtime mechanism performed by the virtual machine based on the non-exact runtime types.  The JVM has no chance to distinguish between different instantiations of the same generic (exception) type.  For this reason, generic exception and error types are pointless in Java and banned from the language. Note that generic exception and error types are not pointless per se, but in the context of Java generics with type erasure their are nonsensical.
        */
        static void noExceptionHandling() {
            // Extends Throwable indirectly.
            //class MyException<T> extends Exception {} // Compile-time error: "Generic class may not extend 'Throwable'".
            // Extends Throwable directly.
            //class MyOtherException<T> extends Throwable {} // Compile-time error: "Generic class may not extend 'Throwable'".

            // A method cannot catch an instance of a type parameter.
            execute(Arrays.asList("a", "b", "c"));

            // You can, however, use a type parameter in a {throws} clause.
            class Parser<T extends Exception> {
                void parse(String source, T exception) throws T { throw exception; }
                void testParse(String source, T exception) {
                    try {
                        parse(source, exception);
                    }
                    //catch (T ex) {} // Compile-time error: "Cannot catch type parameters".
                    catch (Exception ex) { println(ex); }
                }
            }
            Parser parser = new Parser();
            parser.testParse(" ", new IOException());
        }
        public static <T extends Exception, J> void execute(List<J> jobs) {
            try { for (J job : jobs) {} }
            //catch (T e) {} // Compile-time error: "Cannot catch type parameters".
            catch (Exception e) {}
        }
    // 7. Cannot overload a method where the formal parameter types of each overload erase to the same raw type.
        class Example {
            //public void print(List<String> strSet) { } // Compile-time error: "'print(List<String>)' clashes with 'print(List<Integer>)'; both methods have same erasure".
            //public void print(List<Integer> intSet) { }
            // After erasure the overloads would all share the same classfile representation.
            //public void print(List strSet) { }
            //public void print(List strSet) { }
        }

        static class ArraysOfGenerics {
        /*
         It is not type-safe, so no way.
         Arrays are covariant, which means that an array of supertype references is a supertype of an array of subtype references. That is, Object[] is a supertype of String[] and a string array can be accessed through a reference variable of type Object[] .
         In addition, arrays carry runtime type information about their component type, that is, about the type of the elements contained. The runtime type information regarding the component type is used when elements are stored in an array in order to ensure that no "alien" elements can be inserted.
         An unbounded wildcard parameterized type is a reifiable type and arrays with a reifiable component type can be created. Concrete and bounded wildcard parameterized types are non-reifiable types and arrays with a non-reifiable component type can not be created. As a result, an array variable with a reifiable component type can refer to array of its type, but this is not possible for the non-reifiable component types. An unbounded wildcard parameterized types are permitted as component type of an array, while other instantiations are not permitted. In the case of a non-reifiable component type the array reference variable can be declared, but it cannot refer to an array of its type. At most it can refer to an array of a non-parameterized subtype (or an array of the corresponding raw type), which opens opportunities for mistakes, but does not offer any advantage.
        */
            static void testArraysOfGenerics() {
                // Arrays hold elements of the same type or subtypes.
                Object[] arrOfObj = new String[10];
                arrOfObj[0] = new String();
                //arrOfObj[0] = Long.valueOf(0L); // Runtime error: "ArrayStoreException" due to "array store check".
                // Because of type erasure, parameterized types do not have exact runtime type information. As a consequence, the array store check does not work because it uses the dynamic type information regarding the array's (non-exact) component type for the array store check.
                // Can be declared, but the creation of such an array is rejected.
                Pair<Integer>[] arrOfPair = null;
                //arrOfPair = new Pair<Integer>[10]; // Compile-time error: "Generic array creation not allowed".
                //arrOfPair = new Pair<? extends Integer>[10]; // Compile-time error: "Generic array creation not allowed".
                //arrOfObj = arrOfPair;
                //arrOfObj[0] = new Pair<String>("", ""); // Should fail, but would succeed.
                //Integer i = arrOfPair[0].getFirst(); // Runtime error: "ClassCastException".
                // If arrays of concrete parameterized types were allowed, then a reference variable of type {Object[]} could refer to a {Pair<Integer>[]}, as shown in the example. At runtime an array store check must be performed when an array element is added to the array. Since we are trying to add a {Pair<String>} to a {Pair<Integer>[]} we would expect that the type check fails. However, the JVM cannot detect any type mismatch here: at runtime, after type erasure, {arrOfObj} would have the dynamic type {Pair[]} and the element to be stored has the matching dynamic type {Pair}. Hence the store check succeeds, although it should not.
                PairSubTypeTest.test();
            }
        // Conceivable workarounds:
        //  1. array of raw type;
        //  2. array of unbounded wildcard parameterized type;
        //  3. collection instead of array.
            static void populateWithMixTypes(Object[] objArr) {
                objArr[0] = new Pair<Integer>(0, 0);
                objArr[1] = new Pair<String>("", ""); // Succeeds, but should fail because unsafe.
            }
        // 1. Array of raw type.
            static void testArrayOfRawType() {
                Pair[] intPairArr = new Pair[10] ;
                populateWithMixTypes(intPairArr);
                Pair<Integer> pair = intPairArr[1]; // Unchecked warning.
                Integer i = pair.getFirst(); // Runtime error: "ClassClassException".
                pair.setSecond(i);

                // Can be done, but not type-safe.
                Object[] rawPairArr = new Pair[10];
                rawPairArr[0] = new Pair(0L, 0L);
                rawPairArr[0] = new Pair("", "");
                rawPairArr[0] = new ArrayList<String>(); // Runtime error: "ArrayStoreException".
            }
        // 2. Array of unbounded wildcard parameterized type.
            static void testArrayOfUnboundedWildcard() {
                Pair<?>[] intPairArr = new Pair<?>[10];
                populateWithMixTypes(intPairArr);
                //Pair<Integer> pair = intPairArr[1]; // Compile-time error: "Required type: 'Pair<Integer>'; Provided: 'Pair<capture of ?>'".
                //Integer i = pair.getFirst();
                //pair.setSecond(i);

                // Can be done, but not type-safe.
                Object[] wildcardPairArr = new Pair<?>[10];
                wildcardPairArr[0] = new Pair<Long>(0L, 0L);
                wildcardPairArr[0] = new Pair<String>("", "");
                wildcardPairArr[0] = new ArrayList<String>(); // Runtime error: "ArrayStoreException".
            }
        // 3. Array of collections of concrete parameterized types.
            static void testArrayOfCollectionsOfConcreteParameterizedTypes() {
                ArrayList<Pair<Integer>> intPairArr = new ArrayList<Pair<Integer>>(10);
                populateWithCollectionsMixTypes(intPairArr);
                Pair<Integer> pair = intPairArr.get(1);
                Integer i = pair.getFirst();
                pair.setSecond(i);
            }
            static void populateWithCollectionsMixTypes(List<?> objArr) {
                //objArr.add(0, new Pair<Integer>(0, 0)); // Compile-time error: "Required type:'capture of ?'; Provided:'Pair<Integer>'".
                //objArr.add(1, new Pair<String>("", "")); // Compile-time error: "Required type:'capture of ?'; Provided:'Pair<String>'".
            }
            // As we can see, arrays of raw types and unbounded wildcard parameterized types are very different from the illegal arrays of a concrete parameterized type (like Pair<Integer>[10]). An array of a concrete wildcard parameterized type would be a homogenous sequence of elements of the exact same type. In constrast, arrays of raw types and unbounded wildcard parameterized type are heterogenous sequences of elements of different types. The compiler cannot prevent that they contain different instantiations of the generic type. By using arrays of raw types or unbounded wildcard parameterized types we give away the static type checks that a homogenous sequence would come with. As a result we must use explicit casts or we risk unexpected ClassCastException. In the case of the unbounded wildcard parameterized type we are additionally restricted in how we can use the array elements, because the compiler prevents certain operations on the unbounded wildcard parameterized type.
            //For the collections there is no such supertype as an Object[]. Type Collection<?>, or type List<?> in our example, comes closest to what the Object[] is for arrays. But wildcard instantiations of the collection types give only limited access to the collections' operations. A method like populateWithCollectionsMixTypes does not make any sense any longer; we would need a method specifically for a collection of Pair<Integer> instead.
            // A collection of a concrete parameterized type is a homogenous sequence of elements and the compiler prevents any attempt to add alien elements by means of static type checks. Arrays are covariant, while collections are not. They are not as efficient as arrays; they add overhead in terms of memory footprint and performance.
            // In essence, the most compelling argument against collections is efficiency; arrays are without doubt more efficient. The argument in favor of collections is type safety; the compiler performs all necessary type checks to ensure that the collection is a homogenous sequence.

            // More on array of raw type. Not recommended but can have the reference variable of type Pair<String>[] refer (it never refers to an array of its type) to an array of a non-parameterized subtype (raw type).
            static class PairSubType extends Pair<String> { public PairSubType(String first, String second) { super(first, second); } }
            Pair<String>[] arrOfPairSubType = new PairSubType[2];
            class PairSubTypeTest {
                static void test() {
                    Pair<String>[] arr1 = createArrayOfStringPairs1();
                    // Using a variable of type PairSubType[] would be much clearer.
                    PairSubType[] arr2 = createArrayOfStringPairs2(); // Raw type.
                    extractStringPairsFromArray1(arr1);
                    extractStringPairsFromArray2(arr2);
                    printArrayOfStringPairs(arr1);
                    printArrayOfStringPairs(arr2);
                }
                static Pair<String>[] createArrayOfStringPairs1() {
                    //Pair<String>[] arr = new Pair<String>[2]; // Forbidden at compile-time.
                    Pair<String>[] arr = new PairSubType[2];
                    arr[0] = new PairSubType("a", "b");
                    //arr[1] = new Pair<String>("a", "b"); // Runtime error: "ArrayStoreException".
                    return arr;
                }
                static PairSubType[] createArrayOfStringPairs2() {
                    PairSubType[] arr = new PairSubType[2] ;
                    arr[0] = new PairSubType("a", "b");
                    //arr[1] = new Pair<String>("a", "b"); // Compile-time error: "Required type:'PairSubType'; Provided:'Pair<java.lang.String>'".
                    return arr;
                }
                static void extractStringPairsFromArray1(Pair<String>[] arr) {
                    // Since a variable of type Pair<String>[] can never refer to an array that contains elements of type Pair<String>, when we want to recover the actual type of the array elements, which is the subtype PairSubType , we must cast down from Pair<String> to PairSubType.
                    PairSubType firstPair = (PairSubType) arr[0];
                    Pair<String> secondPair = arr[1];
                }
                static void extractStringPairsFromArray2(PairSubType[] arr) {
                    PairSubType firstPair = arr[0]; // No cast needed.
                    Pair<String> secondPair = arr[1];
                }
                static void printArrayOfStringPairs(Pair<String>[] arr) {
                    for (Pair<String> elm : arr) if (elm != null) println(elm.getFirst() + " " + elm.getSecond());
                }
            }

            // More on array of collections of concrete parameterized types.
            ArrayList<Pair<String>> pairList = new ArrayList<>();
            // For demonstration purposes Java array class could be like this.
            // version 0.1
            static class Array<T> {
                private T[] array;
                Array(int size) {
                    // The only way we can put an object into array is through the method set(), assuming getArray() is unavailable, and we only put object of type T inside. So it is safe to cast 'Object[]' to 'T[]'.
                    @SuppressWarnings("unchecked")
                    T[] a = (T[]) new Object[size];
                    this.array = a;
                }
                public void set(int index, T item) { this.array[index] = item; }
                public T get(int index) { return this.array[index]; }
                public T[] getArray() { return this.array; } // Not type safe and lead "ClassCastException" due to misuse.
            }
            static Array<String> array = new Array<>(4);
            static void testGetArrayItem() {
                Object[] objArray = array.getArray();
                objArray[0] = new ArrayList<String>();
                //array.set(0, new ArrayList<String>()); // Compile-time error: "Required type: String; Provided: ArrayList<String>".
                objArray[0] = 2;
                //array.set(0, 2); // Compile-time error: "Required type: String; Provided: int".
                array.get(0); // No error. Retrieved element of type {int} "stored" in object of type {Object}, which was created as this {Array<String> array = new Array<>(4)}.
                //array.set(0, array.get(0)); // Runtime error: "ClassCastException".
                //String element_1 = array.get(0); // Runtime error: "ClassCastException".
                //var element_2 = array.get(0); // Runtime error: "ClassCastException".
                objArray[1] = array.get(0);
                array.set(0, "2");
                var v1 = array.get(0);
                // If method Array::getArray not available then the only way someone can put something into the array is through the Array::set method which only put items of type T into array.
                // And now mixing raw types with parameterized types lead to errors.
                SomeDangerousRaw.populateArray(array);
                //String s = array.get(0); // Runtime error: "ClassCastException" and no compile-time errors, only "Unchecked warning" in SomeDangerousRaw::populateArray.
            }
            class SomeDangerousRaw {
                static void populateArray(Array arr) { arr.set(0, 1234); } // Unchecked call to 'set(int, T)' as a member of raw type 'Array'.
            }
            // !! So, do not use raw types. The only exception to this rule is using it as an operand of the instanceof operator. Since instanceof checks for run-time type and type arguments have been erased, we can only use the instanceof operator on raw types.
            // To sum up since an array reference variable whose component type is a concrete parameterized type can never refer to an array of its type, such a reference variable does not really make sense. Matters are even worse than in the example discussed above, when we try to have the variable refer to an array of the raw type instead of a subtype. First, it leads to numerous "unchecked" warnings because we are mixing use of raw and parameterized type. Secondly, and more importantly, this approach is not type-safe and suffers from all the deficiencies that lead to the ban of arrays of concrete instantiation in the first place.
            //No matter how you put it, you should better refrain from using array reference variable whose component type is a concrete parameterized type. Note, that the same holds for array reference variable whose component type is a wildcard parameterized type. Only array reference variable whose component type is an unbounded wildcard parameterized type make sense. This is because an unbounded wildcard parameterized type is a reifiable type and arrays with a reifiable component type can be created;  the array reference variable can refer to an array of its type and the deficiencies discussed above simply do not exist for unbounded wildcard arrays.
        }

        static class StaticMembers {
        /*
        Generic types can have static members {
         Generic types can have static members, including static fields, static methods and static nested types. Each of these static members exists once per enclosing type, that is, independently of the number of objects of the enclosing type and regardless of the number of instantiations of the generic type  that may be used somewhere in the program. The name of the static member consists - as is usual for static members - of the scope (packages and enclosing type) and the member's name. If the enclosing type is generic, then the type in the scope qualification must be the raw type, not a parameterized type.
         The reason is that the compiler translates the definition of a generic type into one unique byte code representation of that type. The different instantiations of the generic type are later mapped to this unique representation by means of type erasure. The consequence is that there is only one static count field in our example, despite of the fact that we can work with as many instantiations of the generic class as we like.

         By the same reason type parameter in any static context of a generic class is not allowed. Again the static context is independent of the type parameters and exists only once per raw type, that is, only once for all instantiations of a generic type.
            public final class X <T> {
                private static T field1;                          // Not allowed.
                private static SomeClass<T> field2;               // Not allowed.
                public  static T getField() { return field; }     // Not allowed.
                public  static void setField( T t) { field = t; } // Not allowed.
            }
         The attempt of declaring a static field of the unknown type T is non-sensical and rightly rejected by the compiler. There is only one instance of the static field for all instantiations of the generic class. Of which type could that static field possibly be?  The declaration of a static field, whose type is the type parameter, makes it look like there were several instances of different types, namely one per instantiation, which is misleading and confusing.  For this reason, the use of type parameters for declaration of static fields is illegal.
         As static methods often operate on static fields it makes sense to extend the rule to static methods: the type parameter must not appear in a static method.
         Interestingly, the same rule applies to static nested types defined in a generic class. There is no compelling technical reason for this restriction. It's just that static nested types are considered independent of any instantiations of the generic class, like the static fields and methods. For this reason, use of the type parameter in a static nested type is illegal (note, static nested types include nested static classes, nested interfaces and nested enum types).
        */
            static void testStaticMembers() {
                SomeClass<String> ref1 = new SomeClass<>();
                SomeClass<Long> ref2 = new SomeClass<>();
                SomeClass<?> ref3 = new SomeClass<>();
                ref1.count++; // Discouraged, but legal. Although we can refer to the static field through reference variables of different type, we access the same unique static field.
                ref2.count++; // Same.
                ref3.count++; // Same.
                //SomeClass<String>.count++; // Error: "Cannot resolve symbol 'count'".
                //SomeClass<Long>.count++; // Same error.
                //SomeClass<?>.count++; // Same error.
                SomeClass.count++; // Fine, recommended. The uniqueness of the static field is more clearly expressed when we refer to the static field using the enclosing scope instead of object references.
            }

            class Wrapper<T> {
                private final T theObject;
                public Wrapper(T t) { theObject = t; }
                public T getWrappedItem() { return theObject; }
                private static <A> A makeClone(A theObject) { return theObject; }
                public Mutable makeMutable() { return new Mutable(theObject); }
                //public Immutable makeImmutable() { return new Immutable(theObject); }
                public ImmutableGeneric ImmutableGeneric() { return new ImmutableGeneric(theObject); }
                public class Mutable {
                    private final T theObject;
                    public Mutable(T arg) { theObject = makeClone(arg); }
                    public T getWrappedItem() { return makeClone(theObject); }
                }
                public static final class Immutable {
                    // All with same error: "Cannot be referenced from a static context".
                    //private final T theObject;
                    //public Immutable(T arg) { theObject = makeClone(arg); }
                    //public T getWrappedItem() { return makeClone(theObject); }
                }
                // As a workaround we can generify the static class or interface itself, there is no such workaround for nested enum type because they cannot be generic though.
                public static final class ImmutableGeneric<A> {
                    private final A theObject;
                    public ImmutableGeneric(A arg) { theObject = makeClone(arg); }
                    public A getWrappedItem() { return makeClone(theObject); }
                }
            }
            static class SomeClass<T> {
                public static int count;
            }
        }
    }

    /**
     * Type Parameters Bounds.
     */
    public class GenericsTypeParametersBound {
    /*
    Parameter bound can be:
     - all classes;
     - interfaces;
     - enum types;
     - including:
      -- raw types;
      -- nested;
      -- inner types;
     - parameterized types, including:
      -- concrete parameterized types;
      -- bounded wildcard parameterized types;
      -- unbounded wildcard parameterized types;
    Neither primitive types nor array types be used as type parameter bound.
    For example:
         class X0 <T extends int> { } // Not allowed.
         class X1 <T extends Object[]> { } // Not allowed.
         class X2 <T extends Number> { }
         class X3 <T extends String> { }
         class X4 <T extends Runnable> { }
         class X5 <T extends Thread.State> { } // Enum type. Thread.State is an example of a nested type used as type parameter bound.
         class X6 <T extends List> { } // Raw type.
         class X7 <T extends List<String>> { } // Concrete parameterized type.
         class X8 <T extends List<? extends Number>> { } // Bounded wildcard parameterized type.
         class X9 <T extends Comparable<? super Number>> { } // Bounded wildcard parameterized type.
         class X10<T extends Map.Entry<?, ?>> { } // Unbounded wildcard parameterized type.
    A bound that is a wildcard parameterized type allows as type argument all types that belong to the type family that the wildcard denotes. The wildcard parameterized type bound gives only restricted access to fields and methods; the restrictions depend on the kind of wildcard.
    */
        class SomeClass<T extends List<? extends Number>> {
            public void someMethod(T t) {
                //t.add(Long.valueOf(0L)); // Compile-time error: "Required type: 'capture of ? extends Number'; Provided: 'Long'".
                Number n = t.remove(0);
            }
            public void testSomeClass() {
                SomeClass<ArrayList<Long>> x1 = new SomeClass<ArrayList<Long>>();
                //SomeClass<ArrayList<String>> x2 = new SomeClass<ArrayList<String>>(); // Compile-time error: "Type parameter 'java.util.ArrayList' is not within its bound; should implement 'java.util.List<? extends java.lang.Number>'".
            }
            // Reference variables of type T (the type parameter) are treated like reference variables of a wildcard type (the type parameter bound). The consequence is that the compiler rejects invocation of methods that take an argument of the "unknown" type that the type parameter stands for, such as {List.add}, because the bound is a wildcard parameterized type with an upper bound. At the same time the bound {List<? extends Number>} determines the types that can be used as type arguments. The compiler accepts all type arguments that belong to the type family {List<? extends Number>}, that is, all subtypes of List with a type argument that is a subtype of {Number}.
        }
    // A type parameter as a type parameter bound.
        class Triple<T> {
            private T fst, snd, trd;
            public <U extends T, V extends T, W extends T> Triple(U arg1, V arg2, W arg3) {
                fst = arg1;
                snd = arg2;
                trd = arg3;
            }
        }
    // A type parameter as part of its own bounds. The scope of a type parameter includes the type parameter section itself. Therefore, type parameters can appear as parts of their own bounds, or as bounds of other type parameters declared in the same section.
        class Wrapper1<T extends Comparable<T>> implements Comparable<Wrapper1<T>> {
            // In the example above, the type parameter T is used as type argument of its own bound Comparable<T>
            private final T theObject;
            public Wrapper1(T t) { theObject = t; }
            public T getWrapper() { return theObject; }
            public int compareTo(Wrapper1<T> other) { return theObject.compareTo(other.theObject); }
            // The first type parameter S is used as bound of the second type parameter T.
            public <S, T extends S> T wrap(S arg) { return null; }
            // The type parameter can be used in the entire type parameter section, not only after its point of declaration. The type parameter T is used in the type parameter section before it has been defined in the same type parameter section.
            public <S extends T, T extends Comparable<S>> T wrap(S arg1, T arg2) {
                return arg2.compareTo(arg1) == 0 ? arg1 : arg2;
            }
            // Forward references to types, not type parameters, are permitted also. The type Node is used (in the type parameter section of type Edge ) before it has been defined (probably in a different source file). This kind of forward reference permitted, which is not surprising. It is the usual way of defining and using types in Java.
            interface Edge <N extends Node <? extends Edge<N>>> {
                N getBeginNode();
                void setBeginNode(N n);
                N getEndNode();
                void setEndNode(N n);
            }
            interface Node <E extends Edge <? extends Node<E>>> {
                E getOutEdge();
                void setOutEdge(E e);
                E getInEdge();
                void setInEdge(E e);
            }
        }
    // The type parameter of an enclosing generic type or method can be used in the type parameter section of an inner generic type or method, i.e. the type parameters of a generic type or method can appear as parts of the bounds of the type parameters of any generic type or methods in that scope.
        public final class Wrapper2<T> {
            final T theObject;
            public Wrapper2(T t) { theObject = t; }
            // In constructor.
            public <U extends T> Wrapper2(Wrapper2<U> w) { theObject = w.theObject; }
            public T getWrapper() { return theObject; }
            // In inner class.
            final class WrapperComparator<W extends Wrapper2<? extends Comparable<T>>> implements Comparator<W> {
                public int compare(W lhs, W rhs) { return lhs.theObject.compareTo((T)(rhs.theObject)); }
            }
            public <V extends Wrapper2<? extends Comparable<T>>> Comparator<V> comparator() {
                return this.new WrapperComparator<V>();
            }
            // Similar rules apply to generic interfaces. Even the type parameters of a generic method can be used in the declaration of the type parameters of a local generic type.
            class Wrapper3 {
                private static <T> void method() {
                    // However, generic local classes are rather rare in practice.
                    class Local<A extends T> { }
                }
            }
        }
    // But if a type parameter is used as the bound of another type parameter then there must not follow any further bound.
        class Wrapper4<T> implements Cloneable {
            final T theObject = null;
            //<U extends T & Cloneable> Wrapper4<U> cloneObject() { return null; } // Compile-time error: "Type parameter cannot be followed by other bounds", i.e. T is followed by another bound (T & Cloneable), which is illegal.
        }
    /* So with the above exception the type parameters of a generic type or method can appear anywhere in the declaration of the type parameters of any generic type or methods in that scope. A type parameter T can appear:
     - as the bound, as in <U extends T>, or
     - as part of the bounds, as in <U extends Comparable<T>>, or
     - as the bound of a wildcard, as in <U extends <Comparable<? super T>>, or
     - as part of the bound of a wildcard, as in <U extends Wrapper<? extends Comparable<T>>>.
    */

    // At most one instantiation of the same generic type can appear in the list of bounds of a type parameter. The reason for this restriction is that there is no type that is a subtype of two different instantiations of the {Comparable} interface and could serve as a type argument. It is prohibited that a type implements or extends two different instantiations of the same interface. This is because the bridge method generation process cannot handle this situation.
        //class ObjectStore<T extends Comparable<T> & Comparable<String>> { } // Compile-time error: "Duplicate class: 'Comparable'".

    // A bound that is a class gives access to all its public members, that is, public fields, methods, and nested type. Only constructors are not made accessible, because there is no guarantee that a subclass of the bound has the same constructors as the bound.
        public class SuperClass {
            // static members
            public enum EnumType { THIS, THAT }
            public static Object staticField;
            public static Object staticMethod() { return null; }
            // non-static members
            public class InnerClass { }
            public Object nonStaticField;
            public Object nonStaticMethod() { return null; }
            // constructors
            public SuperClass() { }
            // private members
                private Object privateField;
        }
        public final class SubClass1<T extends SuperClass > {
            private T object;
            public SubClass1(T t) { object = t; }
            public String toString() {
                return
                    "static nested type      : " + T.EnumType.class         + "\n"
                    + "static field          : " + T.staticField            + "\n"
                    + "static method         : " + T.staticMethod()         + "\n"
                    + "non-static nested type: " + T.InnerClass.class       + "\n"
                    + "non-static field      : " + object.nonStaticField    + "\n"
                    + "non-static method     : " + object.nonStaticMethod() + "\n"
                    //+ "constructor           : " + (new T())                + "\n" // Compile-time error.
                    //+ "private member        : " + object.privateField      + "\n" // Compile-time error.
                    ;
            }
        }
        // But only the non-static methods are dynamically dispatched. In the unlikely case that a subclass redefines types, fields and static methods of its superclass, these redefinitions would not be accessible through the superclass bound.
        public final class SubClass2 extends SuperClass {
            // static members
            public enum Type { FIX, FOX }
            public static Object staticField;
            public static Object staticMethod() { return null; }
            // non-static members
            public class Inner { }
            public Object nonStaticField;
            public Object nonStaticMethod() { return null; }
            // constructors
            public SubClass2(Object o) { }
            public SubClass2(String s) { }
        }
        public void testAccess() {
            SubClass1<SubClass2> ref = new SubClass1<SubClass2>(new SubClass2("xxx"));
            println(ref);
        }
    }

    /**
     * Type Parameters Scope.
     */
    public class GenericsTypeParametersScope {
    /*
    Type parameter is visible everywhere in the definition of a generic type or method, except any static context of a type.

    Generic Classes.
    The scope of a class's type parameter is the entire definition of the class, except any static members or static initializers of the class. This means that the type parameters cannot be used in the declaration of static fields or methods or in static nested types or static initializers.
    */
        class SomeClass<T> {
            // Non-static initializer, non-static field, non-static method.
            {
                SomeClass<T> test = new SomeClass<T>();
            }
            private T localInfo;
            public T getLocalInfo() { return localInfo; }
            // Non-static nested types.
            public class Accessor {
                public T getInfo() { return localInfo; }
            }
            // Static initializer, static field, static method.
            static {
                //SomeClass<T> test = new SomeClass<T>(); // Compile-error: "'SomeClass.this' cannot be referenced from a static context".
            }
            //private static T globalInfo; // Same error.
            //public static T getGlobalInfo() { return globalInfo; } // Same error.
            // Static nested types.
            public static class Failure extends Exception {
                //private final T info; // Same error.
                //public Failure( T t) { info = t; } // Same error.
                //public T getInfo() { return info; } // Same error.
            }
            // Nested interfaces and enum types are considered static type members of the class. Only inner classes, that is, non-static nested classes, can use the type parameter of the enclosing generic class.
            private interface Copyable {
                //T copy(); // Same error.
            }
            private enum State {
                VALID, INVALID;
                //private T info; // Same error.
                //public void setInfo(T t) { info = t; } // Same error.
                //public T getInfo() { return info; } // Same error.
            }
        }
    /*
    Generic Interfaces.
    The scope of an interface's type parameter is the entire definition of the interface, except any fields or nested types. This is because fields and nested types defined in an interface are implicitly static.
    */
        interface SomeInterface<T> {
            // Field.
            //SomeClass<T> value = new SomeClass<T>(); //Compile-error: "'SomeInterface.this' cannot be referenced from a static context".
            // The nested class is considered a static nested class, not an inner class.
            class Accessor {
                //public T getInfo() { // Same error.
                //    return value.getLocalInfo();
                //}
            }
            // Method.
            T getValue();
        }
    /*
    Generic Methods.
    The scope of a method's or constructor's type parameter is the entire definition of the method; there is no exception, because a method has no static parts. For example, the type parameter can appear in the return and argument type. It can appear in the method body and also in local (or anonymous) classes defined inside the method. It does not matter whether the generic method itself is static or non-static. Methods, different from types, do not have any "static context"; there is no such thing as a static local variable or static local class.
    */
        private interface Copyable<T> { T copy(); }
        <T extends Copyable<T>> void nonStaticMethod(T t) {
            final T copy = t.copy();
            class Task implements Runnable {
                public void run() {
                    T tmp = copy;
                    System.out.println(tmp);
                }
            }
            (new Task()).run();
        }
        static <T extends Copyable<T>> void staticMethod(T t) {
            final T copy = t.copy();
            class Task implements Runnable {
                public void run() {
                    T tmp = copy;
                    System.out.println(tmp);
                }
            }
            (new Task()).run();
        }
    }

    /**
     * Bounded Type Parameters - Type restriction.
     */
    public class GenericsBoundedTypeParameters_ {
        public class UnBoundedBox<T> {
            protected T value;
            public void box(T t) { value = t; }
            public T unbox() {
                T t = value;
                value = null;
                return t;
            }
        }
     // If we want to have a restricted form of box that only holds numbers, Java allows us to achieve this by using a bound on the type parameter.
        public class NumberBoundedBox<T extends Number> extends UnBoundedBox<T> {
            // Notice that because the value field has protected access, it can be accessed directly in the subclass.
            public int intValue() { return value.intValue(); }
        }
     // As a result of this, the compiler knows that value will definitely have a method intValue() available on it (Number has one).
        // Won't compile.
        //NumberBoundedBox<Object> no = new NumberBoundedBox<>();
        // This will.
        NumberBoundedBox<Integer> no = new NumberBoundedBox<>();

     // Don't use it or use it with great caution - runtime exception (ClassCastException):
        // Compiles.
        public void genericsBoundedTypeParametersCouldButDont() {
            NumberBoundedBox numberBoundedBox = new NumberBoundedBox();
            // This is very dangerous.
            numberBoundedBox.box(new Object());
            // Runtime error.
            System.out.println(numberBoundedBox.intValue());
        }
     // In general, type bounds can be used to write better generic code and libraries. With practice, some fairly complex constructions can be built.
     // The definition might seem daunting, but the ComparingBox is really just a UnBoundedBox that contains a Comparable value. The type also extends the comparison operation to the ComparingBox type itself, by just comparing the contents of the two boxes.
        public class ComparingBox<T extends Comparable<T>> extends UnBoundedBox<T> implements Comparable<ComparingBox<T>> {
            @Override
            public int compareTo(ComparingBox<T> o) {
                if (value == null) return o.value == null ? 0 : -1;
                return value.compareTo(o.value);
            }
        }

     // It's possible to narrow classes.
        class ClassName<T extends UnBoundedBox & Comparable<T> & Serializable> { }
     // One can have as many interface supertypes as you like, but at most one of the bounds can be a class. If you have a class as a bound, it must be the first one in the bounds list.

    }
}
