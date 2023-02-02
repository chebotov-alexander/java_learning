package org.example.java_learning;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    In general, there is no relationship between Pair<S> and Pair<T>, no matter how S and T are related. But generic classes can extend or implement other generic classes. In this regard, they are no different from	ordinary classes.
      For example, the class ArrayList<T> implements the interface List<T>.
     Although you could do:
    */
    public void inheritanceRulesForGenericTypesCouldButShouldnt() {
        var managerBuddies = new Pair<Manager>(
            new Manager("",0,2000,1,1),
            new Manager("",0,2000,1,1)
        );
        Pair rawBuddies = managerBuddies; // it's legal to assign to a raw type Pair
        rawBuddies.setFirst(new File("...")); // only a compile-time warning
        // do not do that because if the foreign object is retrieved with getFirst and assigned to a Manager variable, a ClassCastException is thrown.
        Manager managerFirst = managerBuddies.getFirst();
        System.out.println(managerFirst);
    }
}
