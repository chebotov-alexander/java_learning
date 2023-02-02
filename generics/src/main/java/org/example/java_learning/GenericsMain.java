package org.example.java_learning;

import java.util.ArrayList;
import java.util.List;

public class GenericsMain {
    public static void main(String[] args) {
        System.out.println("Generics theory START ---------------------");

        // Chapter 1
        var genericsIntro = new GenericsIntro();
        //genericsIntro.executeGenericHistory();

        // Chapter 2
        var typeErasure = new GenericsTypeErasure();
        typeErasure.keepRuntimeInformation();

        // Chapter 3
        var inheritance = new GenericsInheritance();
        //inheritance.inheritanceRulesForGenericTypesCouldButShouldnt();

        // Chapter 4
        var boundedTypeParameters = new GenericsBoundedTypeParameters();

        // Chapter 5
        var wildcards = new GenericsWildcards();
        wildcards.invokeCaptureHelperTest();

        System.out.println("Generics theory END   ---------------------");
    }
}

