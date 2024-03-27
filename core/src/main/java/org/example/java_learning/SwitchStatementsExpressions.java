package org.example.java_learning;

import static org.example.java_learning.Util.println;

public class SwitchStatementsExpressions {
// https://nipafx.dev/java-switch/
// https://nipafx.dev/java-13-switch-expressions/

    public void testSwitchStatements() {
        var SwitchStatements = new SwitchStatements();
    }
    public void testSwitchExpressions() {
        var SwitchStatements = new SwitchExpressions();
    }

    public static void testFallingThrough() {
        SwitchStatements.fallingThrough1();
        SwitchStatements.fallingThrough2();
        SwitchStatements.fallingThrough3();
    }
}

class SwitchStatements {
/*
 https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.11

    SwitchStatement:
        switch ( Expression ) SwitchBlock

The switch statement transfers control to one of several statements or expressions, depending on the value of an expression.
The Expression is called the selector expression. The type of the selector expression must be char, byte, short, int, or a reference type, or a compile-time error occurs.
 The body of both a switch statement and a switch expression is called a switch block.
Switch Blocks {
    SwitchBlock:
        { SwitchRule {SwitchRule} }
        { {SwitchBlockStatementGroup} {SwitchLabel :} }
    SwitchRule:
        SwitchLabel -> Expression ;
        SwitchLabel -> Block
        SwitchLabel -> ThrowStatement
    SwitchBlockStatementGroup:
        SwitchLabel : {SwitchLabel :} BlockStatements
    SwitchLabel:
        case CaseConstant {, CaseConstant}
        case null [, default]
        case CasePattern {, CasePattern } [Guard]
        default
    CaseConstant:
        ConditionalExpression
    CasePattern:
        Pattern
    Guard:
        when Expression

A {case ... -> ...} label along with its code to its right is called a "switch labeled rule".
A {case ... :} label along with its code to the right is called a "switch labeled statement group"

Every CaseConstant must be either a {constant expression} (https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.29), or the name of an enum constant (§8.9.1), otherwise a compile-time error occurs.
A {case} label with a CasePattern may have an optional {when} expression, known as a guard, which represents a further test on values that match the pattern. A case label is said to be unguarded if either it has no guard, or it has a guard that is a constant expression with value true; and guarded otherwise.
If a case label has multiple patterns then it is a compile-time error for any of the patterns to declare any pattern variables. A case label with multiple case patterns can have a guard. The guard governs the case as a whole, rather than the individual patterns.

Falling through {
 When a switch label applies, and that switch label is for a switch rule, the switch rule expression or statement introduced by the switch label is executed, and nothing else. In the case of a switch label for a statement group, all the block statements in the switch block that follow the switch label are executed, including those that appear after subsequent switch labels. The effect is that execution of statements can "fall through labels".
*/
    public static void fallingThrough1() {
        class FallThrough {
            static void fallThrough(int k) {
                switch (k) {
                    // The code in the switch block for each {case} falls through into the code for the next {case}.
                    case 1: System.out.print("one ");
                    case 2: System.out.print("too ");
                    case 3: System.out.println("many");
                }
            }
            static void testFallThrough() {
                fallThrough(3);
                fallThrough(2);
                fallThrough(1);
            }
        }
        FallThrough.testFallThrough();
        // Prints:
        //many
        //too many
        //one too many
    }

    public static void fallingThrough2() {
        class PreventFallThrough {
            static void preventFallThroughWithBreak(int k) {
                switch (k) {
                    case 1: println("one");
                        break; // exit the switch
                    case 2: println("two");
                        break; // exit the switch
                    case 3: println("many");
                        break; // not needed, but good style
                }
            }
            static void preventFallThroughWithArrow(int k) {
                switch (k) {
                    case 1 -> println("one");
                    case 2 -> println("two");
                    case 3 -> println("many");
                }
            }
            static void testFallThrough() {
                preventFallThroughWithBreak(1);
                preventFallThroughWithBreak(2);
                preventFallThroughWithBreak(3);
                preventFallThroughWithArrow(1);
                preventFallThroughWithArrow(2);
                preventFallThroughWithArrow(3);
            }
        }
        PreventFallThrough.testFallThrough();
        // Prints:
        //one
        //two
        //many
        //one
        //two
        //many

    }
/*
 As of Java 21 it is a compile-time error if, in a switch block that consists of switch labeled statement groups, a statement is labeled with a case pattern that declares one or more pattern variables, and either:
    Condition_1 - an immediately preceding statement in the switch block can complete normally (§14.22), or
    Condition_2 - the statement is labeled with more than one switch label.
*/
    public static void fallingThrough3() {
        Object obj = "Hello";
        switch (obj) {
            case String s:
                // Condition_1. Completes normally.
                println(s + " Statement 1");
                break;
            case Integer i:
                // Compile-time error if previous {break} is commented out due to uninitialized pattern variable "i" since "i" can be reached without matching the pattern "Integer i".
                println(i + 1);
            default:
        }
        switch (obj) {
            // Condition_2. Switch blocks consisting of switch label statement groups allow multiple labels to apply to a statement group.
            case String s:
                break;
            case Integer i:
                // Compile-time error if previous {break} is commented out due to uninitialized pattern variable "i" since "i" can be reached without matching the pattern "Integer i".
                println(i + 1);
            default:
        }
        obj = null;
        switch (obj) {
            case null:
                break;
            case String s:
                println(s + " Statement 2");
            default:
        }
        // Both of these conditions (Condition_1 and Condition_2) apply only when the case pattern declares pattern variables. The following examples, in contrast, are unproblematic:
        record R() {}
        record S() {}
        obj = "Hello";
        switch (obj) {
            case String s:
                // Completes normally.
                println(s + " Statement 3");
                // No break.
            // No pattern variables declared.
            case R():
                // It's either an R or a string.
                println("Statement 4");
                break;
            default:
        }
        obj = new R();
        switch (obj) {
            // Multiple case labels.
            case R():
            case S():
                // Either R or an S.
                println("Statement 5");
                break;
            default:
        }
        obj = null;
        switch (obj) {
            // Multiple case labels.
            case null:
            case R():
                // Either null or an R.
                println("Statement 6");
                break;
            default:
        }
    }
    // Prints:
    //Hello Statement 1
    //Hello Statement 3
    //Statement 4
    //Statement 5
    //Statement 6
/*
}

Exhaustive Switch Blocks  {
 A switch statement or expression is exhaustive if its switch block is exhaustive for the selector expression.
 The switch block of a switch expression or switch statement is exhaustive for a selector expression e if one of the following cases applies:
  - There is a default label associated with the switch block.
  - There is a case null, default label associated with the switch block.
  - The set containing all the case constants and case patterns appearing in an unguarded case label (collectively known as case elements) associated with the switch block is non-empty and covers the type of the selector expression e.
 A set of case elements, P, covers a type T if one of the following cases applies:
  1. P covers a type U where T and U have the same erasure.
  2. P contains a pattern that is unconditional for T.
  3. T is a type variable with upper bound B and P covers B.
  4. T is an intersection type T1& ... &Tn and P covers Ti, for one of the types Ti (1≤ i ≤ n).
  5. The type T is an enum class type E and P contains all of the names of the enum constants of E. A default label is permitted, but not required, in the case where the names of all the enum constants appear as case constants.
  6. The type T names an abstract sealed class or sealed interface C and for every permitted direct subclass or subinterface D of C, one of the following two conditions holds:
   6.1. There is no type that both names D and is a subtype of T, or
   6.2. There is a type U that both names D and is a subtype of T, and P covers U.
   A default label is permitted, but not required, in the case where the switch block exhausts all the permitted direct subclasses and subinterfaces of an abstract sealed class or sealed interface.
  7. The type T names a record class R, and P contains a record pattern p with a type that names R and for every record component of R of type U, if any, the singleton set containing the corresponding component pattern of p covers U.
  8. P rewrites to a set Q and Q covers T.
   A set of case elements, P, rewrites to the set Q, if a subset of P reduces to a pattern p, and Q consists of the remaining elements of P along with the pattern p.
   8.1. A non-empty set of patterns, RP, reduces to a single pattern rp if one of the following holds:
    - RP covers some type U, and rp is a type pattern of type U.
    - RP consists of record patterns whose types all erase to the same record class R with k (k≥1) components and there is a distinguished component cr (1≤r≤k) of R such that for every other component ci (1≤i≤k, i≠r) the set containing the component patterns from the record patterns corresponding to component ci is equivalent to a single pattern qi, the set containing the component patterns from the record patterns corresponding to the component cr reduces to a single pattern q, and rp is the record pattern of type R with a pattern list consisting of the patterns q1, ..., qr-1, q, qr+1, ..., qk.
   8.2. A non-empty set of patterns EP is equivalent to a single pattern ep if one of the following holds:
    - EP consists of type patterns whose types all have the same erasure T, and ep is a type pattern of type T.
    - EP consists of record patterns whose types all erase to the same record class R with k (k≥1) components and for every record component the set containing the corresponding component patterns from the record patterns is equivalent to a single pattern qj (1≤j≤k), and ep is the record pattern of type R with a pattern list consisting of the component patterns q1,...qk.
*/
    // 5.
    enum E1 { F1, G1, H1 }
    static int testEnumExhaustive(E1 e) {
        return switch(e) {
            case F1 -> 0;
            case G1 -> 1;
            case H1 -> 2;
            // Exhaustive. No default required.
        };
    }
    // 6.
    sealed interface I2 permits A2, B2, C2 {}
    final class A2   implements I2 {}
    final class B2   implements I2 {}
    record C2(int j) implements I2 {} // Implicitly final.
    static int testExhaustive1(I2 i) {
        return switch(i) {
            case A2 a -> 0;
            case B2 b -> 1;
            case C2 c -> 2;
            // Exhaustive. No default required.
        };
    }
    // The fact that a permitted direct subclass or subinterface may only extend a particular parameterization of a generic sealed superclass or superinterface means that it may not always need to be considered when determining whether a switch block is exhaustive.
    sealed interface J2<X2> permits D2, E2 {}
    final class D2<Y2> implements J2<String> {}
    final class E2<X2> implements J2<X2> {}
    static int testExhaustive2(J2<Integer> ji) {
        return switch(ji) {
            case E2<Integer> e -> 42;
            // Exhaustive. No default required. As the selector expression has type J<Integer> the permitted direct subclass D need not be considered as there is no possibility that the value of ji can be an instance of D.
        };
    }
    // 7.
    // A record pattern whose component patterns all cover the type of the corresponding record component is considered to cover the record type.
    record Test<X>(Object o, X x){}
    static int testExhaustiveRecordPattern(Test<String> r) {
        return switch(r) {
            case Test<String>(Object o, String s) -> 0;
            // Exhaustive. No default required.
        };
    }
    // 8.
    // Ordinarily record patterns match only a subset of the values of the record type. However, a number of record patterns in a switch block can combine to actually match all of the values of the record type.
    sealed interface I3 permits A3, B3, C3 {}
    final class A3 implements I3 {}
    final class B3 implements I3 {}
    record C3(int j) implements I3 {}  // Implicitly final.
    record Box3(I3 i) {}
    int testExhaustiveRecordPatterns(Box3 box) {
        return switch (box) {
            case Box3(A3 a) -> 0;
            case Box3(B3 b) -> 1;
            case Box3(C3 c) -> 2;
            // Exhaustive. No default required.
        };
    }
    // So determining whether this switch block is exhaustive requires the analysis of the combination of the record patterns. The set containing the record pattern Box(I i) covers the type Box, and so the set containing the patterns Box(A a), Box(B b), and Box(C c) can be rewritten to the set containing the pattern Box(I i). This is because the set containing the patterns A a, B b, C c reduces to the pattern I i (because the same set covers the type I), and thus the set containing the patterns Box(A a), Box(B b), Box(C c) reduces to the pattern Box(I i).
    // However, rewriting a set of record patterns is not always so simple.
    record IPair(I3 i, I3 j){}
    int testNonExhaustiveRecordPatterns(IPair p) {
        return switch (p) {     // Not Exhaustive!
            case IPair(A3 a1, A3 a2) -> 0;
            case IPair(B3 b1, B3 b2) -> 1;
            case IPair(C3 c1, C3 c2) -> 2;
            default -> 3;
            // Autogenerated to reach exhaustiveness without {default}.
            //case IPair(A3 i, B3 j) -> 0;
            //case IPair(A3 i, C3 j) -> 0;
            //case IPair(B3 i, A3 j) -> 0;
            //case IPair(B3 i, C3 j) -> 0;
            //case IPair(C3 i, A3 j) -> 0;
            //case IPair(C3 i, B3 j) -> 0;
            // Or something like this:
            //case IPair(A3 a1, I3 a2) -> 0;
            //case IPair(B3 b1, I3 b2) -> 1;
            //case IPair(C3 c1, I3 c2) -> 2;

        };
    }
/*
Prior to Java SE 21, switch statements (and switch expressions) were limited in two ways: the type of the selector expression was restricted to either an integral type (excluding long), an enum type, or String and no case null labels were supported. Moreover, unlike switch expressions, switch statements did not have to be exhaustive. This is often the cause of difficult-to-detect bugs, where no switch label applies and the switch statement will silently do nothing. For example:
    enum E { A, B, C }
    E e = ...;
    switch (e) {
       case A -> System.out.println("A");
       case B -> System.out.println("B");
       // No case for C!
    }
In Java SE 21, in addition to supporting case patterns, the two limitations of switch statements (and switch expressions) listed above were relaxed to allow a selector expression of any reference type, and to allow a case label with a null literal. The designers of the Java programming language also decided that enhanced switch statements should align with switch expressions and be required to be exhaustive. This is often achieved with the addition of a trivial {default} label.
For compatibility reasons, switch statements that are not enhanced switch statements are not required to be exhaustive. If the switch statement is an enhanced switch statement, then it must be exhaustive .
}

Determining which Switch Label Applies at Run Time {
 Both the execution of a switch statement and the evaluation of a switch expression need to determine if a switch label associated with the switch block applies to the value of the selector expression. This proceeds as follows:
  1. If the value is the null reference, then a case label with a null literal applies.
  2. If the value is not the null reference, then we determine the first (if any) case label in the switch block that applies to the value as follows:
   2.1. A {case} label with a {case} constant c applies to a value of type Character, Byte, Short, or Integer, if the value is first subjected to unboxing conversion and the constant c is equal to the unboxed value. Any unboxing conversion will complete normally as the value being unboxed is guaranteed not to be the null reference. Equality is defined in terms of the "==" operator.
   2.2. A {case} label with a {case} constant c applies to a value that is of type char, byte, short, int, or String or an enum type if the constant c is equal to the value. Equality is defined in terms of the "==" operator unless the value is a String, in which case equality is defined in terms of the equals method of class String.
   2.3. Determining that a {case} label with a {case} pattern p applies to a value proceeds first by checking if the value matches the pattern p
   2.3.1. If pattern matching completes abruptly then the process of determining which switch label applies completes abruptly for the same reason.
   2.3.2. If pattern matching succeeds and the {case} label is unguarded then this case label applies.
   2.3.3. If pattern matching succeeds and the {case} label is guarded, then the guard is evaluated. If the result is of type Boolean, it is subjected to unboxing conversion.
   2.3.4. If evaluation of the guard or the subsequent unboxing conversion (if any) completes abruptly for some reason, the process of determining which switch label applies completes abruptly for the same reason.
   2.3.5. Otherwise, if the resulting value is true then the case label applies.
   2.4. A case null, default label applies to every value.
  3. If the value is not the null reference, and no case label applies according to the rules of step 2, but there is a {default} label associated with the switch block, then the {default} label applies.

 Several case constants.
 A single {case} label can contain several {case} constants. The label applies to the value of the selector expression if any one of its constants is equal to the value of the selector expression.
*/
    void testSeveralCaseConstants(Day day) {
        switch (day) {
            case SATURDAY, SUNDAY:
                System.out.println("It's the weekend!");
                break;
        }
    }
/*
}

An enhanced switch statement {
 As of Java 21 an enhanced switch statement is one where either the type of the selector expression is not char, byte, short, int, Character, Byte, Short, Integer, String, or an enum type, or there is a case pattern or null literal associated with the switch block. All of the following must be true for the switch block of a switch statement, or a compile-time error occurs:
  - Every switch rule expression in the switch block is a statement expression (Assignment, PreIncrementExpression, PreDecrementExpression, PostIncrementExpression, PostDecrementExpression, MethodInvocation, ClassInstanceCreationExpression). Switch statements differ from switch expressions in terms of which expressions may appear to the right of an arrow (->) in the switch block, that is, which expressions may be used as switch rule expressions. In a switch statement, only a statement expression may be used as a switch rule expression, but in a switch expression, any expression may be used
  - If the switch statement is an enhanced switch statement, then it must be exhaustive.
}

Execution of a switch Statement {
 If no switch label applies, then one of the following holds:
  - If the value of the selector expression is null, then a NullPointerException is thrown and the entire switch statement completes abruptly for that reason.
  - If the switch statement is an enhanced switch statement, then a MatchException is thrown and the entire switch statement completes abruptly for that reason.
  - If the value of the selector expression is not null, and the switch statement is not an enhanced switch statement, then the entire switch statement completes normally.
 If a switch label applies, then one of the following holds:
  - If it is the switch label for a switch rule expression, then the switch rule expression is necessarily a statement expression (§14.11.2). The statement expression is evaluated. If the evaluation completes normally, then the switch statement completes normally. If the result of evaluation is a value, it is discarded.
  - If it is the switch label for a switch rule block, then the block is executed. If this block completes normally, then the switch statement completes normally.
  - If it is the switch label for a switch rule {throw} statement, then the {throw} statement is executed.
  - If it is the switch label for a switch labeled statement group, then all the statements in the switch block that follow the switch label are executed in order. If these statements complete normally, then the switch statement completes normally.
  - Otherwise, there are no statements in the switch block that follow the switch label that applies, and the switch statement completes normally.
}

The scope of a local variable {
  Prior to Java 14 the scope of a local variable in the switch block is the entire block:
  */
    void scopeLocalVariableBefore14(Day day) {
        switch (day) {
            case MONDAY:
            case TUESDAY:
                int temp = 0; // The scope of 'temp' continues to the "}".
                break;
            case WEDNESDAY:
            case THURSDAY:
                //int temp = 1; // Error. Variable 'temp' is already defined in the scope.
                int temp2 = 1;
                break;
            default:
                //int temp = 2; // Error.
                //int temp2 = 2; // Error.
                int temp3 = 2;    // Can't call this variable 'temp'
        }
    }
    void scopeLocalVariableAfter14(Day day) {
        switch (day) {
            case MONDAY, TUESDAY -> { int temp = 0; }
            case WEDNESDAY, THURSDAY -> { int temp = 1; }
            default -> { int temp = 2; }
        }
    }
  /*
}

}
*/
}

class SwitchExpressions {
/*
https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.28
https://openjdk.org/jeps/361 Switch Expressions
https://openjdk.org/jeps/354 Switch Expressions (Second Preview)
https://openjdk.org/jeps/325 Switch Expressions (Preview)

The switch expression transfers control to one of several statements or expressions, depending on the value of an expression; all possible values of that expression must be handled; all of the several statements and expressions must produce a value for the result of the switch expression.
So the switch expression produces value that must be handled, whereas the switch statement does not or evaluated value gets discarded.
Before Java 12, if you wanted to compute a value, you had to either assign the result to a variable (and then {break}) or {return} from a method dedicated to the switch statement.
*/
    static void howManyStatement(int k) {
        switch (k) {
            case 1  -> System.out.println("one");
            case 2  -> System.out.println("two");
            default -> System.out.println("many");
        }
    }
    static void howManyExpression(int k) {
        System.out.println(
            switch (k) {
                case  1 -> "one";
                case  2 -> "two";
                default -> "many";
            }
        );
    }
/*
Run-Time Evaluation of switch Expressions {
 If no switch label applies, then one of the following holds:
  - If the value of the selector expression is null, then a NullPointerException is thrown and evaluation of the switch expression completes abruptly for that reason.
  - Otherwise, a MatchException is thrown and evaluation of the switch expression completes abruptly for that reason.
 If a switch label applies, then one of the following holds:
  - If it is the switch label for a switch rule expression, then the expression is evaluated. If the result of evaluation is a value, then the switch expression completes normally with the same value.
  - If it is the switch label for a switch rule block, then the block is executed. If this block completes normally, then the switch expression completes normally.
  - If it is the switch label for a switch rule throw statement, then the throw statement is executed.
  - Otherwise, all the statements in the switch block after the switch label that applies are executed in order. If these statements complete normally, then the switch expression completes normally.
 If execution of any statement or expression in the switch block completes abruptly, it is handled as follows:
  - If evaluation of an expression completes abruptly, then evaluation of the switch expression completes abruptly for the same reason.
  - If execution of a statement completes abruptly because of a yield with value V, then evaluation of the switch expression completes normally and the value of the switch expression is V.
  - If execution of a statement completes abruptly for any reason other than a yield with a value, then evaluation of the switch expression completes abruptly for the same reason.


}

The Switch Block of a switch Expression {
 In addition to the general rules for switch blocks (§14.11.1), there are further rules for switch blocks in switch expressions.
 It is a compile-time error if a switch expression is not exhaustive. In practice this normally means that a default clause is required; however, in the case of an enum switch expression that covers all known constants, a default clause is inserted by the compiler to indicate that the enum definition has changed between compile-time and runtime. Relying on this implicit default clause insertion makes for more robust code; now when code is recompiled, the compiler checks that all cases are explicitly handled. Had the developer inserted an explicit default clause (as is the case today) a possible error will have been hidden.

A switch expression is a poly expression; if the target type is known, this type is pushed down into each arm. The type of a switch expression is its target type, if known; if not, a standalone type is computed by combining the types of each case arm.
If the target type is not known ({var} is used), a type is computed by finding the most specific supertype of the types that the branches produce, so mismatch might occur. For example types: {String} and {Serializable} has discovered and result type has type as is the first branch that is {String}, and compile-time error produced. In that case type must be chosen explicitly as {Serializable}.

The control statements, {break}, {yield}, {return} and {continue}, cannot jump through a switch expression, such as in the following:
    z:
        for (int i = 0; i < MAX_VALUE; ++i) {
            int k = switch (e) {
                case 0:
                    yield 1;
                case 1:
                    yield 2;
                default:
                    continue z;
                    // ERROR! Illegal jump through a switch expression
            };
        ...
        }
}

*/
    int returnWithStatementAndVariable(int value) {
        int result;
        switch (value) {
            case 42: result = 0; break;
            case 4242: result = 1; break;
            default: throw new IllegalStateException();
        }
        return result;
    }
    int returnWithStatementAndReturnFromBlock(int value) {
        switch (value) {
            case 42: {
                println("");
                return 0;
            }
            case 4242: return 1;
            default: throw new IllegalStateException();
        }
    }
    int returnWithStatementAndReturnArrow(int value) {
        switch (value) {
            case 42 -> {
                println("");
                return 0;
            }
            case 4242 -> { return 1; }
            default -> throw new IllegalStateException();
        }
    }
    int returnWithExpressionAndArrow(int value) {
        return switch (value) {
            case 42 -> 0;
            case 4242 -> 1;
            default -> throw new IllegalStateException();
        };
    }
    int returnWithExpressionAndYieldColon(int value) {
        return switch (value) {
            case 42:
                println("");
                yield 0;
            case 4242: {
                println("");
                yield 42;
            }
            case 424242: yield 1;
            default: throw new IllegalStateException();
        };
    }
    int returnWithExpressionAndYield(int value) {
        return switch (value) {
            case 42 -> {
                println("");
                yield 0;
            }
            case 4242 -> 42;
            default -> throw new IllegalStateException();
        };
    }
}
