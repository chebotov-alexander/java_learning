package org.example.java_learning;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Comparator.comparing;

/**
 * Lambdas, Method references, Default methods in interfaces and a glympse at Functional-style programming.
 * Functions in Java and Method reference as first-class citizens (values).
 */
public class LambdasIntro {
/*
Lambdas and method references, provide the ability to concisely pass code or methods as arguments to be executed in the middle of doing something else. Lambdas make this sort of idea much more widely usable: from simply parameterizing a sort method with code to do the comparison to expressing complex queries on collections of data using the new Streams API. Event handlers and callbacks, where you register an object containing a method to be used when some event happens, heavily relies on lambdas and method references.
Lambdas and method references give you a new concise way to express behavior parameterization. Suppose you want to write two methods that differ in only a few lines of code. You can now simply pass the code of the parts that differ as an argument (this programming technique is shorter, clearer, and less error-prone than the common tendency to use copy and paste). Experts will here note that behavior parameterization could, prior to Java 8, be encoded using anonymous classes — but lambdas and method references increased code conciseness and clarity.

Functional-style programming is another style of programming, just like object-oriented programming, but centered on using functions as values.
No shared mutable data and the ability to pass methods and functions—code—to other methods are the cornerstones of what’s generally described as the paradigm of functional programming. In contrast, in the imperative programming paradigm you typically describe a program in terms of a sequence of statements that mutate state. The no-shared-mutable-data requirement means that a method is perfectly described solely by the way it transforms arguments to results; in other words, it behaves as a mathematical function and has no (visible) side effects.
Note that classical object-oriented programming and functional programming, as extremes, might appear to be in conflict. But the idea is to get the best from both programming paradigms, so you have a better chance of having the right tool for the job.

Suppose you want to filter all the hidden files in a directory. You need to start writing a method that, given a File, will tell you whether it’s hidden. Fortunately, there’s such a method in the File class called isHidden. It can be viewed as a function that takes a File and returns a boolean. But to use it for filtering, you need to wrap it into a FileFilter object that you then pass to the File.listFiles method, as follows:
*/
	File[] hiddenFilesWithAnonymousClass = new File(".").listFiles(new FileFilter() {
		public boolean accept(File file) {
			return file.isHidden(); // Filtering hidden files!
		}
	});
// You already have the method isHidden that you could use. Why do you have to wrap it up in a verbose FileFilter class and then instantiate it? Because that’s what you had to do prior to Java 8. Now, you can rewrite that code as follows:
	File[] hiddenFilesWithMethodReference = new File(".").listFiles(File::isHidden);
//	Method reference :: syntax meaning "use this method as a value".

// Same with instead of writing verbose code (to sort a list of apples in inventory based on their weight) like
	void mockMethod1() {
		Collections.sort(inventory, new Comparator<Apple>() {
			public int compare(Apple a1, Apple a2) {
				return Integer.compare(a1.getWeight(), a2.getWeight());
			}
		});
	}
//  in Java 8 you can write more concise code that reads a lot closer to the problem statement, like the following:
	void mockMethod2() {
		inventory.sort(comparing(Apple::getWeight));
	}
// For example, {(int x) -> x + 1} means "the function that, when called with argument x, returns the value x + 1". You could define a method "add1" inside a class "MyMathsUtils" and then write "MyMathsUtils::add1", but the new lambda syntax is more concise for cases where you don’t have a convenient method and class available.
//	Suppose you have a class Apple with a method getColor and a variable inventory holding a list of Apples; then you might wish to select all the green apples (here using a Color enum type that includes values GREEN and RED) and return them in a list. The word filter is commonly used to express this concept. Before Java 8, you might write a method filterGreenApples:
	public static List<Apple> filterGreenApples(List<Apple> inventory) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (Color.GREEN.equals(apple.getColor())) {
				result.add(apple);
			}
		}
		return result;
	}
// But next, somebody would like the list of heavy apples (say over 150 g), and so, with a heavy heart, you’d write the following method to achieve this (perhaps even using copy and paste):
	public static List<Apple> filterHeavyApples(List<Apple> inventory) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (apple.getWeight() > 150) {
				result.add(apple);
			}
		}
		return result;
	}
// We all know the dangers of copy and paste for software engineering (updates and bug fixes to one variant but not the other), and hey, these two methods vary only in one line: the highlighted condition inside the if construct. If the difference between the two method calls in the highlighted code had been what weight range was acceptable, then you could have passed lower and upper acceptable weights as arguments to filter—perhaps (150, 1000) to select heavy apples (over 150 g) or (0, 80) to select light apples (under 80 g). But Java 8 makes it possible to pass the code of the condition as an argument, avoiding code duplication of the filter method. You can now write this:
	public static boolean isGreenApple(Apple apple) {
		return Color.GREEN.equals(apple.getColor());
	}
	public static boolean isHeavyApple(Apple apple) {
		return apple.getWeight() > 150;
	}
	public static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (p.test(apple)) {
			  result.add(apple);
			}
		}
		return result;
	}
	List<Apple> greenApples = filterApples(inventory, LambdasIntro::isGreenApple);
	List<Apple> heavyApples = filterApples(inventory, LambdasIntro::isHeavyApple);
// You don’t even need to write a method definition that’s used only once; the code is crisper and clearer because you don’t need to search to find the code you’re passing:
	List<Apple> greenApples2 = filterApples(inventory, (Apple a) -> Color.GREEN.equals(a.getColor()));
	List<Apple> heavyApples2 = filterApples(inventory, (Apple a) -> a.getWeight() > 150);
	List<Apple> weirdApples = filterApples(inventory, (Apple a) -> a.getWeight() < 80 || Color.RED.equals(a.getColor()));
// But if such a lambda exceeds a few lines in length (so that its behavior isn’t instantly clear), you should instead use a method reference to a method with a descriptive name instead of using an anonymous lambda. Code clarity should be your guide.
// The above example is based on java.util.function.Predicate. However, one can create her own Predicate class like this:
	interface ApplePredicate {
		boolean test(Apple a);
	}
	static class AppleWeightPredicate implements ApplePredicate {
	  @Override
		public boolean test(Apple apple) {
			return apple.getWeight() > 150;
		}
	}
	public static List<Apple> filterApplesApplePredicate(List<Apple> inventory, ApplePredicate p) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (p.test(apple)) {
				result.add(apple);
			}
		}
		return result;
	}
// and use it like that:
	List<Apple> heavyApplesApplePredicate =
		filterApplesApplePredicate(inventory, new AppleWeightPredicate());
// or using anonymous class
	List<Apple> heavyApplesApplePredicateAnonymous =
		filterApplesApplePredicate(inventory, new ApplePredicate() {
			public boolean test(Apple apple){
				return apple.getWeight() > 150;
			}
		});
// and using lambdas
	List<Apple> heavyApplesApplePredicateLambda =
		filterApplesApplePredicate(inventory, (Apple apple) -> apple.getWeight() > 150);
// and finally type abstraction that eliminates the of filterApplesApplePredicate and ApplePredicate
	public interface Predicate<T> {
		boolean test(T t);
	}
	public static <T> List<T> filter(List<T> list, Predicate<T> p) {
		List<T> result = new ArrayList<>();
		for(T e: list) {
			if(p.test(e)) {
				result.add(e);
			}
		}
		return result;
	}
// so that
	List<Apple> typeAbstractionPredicate = filter(inventory, (Apple apple) -> apple.getWeight() > 150);

// So from value parametrization to behavior parametrization and next from classes to anonymous classes to lambdas.

// Real-world examples:
//  - filtering;
//  - sorting with a Comparator;
//  - executing a block of code with Runnable;
//  - returning a result from a task using Callable;
//  - GUI event handling.
//
//  1. Sorting with a Comparator (using java.util.Comparator):
//  public interface Comparator<T> { int compare(T o1, T o2); }
	void mockMethod3() {
		inventory.sort(new Comparator<Apple>() {
			public int compare(Apple a1, Apple a2) {
				return Integer.compare(a1.getWeight(), a2.getWeight());
			}
		});
		inventory.sort((Apple a1, Apple a2) -> Integer.compare(a1.getWeight(), a2.getWeight()));
		inventory.sort(Comparator.comparingInt(Apple::getWeight));
	}

//  2. Executing a block of code with Runnable (using java.lang.Runnable):
//	public interface Runnable {
//  	// return nothing
//  	void run();
//	}
//  before
	Thread runTaskWithAnonymousClass = new Thread(new Runnable() {
		public void run() {
			System.out.println("Hello world");
		}
	});
//  after
	Thread runTaskWithLambda = new Thread(() -> System.out.println("Hello world"));

//  3. Returning a result using Callable (using java.util.concurrent.Callable):
//	public interface Callable<V> {
//	  	// return V as a execution result
//	  	V call();
//	}
//  You can use it, as follows, by submitting a task to an executor service. Here you return the name of the Thread that is responsible for executing the task:
	ExecutorService executorService = Executors.newCachedThreadPool();
//	before
	Future<String> callbackWithAnonymousClass = executorService.submit(new Callable<String>() {
		@Override
		public String call() throws Exception {
			return Thread.currentThread().getName();
		}
	});
//  after
	Future<String> callbackWithLambda = executorService.submit(() -> Thread.currentThread().getName());

//  4. GUI event handling:
// 	Button button = new Button("Send");
//  //before
//	button.setOnAction(new EventHandler<ActionEvent>() {
//	  	public void handle(ActionEvent event) {
//	    	label.setText("Sent!!");
//	  	}
//	});
//  //after
//	button.setOnAction((ActionEvent event) -> label.setText("Sent!!"));

	// Prepare some stuff
	private static List<Apple> inventory = LambdasMain.inventory;
}