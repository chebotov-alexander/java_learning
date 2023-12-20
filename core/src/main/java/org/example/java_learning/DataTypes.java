package org.example.java_learning;

public class DataTypes {
/*
Summary:
 1. Floating-point arithmetic never throws exceptions, even when performing illegal operations, like dividing zero by zero or taking the square root of a negative number.


https://docs.oracle.com/javase/tutorial/java/data/index.html
https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html
https://javarush.com/groups/posts/2274-kak-ispoljhzovatjh-bigdecimal-v-java


Java primitive data types {
  ______________________________________________________________________________
 | Type  |    Contains      |Default| Size  |              Range                |
 |------------------------------------------------------------------------------|
 |boolean|true or false     |false  |1 bit  |NA                                 |
 |------------------------------------------------------------------------------|
 |char   |Unicode character |\u0000 |16 bits|\u0000 (or 0) to \uffff (or 65,535)|
 |------------------------------------------------------------------------------|
 |byte   |Signed integer    |0      |8 bits |–128 to 127                        |
 | Useful in place of int where their limits help to clarify code; the fact that|
 |  a variable's range is limited can serve as a form of documentation.         |
 |------------------------------------------------------------------------------|
 |short  |Signed integer    |0      |16 bits|–32768 to 32767                    |
 | As with byte, the same guidelines apply: you can use a short to save memory  |
 |  in large arrays, in situations where the memory savings actually matters.   |
 |------------------------------------------------------------------------------|
 |int    |Signed integer    |0      |32 bits|-2,147,483,648 to 2,147,483,647    |
 |In Java SE 8 and later, the int data type can be used (through Integer class) |
 | to represent an unsigned 32-bit integer, which has a minimum value of 0 and a|
 | maximum value of (23^2)-1. Static methods like compareUnsigned,              |
 | divideUnsigned etc have been added to the Integer class to support the       |
 | arithmetic operations for unsigned integers.                                 |
 |------------------------------------------------------------------------------|
 |long   |Signed integer    |0      |64 bits|-9,223,372,036,854,775,808 to      |
 |       |                  |       |       | 9,223,372,036,854,775,807         |
 |The Long class contains methods like compareUnsigned, divideUnsigned etc to   |
 | support arithmetic operations for unsigned long.                             |
 |------------------------------------------------------------------------------|
 |float  |IEEE 754 floating |0.0|32 bits|1.4E–45 to 3.4E+38 Stores fractional   |
 |       | point            |   |       | numbers. Sufficient for storing 6 to 7|
 |       |                  |   |       | decimal digits.                       |
 | Never use for precise values, such as currency, use java.math.BigDecimal     |
 |  class instead. As with the recommendations for byte and short, use a float  |
 |  (instead of double) if you need to save memory in large arrays of floating  |
 |  point numbers.                                                              |
 |------------------------------------------------------------------------------|
 |double |IEEE 754 floating |0.0|64 bits|4.9E–324 to 1.8E+308 Stores fractional |
 |       | point            |   |       | numbers. Sufficient for storing 15    |
 |       |                  |   |       | decimal digits.                       |
 | For decimal values, this data type is generally the default choice. As       |
 |  mentioned above, this data type should never be used for precise values,    |
 |  such as currency.                                                           |
 |------------------------------------------------------------------------------|
}

Integer Types {
 Integer arithmetic in Java never produces an overflow or an underflow when you exceed the range of a given integer type. Instead, numbers just wrap around. For example,
   overflow:
    byte b1 = 127, b2 = 1;         // Largest byte is 127
    byte sum = (byte)(b1 + b2);    // Sum wraps to -128, the smallest byte
   underflow:
    byte b3 = -128, b4 = 5;        // Smallest byte is -128
    byte sum2 = (byte)(b3 - b4);   // Sum wraps to a large byte value, 123

 Integer division by zero and modulo by zero are illegal and cause an ArithmeticException to be thrown.
}

Floating-Point Types {
 A float is a 32-bit approximation, which results in at least six significant decimal digits, and a double is a 64-bit approximation, which results in at least 15 significant digits.

 Examples:
  123.45
  0.0
  .01
  1.2345E02 // 1.2345 * 10^2 or 123.45
  1e-6      // 1 * 10^-6 or 0.000001
  6.02e23   // Avogadro's Number: 6.02 * 10^23

 Floating-point literals are double values by default:
  double d = 6.02E23;
  float f = 6.02e23f;

 Infinity, zero and NaN {
  !Floating-point arithmetic never throws exceptions, even when performing illegal operations, like dividing zero by zero or taking the square root of a negative number.
  The float and double types can also represent four special values: positive and negative infinity, zero, and NaN.
  When a floating-point computation produces a value that:
   overflows - then the infinity values result.
   underflows - then a zero value results.
  The infinite floating-point values behave as you would expect. Adding or subtracting any finite value to or from infinity, for example, yields infinity. Negative zero behaves almost identically to positive zero, and, in fact, the == equality operator reports that negative zero is equal to positive zero. One way to distinguish negative zero from positive, or regular, zero is to divide by it: 1.0/0.0 yields positive infinity, but 1.0 divided by negative zero yields negative infinity. Finally, because NaN is Not a Number, the == operator says that it is not equal to any other number, including itself:
   double NaN = 0.0/0.0;   // Not a Number
   NaN == NaN;             // false
   Double.isNaN(NaN);      // true
   double inf = 1.0/0.0;       // Infinity
   double neginf = -1.0/0.0;   // Negative infinity
   double negzero = -1.0/inf;  // Negative zero
   double NaN = 0.0/0.0;       // Not a Number
  Float and Double classes have constants: MIN_VALUE, MAX_VALUE, NEGATIVE_INFINITY, POSITIVE_INFINITY, and NaN.
  }

Primitive Type Conversions {
 Boolean is the only primitive type that cannot be converted to or from another primitive type in Java.
 javac complains when you attempt any narrowing conversion, even if the value being converted would in fact fit in the narrower range of the specified type:
  int i = 13;
  byte b = i; // Incompatible types: possible lossy conversion from int to byte
 The one exception to this rule is that you can assign an integer literal (an int value) to a byte or short variable if the literal falls within the range of the variable.
  byte b = 13;
 Casting to force Java to perform the conversion:
  int i = 13;
  byte b = (byte) i;
  i = (int) 13.456;
 Casts of primitive types are most often used to convert floating-point values to integers. When you do this, the fractional part of the floating-point value is simply truncated (i.e., the floating-point value is rounded toward zero, not toward the nearest integer). The static methods Math.round(), Math.floor(), and Math.ceil() perform other types of rounding.
 Char type is unsigned, so it behaves differently than the short type, even though both are 16 bits wide:
  short s = (short) 0xffff; // These bits represent the number -1
  char c = '\uffff';        // The same bits, as a Unicode character
  int i1 = s;               // Converting the short to an int yields -1
  int i2 = c;               // Converting the char to an int yields 65535
}
}
 */

}
