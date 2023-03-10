package org.example.java_learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Flow.Processor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import io.reactivex.Observer;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.JavaFlowSupport.Sink;
import akka.stream.javadsl.JavaFlowSupport.Source;

import static java.util.stream.Collectors.toList;
import static org.example.java_learning.Const.printlnMainDelimeter;
import static org.example.java_learning.Util.*;
import static org.example.java_learning.ExchangeService.Money;

public class JavaConcurrencyEnhancedMain {
/*
Summary:
 1. Support for concurrency in Java has evolved and continues to evolve. Thread pools are generally helpful but can cause problems when you have many tasks that can block due to ill-structured, unmaintainable operations on threads.
  Parallel streams and fork/join parallelism provide higher-level constructs for expressing parallelism in programs iterating over collections and in programs involving divide-and-conquer, but that method invocations provide additional opportunities for executing code in parallel.
 2. Synchronous APIs are also known as blocking APIs, as the physical return is delayed until the result is ready (clearest when considering a call to an I/O operation), whereas asynchronous APIs can naturally implement nonblocking I/O (where the API call merely initiates the I/O operation without waiting for the result). Making methods asynchronous (returning before all their work is done) allows additional parallelism, complementary to that used to optimize loops.
 3. Reactive style of programming allows methods to invoke their callback multiple times. A Future can be completed only once, and its result is available to get(). In a sense, the reactive-style asynchronous API naturally enables a sequence (in a stream-wise way) of values, whereas the Future-style API corresponds to a one-shot conceptual framework.
  Reactive programming style using java.util.concurrent.Flow is one of the approaches to achieve Reactive Manifesto properties.
 4. Reactive systems and reactive programming express quite different ideas. A reactive system is a program whose architecture allows it to react to changes in its runtime environments. Properties that reactive systems should have are formalized in the Reactive Manifesto (http://www.reactivemanifesto.org).
  Four properties of the Reactive Manifesto:
   - Responsive. Means that a reactive system can respond to inputs in real time rather delaying a simple query because the system is processing a big job for someone else.
   - Resilient. Means that a system generally doesn’t fail because one component fails; a broken network link shouldn’t affect queries that don’t involve that link, and queries to an unresponsive component can be rerouted to an alternative component.
   - Elastic. Means that a system can adjust to changes in its workload and continue to execute efficiently.
   - Message-driven. Message-driven systems have internal APIs based on the box-and-channel model, with components waiting for inputs that are processed, with the results sent as messages to other components to enable the system to be responsive.
 5. The CompletableFuture class expresses one-shot asynchronous computations. Combinators can be used to compose asynchronous computations without the risk of blocking that’s inherent in traditional uses of Futures.
 6.  The Flow API is based on the publish-subscribe protocol, including backpressure, and forms the basis for reactive programming in Java.
 7. CompletableFutures technique over Streams provides more control by resizing thread pools, which ensures that your overall computation doesn’t block because all your (fixed number of) threads are waiting for I/O. So, if you’re doing computation-heavy operations with no I/O, the Stream interface provides the simplest implementation and the one that’s likely to be most efficient. (If all threads are compute-bound, there’s no point in having more threads than processor cores). But ff your parallel units of work involve waiting for I/O (including network connections), the CompletableFutures solution provides more flexibility and allows you to match the number of threads to the wait/computer (W/C) ratio. Another reason to avoid using parallel streams when I/O waits are involved in the stream-processing pipeline is that the laziness of streams can make it harder to reason about when the waits happen.


Concurrency is a programming property (overlapped execution) that can occur even for a single-core machine.
Parallelism is a property of execution hardware, simultaneous execution.


Java evolution of concurrency.
 Initially, Java had locks (via synchronized classes and methods), Runnables and Threads. In 2004, Java 5 introduced the java.util.concurrent package, which supported more expressive concurrency, particularly the ExecutorService (the ExecutorService interface extends the Executor interface with the submit method to run a Callable; the Executor interface merely has an execute method for Runnables.) interface (which decoupled task submission from thread execution), as well as Callable<T> and Future<T>, which produced higher-level and result-returning variants of Runnable and Thread and used generics (also introduced in Java 5). ExecutorServices can execute both Runnables and Callables. These features facilitated parallel programming on the multicore CPUs that started to appear the following year.
 Later versions of Java continued to enhance concurrency support, as it became increasingly demanded by programmers who needed to program multicore CPUs effectively. Java 7 added java.util.concurrent.RecursiveTask to support fork/join implementation of divide-and-conquer algorithms, and Java 8 added support for Streams and their parallel processing (building on the newly added support for lambdas).
 Java further enriched its concurrency features by providing support for composing Futures (via the Java 8 CompletableFuture implementation of Future and Java 9, provided explicit support for distributed asynchronous programming. There the application worked by contacting various web services and combining their information in real time for a user or to expose it as a further web service. This process is called reactive programming, and Java 9 provides support for it via the publish-subscribe protocol (specified by the java.util.concurrent.Flow interface). A key concept of CompletableFuture and java.util.concurrent.Flow is to provide programming structures that enable independent tasks to execute concurrently wherever possible and in a way that easily exploits as much as possible of the parallelism provided by multicore or multiple machines.

Threads and higher-level abstractions.
 In a multicore setting, perhaps a single-user laptop running only one user process, a program can never fully exploit the computing power of the laptop unless it uses threads. Each core can be used for one or more processes or threads, but if your program doesn’t use threads, it’s effectively using only one of the processor cores.
 Executors and thread pools.
  Java 5 provided the Executor framework and the idea of thread pools as a higher-level idea capturing the power of threads, which allow Java programmers to decouple task submission from task execution.
  Problems with threads.
   Java threads access operating-system threads directly. The problem is that operating system threads are expensive to create and to destroy (involving interaction with page tables), and moreover, only a limited number exist. Exceeding the number of operating system threads is likely to cause a Java application to crash mysteriously, so be careful not to leave threads running while continuing to create new ones. The number of operating system (and Java) threads will significantly exceed the number of hardware threads,  so all the hardware threads can be usefully occupied executing code even when some operating-system threads are blocked or sleeping. As an example, the 2016 Intel Core i7-6900K server processor has eight cores, each with two symmetric multiprocessing (SMP) hardware threads, leading to 16 hardware threads, and a server may contain several of these processors, consisting of perhaps 64 hardware threads. By contrast, a laptop may have only one or two hardware threads, so portable programs must avoid making assumptions about how many hardware threads are available. Contrarily, the optimum number of Java threads for a given program depends on the number of hardware cores available.
  Thread pools and why they’re better.
   The Java ExecutorService provides an interface where you can submit tasks and obtain their results later. The expected implementation uses a pool of threads, which can be created by one of the factory methods, such as the newFixedThreadPool method. One great outcome is that it’s cheap to submit thousands of tasks to a thread pool while keeping the number of tasks to a hardware-appropriate number. Several configurations are possible, including the size of the queue, rejection policy, and priority for different tasks. The programmer provides a task (a Runnable or a Callable), which is executed by a thread.
  Thread pools and why they’re worse.
   Thread pools are better than explicit thread manipulation in almost all ways, but you need to be aware of two "gotchas":
    - A thread pool with k threads can execute only k tasks concurrently. Any further task submissions are held in a queue and not allocated a thread until one of the existing tasks completes. This situation is generally good, in that it allows you to submit many tasks without accidentally creating an excessive number of threads, but you have to be wary of tasks that sleep or wait for I/O or network connections. In the context of blocking I/O, these tasks occupy worker threads but do no useful work while they’re waiting. Try taking four hardware threads and a thread pool of size 5 and submitting 20 tasks to it. You might expect that the tasks would run in parallel until all 20 have completed. But suppose that three of the first-submitted tasks sleep or wait for I/O. Then only two threads are available for the remaining 15 tasks, so you’re getting only half the throughput you expected (and would have if you created the thread pool with eight threads instead). It’s even possible to cause deadlock in a thread pool if earlier task submissions or already running tasks, need to wait for later task submissions, which is a typical use-pattern for Futures.
     The takeaway is to try to avoid submitting tasks that can block (sleep or wait for events) to thread pools, but you can’t always do so in existing systems.
    - Java typically waits for all threads to complete before allowing a return from main to avoid killing a thread executing vital code. Therefore, it’s important in practice and as part of good hygiene to shut down every thread pool before exiting the program (because worker threads for this pool will have been created but not terminated, as they’re waiting for another task submission). In practice, it’s common to have a long-running ExecutorService that manages an always running Internet service.
     Java does provide the Thread.setDaemon method to control this behavior.

Other abstractions of threads: non-nested with method calls.
 - Parallel Stream processing and the fork/join framework make use of a form of concurrency in which whenever any task (or thread) is started within a method call, the same method call waits for it to complete before returning. It is the strict fork/join form of concurrency.
 - If a spawned task escapes from an internal method call but is joined in an outer call, so that the interface provided to users still appears to be a normal call then you deal with relaxed form of fork/join.
 - There is another form of concurrency - an asynchronous method acts in a forms of concurrency in which threads created (or tasks spawned) by a user’s method call may outlive the call.
 The asynchronous form have some flaws:
  - The ongoing thread runs concurrently with the code following the method call and therefore requires careful programming to avoid data races.
  - What happens if the Java main() method returns before the ongoing thread has terminated? There are two answers, both rather unsatisfactory:
    -- Wait for all such outstanding threads before exiting the application (possible application crash by never terminating due to a forgotten thread).
    -- Kill all outstanding threads and then exit (possible risks interrupting a sequence of I/O operations writing to disk, thereby leaving an external data in an inconsistent state).
    To avoid both of these problems, ensure that a program keeps track of all threads it creates and joins them all before exiting (including shutting down any thread pools). Java threads can be labeled as daemon or nondaemon, using the setDaemon() method call. Daemon threads are killed on exit (and therefore are useful for services that don’t leave the disk in an inconsistent state), whereas returning from main continues to wait for all threads that aren’t daemons to terminate before exiting the program.
*/
    public static void main(String[] args) { javaConcurrencyEnhancedMain(); }

    public static void javaConcurrencyEnhancedMain() {
        /*
        Fn.test(AsyncSyncAPI::sequential, "testSequential");
        Fn.test(AsyncSyncAPI::parallelWithJoin, "testParallelWithJoin");
        {
            Fn.executorService = Executors.newFixedThreadPool(2);
            Fn.test(AsyncSyncAPI::parallelFuture, "testParallelFuture");
            Fn.executorService.shutdown();
        }
        Fn.test(AsyncSyncAPI::parallelCompletableFuture, "testParallelCompletableFuture");
        Fn.test(AsyncSyncAPI::parallelCallbackStyle, "testParallelCallbackStyle");

        AsyncSyncAPI.doComplexStuffWithWaitingInTheMiddle();

        {
            Fn.executorService = Executors.newFixedThreadPool(2);
            AsyncSyncAPI.CFComplete(Fn.TEST_VALUE);
            AsyncSyncAPI.CFCombine(Fn.TEST_VALUE);
            Fn.executorService.shutdown();
        }

        // Exception in thread "main" java.lang.RuntimeException: java.util.concurrent.ExecutionException: java.lang.UnsupportedOperationException: An intentional exception to test try-catch
        //CompletableFutureInDetails.getPriceAsyncExeption();

        CompletableFutureInDetails.testBestPriceFinder();

        ReactiveProgramming.TempMonitorV1.testTempMonitorV1();
        */
        //ReactiveProgramming.RxJavaTempMonitorV1.rxJavaTempMonitorV1();
        //ReactiveProgramming.RxJavaTempMonitorV2.rxJavaTempMonitorV2();
        ReactiveProgramming.AkkaTempMonitorV1.akkaTempMonitorV1();
    }
    /**
     * Some preparations. A set of functions to work around and their supplements.
     */
    public static class Fn {
        public static final int TEST_VALUE = 1;
        public static final long DURATION_IN_MILLIS = 1*1000;
        public static ExecutorService executorService;
        // Synchronous.
        public static int fi(int x) {
            dumbLoopForMillis(DURATION_IN_MILLIS);
            println("fi DONE");
            return x * 2;
        }
        public static int gi(int x) {
            println("gi DONE");
            return x + 1;
        }
        // Synchronous.
        public static Integer fI(int x) { return Integer.valueOf(fi(x)); }
        public static Integer gI(int x) { return Integer.valueOf(gi(x)); }
        // Asynchronous with CompletableFuture.
        public static Future<Integer> fCFI(int x) {
            return new CompletableFuture<Integer>().completeAsync(() -> fI(x));
        }
        public static Future<Integer> gCFI(int x) {
            return new CompletableFuture<Integer>().completeAsync(() -> gI(x));
        }
        // Asynchronous with callbacks (reactive-style).
        public static void fcbi(int x, IntConsumer dealWithResult) {
            dealWithResult.accept(fi(x));
        }
        public static void gcbi(int x, IntConsumer dealWithResult) {
            dealWithResult.accept(gi(x));
        }
        public static void doComplexStuff() {
            System.out.println("doing some complex stuff...");
        }
        public static void doFinishingStuff() {
            System.out.println("Done!");
        }

        public static void test(BiConsumer<TupleInt, Integer> action, String actionName) {
            println(actionName + printlnMainDelimeter);
            TupleInt result = new TupleInt();
            action.accept(result, TEST_VALUE);
            println(actionName + " -----RESULT: " + result.sum());
        }
    }

    /**
     * Synchronous and asynchronous APIs.
     */
    public static class AsyncSyncAPI {
        // Synchronous approach.
        // With synchronous API you get the result when method or other structure exits.
        private static void sequential(TupleInt res, int x) {
            res.value1 = Fn.fi(x);
            res.value2 = Fn.gi(x);
        }
        // In general, the Java compiler can do nothing to optimize this code because f and g may interact in ways that aren’t clear to the compiler.
        // Suppose that methods f and g execute for a long time, and you know that f and g don’t interact, or you don’t care, you want to execute f and g in separate CPU cores, which makes the total execution time only the maximum of that of the calls to f and g instead of the sum.
        // All you need to do is run the calls to f and g in separate threads.
        public static void parallelWithJoin(TupleInt res, int x) {
                println("parallelWithJoin ENTER");
            try {
                Thread t1 = new Thread(() -> {
                    res.value1 = Fn.fi(x);
                });
                Thread t2 = new Thread(() -> {
                    res.value2 = Fn.gi(x);
                });
                    println(res.sum());
                t1.start();
                    println(res.sum());
                t2.start();
                    println(res.sum());
                t1.join();
                    println(res.sum());
                t2.join();
                    println(res.sum());
                // Some of the complexity here has to do with transferring results back from the thread. Only final outer-object variables can be used in lambdas or inner classes, but the real problem is all the explicit thread manipulation.
            } catch (InterruptedException e) { e.printStackTrace(); }
                println("parallelWithJoin EXIT");
        }
        // You can simplify this code somewhat by using the Future API interface instead of Runnable.
        public static void parallelFuture(TupleInt res, int x) {
                println("parallelFuture ENTER");
            try {
                    println(res.sum());
                Future<Integer> y = Fn.executorService.submit(() -> Fn.fI(x));
                    println(res.sum());
                Future<Integer> z = Fn.executorService.submit(() -> Fn.gI(x));
                    println(res.sum());
                res.value1 = y.get().intValue();
                res.value2 = z.get().intValue();
                    println(res.sum());
            } catch (ExecutionException| InterruptedException e) { e.printStackTrace(); }
                println("parallelFuture EXIT");
        }
        // But this code is still polluted by the boilerplate code involving explicit calls to submit. You need a better way of expressing this idea, analogous to how internal iteration on Streams avoided the need to use thread-creation syntax to parallelize external iteration. The answer involves changing the API to an asynchronous API. Instead of allowing a method to return its result at the same time that it physically returns to the caller (synchronously), you allow it to return physically before producing its result. Thus, the call to f and the code following this call (here, the call to g) can execute in parallel. You can achieve this parallelism by using two techniques, both of which change the signatures of f and g.
        // The first technique uses Java Futures in a better way. Futures appeared in Java 5 and were enriched into CompletableFuture in Java 8 to make them composable.
        // The second technique is a reactive-programming style that uses the Java 9 java.util.concurrent.Flow interfaces, based on the publish-subscribe protocol.

        // Future-style API.
        // The idea is that method f returns a Future, which contains a task that continues to evaluate its original body, but the return from f happens as quickly as possible after the call. Method g similarly returns a future, and the get() used to wait for both Futures to complete and sums their results.
        private static void parallelCompletableFuture(TupleInt res, int x) {
                println("parallelCompletableFuture ENTER");
            try {
                    println(res.sum());
                Future<Integer> y = Fn.fCFI(x);
                    println(res.sum());
                Future<Integer> z = Fn.gCFI(x);
                    println(res.sum());
                res.value1 = y.get().intValue();
                res.value2 = z.get().intValue();
                    println(res.sum());
            } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
                println("parallelCompletableFuture EXIT");
        }

        // Reactive-style API.
        // The core idea is to use callback-style programming.
        // Some use the term callback to mean any lambda or method reference passed as an argument to a method, such as the argument to Stream.filter or Stream.map. Here it used only for those lambda and method references that can be called after the method has returned.
        public static void parallelCallbackStyle(TupleInt res, int x) {
                println("parallelCallbackStyle ENTER");
                println((res.sum()));
            Fn.fcbi(x, (int y) -> {
                res.value1 = y;
                println((res.sum()));
            });
            Fn.gcbi(x, (int z) -> {
                res.value2 = z;
                println((res.sum()));
            });
                println(res.sum());
                println("parallelCallbackStyle EXIT");
        // This alternative may seem to be surprising at first. How can f work if it doesn’t return a value? The answer is that you instead pass a callback (a lambda) to f as an additional argument, and the body of f spawns a task that calls this lambda with the result when it’s ready instead of returning a value with return. Again, f returns immediately after spawning the task to evaluate the body. Before this code prints the correct result (the sum of the calls to f and g), it prints the fastest value to complete (and occasionally instead prints the sum twice, as there’s no locking here, and both operands to + could be updated before either of the println calls is executed). There are two answers:
        //  - You could recover the original behavior by invoking println after testing with if-then-else that both callbacks have been called, perhaps by counting them with appropriate locking.
        //  - This reactive-style API is intended to react to a sequence of events, not to single results, for which Futures are more appropriate.
        // Note that this reactive style of programming allows methods f and g to invoke their callback dealWithResult multiple times. The original versions of f and g were obliged to use a return that can be performed only once. Similarly, a Future can be completed only once, and its result is available to get(). In a sense, the reactive-style asynchronous API naturally enables a sequence (in a stream-wise way) of values, whereas the Future-style API corresponds to a one-shot conceptual framework.
        }
        // You may argue that both alternatives make the code more complex. To some extent, this argument is correct; you shouldn’t thoughtlessly use either API for every method. But APIs keep code simpler (and use higher-level constructs) than explicit thread manipulation does. Also, careful use of these APIs for method calls that (a) cause long-running computations (perhaps longer than several milliseconds) or (b) wait for a network or for input from a human can significantly improve the efficiency of your application. In case (a), these techniques make your program faster without the explicit ubiquitous use of threads polluting your program. In case (b), there’s the additional benefit that the underlying system can use threads effectively without clogging up.


        // Sleeping (and other blocking operations) considered harmful.
        // When you’re interacting with a human or an application that needs to restrict the rate at which things happen, one natural way to program is to use the sleep() method. A sleeping thread still occupies system resources, however. This situation doesn’t matter if you have only a few threads, but it matters if you have many threads, most of which are sleeping. The lesson to remember is that tasks sleeping in a thread pool consume resources by blocking other tasks from starting to run (they can’t stop tasks already allocated to a thread, as the operating system schedules these tasks). It’s not only sleeping that can clog the available threads in a thread pool, of course. Any blocking operation can do the same thing.
        // Blocking operations fall into two classes:
        //  - waiting for another task to do something, such as invoking get() on a Future;
        //  - waiting for external interactions such as reads from networks, database servers, or human interface devices such as keyboards.
        // What can you do? One rather totalitarian answer is never to block within a task or at least to do so with a small number of exceptions in your code. The better alternative is to break your task into two parts — before and after — and ask Java to schedule the after part only when it won’t block.
        public static void doComplexStuffWithWaitingInTheMiddle() {
            // Think of both tasks being executed within a thread pool.
            // scheduledExecutor way, preferred one.
            // After this code queued to execute in the thread pool, it starts executing eventually. It executes doComplexStuff and then terminates, but only after having queued a task to do doFinishingStuff 10 seconds later.
            final long DURATION = 15*1000;
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            Fn.doComplexStuff();
            scheduledExecutorService.schedule(
                Fn::doFinishingStuff,
                DURATION,
                TimeUnit.MILLISECONDS
            );
            scheduledExecutorService.shutdown();

            // Sleep way, avoid this.
            // Halfway through executing, it blocks in the call to sleep, occupying a worker thread for 10 whole seconds doing nothing. Then it executes doFinishingStuff before terminating and releasing the worker thread.
            Fn.doComplexStuff();
            try { Thread.sleep(DURATION); } catch (InterruptedException e) { throw new RuntimeException(e); }
            Fn.doFinishingStuff();
            // Both design patterns may seem to lead to lots of hard-to-read code. But the Java CompletableFuture interface (later on) abstracts this style of code within the runtime library, using combinators instead of explicit uses of blocking get() operations on Futures, as presented earlier.
        }


        /*
        Exceptions work with asynchronous APIs.
         In both Future-based and reactive-style asynchronous APIs, the conceptual body of the called method executes in a separate thread, and the caller’s execution is likely to have exited the scope of any exception handler placed around the call. It’s clear that unusual behavior that would have triggered an exception needs to perform an alternative action.
         In the CompletableFuture implementation of Futures, the API includes provision for exposing exceptions at the time of the get() method and also provides methods such as exceptionally() to recover from exceptions (later on).
         For reactive-style asynchronous APIs, you have to modify the interface by introducing an additional callback, which is called instead of an exception being raised, as the existing callback is called instead of a return being executed. To do this, include multiple callbacks in the reactive API like this:
            void f(int x, Consumer<Integer> dealWithResult, Consumer<Throwable> dealWithException);
         Then the body of f might perform:
            dealWithException(e);
         If there are multiple callbacks, instead of supplying them separately, you can equivalently wrap them as methods in a single object. The Java 9 Flow API, for example, wraps these multiple callbacks within a single object (of class Subscriber<T> containing four methods interpreted as callbacks). Here are three of them:
            void onComplete() // get called when no further values (or exceptions) will be produced.
            void onError(Throwable throwable) // get called when an exception arose while trying to make a value available.
            void onNext(T item) // get called when a value is available.
         So, the API for f would now be:
            void f(int x, Subscriber<Integer> s);
         and the body of f would now indicate an exception, represented as Throwable t, by performing:
            s.onError(t);
         Compare this API containing multiple callbacks with reading numbers from a file or keyboard device. If you think of such a device as being a producer rather than a passive data structure, it produces a sequence of “Here’s a number” or “Here’s a malformed item instead of a number” items, and finally a “There are no more characters left (end-of-file)” notification. It’s common to refer to these calls as messages, or events. You might say, for example, that the file reader produced the number events 3, 7, and 42, followed by a malformednumber event, followed by the number event 2 and then by the end-of-file event. When seeing these events as part of an API, it’s important to note that the API signifies nothing about the relative ordering of these events (often called the channel protocol). In practice, the accompanying documentation specifies the protocol by using phases such as "After an onComplete event, no more events will be produced".
        */

        /*
        The box-and-channel technique.
        Consider a simple situation involving integers, generalizing the earlier example of calculating f(x) + g(x). Now you want to call method or function p with argument x, pass its result to functions q1 and q2, call method or function r with the results of these two calls, and then print the result.
        The box-and-channel model can be used to structure thoughts and code. In an important sense, it raises the level of abstraction for constructing a larger system. You draw boxes (or use combinators in programs) to express the computation you want, which is later executed, perhaps more efficiently than you might have obtained by hand-coding the computation. This use of combinators works not only for mathematical functions, but also for Futures and reactive streams of data. The box-and-channel model also helps you change perspective from directly programming concurrency to allowing combinators to do the work internally. Similarly, Java 8 Streams change perspective from the coder having to iterate over a data structure to combinators on Streams doing the work internally.
                     ┌──┐
                   ┌►│q1├─┐
               ┌─┐ │ └──┘ │  ┌─┐
            x─►│p├─┤      ├─►│r│─►
               └─┘ │ ┌──┐ │  └─┘
                   └►│q2├─┘
                     └──┘
        From above representation it's obvious that q1 and q2 can and should be computed in parallel fashion and before r but strictly after p.
        So, this approach is not optimal:
            int t = p(x);
            System.out.println( r(q1(t), q2(t)) );
        Another way is to use Futures to evaluate f and g in parallel:
            int t = p(x);
            Future<Integer> a1 = executorService.submit(() -> q1(t));
            Future<Integer> a2 = executorService.submit(() -> q2(t));
            System.out.println( r(a1.get(),a2.get()));
        This solution works well if the total amount of concurrency in the system is small. But what if the system becomes large, with many separate box-and-channel diagrams, and with some of the boxes themselves internally using their own boxes and channels? In this situation, many tasks might be waiting (with a call to get()) for a Future to complete, and the result may be underexploitation of hardware parallelism or even deadlock. Moreover, it tends to be hard to understand such large-scale system structure well enough to work out how many tasks are liable to be waiting for a get(). The solution that Java 8 adopts (CompletableFuture later on) is to use combinators. You can use methods such as compose() and andThen() on two Functions to get another Function.
        In the next section, you see how similar ideas of combinators work for CompletableFuture and prevent tasks from ever to have to wait using get().
        */

        /*
        CompletableFuture and combinators for concurrency.
        Future with get() method definitely causes thread blocks even for a moment in best cases but in worst case it can cause deadlocks. Java 8 brings to the party the ability to compose Futures, using the CompletableFuture implementation of the Future interface.
        An ordinary Future is typically created with a Callable, which is run, and the result is obtained with a get(). But a CompletableFuture allows you to create a Future without giving it any code to run, and a complete() method allows some other thread to complete it later with a value (hence the name) so that get() can access that value.
        */
        public static void CFComplete(int x) {
            try {
                CompletableFuture<Integer> a = new CompletableFuture<>();
                Fn.executorService.submit(() -> a.complete(Fn.fi(x)));
                int b = Fn.gi(x);
                System.out.println(a.get() + b);
            } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        }
        // This code can waste processing resources by having a thread blocked waiting for a get(). CompletableFuture enables you to avoid this situation.
        // But using CompletableFuture you can effectively combine actions.
        public static void CFCombine(int x) {
            try {
                CompletableFuture<Integer> a = new CompletableFuture<>();
                CompletableFuture<Integer> b = new CompletableFuture<>();
                CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> y + z);
                Fn.executorService.submit(() -> a.complete(Fn.fi(x)));
                Fn.executorService.submit(() -> b.complete(Fn.gi(x)));
                System.out.println(c.get());
            } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        // The method thenCombine has the following signature (slightly simplified to prevent the clutter associated with generics and wildcards):
        // CompletableFuture<V> thenCombine(CompletableFuture<U> other, BiFunction<T, U, V> fn)
        // The method takes two CompletableFuture values (with result types T and U) and creates a new one (with result type V). When the first two complete, it takes both their results, applies fn to both results, and completes the resulting future without blocking.
        // The thenCombine line is critical: without knowing anything about computations in the Futures a and b (y, z), it creates a computation that’s scheduled to run in the thread pool only when both of the first two computations have completed. The third computation, c, adds their results ((y, z) -> y + z) and (most important) isn’t considered to be eligible to execute on a thread until the other two computations have completed (rather than starting to execute early and then blocking). Therefore, no actual wait operation is performed, which was troublesome in the earlier versions of this code.
        }

        /**
         * Publish-subscribe and reactive programming.
         */
        public static class PubSubAndReactive {
        /*
        The mental model for a Future and CompletableFuture is that of a computation that executes independently and concurrently. The result of the Future is available with get() after the computation completes. Thus, Futures are one-shot, executing code that runs to completion only once. By contrast, the mental model for reactive programming is a Future-like object that, over time, yields multiple results.
        Java 9 models reactive programming with interfaces available inside java.util.concurrent.Flow and encodes what’s known as the publish-subscribe model (or protocol, often shortened to pub-sub).
        There are three main concepts:
         - A publisher to which a subscriber can subscribe.
         - The connection is known as a subscription.
         - Messages (also known an events) are transmitted via the connection.
        A simple but characteristic example of publish-subscribe combines events from two sources of information and publishes them for others to see. This process may sound obscure at first, but it’s what a cell containing a formula in a spreadsheet does conceptually. Model a spreadsheet cell C3, which contains the formula "=C1+C2". Whenever cell C1 or C2 is updated, C3 is updated to reflect the change.
        You need a way for c1 and c2 to subscribe c3 to their events in order to c3 sums the two values when this values get changed in c1 and/or c2. To do so you can use Publisher and Subscriber interfaces from Flow class:
            interface Publisher<T> {
                void subscribe(Subscriber<? super T> subscriber);
            }
        This interface takes a subscriber as an argument that it can communicate with. The Subscriber<T> interface includes a simple method, onNext, that takes that information as an argument and then is free to provide a specific implementation:
            interface Subscriber<T> {
                void onNext(T t);
            }
        Note that in our case the Cell is in fact both a Publisher (can subscribe cells to its events) and a Subscriber (reacts to events from other cells).
        */
            public class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
                private int value = 0;
                private String name;
                private List<Subscriber<? super Integer>> subscribers = new ArrayList<>();

                public SimpleCell(String name) { this.name = name; }
                @Override
                public void subscribe(Subscriber<? super Integer> subscriber) {
                    subscribers.add(subscriber);
                }
                public void subscribe(Consumer<? super Integer> onNext) {
                    subscribers.add(new Subscriber<>() {
                        @Override
                        public void onComplete() {}
                        @Override
                        public void onError(Throwable t) { t.printStackTrace(); }
                        @Override
                        public void onNext(Integer val) { onNext.accept(val); }
                        @Override
                        public void onSubscribe(Subscription s) {}
                    });
                }
                private void notifyAllSubscribers() {
                    subscribers.forEach(subscriber -> subscriber.onNext(value));
                }
                @Override
                public void onNext(Integer newValue) {
                    value = newValue;
                    println(name + ":" + value);
                    notifyAllSubscribers();
                }
                @Override
                public void onComplete() {}
                @Override
                public void onError(Throwable t) { t.printStackTrace(); }
                @Override
                public void onSubscribe(Subscription s) {}
            }
            public void testSimpleCell() {
                SimpleCell c3 = new SimpleCell("C3");
                SimpleCell c2 = new SimpleCell("C2");
                SimpleCell c1 = new SimpleCell("C1");
                c1.subscribe(c3);
                c1.onNext(10); // Update value of C1 to 10
                c2.onNext(20); // update value of C2 to 20
                // Prints:
                //C1:10
                //C3:10
                //C2:20
            }
            // Now how do you implement the behavior of "C3=C1+C2"? You need to introduce a separate class that’s capable of storing two sides of an arithmetic operation (left and right):
            public class ArithmeticCell extends SimpleCell {
                private int left;
                private int right;

                public ArithmeticCell(String name) { super(name); }

                public void setLeft(int left) {
                    this.left = left;
                    onNext(left + right);
                }
                public void setRight(int right) {
                    this.right = right;
                    onNext(right + left);
                }
            }
            public void testArithmeticCell() {
                test1();
                println("------------");
                test2();
            }
            private void test1() {
                ArithmeticCell c3 = new ArithmeticCell("C3");
                SimpleCell c2 = new SimpleCell("C2");
                SimpleCell c1 = new SimpleCell("C1");
                c1.subscribe(c3::setLeft);
                c2.subscribe(c3::setRight);
                c1.onNext(10); // Update value of C1 to 10
                c2.onNext(20); // update value of C2 to 20
                c1.onNext(15); // update value of C1 to 15
                // Prints:
                //C1:10
                //C3:10
                //C2:20
                //C3:30
                //C1:15
                //C3:35
            }
            private void test2() {
                ArithmeticCell c5 = new ArithmeticCell("C5");
                ArithmeticCell c3 = new ArithmeticCell("C3");
                SimpleCell c4 = new SimpleCell("C4");
                SimpleCell c2 = new SimpleCell("C2");
                SimpleCell c1 = new SimpleCell("C1");
                c1.subscribe(c3::setLeft);
                c2.subscribe(c3::setRight);
                // What’s neat about the publisher-subscriber interaction is the fact that you can set up a graph of publishers and subscribers. You could create another cell C5 that depends on C3 and C4 by expressing "C5=C3+C4", for example:
                c3.subscribe(c5::setLeft);
                c4.subscribe(c5::setRight);
                c1.onNext(10); // Update value of C1 to 10
                c2.onNext(20); // update value of C2 to 20
                c1.onNext(15); // update value of C1 to 15
                c4.onNext(1); // update value of C4 to 1
                c4.onNext(3); // update value of C4 to 3
                // Prints:
                //C1:10
                //C3:10
                //C5:10
                //C2:20
                //C3:30
                //C5:30
                //C1:15
                //C3:35
                //C5:35
                //C4:1
                //C5:36
                //C4:3
                //C5:38
            }

        /*
        Because data flows from publisher (producer) to subscriber (consumer), developers often use words such as upstream and downstream. In the preceding code examples, the data newValue received by the upstream onNext() methods is passed via the call to notifyAllSubscribers() to the downstream onNext() call.

        Practical programming of flows may want to signal things other than an onNext event, so subscribers (listeners) need to define onError and onComplete methods so that the publisher can indicate exceptions and terminations of data flow. These methods are among the reasons why this protocol is more powerful than the traditional Observer pattern.
        Two simple but vital ideas that significantly complicate the Flow interfaces are pressure and backpressure. These ideas can appear to be unimportant, but they’re vital for thread utilization. Suppose that your thermometer, which previously reported a temperature every few seconds, was upgraded to a better one that reports a temperature every millisecond. Could your program react to these events sufficiently quickly, or might some buffer overflow and cause a crash? For example, thread pools could get large numbers of tasks if more than a few tasks might block. Suppose that you subscribe to a publisher that furnishes all the SMS messages onto your phone. The subscription might work well on my newish phone with only a few SMS messages, but what happens in a few years when there are thousands of messages, all potentially sent via calls to onNext in less than a second? This situation is often known as pressure.
        Backpressure. The pull model instead of the push model.
        Think of a vertical pipe containing messages written on balls. You also need a form of backpressure (flow control), such as a mechanism that restricts the number of balls being added to the column. Backpressure is implemented in the Java 9 Flow API by a request() method (in a new interface called Subscription) that invites the publisher to send the next item(s), instead of items being sent at an unlimited rate (the pull model instead of the push model).
        */
        }

    }

    /**
     * CompletableFuture: composable asynchronous programming.
     */
    public static class CompletableFutureInDetails {
    /*
    The Future interface models an asynchronous computation and provides a reference to its result that becomes available when the computation itself is completed. Triggering a potentially time-consuming action inside a Future allows the caller Thread to continue doing useful work instead of waiting for the operation’s result. Another advantage of Future is that it’s friendlier to work with than lower-level Threads. To work with a Future, you typically have to wrap the time-consuming operation inside a Callable object and submit it to an ExecutorService.
    */
        public static void utilizeFuture() {
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<Double> future = executor.submit(new Callable<Double>() {
                public Double call() {
                    println("Futured task executed by separated thread asynchronously.");
                    // doSomeLongComputation();
                    return 0.0;
                }
            });
            println("The rest of the method executed by current thread.");
            // doSomethingElse(); // Do something else while the asynchronous operation is progressing.
            try {
                // Then, when you can’t do any other meaningful work without having the result of that asynchronous operation, you can retrieve it from the Future by invoking its get method. This method immediately returns the result of the operation if it’s already completed or blocks your thread, waiting for its result to be available. The zero argument version of get would instead wait indefinitely.
                // Retrieve the result of the asynchronous operation, blocking if it isn’t available yet but waiting for 1 second at most before timing out.
                Double result = future.get(1, TimeUnit.SECONDS);
            } catch (ExecutionException ee) {
                // The computation threw an exception.
            } catch (InterruptedException ie) {
                // The current thread was interrupted while waiting.
            } catch (TimeoutException te) {
                // The timeout expired before the Future completion.
            }
        }
        /*
        Expressing dependencies among results of a Future isn't trivia task to do. It’s difficult to code something like this: "when the result of the long computation is available, please send its result to another long computation, and when that’s done, combine its result with the result from another query". That's why it would be useful to have more declarative features in the implementation, such as these:
          - Combining two asynchronous computations both when they’re independent and when the second depends on the result of the first.
          - Waiting for the completion of all tasks performed by a set of Futures.
          - Waiting for the completion of only the quickest task in a set of Futures (possibly because the Futures are trying to calculate the same value in different ways) and retrieving its result.
          - Programmatically completing a Future (that is, by providing the result of the asynchronous operation manually).
          - Reacting to a Future completion (that is, being notified when the completion happens and then being able to perform a further action with the result of the Future instead of being blocked while waiting for its result).
        CompletableFuture makes all these things possible in a declarative way. The designs of Stream and CompletableFuture follow similar patterns, because both use lambda expressions and pipelining.

        To explore the CompletableFuture features, next you incrementally develop a best-price-finder application that contacts multiple online shops to find the lowest price for a given product or service. Along the way, you learn several important skills:
          - How to provide an asynchronous API for your customers (useful if you’re the owner of one of the online shops).
          - How to make your code nonblocking when you’re a consumer of a synchronous API. You discover how to pipeline two subsequent asynchronous operations, merging them into a single asynchronous computation. This situation arises, for example, when the online shop returns a discount code along with the original price of the item you wanted to buy. You have to contact a second remote discount service to find out the percentage discount associated with this discount code before calculating the actual price of that item.
          - How to reactively process events representing the completion of an asynchronous operation and how doing so allows the best-price-finder application to constantly update the best-buy quote for the item you want to buy as each shop returns its price, instead of waiting for all the shops to return their respective quotes. This skill also averts the scenario in which the user sees a blank screen forever if one of the shops’ servers is down.
        */
        // First define the API that each shop should provide.
        public static class ShopV0 {
            private final String name;
            private final Random random;

            public ShopV0(String name) {
                this.name = name;
                random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
            }
            public double getPrice(String product) {
                return calculatePrice(product);
            }
            public double calculatePrice(String product) {
                delay();
                return random.nextDouble() * product.charAt(0) + product.charAt(1);
            }
            public Future<Double> getPriceAsync(String product) {
                CompletableFuture<Double> futurePrice = new CompletableFuture<>();
                // Execute the computation asynchronously in a different Thread.
                new Thread(() -> {
                    double price = calculatePrice(product);
                    // Set the value returned by the long computation on the Future when it becomes available ("Complete future with computed result").
                    futurePrice.complete(price);
                }).start();
                // Return the Future without waiting for the computation of the result it contains to be completed.
                return futurePrice;
            }
            public String getName() {
                return name;
            }
        }
        // Now test new brand class.
        public static void testShopV0() {
            ShopV0 shop = new ShopV0("BestShop");
                long start = System.nanoTime();
            Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
                long invocationTime = ((System.nanoTime() - start) / 1_000_000);
                System.out.println("Invocation returned after " + invocationTime + " msecs");
            // Do some more tasks, like querying other shops
            doSomethingElse();
            // while the price of the product is being calculated
            try {
                double price = futurePrice.get();
                // Later you learn that it’s also possible for the client to avoid any risk of being blocked. Instead, the client can be notified when the Future is complete and can execute a callback code, defined through a lambda expression or a method reference, only when the result of the computation is available.
                    System.out.printf("Price is %.2f%n", price);
            } catch (ExecutionException | InterruptedException e) { throw new RuntimeException(e); }
                long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
                System.out.println("Price returned after " + retrievalTime + " msecs");
            // Prints:
            //Invocation returned after 2 msecs
            //Doing something else...
            //Price is 123.26
            //Price returned after 1013 msecs
        }
        private static void doSomethingElse() {
            System.out.println("Doing something else...");
        }


        // Dealing with errors.
        // The code above works correctly if everything goes smoothly. But what happens if the price calculation generates an error? Unfortunately, in this case you get a particularly negative outcome: the exception raised to signal the error remains confined in the thread, which is trying to calculate the product price, and ultimately kills the thread. As a consequence, the client remains blocked forever, waiting for the result of the [get()] method to arrive.
        // The client can prevent this problem by using an overloaded version of the get method that also accepts a timeout. It’s good practice to use a timeout to prevent similar situations elsewhere in your code. This way, the client at least avoids waiting indefinitely, but when the timeout expires, it’s notified with a TimeoutException. As a consequence, the client won’t have a chance to discover what caused that failure inside the thread that was trying to calculate the product price. To make the client aware of the reason why the shop wasn’t able to provide the price of the requested product, you have to propagate the Exception that caused the problem inside the CompletableFuture through its completeExceptionally method.
        public static Future<Double> getPriceAsync(String product) {
            CompletableFuture<Double> futurePrice = new CompletableFuture<>();
            new Thread(() -> {
                try {
                    double price = calculatePrice(product);
                    // Complete the Future with the price.
                    futurePrice.complete(price);
                } catch (Exception ex) {
                    // Otherwise, complete with the Exception that caused the failure, so the client will be notified with an ExecutionException.
                    futurePrice.completeExceptionally(ex);
                }
            }).start();
            return futurePrice;
        }
        public static double calculatePrice(String product) {
            throw new UnsupportedOperationException("An intentional exception to test try-catch");
        }
        public static void getPriceAsyncExeption() {
            Future<Double> futurePrice = getPriceAsync("my favorite product");
            try { double price = futurePrice.get(); }
            catch (ExecutionException | InterruptedException e) { throw new RuntimeException(e); }
        }


        // Creating a CompletableFuture with the supplyAsync factory method.
        // The CompletableFuture class comes with lots of handy factory methods. The supplyAsync method, for example, lets you rewrite the getPriceAsync method with a single statement:
        public static Future<Double> getPriceSupplyAsync(String product) {
            return CompletableFuture.supplyAsync(() -> calculatePrice(product));
            // You can specify a different Executor by passing it as a second argument to the overloaded version of this method. More generally, you can pass an Executor to all other CompletableFuture factory methods.
            // Note that supplyAsync nicely handles errors as described above.
        }


        // Making your code nonblocking.
        // Suppose that you have no control of the API implemented by the ShopV0 class and that it provides only synchronous blocking methods. This situation typically happens when you want to consume an HTTP API provided by some service. You see how it’s still possible to query multiple shops asynchronously, thus avoiding becoming blocked on a single request and thereby increasing the performance and the throughput of your best-price-finder application.
        public static class BestPriceFinderV0 {
            private final List<ShopV0> shops = Arrays.asList(
                new ShopV0("BestPrice"),
                new ShopV0("LetsSaveBig"),
                new ShopV0("MyFavoriteShop"),
                new ShopV0("BuyItAll"),
                new ShopV0("ShopEasy")
            );

            private final Executor executor = Executors.newFixedThreadPool(
                Math.min(shops.size(), 100),
                (Runnable r) -> {
                    Thread t = new Thread(r);
                    // Daemon threads don’t prevent the termination of the program. A Java program can’t terminate or exit while a normal thread is executing, so a leftover thread waiting for a never-satisfiable event causes problems.
                    t.setDaemon(true);
                    return t;
                }
            );

            public List<String> findPricesSequential(String product) {
                return shops.stream()
                    .map(shop -> shop.getName() + " price is " + shop.getPrice(product))
                    .collect(toList());
            }

            public List<String> findPricesParallel(String product) {
                return shops.parallelStream()
                    .map(shop -> shop.getName() + " price is " + shop.getPrice(product))
                    .collect(toList());
            }

            public List<String> findPricesFuture(String product) {
                List<CompletableFuture<String>> priceFutures = shops.stream()
                    .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getName() + " price is " + shop.getPrice(product), executor)
                    )
                    .collect(toList());
                return priceFutures.stream()
                    // You use two separate stream pipelines instead of putting the two map operations one after the other in the same stream-processing pipeline—and for a good reason. Given the lazy nature of intermediate stream operations, if you’d processed the stream in a single pipeline, you’d have succeeded only in executing all the requests to different shops synchronously and sequentially. The creation of each CompletableFuture to interrogate a given shop would start only when the computation of the previous one completed, letting the join method return the result of that computation.
                    .map(CompletableFuture::join)
                    // The join method of the CompletableFuture class has the same meaning as the get method also declared in the Future interface, the only difference being that join doesn’t throw any checked exception. By using join, you don’t have to bloat the lambda expression passed to this second map with a try/catch block.
                    .collect(toList());
            }
            // This implementation with CompletableFutures is faster than the original sequential and blocking implementation, but it might be slower than previous implementation using a parallel stream in some cases, such as when you’re using a machine that’s capable of running as many threads in parallel as a number of shops you have. So if your machine has 4 threads then four shop evaluations takes about 4 seconds for findPricesSequential method, about 1 sec. for findPricesParallel and about 2 sec. for findPricesFuture. But if you have 4 threads machine and 9 shops it will be 9, 3, 3 secs respectively. To gain more, actually much more from findPricesFuture you need properly set the Executor for CompletableFuture.supplyAsync. For example, on a machine with 24 threads and 100 shops findPricesParallel takes 5 sec. and findPricesFuture - only 1 sec. (i.e. the time of delay of 1 sec. mostly), but if you dont't pass Executor in CompletableFuture.supplyAsync it would be 5 sec. as with findPricesParallel. For 4 threaded machine it would be 1 sec. and this trend carries on until the number of shops reaches the threshold of 400 that you can calculate as fallows: [Nthreads = NCPU * UCPU * (1 + W/C)] (This formula came from the great book "Java Concurrency in Practice"), where  NCPU is the number of cores, available through Runtime.getRuntime().availableProcessors(), UCPU is the target CPU use (between 0 and 1). W/C is the ratio of wait time to compute time. BestPriceFinderV0 is spending about 99 percent of its time waiting for the shops' responses, so you could estimate a W/C ratio of 100. If your target is 100 percent CPU use, you should have a pool with 400 threads.
            // How do you size this Executor correctly? In BestPriceFinderV0 case, a sensible choice seems to be to create an Executor with a number of threads in its pool that takes into account the actual workload you could expect in your application. In practice, it’s wasteful to have more threads than shops, because you’ll have threads in your pool that are never used. For this reason, you need to set up an Executor with a fixed number of threads equal to the number of shops you have to query, so that you have one thread for each shop.

            // CompletableFutures technique over Streams provides more control by resizing thread pools, which ensures that your overall computation doesn’t block because all your (fixed number of) threads are waiting for I/O. So, if you’re doing computation-heavy operations with no I/O, the Stream interface provides the simplest implementation and the one that’s likely to be most efficient. (If all threads are compute-bound, there’s no point in having more threads than processor cores). But ff your parallel units of work involve waiting for I/O (including network connections), the CompletableFutures solution provides more flexibility and allows you to match the number of threads to the wait/computer (W/C) ratio. Another reason to avoid using parallel streams when I/O waits are involved in the stream-processing pipeline is that the laziness of streams can make it harder to reason about when the waits happen.
        }


        // Pipelining asynchronous tasks.
        // Suppose that all the shops have agreed to use a centralized discount service. This service uses five discount codes, each of which has a different discount percentage(Discount.Code enumeration). Also suppose that the shops have agreed to change the format of the result of the getPrice method, which now returns a String in the format ShopName:price:DiscountCode. So that invoking getPrice method might then return a String such as: "BestPrice:123.26:GOLD".
        public static class Shop {
            private final String name;
            private final Random random;
            public Shop(String name) {
                this.name = name;
                random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
            }
            public String getPrice(String product) {
                double price = getBasePrice(product);
                Discount.Code code =
                    Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
                return name + ":" + price + ":" + code;
            }
            public double getBasePrice(String product) {
                return calculatePrice(product);
            }
            public double calculatePrice(String product) {
                delay();
                return format(random.nextDouble() * product.charAt(0) + product.charAt(1));
            }
            public String getName() { return name; }
        }
        // The best-price-finder application should now obtain the prices from the shops; parse the resulting Strings; and, for each String, query the discount server’s needs. This process determines the final discounted price of the requested product. (The actual discount percentage associated with each discount code could change, which is why you query the server each time). The parsing of the Strings produced by the shop is encapsulated in the Quote class. You can obtain an instance of the Quote class - which contains the name of the shop, the nondiscounted price, and the discount code — by passing the String produced by a shop to the static parse factory method. The Discount service also has an applyDiscount method that accepts a Quote object and returns a String stating the discounted price for the shop that produced that quote. Because the Discount service is a remote service it has the apply method utilizing delay functionality.
        public static class BestPriceFinder {
            private final List<Shop> shops = Arrays.asList(
                new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"),
                new Shop("ShopEasy"));

            private final Executor executor = Executors.newFixedThreadPool(
                Math.min(shops.size(), 100),
                (Runnable r) -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
            );
            public List<String> findPricesSequential(String product) {
                return shops.stream()
                    // Retrieve the nondiscounted price from each shop into a String that encodes the price and discount code of the requested product for that shop.
                    .map(shop -> shop.getPrice(product))
                    // Transform the Strings returned by the shops in Quote objects.
                    .map(Quote::parse)
                    // Contact the Discount service to apply the discount on each Quote and return another String containing the name of the shop with that price.
                    .map(Discount::applyDiscount)
                    .collect(toList());
                // This code takes 10 seconds to run, because the 5 seconds required to query the five shops sequentially is added to the 5 seconds consumed by the discount service in applying the discount code to the prices returned by the five shops.
            }
            public List<String> findPricesParallel(String product) {
                return shops.parallelStream()
                    .map(shop -> shop.getPrice(product))
                    .map(Quote::parse)
                    .map(Discount::applyDiscount)
                    .collect(toList());
            }
            // Composing synchronous and asynchronous operations.
            public List<String> findPricesFuture(String product) {
                List<CompletableFuture<String>> priceFutures = findPricesStream(product)
                    .collect(Collectors.<CompletableFuture<String>>toList());
                return priceFutures.stream()
                    // Wait for all the Futures in the stream to be completed and extract their respective results.
                    .map(CompletableFuture::join)
                    .collect(toList());
            }
            // Note that findPricesFuture and findPricesStream can be easily merged. But the main idea is you can make synchronous operations like getPrice asynchronous when necessary, using the feature provided by the CompletableFuture class.
            public Stream<CompletableFuture<String>> findPricesStream(String product) {
                return shops.stream()
                    // Asynchronously retrieve the nondiscounted price from each shop.
                    .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
                    // Transform the String returned by a shop into a Quote object when it becomes available.Because this parsing operation isn’t invoking any remote service or doing any I/O in general, it can be performed almost instantaneously and can be done synchronously without introducing any delay. For this reason, thenApply method is used. Using the thenApply method doesn’t block your code until the CompletableFuture on which you’re invoking it is complete.
                    .map(future -> future.thenApply(Quote::parse))
                    // Compose the resulting asynchronous task with another asynchronous task,  and pass to it a Function. This Function has as an argument the value returned by that first CompletableFuture when it completes, and it returns a second CompletableFuture that uses the result of the first as input for its computation. Note that with this approach, while the Futures are retrieving the quotes from the shops, the main thread can perform other useful operations, such as responding to UI events.
                    .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
                // The thenCompose method like other methods of the CompletableFuture class, has a variant with an Async suffix, thenComposeAsync. In general, a method without the Async suffix in its name executes its task in the same thread as the previous task, whereas a method terminating with Async always submits the succeeding task to the thread pool, so each of the tasks can be handled by a different thread. In this case, the result of the second CompletableFuture depends on the first, so it makes no difference to the final result or to its broad-brush timing whether you compose the two CompletableFutures with one or the other variant of this method. In this case thenCompose slightly more efficient due to less thread-switching overhead. Note, however, that it may not always be clear which thread is being used, especially if you run an application that manages its own thread pool (such as Spring).
            }


            // Combining two CompletableFutures: dependent and independent.
            // Use the thenCombine method if you need to combine the results of the operations performed by two independent CompletableFutures, and you don’t want to wait for the first to complete before starting the second. This method takes as a second argument a BiFunction, which defines how the results of the two CompletableFutures are to be combined when both become available. Like thenCompose, the thenCombine method comes with an Async variant. In this case, using the then CombineAsync method causes the combination operation defined by the BiFunction to be submitted to the thread pool and then executed asynchronously in a separate task.
            // Suppose one of the shops provides prices in € (EUR), but you always want to communicate them to your customers in $(USD). You can asynchronously ask the shop the price of a given product and separately retrieve, from a remote exchange-rate service, the current exchange rate between € and $. After both requests have completed, you can combine the results by multiplying the price by the exchange rate. With this approach, you obtain a third CompletableFuture that completes when the results of the two CompletableFutures are both available and have been combined by means of the BiFunction.
            private CompletableFuture<Double> getFuturePriceInUSD(Shop shop, String product) {
                return CompletableFuture.supplyAsync(() -> shop.getBasePrice(product))
                    .thenCombine(
                        CompletableFuture.supplyAsync(
                            () ->  ExchangeService.getRate(Money.EUR, Money.USD)
                        )
                            // Timeout management added in Java 9
                            .completeOnTimeout(
                                // Use a default exchange rate if the exchange service doesn’t provide a result in 1 second.
                                ExchangeService.DEFAULT_RATE,
                        1,
                                TimeUnit.SECONDS
                            )
                        // Combine the price and exchange rate by multiplying them. Because the combination operation is a simple multiplication, performing it in a separate task would have been a waste of resources, so you need to use the thenCombine method instead of its asynchronous thenCombineAsync counterpart.
                        , (price, rate) -> price * rate
                    )
                        // Timeout management added in Java 9
                        .orTimeout(3, TimeUnit.SECONDS);
            }
            public List<String> findPricesInUSDv1(String product) {
                List<CompletableFuture<Double>> priceFutures = new ArrayList<>();
                for (Shop shop : shops) {
                    priceFutures.add(getFuturePriceInUSD(shop, product));
                }
                return priceFutures.stream()
                    .map(CompletableFuture::join)
                    // Drawback: The shop is not accessible anymore outside the loop, so the getName() call below has been commented out.
                    .map(price -> /*shop.getName() +*/ " price is " + price)
                    .collect(toList());
            }
            public List<String> findPricesInUSDv2(String product) {
                List<CompletableFuture<String>> priceFutures = new ArrayList<>();
                for (Shop shop : shops) {
                    // Here, an extra operation has been added so that the shop name is retrieved within the loop. As a result, we now deal with CompletableFuture<String> instances.
                    priceFutures.add(getFuturePriceInUSD(shop, product).thenApply(price -> shop.getName() + " price is " + price));
                }
                return priceFutures
                    .stream()
                    .map(CompletableFuture::join)
                    .collect(toList());
            }
            public List<String> findPricesInUSDv3(String product) {
                return shops.stream()
                    .map(shop -> getFuturePriceInUSD(shop, product).thenApply(price -> shop.getName() + " price is " + price)
                    )
                    .map(CompletableFuture::join)
                    .collect(toList());
            }

            // To get a more tangible idea of the code readability benefits of CompletableFuture , here is same result purely in Java 7.
            public List<String> findPricesInUSDJava7(String product) {
                ExecutorService executor = Executors.newCachedThreadPool();
                List<Future<Double>> priceFutures = new ArrayList<>();
                for (Shop shop : shops) {
                    final Future<Double> futureRate = executor.submit(new Callable<Double>() {
                        @Override
                        public Double call() {
                            return ExchangeService.getRate(Money.EUR, Money.USD);
                        }
                    });
                    Future<Double> futurePriceInUSD = executor.submit(new Callable<Double>() {
                        @Override
                        public Double call() {
                            try {
                                double priceInEUR = shop.getBasePrice(product);
                                // Here is the combining.
                                // Note that using thenCombineAsync instead of thenCombine in getFuturePriceInUSD would have been equivalent to performing the price by rate multiplication in a third Future in listing. The difference between these two implementations may seem to be small only because you’re combining two Futures. Also note that using Future<Double> you can't call shop.getName(), so you need another Future<String> and some conversion on Future<Double>.
                                return priceInEUR * futureRate.get();
                            }
                            catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        }
                    });
                    priceFutures.add(futurePriceInUSD);
                }
                List<String> prices = new ArrayList<>();
                for (Future<Double> priceFuture : priceFutures) {
                    try {
                        prices.add(/*shop.getName() +*/ " price is " + priceFuture.get());
                    }
                    catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return prices;
            }

            // Now suppose you want to display the price for a given shop as soon as it becomes available without waiting for the slowest one (which may even time out). To achieve this goal you have to react to the completion of a CompletableFuture instead of invoke get() or join() on it and thereby remaining blocked until the CompletableFuture itself completes. So you have to use a stream (findPricesStream) instead of a list.
            public void printPricesStream(String product) {
                long start = System.nanoTime();
                CompletableFuture[] futures = findPricesStream(product)
                    // The CompletableFuture API provides this feature via the thenAccept method, which takes as an argument a Consumer of the value with which it completes. In this case, this value is the String returned by the discount services and containing the name of a shop together with the discounted price of the requested product for that shop.
                    // Because the thenAccept method already specifies how to consume the result produced by the CompletableFuture when it becomes available, it returns a CompletableFuture<Void>. As a result, the map operation returns a Stream<CompletableFuture<Void>>. You can’t do much with a CompletableFuture<Void> except wait for its completion, but this is exactly what you need.
                    // To see the difference of computation time each thread takes substitute delay() with delayRandom() in Shop.calculatePrice() method.
                    .map(f -> f.thenAccept(s -> System.out.println(s + " (done in " + ((System.nanoTime() - start) / 1_000_000) + " msecs)")))
                    // You also want to give the slowest shop a chance to provide its response and print its returned price. To do so, you can put all the CompletableFuture<Void>'s of the stream into an array and then wait for all of them to complete, as shown in this next listing.
                    .toArray(size -> new CompletableFuture[size]);
                // The allOf factory method takes as input an array of CompletableFutures and returns a CompletableFuture<Void> that’s completed only when all the CompletableFutures passed have completed. Invoking join on the CompletableFuture returned by the allOf method provides an easy way to wait for the completion of all the CompletableFutures in the original stream. This technique is useful for the best-price-finder application because it can display a message such as All shops returned results or timed out so that a user doesn’t keep wondering whether more prices might become available.
                CompletableFuture.allOf(futures).join();
                System.out.println("All shops have now responded in " + ((System.nanoTime() - start) / 1_000_000) + " msecs");
                // The thenAccept method has an Async variant named thenAcceptAsync. The Async variant schedules the execution of the Consumer passed to it on a new thread from the thread pool instead of performing it directly, using the same thread that completed the CompletableFuture. Because you want to avoid an unnecessary context switch, and because (more important) you want to react to the completion of the CompletableFuture as soon as possible instead waiting for a new thread to be available, you don’t use this variant here.
            }
        }

        public static void testBestPriceFinder() {
            String product = "myPhone27S";
            BestPriceFinderV0 bestPriceFinderV0 = new BestPriceFinderV0();
            execute(
                "sequential",
                () -> bestPriceFinderV0.findPricesSequential(product)
            );
            execute(
                "parallel",
                () -> bestPriceFinderV0.findPricesParallel(product)
            );
            execute(
                "composed CompletableFuture",
                () -> bestPriceFinderV0.findPricesFuture(product)
            );

            BestPriceFinder bestPriceFinder = new BestPriceFinder();
            execute(
            "sequential",
                () -> bestPriceFinder.findPricesSequential(product));
            execute(
            "parallel",
                () -> bestPriceFinder.findPricesParallel(product));
            execute(
            "composed CompletableFuture",
                () -> bestPriceFinder.findPricesFuture(product));
            execute(
                "combined USD CompletableFuture explicit loop without shop name",
                () -> bestPriceFinder.findPricesInUSDv1(product)
            );
            execute(
                "combined USD CompletableFuture explicit loop",
                () -> bestPriceFinder.findPricesInUSDv2(product)
            );
            execute(
                "combined USD CompletableFuture stream",
                () -> bestPriceFinder.findPricesInUSDv3(product)
            );
            execute(
                "combined USD with Java7 without shop name",
                () -> bestPriceFinder.findPricesInUSDJava7(product)
            );

            bestPriceFinder.printPricesStream(product);
        }
        private static void execute(String msg, Supplier<List<String>> s) {
            long start = System.nanoTime();
            System.out.println(s.get());
            long duration = (System.nanoTime() - start) / 1_000_000;
            System.out.println(msg + " done in " + duration + " msecs");
        }
    }


    /**
     * Reactive programming.
     */
    public static class ReactiveProgramming {
        /*
        The Reactive Manifesto - set of core principles for developing reactive applications and systems. The key features of a reactive system:
          - Responsive. A reactive system has a fast and, even more important, consistent, predictable response time. As a result, the user knows what to expect. This fact in turn increases user confidence, which is without a doubt the key aspect of a usable application.
          - Resilient. A system has to remain responsive despite failures. The Reactive Manifesto suggests different techniques to achieve resiliency, including replicating the execution of components, decoupling these components in time (sender and receiver have independent life cycles) and space (sender and receiver run in different processes), and letting each component asynchronously delegate tasks to other components.
          - Elastic .Another issue that harms the responsiveness of applications is the fact that they can be subject to different workloads during their life cycles. Reactive systems are designed to react automatically to a heavier workload by increasing the number of resources allocated to the affected components.
          - Message-driven. Resilience and elasticity require the boundaries of the components that form the system to be clearly defined to ensure loose coupling, isolation, and location transparency. Communication across these boundaries is performed through asynchronous message passing. This choice enables both resiliency (by delegating failures as messages) and elasticity (by monitoring the number of exchanged messages and then scaling the number of the resources intended to manage them accordingly).

        Reactive at application level.
        The reactive frameworks and libraries share threads (relatively expensive and scarce resources) among lighter constructs such as futures; actors; and (more commonly) event loops dispatching a sequence of callbacks intended to aggregate, transform, and manage the events to be processed.
        These techniques not only have the benefit of being cheaper than threads, but also have a major advantage from developers' point of view: they raise the level of abstraction of implementing concurrent and asynchronous applications, allowing developers to concentrate on the business requirements instead of dealing with typical problems of low level multithreading issues such as synchronization, race conditions, and deadlocks.
        The most important thing to pay attention to when using these thread-multiplexing strategies is to never perform blocking operations (all I/O-bound operations such as accessing a database or the file system or calling a remote service that may take a long or unpredictable time to complete) inside the main event loop.
        It’s easy and interesting to explain why you should avoid blocking operations by providing a practical example as follows:
          Imagine a simplified yet typical multiplexing scenario with a pool of two threads processing three streams of events. Only two streams can be processed at the same time and the streams have to compete to share those two threads as fairly and efficiently as possible. Now suppose that processing one stream’s event triggers a potentially slow I/O operation, such as writing into the file system or retrieving data from a database by using a blocking API. In this situation Thread 2 is wastefully blocked waiting for the I/O operation to complete, so although the Thread 1 can process the first stream, the third stream can’t be processed before the blocking operation finishes.
        To overcome this problem, most reactive frameworks (such as RxJava and Akka) allow blocking operations to be executed by means of a separate dedicated thread pool. All the threads in the main pool are free to run uninterruptedly, keeping all the cores of the CPU at the highest possible use rate. Keeping separate thread pools for CPU bound and I/O-bound operations has the further benefit of allowing you to size and configure the pools with a finer granularity and to monitor the performance of these two kinds of tasks more precisely.

        Reactive at system level.
        A reactive system is a software architecture that allows multiple applications to work as a single coherent, resilient platform and also allows these applications to be sufficiently decoupled so when one of them fails, it doesn’t bring down the whole system. The main difference between reactive applications and systems is that the former type usually perform computations based on ephemeral streams of data and are called event-driven. The latter type are intended to compose the applications and facilitate communication. Systems with this property are often referred to as being message-driven.
        The other important distinction between messages and events is the fact that messages are directed toward a defined single destination, whereas events are facts that will be received by the components that are registered to observe them. In reactive systems, it’s also essential for these messages to be asynchronous to keep the sending and the receiving operations decoupled from the sender and receiver, respectively. This decoupling is a requirement for full isolation between components and is fundamental for keeping the system responsive under both failures (resilience) and heavy load (elasticity).
        More precisely, resilience is achieved in reactive architectures by isolating failures in the components where they happen to prevent the malfunctions from being propagated to adjacent components and from there in a catastrophic cascade to the rest of the system. Resilience in this reactive sense is more than fault-tolerance. The system doesn’t gracefully degrade but fully recovers from failures by isolating them and bringing the system back to a healthy state. This "magic" is obtained by containing the errors and reifying them as messages that are sent to other components acting as supervisors. In this way, the management of the problem can be performed from a safe context external to the failing component itself.
        As isolation and decoupling are key for resilience, the main enabler for elasticity is location transparency, which allows any component of a reactive system to communicate with any other service, regardless of where the recipient resides. Location transparency in turn allows the system to replicate and (automatically) scale any application depending on the current workload. Such location-agnostic scaling shows another difference between reactive applications (asynchronous and concurrent and decoupled in time) and reactive systems (which can become decoupled in space through location transparency).


        Reactive streams and the Flow API. PubSub protocol.
        Reactive programming is programming that uses reactive streams. Reactive streams are a standardized technique (based on the publish-subscribe, or pub-sub protocol) to process potentially unbounded streams of data asynchronously, in sequence and with mandatory nonblocking backpressure. Backpressure is a flowcontrol mechanism used in publish-subscribe to prevent a slow consumer of the events in the stream from being overwhelmed by one or more faster producers. When this situation occurs, it’s unacceptable for the component under stress to fail catastrophically or to drop events in an uncontrolled fashion. The component needs a way to ask the upstream producers to slow down or to tell them how many events it can accept and process at a given time before receiving more data.
        It’s worth noting that the requirement for built-in backpressure is justified by the asynchronous nature of the stream processing. In fact, when synchronous invocations are being performed, the system is implicitly backpressured by the blocking APIs. Unfortunately, this situation prevents you from executing any other useful task until the blocking operation is complete, so you end up wasting a lot of resources by waiting. Conversely, with asynchronous APIs you can maximize the use rate of your hardware, but run the risk of overwhelming some other slower downstream component. Backpressure or flow-control mechanisms come into play in this situation; they establish a protocol that prevents data recipients from being overwhelmed without having to block any threads.
        These requirements and the behavior that they imply were condensed in the Reactive Streams project (www.reactive-streams.org), which involved engineers from Netflix, Red Hat, Twitter, Lightbend, and other companies, and produced the definition of four interrelated interfaces representing the minimal set of features that any Reactive Streams implementation has to provide. These interfaces are part of Java 9, nested within the java.util.concurrent.Flow class, and implemented by many third-party libraries, including Akka Streams (Lightbend), Reactor (Pivotal), RxJava (Netflix), and Vert.x (Red Hat).

        The Flow class can’t be instantiated and contains only static components (among others four nested interfaces): Publisher, Subscriber, Subscription, Processor.
        The Flow class allows interrelated interfaces and static methods to establish flowcontrolled components, in which Publishers produce items consumed by one or more Subscribers, each managed by a Subscription. The Publisher is a provider of a potentially unbounded number of sequenced events, but it’s constrained by the backpressure mechanism to produce them according to the demand received from its Subscriber(s).
            @FunctionalInterface
            public interface Publisher<T> {
                void subscribe(Subscriber<? super T> s);
            }
            public interface Subscriber<T> {
                void onSubscribe(Subscription s);
                void onNext(T t);
                void onError(Throwable t);
                void onComplete();
            }
        Any events have to be published (and the corresponding methods invoked) strictly following the sequence defined by this protocol: [onSubscribe onNext* (onError | onComplete)?]. This notation means that onSubscribe is always invoked as the first event, followed by an arbitrary number of onNext signals. The stream of events can go on forever, or it can be terminated by an onComplete callback to signify that no more elements will be produced or by an onError if the Publisher experiences a failure.
        When a Subscriber registers itself on a Publisher, the Publisher’s first action is to invoke the onSubscribe method to pass back a Subscription object. The Subscription interface declares two methods. The Subscriber can use the first method to notify the Publisher that it’s ready to process a given number of events; the second method allows it to cancel the Subscription, thereby telling the Publisher that it’s no longer interested in receiving its events.
            public interface Subscription {
                void request(long n);
                void cancel();
            }
        The Flow specification defines a set of rules through which the implementations of these interfaces should cooperate. These rules can be summarized as follows:
          - The Publisher must send the Subscriber a number of elements no greater than that specified by the Subscription’s request method. A Publisher, however, may send fewer onNext than requested and terminate the Subscription by calling onComplete if the operation terminated successfully or onError if it failed. In these cases, when a terminal state has been reached (onComplete or onError), the Publisher can’t send any other signal to its Subscribers, and the Subscription has to be considered to be canceled.
          - The Subscriber must notify the Publisher that it’s ready to receive and process n elements. In this way, the Subscriber exercises backpressure on the Publisher preventing the Subscriber from being overwhelmed by too many events to manage. Moreover, when processing the onComplete or onError signals, the Subscriber isn’t allowed to call any method on the Publisher or Subscription and must consider the Subscription to be canceled. Finally, the Subscriber must be prepared to receive these terminal signals even without any preceding call of the Subscription.request() method and to receive one or more onNext even after having called Subscription.cancel().
          - The Subscription is shared by exactly one Publisher and Subscriber and represents the unique relationship between them. For this reason, it must allow the Subscriber to call its request method synchronously from both the onSubscribe and onNext methods. The standard specifies that the implementation of the Subscription.cancel() method has to be idempotent (calling it repeatedly has the same effect as calling it once) and thread-safe so that, after the first time it has been called, any other additional invocation on the Subscription has no effect. Invoking this method asks the Publisher to eventually drop any references to the corresponding Subscriber. Resubscribing with the same Subscriber object is discouraged, but the specification doesn’t mandate an exception being raised in this situation because all previously canceled subscriptions would have to be stored indefinitely.
        The Processor interface extends both Publisher and Subscriber without requiring any additional method.
            public interface Processor<T, R> extends Subscriber<T>, Publisher<R> { }
        In fact, this interface represents a transformation stage of the events processed through the reactive stream. When receiving an error, the Processor can choose to recover from it (and then consider the Subscription to be canceled) or immediately propagate the onError signal to its Subscriber(s). The Processor should also cancel its upstream Subscription when its last Subscriber cancels its Subscription to propagate the cancellation signal (even though this cancellation isn’t strictly required by the specification).
        The Flow API / Reactive Streams API mandates that any implementation of all the methods of the Subscriber interface should never block the Publisher, but it doesn’t specify whether these methods should process the events synchronously or asynchronously. Note, however, that all methods defined by these interfaces return void so that they can be implemented in a completely asynchronous way.
        */

        /*
         Here is a simple temperature reporting program using reactive principles. This program has two components:
           - TempInfo, which mimics a remote thermometer (constantly reporting randomly chosen temperatures between 0 and 99 degrees Fahrenheit.
           - TempSubscriber, which listens to these reports and prints the stream of temperatures reported by a sensor installed in a given city.
        */
        public static class TempInfo {
            public static final Random random = new Random();
            private final String town;
            private final int temp;

            public TempInfo(String town, int temp) {
                this.town = town;
                this.temp = temp;
            }
            // TempInfo instance for a given town is created via a static factory method.
            public static TempInfo fetch(String town) {
                // Randomly fail one time out of ten.
                if (random.nextInt(10) == 0) { throw new RuntimeException("Error!"); }
                return new TempInfo(town, random.nextInt(100));
            }
            @Override
            public String toString() { return town + " : " + temp; }
            public int getTemp() { return temp; }
            public String getTown() { return town; }
        }
        // Subscription for the temperatures of a given town that sends a temperature report whenever this report is requested by its Subscriber.
        public static class TempSubscription implements Subscription {
            private static final ExecutorService executor = Executors.newSingleThreadExecutor();
            private final Subscriber<? super TempInfo> subscriber;
            private final String town;

            public TempSubscription(Subscriber<? super TempInfo> subscriber, String town) {
                this.subscriber = subscriber;
                this.town = town;
            }
            // Request made by the Subscriber.
            @Override
            public void request(long n) {
                // Every time the TempSubscriber receives a new element into its onNext method, it sends a new request to the TempSubscription, and then the request method sends another element to the TempSubscriber itself. These recursive invocations are pushed onto the stack one after the other until the stack overflows, generating StackOverflowError. One possible solution is to add an Executor to the TempSubscription and then use it to send new elements to the TempSubscriber from a different thread.
                executor.submit(() -> {
                    for (long i = 0L; i < n; i++) {
                        // Sends the current temperature to the Subscriber.
                        try { subscriber.onNext(TempInfo.fetch(town)); }
                        catch (Exception e) { subscriber.onError(e); break; }
                    }
                });
            }
            @Override
            public void cancel() { subscriber.onComplete(); }
        }
        // Subscriber that, every time it gets a new element, prints the temperatures received from the Subscription and asks for a new report.
        public static class TempSubscriber implements Subscriber<TempInfo> {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }
            @Override
            public void onNext(TempInfo tempInfo) {
                System.out.println(tempInfo);
                subscription.request(1);
            }
            @Override
            public void onError(Throwable t) { System.err.println(t.getMessage()); }

            @Override
            public void onComplete() { System.out.println("Done!"); }
        }
        // The app.
        public class TempMonitorV1 {
            public static void testTempMonitorV1() {
                //{
                //    Subscriber subscriber = new TempSubscriber();
                //    Subscription subscription = new TempSubscription(subscriber, "New York");
                //    subscriber.onSubscribe(subscription);
                //}
                // or
                //{
                //    Publisher<TempInfo> publisher =
                //        subscriber -> subscriber.onSubscribe(
                //            new TempSubscription(subscriber, "New York")
                //        );
                //    publisher.subscribe(new TempSubscriber());
                //}
                // or
                //runMonitor(
                //    subscriber -> subscriber.onSubscribe(
                //        new TempSubscription(subscriber, "New York")
                //    ),
                //    new TempSubscriber()
                //);
                // or
                getTemperatures("New York").subscribe(new TempSubscriber());
            }
            // Returns a lambda expression that takes a Subscriber as an argument and invokes its onSubscribe method, passing to it a new TempSubscription instance. Because the signature of this lambda is identical to the only abstract method of the Publisher functional interface [public void subscribe(Subscriber<? super T> subscriber)], the Java compiler can automatically convert the lambda to a Publisher. The main method creates a Publisher for the temperatures in New York and then subscribes a new instance of the TempSubscriber class to it.
            private static Publisher<TempInfo> getTemperatures(String town) {
                return subscriber -> subscriber.onSubscribe(new TempSubscription(subscriber, town));
            }
            //private static void runMonitor(
            //    Publisher<TempInfo> publisher,
            //    TempSubscriber subscriber
            //) {
            //    publisher.subscribe(subscriber);
            //}
        }

        // Transforming data with a Processor.
        // A Processor is both a Subscriber and a Publisher. In fact, it’s intended to subscribe to a Publisher and republish the data that it receives after transforming that data.
        public static class TempProcessor implements Processor<TempInfo, TempInfo> {
            private Subscriber<? super TempInfo> subscriber;

            @Override
            public void subscribe(Subscriber<? super TempInfo> subscriber) {
                this.subscriber = subscriber;
            }
            @Override
            public void onNext(TempInfo temp) {
                subscriber.onNext(
                    // Converts Fahrenheit to Celsius.
                    new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9)
                );
            }
            @Override
            public void onSubscribe(Subscription subscription) {
                subscriber.onSubscribe(subscription);
            }
            @Override
            public void onError(Throwable throwable) { subscriber.onError(throwable); }
            @Override
            public void onComplete() { subscriber.onComplete(); }
            // Note that the only method of the TempProcessor that contains some business logic is onNext, which republishes temperatures after converting them from Fahrenheit to Celsius. All other methods that implement the Subscriber interface merely pass on unchanged (delegate) all received signals to the upstream Subscriber, and the Publisher’s subscribe method registers the upstream Subscriber into the Processor.
        }
        public class TempMonitorV2 {
            public static void testTempMonitorV2() {
                getCelsiusTemperatures("New York").subscribe(new TempSubscriber());
            }
            public static Publisher<TempInfo> getCelsiusTemperatures(String town) {
                return subscriber -> {
                    TempProcessor processor = new TempProcessor();
                    processor.subscribe(subscriber);
                    processor.onSubscribe(new TempSubscription(processor, town));
                };
            }
        }

        // Why doesn’t Java provide an implementation of the Flow API? And why it's so weird looking piece of Java. The answer is historic: there were multiple Java code libraries of reactive streams (such as Akka and RxJava). Originally, these libraries were developed separately, and although they implemented reactive programming via publish-subscribe ideas, they used different nomenclature and APIs. During the standardization process of Java 9, these libraries evolved so that their classes formally implemented the interfaces in java.util.concurrent.Flow, as opposed to merely implementing the reactive concepts. This standard enables more collaboration among different libraries.


        // PubSub using the reactive library RxJava.
        public static class TempObserver implements Observer<TempInfo> {
            @Override
            public void onComplete() { System.out.println("Done!"); }
            @Override
            public void onError(Throwable throwable) { System.out.println("Got problem: " + throwable.getMessage()); }
            @Override
            public void onSubscribe(Disposable disposable) {}
            @Override
            public void onNext(TempInfo tempInfo) { System.out.println(tempInfo); }
        }

        public static class TempObservable {
            public static Observable<TempInfo> getTemperature(String town) {
                // Creates an Observable from a function consuming an Observer (here the emitter).
                return Observable.create(
                    // An Observable emitting an infinite sequence of ascending longs, one per second.
                    emitter -> Observable.interval(1, TimeUnit.SECONDS)
                        .subscribe(i -> {
                            // Do something only if the consumed observer hasn’t been disposed yet (for a former error).
                            if (!emitter.isDisposed()) {
                                // If the temperature has been already emitted five times, completes the observer terminating the stream.
                                if (i >= 5) { emitter.onComplete(); }
                                else {
                                    // Otherwise, sends a temperature report to the Observer.
                                    try { emitter.onNext(TempInfo.fetch(town)); }
                                    catch (Exception e) { emitter.onError(e); }
                                }
                            }
                        })
                );
            }
        }
        public class RxJavaTempMonitorV1 {
            public static void rxJavaTempMonitorV1() {
                Observable<TempInfo> observable = TempObservable.getTemperature("New York");
                observable.subscribe(new TempObserver());
                // Observable emits one event per second and on receipt of this message, the Subscriber fetches the temperature in New York and prints it. Since this statement is executed in a main thread, you see nothing because the Observable publishing one event per second is executed in a thread that belongs to RxJava’s computation thread pool, which is made up of daemon threads and your main program terminates immediately and, in doing so, kills the daemon thread before it can produce any output.
                //As a bit of a hack, you can prevent this immediate termination by putting the main thread sleep after the preceding statement. Better, you could use the blockingSubscribe method that calls the callbacks on the current thread (in this case, the main thread).
                try { Thread.sleep(10000L); }
                catch (InterruptedException e) { throw new RuntimeException(e); }
            }
        }

        // RxJava. Transforming and combining Observables.
        // One of the main advantages of RxJava and other reactive libraries in working with reactive streams, compared with what’s offered by the native Flow API, is that they provide a rich toolbox of functions to combine, create, and filter any of those streams. You can also filter a stream to get another one that has only the elements you’re interested in, transform those elements with a given mapping function (both these things can be achieved with Flow.Processor), or even merge or combine two streams in many ways (which can’t be achieved with Flow.Processor).
        //These transforming and combining functions can be quite sophisticated, to the
        //point that explaining their behavior in plain words may result in awkward, convoluted
        //sentences. To get an idea, see how RxJava documents its mergeDelayError function:

        public static class TempObservableV2 extends TempObservable {
            public static Observable<TempInfo> getCelsiusTemperature(String town) {
                return getTemperature(town)
                    .map(temp -> new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9));
            }
            public static Observable<TempInfo> getNegativeTemperature(String town) {
                return getCelsiusTemperature(town).filter(temp -> temp.getTemp() < 0);
            }
            public static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
            // The varargs is converted to a stream of String; then each String is passed to the getCelsiusTemperature method. This way, each town is transformed into an Observable emitting the temperature of that town each second. Finally, the stream of Observables is collected into a list, and the list is passed to the merge static factory method provided by the Observable class itself. This method takes an Iterable of Observables and combines their output so that they act like a single Observable. In other words, the resulting Observable emits all the events published by all the Observables contained in the passed Iterable, preserving their temporal order.
                return Observable.merge(Arrays.stream(towns)
                    .map(TempObservableV2::getCelsiusTemperature)
                    .collect(toList()));
            }
        }
        public class RxJavaTempMonitorV2 {
            public static void rxJavaTempMonitorV2() {
                Observable<TempInfo> observable = TempObservableV2.getCelsiusTemperatures("New York", "Chicago", "San Francisco");
                //observable.subscribe(new TempObserver());
                //try { Thread.sleep(10000L); }
                //catch (InterruptedException e) { throw new RuntimeException(e); }
                observable.blockingSubscribe(new TempObserver());
            }
        }


        // PubSub using the reactive library Akka.
        public class AkkaTempMonitorV1 {
            private static Publisher<TempInfo> getTemperatures(String town) {
                return subscriber -> subscriber.onSubscribe(new TempSubscription(subscriber, town));
            }
            public static Publisher<TempInfo> getCelsiusTemperatures(String town) {
                return subscriber -> {
                    TempProcessor processor = new TempProcessor();
                    processor.subscribe(subscriber);
                    processor.onSubscribe(new TempSubscription(processor, town));
                };
            }
            public static void akkaTempMonitorV1() {
                ActorSystem system = ActorSystem.create("temp-info");
                Materializer materializer = ActorMaterializer.create(system);

                Publisher<TempInfo> publisher =
                    Source.fromPublisher(getTemperatures("New York"))
                        .runWith(Sink.asPublisher(AsPublisher.WITH_FANOUT), materializer);
                publisher.subscribe(new TempSubscriber());

                try { Thread.sleep(10000L); }
                catch (InterruptedException e) { throw new RuntimeException(e); }
            }
        }
    }
}