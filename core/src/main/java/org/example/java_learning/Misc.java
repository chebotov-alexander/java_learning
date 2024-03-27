package org.example.java_learning;

public class Misc {
/*

Expressions and Operators {
 The default operator precedence in Java was chosen for compatibility with C; the designers of C chose this precedence so that most expressions can be written naturally without parentheses. Only a few common Java idioms require parentheses. Examples include:
  // Class cast combined with member access
  ((Integer) o).intValue();
  // Assignment combined with comparison
  while((line = in.readLine()) != null) { ... }
  // Bitwise operators combined with comparison
  if ((flags & (PUBLIC | PROTECTED)) != 0) { ... }

 As with operator precedence, operator associativity establishes a default order of evaluation for an expression. This default order can be overridden through the use of parentheses. However, the default operator associativity in Java has been chosen to yield a natural expression syntax. Most operators are left-to-right associative, which means that the operations are performed from left to right. The assignment and unary operators, however, have right-to-left associativity.
  a = b += c = -~d
  // This is evaluated as follows:
  a = (b += (c = -(~d)))

  ______________________________________________________________________________________________________
 |Order of evaluation. When the Java interpreter evaluates an expression, it performs the various       |
 | operations in an order specified by the parentheses in the expression, the precedence of the         |
 | operators, and the associativity of the operators. Before any operation is performed, however, the   |
 | interpreter first evaluates the operands of the operator (the exceptions are the &&, ||, and ?:      |
 | operators, which do not always evaluate all their operands).                                         |
 |The P and A columns of the table specify the precedence and associativity of each group of related    |
 | operators, respectively. The table is ordered from highest precedence to lowest.                     |
 |------------------------------------------------------------------------------------------------------|
 |P |A| Operator   |  Operand type(s)   |                      Operation performed                      |
 |------------------------------------------------------------------------------------------------------|
 |16|L|.           |object, member      |Object member access                                           |
 |  | |[ ]         |array, int          |Array element access                                           |
 |  | |( args )    |method, arglist     |Method invocation                                              |
 |  | |++, --      |variable            |Post-increment, post-decrement                                 |
 |15|R|++, --      |variable            |Pre-increment, pre-decrement                                   |
 |  | |+, -        |number              |Unary plus, unary minus                                        |
 |  | |~           |integer             |Bitwise complement                                             |
 |  | |!           |boolean             |Boolean NOT                                                    |
 |14|R|new         |class, arglist      |Object creation                                                |
 |  | |( type )    |type, any           |Cast (type conversion)                                         |
 |13|L|*, /, %     |number, number      |Multiplication, division, remainder                            |
 |12|L|+, -        |number, number      |Addition, subtraction                                          |
 |  | |+           |string, any         |String concatenation                                           |
 |11|L|<<          |integer, integer    |Left shift                                                     |
 |  | |>>          |integer, integer    |Right shift with sign extension                                |
 |  | |>>>         |integer, integer    |Right shift with zero extension                                |
 |10|L|<, <=       |number, number      |Less than, less than or equal                                  |
 |  | |>, >=       |number, number      |Greater than, greater than or equal                            |
 |  | |instanceof  |reference, type     |Type comparison                                                |
 |9 |L|==          |primitive, primitive|Equal (have identical values)                                  |
 |  | |!=          |primitive, primitive|Not equal (have different values)                              |
 |  | |==          |reference, reference|Equal (refer to same object)                                   |
 |  | |!=          |reference, reference|Not equal (refer to different objects)                         |
 |8 |L|&           |integer, integer    |Bitwise AND                                                    |
 |  | |&           |boolean, boolean    |Boolean AND                                                    |
 |7 |L|^           |integer, integer    |Bitwise XOR                                                    |
 |  | |^           |boolean, boolean    |Boolean XOR                                                    |
 |6 |L|ǀ           |integer, integer    |Bitwise OR                                                     |
 |  | |ǀ           |boolean, boolean    |Boolean OR                                                     |
 |5 |L|&&          |boolean, boolean    |Conditional AND                                                |
 |4 |L|ǀǀ          |boolean, boolean    |Conditional OR                                                 |
 |3 |R|? :         |boolean, any        |Conditional (ternary) operator                                 |
 |2 |R|=           |variable, any       |Assignment                                                     |
 |  | |*=, /=, %=, |variable, any       |Assignment with operation                                      |
 |  | |+=, -=, <<=,|                    |                                                               |
 |  | |>>=, >>>=,  |                    |                                                               |
 |  | |&=, ^=, ǀ=  |                    |                                                               |
 |1 |R|→           |arglist, method body|lambda expression                                              |
 |------------------------------------------------------------------------------------------------------|
 |Number- An integer, floating-point value, or character (i.e., any primitive type except boolean).     |
 | Auto-unboxing means that the wrapper classes (such as Character, Integer, and Double) for these types|
 | can be used in this context as well.                                                                 |
 |Integer - A byte, short, int, long, or char value (long values are not allowed for the array access   |
 | operator [ ]). With auto-unboxing, Byte, Short, Integer, Long, and Character values are also allowed.|
 |Unary operators operate on only one operand, for example, the unary minus operator changes the sign of|
 | a single number:                                                                                     |
 |  -n    // The unary minus operator.                                                                  |
 |  a – b // The subtraction operator is a binary operator.                                             |
 |Side effects {                                                                                        |
 | Every operator computes a value based on one or more operand values. Some operators, however, have   |
 |  side effects in addition to their basic evaluation. If an expression contains side effects,         |
 |  evaluating it changes the state of a Java program in such a way that evaluating the expression      |
 |  again may yield a different result. For example, the ++ increment operator has the side effect of   |
 |  incrementing a variable. The expression ++a increments the variable a and returns the newly         |
 |  incremented value. If this expression is evaluated again, the value will be different.              |
 | The method invocation operator () has side effects if the invoked method has side effects. Some      |
 |  methods, such as Math.sqrt(), simply compute and return a value without side effects of any kind.   |
 |  Typically, however, methods do have side effects. Finally, the new operator has the profound side   |
 |  effect of creating a new object.                                                                    |
 |  For example: int v = ++a + ++a * ++a;                                                               |
 |   in case if a=2 v will be 23. Although the multiplication is performed before the addition, the     |
 |    operands of the + operator are evaluated first. As the operand of ++ are both ++a, these are      |
 |     evaluated to 3 and 4, and so the expression evaluates to 3 + 4 * 5, or 23.                       |
 |}                                                                                                     |
 |------------------------------------------------------------------------------------------------------|

 Return type {
  Just as every operator expects its operands to be of specific types, each operator produces a value of a specific type. The arithmetic, increment and decrement, bitwise, and shift operators return a double if at least one of the operands is a double. They return a float if at least one of the operands is a float. They return a long if at least one of the operands is a long. Otherwise, they return an int, even if both operands are byte, short, or char types that are narrower than int.
  The comparison, equality, and Boolean operators always return boolean values. Each assignment operator returns whatever value it assigned, which is of a type compatible with the variable on the left side of the expression. The conditional operator returns the value of its second or third argument (which must both be convertible to the same type).
 }

 Increment and Decrement Operators {
  The expressions x++ and x-- are equivalent to x = x + 1 and x = x - 1, respectively, except that when you are using the increment and decrement operators, x is evaluated only once. If x is itself an expression with side effects, this makes a big difference. For example, these two expressions are not equivalent, as the second form increments i twice:
   a[i++]++;             // Increments an element of an array
   a[i++] = a[i++] + 1;  // Adds 1 to an array element and stores new value in another element
 }

 Comparison Operators {
  If you experiment comparing strings via == you may see results that suggest it works properly. This is a side effect of Java’s internal caching of strings, known as interning. The only reliable way to compare strings (or any other reference type for that matter) for equality is the equals() method.
  The same applies with primitive wrapper classes, so new Integer(1) != new Integer(1), while the preferred Integer.valueOf(1) == Integer.valueOf(1) does. The lesson is clearly that looking at equality on any nonprimitive type should be done with equals().
  If == is used to compare two numeric or character operands that are not of the same type, the narrower operand is converted to the type of the wider operand before the comparison is done.
 }

 Boolean Operators {
  Operands evaluates one by one. If current operand evaluates to false for AND operator then evaluation of the rest operands are skipped. For OR operator same if current operand evaluates to true. So you must use caution when using this operator with expressions that have side effects.
  Boolean AND (&), OR (|) almost always used as a bitwise operator with integer operands, however when used with boolean operands behave like the &&, || operators respectively, except that it always evaluates all operands, regardless of the value of the current operand. So its use with boolean operands is very rare.
  Boolean XOR (^) when used with boolean operands, this operator computes the exclusive OR (XOR) of its operands. It evaluates to true if exactly one of the two operands is true. In other words, it evaluates to false if both operands are false or if both operands are true. Unlike the && and || operators, this one must always evaluate both operands. The ^ operator is much more commonly used as a bitwise operator on integer operands. With boolean operands, this operator is equivalent to the != operator.

  Because ! is a unary operator, it has a high precedence and often must be used with parentheses: if (!(x > y && y > z)).
 }

 Bitwise and Shift Operators {
  The bitwise operators are not commonly used in modern Java except for low-level work (e.g., network programming). They are used for testing and setting individual flag bits in a value. To understand their behavior, you must understand binary (base-2) numbers and the two’s complement format used to represent negative integers.
  If either of the arguments to a bitwise operator is a long, the result is a long. Otherwise, the result is an int. If the left operand of a shift operator is a long, the result is a long; otherwise, the result is an int. The operators are:

 }


}

 3.10.6. Escape Sequences for Character and String Literals https://docs.oracle.com/javase/specs/jls/se12/html/jls-3.html#jls-3.10.6

 Java Object Serialization Specification: 1 - System Architecture {
  https://docs.oracle.com/en/java/javase/21/docs/specs/serialization/serial-arch.html#serialization-of-records
  https://docs.oracle.com/en/java/javase/21/serializable-records/index.html
 }

 */
}
