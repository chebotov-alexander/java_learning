package org.example.java_learning;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Type Erasure (translation of Java generics)
 */
public class GenericsTypeErasure {
    /*
	Key points:
      - There are no generics in the virtual machine, only ordinary classes and methods.
      - All type parameters are replaced by their bounds.
      - Bridge methods are synthesized to preserve polymorphism.
      - Casts are inserted as necessary to preserve type safety.

    The nongeneric type List is usually called a raw type.
    Type erasure is a mechanism to achieve compatibility between newer (generic collections) and older, nongeneric collection classes. This means that generic type parameters are only visible at compile time — they are stripped out by javac and are replaced by their bounding types (or Object for variables without bounds) and are not reflected in the bytecode (actually small traces of generics remain, which can be seen at runtime via reflection).
	*/
	public void initAsRaw() {
		List rawList = new ArrayList();
		List<String> list = new ArrayList();
		rawList = list;
		//	Warning, no runtimeError
		rawList.add(8);
		//	runtimeError ClassCastException
		String s = list.get(0);
	}

    // For example, the raw type for Pair<T> looks like this:
	class PairRaw {
		private Object first;
		private Object second;
		public PairRaw(Object first, Object second) { this.first = first;  this.second = second; }
		public Object getFirst() { return first; }
		public Object getSecond() { return second; }
		public void setFirst(Object newValue) { first = newValue; }
		public void setSecond(Object newValue) { second = newValue; }
	}
    // A programs may contain different kinds of Pair, such as Pair<String> or Pair<LocalDate>, but erasure turns	them all into raw Pair types.
    // For bounded type parameters (more on that later):
	class Interval<T extends Comparable & Serializable> implements Serializable {
		private T lower;
		private T upper;
		public Interval(T first, T second) {
			if (first.compareTo(second) <= 0) { lower = first; upper = second; }
			else { lower = second; upper = first; }
		}
	}
    // The raw type Interval looks like this:
	class IntervalAfterTypeErasure implements Serializable {
		private Comparable lower;
		private Comparable upper;
		public IntervalAfterTypeErasure(Comparable first, Comparable second) {}
	}
    /*
	If you switch the bounds: class Interval<T extends Serializable & Comparable, the raw type replaces T with Serializable, and the compiler inserts casts to Comparable when necessary. For efficiency, you should therefore put tagging interfaces (that is, interfaces without methods) at the end of the bounds list.

    ! When you program a call to a generic method, the compiler inserts casts when the return type has been erased.
	So under the hood it's old school java casts, but for developer it's telling the compiler what type to cast and compiler checks if it is possible. It works that way for all java beings (fields, methods, classes), for example:
    	public static <T extends Comparable> T min(T[] a)
     after compiler did his job
    	public static Comparable min(Comparable[] a)

    Compile and Runtime Typing:
    	JVM and javac see types differently.
    	List<String> l = new ArrayList<>();
    	System.out.println(l);
      - javac will see the type of l as List-of-String, and will use that type information to carefully check for syntax errors, such as an attempted add() of an illegal type. As of List interface itself actually in above	example the compile-time type is less specific than the runtime type, because we don’t know exactly	what concrete type l will be, all we know is that it will be of a type compatible with List (not ArrayList).
      - JVM will see l as an object of type ArrayList — as we can see from the println() statement. The runtime	type  of l is a raw type due to type erasure.

    Bridge methods {
     Erasure of methods brings up a couple of complexities.
	 Next code seems like perfectly legal Java code, but it will not compile. The issue is that although the two methods seem like normal overloads, after type erasure, the signature of both methods becomes:	int totalOrders(Map);
     The runtime would be unable to distinguish between the methods by signature, and so the language specification makes this syntax illegal.
     Won't compile
	*/
	interface OrderCounter {
      	// Name maps to list of order numbers
		int totalOrders(Map<String, List<String>> orders);
    	// Name maps to total orders made so far
    	//int totalOrders(Map<String, Integer> orders);
	}
    // For example:
	class DateInterval extends Pair<LocalDate> {
		public DateInterval(LocalDate first, LocalDate second) {
			super(first, second);
		}
		// a DateInterval is a Pair of LocalDate objects, and we’ll want to override the methods to ensure that the	second value is never smaller than the first.
		public void setSecond(LocalDate second)	{
			if (second.compareTo(getFirst()) >= 0) super.setSecond(second);
		}
	}
	// This class is erased to:
    //class DateInterval extends PairRaw {
    //    public void setSecond(LocalDate second) {...}
    //}
    // There is another setSecond method, inherited from Pair, namely:
    //    public void setSecond(Object second)
    // This is clearly a different method because it has a parameter of a different type - Object instead of LocalDate. But it shouldn’t be different. Consider this sequence of statements:
	void doSomeBridge() {
		DateInterval interval = new DateInterval(LocalDate.now(), LocalDate.now());
		Pair<LocalDate> pair = interval; // OK assignment to superclass
		pair.setSecond(LocalDate.now());
	}
    /*
	Our expectation is that the call to setSecond is polymorphic and that the appropriate method is called. Since pair refers to a DateInterval object, that should be DateInterval.setSecond.
    !! The problem is that the type erasure interferes with polymorphism. To fix this problem, the compiler	generates a bridge method in the DateInterval class:
    	public void setSecond(Object second) { setSecond((LocalDate) second); }
    To see why this works, let us carefully follow the execution of the statement:
    	pair.setSecond(...)
    ! The variable pair has declared type Pair<LocalDate>, and that type only has a single method called setSecond, namely setSecond(Object). The virtual machine calls that method on the object to which pair refers. That object is of type DateInterval. Therefore, the method DateInterval.setSecond(Object) is called. That method is the synthesized bridge method. It calls DateInterval.setSecond(LocalDate), which is what we want.

    Bridge methods are not limited to generic types. It is legal for a method to specify a more restrictive	return type when overriding another method. For example:
    	public class Employee implements Cloneable {
    		public Employee clone() throws CloneNotSupportedException {...}
    	}
    The Object.clone and Employee.clone methods are said to have covariant return types.
    	Employee clone() // defined above
    	Object clone() // synthesized bridge method, overrides Object.clone
     The synthesized bridge method calls the newly defined method.
    }

    Tell java to keep some type information in runtime {
     Note <T>
	*/
	public class SomeType<T> {
		public <E> void test(Collection<E> collection) {
			for (E e : collection) {System.out.println(e);}
		}
		public void test(List<Integer> list) {
			for (Integer e : list) {System.out.println(e);}
		}
	}
    // 1st use without Generalization
	SomeType<?> someType1 = new SomeType<Object>();
    // 2nd use with Generalization
	SomeType someType2 = new SomeType();
	List<String> list = Arrays.asList("value");
    // for 1st compiler adds information to use generics, so there will be no errors
	// for 2nd will be runtimeError ClassCastException because type erasure
	public void keepRuntimeInformation() {
		someType1.test(list);
		//someType2.test(list);
	}
	//}
}
