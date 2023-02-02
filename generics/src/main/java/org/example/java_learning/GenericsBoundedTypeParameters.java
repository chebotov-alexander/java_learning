package org.example.java_learning;

/**
 * Bounded Type Parameters - Type restriction
 */
public class GenericsBoundedTypeParameters {
    public class UnBoundedBox<T> {
        protected T value;
        public void box(T t) { value = t; }
        public T unbox() {
            T t = value;
            value = null;
            return t;
        }
    }
    // If we want to have a restricted form of box that only holds numbers, Java allows us to achieve this by using a bound on the type parameter.
    public class NumberBoundedBox<T extends Number> extends UnBoundedBox<T> {
        // Notice that because the value field has protected access, it can be accessed directly in the subclass.
        public int intValue() { return value.intValue(); }
    }
    // As a result of this, the compiler knows that value will definitely have a method intValue() available on it (Number has one).
    // Won't compile
    //NumberBoundedBox<Object> no = new NumberBoundedBox<>();
    // This will
    NumberBoundedBox<Integer> no = new NumberBoundedBox<>();

    // Don't use it or use it with great caution - runtime exception (java.lang.ClassCastException):
    // Compiles
    public void genericsBoundedTypeParametersCouldButDont() {
        NumberBoundedBox numberBoundedBox = new NumberBoundedBox();
        // This is very dangerous
        numberBoundedBox.box(new Object());
        // Runtime error
        System.out.println(numberBoundedBox.intValue());
    }
    // In general, type bounds can be used to write better generic code and libraries. With practice, some fairly complex constructions can be built.
    // The definition might seem daunting, but the ComparingBox is really just a UnBoundedBox that contains a Comparable value. The type also extends the comparison operation to the ComparingBox type itself, by just comparing the contents of the two boxes.
    public class ComparingBox<T extends Comparable<T>> extends UnBoundedBox<T> implements Comparable<ComparingBox<T>> {
        @Override
        public int compareTo(ComparingBox<T> o) {
            if (value == null) return o.value == null ? 0 : -1;
            return value.compareTo(o.value);
        }
    }

    // Note, it's possible to narrow classes class
    //ClassName<T extends ClassORInterface1 & Comparable<T> & Interface3> {...}
    // One can have as many interface supertypes as you like, but at most one of the bounds can be a class. If you	have a class as a bound, it must be the first one in the bounds list.
}
