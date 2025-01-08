package org.example.java_learning;

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
   - If the compiler erases all type parameters at compile time, why use generics:
 	 -- The Java compiler enforces tighter type checks on generic code at compile time.
 	 -- Generics support programming types as parameters.
 	 -- Generics enable you to implement generic algorithms.
   - The use of raw types in code written after the introduction of genericity into the Java programming language is discouraged. According to the Java Language Specification, it is possible that future versions of the Java programming language will disallow the use of raw types.
   - Heap pollution occurs when a variable of a parameterized type refers to an object that is not of that parameterized type. This situation occurs if the program performed some operation that gives rise to an unchecked warning at compile-time. An unchecked warning is generated if, either at compile-time (within the limits of the compile-time type checking rules) or at runtime, the correctness of an operation involving a parameterized type (for example, a cast or method call) cannot be verified. For example, heap pollution occurs when mixing raw types and parameterized types, or when performing unchecked casts. In normal situations, when all code is compiled at the same time, the compiler issues an unchecked warning to draw your attention to potential heap pollution. If you compile sections of your code separately, it is difficult to detect the potential risk of heap pollution. If you ensure that your code compiles without warnings, then no heap pollution can occur.

 Type erasure is a mechanism to achieve compatibility between newer (generic collections) and older, non-generic collection classes. This means that generic type parameters are only visible at compile time — they are stripped out by javac and are replaced by their bounding types (or Object for variables without bounds) and are not reflected in the bytecode (actually small traces of generics remain, which can be seen at runtime via reflection).
 To implement generics, the Java compiler applies type erasure to:
  - Replace all type parameters in generic types with their bounds or Object if the type parameters are unbounded. The produced bytecode, therefore, contains only ordinary classes, interfaces, and methods.
  - Insert type casts if necessary to preserve type safety.
  - Generate bridge methods to preserve polymorphism in extended generic types.

 Classes/Interfaces like List<T> or MyClass<T>, MyInterface<T> are parameterized types (generic types) and can be instantiated like this {MyClass<String> cls = new MyClass<>()}.
 The non-generic types like List, MyClass, MyInterface instantiated like this {MyClass rawCls = new MyClass()}, i.e. without any type arguments, is usually called a raw type.
 However, a non-generic class or interface type like String instantiated like this {String str = " "} is not a raw type.
 The use of raw types in code written after the introduction of genericity into the Java programming language is discouraged. According to the Java Language Specification, it is possible that future versions of the Java programming language will disallow the use of raw types.
 Raw types show up in legacy code because lots of API classes (such as the Collections classes) were not generic prior to JDK 5.0. When using raw types, you essentially get pre-generics behavior — a MyClass gives you Objects.
 */
	public void initAsRaw() {
		List<String> list = new ArrayList();
		List rawList = new ArrayList();
		// For backward compatibility, assigning a parameterized type to its raw type is allowed.
		rawList = list;
		// But if you assign a raw type to a parameterized type, you get a warning.
		list = rawList; // Warning: "Unchecked assignment: 'List' to 'List<String>'".
		// You also get a warning if you use a raw type to invoke generic methods defined in the corresponding generic type.
		rawList.add(8); //	Warning, no runtime-error: "Unchecked call to 'add(E)' as a member of raw type 'List'".
		// The warning shows that raw types bypass generic type checks, deferring the catch of unsafe code to runtime. Therefore, you should avoid using raw types.
		String s = list.get(0); // Runtime error: "ClassCastException".
	}
 // Another example.
	class SomeLegacyClass {
		private List names;
		public void setNames(List names) { this.names = names; }
		public List getNames() { return this.names; }
	}
	void testSomeLegacyClass() {
		SomeLegacyClass obj = new SomeLegacyClass();
		List<String> names = new LinkedList<>();
		obj.setNames(names);
		names = obj.getNames(); // This is not type-safe, so unchecked warning. The compiler has not enough information to ensure that the list returned really is a list of strings.
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
 // A programs may contain different kinds of Pair, such as Pair<String> or Pair<LocalDate>, but erasure turns them all into raw Pair types.
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
 If you switch the bounds: class Interval<T extends Serializable & Comparable>, the raw type replaces T with Serializable, and the compiler inserts casts to Comparable when necessary. For efficiency, you should therefore put tagging interfaces (that is, interfaces without methods) at the end of the bounds list.

 ! When you program a call to a generic method, the compiler inserts casts when the return type has been erased.
 So under the hood it's old school java casts, but for developer it's telling the compiler what type to cast and compiler checks if it is possible. It works that way for all java beings (fields, methods, classes), for example:
 	public static <T extends Comparable> T min(T[] a)
  after compiler did his job
 	public static Comparable min(Comparable[] a)
 */
 // Raw types can be used like regular types without any restrictions, except that certain uses will result in "unchecked" warnings.
	interface Copyable<T> { T copy(); }
	final class Wrapped <Elem extends Copyable<Elem>> {
		private Elem theObject;
		//private Copyable theObject; // After erasure. A method or constructor call to a raw type generates an unchecked warning if the erasure changes the argument types. The invocation is unsafe because the compiler cannot ensure that the argument passed to the method is compatible to the "erased" type that the type parameter Elem stands for.
		public Wrapped(Elem arg) { theObject = arg.copy(); }
		//public Wrapped(Copyable arg) ... // After erasure.
		public void setObject(Elem arg) { theObject = arg.copy(); }
		//public void setObject(Copyable arg) ... // After erasure.
		public Elem getObject() { return theObject.copy(); }
		//public Copyable getObject() ... // After erasure.
		public boolean equals(Object other) {
			if (other == null) return false;
			if (!(other instanceof Wrapped)) return false;
			return (this.theObject.equals(((Wrapped)other).theObject));
		}
	}
	class StringCopyable implements Copyable<StringCopyable> {
		private StringBuilder buffer;
		public StringCopyable(String s) { buffer = new StringBuilder(s); }
		public StringCopyable copy() { return new StringCopyable(buffer.toString()); }
	}
	void testStringBuilderCopyable() {
		Wrapped<StringCopyable> wrapper = new Wrapped<>(new StringCopyable("s1"));
		wrapper.setObject(new StringCopyable("s 2")); // Unchecked warning.
		Object s = wrapper.getObject();
	// If the method's argument type is not changed by type erasure, then the method call is safe. For instance, the method getObject has the signature Copyable getObject(void) after type erasure and its invocation is safe and warning-free.
	// Fields of a raw type have the type that they would have after type erasure. A field assignment to a raw type generates an unchecked warning if erasure changes the field type. The field theObject of the raw type Wrapped is changed by type erasure and is of type Copyable after type erasure. If the theObject field were public and assignable, the assignment would be unsafe because the compiler cannot ensure that the value being assigned really is of type Elem. Reading the field is safe and does not result in a warning.
	}

 /*
 Compile and Runtime Typing {
  JVM and javac see types differently:
 	List<String> l = new ArrayList<>();
 	System.out.println(l);
   - javac will see the type of l as List-of-String, and will use that type information to carefully check for syntax errors, such as an attempted add() of an illegal type. As of List interface itself actually in above example the compile-time type is less specific than the runtime type, because we don’t know exactly what concrete type l will be, all we know is that it will be of a type compatible with List (not ArrayList).
   - JVM will see l as an object of type ArrayList — as we can see from the println() statement. The runtime type of l is a raw type due to type erasure.
  All instantiations of a generic type share the same runtime type representation, namely the representation of the raw type. For instance, the instantiations of a generic type List ,  such as List<Date> , List<String> , List<Long> , etc. have different static types at compile time, but the same dynamic type List at runtime.
  A cast consists of two parts:
   - a static type check performed by the compiler at compile time and
   - a dynamic type check performed by the virtual machine at runtime.
  The static part sorts out nonsensical casts, that cannot succeed, such as the cast from String to Date or from List<String> to List<Date> .
  The dynamic part uses the runtime type information and performs a type check at runtime.  It raises a ClassCastException if the dynamic type of the object is not the target type (or a subtype of the target type) of the cast. Examples of casts with a dynamic part are the cast from Object to String or from Object to List<String> .  These are the so-called downcasts, from a supertype down to a subtype.
  Not all casts have a dynamic part. Some casts are just static casts and require no type check at runtime.  Examples are the casts between primitive types, such as the cast from long to int or byte to char .  Another example of static casts are the so-called upcasts, from a subtype up to a supertype, such as the casts from String to Object or from LinkedList<String> to List<String> . Upcasts are casts that are permitted, but not required.  They are automatic conversions that the compiler performs implicitly, even without an explicit cast expression in the source code, which means, the cast is not required and usually omitted.  However, if an upcast appears somewhere in the source code then it is a purely static cast that does not have a dynamic part.
  Type casts with a dynamic part are potentially unsafe, when the target type of the cast is a parameterized type.  The runtime type information of a parameterized type is non-exact, because all instantiations of the same generic type share the same runtime type representation. The virtual machine cannot distinguish between different instantiations of the same generic type.  Under these circumstances the dynamic part of a cast can succeed although it should not.
 */
	static void genericTypeToObject() {
		List<String> list = Arrays.asList("a", "b", "c");
		fromObjectToGenericType(list);
	}
	static void fromObjectToGenericType(Object arg) {
		// Actually is a cast from Object to the raw type List. It would succeed even if the object referred to were a List<String> instead of a List<Integer>.
		List<Integer> list = (List<Integer>) arg; // Unchecked warning: "Unchecked cast: 'java.lang.Object' to 'java.util.List<java.lang.Integer>'". The compiler emits "unchecked" warnings for every dynamic cast whose target type is a parameterized type. Note that an upcast whose target type is a parameterized type does not lead to an "unchecked" warning, because the upcast has no dynamic part.
		getFromCastedToGeneric(list);
	}
	static void getFromCastedToGeneric(List<Integer> list) {
		Integer i = list.get(0); // Runtime error: ClassCastException: String cannot be cast to class Integer.
	}
/*
}

Bridge methods {
 When compiling a class or interface that extends a parameterized class or implements a parameterized interface, the compiler may need to create a synthetic method (to solve the problem below and preserve the polymorphism of generic types after type erasure), which is called a bridge method, as part of the type erasure process. You normally don't need to worry about bridge methods, but you might be puzzled if one appears in a stack trace.
 Erasure of methods brings up a couple of complexities.
 Next code seems like perfectly legal Java code, but it will not compile. The issue is that although the two methods seem like normal overloads, after type erasure, the signature of both methods becomes: {int totalOrders(Map)};
 The runtime would be unable to distinguish between the methods by signature, and so the language specification makes this syntax illegal.
*/
		interface OrderCounter {
			// Name maps to list of order quantity.
			int totalOrders(Map<String, List<String>> orders);
			// Name maps to total orders made so far.
			//int totalOrders(Map<String, Integer> orders); // Won't compile.
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
    //    class DateInterval extends PairRaw {
    //        public void setSecond(LocalDate second) {...}
    //    }
    // There is another setSecond method, inherited from Pair, namely:
    //    public void setSecond(Object second)
    // This is clearly a different method because it has a parameter of a different type - Object instead of LocalDate (method DateInterval.setSecond(LocalDate) does not override the Pair.setSecond(Object)). But it shouldn’t be different. Consider this sequence of statements:
		void doSomeBridge() {
			DateInterval interval = new DateInterval(LocalDate.now(), LocalDate.now());
			Pair<LocalDate> pair = interval; // OK assignment to superclass
			pair.setSecond(LocalDate.now());
		}
    /*
	 Our expectation is that the call to setSecond is polymorphic and that the appropriate method is called. Since pair refers to a DateInterval object, that should be DateInterval.setSecond.
     !! The problem is that the type erasure interferes with polymorphism. To fix this problem, the compiler generates a bridge method in the DateInterval class:
    	public void setSecond(Object second) { setSecond((LocalDate) second); }
     To see why this works, let us carefully follow the execution of the statement:
    	pair.setSecond(...)
     ! The variable pair has declared type Pair<LocalDate>, and that type only has a single method called setSecond, namely setSecond(Object). The virtual machine calls that method on the object to which pair refers. That object is of type DateInterval. Therefore, the method DateInterval.setSecond(Object) is called. That method is the synthesized bridge method. It calls DateInterval.setSecond(LocalDate), which is what we want.

     Bridge methods are not limited to generic types. It is legal for a method to specify a more restrictive return type when overriding another method. For example:
    	public class Employee implements Cloneable {
    		public Employee clone() throws CloneNotSupportedException {...}
    	}
     The Object.clone and Employee.clone methods are said to have covariant return types.
    	Employee clone() // defined above
    	Object clone() // synthesized bridge method, overrides Object.clone
     The synthesized bridge method calls the newly defined method.
    }

    Tell java to keep some type information in runtime {
	*/
		public static class SomeType<T> {
			public <E> void test(Collection<E> collection) {
				for (E e : collection) {System.out.println(e);}
			}
			public void test(List<Integer> list) {
				for (Integer e : list) {System.out.println(e);}
			}
		}
		public static void keepRuntimeInformation() {
			// 1st use without Generalization.
			SomeType<?> someType1 = new SomeType<>();
			SomeType<String> someType3 = new SomeType<>();
			SomeType<Integer> someType4 = new SomeType<>();
			// 2nd use with Generalization.
			SomeType someType2 = new SomeType();

			List<String> list = Arrays.asList("value");
			// for 1st compiler adds information to use generics (there is <?>), so there will be no errors.
			someType1.test(list);
			someType3.test(list);
			someType4.test(list);
			// for 2nd will be runtimeError ClassCastException because of type erasure this piece of code will be executed "for (Integer e : list)" on List of type "String" and expectedly will fail.
			//someType2.test(list);
		}
	//}

	/*
	Generics and Varargs {
	 https://docs.oracle.com/javase/tutorial/java/generics/nonReifiableVarargsType.html
	 Generic methods that include vararg input parameters can cause heap pollution.
	 When the compiler encounters a varargs method, it translates the varargs formal parameter into an array. However, the Java programming language does not permit the creation of arrays of parameterized types. The compiler translates the varargs formal parameter T... elements to the formal parameter T[] elements, an array. However, because of type erasure, the compiler converts the varargs formal parameter to Object[] elements. Consequently, there is a possibility of heap pollution.
	}
	*/

	static class GenericsAndUncheckedWarning {
	/*
	An unchecked warning tells a programmer that a cast may cause a program to throw an exception somewhere else. Suppressing the warning with @SuppressWarnings("unchecked") tells the compiler that the programmer believes the code to be safe and won't cause unexpected exceptions.
	Why would you want to do that? Java type system isn't good enough to represent all possible type usage patterns. Sometimes you may know that a cast is safe, but Java doesn't provide a way to say so - to hide warnings like this, {@SupressWarnings("unchecked")} can be used, so that a programmer can focus on real warnings.
	For instance, inside Java class Optional method empty() returns a singleton to avoid allocation of empty optionals that don't store a value. This cast is safe, as the value stored in an empty optional cannot be retrieved so there is no risk of unexpected class cast exceptions.
		private static final Optional<?> EMPTY = new Optional<>();
		public static<T> Optional<T> empty() {
			@SuppressWarnings("unchecked")
			Optional<T> t = (Optional<T>) EMPTY;
			return t;
		}
	Another example:
		@SuppressWarnings("unchecked")
		public List<User> findAllUsers(){
			Query query = entitymanager.createQuery("SELECT u FROM User u");
			return (List<User>)query.getResultList();
		}

	@SuppressWarning is a powerful annotation that suppresses warning messages from compilers. Like everything that is powerful, we have the responsibility to use it properly:
     - @SuppressWarning can apply to declaration at a different scope: a local variable, a method, a type, etc. We must always use @SuppressWarning to the most limited scope to avoid unintentionally suppressing warnings that are valid concerns from the compiler.
     - We must suppress a warning only if we are sure that it will not cause a type error later.
     - We must always add a note (as a comment) to fellow programmers explaining why a warning can be safely suppressed.
	*/
	// !! Do not use raw types. The only exception to this rule is using it as an operand of the instanceof operator. Since instanceof checks for run-time type and type arguments have been erased, we can only use the instanceof operator on raw types. See vivid example:
		static void doNotUseRaw() {
			GenericsIntro.GenericsRestrictions.ArraysOfGenerics.testGetArrayItem();
		}
	}
}
