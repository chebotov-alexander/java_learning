package org.example.java_learning;

public class GenericsMain {
    public static void main(String[] args) {
    // http://www.angelikalanger.com/GenericsFAQ/FAQSections/TechnicalDetails.html
    /*
    Java Generics support definition and use of generic types and methods.  It provides language features for the following purposes:
        Definition of a generic type.
        Definition of a generic method.
        Type parameters.
         Type parameter bounds.
        Type arguments.
         Wildcards.
         Wildcard bounds.
         Wildcard capture.
        Instantiation of a generic type.
         Raw type.
         Concrete instantiation.
         Wildcard instantiation.
        Instantiation of a generic method.
         Automatic type inference.
         Explicit type argument specification.
     */
        GenericsWildcards.keyPointsOnGenericsWildcards();

        System.out.println("Generics theory START ---------------------");

        // Chapter 1
        //GenericsIntro.executeGenericHistory();
        //GenericsIntro.testArraysOfGenerics();
        GenericsIntro.GenericsRestrictions.noExceptionHandling();

        // Chapter 2
        // Restrictions and weird things with generics almost always due to type erasure.
        GenericsTypeErasure.keepRuntimeInformation();
        //GenericsTypeErasure.GenericsAndUncheckedWarning.doNotUseRaw();

        // Chapter 3
        GenericsInheritance.testGenericsInheritance();
        //GenericsInheritance.inheritanceRulesForGenericTypesCouldButShouldnt();

        // Chapter 4
        GenericsWildcards.testWildcards();

        // Chapter 5
        //GenericsExample.testGenericsExample();

        System.out.println("Generics theory END   ---------------------");
    }
}

