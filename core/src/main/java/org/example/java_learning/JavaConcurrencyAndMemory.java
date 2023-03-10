package org.example.java_learning;

import java.util.LinkedList;

public class JavaConcurrencyAndMemory {
/*
Java Memory {
In Java, the memory occupied by an object is automatically reclaimed when the object is no longer needed. This is done through a process known as garbage collection (or automatic memory management). Garbage collection is a technique that has been around for years in languages such as Lisp. In other languages such as C and C++ you must call the free() function or the delete operator to reclaim memory, some languages, for example Rust, perform such operations automatically when objects get out of scope.
Different VM implementations handle garbage collection in different ways, and the specifications do not impose very stringent restrictions on how GC must be implemented.

Memory Leaks in Java.
The fact that Java supports garbage collection dramatically reduces the incidence of memory leaks. A memory leak occurs when memory is allocated and never reclaimed. At first glance, it might seem that garbage collection prevents all memory leaks because it reclaims all unused objects.
A memory leak can still occur in Java, however, if a valid (but unused) reference to an unused object is left hanging around. For example, when a method runs for a long time (or forever), the local variables in that method can retain object references much longer than they are actually required. The following code illustrates:
    public static void main(String args[]) {
        int bigArray[] = new int[100000];
            // Do some computations with bigArray and get a result.
        int result = compute(bigArray);
            // We no longer need bigArray. It will get garbage collected when there are no more references to it. Because bigArray is a local variable, it refers to the array until this method returns. But this method doesn't return. So we've got to explicitly get rid of the reference ourselves, so the garbage collector knows it can reclaim the array.
        bigArray = null;
            // Loop forever, handling the user's input
        for(;;) handle_input(result);
    }
Memory leaks can also occur when you use a HashMap or similar data structure to associate one object with another. Even when neither object is required anymore, the association remains in the hash table, preventing the objects from being reclaimed until the hash table itself is reclaimed. If the hash table has a substantially longer lifetime than the objects it holds, this can cause memory leaks.
}


Java’s Support for Concurrency {
For mainstream implementations of the Java platform, every time we call Thread::start() this call is delegated to the operating system, and a new OS thread is created. This new OS thread exec()’s a new copy of the JVM bytecode interpreter. The interpreter starts executing at the run() method (or, equivalently, at the body of the lambda).
This means that application threads have their access to the CPU controlled by the operating system scheduler—a built-in part of the OS that is responsible for managing timeslices of processor time (and that will not allow an application thread to exceed its allocated time).
In more recent versions of Java, an increasing trend toward runtime-managed concurrency has appeared. This is the idea that for many purposes it’s not desirable for developers to explicitly manage threads. Instead, the runtime should provide "fire and forget" capabilities, whereby the program specifies what needs to be done, but the low-level details of how this is to be accomplished are left to the runtime.
This viewpoint can be seen in the concurrency toolkit contained in java.util.concurrent, a full discussion of which is a scope of this book: "Java Concurrency in Practice" by Brian Goetz.

Low-level view of concurrency {
Thread Lifecycle.
Every operating system has a view of threads that can differ in the details (but in most cases is broadly similar at a high level). Java tries hard to abstract these details away, and has an enum called Thread.State, which wrappers over the operating system’s view of the thread’s state. The values of Thread.State provide an overview of the lifecycle of a thread:
    NEW. The thread has been created, but its start() method has not yet been called. All threads start in this state.
    RUNNABLE. The thread is running or is available to run when the operating system schedules it.
    BLOCKED. The thread is not running because it is waiting to acquire a lock so that it can enter a synchronized method or block. We’ll see more about synchronized methods and blocks later in this section.
    WAITING. The thread is not running because it has called Object.wait() or Thread.join().
    TIMED_WAITING. The thread is not running because it has called Thread.sleep() or has called Object.wait() or Thread.join() with a timeout value.
    TERMINATED. The thread has completed execution. Its run() method has exited normally or by throwing an exception.

Visibility and Mutability.
In mainstream Java implementations, all Java application threads in a process have their own call stacks (and local variables) but share a single heap. This makes it very easy to share objects between threads, as all that is required is to pass a reference from one thread to another. This leads to a general design principle of Java—that objects are visible by default. If I have a reference to an object, I can copy it and hand it off to another thread with no restrictions. A Java reference is essentially a typed pointer to a location in memory—and threads share the same address space, so visible by default is a natural model.
In addition to visible by default, Java has another property that is important to fully understand concurrency, which is that objects are mutable—the contents of an object instance’s fields can usually be changed. We can make individual variables or references constant by using the final keyword, but this does not apply to the contents of the object.

Concurrent safety.
A safe multithreaded program is one in which it is impossible for any object to be seen in an illegal or inconsistent state by any another object, no matter what methods are called, and no matter in what order the application threads are scheduled by the operating system.
For most mainstream cases, the operating system will schedule threads to run on particular processor cores at seemingly random times, depending on load and what else is running in the system. If load is high, then there may be other processes that also need to run. The operating system will forcibly remove a Java thread from a CPU core if it needs to. The thread is suspended immediately, no matter what it’s doing—including being partway through a method. However, a method can temporarily put an object into an illegal state while it is working on it, providing it corrects it before the method exits. This means that if a thread is swapped off before it has completed a long-running method, it may leave an object in an inconsistent state, even if the program follows the safety rules. Another way of saying this is that even data types that have been correctly modeled for the single-threaded case still need to protect against the effects of concurrency. Code that adds on this extra layer of protection is called concurrently safe, or (more informally) threadsafe.

Exclusion and Protecting State.
Any code that modifies or reads state that can become inconsistent must be protected. To achieve this, the Java platform provides only one mechanism: exclusion. Consider a method that contains a sequence of operations that, if interrupted partway through, could leave an object in an inconsistent or illegal state. If this illegal state was visible to another object, incorrect code behavior could occur. To allow the developer to make code like this concurrently safe, Java provides the synchronized keyword. This keyword can be applied to a block or to a method, and when it is used, the platform uses it to restrict access to the code inside the block or method. The Java platform keeps track of a special token, called a monitor, for every object that it ever creates. These monitors (also called locks) are used by synchronized to indicate that the following code could temporarily render the object inconsistent.
The sequence of events for a synchronized block or method is:
  1. Thread needs to modify an object and may make it briefly inconsistent as an intermediate step.
  2. Thread acquires the monitor, indicating it requires temporary exclusive access to the object.
  3. Thread modifies the object, leaving it in a consistent, legal state when done.
  4. Thread releases the monitor.
If another thread attempts to acquire the lock while the object is being modified, then the attempt to acquire the lock blocks, until the holding thread releases the lock.
Note that you do not have to use the synchronized statement unless your program creates multiple threads that share data. If only one thread ever accesses a data structure, there is no need to protect it with synchronized.
One point is of critical importance — acquiring the monitor does not prevent access to the object. It only prevents any other thread from claiming the lock. Correct concurrently safe code requires developers to ensure that all accesses that might modify or read potentially inconsistent state acquire the object monitor before operating on or reading that state. Put another way, if a synchronized method is working on an object and has placed it into an illegal state, and another method (which is not synchronized) reads from the object, it can still see the inconsistent state.
The reason to use the word synchronized as the keyword for "requires temporary exclusive access" is that in addition to acquiring the monitor, the JVM also rereads the current state of the object from main memory when the block is entered. Similarly, when the synchronized block or method is exited, the JVM flushes any modified state of the object back to main memory. Without synchronization, different CPU cores in the system may not see the same view of memory, and memory inconsistencies can damage the state of a running program.

Volatile keyword.
This is the volatile keyword, and it indicates that before being used by application code, the value of the field or variable must be reread from main memory. Equally, after a volatile value has been modified, as soon as the write to the variable has completed, it must be written back to main memory.

Useful Methods of Thread.
  1. getId().
  2. getPriority() and setPriority().
  3. setName() and getName().
  4. getState().
  5. isAlive() .
  6. start(). This method is used to create a new application thread, and to schedule it, with the run() method being the entry point for execution. A thread terminates normally when it reaches the end of its run() method or when it executes a return statement in that method.
  7. interrupt(). If a thread is blocked in a sleep(), wait(), or join() call, then calling interrupt() on the Thread object that represents the thread will cause the thread to be sent an InterruptedException (and to wake up). If the thread was involved in interruptible I/O, then the I/O will be terminated and the thread will receive a ClosedByInterruptException. The interrupt status of the thread will be set to true, even if the thread was not engaged in any activity that could be interrupted.
  8. join().
  9. setDaemon().
  10. setUncaughtExceptionHandler(). This can be useful in some situations—for example, if one thread is supervising a group of other worker threads, then this pattern can be used to restart any threads that die. There is also setDefaultUncaughtExceptionHandler(), a static method that sets a backup handler for catching any thread’s uncaught exceptions.
Deprecated Methods of Thread.
In addition to the useful methods of Thread, there are a number of unsafe methods that you should not use. These methods form part of the original Java thread API, but were quickly found to be not suitable for developer use. Unfortunately, due to Java’s backward compatibility requirements, it has not been possible to remove them from the API. Developers simply need to be aware of them, and to avoid using them under all circumstances:
  1. stop(). Is almost impossible to use correctly without violating concurrent safety, as stop() kills the thread immediately, without giving it any opportunity to recover objects to legal states. This is in direct opposition to principles such as concurrent safety, and so should never be used.
  2. suspend(), resume(), and countStackFrames(). The suspend() mechanism does not release any monitors it holds when it suspends, so any other thread that attempts to access those monitors will deadlock. In practice, this mechanism produces race conditions between these deadlocks and resume() that render this group of methods unusable.
  3. destroy(). This method was never implemented—it would have suffered from the same race condition issues as suspend() if it had been.
All of these deprecated methods should always be avoided. A set of safe alternative patterns that achieve the same intended aims as the preceding methods have been developed. A good example of one of these patterns is the run-until-shutdown pattern:
    private volatile boolean shutdown = false;
    public void shutdown() { shutdown = true; }
    public void run() { while (!shutdown) { // ... process another task  }}

Working with Threads.
In order to work effectively with multithreaded code, it’s important to have the basic facts about monitors and locks at your command. This checklist contains the main facts that you should know:
  - Synchronization is about protecting object state and memory, not code.
  - Synchronization is a cooperative mechanism between threads. One bug can break the cooperative model and have far-reaching consequences.
  - Acquiring a monitor only prevents other threads from acquiring the monitor - it does not protect the object.
  - Unsynchronized methods can see (and modify) inconsistent state, even while the object’s monitor is locked.
  - Locking an Object[] doesn’t lock the individual objects.
  - Primitives are not mutable, so they can’t (and don’t need to) be locked.
  - synchronized can’t appear on a method declaration in an interface.
  - Inner classes are just syntactic sugar, so locks on inner classes have no effect on the enclosing class (and vice versa).
  - Java’s locks are reentrant. This means that if a thread holding a monitor encounters a synchronized block for the same monitor, it can enter the block.

Threads can be asked to sleep for a period of time. It is also useful to go to sleep for an unspecified amount of time, and wait until a condition is met. In Java, this is handled by the wait() and notify() methods that are present on Object.
Just as every Java object has a lock associated with it, every object maintains a list of waiting threads. When a thread calls the wait() method of an object, any locks the thread holds are temporarily released, and the thread is added to the list of waiting threads for that object and stops running. When another thread calls the notifyAll() method of the same object, the object wakes up the waiting threads and allows them to continue running.
For example, let’s look at a simplified version of a queue that is safe for multithreaded use:
    //
    // One thread calls push() to put an object on the queue.
    // Another calls pop() to get an object off the queue. If there is no
    // data, pop() waits until there is some, using wait()/notify().
    //
    public class WaitingQueue<E> {
        LinkedList<E> q = new LinkedList<E>(); // storage
        public synchronized void push(E o) {
            q.add(o); // Append the object to the end of the list
            this.notifyAll(); // Tell waiting threads that data is ready
        }
        public synchronized E pop() {
            while (q.size() == 0) {
                try { this.wait(); }
                catch (InterruptedException ignore) {}
            }
            return q.remove();
        }
    }
!!In general, most developers shouldn’t roll their own classes like the one in this example—instead, make use of the libraries and components that the Java platform provides for you.
}
}
*/
}

