package org.example.java_learning;

import com.sun.net.httpserver.Request;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.constant.Constable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.StringTemplate.RAW;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.regex.Pattern.matches;
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
 - var type allowed for local variables (local-variable type inference):
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
            println( x + y );
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
https://docs.oracle.com/en/java/javase/21/language/index.html
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
Since Java 22:
 - Statements before super(...) (Preview)
 - Implicitly Declared Classes and Instance Main Methods (Second Preview)
 - String Templates (Second Preview)
 - Stream Gatherers (Preview)
 - Launch Multi-File Source-Code Programs
 - Foreign Function & Memory API
 - Class-File API (Preview)
 - Structured Concurrency (Second Preview)
 - Scoped Values (Second Preview)





Future features {
 - Project Loom: Virtual threads, a lightweight user-mode scheduled alternative to standard OS managed threads. Virtual threads are mapped to OS threads in a many-to-many relationship, in contrast to the many-to-one relationship from the original green threads implementation in early versions of Java.
   https://openjdk.org/projects/loom/
   https://cr.openjdk.org/~rpressler/loom/Loom-Proposal.html
 - Project Panama: Improved interoperability with native code, to enable Java source code to call functions and use data types from other languages, in a way that is easier and has better performance than today. Vector API (a portable and relatively low-level abstraction layer for SIMD programming) is also developed under Project Panama umbrella.
   https://openjdk.java.net/projects/panama/
 - Project Valhalla: Value types, objects without identity but with an efficient memory layout and leading to better results of escape analysis. Valhalla anticipates adding three core features to the platform: value objects, primitive classes, and specialized generics.
   https://openjdk.org/projects/valhalla/
   https://openjdk.org/projects/valhalla/design-notes/state-of-valhalla/01-background
   State of the Values https://cr.openjdk.org/~jrose/values/values-0.html
   JEP 401: Value Classes and Objects (Preview) https://openjdk.org/jeps/401
   JEP 402: Enhanced Primitive Boxing (Preview) https://openjdk.org/jeps/402

   Value-based Classes https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/doc-files/ValueBased.html
}

Preview Features {
 A preview feature is a new feature whose design, specification, and implementation are complete, but which is not permanent, which means that the feature may exist in a different form or not at all in future JDK releases.
 https://openjdk.org/jeps/12 Preview Features
 https://docs.oracle.com/en/java/javase/21/language/preview-language-and-vm-features.html
 To use preview language features in your programs, you must explicitly enable them in the compiler and the runtime system. If not, you'll receive an error message that states that your code is using a preview feature and preview features are disabled by default.
 Note:Code that uses preview features from an older release of the Java SE Platform will not necessarily compile or run on a newer release.
 To compile source code with javac that uses preview features from JDK release n, use javac from JDK release n with the --enable-preview command-line option in conjunction with either the --release n or -source n command-line option. For example:
    javac --enable-preview --release 12 MyApp.java
 To run an application that uses preview features from JDK release n, use java from JDK release n with the --enable-preview option. To continue the previous example, to run MyApp, run java from JDK 12 as follows:
    java --enable-preview MyApp
}


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

    public static class VarKeyword {
        /*
        https://openjdk.org/jeps/286 Local-Variable Type Inference.
        https://openjdk.org/projects/amber/guides/lvti-style-guide Local Variable Type Inference. Style Guidelines.

        Local variable declarations can make code more readable by eliminating redundant information. However, it can also make code less readable by omitting useful information. Consequently, use this feature with judgment; no strict rule exists about when it should and shouldn't be used.

        Applicable to local variables with initializers, indexes in the enhanced for-loop, and locals declared in a traditional for-loop; not available for method formals, constructor formals, method return types, method parameter types, fields, catch formals, or any other kind of variable declaration. So forms of local variable declarations that lack initializers, declare multiple variables, have extra array dimension brackets, or reference the variable being initialized are not allowed. The inference process, substantially, just gives the variable the type of its initializer expression. Some subtleties:
         - The initializer has no target type (because we haven't inferred it yet). Poly expressions that require such a type, like lambdas, method references, and array initializers, will trigger an error.
         - If the initializer has the null type, an error occurs—like a variable without an initializer, this variable is probably intended to be initialized later, and we don't know what type will be wanted.
         - Capture variables, and types with nested capture variables, are projected to supertypes that do not mention capture variables. This mapping replaces capture variables with their upper bounds and replaces type arguments mentioning capture variables with bounded wildcards (and then recurs). This preserves the traditionally limited scope of capture variables, which are only considered within a single statement.
         - Other than the above exceptions, non-denotable types, including anonymous class types and intersection types, may be inferred. Compilers and tools need to account for this possibility.
        */
        // For non-denotable types see:
        public static void seeNonDenotableTypes() { DataTypes.NonDenotableTypes.testNonDenotableTypes(); }
        // var can be used for the following types of variables:
        public static void testVarKeyword() throws IOException {
            //  - Local variable declarations with initializers:
            var list = new ArrayList<String>();    // infers ArrayList<String>
            var stream = list.stream();            // infers Stream<String>
            var path = Paths.get("fileName");        // infers Path
            var bytes = Files.readAllBytes(path);  // infers bytes[]
            //  - Enhanced for-loop indexes:
            List<String> myList = Arrays.asList("a", "b", "c");
            for (var element : myList) {}  // infers String
            //  - Index variables declared in traditional for loops:
            for (var counter = 0; counter < 10; counter++)  {}   // infers int
            //  - try-with-resources variable:
            try (var input = new FileInputStream("validation.txt")) {}   // infers FileInputStream
            //  - The var in lambda expressions (https://openjdk.org/jeps/323 - Local-Variable Syntax for Lambda Parameters):
            // Formal parameter declarations of implicitly typed lambda expressions: A lambda expression whose formal parameters have inferred types is implicitly typed:
            BiFunction<Integer, Integer, Integer> fn0 = (a, b) -> a + b;
            // Since Java 11 for uniformity with local variables each formal parameter of an implicitly typed lambda expression can be declared with the var identifier:
            BiFunction<Integer, Integer, Integer> fn1 = (var a, var b) -> a + b;
            // It's not possible to mix inferred formal parameters and var-declared formal parameters in implicitly typed lambda expressions nor can you mix var-declared formal parameters and manifest types in explicitly typed lambda expressions. The following examples are not permitted: {(var x, y) -> x.process(y)}, {(var x, int y) -> x.process(y)}.
            // However, it's not possible to assign a lambda to a variable using var keyword:
            //var fn0 = (a, b) -> a + b; // Error.
            // One benefit of uniformity is that modifiers, notably annotations, can be applied to local variables and lambda formals without losing brevity:
            BiFunction<Integer, Integer, Integer> fn2 = (@NonNull var a, @Nullable var b) -> a + b;
            boolean isThereAneedle = Arrays.asList("").stream()
                .anyMatch((@NonNull var s) -> s.equals(""));
        }
        // The following code removes at most max matching entries from a Map. Wildcarded type bounds are used for improving the flexibility of the method, resulting in considerable verbosity. Unfortunately, this requires the type of the Iterator to be a nested wildcard, making its declaration more verbose. This declaration is so long that the header of the for-loop no longer fits on a single line, making the code even harder to read.
        public static void removeAtMostMaxMatchingEntries (Map<? extends String, ? extends Number> map, int max) {
            // Before.
            for (Iterator<? extends Map.Entry<? extends String, ? extends Number>> iterator =
                 map.entrySet().iterator(); iterator.hasNext();
            ) {
                Map.Entry<? extends String, ? extends Number> entry = iterator.next();
                if (max > 0 && matches(entry.getKey(), (CharSequence) entry.getValue())) {
                    iterator.remove();
                    max--;
                }
            }
            // After.
            for (var iterator = map.entrySet().iterator(); iterator.hasNext();) {
                var entry = iterator.next();
                if (max > 0 && matches(entry.getKey(), (CharSequence) entry.getValue())) {
                    iterator.remove();
                    max--;
                }
            }
        }
        // Consider code that reads a single line of text from a socket using the try-with-resources statement. The networking and I/O APIs use an object wrapping idiom. Each intermediate object must be declared as a resource variable so that it will be closed properly if an error occurs while opening a subsequent wrapper. The conventional code for this requires the class name to be repeated on the left and right sides of the variable declaration, resulting in a lot of clutter.
        public static String tryWithResources(Socket socket, String charsetName) throws IOException {
            String result;
            // Before.
            try (
                InputStream inputStream = socket.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream, charsetName);
                BufferedReader bufReader = new BufferedReader(reader)
            ) {
                result = bufReader.readLine();
            }
            // After.
            try (
                var inputStream = socket.getInputStream();
                var reader = new InputStreamReader(inputStream, charsetName);
                var bufReader = new BufferedReader(reader)
            ) {
                result = bufReader.readLine();
            }
            return result;
        }
        //It is legal to use both var and diamond.
        public static void varDiamond() {
            // Before.
            PriorityQueue<Pair> itemQueue0 = new PriorityQueue<Pair>();
            // This can be rewritten using either diamond or var, without losing type information (both declare variables of type PriorityQueue<Pair>).
            PriorityQueue<Pair> itemQueue1 = new PriorityQueue<>();
            var itemQueue2 = new PriorityQueue<Pair>();
            // Using either diamond and var together, but the inferred type will change ().
            var itemQueue3 = new PriorityQueue<>(); // DANGEROUS: infers as PriorityQueue<Object>.
            // For its inference, diamond can use the target type (typically, the left-hand side of a declaration) or the types of constructor arguments. If neither is present, it falls back to the broadest applicable type, which is often Object. This is usually not what was intended.
            // Generic methods have employed type inference so successfully that it’s quite rare for programmers to provide explicit type arguments. Inference for generic methods relies on the target type if there are no actual method arguments that provide sufficient type information. In a var declaration, there is no target type, so a similar issue can occur as with diamond.
            var list0 = List.of(); // DANGEROUS: infers as List<Object>.
            // With both diamond and generic methods, additional type information can be provided by actual arguments to the constructor or method, allowing the intended type to be inferred.
            Comparator<String> ignoreCase = String::compareToIgnoreCase;
            var itemQueue4 = new PriorityQueue<>(ignoreCase); // inferred type PriorityQueue<String>.
            var list1 = List.of(BigInteger.ZERO); // infers as List<BigInteger>.
            // If you decide to use var with diamond or a generic method, you should ensure that method or constructor arguments provide enough type information so that the inferred type matches your intent. Otherwise, avoid using both var with diamond or a generic method in the same declaration.
        }
        // Primitive literals can be used as initializers for var declarations. It’s unlikely that using var in these cases will provide much advantage, as the type names are generally short. However, var is sometimes useful, for example, to align variable names.
        public static void literals () {
            // There is no issue with boolean, character, long, and string literals. The type inferred from these literals is precise, and so the meaning of var is unambiguous.
            // Before.
            boolean ready0 = true;
            char ch0 = '\ufffd';
            long sum0 = 0L;
            String label0 = "wombat";
            // After.
            var ready1 = true;
            var ch1    = '\ufffd';
            var sum1   = 0L;
            var label1 = "wombat";
            // Particular care should be taken when the initializer is a numeric value, especially an integer literal. With an explicit type on the left-hand side, the numeric value may be silently widened or narrowed to types other than int. With var, the value will be inferred as an int, which may be unintended.
            // Before.
            byte flags0 = 0;
            short mask0 = 0x7fff;
            long base0 = 17;
            // After. DANGEROUS: all infer as int.
            var flags1 = 0;
            var mask1 = 0x7fff;
            var base1 = 17;
            // Floating point literals are mostly unambiguous.
            // Before.
            float f0 = 1.0f;
            double d0 = 2.0;
            // After.
            var f1 = 1.0f;
            var d1 = 2.0;
            // Note that float literals can be widened silently to double. It is somewhat obtuse to initialize a double variable using an explicit float literal such as 3.0f, however, cases may arise where a double variable is initialized from a float field. Caution with var is advised here.
            // ORIGINAL
            final float INITIAL = 3.0f;
            double temp0 = INITIAL;
            var temp1 = INITIAL; // DANGEROUS: now infers as float.
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
                    println("Work work work");
                    yield false;
                }
                case SATURDAY, SUNDAY -> {
                    println("Yey, a free day!");
                    yield true;
                }
            };

            static String asString(Object value) {
                return switch (value) {
                    case Color c  -> "Color with " + c.values().length + " values";
                    case Enum<?> e -> e.getDeclaringClass().getSimpleName() + "." + e.name();
                    case Point p  -> "Record class: " + p.toString();
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
                    if (obj instanceof String) {
                        String myObject = (String) obj;
                        // … further logic
                    }
                }

                public static void toThis(Object obj) {
                    if (obj instanceof String myObject) {
                        // No need for extra line of code declaring local variable like myObject,  and no type casting as well.
                        // ...
                    }
                    // Moreover, the declared variable can be used in the same if condition, like this:
                    if (obj instanceof String myObject && myObject.isEmpty()) {
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
        public static void testTextBlocks() {
        /*
        https://openjdk.org/jeps/378 Text Blocks
        https://openjdk.org/jeps/368 Text Blocks (Second Preview)
        https://openjdk.org/jeps/355 Text Blocks (Preview)
        https://docs.oracle.com/en/java/javase/21/text-blocks/index.html Programmer's Guide to Text Blocks

        Add text blocks to the Java language. A text block is a multi-line string literal that avoids the need for most escape sequences, automatically formats the string in a predictable way, and gives the developer control over the format when desired.

        Description {
         Text blocks can be used anywhere a string literal can be used.
         A text block consists of zero or more content characters, enclosed by opening and closing delimiters.
         A text block begins with three double-quote characters followed by a line terminator. You can't put a text block on a single line, nor can the contents of the text block follow the three opening double-quotes without an intervening line terminator.
         The opening delimiter is a sequence of three double quote characters (""") followed by zero or more white spaces followed by a line terminator. The content begins at the first character after the line terminator of the opening delimiter.
         The closing delimiter is a sequence of three double quote characters. The content ends at the last character before the first double quote of the closing delimiter.
         The content may include double quote characters directly, unlike the characters in a string literal. The use of \" in a text block is permitted, but not necessary or recommended. Fat delimiters (""") were chosen so that " characters could appear unescaped, and also to visually distinguish a text block from a string literal.
         The content may include line terminators directly, unlike the characters in a string literal. The use of \n in a text block is permitted, but not necessary or recommended. For example, the text block:

         Compile-time processing {
          1. Line terminators.
           Line terminators in the content are normalized from CR (\u000D) and CRLF (\u000D\u000A) to LF (\u000A) by the Java compiler. This ensures that the string derived from the content is equivalent across platforms, even if the source code has been translated to a platform encoding (see javac -encoding). If the platform line terminator is required then String::replaceAll("\n", System.lineSeparator()) can be used.
           For example, if Java source code that was created on a Unix platform (where the line terminator is LF) is edited on a Windows platform (where the line terminator is CRLF), then without normalization, the content would become one character longer for each line. Any algorithm that relied on LF being the line terminator might fail, and any test that needed to verify string equality with String::equals would fail.
           The escape sequences \n (LF), \f (FF), and \r (CR) are not interpreted during normalization; escape processing happens later.
          2. Incidental white space.
           The position of the opening """ characters has no effect on the algorithm, but the position of the closing """ characters does have an effect if placed on its own line. The algorithm is as follows:
            - Split the content of the text block at every LF, producing a list of individual lines. Note that any line in the content which was just an LF will become an empty line in the list of individual lines.
            - Add all non-blank lines from the list of individual lines into a set of determining lines. (Blank lines -- lines that are empty or are composed wholly of white space -- have no visible influence on the indentation. Excluding blank lines from the set of determining lines avoids throwing off step 4 of the algorithm.)
            - If the last line in the list of individual lines (i.e., the line with the closing delimiter) is blank, then add it to the set of determining lines. (The indentation of the closing delimiter should influence the indentation of the content as a whole -- a significant trailing line policy.)
            - Compute the common white space prefix of the set of determining lines, by counting the number of leading white space characters on each line and taking the minimum count.
            - Remove the common white space prefix from each non-blank line in the list of individual lines.
            - Remove all trailing white space from all lines in the modified list of individual lines f1rom step 5. This step collapses wholly-white-space lines in the modified list so that they are empty, but does not discard them.
            - Construct the result string by joining all the lines in the modified list of individual lines from step 6, using LF as the separator between lines. If the final line in the list from step 6 is empty, then the joining LF from the previous line will be the last character in the result string.
           The escape sequences \b (backspace), \t (tab) and \s (space) are not interpreted by the algorithm; escape processing happens later. Similarly, the \<line-terminator> escape sequence does not prevent the splitting of lines on the line-terminator since the sequence is treated as two separate characters until escape processing.
          3. Escape sequences.
           After the content is re-indented, any escape sequences in the content are interpreted. Text blocks support all of the escape sequences supported in string literals, including \n, \t, \', \", and \\.
           Interpreting escapes as the final step allows developers to use \n, \f, and \r for vertical formatting of a string without it affecting the translation of line terminators in step 1, and to use \b and \t for horizontal formatting of a string without it affecting the removal of incidental white space in step 2.
           However, a sequence of three " characters requires at least one " to be escaped, in order to avoid mimicking the closing delimiter. (A sequence of n " characters requires at least Math.floorDiv(n,3) of them to be escaped.) The use of " immediately before the closing delimiter also requires escaping. For example:
           */
            String code =
                """
                String text = \"""
                    A text block inside a text block
                \""";
                """;

            String tutorial1 =
                """
                A common character
                in Java programs
                is \"""";

            String tutorial2 =
                """
                The empty string literal
                is formed from " characters
                as follows: \"\"""";

            System.out.println("""
                 1 "
                 2 ""
                 3 ""\"
                 4 ""\""
                 5 ""\"""
                 6 ""\"""\"
                 7 ""\"""\""
                 8 ""\"""\"""
                 9 ""\"""\"""\"
                10 ""\"""\"""\""
                11 ""\"""\"""\"""
                12 ""\"""\"""\"""\"
            """);
           /*
           New escape sequences.
           To allow finer control of the processing of newlines and white space, we introduce two new escape sequences.
           First, the \<line-terminator> escape sequence explicitly suppresses the insertion of a newline character.
           For example, it is common practice to split very long string literals into concatenations of smaller substrings, and then hard wrap the resulting string expression onto multiple lines:
           */
            String multilineOneliner =
               "Lorem ipsum dolor sit amet, consectetur adipiscing " +
               "elit, sed do eiusmod tempor incididunt ut labore " +
               "et dolore magna aliqua.";
            String multilineOnelinerTextBlock = """
                Lorem ipsum dolor sit amet, consectetur adipiscing \
                elit, sed do eiusmod tempor incididunt ut labore \
                et dolore magna aliqua.\
                """;
           /*
           Second, the new \s escape sequence simply translates to a single space (\u0020).
           Each line is exactly six characters long:
           */
            String extraSpaces = """
                red  \s
                green\s
                blue \s
                """;
           // Or ugly alternatives:
           //  - character substitution:
            String r = """
                trailing$$$
                white space
                """.replace('$', ' ');
           //  - character fence:
            String s = """
                trailing   |
                white space|
                """.replace("|\n", "\n");
           //  - octal escape sequence for space (note: \u0020 cannot be used because Unicode escapes are translated early during source file reading, prior to lexical analysis. By contrast, character and string escapes such as \040 are processed after lexical analysis has divided the source file into tokens and has identified string literals and text blocks):
            String t = """
                trailing\040\040\040
                white space
                """;
           /*
           The \s escape sequence can be used in text blocks, traditional string literals, and character literals.
           For the simple reason that character literals and traditional string literals don't allow embedded newlines, the \<line-terminator> escape sequence is only applicable to text blocks.

          Concatenation of text blocks {
           Text blocks and string literals may be concatenated interchangeably:
          */
            String simpleConcatenation =
                "public void print(Object o) {" +
                """
                    System.out.println(Objects.toString(o));
                }
                """;
           // However, concatenation involving a text block can become rather clunky. Take this text block as a starting point:
            String textBlock = """
                public void print(Object o) {
                    System.out.println(Objects.toString(o));
                }
                """;
           // Suppose it needs to be changed so that the type of o comes from a variable. Using concatenation, the text block that contains the trailing code will need to start on a new line. Unfortunately, the straightforward insertion of a newline in the program, as below, will cause a long span of white space between the type and the text beginning o :
            String type = String.valueOf(TextBlocks.class);
            textBlock = """
                public void print(""" + type + """
                                                   o) {
                    System.out.println(Objects.toString(o));
                }
                """;
           // The white space can be removed manually, but this hurts readability of the quoted code:
            textBlock = """
                public void print(""" + type + """
                 o) {
                    System.out.println(Objects.toString(o));
                }
                """;
           // A cleaner alternative is to use String::replace or String::format, as follows:
            textBlock = """
                public void print($type o) {
                    System.out.println(Objects.toString(o));
                }
                """.replace("$type", type);

            textBlock = String.format("""
                public void print(%s o) {
                    System.out.println(Objects.toString(o));
                }
                """, type);
           // Another alternative involves the introduction of a new instance method, String::formatted, which could be used as follows:
            textBlock = """
                public void print(%s object) {
                    System.out.println(Objects.toString(object));
                }
                """.formatted(type);
          /*
          }
         }
        }
        Additional Methods {
         The following methods will be added to support text blocks;
          - String::stripIndent(): removes incidental white space from a multi-line string, using the same algorithm used by the Java compiler. This is useful if you have a program that reads text as input data and you want to strip indentation in the same manner as is done for text blocks.
          - String::translateEscapes(): performs the translation of escape sequences (\b, \f, \n, \t, \r, \", \', \\ and octal escapes) and is used by the Java compiler to process text blocks and string literals. This is useful if you have a program that reads text as input data and you want to perform escape sequence processing. Note that Unicode escapes (\\uNNNN) are not processed.
          - String::formatted(Object... args): is equivalent to String.format(this, args). The advantage is that, as an instance method, it can be chained off the end of a text block:
            String output = """
                Name: %s
                Phone: %s
                Address: %s
                Salary: $%.2f
                """.formatted(name, phone, address, salary);
        }

        Alternatives {

        }
        */
        // More examples:
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
        String queryBefore = "SELECT \"EMP_ID\", \"LAST_NAME\" FROM \"EMPLOYEE_TB\"\n" +
            "WHERE \"CITY\" = 'INDIANAPOLIS'\n" +
            "ORDER BY \"EMP_ID\", \"LAST_NAME\";\n";
        String queryAfter = """
            SELECT "EMP_ID", "LAST_NAME" FROM "EMPLOYEE_TB"
            WHERE "CITY" = 'INDIANAPOLIS'
            ORDER BY "EMP_ID", "LAST_NAME";
            """;
        // If the last line does not end with a line terminator, you need to use String::indent to control the indentation explicitly. In the following example,
        String colors = """
        red
        green
        blue""";
        // all of the indentation is treated as incidental and is stripped away:
        //red
        //green
        //blue
        // To include some indentation in the string's contents, invoke the indent method on the text block:
        colors = """
        red
        green
        blue""".indent(4);
        //    red
        //    green
        //    blue
        // The \<line-terminator> escape sequence should be used when a text block's final new line needs to be excluded. This better frames the text block and allows the closing delimiter to manage indentation.
        // Original.
        String name = """
            red
            green
            blue""".indent(4);
        // Better.
        name = """
                red
                green
                blue\
            """;
        }
    }

    /**
     * String Templates.
     * This is a preview feature.
     */
    public static class StringTemplates {
        /*
        https://openjdk.org/jeps/459 String Templates (Second Preview)
        https://openjdk.org/jeps/430 String Templates (Preview)
        https://docs.oracle.com/en/java/javase/21/language/string-templates.html
        https://cr.openjdk.org/~jlaskey/templates/docs/api/java.base/java/lang/StringTemplate.html
        String templates complement Java's existing string literals and text blocks by coupling literal text with embedded expressions and template processors to produce specialized results. An embedded expression is a Java expression except it has additional syntax to differentiate it from the literal text in the string template. A template processor combines the literal text in the template with the values of the embedded expressions to produce a result.

        Goals {
         - Simplify the writing of Java programs by making it easy to express strings that include values computed at run time.
         - Enhance the readability of expressions that mix text and expressions, whether the text fits on a single source line (as with string literals) or spans several source lines (as with text blocks).
         - Improve the security of Java programs that compose strings from user-provided values and pass them to other systems (e.g., building queries for databases) by supporting validation and transformation of both the template and the values of its embedded expressions.
         - Retain flexibility by allowing Java libraries to define the formatting syntax used in string templates.
         - Simplify the use of APIs that accept strings written in non-Java languages (e.g., SQL, XML, and JSON).
         - Enable the creation of non-string values computed from literal text and embedded expressions without having to transit through an intermediate string representation.
        }

        Non-Goals {
         -It is not a goal to introduce syntactic sugar for the string concatenation operator (+), since that would circumvent the goal of validation.
         -It is not a goal to deprecate or remove the StringBuilder and StringBuffer classes, which have traditionally been used for complex or programmatic string composition.
        }

        Motivation {
         Developers routinely compose strings from a combination of literal text and expressions. The Java language and APIs provide several mechanisms for string composition, though unfortunately all have drawbacks.
         */
            void testClassicStringComposition() {
                var x = "1";
                var y = "2";
                String s;
                // String concatenation with the + operator produces hard-to-read code:
                s = x + " plus " + y + " equals " + (x + y);
                // StringBuilder is verbose:
                s = new StringBuilder()
                    .append(x)
                    .append(" plus ")
                    .append(y)
                    .append(" equals ")
                    .append(x + y)
                    .toString();
                // String::format and String::formatted separate the format string from the parameters, inviting arity and type mismatches:
                s = String.format("%2$d plus %1$d equals %3$d", x, y, x + y);
                s = "%2$d plus %1$d equals %3$d".formatted(x, y, x + y);
                // java.text.MessageFormat requires too much ceremony and uses an unfamiliar syntax in the format string:
                MessageFormat mf = new MessageFormat("{0} plus {1} equals {2}");
                s = mf.format(x, y, x + y);
            }
         /*
         String interpolation.
         Many programming languages offer string interpolation as an alternative to string concatenation. Typically, this takes the form of a string literal that contains embedded expressions as well as literal text. Embedding expressions in situ means that readers can easily discern the intended result. At run time, the embedded expressions are replaced with their (stringified) values — the values are said to be interpolated into the string.
         For example:
            C#	       $"{x} plus {y} equals {x + y}"
            Python     f"{x} plus {y} equals {x + y}"
            Scala      s"$x plus $y equals ${x + y}"
            JavaScript `${x} plus ${y} equals ${x + y}`
            Swift      "\(x) plus \(y) equals \(x + y)"
         Not only is interpolation more convenient than concatenation when writing code, it also offers greater clarity when reading code. The clarity is especially striking with larger strings. Unfortunately, the convenience of interpolation has a downside: It is easy to construct strings that will be interpreted by other systems but which are dangerously incorrect in those systems. Strings that hold SQL statements, HTML/XML documents, JSON snippets, shell scripts, and natural-language text all need to be validated and sanitized according to domain-specific rules. Since the Java programming language cannot possibly enforce all such rules, it is up to developers using interpolation to validate and sanitize. Typically, this means remembering to wrap embedded expressions in calls to escape or validate methods

         In summary, we could improve the readability and reliability of almost every Java program if we had a first-class, template-based mechanism for composing strings. Such a feature would offer the benefits of interpolation, as seen in other programming languages, but would be less prone to introducing security vulnerabilities. It would also reduce the ceremony of working with libraries that take complex input as strings.
        }

        At run time, a template expression is evaluated as follows:
        - The TemplateProcessor expression is evaluated to obtain an instance of the StringTemplate.Processor interface, that is, a template processor.
        - The TemplateArgument expression is evaluated to obtain an instance of StringTemplate.
        - The StringTemplate instance is passed to the process method of the StringTemplate.Processor instance, which composes a result.

        The following example declares a template expression that uses the template processor STR and contains one embedded expression, "name":
         */
            public static void testBasicStringTemplates() {
                String name = "Duke";
                String info = STR."My name is \{name}";
                println(info);
            }
        /*
        The template processor STR (https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StringTemplate.html#STR) is one of the template processors that's included in the JDK. It automatically performs string interpolation by replacing each embedded expression in the template with its value, converted to a string. The JDK includes two other template processors:
         - The FMT Template Processor (https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/FormatProcessor.html): It's like the STR template processor except that it accepts format specifiers as defined in java.util.Formatter and locale information in a similar way as in printf method invocations.
         - The RAW Template Processor (https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StringTemplate.html#RAW): It doesn't automatically process the string template like the STR template processors. You can use it to help you create your own template processors. Note that you can also implement the StringTemplate.Processor interface to create a template processor. See Creating a Template Processor.
        By implementing the StringTemplate.Processor interface, you can create your own template processor, which can return objects of any type, not just String, and throw check exceptions if processing fails.
        */
        // Any Java expression can be used as an embedded expression in a string template.
        public static void testStringTemplatesAnyExpression() {
            // Embedded expressions can invoke methods and access fields.
            String time = STR."Today is \{LocalDate.now()}";
            println(time);
            String canLang = STR."The language code of \{
                Locale.CANADA_FRENCH} is \{
                Locale.CANADA_FRENCH.getLanguage()}";
            println(canLang);
            // Prints:
            //Today is 2023-07-11
            //The language code of fr_CA is fr

            // Note that you can insert line breaks in an embedded expression and not introduce newlines in the result like in the previous example. In addition, you don't have to escape quotation marks in an embedded expression.
            Path filePath = Paths.get("Stemp.java");
            String msg = STR."The file \{filePath} \{
                // The Files class is in the package java.nio.file
                Files.exists(filePath) ? "does" : "does not"} exist";
            println(msg);
            String currentTime = STR."The time is \{
                DateTimeFormatter
                    .ofPattern("HH:mm:ss")
                    .format(LocalTime.now())
                } right now";
            println(currentTime);
            // Prints:
            //The file Stemp.java does exist
            //The time is 11:32:38 right now
        }
        public static void testStringTemplatesFormat()  {
            // Embedding a template expression in a string template.
            String[] a = { "X", "Y", "Z" };
            String letters = STR."\{a[0]}, \{STR."\{a[1]}, \{a[2]}"}";
            println(letters);
            // Prints:
            //X, Y, Z
            // Same as:
            String temp = STR."\{a[1]}, \{a[2]}";
            String letters2 = STR."\{a[0]}, \{temp}";

            // Multiline String Templates with The Text Blocks.
            String title = "My Web Page";
            String text = "Hello, world";
            String webpage = STR."""
                <html>
                  <head>
                    <title>\{title}</title>
                  </head>
                  <body>
                    <p>\{text}</p>
                  </body>
                </html>
                """;
            String customerName    = "Java Duke";
            String phone           = "555-123-4567";
            String address         = "1 Maple Drive, Anytown";
            String json = STR."""
                {
                    "name":    "\{customerName}",
                    "phone":   "\{phone}",
                    "address": "\{address}"
                }
                """;

            // Tabular data with The Text Blocks.
            record Rectangle(String name, double width, double height) {
                double area() { return width * height; }
            }
            Rectangle[] zone = new Rectangle[] {
                new Rectangle("First",  17.8, 31.4),
                new Rectangle("Second",  9.6, 12.2),
            };
            String table = STR."""
                Description\tWidth\tHeight\tArea
                \{zone[0].name}\t\t\{zone[0].width}\t\{zone[0].height}\t\{zone[0].area()}
                \{zone[1].name}\t\t\{zone[1].width}\t\{zone[1].height}\t\{zone[1].area()}
                Total \{zone[0].area() + zone[1].area()}
                """;
            println(table);
            // Prints:
            //Description     Width   Height  Area
            //First           17.8    31.4    558.92
            //Second          9.6     12.2    117.11999999999999
            //Total 676.04
            //
            // This is same data:
            //Description     Width    Height     Area
            //First           17.80    31.40      558.92
            //Second           9.60    12.20      117.12
            //                             Total  676.04
            // but with The FMT Template Processor that can use format specifiers that appear to the left of an embedded expression. These format specifiers are the same as those defined in the class java.util.Formatter (https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Formatter.html).
            String formattedTable = FormatProcessor.FMT."""
                Description     Width    Height     Area
                %-12s\{zone[0].name}  %7.2f\{zone[0].width}  %7.2f\{zone[0].height}     %7.2f\{zone[0].area()}
                %-12s\{zone[1].name}  %7.2f\{zone[1].width}  %7.2f\{zone[1].height}     %7.2f\{zone[1].area()}
                \{" ".repeat(28)} Total %7.2f\{zone[0].area() + zone[1].area()}
                """;
            println(formattedTable);
        }
        public static void testStringTemplatesDefer() {
            // The template expression STR."..." is a shortcut for invoking the process method of the STR template processor. That is, the now-familiar example:
            String name = "Joan";
            String info = STR."My name is \{name}";
            // is equivalent to:
            name = "Joan";
            StringTemplate st = RAW."My name is \{name}";
            info = STR.process(st);
            // The RAW template processor defers the processing of the template to a later time. Consequently, you can retrieve the template's string literals and embedded expression results before processing it.
            //To retrieve a template's string literals and embedded expression results, call StringTemplate::fragments and StringTemplate::values, respectively. To process a string template, call StringTemplate.process(StringTemplate.Processor) or StringTemplate.Processor.process(StringTemplate). You can also call the method StringTemplate::interpolate, which returns the same result as the STR template processor.
            int v = 10, w = 20;
            StringTemplate rawST = RAW."\{v} plus \{w} equals \{v + w}";
            List<String> fragments = rawST.fragments();
            List<Object> values = rawST.values();
            println(rawST.toString());
            // The method StringTemplate::fragments returns a list of fragment literals. In this example, the first and last fragment literals are zero-length strings because an embedded expression appears at the beginning and end of the template.
            fragments.stream().forEach(f -> System.out.print("[" + f + "]"));
            println("");
            // List of embedded expression results. These values are computed every time a string template is evaluated.
            values.stream().forEach(val -> System.out.print("[" + val + "]"));
            println("");
            println(rawST.process(STR));
            println(STR.process(rawST));
            println(rawST.interpolate());
            // Prints:
            //StringTemplate{ fragments = [ "", " plus ", " equals ", "" ], values = [10, 20, 30] }
            //[][ plus ][ equals ][]
            //[10][20][30]
            //10 plus 20 equals 30
            //10 plus 20 equals 30
            //10 plus 20 equals 30
        }
        /*
        Creating a Template Processor.
        */
        // Earlier we saw the template processors STR and FMT, which make it look as if a template processor is an object accessed via a field. That is useful shorthand, but again it is more accurate to say that a template processor is an object which is an instance of the functional interface StringTemplate.Processor. In particular, the object's class implements the single abstract method of that interface, process, which takes a StringTemplate and returns an object. A static field such as STR merely stores an instance of such a class. The actual class whose instance is stored in STR has a process method that performs a stateless interpolation for which a singleton instance is suitable, hence the upper-case field name.
        // Using fragments() and values(), we can easily create an interpolating template processor by passing a lambda expression to the static factory method StringTemplate.Processor::of:
        public static void testUserTemplateProcessor() {
            var INTER = StringTemplate.Processor.of((StringTemplate st) -> {
                String placeHolder = "•";
                String stencil = String.join(placeHolder, st.fragments());
                for (Object value : st.values()) {
                    String v = String.valueOf(value);
                    stencil = stencil.replaceFirst(placeHolder, v);
                }
                return stencil;
            });
            int x = 10, y = 20;
            println(INTER."\{x} plus \{y} equals \{x + y}");
            // Prints:
            //10 plus 20 equals 30
        }
        // We can make this interpolating template processor more efficient by building up its result from fragments and values, taking advantage of the fact that every template represents an alternating sequence of fragments and values:
        public static void testUserTemplateProcessorWithBuilder() {
            var INTER = StringTemplate.Processor.of((StringTemplate st) -> {
                StringBuilder sb = new StringBuilder();
                Iterator<String> fragIter = st.fragments().iterator();
                for (Object value : st.values()) {
                    sb.append(fragIter.next());
                    sb.append(value);
                }
                sb.append(fragIter.next());
                return sb.toString();
            });
            int x = 10, y = 20;
            println(INTER."\{x} plus \{y} equals \{x + y}");
        }
        // The factory method StringTemplate.Processor::of. These example processors return instances of String and throw no exceptions, so template expressions which use them will always evaluate successfully.
        // The utility method StringTemplate::interpolate does the same thing, successively concatenating fragments and values.
        // In contrast, a template processor that implements the StringTemplate.Processor interface directly can be fully general. It can return objects of any type, not just String. It can also throw checked exceptions if processing fails, either because the template is invalid or for some other reason, such as an I/O error. If a template processor throws checked exceptions then developers who use it in template expressions must handle processing failures with try-catch statements, or else propagate the exceptions to callers.
        public static void testUserTemplateProcessorAndUtilityMethod() {
            var INTER = StringTemplate.Processor.of(StringTemplate::interpolate);
            // The type of the template expression INTERProcessor."..." is specified by the first type argument of INTERProcessor's type, namely String. The checked exceptions thrown by the template processor INTER are specified by the second type argument of INTERProcessor's type. INTERProcessor throws no checked exceptions, but since the second type argument is mandatory we must express that fact by specifying an unchecked exception (RuntimeException).
            StringTemplate.Processor<String, RuntimeException> INTERProcessor =
                StringTemplate.Processor.of(StringTemplate::interpolate);
            int x = 10, y = 20;
            var s1 = INTER."\{x} plus \{y} equals \{x + y}";
            var s2 = INTERProcessor."\{x} plus \{y} equals \{x + y}";
            println(s1);
            println(s2);
            assert s1 == s2;
        }
        // Here is a template processor that returns not strings but, rather, instances of JSONObject:
        public static void testUserTemplateProcessorJSON() {
            class JSONObject { JSONObject(String interpolate) {} }
            class JSONException extends Throwable { public JSONException(String message) {} }
            var JSONof = StringTemplate.Processor.of(
                (StringTemplate st) -> new JSONObject(st.interpolate())
            );
            // Same as:
            StringTemplate.Processor<JSONObject, RuntimeException> JSONprocessor =
                StringTemplate.Processor.of(
                    (StringTemplate st) -> new JSONObject(st.interpolate())
                );
            String name    = "Joan Smith";
            String phone   = "555-123-4567";
            String address = "1 Maple Drive, Anytown";
            JSONObject doc =
                JSONof."""
                {
                    "name":    "\{name}",
                    "phone":   "\{phone}",
                    "address": "\{address}"
                };
                """;
            // Users of this hypothetical JSON processor never see the String produced by st.interpolate(). However, using st.interpolate() in this way risks propagating injection vulnerabilities into the JSON result. We can be prudent and revise the code to check the template's values first and throw a checked exception, JSONException, if a value is suspicious:
            StringTemplate.Processor<JSONObject, JSONException> JSON_VALIDATE =
                // This version of the template processor throws a checked exception, so we cannot create it using the factory method StringTemplate.Processor::of. Instead, we use a lambda expression on the right-hand side directly. In turn, this means we cannot use var on the left-hand side because the language requires an explicit target type for the lambda expression.
                (StringTemplate st) -> {
                    String quote = "\"";
                    List<Object> filtered = new ArrayList<>();
                    for (Object value : st.values()) {
                        if (value instanceof String str) {
                            if (str.contains(quote)) {
                                throw new JSONException("Injection vulnerability");
                            }
                            filtered.add(quote + str + quote);
                        } else if (value instanceof Number || value instanceof Boolean) {
                            filtered.add(value);
                        } else {
                            throw new JSONException("Invalid value type");
                        }
                    }
                    String jsonSource = StringTemplate.interpolate(st.fragments(), filtered);
                    return new JSONObject(jsonSource);
                };
            try {
                // To make it more efficient, we could memoize this template processor by compiling the template's fragments into a JSONObject with placeholder values and caching the result. If the next invocation of the processor uses the same fragments then it can inject the values of the embedded expressions into a fresh deep copy of the cached object; there would be no intermediate String anywhere.
                JSONObject docValidated =
                    JSON_VALIDATE."""
                    {
                        "name":    \{name},
                        "phone":   \{phone},
                        "address": \{address}
                    };
                    """;
            } catch (JSONException ex) {}
        }
        // Safely composing and executing database queries.
        //The template processor class below, QueryBuilder, first creates a SQL query string from a string template. It then creates a JDBC PreparedStatement from that query string and sets its parameters to the values of the embedded expressions.
        public static void testUserTemplateProcessorDB() throws SQLException {
            record QueryBuilder(Connection conn) implements StringTemplate.Processor<PreparedStatement, SQLException> {
                public PreparedStatement process(StringTemplate st) throws SQLException {
                    // 1. Replace StringTemplate placeholders with PreparedStatement placeholders
                    String query = String.join("?", st.fragments());
                    // 2. Create the PreparedStatement on the connection
                    PreparedStatement ps = conn.prepareStatement(query);
                    // 3. Set parameters of the PreparedStatement
                    int index = 1;
                    for (Object value : st.values()) {
                        switch (value) {
                            case Integer i -> ps.setInt(index++, i);
                            case Float f -> ps.setFloat(index++, f);
                            case Double d -> ps.setDouble(index++, d);
                            case Boolean b -> ps.setBoolean(index++, b);
                            default -> ps.setString(index++, String.valueOf(value));
                        }
                    }
                    return ps;
                }
            }
            Connection conn = null;
            var DB = new QueryBuilder(conn);
            Object name = "somename";
            //  Instead of the unsafe, injection-attack-prone code:
            //String query = "SELECT * FROM Person p WHERE p.last_name = '" + name + "'";
            //ResultSet rs = conn.createStatement().executeQuery(query);
            // Here is the more secure and more readable code:
            PreparedStatement ps = DB."SELECT * FROM Person p WHERE p.last_name = \{name}";
            ResultSet rs = ps.executeQuery();
        }


        // The following template processor, LocalizationProcessor, maps a string to a corresponding property in a resource bundle. When using this template processor, in your resource bundles, the property names are the string templates in your applications, where embedded expressions are substituted with underscores (_) and spaces with periods (.).
        record LocalizationProcessor(Locale locale) implements StringTemplate.Processor<String, RuntimeException> {
            public String process(StringTemplate st) {
                ResourceBundle resource = ResourceBundle.getBundle("resources", locale);
                String stencil = String.join("_", st.fragments());
                String msgFormat = resource.getString(stencil.replace(' ', '.'));
                return MessageFormat.format(msgFormat, st.values().toArray());
            }
        }
        public static void testLocalizationProcessor() {
            var userLocale = new Locale("en", "US");
            var LOCALIZE = new LocalizationProcessor(userLocale);
            String user = "Duke", option = "b";
            println(LOCALIZE."\{user} chose option \{option}");
            userLocale = new Locale("fr", "CA");
            LOCALIZE = new LocalizationProcessor(userLocale);
            println(LOCALIZE."\{user} chose option \{option}");
            // Prints:
            //Duke chose option b
            //Duke a choisi l'option b
        }
        // For more examples see:
        //  https://docs.oracle.com/en/java/javase/21/language/string-templates.html
        //  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StringTemplate.Processor.html

    }

    /**
     * Unnamed Patterns and Variables
     */
    public static class UnnamedPatternsAndVariables {
        /*
        https://openjdk.org/jeps/456 Unnamed Variables & Patterns
        https://openjdk.org/jeps/443 Unnamed Patterns and Variables (Preview)
        Enhance the Java programming language with unnamed variables and unnamed patterns, which can be used when variable declarations or nested patterns are required but never used. Both are denoted by the underscore character, "_".

        Summary {
         Used to improve code readability.
         The underscore keyword "_" is only allowed to declare:
          - unnamed patterns
          - local variables
          - exception parameters
          - lambda parameters
        }

        Goals {
         - Capture developer intent that a given binding or lambda parameter is unused, and enforce that property, so as to clarify programs and reduce opportunities for error.
         - Improve the maintainability of all code by identifying variables that must be declared (e.g., in catch clauses) but are not used.
         - Allow multiple patterns to appear in a single case label, provided that none of them declare any pattern variables.
         - Improve the readability of record patterns by eliding unnecessary nested type patterns.
        }

        Non-Goals {
         - It is not a goal to allow unnamed fields or method parameters.
         - It is not a goal to alter the semantics of local variables in, e.g., definite assignment analysis.
        }

        Motivation {
         Developers sometimes declare variables that they do not intend to use, whether as a matter of code style or because the language requires variable declarations in certain contexts. The intent of non-use is known at the time the code is written, but if it is not captured explicitly then later maintainers might accidentally use the variable, thereby violating the intent. If we could make it impossible to accidentally use such variables then code would be more informative, more readable, and less prone to error.

         Unused variables {
          The need to declare a variable that is never used is especially common in code whose side-effect is more important than its result. For example, this code calculates total as the side effect of a loop, without using the loop variable "transaction":
          */
        public void testUnusedPatternVariables0() {
            int[] orderIDs = {34, 45, 23, 27, 15};
            int total = 0;
            for (int id : orderIDs) { total++; } // "id" is unused.
            // Since Java 22 (21 preview).
            for (int _ : orderIDs) { total++; }
        }
        /*
        Another common examples:
         - The try-with-resources statement is always used for its side effect, namely the automatic closing of resources. In some cases a resource represents a context in which the code of the try block executes; the code does not use the context directly, so the name of the resource variable is irrelevant.
              try (var acquiredContext = ScopedContext.acquire()) {
         - Exceptions are the ultimate side effect, and handling one often gives rise to an unused variable.
              } catch (NumberFormatException ex) {
         - Code without side effects must sometimes declare unused variables.
              // Map which maps each key to the same placeholder value.
              ...stream.collect(Collectors.toMap(String::toUpperCase, v -> "NODATA"));
       }

       Unused pattern variables {
        Local variables can also be declared by type patterns — such local variables are known as pattern variables — and so type patterns can also declare variables that are unused.
        */
        sealed abstract class Ball permits RedBall, BlueBall, GreenBall { }
        final class RedBall   extends Ball { }
        final class BlueBall  extends Ball { }
        final class GreenBall extends Ball { }
        Ball ball = new RedBall();
        public void testUnusedPatternVariables1() {
            switch (ball) {
                case RedBall red -> process(ball); // "red" unused
                case BlueBall blue ->  process(ball); // "blue" unused
                case GreenBall green -> stopProcessing();
            }
            // Since Java 22 (21 preview).
            switch (ball) {
                case RedBall _ -> process(ball);
                case BlueBall _ ->  process(ball);
                case GreenBall _ -> stopProcessing();
            }
            // Or even use multiple patterns in a case label provided that they don't declare any pattern variables.
            switch (ball) {
                case RedBall _, BlueBall _ ->  process(ball);
                case GreenBall _ -> stopProcessing();
            }
        }
        private void stopProcessing() {}
        private void process(Ball ball) {}
        // A record class Box which can hold any type of Ball, but might also hold the null value:
        record Box<T extends Ball>(T content) { }
        Box<? extends Ball> box = new Box(new RedBall());
        public void testUnusedPatternVariables2() {
            switch (box) {
                // The nested type patterns still declare pattern variables that are not used.
                case Box(RedBall red) -> processBox(box);
                case Box(BlueBall blue) -> processBox(box);
                case Box(GreenBall green) -> stopProcessing();
                case Box(var itsNull) -> pickAnotherBox();
            }
            // Since Java 22 (21 preview).
            switch (box) {
                case Box(RedBall _), Box(BlueBall _) -> processBox(box);
                case Box(GreenBall _) -> stopProcessing();
                //case Box(var _) -> pickAnotherBox(); // Same as:
                case Box(_) -> pickAnotherBox();
            }
        }
        private void pickAnotherBox() {}
        private void processBox(Box<? extends Ball> box) {}
        /*
       }

       Unused nested patterns {
        Sometimes the shape of a data structure is as important as the data items within it.
        */
        double getDistance(Object obj1, Object obj2) {
            if (obj1 instanceof ColoredPoint(Point p1, Color c1) &&
                    obj2 instanceof ColoredPoint(Point p2, Color c2)
            ) {
                // The Color component of the ColoredPoint record is unused.
                return calculateDistance(p1.x(), p2.x(), p1.y(), p2.y());
                // Since Java 22 (21 preview).
            } else if (
                    obj1 instanceof ColoredPoint(Point p1, _) &&
                            obj2 instanceof ColoredPoint(Point p2, _)
            ) {
                // Note no value is bound to the unnamed pattern variable.
                //println("Color: " + _); // Error: "Using '_' as a reference is not allowed"
                return calculateDistance(p1.x(), p2.x(), p1.y(), p2.y());
                // Alternatively as of Java 22 (21 preview), you can keep the type pattern's type and elide just its name:
            } else if (
                    obj1 instanceof ColoredPoint(Point p1, Color _) &&
                            obj2 instanceof ColoredPoint(Point p2, Color _)
            ) {
                return calculateDistance(p1.x(), p2.x(), p1.y(), p2.y());
                // Even deeper patterns.
            } else if (
                    obj1 instanceof ColoredPoint(Point(int x1, int y1), Color c1) &&
                            obj2 instanceof ColoredPoint(Point(int x2, int y2), Color c2)
            ) {
                return calculateDistance(x1, x2, y1, y2);
            } else {
                return -1;
            }
        }
        double calculateDistance(double x1, double x2, double y1, double y2) {
            return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        }
        /*
       }
      }

      Description {
       Unnamed Variables {
        You can use the underscore keyword (_) not just as a pattern in a pattern list, but also as the name of a local variable, exception, or lambda parameter in a declaration when the value of the declaration isn't needed. This is called an unnamed variable, which represents a variable that's being declared, but it has no usable name.
        Valid Unnamed Variable Declarations:
         1. A local variable declaration statement in a block.
         2. A resource specification of a try-with-resources statement.
         3. The header of a basic for statement.
         4. The header of an enhanced for loop.
         5. An exception parameter of a catch block.
         6. A formal parameter of a lambda expression.
         */
        // 1.
        record Caller(String phoneNumber) {}
        static List everyFifthCaller(Queue<Caller> q, int prizes) {
            var winners = new ArrayList<Caller>();
            try {
                while (prizes > 0) {
                    Caller _ = q.remove();
                    Caller _ = q.remove();
                    Caller _ = q.remove();
                    Caller _ = q.remove();
                    winners.add(q.remove());
                    // Note that you don't have to assign the value returned by Queue::remove to a variable, named or unnamed. You might want to do so to signify that a lesser known API returns a value that your application doesn't use.
                    prizes--;
                }
            } catch (NoSuchElementException _) {} // Do nothing
            return winners;
        }
        // 2.
        static void doesFileExist(String path) {
            try (var _ = new FileReader(path)) {} // Do nothing
            catch (IOException e) { e.printStackTrace(); }
        }
        // 3.
        static Function<String,Integer> sideEffect = s -> {
            println(s);
            return 0;
        };
        static void sideEffect() {
            for (int i = 0, _ = sideEffect.apply("Starting for-loop"); i < 10; i++) {
                println(i);
            }
        }
        // 4.
        static void stringLength(String s) {
            int len = 0;
            for (char _ : s.toCharArray()) {
                len++;
            }
            println("Length of " + s + ": " + len);
        }
        // 5.
        static void validateNumber(String s) {
            try {
                int i = Integer.parseInt(s);
                println(i + " is valid");
            } catch (NumberFormatException _) {
                println(s + " isn't valid");
            }
        }
        record UniqueRectangle(String id, Point upperLeft, Point lowerRight) {}
        static Map getIDs(List<UniqueRectangle> r) {
            return r.stream().collect(Collectors.toMap(UniqueRectangle::id, _ -> "NODATA"));
        }
           /*
         }

         Unnamed pattern variables {
          No value is bound to the unnamed pattern variable.
          Multiple patterns in case labels. Currently, case labels are restricted to contain at most one pattern. With the introduction of unnamed pattern variables and unnamed patterns, it is more likely that we will have within a single switch block several case clauses with different patterns but the same right-hand side.
          If a case label has multiple patterns then it is a compile-time error for any of the patterns to declare any pattern variables. A case label with multiple case patterns can have a guard. The guard governs the case as a whole, rather than the individual patterns.
         }

         The unnamed pattern {
          The unnamed pattern is an unconditional pattern that matches anything but declares and initializes nothing. Like the unnamed type pattern var _ , the unnamed pattern can be nested in a record pattern. It cannot, however, be used as a top-level pattern in, e.g., an instanceof expression or a case label.
            if (r instanceof ColoredPoint(Point(int x, _), _)) { ... x ... }
         }
        }
        */
    }

    public static class BeforeSuper {
        public static void testBeforeSuper() {}
    // https://openjdk.org/jeps/447 Statements before super(...) (Preview)
    /*
    In constructors in the Java programming language, allow statements that do not reference the instance being created to appear before an explicit constructor invocation.

    Goals {
     - Give developers greater freedom to express the behavior of constructors, enabling the more natural placement of logic that currently must be factored into auxiliary static methods, auxiliary intermediate constructors, or constructor arguments.
     - Preserve the existing guarantee that constructors run in top-down order during class instantiation, ensuring that code in a subclass constructor cannot interfere with superclass instantiation.
    }

    Motivation {
     When one class extends another, the subclass inherits functionality from the superclass and can add functionality by declaring its own fields and methods.
     The initial values of fields declared in the subclass can depend upon the initial values of fields declared in the superclass, so it is critical to initialize fields of the superclass first, before fields of the subclass. For example, if class B extends class A then the fields of the unseen class Object must be initialized first, then the fields of class A, then the fields of class B.
     Initializing fields in this order means that constructors must run from the top down: A constructor in a superclass must finish initializing the fields declared in that class before a constructor in a subclass is run. This is how the overall state of an object is initialized.
     It is also critical to ensure that fields of a class are not accessed before they are initialized. Preventing access to uninitialized fields means that constructors must be constrained: The body of a constructor must not access fields declared in its own class or any superclass until the constructor in the superclass has finished.
     To guarantee that constructors run from the top down, the Java language requires that in a constructor body, any explicit invocation of another constructor must appear as the first statement; if no explicit constructor invocation is given, then one is injected by the compiler.
     To guarantee that constructors do not access uninitialized fields, the Java language requires that if an explicit constructor invocation is given, then none of its arguments can access the current object, {this}, in any way.
     These requirements guarantee top-down behavior and no-access-before-initialization, but they are heavy-handed because they make several idioms that are used in ordinary methods difficult, or even impossible, to use in constructors. Constructors could more naturally do argument validation, argument preparation, and argument sharing without doing that work via clumsy auxiliary methods or constructors.
     The following examples illustrate the issues.
    }
    */
    // Example 1: Validating superclass constructor arguments.
    //  Sometimes we need to validate an argument that is passed to a superclass constructor. We can validate the argument after the fact, but that means potentially doing unnecessary work:
        class PositiveBigInteger0 extends BigInteger {
            PositiveBigInteger0(long value) {
                super(String.valueOf(value)); // Potentially unnecessary work
                if (value <= 0) throw new IllegalArgumentException("non-positive value");
            }
        }
        // It would be better to declare a constructor that fails fast, by validating its arguments before it invokes the superclass constructor. Before Java 22 we can only do that in-line, using an auxiliary static method:
        class PositiveBigInteger1 extends BigInteger {
            PositiveBigInteger1(long value) {
                super(String.valueOf(verifyPositive(value)));
            }
            private static long verifyPositive(long value) {
                if (value <= 0) throw new IllegalArgumentException("non-positive value");
                return value;
            }
        }
        // As of Java 22:
        class PositiveBigInteger2 extends BigInteger {
            PositiveBigInteger2(long value) {
                if (value <= 0) throw new IllegalArgumentException("non-positive value");
                super(String.valueOf(value));
            }
        }
    // Example 2: Preparing superclass constructor arguments.
    //  Sometimes we must perform non-trivial computation in order to prepare arguments for a superclass constructor, resorting, yet again, to auxiliary methods:
        class SuperClass0 {
            SuperClass0(byte[] certificate) {}
        }
        class SubClass0 extends SuperClass0 {
            public SubClass0(Certificate certificate) {
                super(prepareByteArray(certificate));
            }
            // Auxiliary method
            private static byte[] prepareByteArray(Certificate certificate) {
                var publicKey = certificate.getPublicKey();
                if (publicKey == null) throw new IllegalArgumentException("null certificate");
                return switch (publicKey) {
                    default -> HexFormat.ofDelimiter(":")
                        .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");
                };
            }
        }
        // As of Java 22:
        class SubClass1 extends SuperClass0 {
            SubClass1(Certificate certificate) {
                var publicKey = certificate.getPublicKey();
                if (publicKey == null) throw new IllegalArgumentException("null certificate");
                final byte[] byteArray = switch (publicKey) {
                    default -> HexFormat.ofDelimiter(":")
                            .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");
                };
                super(byteArray);
            }
        }
    // Example 3: Sharing superclass constructor arguments.
    //  Sometimes we need to compute a value and share it between the arguments of a superclass constructor invocation. The requirement that the constructor invocation appear first means that the only way to achieve this sharing is via an intermediate auxiliary constructor:
        class SuperClass2 {
            SuperClass2(F f1, F f2) {}
        }
        class F {}
        class SubClass2 extends SuperClass2 {
            // Auxiliary constructor
            private SubClass2(int i, F f) {
                super(f, f); // f is shared here
                println(i);
            }
            // In the public Sub constructor we want to create a new instance of a class F and pass two references to that instance to the superclass constructor. We do that by declaring an auxiliary private constructor.
            SubClass2(int i) {
                this(i, new F());
            }
        }
        // As of Java 22:
        class SubClass3 extends SuperClass2 {
            SubClass3(int i) {
                var f = new F();
                super(f, f);
                println(i);
            }
        }

    // Records {
    //  Record constructors may not invoke super(...). However, noncanonical constructors must involve a canonical constructor by invoking this(...). Statements may appear before this(...).
    //  Remember that a canonical constructor is a constructor whose signature is the same as the record's component list. It initializes all the component fields of the record class. Alternative or noncanonical record constructors have argument lists that don't match the record's type parameters.
        record Pair<T extends Number>(T x, T y) { }
        record RectanglePair(float length, float width) {
            public RectanglePair(Pair<Float> corner) {
                float x = corner.x().floatValue();
                float y = corner.y().floatValue();
                if (x < 0 || y < 0) throw new IllegalArgumentException("non-positive value");
                this(corner.x().floatValue(), corner.y().floatValue());
            }
        }
    // }

    // Value objects {
    //  Generally, final fields may be initialized at any point during construction, and nothing prevents attempts to read those fields beforehand, revealing their pre-initialization values. Despite the claim that fields x and y are final, for a short window they are actually mutable. Were the constructor to share this with another thread, any code in that thread would be able to observe an identity-dependent, mutable object.
        public static class ValueObjectsPreInitializationValues {
        /*
        Value objects cannot support this sort of behavior, and need to be more carefully constructed. Specifically, value class constructors must set all of the class's instance fields in the earliest stages of construction, before the super() call. At this stage, the object is not yet fully-formed, its instance fields can't be read, and this references are illegal.
        See:
         JEP 447: Statements before super(...) (Preview) https://openjdk.org/jeps/447
         https://openjdk.org/jeps/401 https://openjdk.org/jeps/401
        */
            final int x;
            final int y;
            public int sum() { return x + y; }
            public ValueObjectsPreInitializationValues() { this(1, 2); }
            public ValueObjectsPreInitializationValues(int x, int y) {
                println(sum()); // 0
                this.x = x;
                println(sum()); // 1
                this.y = y;
                println(sum()); // 3
            }
        }
    // }
    // Enums {
    //  Currently, enum class constructors may contain explicit alternative constructor invocations but not superclass constructor invocations. Enum classes will benefit from the changes described above, primarily in that their constructors will be able to contain statements before explicit alternative constructor invocations.
    // }
    /*
    Description {
     The grammar for constructor bodies (JLS §8.8.7):
        ConstructorBody:
            { [BlockStatements] }
            { [BlockStatements] ExplicitConstructorInvocation [BlockStatements] }
     The block statements that appear before an explicit constructor invocation constitute the prologue of the constructor body. The statements in a constructor body with no explicit constructor invocation, and the statements following an explicit constructor invocation, constitute the epilogue.
     Code that appears in the argument list of an explicit constructor invocation in a constructor body are in a static context (JLS §8.1.3) and means that the arguments to such a constructor invocation are treated as if they were in a static method; in other words, as if no instance is available. The technical restrictions of a static context are stronger than necessary, however, and they prevent code that is useful and safe from appearing as constructor arguments. Rather than revise the concept of a static context, we define a new, strictly weaker concept of a pre-construction context to cover both the arguments to an explicit constructor invocation and any statements that occur before it. Within a pre-construction context, the rules are similar to normal instance methods, except that the code may not access the instance under construction.
     A return statement may be used in the epilogue of a constructor body if it does not include an expression (i.e. return; is allowed, but return e; is not). It is a compile-time error if a return statement appears in the prologue of a constructor body.
     Throwing an exception in a prologue of a constructor body is permitted. In fact, this will be typical in fail-fast scenarios.
    */
     // Here's some examples describe accessing the instance under construction.
     //  1. Any unqualified {this} expression is disallowed in a pre-construction context:
        class A1 {
            int i;
            A1() {
                // All three, same Error: "Cannot reference {this} before supertype constructor has been called".
                //this.i++;
                //this.hashCode(); // Error.
                //System.out.print(this); // Error.

                // In trickier cases, an illegal access does not need to contain a {this} or [super} keyword:
                //i++; // Error: "Cannot reference i before supertype constructor has been called".
                //hashCode(); // Error: "Cannot reference hashCode() before supertype constructor has been called".

                super();
            }
        }
     // Any field access, method invocation, or method reference qualified by super is disallowed in a pre-construction context:
        class A2 { int i; }
        class B2 extends A2 {
            B2() {
                //super.i++; // Error: "Cannot reference super before supertype constructor has been called".
                //i++; // Error: "Cannot reference j before supertype constructor has been called".
                super();
            }
        }
     // More confusingly, sometimes an expression involving {this} does not refer to the current instance but, rather, to the enclosing instance of an inner class:
        class A3 {
            int b;
            class B3 {
                int c;
                B3() {
                    A3.this.b++; // Allowed - enclosing instance.
                    //B3.this.c++; // Error: "Cannot reference {this} before supertype constructor has been called".
                    super();
                }
            }
        }
     // Unqualified method invocations are also complicated by the semantics of inner classes:
        class A4 {
            void hello() { System.out.println("Hello"); }
            class B4 {
                B4() {
                    // The outer enclosing A4 instance was already constructed, and therefore accessible, whereas the inner B4 instance was under construction and therefore not accessible.
                    hello(); // Allowed - enclosing instance method.
                    super();
                }
            }
            A4() {
                // Illegal because it requires providing the inner B4 constructor with an enclosing instance of outer A4, but the instance of A4 that would be provided is still under construction and therefore inaccessible.
                //new B4(); // Error: "Cannot reference {this} before supertype constructor has been called".

                // Similarly, in a pre-construction context, class instance creation expressions that declare anonymous classes cannot have the newly created object as the implicit enclosing instance. Here the anonymous class being declared is a subclass of B4, which is an inner class of A4. This means that the anonymous class would also have an enclosing instance of A4, and hence the class instance creation expression would have the newly created object as the implicit enclosing instance.
                // If the class S were declared static, or if it were an interface instead of a class, then it would have no enclosing instance and there would be no compile-time error.
                //var tmp = new B4() { }; // Same error.

                super();

                // Here the enclosing instance of the class instance creation expression is not the newly created C4 object but, rather, the lexically enclosing A4 instance.
                class C4 {
                    C4() {
                        var tmp = new B4() { };  // Allowed.
                        super();
                    }
                }
            }
        }
        // Unlike in a static context, code in a pre-construction context may refer to the type of the instance under construction, as long as it does not access the instance itself:
        class A5<T> {
            A5() {};
            A5(T list) {};
        }
        class B5<T> extends A5 {
            B5() {
                //super(this); // Error: "Cannot reference {this} before supertype constructor has been called".
            }
            B5(List<?> list) {
                super((T)list.get(0)); // Allowed - refers to {T} but not {this}.
            }
        }
    /*
    }
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
            listOfStrings.stream()
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toList());
            // String got some new stuff too:
            "\nPretius\n rules\n  all!".repeat(10).lines()
                .filter(Predicate.not(String::isBlank))
                .map(String::strip)
                .map(s -> s.indent(2))
                .collect(Collectors.toList());
            // No need to have an instance of array passed as an argument:
            String[] myArray= listOfStrings.toArray(String[]::new);
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
            String myObject = listOfStrings.stream()
                .filter(String::isBlank)
                .filter((b) -> false)
                .findFirst()
                // If there is no value, the method [get] throws an exception
                //.get();
                // But the readability is better with [orElseThrow]
                .orElseThrow();
        }
    }
    
    static List<String> listOfStrings = Arrays.asList("a", "b", "c");
}