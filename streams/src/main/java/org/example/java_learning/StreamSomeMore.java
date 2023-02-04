package org.example.java_learning;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StreamSomeMore {
    public static class StreamFiltering {
        // Filtering with a predicate  takes as argument a predicate (a function returning a boolean) and returns a stream including all elements that match the predicate.
        List<Dish> vegetarianMenu = Dish.menu.stream()
            .filter(Dish::isVegetarian)
            .collect(toList());

        // Filtering unique elements with a method called distinct that returns a stream with unique elements (according to the implementation of the hashcode and equals methods of the objects produced by the stream).
        void filterUniqueElements() {
            List<Integer> uniqueEvenNumbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
            uniqueEvenNumbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct() // using the equals method for the comparison
                .forEach(System.out::println);
        }
    }

    public static class SlicingStream {
        // Let's see how to select and skip elements in a stream in different ways. There are operations available that let you efficiently select or drop elements using a predicate, ignore the first few elements of a stream, or truncate a stream to a given size.

        // Slicing using a predicate (Java9).


    }
}
