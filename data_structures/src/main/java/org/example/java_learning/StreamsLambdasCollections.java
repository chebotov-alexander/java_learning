package org.example.java_learning;

import static java.util.Map.entry;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsLambdasCollections {

    public class CreatingCollections {
        // Java 9 introduced a few convenient ways to create small collection objects - Collection factories.
        public static void creatingLists() {
            // Creating a list the old-fashioned way.
            List<String> friends1_1 = new ArrayList<>();
            friends1_1.add("Raphael");
            friends1_1.add("Olivia");
            friends1_1.add("Thibaut");
            List<String> friends1_2 = new ArrayList<>(){{
                add("Raphael");
                add("Olivia");
                add("Thibaut");
            }};
            // Using Arrays.asList() factory method.
            List<String> friends2_1 = Arrays.asList("Raphael", "Olivia");
            // The result is a fixed-sized list that you can update, but not add elements to or remove elements from. Attempting to add elements, results in an UnsupportedModificationException, but updating by using the method set is allowed.
            friends2_1.set(0, "Richard");
            try {
                friends2_1.add("Thibaut");
            }
            catch (UnsupportedOperationException e) {
                System.out.println("As expected, we can't add items to a list created with Arrays.asList()");
            }
            // To solve this:
            List<String> friends2_2 = new ArrayList<>(Arrays.asList("Raphael", "Olivia"));

            // Creating a Set from a List. There is no Arrays.asSet() factory method, so the HashSet constructor, which accepts a List, could be used.
            Set<String> friends3 = new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));
            // Creating a Set from a Stream.
            Set<String> friends4 = Stream.of("Raphael", "Olivia", "Thibaut").collect(Collectors.toSet());
            // Both last solutions, however, are far from elegant and involve unnecessary object allocations behind the scenes. Also note that you get a mutable Set as a result.
            // How about Map? There’s no elegant way of creating small maps.

            // So here comes factories.
            // Creating a List with List.of().
            List<String> friends5_1 = List.of("Raphael", "Olivia", "Thibaut");
            // Result list is immutable.
            try {
                friends5_1.add("Richard");
                // or
                friends5_1.set(0, "Richard");
            }
            catch (UnsupportedOperationException e) {
                System.out.println("As expected, we can't add or set items to a immutable list created with List.of().");
            }
            // Nothing is stopping you from having elements that are mutable themselves. If you need a mutable list, you can still instantiate one manually. Finally, note that to prevent unexpected bugs and enable a more-compact internal representation, null elements are disallowed.
            // Make immutable list mutable
            List<String> friends5_2 = new ArrayList<>(friends5_1);
            friends5_2.add("Richard");
            friends5_2.sort(Comparator.comparing(Object::toString));
            System.out.println("My sorted list: " + String.join(",", friends5_2));

            // Creating a Set with Set.of().
            Set<String> friends6 = Set.of("Raphael", "Olivia", "Thibaut");
            try {
                friends6 = Set.of("Raphael", "Olivia", "Olivia");
                System.out.println("We shouldn't get here...");
            }
            catch (IllegalArgumentException e) {
                System.out.println("As expected, duplicate items are not allowed with Set.of().");
            }

            // Creating a Map with Map.of().
            Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
            // This method is convenient if you want to create a small map of up to ten keys and values. To go beyond this, use the alternative factory method called Map.ofEntries, which takes Map.Entry<K, V> objects but is implemented with varargs.
            Map<String, Integer> ageOfFriends2 = Map.ofEntries(
                entry("Raphael", 30),
                entry("Olivia", 25),
                entry("Thibaut", 26));
            // Both ways create immutable maps.
        }
    }

    public class WorkingWithCollections {
        public static void workingWithCollections() {
            // Working with List and Set.
            // Consider the following code, which tries to remove transactions that have a reference code starting with a digit:
            List<Transaction> transactions = new ArrayList<>();
            transactions.add(new Transaction(new Trader("t1", "c1"), 2000, 100));
            transactions.add(new Transaction(new Trader("t2", "c2"), 2000, 200));
            transactions.add(new Transaction(new Trader("t3", "c2"), 2000, 300));
            for (Transaction transaction : transactions) {
                if(Character.isDigit(transaction.getTrader().getName().charAt(0))) {
                    transactions.remove(transaction);
                }
            }
            // Unfortunately, this code may result in a ConcurrentModificationException. Why? Under the hood, the for-each loop uses an Iterator object, so the code executed is as follows:
            for (
                Iterator<Transaction> iterator = transactions.iterator();
                iterator.hasNext();
            ) {
                Transaction transaction = iterator.next();
                if(Character.isDigit(transaction.getTrader().getName().charAt(0))) {
                    // Problem we are iterating and modifying the collection through two separate objects.
                    transactions.remove(transaction);
                }
            }
            // Two separate objects manage the collection: first - the Iterator object, which is querying the source by using next() and hasNext(); and second - the Collection object itself, which is removing the element by calling remove(). As a result, the state of the iterator is no longer synced with the state of the collection, and vice versa. To solve this problem, you have to use the Iterator object explicitly and call its remove() method:
            for (
                Iterator<Transaction> iterator = transactions.iterator();
                iterator.hasNext();
            ) {
                Transaction transaction = iterator.next();
                if(Character.isDigit(transaction.getTrader().getName().charAt(0))) {
                    iterator.remove();
                }
            }
            // removeIf method is not only simpler but also protects from these bugs. It takes a predicate indicating which elements to remove:
            transactions.removeIf(transaction -> Character.isDigit(transaction.getTrader().getName().charAt(0)));
            // Transforming list items with a Stream.
            // The replaceAll method on the List interface lets you replace each element in a list with a new one.
            List<String> referenceCodes = Arrays.asList("a12", "C14", "b13");
            referenceCodes.stream()
                .map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))
                .collect(Collectors.toList())
                .forEach(System.out::println);
            // This code results in a new collection of strings, so the original List remains unchanged.
            // To update the existing collection you can use a ListIterator object as follows (supporting a set() method to replace an element:
            for (
                ListIterator<String> iterator = referenceCodes.listIterator();
                iterator.hasNext();
            ) {
                String code = iterator.next();
                iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
            }
            // Mutating a list with replaceAll().
            referenceCodes = Arrays.asList("a12", "C14", "b13");
            referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));

            // Working with Maps.
            // Iterating a map with a for loop.
            Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
            for (Map.Entry<String, Integer> entry: ageOfFriends.entrySet()) {
                String friend = entry.getKey();
                Integer age = entry.getValue();
                System.out.println(friend + " is " + age + " years old");
            }
            // Iterating a map with forEach(), do this instead.
            ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
            // Sorting.
            // Entry.comparingByValue and Entry.comparingByKey utilities let you sort the entries of a map by values or keys.
            // Iterating a map sorted by keys through a Stream:
            Map<String, String> favouriteMovies1 = Map.ofEntries(
                entry("Raphael", "Star Wars"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James Bond")
            );
            favouriteMovies1.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(System.out::println);
            Optional<String> mostOftenString = List.of("a", "a", "b", "c", "c", "c").stream()
                .collect(groupingBy(s -> s, counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
            // Nulls.
            // When the key you’re looking up isn’t present, you receive a null reference that you have to check against to prevent a NullPointerException.
            // One way to handle nulls is to provide a default value using getOrDefault.
            System.out.println(favouriteMovies1.getOrDefault("Olivia", "Matrix"));
            System.out.println(favouriteMovies1.getOrDefault("Thibaut", "Matrix"));
            // Note that if the key existed in the Map but was accidentally associated with a null value, getOrDefault can still return null. Also note that the expression you pass as a fallback is always evaluated, whether the key exists or not.
            // The better way is using Optional about which later.

            // Compute patterns.
            // Sometimes, you want to perform an operation conditionally and store its result, depending on whether a key is present or absent in a Map. You may want to cache the result of an expensive operation given a key, for example. If the key is present, there’s no need to recalculate the result. Three operations can help:
            //  - computeIfAbsent. If the specified key is not already associated with a value (or is mapped to null), attempts to compute its value using the given mapping function and enters it into this map unless null. If the mapping function returns null, no mapping is recorded.
            //  - computeIfPresent. If the specified key is present and non-null, calculate a new value for it and add it to the Map. If the remapping function returns null, the mapping is removed. The remapping function should not modify this map during computation.
            //  - compute. This operation calculates a new value for a given key and stores it in the Map. If the remapping function returns null, the mapping is removed (or remains absent if initially absent). The remapping function should not modify this map during computation.
            Map<String, List<String>> friendsToMovies2 = new HashMap<>();
            // Adding a friend and movie in a verbose way.
            String friend2 = "Raphael";
            List<String> movies = friendsToMovies2.get(friend2);
            if (movies == null) {
                movies = new ArrayList<>();
                friendsToMovies2.put(friend2, movies);
            }
            movies.add("Star Wars");
            // Adding a friend and movie using computeIfAbsent().
            friendsToMovies2.clear();
            friendsToMovies2.computeIfAbsent("Raphael", name -> new ArrayList<>())
                .add("Star Wars");
            // Another example, the cache one is here.
            CacheExample.main(new String[0]);

            // Remove patterns.
            Map<String, String> favouriteMovies3 = new HashMap<>();
            favouriteMovies3.put("Raphael", "Jack Reacher 2");
            favouriteMovies3.put("Cristina", "Matrix");
            favouriteMovies3.put("Olivia", "James Bond");
            String key = "Olivia";
            String value = "James Bond";
            // Removing an unwanted entry the cumbersome way with user defined method.
            boolean result = remove(favouriteMovies3, key, value);
            // Put back the deleted entry for the second test
            favouriteMovies3.put("Olivia", "James Bond");
            // Removing an unwanted the easy way.
            favouriteMovies3.remove(key, value);
            // Conditional remove.
            // Removing entry the cumbersome way.
            Map<String, Integer> movies2 = new HashMap<>();
            movies2.put("JamesBond", 20);
            movies2.put("Matrix", 15);
            movies2.put("Harry Potter", 5);
            Iterator<Map.Entry<String, Integer>> iterator = movies2.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                if(entry.getValue() < 10) { iterator.remove(); }
            }
            // The easy way.
            movies2.entrySet().removeIf(entry -> entry.getValue() < 10);

            // Replacement patterns.
            // Some methods to replace the entries inside a Map:
            //  - replaceAll. Replaces each entry’s value with the result of applying a BiFunction. This method works similarly to replaceAll on a List.
            //  - replace. Lets you replace a value in the Map if a key is present. An additional overload replaces the value only if the key is mapped to a certain value.
            Map<String, String> favouriteMovies4 = new HashMap<>();
            favouriteMovies4.put("Raphael", "Star Wars");
            favouriteMovies4.put("Olivia", "james bond");
            favouriteMovies4.replaceAll((friend, movie) -> movie.toUpperCase());

            // Merge.
            Map<String, String> family = Map.ofEntries(
                entry("Teo", "Star Wars"),
                entry("Cristina", "James Bond")
            );
            Map<String, String> friends = Map.ofEntries(
                entry("Raphael", "Star Wars")
            );
            // Merging the old way");
            Map<String, String> everyone = new HashMap<>(family);
            everyone.putAll(friends);
            // This code works as expected as long as you don’t have duplicate keys. If you require more flexibility in how values are combined, you can use the merge method. This  method takes a BiFunction to merge values that have a duplicate key. Suppose that Cristina is in both the family and friends maps but with different associated movies:
            Map<String, String> friends2 = Map.ofEntries(
                entry("Raphael", "Star Wars"),
                entry("Cristina", "Matrix"));
            Map<String, String> everyone2 = new HashMap<>(family);
            friends2.forEach(
                (k, v) -> everyone2.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2));
            // You can also use merge to implement initialization checks. Suppose that you have a Map for recording how many times a movie is watched. You need to check that the key representing the movie is in the map before you can increment its value:
            Map<String, Long> moviesToCount = new HashMap<>();
            String movieName = "JamesBond";
            long count = moviesToCount.get(movieName);
            if (Long.valueOf(count) == null) {
                moviesToCount.put(movieName, 1L);
            }
            else {
                moviesToCount.put(movieName, count + 1L);
            }
            // This code can be rewritten as:
            moviesToCount.merge(movieName, 1L, (k, v) -> v + 1L);
            // The second argument to merge in this case is 1L. The Javadoc specifies that this argument is "the non-null value to be merged with the existing value associated with the key or, if no existing value or a null value is associated with the key, to be associated with the key". Because the value returned for that key is null, the value 1 is provided the first time. The next time, because the value for the key was initialized to the value of 1, the BiFunction is applied to increment the count.


            /*
            ConcurrentHashMap.
            The ConcurrentHashMap class was introduced to provide a more modern HashMap, which is also concurrency friendly. ConcurrentHashMap allows concurrent add and update operations that lock only certain parts of the internal data structure. Thus, read and write operations have improved performance compared with the synchronized Hashtable alternative. Note that the standard HashMap is unsynchronized.
            ConcurrentHashMap supports three notable kinds of operations, reminiscent of what you saw with streams:
              - forEach. Performs a given action for each (key, value).
              - reduce. Combines all (key, value) given a reduction function into a result.
              - search. Applies a function on each (key, value) until the function produces a non-null result.
            Each kind of operation supports four forms, accepting functions with keys, values, Map.Entry, and (key, value) arguments:
              - Operates with keys and values (forEach, reduce, search).
              - Operates with keys (forEachKey, reduceKeys, searchKeys).
              - Operates with values (forEachValue, reduceValues, searchValues).
              - Operates with Map.Entry objects (forEachEntry, reduceEntries, searchEntries).
            Note that these operations don’t lock the state of the ConcurrentHashMap; they operate on the elements as they go along. The functions supplied to these operations shouldn’t depend on any ordering or on any other objects or values that may change while computation is in progress.
            In addition, you need to specify a parallelism threshold for all these operations. The operations execute sequentially if the current size of the map is less than the given threshold. A value of 1 enables maximal parallelism using the common thread pool. A threshold value of Long.MAX_VALUE runs the operation on a single thread. You generally should stick to these values unless your software architecture has advanced resource-use optimization.
            */
            // Find the maximum value in the map.
            ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
            long parallelismThreshold = 1;
            Optional<Long> maxValue1 = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
            // There is primitive specializations for int, long, and double for each reduce operation: reduceValuesToInt, reduceKeysToLong, and so on.
        }
        private static <K, V> boolean remove(Map<K, V> favouriteMovies, K key, V value) {
            if (favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {
                favouriteMovies.remove(key);
                return true;
            }
            return false;
        }
    }
}
