package org.example.java_learning;

import java.io.File;
import java.util.*;

import static org.example.java_learning.Util.println;

/**
 * Inheritance Rules for Generic Types
 */
public class GenericsInheritance {
    List<String> lstr = new ArrayList<>();
/*
 This don't compile
     List<Object> lobj = lstr;
  Because if it did after adding elements typed Object into the same array, one could ask for new added element and wanted to assign it to String variable, but actually where is Object. So compile prohibit this behaviour:
     lobj.add(new Object());
     String s = lstr.get(0);
  That said Generics and Inheritance can't go along, so is that illegal also:
     Pair<Employee> buddies = new Pair<Manager>(ceo, cfo);
*/
    static void print1(List<Object> c) {
        for (Object o : c) println(o);
    }
    static void print2(Collection<Object> c) {
        for (Object o : c) println(o);
    }
    static void testGenericsInheritance() {
        List<String> list1 = Arrays.asList("a", "b", "c");
        //print1(list1); // Compile-time error: "Required type: List<Object>; Provided:List<String>".
        // On the other hand, instantiations of different generic types for the same type argument can be compatible. A List<Object> is compatible to a Collection<Object> because the two types are instantiations of a generic supertype and its generic subtype and the instantiations are for the same type argument Object.
        List<Object> list2 = Arrays.asList("a", "b", "c");
        print2(list2);
    }
/*
 In general, there is no relationship between Pair<S> and Pair<T>, no matter how S and T are related. But generic classes can extend or implement other generic classes. In this regard, they are no different from ordinary classes. For example:
    class ArrayList<T> implements the interface List<T>.
 Although you could do:
*/
    public static void inheritanceRulesForGenericTypesCouldButShouldnt() {
        var managerBuddies = new Pair<Manager>(
            new Manager("",0,2000,1,1),
            new Manager("",0,2000,1,1)
        );
        Pair rawBuddies = managerBuddies; // it's legal to assign to a raw type Pair.
        rawBuddies.setFirst(new File("...")); // only a compile-time warning
        // do not do that because if the foreign object is retrieved with getFirst and assigned to a Manager variable, a ClassCastException is thrown.
        Manager managerFirst = managerBuddies.getFirst();
        System.out.println(managerFirst);
    }
// Compatibility between instantiations of the same generic type exist only among wildcard instantiations and concrete instantiations that belong to the family of instantiations that the wildcard instantiation denotes.
// More on relationships on Generics see {GenericsWildcards}, For example:
    Collection<?> collection = new ArrayList<String>();
    List<? extends Number> list = new ArrayList<Long>();
}
