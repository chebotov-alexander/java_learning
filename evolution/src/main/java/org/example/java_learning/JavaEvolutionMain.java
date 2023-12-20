package org.example.java_learning;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.constant.Constable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import com.sun.net.httpserver.Request;
import io.reactivex.annotations.*;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.example.java_learning.Util.println;

/**
 * Represents most significant changes between java versions from the perspective of most of Java Developers.
 */
public class JavaEvolutionMain {
/*
https://javaalmanac.io/
https://ondro.inginea.eu/index.php/new-features-in-java-versions-since-java-8/
https://en.wikipedia.org/wiki/Java_version_history
https://www.baeldung.com/project-jigsaw-java-modularity
https://nipafx.dev/will-there-be-module-hell/
*/
    public static void main(String[] args) {
        Concurrency.vThreads();
    }
/*
Since worth all maintaining issues.

Since Java 8:
  - Interface Default and Static Methods.
  - Method References.
  - Optional class.
  - Lambda expressions.
  - Functional interfaces.
  - Stream API.
  - Effectively Final Variables.
  - Repeating Annotations.
  - New Date Time API.
Since Java 9:
  - Flow API (reactive streams).
  - Java Platform Module System (modules).
  - Collection factory methods: List.of(a, b, c); Set.of(d, e, f, g); Map.of(k1, v1, k2, v2).
  - Stream API improvements (takeWhile, dropWhile, ofNullable, iterate with condition).
  - this.getClass().getPackageName().
  - Process API updates (detailed info about processes, e.g. ID, onExit, destroy).
  - New methods in CompletableFuture API (delay, timeout).
  - Interface private methods.
Since Java 10:
  - var type allowed for local variables:
        var length = str.length()
Since Java 11:
  - var type allowed in Lambda Parameters:
        (@NonNull var x) -> process(x)
  - New String methods (repeat, isBlank, strip, lines).
Since Java 12:
  - New String methods (indent, transform).
  - CompactNumberFormat class.
Since Java 14/12:
  - Switch expressions:
        boolean isWeekend = switch (day) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
Since Java 15/13:
  - Text blocks:
        String query =
            """
            SELECT "EMP_ID", "LAST_NAME"
            FROM "EMPLOYEE_TB";
            """
Since Java 15:
  - New String methods (formatted, stripIndent, translateEscapes).
Since Java 16/14:
  - Pattern Matching for instanceof:
        if (x instanceOf String s) { String a = s; }
  - Record type – data classes with implicit getters, constructor, equals, hashCode and toString methods:
        record Point(int x, int y) { }
Since Java 16:
  - Static members in inner classes (part of Record type):
        new Object() {
            static record MyData(String data) {};
            public static final int CONSTANT = 1;
        };
Since Java 17/15:
  - Sealed classes (inheritance only for allowed classes):
        public abstract sealed class Shape permits Circle, Rectangle, Square {...}
Since Java 17:

In preview only stage:
Since Java 16:
  - Foreign Function & Memory API (an alternative to JNI).
Since Java 17:
  - Pattern Matching for switch – like instanceof for switch; switch is an expression and can be assigned:
        String result = switch (o) {
            case null -> null;
            case 0 -> throw new RuntimeException("Cannot be 0"); // Special cases
            case Integer i when i > 0 -> "Positive number";
            case Integer i -> "Negative number"; // 0 and positive numbers handled by above rules
            case String s -> s;
            case Point p -> p.toString();
            case int[] ia -> "Array length" + ia.length;
            default -> "Something else";
        }
Since Java 19:
  - Structured concurrency:
        try (var scope = new ShutdownOnFailure()) {
            Future<String> user = scope.fork(() -> findUser());
            Future<Integer> order = scope.fork(() -> fetchOrder());
        }
        //both threads are terminated here, outside of try block
  - Record patterns:
        record Point(int x, int y) {}
        …
        if (o instanceof Point(int x, int y)) {
            System.out.println( x + y );
        }
  - Virtual threads:
        Thread.startVirtualThread(runnable);
        Thread.ofVirtual().name("duke").unstarted(runnable);
        Executors.newVirtualThreadPerTaskExecutor();
        Executors.newThreadPerTaskExecutor(threadFactory);
Since Java 20:
  - Scoped Values (Incubator)
  - Record Patterns (Second Preview)
  - Pattern Matching for switch (Fourth Preview)
  - Foreign Function & Memory API (Second Preview)
  - Virtual Threads (Second Preview)
  - Structured Concurrency (Second Incubator)
Since Java 21:
  - String Templates (Preview)
  - Sequenced Collections
  - Generational ZGC
  - Record Patterns
  - Pattern Matching for switch
  - Foreign Function & Memory API (Third Preview)
  - Unnamed Patterns and Variables (Preview)
  - Virtual Threads
  - Unnamed Classes and Instance Main Methods (Preview)
  - Scoped Values (Preview)
  - Structured Concurrency (Preview)

Future features:
  - Project Loom: Virtual threads, a lightweight user-mode scheduled alternative to standard OS managed threads. Virtual threads are mapped to OS threads in a many-to-many relationship, in contrast to the many-to-one relationship from the original green threads implementation in early versions of Java.
    https://openjdk.org/projects/loom/
    https://cr.openjdk.org/~rpressler/loom/Loom-Proposal.html
  - Project Panama: Improved interoperability with native code, to enable Java source code to call functions and use data types from other languages, in a way that is easier and has better performance than today. Vector API (a portable and relatively low-level abstraction layer for SIMD programming) is also developed under Project Panama umbrella.
    https://openjdk.java.net/projects/panama/
  - Project Valhalla: Value types, objects without identity but with an efficient memory layout and leading to better results of escape analysis.
    https://openjdk.org/projects/valhalla/



JVM 17 vs JVM 8 changes:
  Most changes affect Java performers, code structure and bug fixe. Most significant of all are:
  - new Garbage Collectors: G1, ZGC, Shenandoah.
  - Java 9 modules.
  https://blog.csanchez.org/2017/05/31/running-a-jvm-in-a-container-without-getting-killed/
  https://nipafx.dev/will-there-be-module-hell/
  https://www.baeldung.com/project-jigsaw-java-modularity
  https://www.baeldung.com/java-flight-recorder-monitoring
  Performance of Modern Java on Data-Heavy Workloads: Real-Time Streaming https://jet-start.sh/blog/2020/06/09/jdk-gc-benchmarks-part1

*/
    public static class VarKeyword {
        // https://openjdk.org/jeps/286
        // Var keyword allows local variables to be declared in a more concise manner.
        private static List<String> getListofStrings() {return Arrays.asList("");}
        // Java 8:
        public static void java8() {
            Map<String, List<String>> myMap = new HashMap<String, List<String>>();
            List<String> myList = getListofStrings();
        }
        // java 10:
        public static void java10() {
            var myMap = new HashMap<String, List<String>>();
            var myList = getListofStrings();
        }
        // However, it's not possible to assign a lambda to a variable using var keyword:
        public static void java10NoLambdasAssignment() {
            Function<Integer, Boolean> fn1 = x -> x > 0;
            //var fn2 = x -> x > 0;
        }
        // Although, it's possible to use the var in lambda expressions:
        // https://openjdk.org/jeps/323
        public static void java10LambdaExpessions() {
            boolean isThereAneedle = getListofStrings().stream()
                .anyMatch((@NonNull var s) -> s.equals(""));
        }
    }

    public static class Records {
        /*
        https://openjdk.org/jeps/359 Preview
        https://openjdk.org/jeps/384 Second Preview
        https://openjdk.org/jeps/395
        Record is roughly equivalent to Lombok’s @Value. In terms of language, it’s kind of similar to an enum. However, instead of declaring possible values, you declare the fields. Java generates some code based on that declaration and is capable of handling it in a better, optimized way. Like enum, it can’t extend or be extended by other classes, but it can implement an interface and have static fields and methods. Contrary to an enum, a record can be instantiated with the new keyword.
        A record acquires many standard members automatically:
          - A private final field for each component of the state description;
          - A public read accessor method for each component of the state description, with the same name and type as the component;
          - A public constructor, whose signature is the same as the state description, which initializes each field from the corresponding argument;
          - Implementations of equals and hashCode that say two records are equal if they are of the same type and contain the same state;
          - An implementation of toString that includes the string representation of all the record components, with their names.
        Any automatically generated methods can be declared manually by the programmer. A set of constructors can be also declared. Moreover, in constructors, all fields that are definitely unassigned are implicitly assigned to their corresponding constructor parameters. It means that the assignment can be skipped entirely in the constructor.
        */
        interface HasAccountNumber {};
        record StoreAccount (String bankName, String accountNumber) implements HasAccountNumber {};
        record BankAccount (String bankName, String accountNumber) implements HasAccountNumber {
            public BankAccount { // No parameters necessary here, compilers does it automatically.
                if (accountNumber == null || accountNumber.length() != 26) {
                    throw new IllegalArgumentException("Account number invalid");
                }
                // No assignment necessary here.
            }
        }
        // Records can act as a local support structure, aka tuple:
        List<Customer> findTopScoredCustomer(List<Customer> allCustomers) {
            return allCustomers.stream()
                .sorted(comparing(customer -> calculateCustomerScore(customer), reverseOrder()))
                .limit(10)
                .toList();
        }
        List<Customer> findTopScoredCustomerEfficient(List<Customer> allCustomers) {
            record CustomerScore(Customer customer, double score) {}
            return allCustomers.stream()
                .map(customer -> new CustomerScore(customer, calculateCustomerScore(customer)))
                .sorted(comparing(CustomerScore::score, reverseOrder()))
                .map(CustomerScore::customer)
                .limit(10)
                .toList();
        }
        static double calculateCustomerScore(Customer customer) {
            // Heavy computation.
            return 0;
        }
    }

    public static class PatternMatching {
        // https://openjdk.org/projects/amber/design-notes/patterns/pattern-matching-for-java
        public static class Sealed {
            /*
            https://openjdk.org/jeps/409
            Sealed Classes adds sealed classes and interfaces that restrict which other classes or interfaces may extend or implement them. Only those classes specified in a permits clause may extend the class or interface.
            */
            // If you have a hierarchy like this:
            public abstract sealed class Animal permits Dog, Cat, Parrot, Goldfish {}
            // Absence of [permits] means only inner classes can exist.
            public sealed class Dog extends Animal {
                public static final class Collie extends Dog {
                    public Collie() { new Sealed().super(); }
                    void woof() {}
                }
                // ...
            }
            // [final] means no more subclasses.
            public final class Parrot extends Animal { void chirp() {} }
            public final class Goldfish extends Animal { void burp() {} }
            // [non-sealed] means any class can extend the Cat.
            public non-sealed class Cat extends Animal { void meow() {} }
            // You will now be able to do this:
            public static void sealedWithInstanceof(Animal animal) {
                if (animal instanceof Dog.Collie d) { d.woof(); }
                else if (animal instanceof Cat c) { c.meow(); }
            }
            // And you won’t get the "no default" warning, which happens even if you are covered all the options that the domain accepted (with default you cover newly created ones).
            // Permitted classes must be in the same package as sealed class, but can be in different files or in different packages but in the same module.

        }
        public static class SwitchExpressions {
            enum Pet { DOG, CAT, PARROT, GOLDFISH }
            int getLegsNumber1(Pet pet) {
                int legs;
                switch (pet) {
                    case DOG:
                    case CAT:
                        legs = 4;
                        break;
                    case PARROT:
                        legs = 2;
                        break;
                    case GOLDFISH:
                        legs = 0;
                        break;
                    default:
                        throw new AssertionError();
                }
                return legs;
            }
            int getLegsNumber2(Pet pet) {
                int legs;
                switch (pet) {
                    case DOG, CAT:
                        legs = 4;
                        break;
                    // ...
                    default:
                        throw new AssertionError();
                }
                return legs;
            }
            int getLegsNumber3(Pet pet) {
                int legs;
                switch (pet) {
                    case DOG, CAT -> legs = 4;
                    case PARROT -> legs = 2;
                    case GOLDFISH -> legs = 0;
                    default -> throw new AssertionError();
                }
                return legs;
            }
            int getLegsNumber4(Pet pet) {
                return switch (pet) {
                    case DOG, CAT -> 4;
                    case PARROT -> 2;
                    case GOLDFISH -> 0;
                    default -> throw new AssertionError();
                };
            }
            int getLegsNumber5(Pet pet) {
                return switch (pet) {
                    case DOG, CAT -> 4;
                    case PARROT -> 2;
                    case GOLDFISH -> 0;
                    // automatic exception
                };
            }

            DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
            boolean freeDay1 = switch (dayOfWeek) {
                case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> false;
                case SATURDAY, SUNDAY -> true;
            };
            // Even more can be achieved with the new [yield] keyword that allows returning a value from inside a code block. It’s virtually a return that works from inside a case block and sets that value as a result of its switch. It can also accept an expression instead of a single value.
            boolean freeDay2 = switch (dayOfWeek) {
                case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> {
                    System.out.println("Work work work");
                    yield false;
                }
                case SATURDAY, SUNDAY -> {
                    System.out.println("Yey, a free day!");
                    yield true;
                }
            };

            static String asString(Object value) {
                return switch (value) {
                    case Enum<?> e -> e.getDeclaringClass().getSimpleName() + "." + e.name();
                    case Collection c -> "Collection [size = %d]".formatted(c.size());
                    case Object[] arr -> "Array [length = %d]".formatted(arr.length);
                    case String s when s.length() > 50 -> '"'+s.substring(0, 50)+"...\"";
                    // The next must be on at least one line lower, i.e. after [case String s] because [CharSequence] is more general than String.
                    //case CharSequence cs -> '"' + cs.length() + '"';
                    case String s -> '"' + s + '"';
                    case null -> "null";
                    default -> value.toString();
                };
            }

            public static class Instanceof {
                public static void fromThis(Object obj) {
                    if (obj instanceof VarKeyword) {
                        VarKeyword myObject = (VarKeyword) obj;
                        // … further logic
                    }
                }

                public static void toThis(Object obj) {
                    if (obj instanceof VarKeyword myObject) {
                        // No need for extra line of code declaring local variable like myObject,  and no type casting as well.
                        // ...
                    }
                    // Moreover, the declared variable can be used in the same if condition, like this:
                    if (obj instanceof VarKeyword myObject && myObject.getListofStrings().isEmpty()) {
                        // ...
                    }
                }
                public static Constable nullsAndDefault(Object obj) {
                    return switch (obj) {
                        case null -> "Null";
                        case String s -> String.format("String %s", s);
                        case Long l -> String.format("long %d", l);
                        case Double d -> String.format("double %f", d);
                        // Refining patterns.
                        case Integer i when i > 0 -> String.format("positive int %d", i);
                        case Integer i when i == 0 -> String.format("zero int %d", i);
                        case Integer i when i < 0 -> String.format("negative int %d", i);
                        default -> obj.toString();
                    };
                }
                public static void records(Object obj) {
                    record Rectangle(int x, int y, int w, int h) {}
                    // Such patterns can include nested patterns, where the components of records are themselves records, allowing patterns to match more object graphs.
                    if (obj instanceof Rectangle(int x, int y, int w, int h)) {
                        println(w * h);
                    }
                }
                // Using annotations and making local variable final.
                @Target(ElementType.LOCAL_VARIABLE)
                @interface LocalAnno {}
                void  printIfString(Object obj) {
                    if (obj instanceof @LocalAnno final String str) {
                        println(str.trim());
                    }
                }
            }
            // Using switch is much cleaner and maintainable than using standard Visitor pattern approach:
            interface NodeVisitor<T> {
                T visitConst(Const c);
                T visitPlus(Plus p);
                T visitMultiply(Multiply m);
            }
            sealed interface Node {
                <T> T accept(NodeVisitor<T> visitor);
            }
            record Const(int value) implements Node {
                public <T> T accept(NodeVisitor<T> visitor) {return visitor.visitConst(this);}
            }
            record Plus(Node left, Node right) implements Node {
                public <T> T accept(NodeVisitor<T> visitor) {return visitor.visitPlus(this);}
            }
            record Multiply(Node left, Node right) implements Node {
                public <T> T accept(NodeVisitor<T> visitor) {return visitor.visitMultiply(this);}
            }
            // Visitor pattern:
            static int evaluate1(Node node) {
                return node.accept(new NodeVisitor<>() {
                    public Integer visitConst(Const c) {
                        return c.value();
                    }
                    public Integer visitPlus(Plus p) {
                        return evaluate1(p.left()) + evaluate1(p.right());
                    }
                    public Integer visitMultiply(Multiply m) {
                        return evaluate1(m.left()) * evaluate1(m.right());
                    }
                });
            }
            // but [instanceof] is better:
            static int evaluate2(Node node) {
                if (node instanceof Const c) {
                    return c.value();
                } else if (node instanceof Plus p) {
                    return evaluate2(p.left()) + evaluate2(p.right());
                } else if (node instanceof Multiply m) {
                    return evaluate2(m.left()) * evaluate2(m.right());
                }
                throw new AssertionError("Unknown node type: " + node.getClass());
            }
            // but pattern matching with [switch] is even better:
            static int evaluate3(Node node) {
                return switch (node) {
                    case Const c -> c.value();
                    case Plus p -> evaluate3(p.left()) + evaluate3(p.right());
                    case Multiply m -> evaluate3(m.left()) * evaluate3(m.right());
                    // No default makes you pay attention to the code if new Node subclasses emerges, something Visitor pattern can't give, also no necessary exception needed.
                };
            }
        }
    }
    public static class TextBlocks {
        // https://openjdk.org/jeps/378
        String onLinerBefore = "Ignored New Lines";
        String onLinerAfter = """
            Ignored \
            New Lines
            """;
        String htmlBlockBefore =
            "<html>\n"
            + "\n"
            + "    <body>\n"
            + "        <span>example text</span>\n"
            + "    </body>\n"
            + "</html>";
        String htmlBlockAfter = """
            <html>

                <body>
                    <span>example text</span>
                </body>
            </html>""";
        String escapeThreeDoubleQuotes = """
            This is "\"""\"
            the only case where double-quotes must be escaped
            """;
        // Note that even if a source file has Windows line endings (\r\n), the text blocks will only be terminated with newlines (\n). If you need carriage returns (\r) to be present, you have to explicitly add them to the text block:
        String textWithCarriageReturnsBefore = "separated with\r\ncarriage returns";
        String textWithCarriageReturnsAfter = """
            separated with\r
            carriage returns
            """;
        String extraSpacesBefore = "line 1\nline 2        \n";
        String extraSpacesAfter = """
            line 1        
            line 2        \s
            """;
        String formattedText = """
            Some parameters: %s %d
            """.formatted("parameter", 0);
    }

    public static class Concurrency {
        /*
        https://openjdk.org/projects/loom/ - Loom - Fibers, Continuations and Tail-Calls for the JVM.
        https://cr.openjdk.org/~rpressler/loom/Loom-Proposal.html
        https://openjdk.org/jeps/444 - Virtual Threads.
        https://openjdk.org/jeps/436 - Virtual Threads (Second Preview).
        https://openjdk.org/jeps/425 - Virtual Threads (Preview).
        https://openjdk.org/jeps/453 - Structured Concurrency (Preview)
        https://openjdk.org/jeps/437 - Structured Concurrency (Second Incubator).
        https://openjdk.org/jeps/428 - Structured Concurrency (Incubator).
        https://openjdk.org/jeps/429 - Scoped Values (Incubator).
        https://helidon.io/nima
        https://www.eclipse.org/jetty/documentation/jetty-12/operations-guide/index.html#og-server-threadpool-virtual
        https://inside.java/tag/loom
        Terminology:
          - OS thread. A "thread-like" data-structure managed by the Operating System.
          - Platform thread. Up until Java 19, every instance of the Thread class was a platform thread, that is, a wrapper around an OS thread. Creating a platform threads creates an OS thread, blocking a platform thread blocks an OS thread.
          - Virtual thread. Lightweight, JVM-managed threads. They extend the Thread class but are not tied to one specific OS thread. Thus, scheduling virtual threads is the responsibility of the JVM.
          - Carrier thread. A platform thread used to execute a virtual thread is called a carrier. This isn’t a class distinct from Thread or VirtualThread but rather a functional denomination.
        Summary:
         The JVM is a multithreaded environment. As we may know, the JVM gives us an abstraction of OS threads through the type java.lang.Thread. Until Project Loom, every thread in the JVM is just a little wrapper around an OS thread. We can call the such implementation of the java.lang.Thread type as platform thread.
         The problem with platform threads is that they are expensive from a lot of points of view. First, they are costly to create. Whenever a platform thread is made, the OS must allocate a large amount of memory (megabytes) in the stack to store the thread context, native, and Java call stacks. This is due to the not resizable nature of the stack. Moreover, whenever the scheduler preempts a thread from execution, this enormous amount of memory must be moved around.
         Virtual threads are a new type of thread that tries to overcome the resource limitation problem of platform threads. They are an alternate implementation of the java.lang.Thread type, which stores the stack frames in the heap (garbage-collected memory) instead of the stack. Therefore, the initial memory footprint of a virtual thread tends to be very small, a few hundred bytes instead of megabytes. In fact, the stack chunk can resize at every moment. So, we don’t need to allocate a gazillion of memory to fit every possible use case.
         The JVM maintains a pool of platform threads, created and maintained by a dedicated ForkJoinPool. Initially, the number of platform threads equals the number of CPU cores, and it cannot increase more than 256. For each created virtual thread, the JVM schedules its execution on a platform thread, temporarily copying the stack chunk for the virtual thread from the heap to the stack of the platform thread. We said that the platform thread becomes the carrier thread of the virtual thread. The first time the virtual thread blocks on a blocking operation, the carrier thread is released, and the stack chunk of the virtual thread is copied back to the heap. This way, the carrier thread can execute any other eligible virtual threads. Once the blocked virtual thread finishes the blocking operation, the scheduler schedules it again for execution. The execution can continue on the same carrier thread or a different one.
         Cooperative scheduling is helpful when working in a highly collaborative environment. Since a virtual thread releases its carrier thread only when reaching a blocking operation, cooperative scheduling and virtual threads will not improve the performance of CPU-intensive applications. The JVM already gives a tool for those tasks: Java parallel streams. However, there are some cases where a blocking operation doesn’t unmount the virtual thread from the carrier thread, blocking the underlying carrier thread. In such cases, we say the virtual is pinned to the carrier thread. It’s not an error but a behavior that limits the application’s scalability. Note that if a carrier thread is pinned, the JVM can always add a new platform thread to the carrier pool if the configurations of the carrier pool allow it. Fortunately, there are only two cases in which a virtual thread is pinned to the carrier thread: 1- when it executes code inside a synchronized block or method; 2- when it calls a native method or a foreign function (i.e., a call to a native library using JNI).

        1. Virtual threads.
        Virtual threads are lightweight threads that dramatically reduce the effort of writing, maintaining, and observing high-throughput concurrent applications.
        ! The modern world has increased demands. The software unit of concurrency simply can’t match the scale of the domain’s unit of concurrency. Overall, a server can handle millions of concurrent open sockets. But the problem is that the current Java runtime limits the server to a few thousand concurrent open sockets. The reason for such limitation is the need for Java runtime to create corresponding threads within the operating system. And the OS can’t handle millions or billions of operations.
        ! Virtual threads are useful for I/O-bound workloads only. CPU-bound doesn’t consist in quickly swapping threads while they need to wait for the completion of an I/O but in leaving them attached to a CPU-core to actually compute something. In this scenario, it is useless to have thousands of threads if we have tens of CPU-cores, virtual threads won’t enhance the performance of CPU-bound workloads.
        1.1. Goals:
          - Enable server applications written in the simple thread-per-request style to scale with near-optimal hardware utilization.
          - Enable existing code that uses the java.lang.Thread API to adopt virtual threads with minimal change.
          - Enable easy troubleshooting, debugging, and profiling of virtual threads with existing JDK tools.
        1.2. Non-Goals. It is not a goal:
          - to remove the traditional implementation of threads, or to silently migrate existing applications to use virtual threads.
          - to change the basic concurrency model of Java.
          - to offer a new data parallelism construct in either the Java language or the Java libraries. The Stream API remains the preferred way to process large data sets in parallel.
        1.3. Alternatives.
          - Continue to rely on asynchronous APIs. Asynchronous APIs are difficult to integrate with synchronous APIs, create a split world of two representations of the same I/O operations, and provide no unified concept of a sequence of operations that can be used by the platform as context for troubleshooting, monitoring, debugging, and profiling purposes.
          - Add syntactic stackless coroutines (i.e., async/await) to the Java language. These are easier to implement than user-mode threads and would provide a unifying construct representing the context of a sequence of operations.
           That construct would be new, however, and separate from threads, similar to them in many respects yet different in some nuanced ways. It would split the world between APIs designed for threads and APIs designed for coroutines, and would require the new thread-like construct to be introduced into all layers of the platform and its tooling. This would take longer for the ecosystem to adopt, and would not be as elegant and harmonious with the platform as user-mode threads.
            Most languages that have adopted syntactic coroutines have done so due to an inability to implement user-mode threads (e.g., Kotlin), legacy semantic guarantees (e.g., the inherently single-threaded JavaScript), or language-specific technical constraints (e.g., C++). These limitations do not apply to Java.
          - Introduce a new public class to represent user-mode threads, unrelated to java.lang.Thread. This would be an opportunity to jettison the unwanted baggage that the Thread class has accumulated over 25 years. We explored and prototyped several variants of this approach, but in every case grappled with the issue of how to run existing code.
           The main problem is that Thread.currentThread() is used, directly or indirectly, pervasively in existing code (e.g., in determining lock ownership, or for thread-local variables). This method must return an object that represents the current thread of execution. If we introduced a new class to represent user-mode threads then currentThread() would have to return some sort of wrapper object that looks like a Thread but delegates to the user-mode thread object.
            It would be confusing to have two objects represent the current thread of execution, so we eventually concluded that preserving the old Thread API is not a significant hurdle. With the exception of a few methods such as currentThread(), developers rarely use the Thread API directly; they mostly interact use higher-level APIs such as ExecutorService. Over time we will jettison unwanted baggage from the Thread class, and associated classes such as ThreadGroup, by deprecating and removing obsolete methods.
        1.4. Risks and Assumptions (detailed info https://openjdk.org/jeps/444).
          - The primary risks of this proposal are ones of compatibility due to changes in existing APIs and their implementations.
          - A few source and binary incompatible changes may impact code that extends java.lang.Thread.
          - A few behavioral differences between platform threads and virtual threads may be observed when mixing existing code with newer code that takes advantage of virtual threads or the new APIs.
        1.5. Motivation.
        In summary, virtual threads preserve the reliable thread-per-request style that is harmonious with the design of the Java Platform while utilizing the hardware optimally. Using virtual threads does not require learning new concepts, though it may require unlearning habits developed to cope with today's high cost of threads. Virtual threads will not only help application developers — they will also help framework designers provide easy-to-use APIs that are compatible with the platform's design without compromising on scalability.
        Virtual threads can significantly improve application throughput when:
          - The number of concurrent tasks is high (more than a few thousand), and
          - The workload is not CPU-bound, since having many more threads than processor cores cannot improve throughput in that case.
        To take advantage of virtual threads, it is not necessary to rewrite your program. Virtual threads do not require or expect application code to explicitly hand back control to the scheduler.
        !! To see detailed changes across the Java Platform and its implementation consult https://openjdk.org/jeps/444 section "Detailed changes".
        Motivation from Loom project (https://cr.openjdk.org/~rpressler/loom/Loom-Proposal.html):
          Programmers are forced to choose between modeling a unit of domain concurrency directly as a thread and lose considerably in the scale of concurrency a single server can support, or use other constructs to implement concurrency on a finer-grained level than threads (tasks), and support concurrency by writing asynchronous code that does not block the thread running it.
          Recent years have seen the introduction of many asynchronous APIs to the Java ecosystem, from asynchronous NIO in the JDK, asynchronous servlets, and many asynchronous third-party libraries. Those APIs were created not because they are easier to write and to understand, for they are actually harder; not because they are easier to debug or profile — they are harder (they don't even produce meaningful stacktraces); not because they compose better than synchronous APIs — they compose less elegantly; not because they fit better with the rest of the language or integrate well with existing code — they are a much worse fit, but just because the implementation of the software unit of concurrency in Java — the thread — is insufficient from a footprint and performance perspective. This is a sad case of a good and natural abstraction being abandoned in favor of a less natural one, which is overall worse in many respects, merely because of the runtime performance characteristics of the abstraction.
          While there are some advantages to using kernel threads as the implementation of Java threads — most notably because all native code is supported by kernel threads, and so Java code running in a thread can call native APIs — the disadvantages mentioned above are too great to ignore, and result either in hard-to-write, expensive-to-maintain code, or in a significant waste of computing resources, that is especially costly when code runs in the cloud. Indeed, some languages and language runtimes successfully provide a lightweight thread implementation, most famous are Erlang and Go, and the feature is both very useful and popular.
          The main goal of this project is to add a lightweight thread construct, which we call fibers, managed by the Java runtime, which would be optionally used alongside the existing heavyweight, OS-provided, implementation of threads. Fibers are much more lightweight than kernel threads in terms of memory footprint, and the overhead of task-switching among them is close to zero. Millions of fibers can be spawned in a single JVM instance, and programmers need not hesitate to issue synchronous, blocking calls, as blocking will be virtually free. In addition to making concurrent applications simpler and/or more scalable, this will make life easier for library authors, as there will no longer be a need to provide both synchronous and asynchronous APIs for a different simplicity/performance tradeoff. Simplicity will come with no tradeoff.
          The much bigger problem with the OS implementation of threads is the scheduler. For one, the OS scheduler runs in kernel mode, and so every time a thread blocks and control returned to the scheduler, a non-cheap user/kernel switch must occur. For another, OS schedulers are designed to be general-purpose and schedule many different kinds of program threads. But a thread running a video encoder behaves very differently from one serving requests coming over the network, and the same scheduling algorithm will not be optimal for both. Threads handling transactions on servers tend to present certain behavior patterns that present a challenge to a general-purpose OS scheduler. For example, it is a common pattern for a transaction-serving thread [A] to perform some action on the request, and then pass data on to another thread, [B], for further processing. This requires some synchronization of a handoff between the two threads that can involve either a lock or a message queue, but the pattern is the same: [A] operates on some data [X], hands it over to [B], wakes [B] up and then blocks until it is handed another request from the network or another thread. This pattern is so common that we can assume that [A] will block shortly after unblocking [B], and so scheduling [B] on the same core as [A] will be beneficial, as [X] is already in the core's cache; in addition, adding [B] to a core-local queue doesn't require any costly contended synchronization. Indeed, a work-stealing scheduler like ForkJoinPool makes this precise assumption, as it adds tasks scheduled by running task into a local queue. The OS kernel, however, cannot make such an assumption. As far as it knows, thread [A] may want to continue running for a long while after waking up [B], and so it would schedule the recently unblocked [B] to a different core, thus both requiring some synchronization, and causing a cache-fault as soon as [B] accesses [X].
          In order to suspend a computation, a continuation is required to store an entire call-stack context, or simply put, store the stack. To support native languages, the memory storing the stack must be contiguous and remain at the same memory address. While virtual memory does offer some flexibility, there are still limitations on just how lightweight and flexible such kernel continuations (i.e. stacks) can be. Ideally, we would like stacks to grow and shrink depending on usage. As a language runtime implementation of threads is not required to support arbitrary native code, we can gain more flexibility over how to store continuations, which allows us to reduce footprint.
        1.5.1. The thread-per-request style.
        Server applications generally handle concurrent user requests that are independent of each other, so it makes sense for an application to handle a request by dedicating a thread to that request for its entire duration. This thread-per-request style is easy to understand, easy to program, and easy to debug and profile because it uses the platform's unit of concurrency to represent the application's unit of concurrency.
        The scalability of server applications is governed by Little's Law, which relates latency, concurrency, and throughput: For a given request-processing duration (i.e., latency), the number of requests an application handles at the same time (i.e., concurrency) must grow in proportion to the rate of arrival (i.e., throughput). For example, suppose an application with an average latency of 50ms achieves a throughput of 200 requests per second by processing 10 requests concurrently. In order for that application to scale to a throughput of 2000 requests per second, it will need to process 100 requests concurrently. If each request is handled in a thread for the request's duration then, for the application to keep up, the number of threads must grow as throughput grows.
        Unfortunately, the number of available threads is limited because the JDK implements threads as wrappers around operating system (OS) threads. OS threads are costly, so we cannot have too many of them, which makes the implementation ill-suited to the thread-per-request style. If each request consumes a thread, and thus an OS thread, for its duration, then the number of threads often becomes the limiting factor long before other resources, such as CPU or network connections, are exhausted. The JDK's current implementation of threads caps the application's throughput to a level well below what the hardware can support. This happens even when threads are pooled, since pooling helps avoid the high cost of starting a new thread but does not increase the total number of threads.
        1.5.2. Improving scalability with the asynchronous style.
        Some developers wishing to utilize hardware to its fullest have given up the thread-per-request style in favor of a thread-sharing style. Instead of handling a request on one thread from start to finish, request-handling code returns its thread to a pool when it waits for an I/O operation to complete so that the thread can service other requests. This fine-grained sharing of threads — in which code holds on to a thread only when it performs calculations, not when it waits for I/O — allows a high number of concurrent operations without consuming a high number of threads. While it removes the limitation on throughput imposed by the scarcity of OS threads, it comes at a high price: It requires what is known as an asynchronous programming style, employing a separate set of I/O methods that do not wait for I/O operations to complete but rather, later on, signal their completion to a callback. Without a dedicated thread, developers must break down their request-handling logic into small stages, typically written as lambda expressions, and then compose them into a sequential pipeline with an API (see CompletableFuture, for example, or so-called "reactive" frameworks). They thus forsake the language's basic sequential composition operators, such as loops and try/catch blocks.
        !! In the asynchronous style, each stage of a request might execute on a different thread, and every thread runs stages belonging to different requests in an interleaved fashion. This has deep implications for understanding program behavior: Stack traces provide no usable context, debuggers cannot step through request-handling logic, and profilers cannot associate an operation's cost with its caller. Composing lambda expressions is manageable when using Java's stream API to process data in a short pipeline, but problematic when all of the request-handling code in an application must be written in this way. This programming style is at odds with the Java Platform because the application's unit of concurrency — the asynchronous pipeline — is no longer the platform's unit of concurrency.
        1.5.3. Preserving the thread-per-request style with virtual threads.
        To enable applications to scale while remaining harmonious with the platform, we should strive to preserve the thread-per-request style by implementing threads more efficiently, so they can be more plentiful. Operating systems cannot implement OS threads more efficiently because different languages and runtimes use the thread stack in different ways. It is possible, however, for a Java runtime to implement Java threads in a way that severs their one-to-one correspondence to OS threads. Just as operating systems give the illusion of plentiful memory by mapping a large virtual address space to a limited amount of physical RAM, a Java runtime can give the illusion of plentiful threads by mapping a large number of virtual threads to a small number of OS threads.
        A virtual thread is an instance of java.lang.Thread that is not tied to a particular OS thread. A platform thread, by contrast, is an instance of java.lang.Thread implemented in the traditional way, as a thin wrapper around an OS thread.
        Application code in the thread-per-request style can run in a virtual thread for the entire duration of a request, but the virtual thread consumes an OS thread only while it performs calculations on the CPU. The result is the same scalability as the asynchronous style, except it is achieved transparently: when code running in a virtual thread calls a blocking I/O operation in the java.* API, the runtime performs a non-blocking OS call and automatically suspends the virtual thread until it can be resumed later. To Java developers, virtual threads are simply threads that are cheap to create and almost infinitely plentiful. Hardware utilization is close to optimal, allowing a high level of concurrency and, as a result, high throughput, while the application remains harmonious with the multithreaded design of the Java Platform and its tooling.
        1.5.4. Implications of virtual threads.
        Virtual threads are cheap and plentiful, and thus should never be pooled: A new virtual thread should be created for every application task. Most virtual threads will thus be short-lived and have shallow call stacks, performing as little as a single HTTP client call or a single JDBC query. Platform threads, by contrast, are heavyweight and expensive, and thus often must be pooled. They tend to be long-lived, have deep call stacks, and be shared among many tasks.
        1.6. Description (see https://openjdk.org/jeps/444).
        As of 2023, every instance of java.lang.Thread in the JDK is a platform thread. A platform thread runs Java code on an underlying OS thread and captures the OS thread for the code's entire lifetime. The number of platform threads is limited to the number of OS threads.
        A virtual thread is an instance of java.lang.Thread that runs Java code on an underlying OS thread but does not capture the OS thread for the code's entire lifetime. This means that many virtual threads can run their Java code on the same OS thread, effectively sharing it. While a platform thread monopolizes a precious OS thread, a virtual thread does not. The number of virtual threads can be much larger than the number of OS threads.
        Virtual threads are a lightweight implementation of threads that is provided by the JDK rather than the OS. They are a form of user-mode threads, which have been successful in other multithreaded languages (e.g., goroutines in Go and processes in Erlang). User-mode threads even featured as so-called "green threads" in early versions of Java, when OS threads were not yet mature and widespread. However, Java's green threads all shared one OS thread (M:1 scheduling) and were eventually outperformed by platform threads, implemented as wrappers for OS threads (1:1 scheduling). Virtual threads employ M:N scheduling, where a large number (M) of virtual threads is scheduled to run on a smaller number (N) of OS threads.
        Developers can choose whether to use virtual threads or platform threads. Here is an example program that creates a large number of virtual threads. The program first obtains an ExecutorService that will create a new virtual thread for each submitted task. It then submits 10,000 tasks and waits for all of them to complete:
        */
        public static void vThreads() {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                IntStream.range(0, 10_000).forEach(i -> {
                    executor.submit(() -> {
                        Thread.sleep(Duration.ofSeconds(1));
                        println(Thread.currentThread());
                        return i;
                    });
                });
            }  // executor.close() is called implicitly, and waits
        }
        /*
        The task in this example is simple code — sleep for one second — and modern hardware can easily support 10,000 virtual threads running such code concurrently. Behind the scenes, the JDK runs the code on a small number of OS threads, perhaps as few as one.
        Things would be very different if this program used an ExecutorService that creates a new platform thread for each task, such as Executors.newCachedThreadPool(). The ExecutorService would attempt to create 10,000 platform threads, and thus 10,000 OS threads, and the program might crash, depending on the machine and operating system.
        Things would be not much better if the program, instead, used an ExecutorService that obtains platform threads from a pool, such as Executors.newFixedThreadPool(200). The ExecutorService would create 200 platform threads to be shared by all 10,000 tasks, so many of the tasks would run sequentially rather than concurrently and the program would take a long time to complete. For this program, a pool with 200 platform threads can only achieve a throughput of 200 tasks-per-second, whereas virtual threads achieve a throughput of about 10,000 tasks-per-second (after sufficient warmup). Moreover, if the 10_000 in the example program is changed to 1_000_000, then the program would submit 1,000,000 tasks, create 1,000,000 virtual threads that run concurrently, and (after sufficient warmup) achieve a throughput of about 1,000,000 tasks-per-second.
        If the tasks in this program performed a calculation for one second (e.g., sorting a huge array) rather than merely sleeping, then increasing the number of threads beyond the number of processor cores would not help, whether they are virtual threads or platform threads. Virtual threads are not faster threads — they do not run code any faster than platform threads. They exist to provide scale (higher throughput), not speed (lower latency). There can be many more of them than platform threads, so they enable the higher concurrency needed for higher throughput according to Little's Law.
        A virtual thread can run any code that a platform thread can run. In particular, virtual threads support thread-local variables and thread interruption, just like platform threads. This means that existing Java code that processes requests will easily run in a virtual thread. Many server frameworks will choose to do this automatically, starting a new virtual thread for every incoming request and running the application's business logic in it.
        Here is an example of a server application that aggregates the results of two other services. A hypothetical server framework (not shown) creates a new virtual thread for each request and runs the application's handle code in that virtual thread. The application code, in turn, creates two new virtual threads to fetch resources concurrently via the same ExecutorService as the first example:
        */
        void handle(Request request, Response response) throws MalformedURLException {
            var url1 = new URL("https://openjdk.org/jeps/444");
            var url2 = new URL("https://openjdk.org/jeps/425");
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                var future1 = executor.submit(() -> fetchURL(url1));
                var future2 = executor.submit(() -> fetchURL(url2));
                response.send(future1.get() + future2.get());
            } catch (ExecutionException | InterruptedException e) { response.fail(e); }
        }
        /*
        A server application like this, with straightforward blocking code, scales well because it can employ a large number of virtual threads.
        Executor.newVirtualThreadPerTaskExecutor() is not the only way to create virtual threads. The new java.lang.Thread.Builder API, discussed below, can create and start virtual threads. In addition, structured concurrency offers a more powerful API to create and manage virtual threads, particularly in code similar to this server example, whereby the relationships among threads are made known to the platform and its tools.
        1.6.1. Do not pool virtual threads.
        Developers will typically migrate application code to the virtual-thread-per-task ExecutorService from a traditional ExecutorService based on thread pools. Thread pools, like all resource pools, are intended to share expensive resources, but virtual threads are not expensive and there is never a need to pool them.
        Developers sometimes use thread pools to limit concurrent access to limited resources. For example, if a service cannot handle more than 20 concurrent requests then making all requests to the service via tasks submitted to a thread pool of size 20 will ensure that. This idiom has become ubiquitous because the high cost of platform threads has made thread pools ubiquitous, but do not be tempted to pool virtual threads in order to limit concurrency. A construct specifically designed for that purpose, such as semaphores, should be used to guard access to a limited resource. This is more effective and convenient than thread pools, and is also more secure since there is no risk of thread-local data accidentally leaking from one task to another.
        In conjunction with thread pools, developers sometimes use thread-local variables to share expensive resources among multiple tasks that share the same thread. For example, if a database connection is expensive to create then you can open it once and store it in a thread-local variable for later use by other tasks in the same thread. If you migrate code from using a thread pool to using a virtual thread per task, be wary of usages of this idiom since creating an expensive resource for every virtual thread may degrade performance significantly. Change such code to use alternative caching strategies so that expensive resources can be shared efficiently among a very large number of virtual threads.
        1.6.2. Observing virtual threads (consult https://openjdk.org/jeps/444).
        Writing clear code is not the full story. A clear presentation of the state of a running program is also essential for troubleshooting, maintenance, and optimization, and the JDK has long offered mechanisms to debug, profile, and monitor threads. Such tools should do the same for virtual threads — perhaps with some accommodation to their large quantity — since they are, after all, instances of java.lang.Thread.
        1.6.3. Scheduling virtual threads.
        To do useful work a thread needs to be scheduled, that is, assigned for execution on a processor core. For platform threads, which are implemented as OS threads, the JDK relies on the scheduler in the OS. By contrast, for virtual threads, the JDK has its own scheduler. Rather than assigning virtual threads to processors directly, the JDK's scheduler assigns virtual threads to platform threads (this is the M:N scheduling of virtual threads mentioned earlier). The platform threads are then scheduled by the OS as usual.
        The JDK's virtual thread scheduler is a work-stealing ForkJoinPool that operates in FIFO mode. The parallelism of the scheduler is the number of platform threads available for the purpose of scheduling virtual threads. By default it is equal to the number of available processors, but it can be tuned with the system property jdk.virtualThreadScheduler.parallelism. Note that this ForkJoinPool is distinct from the common pool which is used, for example, in the implementation of parallel streams, and which operates in LIFO mode.
        The platform thread to which the scheduler assigns a virtual thread is called the virtual thread's carrier. A virtual thread can be scheduled on different carriers over the course of its lifetime; in other words, the scheduler does not maintain affinity between a virtual thread and any particular platform thread. From the perspective of Java code, a running virtual thread is logically independent of its current carrier:
          - The identity of the carrier is unavailable to the virtual thread. The value returned by Thread.currentThread() is always the virtual thread itself.
          - The stack traces of the carrier and the virtual thread are separate. An exception thrown in the virtual thread will not include the carrier's stack frames. Thread dumps will not show the carrier's stack frames in the virtual thread's stack, and vice-versa.
          - Thread-local variables of the carrier are unavailable to the virtual thread, and vice-versa.
        In addition, from the perspective of Java code, the fact that a virtual thread and its carrier temporarily share an OS thread is invisible. From the perspective of native code, by contrast, both the virtual thread and its carrier run on the same native thread. Native code that is called multiple times on the same virtual thread may thus observe a different OS thread identifier at each invocation.
        The scheduler does not currently implement time sharing for virtual threads. Time sharing is the forceful preemption of a thread that has consumed an allotted quantity of CPU time. While time sharing can be effective at reducing the latency of some tasks when there are a relatively small number of platform threads and CPU utilization is at 100%, it is not clear that time sharing would be as effective with a million virtual threads.
        1.6.4. Executing virtual threads.
        To take advantage of virtual threads, it is not necessary to rewrite your program. Virtual threads do not require or expect application code to explicitly hand back control to the scheduler; in other words, virtual threads are not cooperative. User code must not make assumptions about how or when virtual threads are assigned to platform threads any more than it makes assumptions about how or when platform threads are assigned to processor cores.
        To run code in a virtual thread, the JDK's virtual thread scheduler assigns the virtual thread for execution on a platform thread by mounting the virtual thread on a platform thread. This makes the platform thread become the carrier of the virtual thread. Later, after running some code, the virtual thread can unmount from its carrier. At that point the platform thread is free so the scheduler can mount a different virtual thread on it, thereby making it a carrier again.
        Typically, a virtual thread will unmount when it blocks on I/O or some other blocking operation in the JDK, such as BlockingQueue.take(). When the blocking operation is ready to complete (e.g., bytes have been received on a socket), it submits the virtual thread back to the scheduler, which will mount the virtual thread on a carrier to resume execution.
        The vast majority of blocking operations in the JDK will unmount the virtual thread, freeing its carrier and the underlying OS thread to take on new work. However, some blocking operations in the JDK do not unmount the virtual thread, and thus block both its carrier and the underlying OS thread. This is because of limitations either at the OS level (e.g., many filesystem operations) or at the JDK level (e.g., Object.wait()). The implementation of these blocking operations will compensate for the capture of the OS thread by temporarily expanding the parallelism of the scheduler. Consequently, the number of platform threads in the scheduler's ForkJoinPool may temporarily exceed the number of available processors. The maximum number of platform threads available to the scheduler can be tuned with the system property jdk.virtualThreadScheduler.maxPoolSize.
        There are two scenarios in which a virtual thread cannot be unmounted during blocking operations because it is pinned to its carrier:
          - When it executes code inside a synchronized block or method, or
          - When it executes a native method or a foreign function.
        Pinning does not make an application incorrect, but it might hinder its scalability. If a virtual thread performs a blocking operation such as I/O or BlockingQueue.take() while it is pinned, then its carrier and the underlying OS thread are blocked for the duration of the operation. Frequent pinning for long durations can harm the scalability of an application by capturing carriers.
        !! The scheduler does not compensate for pinning by expanding its parallelism. Instead, avoid frequent and long-lived pinning by revising synchronized blocks or methods that run frequently and guard potentially long I/O operations to use java.util.concurrent.locks.ReentrantLock instead. There is no need to replace synchronized blocks and methods that are used infrequently (e.g., only performed at startup) or that guard in-memory operations. As always, strive to keep locking policies simple and clear.
        New diagnostics assist in migrating code to virtual threads and in assessing whether you should replace a particular use of synchronized with a java.util.concurrent lock:
          - A JDK Flight Recorder (JFR) event is emitted when a thread blocks while pinned (see JDK Flight Recorder).
          - The system property jdk.tracePinnedThreads triggers a stack trace when a thread blocks while pinned. Running with -Djdk.tracePinnedThreads=full prints a complete stack trace when a thread blocks while pinned, with the native frames and frames holding monitors highlighted. Running with -Djdk.tracePinnedThreads=short limits the output to just the problematic frames.
        In a future release, we may be able to remove the first limitation above (pinning inside synchronized). The second limitation is required for proper interaction with native code.
        1.6.5. Memory use and interaction with garbage collection.
        The stacks of virtual threads are stored in Java's garbage-collected heap as stack chunk objects. The stacks grow and shrink as the application runs, both to be memory-efficient and to accommodate stacks of arbitrary depth (up to the JVM's configured platform thread stack size). This efficiency is what enables a large number of virtual threads, and thus the continued viability of the thread-per-request style in server applications.
        In the second example above, recall that a hypothetical framework processes each request by creating a new virtual thread and calling the handle method; even if it calls handle at the end of a deep call stack (after authentication, transactions, etc.), handle itself spawns multiple virtual threads that only perform short-lived tasks. Therefore, for each virtual thread with a deep call stack, there will be multiple virtual threads with shallow call stacks consuming little memory.
        The amount of heap space and garbage collector activity that virtual threads require is difficult, in general, to compare to that of asynchronous code. A million virtual threads require at least a million objects, but so do a million tasks sharing a pool of platform threads. In addition, application code that processes requests typically maintains data across I/O operations. Thread-per-request code can keep that data in local variables, which are stored on virtual thread stacks in the heap, while asynchronous code must keep that same data in heap objects that are passed from one stage of the pipeline to the next. On the one hand, the stack frame layout needed by virtual threads is more wasteful than that of a compact object; on the other hand, virtual threads can mutate and reuse their stacks in many situations (depending on low-level GC interactions) while asynchronous pipelines always need to allocate new objects, and so virtual threads might require fewer allocations. Overall, the heap consumption and garbage collector activity of thread-per-request versus asynchronous code should be roughly similar. Over time, we expect to make the internal representation of virtual thread stacks significantly more compact.
        Unlike platform thread stacks, virtual thread stacks are not GC roots, so the references contained in them are not traversed in a stop-the-world pause by garbage collectors, such as G1, that perform concurrent heap scanning. This also means that if a virtual thread is blocked on, e.g., BlockingQueue.take(), and no other thread can obtain a reference to either the virtual thread or the queue, then the thread can be garbage collected — which is fine, since the virtual thread can never be interrupted or unblocked. Of course, the virtual thread will not be garbage collected if it is running or if it is blocked and could ever be unblocked.
        A current limitation of virtual threads is that the G1 GC does not support humongous stack chunk objects. If a virtual thread's stack reaches half the region size, which could be as small as 512KB, then a StackOverflowError might be thrown.
        */

        /*
        2. Structured Concurrency.
        Structured concurrency treats multiple tasks running in different threads as a single unit of work, thereby streamlining error handling and cancellation, improving reliability, and enhancing observability.
        2.1. Goals:
          - Improve the maintainability, reliability, and observability of multithreaded code.
          - Promote a style of concurrent programming which can eliminate common risks arising from cancellation and shutdown, such as thread leaks and cancellation delays.
        2.2. Non-Goals. It is not a goal:
          - to replace any of the concurrency constructs in the java.util.concurrent package, such as ExecutorService and Future.
          - to define the definitive structured concurrency API for Java. Other structured concurrency constructs can be defined by third-party libraries or in future JDK releases.
          - to define a means of sharing streams of data among threads (i.e., channels). We might propose to do so in the future.
          - to replace the existing thread interruption mechanism with a new thread cancellation mechanism. We might propose to do so in the future.
        2.3. Alternatives:
          - Do nothing. Leave it to developers to continue using the existing low-level java.util.concurrent APIs and continue having to carefully consider all of the exceptional conditions and lifetime-coordination problems that arise in concurrent code.
          - Enhance the ExecutorService interface. We prototyped an implementation of this interface that always enforces structure and restricts which threads can submit tasks. However, we found it to be problematic because most uses of ExecutorService (and its parent interface Executor) in the JDK and in the ecosystem are not structured. Reusing the same API for a far more restricted concept is bound to cause confusion. For example, passing a structured ExecutorService instance to existing methods that accept this type would be all but certain to throw exceptions in most situations.
        2.4. Dependencies:
          - JEP 436: Virtual Threads (Second Preview).
        */
        Response handleUnstructured() throws ExecutionException, InterruptedException {
            Future<String>  user  = esvc.submit(() -> findUser());
            Future<Integer> order = esvc.submit(() -> fetchOrder());
            String theUser  = user.get(); // Join findUser
            int theOrder = order.get(); // Join fetchOrder
            esvc.shutdown();
            return new Response(theUser, theOrder);
        }
        Response handleStructuredAsAUnit() throws ExecutionException, InterruptedException {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                Supplier<String> user  = scope.fork(() -> findUser());
                Supplier<Integer> order = scope.fork(() -> fetchOrder());
                scope.join();           // Join both forks
                scope.throwIfFailed();  // ... and propagate errors
                // Here, both forks have succeeded, so compose their results
                return new Response(user.get(), order.get());
            }
        }
        /*
        2.5. Motivation.
        In contrast to the handleUnstructured example, understanding the lifetimes of the threads involved in the handleStructuredAsAUnit is easy: under all conditions their lifetimes are confined to a lexical scope, namely the body of the try-with-resources statement. Furthermore, the use of StructuredTaskScope ensures a number of valuable properties:
          - Error handling with short-circuiting — If either the findUser() or fetchOrder() subtasks fail, the other is cancelled if it has not yet completed. This is managed by the cancellation policy implemented by ShutdownOnFailure; other policies are possible.
          - Cancellation propagation — If the thread running handle() is interrupted before or during the call to join(), both forks are cancelled automatically when the thread exits the scope.
          - Clarity — The above code has a clear structure: Set up the subtasks, wait for them to either complete or be cancelled, and then decide whether to succeed (and process the results of the child tasks, which are already finished) or fail (and the subtasks are already finished, so there is nothing more to clean up).
          - Observability — A thread dump, as described below, clearly displays the task hierarchy, with the threads running findUser() and fetchOrder() shown as children of the scope.
        Like ExecutorService.submit(...), the StructuredTaskScope.fork(...) method takes a Callable and returns a Future. Unlike ExecutorService, however, the returned future is not intended to be joined via its get() method or cancelled via its cancel() method. All forks in a scope are, rather, intended to be joined or cancelled as a unit. Two new Future methods, resultNow() and exceptionNow(), are designed to be used after subtasks complete, for example after calling scope.join().
        */
        String findUser() { return ""; }
        Integer fetchOrder() { return 0; }
        class Response {
            Response(String user, Integer order) {}
            public void send(String s) {}
            public void fail(Exception e) {
            }
        }
        String fetchURL(URL url) throws IOException {
            try (var in = url.openStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        static ExecutorService esvc = Executors.newFixedThreadPool(2);

        /*
        3. Debug.
        It’s possible to trace pinned virtual threads during the execution of a program by adding a property to the run configuration:
          -Djdk.tracePinnedThreads=full/short
         The output could look like this:
           Thread[#22,ForkJoinPool-1-worker-1,5,CarrierThreads]
             ... (App.java:188) <== monitors:1
        Configuration of the carrier pool to allow the JVM to add a new carrier thread to the pool when needed:
          -Djdk.virtualThreadScheduler.parallelism=1
          -Djdk.virtualThreadScheduler.maxPoolSize=2
          -Djdk.virtualThreadScheduler.minRunnable=1
        */
    }

    public static class Misc {
        // Local interfaces, enums, inner statics. Since Java 16.
        void ifcEnmInst() {
            enum LocalEnum {A, B, C}
            interface LocalInterface {
                void ifcEnmInst(LocalEnum e);
            }
            LocalInterface r = new LocalInterface() {
                static final AtomicInteger callCounter = new AtomicInteger();
                @Override
                public void ifcEnmInst(LocalEnum e) {
                    println(callCounter.incrementAndGet());
                    println(e);
                    staticInAnonymous();
                }
                static void staticInAnonymous() {
                    println("static");
                }
            };
            r.ifcEnmInst(LocalEnum.A);
        }

        // Small but nice API changes.
        public static void misc() {
            // Invert a Predicate, will be even shorter with static import:
            VarKeyword.getListofStrings().stream()
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toList());
            // String got some new stuff too:
            "\nPretius\n rules\n  all!".repeat(10).lines()
                .filter(Predicate.not(String::isBlank))
                .map(String::strip)
                .map(s -> s.indent(2))
                .collect(Collectors.toList());
            // No need to have an instance of array passed as an argument:
            String[] myArray= VarKeyword.getListofStrings().toArray(String[]::new);
            // Read and write to files quickly (remember to catch all the possible exceptions though):
            Path path = null;
            try { path = Files.writeString(Path.of(""), "Pretius Rules All !"); }
            catch (IOException e) { throw new RuntimeException(e); }
            try { String fileContent = Files.readString(path); }
            catch (IOException e) { throw new RuntimeException(e); }
            // .toList() on a stream():
            String[] arr={"a", "b", "c"};
            var list = Arrays.stream(arr).toList();
            // Better NullPointerExceptions. Before without a debugger you couldn’t tell which object was null, or rather, which invoke operation has actually caused the problem. Now the message will be specific and it’ll tell us that the JVM cannot invoke which object.
            Tree tree = new Tree("Mary", 22,
                    new Tree("Emily", 20,
                            new Tree("Alan", 50, null, null),
                            new Tree("Georgie", 23, null, null)
                    ),
                    new Tree("Tian", 29,
                            new Tree("Raoul", 23, null, null),
                            null
                    )
            );
            var innerTree = tree.right.right.right;
            // New Optional.orElseThrow() method:
            String myObject = VarKeyword.getListofStrings().stream()
                .filter(String::isBlank)
                .filter((b) -> false)
                .findFirst()
                // If there is no value, the method [get] throws an exception
                //.get();
                // But the readability is better with [orElseThrow]
                .orElseThrow();
        }
    }
}