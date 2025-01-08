package org.example.java_learning;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.example.java_learning.Util.print;
import static org.example.java_learning.Util.println;

public class GenericsExample {
    public static void testGenericsExample() {
        testRelativelyPrimePredicate();
    }

    /**
     * Finds the maximal element in the range [begin, end) of a list.
     */
    public final class Algorithm {
        public static <T extends Object & Comparable<? super T>> T max(List<? extends T> list, int begin, int end) {
            T maxElem = list.get(begin);
            for (++begin; begin < end; ++begin) if (maxElem.compareTo(list.get(begin)) < 0) maxElem = list.get(begin);
            return maxElem;
        }

        public static <T> int findFirst(List<T> list, int begin, int end, Predicate<T> p) {
            for (; begin < end; ++begin) if (p.test(list.get(begin))) return begin;
            return -1;
        }

        // x > 0 and y > 0
        public static int gcd(int x, int y) {
            for (int r; (r = x % y) != 0; x = y, y = r) { }
            return y;
        }
    }

    /**
     * Find the first integer in a list that is relatively prime to a list of specified integers.
     */
    static class RelativelyPrimePredicate implements Predicate<Integer> {
        private Collection<Integer> c;
        public RelativelyPrimePredicate(Collection<Integer> c) { this.c = c; }
        public boolean test(Integer x) {
            for (Integer i : c) if (Algorithm.gcd(x, i) != 1) return false;
            return c.size() > 0;
        }
    }
    static void testRelativelyPrimePredicate() {
        List<Integer> li = Arrays.asList(3, 4, 6, 8, 11, 15, 28, 32);
        Collection<Integer> c = Arrays.asList(7, 18, 19, 25);
        Predicate<Integer> p = new RelativelyPrimePredicate(c);
        int i = Algorithm.findFirst(li, 0, li.size(), p);
        if (i != -1) {
            print(li.get(i) + " is relatively prime to ");
            for (Integer k : c) print(k + " ");
            println("");
        }
    }

    /**
     * Clones object.
     */
    class Wrapper<T> {
        private T wrapped ;
        public Wrapper (T arg) {wrapped = arg;}
        public Wrapper <T> clone() {
            Wrapper<T> clon = null;
            try { clon = (Wrapper<T>) super.clone(); } // Unchecked warning: "found: 'Object'; required: 'Wrapper <T>'".
            catch (CloneNotSupportedException e) { throw new InternalError(); }
            try {
                Class<?> clzz = this.wrapped.getClass();
                Method   meth = clzz.getMethod("clone", new Class[0]);
                Object   dupl = meth.invoke(this.wrapped, new Object[0]);
                clon.wrapped = (T) dupl; // Unchecked warning: "found: 'Object'; required: 'T'".
            } catch (Exception e) {}
            return clon;
        }
    }
}
