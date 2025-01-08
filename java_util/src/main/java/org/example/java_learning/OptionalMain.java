package org.example.java_learning;


import static java.util.stream.Collectors.toSet;
import static org.example.java_learning.Util.println;

import java.util.*;
import java.util.stream.Stream;

public class OptionalMain {
/*
Summary:
  1. Using null to represent the absence of a value is the wrong approach, it causes both theoretical and practical problems. Java solves this problem with java.util.Optional<T> class.
  2. Consistently using Optional values creates a clear distinction between a missing value that’s planned for and a value that’s absent only because of a bug in your algorithm or a problem in your data. It’s important to note that the intention of the Optional class isn’t to replace every single null reference. Instead, its purpose is to help you design more-comprehensible APIs so that by reading the signature of a method, you can tell whether to expect an optional value.
  3. Java language architect Brian Goetz clearly stated that the purpose of Optional is to support the optional return idiom only. Because the Optional class wasn’t intended for use as a field type, it doesn’t implement the Serializable interface. For this reason, using Optionals in your domain model could break applications with tools or frameworks that require a serializable model to work. But using Optionals as a proper type in your domain is a good idea, especially when you have to traverse a graph of objects that potentially aren’t present. So, if you need to have a serializable domain model, you could at least provide a method allowing access to any possibly missing value as an optional, as in the following example:
    public class Person {
        private Car car;
        public Optional<Car> getCarAsOptional() {
            return Optional.ofNullable(car);
        }
    }
  4. Optionals have primitive counterparts: OptionalInt, OptionalLong, and OptionalDouble so you can use OptionalInt instead of Optional<Integer>. But primitive optionals lack the map, flatMap, and filter methods, which are the most useful methods of the Optional class. Moreover, an optional can’t be composed with its primitive counterpart, so if the method returns OptionalInt, you can't pass it as a method reference to the flatMap method of another optional.

_________________________________________________________________________________________________
|     Method    |                            Description                                        |
-------------------------------------------------------------------------------------------------
|empty          |Returns an empty Optional instance.                                            |
|filter         |If the value is present and matches the given predicate, returns this Optional;|
|               |otherwise, returns the empty one.                                              |
|flatMap        |If a value is present, returns the Optional resulting from the application of  |
|               |the provided mapping function to it; otherwise, returns the empty Optional.    |
|get            |Returns the value wrapped by this Optional if present; otherwise, throws a     |
|               |NoSuchElementException.                                                        |
|ifPresent|     |If a value is present, invokes the specified consumer with the value;          |
|               |otherwise, does nothing.                                                       |
|ifPresentOrElse|If a value is present, performs an action with the value as input; otherwise,  |
|               |performs a different action with no input.                                     |
|isPresent      |Returns true if a value is present; otherwise, returns false.                  |
|map            |If a value is present, applies the provided mapping function to it.            |
|of             |Returns an Optional wrapping the given value or throws a NullPointerException  |
|               |if this value is null.                                                         |
|ofNullable     |Returns an Optional wrapping the given value or the empty Optional if this     |
|               |value is null.                                                                 |
|or             |If the value is present, returns the same Optional; otherwise, returns another |
|               |Optional produced by the supplying function.                                   |
|orElse         |Returns the value if present; otherwise, returns the given default value.      |
|orElseGet      |Returns the value if present; otherwise, returns the one provided by the given |
|               |Supplier.                                                                      |
|orElseThrow    |Returns the value if present; otherwise, throws the exception created by the   |
|               |given Supplier.                                                                |
|stream         |If a value is present, returns a Stream containing only it; otherwise, returns |
|               |an empty Stream.                                                               |
|_______________|_______________________________________________________________________________|

*/
    // Suppose you have:
    public class PersonV1 {
        private CarV1 car;
        public CarV1 getCar() { return car; }
    }
    public class CarV1 {
        private Insurance insurance;
        public Insurance getInsurance() { return insurance; }
    }
    public class Insurance {
        private String name;
        public String getName() { return name; }
        public Insurance() {}
        public Insurance(String name) { this.name = name; }
    }
    // What’s problematic with the following code?
    public String getCarInsuranceNameNullUnSafe(PersonV1 person) {
        return person.getCar().getInsurance().getName();
    }
    // This code looks pretty reasonable, but many people don’t own a car, so what’s the result of calling the method getCar? A common unfortunate practice is to return the null reference to indicate the absence of a value (here, to indicate the absence of a car). As a consequence, the call to getInsurance returns the insurance of a null reference, which results in a NullPointerException at runtime and stops your program from running further. But that’s not all. What if person was null? What if the method getInsurance returned null too?

    // You can add null checks where necessary (and sometimes, in an excess of defensive programming, even where not necessary) and often with different styles.
    public String getCarInsuranceNameNullSafeV1(PersonV1 person) {
        // so-called null checking through dereferencing chain.
        if (person != null) {
            CarV1 car = person.getCar();
            if (car != null) {
                Insurance insurance = car.getInsurance();
                if (insurance != null) {
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }
    // This technique clearly scales poorly and compromises readability, so maybe you’d like to attempt another solution.
    public String getCarInsuranceNameNullSafeV2(PersonV1 person) {
        if (person == null) {
            return "Unknown";
        }
        CarV1 car = person.getCar();
        if (car == null) {
            return "Unknown";
        }
        Insurance insurance = car.getInsurance();
        if (insurance == null) {
            return "Unknown";
        }
        return insurance.getName();
    }
    // Now the method has four distinct exit points, making it hard to maintain. Even worse, the default value to be returned in case of a null, the string "Unknown", is repeated in three places—and (we hope) not misspelled! Furthermore, the process is error-prone. What if you forget to check whether one property could be null?

    /*
    Key point.
    Using null to represent the absence of a value is the wrong approach, it causes both theoretical and practical problems:
      - It’s a source of error. NullPointerException is by far the most common exception in Java.
      - It bloats your code. It worsens readability by making it necessary to fill your code with null checks that are often deeply nested.
      - It’s meaningless. It doesn’t have any semantic meaning, and in particular, it represents the wrong way to model the absence of a value in a statically typed language.
      - It breaks Java philosophy. Java always hides pointers from developers except in one case: the null pointer.
      - It creates a hole in the type system. null carries no type or other information, so it can be assigned to any reference type. This situation is a problem because when null is propagated to another part of the system, you have no idea what that null was initially supposed to be.
    What you need is a better way to model the absence and presence of a value.
    What are the alternatives to null in other languages?
      1. Groovy introduced a safe navigation operator [?]
            def carInsuranceName = person?.car?.insurance?.name
         The Groovy safe navigation operator allows you to safely navigate these potentially null references without throwing a NullPointerException by propagating the null reference through the invocations chain, returning a null in the event that any value in the chain is a null.
         If you solve this problem in this way, without wondering whether it’s correct for your algorithm or your data model to present a null value in that specific situation, you’re not fixing a bug but hiding it, making its discovery and remedy far more difficult for whoever will be called to work on it next time (likely you in the next week or month). You’re sweeping the dirt under the carpet. Groovy’s null-safe dereferencing operator is only a bigger and more powerful broom for making this mistake without worrying too much about its consequences.
      2. Haskell and Scala.
         Haskell includes a Maybe type, which essentially encapsulates an optional value. A value of type Maybe can contain a value of a given type or nothing. Haskell no concept of a null reference. Scala has a similar construct called Option[T] to encapsulate the presence or absence of a value of type T. Then you have to explicitly check whether a value is present or not using operations available on the Option type, which enforces the idea of “null checking.” You can no longer forget to check for null—because checking is enforced by the type system.
    Java 8 takes inspiration from this idea of an optional value by introducing a new class called java.util.Optional<T>.
    In above case if you know that a person may not have a car, for example, the car variable inside the Person class shouldn’t be declared type Car and assigned to a null reference when the person doesn’t own a car; instead, it should be type Optional<Car>
    When a value is present, the Optional class wraps it. Conversely, the absence of a value is modeled with an empty optional returned by the method Optional.empty. This static factory method returns a special singleton instance of the Optional class. You may wonder about the difference between a null reference and Optional.empty(). Semantically, they could be seen as the same thing, but in practice, the difference is huge. Trying to dereference a null invariably causes a NullPointerException, whereas Optional.empty() is a valid, workable object of type Optional that can be invoked in useful ways.
    An important, practical semantic difference in using Optionals instead of nulls is that in the first case, declaring a variable of type Optional<Car> instead of Car clearly signals that a missing value is permitted there.
    */
    // Let's rework the original model using Optional class:
    public class Person {
        private Optional<Car> car;
        private int age;
        public Optional<Car> getCar() { return car; }
        public int getAge() { return age; }
    }
    public class Car {
        private Optional<Insurance> insurance;
        public Optional<Insurance> getInsurance() { return insurance; }
    }
    // Insurance class stays the same since insurance company must have a name. So if you find one without a name, you’ll have to work out what’s wrong in your data instead of adding a piece of code to cover up this circumstance. Consistently using Optional values creates a clear distinction between a missing value that’s planned for and a value that’s absent only because of a bug in your algorithm or a problem in your data. It’s important to note that the intention of the Optional class isn’t to replace every single null reference. Instead, its purpose is to help you design more-comprehensible APIs so that by reading the signature of a method, you can tell whether to expect an optional value. You’re forced to actively unwrap an optional to deal with the absence of a value.


    // Patterns for adopting Optionals.
    //   Creating Optional objects.
    public class CreatingOptional {
        // Empty optional.
        Optional<Car> optCar = Optional.empty();
        // Optional from a non-null value. If car were null, a NullPointerException would be thrown immediately (rather than getting a latent error when you try to access properties of the car).
        Optional<Car> optCarNonNull = Optional.of(new Car());
        // Optional from nullable (object that may hold a null value). If car were null, the resulting Optional object would be empty.
        Optional<Car> optCarNullable1 = Optional.ofNullable(optCar.orElse(null));
        Optional<Car> optCarNullable2 = Optional.ofNullable(optCarNonNull.get());
        // In optCar.get() method [get] raises an exception [NoSuchElementException("No value present")] when the optional is empty, so using it in an ill-disciplined manner effectively re-creates all the maintenance problems caused by using null.
    }
    //   Extracting and transforming values from Optionals.
    public class ExtractingValues {
        /* The Optional class provides several instance methods to read the value contained by an Optional instance:
          - get(). The simplest but also the least safe of these methods. It returns the wrapped value if one is present and throws a NoSuchElementException otherwise. For this reason, using this method is almost always a bad idea unless you’re sure that the optional contains a value. In addition, this method isn’t much of an improvement on nested null checks.
          - orElse(T other). The method allows you to provide a default value when the optional doesn’t contain a value.
          - orElseGet(Supplier<? extends T> other). The lazy counterpart of the orElse method, because the supplier is invoked only if the optional contains no value. You should use this method when the default value is time-consuming to create (to gain efficiency) or you want the supplier to be invoked only if the optional is empty (when using orElseGet is vital).
          - or(Supplier<? extends Optional<? extends T>> supplier). Similar to the former orElseGet method, but it doesn’t unwrap the value inside the Optional, if present. It doesn’t perform any action and returns the Optional as it is when it contains a value, but lazily provides a different Optional when the original one is empty.
          - orElseThrow(Supplier<? extends X> exceptionSupplier). Similar to the get method in that it throws an exception when the optional is empty, but it allows you to choose the type of exception that you want to throw.
          - ifPresent(Consumer<? super T> consumer). Execute the action given as argument if a value is present; otherwise, no action is taken.
          - ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction). Differs from ifPresent by taking a Runnable that gives an empty-based action to be executed when the Optional is empty.
        */

        // Extracting and transforming values from Optionals with map.
        // The map operation applies the provided function to each element of a stream. You could also think of an Optional object as being a particular collection of data, containing at most a single element. If the Optional contains a value, the function passed as argument to map transforms that value. If the Optional is empty, nothing happens.
        Optional<Insurance> optInsurance = Optional.ofNullable(new Insurance());
        Optional<String> name = optInsurance.map(Insurance::getName);
        // To evaluate more complex expression like:
        //person.getCar().getInsurance().getName();
        // you can't use something like this
        //public String getCarInsuranceName(Person person) {
        //    Optional<Person> optPerson = Optional.of(person);
        //    Optional<String> name = optPerson.map(Person::getCar) // (1)
        //        .map(Car::getInsurance) // (2)
        //        .map(Insurance::getName);
        //    return name.orElse("Unknown");
        //}
        // because getCar returns an object of type Optional<Car> which means that the result of the map Car operation is an object of type Optional<Optional<Car>>, and therefore getInsurance is invalid because the outermost optional contains as its value another optional, which of course doesn’t support the Insurance method. So compile-error occurs.
        public String getCarInsuranceName(Optional<Person> person) {
            return person
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                // At this point the resulting optional will be empty if any of the methods in this invocation chain returns an empty optional, so as an option you can instead return some default value.
                .orElse("Unknown");
        }
        // Another example, imagine you were asked to find the cheapest insurance for a given person and car.
        public Insurance findCheapestInsurance(Person person, Car car) {
            // Queries services provided by the different insurance companies.
            // Compare all those data.
            Insurance cheapestCompany = new Insurance(); // dummy value to compile the method.
            return cheapestCompany;
        }
        // Then you want to work on nulls like this.
        public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
            if (person.isPresent() && car.isPresent()) {
                return Optional.of(findCheapestInsurance(person.get(), car.get()));
            } else {
                return Optional.empty();
            }
        }
        // But the better way would be.
        public Optional<Insurance> nullSafeFindCheapestInsuranceMap(Optional<Person> person, Optional<Car> car) {
            // If any person optional is empty, the flatMap won't execute the lambda expression passed to it. If all then this invocation will return an empty optional. So does the map method.
            return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
        }

        // Rejecting certain values with filter.
        Insurance insurance = new Insurance("MayBeCambridgeInsurance");
        void printIsCambridgeInsurance(Insurance insurance) {
            if (insurance != null && "CambridgeInsurance".equals(insurance.getName())) { println("ok"); }
        }
        // or
        void printIsCambridgeInsurance(Optional<Insurance> insurance) {
            insurance
                .filter(i -> "CambridgeInsurance".equals(i.getName()))
                .ifPresent(x -> println("ok"));
        }
        // Another example.
        public String getCarInsuranceNameForAge(Optional<Person> person, int minAge) {
            return person
                .filter(p -> p.getAge() >= minAge)
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
        }

        // Manipulating a stream of optionals.
        // The Optional’s stream() method (Java 9), allows you to convert an Optional with a value to a Stream containing only that value or an empty Optional to an equally empty Stream . This technique can be particularly convenient in a common case: when you have a Stream of Optional and need to transform it into another Stream containing only the values present in the nonempty Optional of the original Stream.
        // Here's a method that’s passed with a List<Person> and that should return a Set<String> containing all the distinct names of the insurance companies used by the people in that list who own a car.
        public Set<String> getCarInsuranceNames(List<Person> persons) {
            return persons.stream()
                // producing Stream of Optional<Optional<Car>>.
                .map(Person::getCar)
                // producing Stream of Optional<Optional<Insurance>>.
                .map(optCar -> optCar.flatMap(Car::getInsurance))
                // producing Stream of Optional<String>.
                .map(optInsurance -> optInsurance.map(Insurance::getName))
                // Filtering the empty Optionals and unwrapping the values contained in the remaining with the explicit filter and the map using the isPresent and the get on the Optional;
                //.filter(Optional::isPresent)
                //.map(Optional::get)
                // or just using the flatMap and the stream method on the Optional.
                // producing Stream of String.
                .flatMap(Optional::stream)
                // and finally collect to Set.
                .collect(toSet());
        }
    }


    // For backward-compatibility reasons, old Java APIs can’t be changed to make proper use of optionals, but all is not lost. You can fix, or at least work around, this issue by adding to your code small utility methods that allow you to benefit from the power of optionals. You see how to do this with a couple of practical examples.
    // An existing Java API almost always returns a null to signal that the required value is absent or that the computation to obtain it failed for some reason.
    Map<String, String> map = Map.of("key1", "value1", "key2", " ");
    // Returns null.
    String value = map.get("key");
    Optional<String> valueOpt = Optional.ofNullable(map.get("key"));

    // Exceptions vs. Optional.
    // Instead of encapsulating the ugly try/catch logic every time you use something like Integer.parseInt, you can create utility method stringToInt(String s) and then collect several similar methods in a utility class, called say OptionalUtility.
    public static Optional<Integer> stringToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public class WorkingWithNulls1 {
        /*
        Imagine you have some Properties that are passed as configuration arguments to your program.
        Also suppose that your program needs to read a value from these Properties and interpret it as a duration in seconds. Because a duration has to be a positive (>0) number, you’ll want a method with the signature:
            public int readDuration(Properties props, String name)
         so that when the value of a given property is a String representing a positive integer, the method returns that integer, but it returns zero in all other cases.
            assertEquals(5, readDuration(props, "a"));
            assertEquals(0, readDuration(props, "b"));
            assertEquals(0, readDuration(props, "c"));
            assertEquals(0, readDuration(props, "d"));
         where "a" = "5", "b" = "true", "c" = "-3".
        */
        public static int readDurationImperative(Properties props, String name) {
            String value = props.getProperty(name);
            if (value != null) {
                try {
                    int i = Integer.parseInt(value);
                    if (i > 0) {
                        return i;
                    }
                } catch (NumberFormatException nfe) { }
            }
            return 0;
        }
        public static int readDurationWithOptional(Properties props, String name) {
            return Optional.ofNullable(props.getProperty(name))
                .flatMap(OptionalMain::stringToInt)
                .filter(i -> i > 0).orElse(0);
        }
        public static void workingWithNulls1() {
            Properties props = new Properties();
            props.setProperty("a", "5");
            props.setProperty("b", "true");
            props.setProperty("c", "-3");
            println(readDurationWithOptional(props, "a"));
            println(readDurationWithOptional(props, "b"));
            println(readDurationWithOptional(props, "c"));
            println(readDurationWithOptional(props, "d"));
        }
    }

    public class WorkingWithNulls2 {
        public void workingWithNulls2() {
            Map<String, Long> moviesToCount = new HashMap<>();
            String movieName = "JamesBond";
            long count = moviesToCount.get(movieName);
            if (Long.valueOf(count) == null) {
                moviesToCount.put(movieName, 1L);
            } else {
                moviesToCount.put(movieName, count + 1L);
            }
        }
    }

    public class WorkingWithNulls3 {
        public static void workingWithNulls3() {
            println(max(Optional.of(3), Optional.of(5)));
            println(max(Optional.empty(), Optional.of(5)));

            Optional<Integer> opt1 = Optional.of(5);
            Optional<Integer> opt2 = opt1.or(() -> Optional.of(4));

            println(Optional.of(5).or(() -> Optional.of(4)));
        }
        public static Optional<Integer> max(Optional<Integer> i, Optional<Integer> j) {
            return i.flatMap(a -> j.map(b -> Math.max(a, b)));
        }
    }
}
