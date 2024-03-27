package org.example.java_learning;

import static org.example.java_learning.Util.println;

/**
 * Project Amber - Pattern Matching for Java
 * Pattern Matching for instanceof
 * Pattern Matching for switch
 * Pattern Matching for Records
 */
public class PatternMatching {
/*
 Best practices {
  In conclusion: An exhaustive switch without a match-all clause is better than an exhaustive switch with one, when possible. If we code the switch to cover all the constants known at compile time, and omit the match-all clause, then we will find out about this change the next time we recompile the class containing the switch. A match-all clause risks sweeping exhaustiveness errors under the rug. The notion of exhaustiveness is designed to strike a balance between covering all reasonable cases while not forcing you to write possibly many rare corner cases that will pollute or even dominate your code for little actual value. Put another way: Exhaustiveness is a compile-time approximation of true run-time exhaustiveness.

 }
*/
    /**
     * Project Amber - Pattern Matching for Java
     */
    class PatternMatchingForJava {
    /*
    https://openjdk.org/projects/amber/design-notes/patterns/pattern-matching-for-java
    https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.30

    !This is an exploratory document (section) only and does not constitute a plan for any specific feature in any
    specific
    version of the Java Language. This document also may reference other features under exploration; this is purely for illustrative purposes, and does not constitute any sort of plan or committment to deliver any of these features.

    Pattern matching allows common logic in a program, namely the conditional extraction of components from objects, to be expressed more concisely and safely.

    Pattern matching documents:
     - Pattern Matching For Java (this document) — Overview of pattern matching concepts, and how they might be surfaced in Java. https://openjdk.org/projects/amber/design-notes/patterns/pattern-matching-for-java
     - !! Pattern Matching For Java—Semantics — More detailed notes on type checking, matching, and scoping of patterns and binding variables. https://openjdk.org/projects/amber/design-notes/patterns/pattern-match-semantics
     - Extending Switch for Patterns — An early exploration of the issues surrounding extending pattern matching to the switch statement. https://openjdk.org/projects/amber/design-notes/patterns/extending-switch-for-patterns
     - Type Patterns in Switch — A more up-to-date treatment of extending pattern matching to switch statements, including treatment of nullity and totality. https://openjdk.org/projects/amber/design-notes/patterns/type-patterns-in-switch
     - Pattern Matching in the Java Object model — Explores how patterns fit into the Java object model, how they fill a hole we may not have realized existed, and how they might affect API design going forward. Aggregation and destructuring. https://openjdk.org/projects/amber/design-notes/patterns/pattern-match-object-model
    */

    }

    /**
     * Pattern Matching for instanceof
     */
    class PatternMatchingForInstanceof {
    /*
    https://openjdk.org/jeps/305 Pattern Matching for instanceof (Preview)
    https://openjdk.org/jeps/375 Pattern Matching for instanceof (Second Preview)
    https://openjdk.org/jeps/394 Pattern Matching for instanceof

    Motivation {
     Nearly every program includes some sort of logic that combines testing if an expression has a certain type or structure, and then conditionally extracting components of its state for further processing. Pattern matching allows the desired "shape" of an object to be expressed concisely (the pattern), and for various statements and expressions to test that "shape" against their input (the matching).
     This construction (instanceof-and-cast idiom):
        if (obj instanceof String) {
            String s = (String) obj;    // grr...
            ...
        }
      - is tedious - doing both the type test and cast should be unnecessary;
      - obfuscates the more significant logic that follows because of three occurrences of the type String;
      - provides opportunities for errors for same reason.
    }

    Description {
     A pattern is a combination of a predicate, or test, that can be applied to a target, and a set of local variables, known as pattern variables, that are extracted from the target only if the predicate successfully applies to it.
     A type pattern consists of a predicate that specifies a type, along with a single pattern variable.

     The instanceof operator is extended to take a type pattern instead of just a type and perform pattern matching.
        if (obj instanceof String s) {
            // Let pattern matching do the work!
            ...
        }
     The conditionality of pattern matching — if a value does not match a pattern, then the pattern variable is not assigned a value — means that we have to consider carefully the scope of the pattern variable. Rather than using a coarse approximation for the scope of pattern variables, pattern variables instead use the concept of flow scoping. A pattern variable is only in scope where the compiler can deduce that the pattern has definitely matched and the variable will have been assigned a value. This analysis is flow sensitive and works in a similar way to existing flow analyses such as definite assignment.
        if (a instanceof Point p) {
            // p is in scope
        }
        // p not in scope here
        if (b instanceof Point p) {
            // Sure!
        }
     When the conditional expression of the if statement grows more complicated than a single instanceof, the scope of the pattern variable grows accordingly.
        if (obj instanceof String s && s.length() > 5) {}
        if (obj instanceof String s || s.length() > 5) {// Error!}
     So:
        */
        class Example0 {
            String s;
            Point p;
            //Before
            public final boolean equalsBefore0(Object o) {
                return (o instanceof CaseInsensitiveString)
                    && ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
            }
            //After
            public final boolean equalsAfter0(Object o) {
                return (o instanceof CaseInsensitiveString cis)
                    && cis.s.equalsIgnoreCase(s);
            }
            //Before
            public final boolean equalsBefore1(Object o) {
                if (!(o instanceof Point))
                    return false;
                Point other = (Point) o;
                return p.x == other.x && p.y == other.y;
            }
            //After
            public final boolean equalsAfter1(Object o) {
                return (o instanceof Point other) && p.x == other.x && p.y == other.y;
            }
        }
        /*
     The flow scoping analysis for pattern variables is sensitive to the notion of whether a statement can complete normally.
        public void onlyForStrings(Object o) throws MyException {
            if (!(o instanceof String s))
                throw new MyException();
            // s is in scope
            println(s);
            ...
        }

     Pattern variables are just a special case of local variables, and aside from the definition of their scope, in all other respects pattern variables are treated as local variables. In particular, this means that (1) they can be assigned to, and (2) they can shadow a field declaration.
        */
        class Example1 {
            String s;
            Point p;

            void test1(Object o) {
                if (o instanceof String s) {
                    println(s);      // Field s is shadowed
                    s = s + "\n";               // Assignment to pattern variable
                }
                println(s);          // Refers to field s
            }
            void test2(Object o) {
                if (o instanceof Point p) {
                    // p refers to the pattern variable
                } else {
                    // p refers to the field
                }
            }
        }
        /*
    }

    Alternatives {
     The benefits of type patterns could be obtained by flow typing in if statements, or by a type switch construct.
     Pattern matching generalizes both of these constructs.
    }
    */
    }

    /**
     * Pattern Matching for switch
     */
    public static class PatternMatchingForSwitch {
    /*
    https://openjdk.org/jeps/406 Pattern Matching for switch (Preview)
    https://openjdk.org/jeps/420 Pattern Matching for switch (Second Preview)
    https://openjdk.org/jeps/427 Pattern Matching for switch (Third Preview)
    https://openjdk.org/jeps/433 Pattern Matching for switch (Fourth Preview)
    https://openjdk.org/jeps/441 Pattern Matching for switch
    */
        SwitchStatementsExpressions switchStatement = new SwitchStatementsExpressions();

    /*
    Extending pattern matching to switch allows an expression to be tested against a number of patterns, each with a specific action, so that complex data-oriented queries can be expressed concisely and safely.

    Summary {
     Selector Expression Type {
      The type of a selector expression can either be an integral primitive type or any reference type, such as in the previous examples.
     }
     When Clauses {
      You can add a Boolean expression right after a pattern label with a when clause. This is called a guarded pattern label. The Boolean expression in the when clause is called a guard. A value matches a guarded pattern label if it matches the pattern and, if so, the guard also evaluates to true.
     }
     Pattern Label Dominance {
      Guarded patterns aren't checked for dominance because they're generally undecidable. Consequently, you should order your case labels so that constant labels appear first, followed by guarded pattern labels, and then followed by nonguarded pattern labels.
     }
     Type Coverage in switch Expressions and Statements {
      Switch expressions must be exhausted and statements do not.
     }
     Null case Labels {
      If a selector expression evaluates to null and the switch block does not have null case label, then a NullPointerException is thrown as normal.
     }
    }

    Dependencies {
     This JEP builds on Pattern Matching for instanceof (JEP 394), delivered in JDK 16, and also the enhancements offered by Switch Expressions (JEP 361). It has co-evolved with Record Patterns (JEP 440).
    }

    Goals {
     - Expand the expressiveness and applicability of switch expressions and statements by allowing patterns to appear in case labels.
     - Allow the historical null-hostility of switch to be relaxed when desired.
     - Increase the safety of switch statements by requiring that pattern switch statements cover all possible input values.
     - Ensure that all existing switch expressions and statements continue to compile with no changes and execute with identical semantics.
    }

    Motivation {
     We often want to compare a variable such as obj against multiple alternatives. Java supports multi-way comparisons with switch statements and, since Java 14, switch expressions (JEP 361), but unfortunately switch is very limited. We can only switch on values of a few types — integral primitive types (excluding long), their corresponding boxed forms, enum types, and String — and we can only test for exact equality against constants. We might like to use patterns to test the same variable against a number of possibilities, taking a specific action on each, but since the existing switch does not support that we end up with a chain of if...else tests such as:
        */
        // Prior to Java 21
        static String formatter(Object obj) {
            String formatted = "unknown";
            if (obj instanceof Integer i) { formatted = String.format("int %d", i); }
            else if (obj instanceof Long l) { formatted = String.format("long %d", l); }
            else if (obj instanceof Double d) { formatted = String.format("double %f", d); }
            else if (obj instanceof String s) { formatted = String.format("String %s", s); }
            return formatted;
        }
        /*
     If we extend switch statements and expressions to work on any type, and allow case labels with patterns rather than just constants, then we can rewrite the above code more clearly and reliably (the semantics of this switch are clear: A case label with a pattern applies if the value of the selector expression obj matches the pattern):
        */
        // As of Java 21
        static String formatterPatternSwitch(Object obj) {
            return switch (obj) {
                case Integer i -> String.format("int %d", i);
                case Long l    -> String.format("long %d", l);
                case Double d  -> String.format("double %f", d);
                case String s  -> String.format("String %s", s);
                default        -> obj.toString();
            };
        }
        /*

     Switches and null {
      Traditionally, switch statements and expressions throw NullPointerException if the selector expression evaluates to null, so testing for null must be done outside of the switch:
        */
        // Prior to Java 21
        static void testFooBarOld(String s) {
            if (s == null) {
                println("Oops!");
                return;
            }
            switch (s) {
                case "Foo", "Bar" -> println("Great");
                default           -> println("Ok");
            }
        }
        // As of Java 21
        static void testFooBarNew(String s) {
            switch (s) {
                case null         -> println("Oops");
                case "Foo", "Bar" -> println("Great");
                // To maintain backward compatibility with the current semantics of switch, the default label does not match a null selector.
                default           -> println("Ok");
            }
        }
        /*
     }

     Case refinement (guarded case label) {
      In contrast to case labels with constants, a pattern case label can apply to many values:
        */
        // As of Java 21
        static void testStringOld(String response) {
            switch (response) {
                case null -> { }
                case String s -> {
                    if (s.equalsIgnoreCase("YES")) println("You got it");
                    else if (s.equalsIgnoreCase("NO")) println("Shame");
                    else println("Sorry?");
                }
            }
        }
        /*
       // We would prefer to write multiple patterns but we then need some way to express a refinement to a pattern. We therefore allow when clauses in switch blocks to specify guards to pattern case labels, e.g., case String s when s.equalsIgnoreCase("YES"). We refer to such a case label as a guarded case label, and to the boolean expression as the guard.
        */
        // As of Java 21
        static void testStringNew(String response) {
            switch (response) {
                case null -> { }
                case String s
                when s.equalsIgnoreCase("YES") -> { println("You got it"); }
                case String s
                when s.equalsIgnoreCase("NO") -> { println("Shame"); }
                case String s -> { println("Sorry?"); }
            }
        }
        /*
        // We can further enhance this example with extra rules for other known constant strings:
        */
        // As of Java 21
        static void testStringEnhanced(String response) {
            switch (response) {
                case null -> { }
                case "y", "Y" -> { println("You got it"); }
                case "n", "N" -> { println("Shame"); }
                case String s
                when s.equalsIgnoreCase("YES") -> { println("You got it"); }
                case String s
                when s.equalsIgnoreCase("NO") -> { println("Shame"); }
                case String s -> { println("Sorry?"); }
            }
        }
        /*
     }

     Switches and enum constants {
      Prior to Java 21 the selector expression of the switch must be of the enum type, and the labels must be simple names of the enum's constants:
        */
        // Prior to Java 21
        public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
        static void testforHearts(Suit s) {
            switch (s) {
                case HEARTS -> println("It's a heart!");
                default -> println("Some other suit");
            }
        }
        /*
      Even after adding pattern labels, this constraint leads to unnecessarily verbose code:
        */
        // As of Java 21
        sealed interface CardClassification permits Standard, Tarot, Weird {}
        public enum Standard implements CardClassification { SPADE, HEART, DIAMOND, CLUB }
        public enum Tarot implements CardClassification { SPADE, HEART, DIAMOND, CLUB, TRUMP, EXCUSE }
        final class Weird implements CardClassification {}

        static void exhaustiveSwitchWithoutEnumSupport(CardClassification c) {
            // The type of the selector expression is an interface that's been implemented by two enum types. Because the type of the selector expression isn't an enum type, this switch expression uses guarded patterns instead:
            switch (c) {
                case Standard s when s == Standard.CLUB -> { println("It's clubs"); }
                case Standard s when s == Standard.DIAMOND -> { println("It's diamonds"); }
                case Standard s -> { println("It's Standard"); }
                case Tarot t when t == Tarot.HEART -> { println("It's hearts"); }
                case Tarot t -> { println("It's Tarot"); }
                case Weird w -> { println("It's Weird"); }
                default -> throw new IllegalStateException("Unexpected value: " + c);
            }
        }
        /*
        This code would be more readable if we could have a separate case for each enum constant rather than lots of guarded patterns. We therefore relax the requirement that the selector expression be of the enum type and we allow case constants to use qualified names of enum constants:
        */
        // As of Java 21
        static void exhaustiveSwitchWithBetterEnumSupport(CardClassification c) {
            switch (c) {
                case Standard.SPADE   -> println("Spades");
                case Standard.HEART   -> println("Hearts");
                case Standard.DIAMOND -> println("Diamonds");
                case Standard.CLUB    -> println("Clubs");
                case Tarot.SPADE      -> println("Spades or Piques");
                case Tarot.HEART      -> println("Hearts or C\u0153ur");
                case Tarot.DIAMOND    -> println("Diamonds or Carreaux");
                case Tarot.CLUB       -> println("Clubs or Trefles");
                case Tarot.TRUMP      -> println("Trumps or Atouts");
                case Tarot.EXCUSE     -> println("The Fool or L'Excuse");
                case Weird weird      -> { println("It's Weird"); }
            }
            // Therefore, you can use an enum constant when the type of the selector expression is not an enum type provided that the enum constant's name is qualified and its value is assignment-compatible with the type of the selector expression.
        }
        /*
     }

    }

    Description {
     At the moment (JEP 441) switch statements and expressions were enhanced in four ways:
      - Improve enum constant case labels,
      - Extend case labels to include patterns and null in addition to constants,
      - Broaden the range of types permitted for the selector expressions of both switch statements and switch expressions (along with the required richer analysis of exhaustiveness of switch blocks)
      - Allow optional when clauses to follow case labels.
     Improved enum constant case labels {
      To maintain compatibility with existing Java code, when switching over an enum type a case constant can still use the simple name of a constant of the enum type being switched over.
      For new code, we extend the treatment of enums. First, we allow qualified names of enum constants to appear as case constants. These qualified names can be used when switching over an enum type.
      Second, we drop the requirement that the selector expression be of an enum type when the name of one of that enum's constants is used as a case constant. In that situation we require the name to be qualified and its value to be assignment compatible with the type of the selector expression. (This aligns the treatment of enum case constants with the numerical case constants.)
      */
        // As of Java 21
        sealed interface Currency permits Coin {}
        enum Coin implements Currency { HEADS, TAILS }
        static void goodEnumSwitch1(Currency c) {
            switch (c) {
                // Qualified name of enum constant as a label
                case Coin.HEADS -> { println("Heads"); }
                // Error - TAILS must be qualified
                //case TAILS -> { println("Tails");}
                default -> { println("Some currency"); }
            }
        }
        static void goodEnumSwitch2(Coin c) {
            switch (c) {
                case HEADS -> { println("Heads"); }
                // Unnecessary qualification but allowed
                case Coin.TAILS -> { println("Tails"); }
            }
        }
      /*
     }

     Patterns in switch labels {
      Grammar for switch labels in a switch block before (compare JLS §14.11.1 https://docs.oracle.com/javase/specs/jls/se19/html/jls-14.html#jls-SwitchLabel):
        SwitchLabel:
        case CaseConstant {, CaseConstant}
        default
      Now it's:
        SwitchLabel:
          case CaseConstant { , CaseConstant }
          case null [, default]
          case Pattern [ Guard ]
          default
      The essence of switch is unchanged: The value of the selector expression is compared to the switch labels, one of the labels is selected, and the code associated with that label is executed or evaluated. The difference now is that for case labels with patterns, the label selected is determined by the result of pattern matching rather than by an equality test.
      After a successful pattern match we often further test the result of the match. We introduce guarded pattern case labels by allowing an optional guard, which is a boolean expression, to follow the pattern label. Only pattern labels can have guards. For example, it is not valid to write a label with a case constant and a guard; e.g., case "Hello" when callRandomBooleanExpression().
      */
        // Before
        static void testOld(Object obj) {
            switch (obj) {
                case String s:
                    if (s.length() == 1) {  }
                    else {  }
                    break;
                default: throw new IllegalStateException("Unexpected value: " + obj);
            }
        }
        // After
        // As of Java 21
        static void testNew(Object obj) {
            switch (obj) {
                case String s when s.length() == 1 -> {}
                case String s -> {}
                default -> throw new IllegalStateException("Unexpected value: " + obj);
            }
        }
      /*
     }


     There are five major language design areas to consider when supporting patterns in switch:
      - Enhanced type checking.
      - Exhaustiveness of switch expressions and statements.
      - Scope of pattern variable declarations.
      - Dealing with null.
      - Errors.

     Enhanced type checking {
      Selector expression typing {
       Supporting patterns in switch means that we can relax the restrictions on the type of the selector expression. Currently the type of the selector expression of a normal switch must be either an integral primitive type (excluding long), the corresponding boxed form (i.e., Character, Byte, Short, or Integer), String, or an enum type. We extend this and require that the type of the selector expression be either an integral primitive type (excluding long) or any reference type.
       */
        // As of Java 21
        static void typeTester(Object obj) {
            switch (obj) {
                case null     -> println("null");
                case String s -> println("String");
                case Color c  -> println("Color: " + c.toString());
                case Point p  -> println("Record class: " + p.toString());
                case int[] ia -> println("Array of ints of length" + ia.length);
                default       -> println("Something else");
            }
        }
       /*
      }
      Dominance of case labels {
       Supporting pattern case labels means that for a given value of the selector expression it is now possible for more than one case label to apply, whereas previously at most one case label could apply. For example, if the selector expression evaluates to a String then both the case labels case String s and case CharSequence cs would apply. The first case label appearing in a switch block that applies to a value is chosen.
       */
        // As of Java 21
        static void first(Object obj) {
            switch (obj) {
                //case CharSequence cs -> println("A sequence of length " + cs.length());
                // Error - pattern is dominated by previous pattern. The String case label is unreachable in the sense that there is no value of the selector expression that would cause it to be chosen (String is a subtype of CharSequence).
                //case String s -> println("A string: " + s);
                case String s -> println("A string: " + s);
                case CharSequence cs -> println("A sequence of length " + cs.length());
                default -> { break; }
            }
        }
       /*
       All of this suggests a simple, predictable, and readable ordering of case labels in which the constant case labels should appear before the guarded pattern case labels, and those should appear before the unguarded pattern case labels:
        // As of Java 21
        Integer i = ...
        switch (i) {
            case -1, 1 -> ...                   // Special cases
            case Integer j when j > 0 -> ...    // Positive integer cases
            case Integer j -> ...               // All the remaining integers
        }
       Guarded patterns aren't checked for dominance because they're generally undecidable. Consequently, you should order your case labels so that constant labels appear first, followed by guarded pattern labels, and then followed by nonguarded pattern labels.
       Supertype dominates subtype, for example, String is a subtype of CharSequence.
       An unguarded pattern case label dominates a guarded pattern case label that has the same pattern.
       A guarded pattern case label dominates another pattern case label (guarded or unguarded) only when both the former's pattern dominates the latter's pattern and when its guard is a constant expression of value true. For example, the guarded pattern case label case String s when true dominates the pattern case label case String s. We do not analyze the guarding expression any further in order to determine more precisely which values match the pattern label — a problem which is undecidable in general.
       A pattern case label can dominate a constant case label. For example, the pattern case label case Integer i dominates the constant case label case 42, and the pattern case label case E e dominates the constant case label case A when A is a member of enum class type E. A guarded pattern case label dominates a constant case label if the same pattern case label without the guard does. In other words, we do not check the guard, since this is undecidable in general. For example, the pattern case label case String s when s.length() > 1 dominates the constant case label case "hello", as expected; but case Integer i when i != 0 dominates the case label case 0.
       The match-all labels are default and pattern case labels where the pattern unconditionally matches the selector expression. For example, the type pattern String s unconditionally matches a selector expression of type String, and the type pattern Object o unconditionally matches a selector expression of any reference type:
       */
        // As of Java 21
        static void matchAll(String s) {
            switch(s) {
                case String t: println(t); break;
                // Duplicate unconditional pattern.
                //case Object o: println("An Object"); break;
                // 'switch' has both an unconditional pattern and a default label.
                //default: println("Something else");  // Error - dominated!
            }
        }
       /*
      }
     }

     Exhaustiveness of switch expressions and statements {
      Type coverage {
       A switch expression evaluates exactly one arm of the switch and requires that all possible values of the selector expression be handled in the switch block; in other words, it must be exhaustive. This maintains the property that successful evaluation of a switch expression always yields a value.
       For normal switch expressions, this property is enforced by a straightforward set of extra conditions on the switch block.
       For pattern switch expressions and statements, we achieve this by defining a notion of type coverage of switch labels in a switch block. The type coverage of all the switch labels in the switch block is then combined to determine if the switch block exhausts all the possibilities of the selector expression.
       Manually writing a default clause in this situation is not only irritating but actually pernicious, since the compiler can do a better job of checking exhaustiveness without one. (The same is true of any other match-all clause such as default, case null, default, or an unconditional type pattern.) If we omit the default clause then we will discover at compile time if we have forgotten a case label, rather than finding out at run time — and maybe not even then.
        enum Color { RED, YELLOW, GREEN }
        int numLetters = switch (color) {
            case RED -> 3;
            case GREEN -> 5;
            case YELLOW -> 6;
            default -> throw new ArghThisIsIrritatingException(color.toString());
        }
       More importantly, what happens if someone later adds another constant to the Color enum? If we have an explicit match-all clause then we will only discover the new constant value if it shows up at run time. But if we code the switch to cover all the constants known at compile time, and omit the match-all clause, then we will find out about this change the next time we recompile the class containing the switch. A match-all clause risks sweeping exhaustiveness errors under the rug. Looking to run time, what happens if a new Color constant is added, and the class containing the switch is not recompiled? There is a risk that the new constant will be exposed to our switch. Because this risk is always present with enums, if an exhaustive enum switch does not have a match-all clause then the compiler will synthesize a default clause that throws an exception. This guarantees that the switch cannot complete normally without selecting one of the clauses.
       In conclusion: An exhaustive switch without a match-all clause is better than an exhaustive switch with one, when possible. The notion of exhaustiveness is designed to strike a balance between covering all reasonable cases while not forcing you to write possibly many rare corner cases that will pollute or even dominate your code for little actual value. Put another way: Exhaustiveness is a compile-time approximation of true run-time exhaustiveness.
      }

      Exhaustiveness and sealed classes {
       If the type of the selector expression is a sealed class (JEP 409) then the type coverage check can take into account the permits clause of the sealed class to determine whether a switch block is exhaustive. This can sometimes remove the need for a default clause, which as argued above is good practice.
       */
        // As of Java 21
        sealed interface S permits A, B, C {}
        final class A implements S {}
        final class B implements S {}
        record C(int i) implements S {} // Implicitly final
        static int testSealedExhaustive(S s) {
            return switch (s) {
                case A a -> 1;
                case B b -> 2;
                case C c -> 3;
            };
        }
       // Some extra care is needed when a permitted direct subclass only implements a specific parameterization of a (generic) sealed superclass:
        // As of Java 21
        sealed interface I<T> permits D, E {}
        final class D<X> implements I<String> {}
        final class E<Y> implements I<Y> {}
        static int testGenericSealedExhaustive(I<Integer> i) {
            return switch (i) {
                // Exhaustive as no D case possible. The only permitted subclasses of I are A and B, but the compiler can detect that the switch block need only cover the class B to be exhaustive since the selector expression is of type I<Integer> and no parameterization of A is a subtype of I<Integer>.
                case E<Integer> bi -> 42;
            };
        }
       /*
       Again, the notion of exhaustiveness is an approximation. Because of separate compilation, it is possible for a novel implementation of the interface I to show up at runtime, so the compiler will in this case insert a synthetic default clause that throws.
      Exhaustiveness and compatibility. Exhaustiveness is required of any switch statement that uses pattern or null labels or whose selector expression is not one of the legacy types (char, byte, short, int, Character, Byte, Short, Integer, String, or an enum type).
      }
     }

     Scope of pattern variable declarations {
     */
        // As of Java 21
        static void testFlowScoping(Object obj) {
            if ((obj instanceof String s) && s.length() > 3) { println(s + "in scope"); }
            else { println("s not in scope"); }
        }
      /*
      We extend this flow-sensitive notion of scope for pattern variable declarations to encompass pattern declarations occurring in case labels with three new rules:
       - The scope of a pattern variable declaration which occurs in the pattern of a guarded case label includes the guard, i.e., the when expression.
        */
       // As of Java 21
        static void testScope1(Object obj) {
            switch (obj) {
                case Character c when c.charValue() == 7: println("Ding!"); break;
                default: break;
            }
        }
       /*
       - The scope of a pattern variable declaration which occurs in a case label of a switch rule includes the expression, block, or throw statement that appears to the right of the arrow.
       */
        // As of Java 21
        static void testScope2(Object obj) {
            switch (obj) {
                case Character c -> {
                    if (c.charValue() == 7) { println("Ding!"); }
                    println("Character");
                }
                case Integer i ->
                    throw new IllegalStateException("Invalid Integer argument: " + i.intValue());
                default -> { break; }
            }
        }
       /*
       - The scope of a pattern variable declaration which occurs in a case label of a switch labeled statement group includes the block statements of the statement group. Falling through a case label that declares a pattern variable is forbidden.
       */
        // As of Java 21
        static void testScope3(Object obj) {
            switch (obj) {
                // Only one case label for a switch labeled statement group is OK.
                case Character c:
                    if (c.charValue() == 7) { System.out.print("Ding "); }
                    if (c.charValue() == 9) { System.out.print("Tab "); }
                    println(c +  "in scope");
                    //break;
                    // Without {break} this code allows falling through and switch label consisting of multiple pattern labels.
                // case Integer i: println("An integer " + i);
                // The possibility of falling through a case label that declares a pattern variable is forbidden. If this were allowed and the value of obj were a Character then execution of the switch block could fall through the second statement group, after case Integer i:, where the pattern variable i would not have been initialized. Allowing execution to fall through a case label that declares a pattern variable is therefore a compile-time error.
                default: println("c not in scope");
            }
        }
        // As of Java 21
        static void testScope4(Object obj) {
            switch (obj) {
                case Character c -> {
                    if (c.charValue() == 7) { System.out.print("Ding "); }
                    if (c.charValue() == 9) { System.out.print("Tab "); }
                    println(c + "in scope");
                }
                // Arrow operator -> to execute code for that case. This syntax eliminates case-fall-through, so no break needed.
                case Integer i -> println("An integer " + i);
                default -> println("c not in scope");
            }
        }
      /*
     }

     Dealing with null {
      Traditionally, a switch throws NullPointerException if the selector expression evaluates to null.
      We introduce a new null case label.
      */
        // As of Java 21
        static void nullMatch1(Object obj) {
            switch (obj) {
                case String s  -> println("String: " + s);
                case Integer i -> println("Integer");
                default        -> println("default");
            }
        }
        //is equivalent to:
        // As of Java 21
        static void nullMatch2(Object obj) {
            switch (obj) {
                case null      -> throw new NullPointerException();
                // Error: "Invalid case label combination: 'null' can only be used as a single case label or paired only with 'default'".
                //case null, String s -> System.out.println("String: " + s);
                case String s  -> println("String: " + s);
                case Integer i -> println("Integer");
                default        -> println("default");
            }
            switch (obj) {
                case String s  -> println("String: " + s);
                case Integer i -> println("Integer");
                case null, default -> println("The rest (including null)");
            }
        }
        // If a selector expression evaluates to null and the switch block does not have null case label, then a NullPointerException is thrown as normal.
        static void nullMatch3(Object obj) {
            switch (obj) {
                case Object s -> System.out.println("This doesn't match null");
                // No null label. NullPointerException is thrown if obj is null.
            }
        }
      /*
     }

     Errors {
      Pattern matching can complete abruptly. For example, when matching a value against a record pattern, the record’s accessor method can complete abruptly. In this case, pattern matching is defined to complete abruptly by throwing a MatchException. If such a pattern appears as a label in a switch then the switch will also complete abruptly by throwing a MatchException.
      If a case pattern has a guard, and evaluating the guard completes abruptly, then the switch completes abruptly for the same reason.
      If no label in a pattern switch matches the value of the selector expression then the switch completes abruptly by throwing a MatchException, since pattern switches must be exhaustive.
      To align with pattern switch semantics, switch expressions over enum classes now throw MatchException rather than IncompatibleClassChangeError when no switch label applies at run time. This is a minor incompatible change to the language. (An exhaustive switch over an enum fails to match only if the enum class is changed after the switch has been compiled, which is highly unusual).
     }
    }

    Future work {
     At the moment (JEP 441), pattern switch does not support the primitive types boolean, long, float, and double. Allowing these primitive types would also mean allowing them in instanceof expressions, and aligning primitive type patterns with reference type patterns, which would require considerable additional work. This is left for a possible future JEP.
    }

    */

    }

    /**
     * Pattern Matching for Records
     */
    public static class PatternMatchingForRecords {
    /*
    https://openjdk.org/jeps/440 Record Patterns
    https://openjdk.org/jeps/432 Record Patterns (Second Preview)
    https://openjdk.org/jeps/405 Record Patterns (Preview)
    Used to deconstruct record values.
    Record patterns and type patterns can be nested to enable a powerful, declarative, and composable form of data navigation and processing.
    This feature has co-evolved with Pattern Matching for switch, with which it has considerable interaction.

    Goals {
     - Extend pattern matching to destructure instances of record classes, enabling more sophisticated data queries.
     - Add nested patterns, enabling more composable data queries.
    }

    Motivation {
     In Java 16, JEP 394 extended the instanceof operator to take a type pattern and perform pattern matching. This modest extension allows the familiar instanceof-and-cast idiom to be simplified, making it both more concise and less error-prone.

     Pattern matching and records {
      */
      // As of Java 16
        record Point(int x, int y) {}
        static void printSum16(Object obj) {
            if (obj instanceof Point p) {
                int x = p.x();
                int y = p.y();
                println(x+y);
            }
        }
        // As of Java 21
        static void printSum21(Object obj) {
            if (obj instanceof Point(int x, int y)) {
                // Point(int x, int y) is a record pattern. It lifts the declaration of local variables for extracted components into the pattern itself, and initializes those variables by invoking the accessor methods when a value is matched against the pattern. In effect, a record pattern disaggregates an instance of a record into its components.
                println(x+y);
            }
        }
      /*
     }

     Nested record patterns {
      The true power of pattern matching is that it scales elegantly to match more complicated object graphs.
      */
        // As of Java 16
        enum Color { RED, GREEN, BLUE }
        record ColoredPoint(Point p, Color c) {}
        record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {}
        // To extract the color from the upper-left point, we could write:
        // As of Java 21
        static void printUpperLeftColoredPoint(Rectangle r) {
            if (r instanceof Rectangle(ColoredPoint ul, ColoredPoint lr)) {
                println(ul.c());
            }
        }
        // But the ColoredPoint value ul is itself a record value, which we might want to decompose further. Record patterns therefore support nesting, which allows the record component to be further matched against, and decomposed by, a nested pattern. We can nest another pattern inside the record pattern and decompose both the outer and inner records at once:
        // As of Java 21
        static void printColorOfUpperLeftPoint(Rectangle r) {
            if (r instanceof Rectangle(ColoredPoint(Point p, Color c), ColoredPoint lr)) {
                println(c);
            }
        }
        // Nested patterns allow us, further, to take apart an aggregate with code that is as clear and concise as the code that puts it together. If we were creating a rectangle, for example, we would likely nest the constructors in a single expression:
        // As of Java 16
        Rectangle r = new Rectangle(
            new ColoredPoint(new Point(0, 0), Color.RED),
            new ColoredPoint(new Point(1, 1), Color.GREEN));
        // With nested patterns we can deconstruct such a rectangle with code that echoes the structure of the nested constructors:
        // As of Java 21
        static void printXCoordOfUpperLeftPointWithPatterns(Rectangle r) {
            if (r instanceof Rectangle(ColoredPoint(Point(var x, var y), var c), var lr)) {
                println("Upper-left corner: " + x);
            }
        }

      // Either the entire pattern matches, or not. Nested patterns can, of course, fail to match:
      // As of Java 21
        record Pair(Object x, Object y) {}
        Pair p = new Pair(42, 42);
        void testFail() {
            // Here the record pattern Pair(String s, String t) contains two nested type patterns, namely String s and String t. A value matches the pattern Pair(String s, String t) if it is a Pair and, recursively, its component values match the type patterns String s and String t. In our example code above these recursive pattern matches fail since neither of the record component values are strings, and thus the else block is executed.
            if (p instanceof Pair(String s, String t)) {
                println(s + ", " + t);
            } else {
                println("Not a pair of strings");
            }
        }
     /*
     }
    }

    Description {
     The grammar for patterns becomes:
      Pattern:
        TypePattern
        RecordPattern
      TypePattern:
        LocalVariableDeclaration
      RecordPattern:
        ReferenceType ( [ PatternList ] )
      PatternList :
        Pattern { , Pattern }

     A record pattern consists of a record class type and a (possibly empty) pattern list which is used to match against the corresponding record component values. For example, given the declaration
        record Point(int i, int j) {}
      value v matches the record pattern Point(int i, int j) if it is an instance of the record type Point; if so, the pattern variable i is initialized with the result of invoking the accessor method corresponding to i on the value v, and the pattern variable j is initialized to the result of invoking the accessor method corresponding to j on the value v. The names of the pattern variables do not need to be the same as the names of the record components.
     A record pattern can use var to match against a record component without stating the type of the component. In that case the compiler infers the type of the pattern variable introduced by the var pattern. For example, the pattern Point(var a, var b) is shorthand for the pattern Point(int a, int b).
     An expression is compatible with a record pattern if it could be cast to the record type in the pattern without requiring an unchecked conversion.
     If a record pattern names a generic record class but gives no type arguments (i.e., the record pattern uses a raw type) then the type arguments are always inferred. For example:
     */
        // As of Java 21
        record MyPair<S,T>(S fst, T snd){};
        static void recordInference(MyPair<String, Integer> pair){
            switch (pair) {
                // Inferred record pattern MyPair<String,Integer>(var f, var s)
                case MyPair(var f, var s) -> {}
            }
        }
     /*
     Inference of type arguments for record patterns is supported in all constructs that support record patterns, namely instanceof expressions and switch statements and expressions.
     Inference works with nested record patterns; for example:
     */
        // As of Java 21
        record Box<T>(T t) {}
        static void test1(Box<Box<String>> bbs) {
            // Here the type argument for the nested pattern Box(var s) is inferred to be String, so the pattern itself is inferred to be Box<String>(var s).
            if (bbs instanceof Box<Box<String>>(Box(var s))) {
                println("String " + s);
            }
        }
     /*
     In fact it is possible to drop the type arguments in the outer record pattern as well, leading to the concise code:
     */
        // As of Java 21
        static void test2(Box<Box<String>> bbs) {
            // Here the compiler will infer that the entire instanceof pattern is Box<Box<String>>(Box<String>(var s)).
            if (bbs instanceof Box(Box(var s))) {
                println("String " + s);
            }
        }
     /*

     For compatibility, type patterns do not support the implicit inference of type arguments; e.g., the type pattern List l is always treated as a raw type pattern.
     The null value does not match any record pattern.

     Record patterns and exhaustive switch {
      JEP 441 enhances both switch expressions and switch statements to support pattern labels. Both switch expressions and pattern switch statements must be exhaustive: The switch block must have clauses that deal with all possible values of the selector expression. For pattern labels this is determined by analysis of the types of the patterns; for example, the case label case Bar b matches values of type Bar and all possible subtypes of Bar.
      With pattern labels involving record patterns, the analysis is more complex since we must consider the types of the component patterns and make allowances for sealed hierarchies. For example, consider the declarations:
        class A {}
        class B extends A {}
        sealed interface I permits C, D {}
        final class C implements I {}
        final class D implements I {}
        record Pair<T>(T x, T y) {}

        Pair<A> p1;
        Pair<I> p2;

      The following switch is not exhaustive, since there is no match for a pair containing two values both of type A:
        // As of Java 21
        switch (p1) {                 // Error!
            case Pair<A>(A a, B b) -> ...
            case Pair<A>(B b, A a) -> ...
        }
      These two switches are exhaustive, since the interface I is sealed and so the types C and D cover all possible instances:
        // As of Java 21
        switch (p2) {
            case Pair<I>(I i, C c) -> ...
            case Pair<I>(I i, D d) -> ...
        }
        switch (p2) {
            case Pair<I>(C c, I i) -> ...
            case Pair<I>(D d, C c) -> ...
            case Pair<I>(D d1, D d2) -> ...
        }
      In contrast, this switch is not exhaustive since there is no match for a pair containing two values both of type D:
        // As of Java 21
        switch (p2) {                        // Error!
            case Pair<I>(C fst, D snd) -> ...
            case Pair<I>(D fst, C snd) -> ...
            case Pair<I>(I fst, C snd) -> ...
        }
     }
    }

    Dependencies {
     This JEP 440 builds on Pattern Matching for instanceof (JEP 394), delivered in JDK 16. It has co-evolved with Pattern Matching for switch (JEP 441).
    }
    */
    }

    class Point {
        int x;
        int y;
    }
    class CaseInsensitiveString {
        public String s;
    }
}
