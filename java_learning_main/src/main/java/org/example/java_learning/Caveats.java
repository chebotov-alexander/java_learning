package org.example.java_learning;

/**
 *
 */
public class Caveats {
    /*
    Generally, final fields may be initialized at any point during construction, and nothing prevents attempts to read those fields beforehand, revealing their pre-initialization values.
    Despite the claim that fields x and y are final, for a short window they are actually mutable. Were the constructor to share this with another thread, any code in that thread would be able to observe an identity-dependent, mutable object.
    */
    class LogIdentityInit {
        final int x;
        final int y;

        public int sum() { return x + y; }

        public LogIdentityInit() {
            this(1, 2);
        }

        public LogIdentityInit(int x, int y) {
            System.out.println(sum()); // 0
            this.x = x;
            System.out.println(sum()); // 1
            this.y = y;
            System.out.println(sum()); // 3
        }
    }
    /*
    Value objects cannot support this sort of behavior, and need to be more carefully constructed. Specifically, value class constructors must set all of the class's instance fields in the earliest stages of construction, before the super() call. At this stage, the object is not yet fully-formed, its instance fields can't be read, and this references are illegal.
    See:
     JEP 447: Statements before super(...) (Preview) https://openjdk.org/jeps/447
     https://openjdk.org/jeps/401 https://openjdk.org/jeps/401
    */
}
