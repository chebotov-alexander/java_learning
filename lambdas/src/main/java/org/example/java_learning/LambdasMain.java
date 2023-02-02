package org.example.java_learning;

import java.util.Arrays;
import java.util.List;

public class LambdasMain {
    public static void main(String[] args) {
        System.out.println("Lambdas theory START ---------------------");

        LambdasInDetails.LambdasUsefulMethods.lambdasCompositionExamples();
        LambdasInDetails.LambdasAndMathematics.lambdasAndMathematicsTest();

        System.out.println("Lambdas theory END   ---------------------");
    }

    static List<Apple> inventory = Arrays.asList(
        new Apple(80, Color.GREEN),
        new Apple(155, Color.GREEN),
        new Apple(120, Color.RED)
    );

    static final String FILE;
    static {
        try {
            FILE = Class
                .forName("org.example.java_learning.LambdasMain")
                .getResource("/org/example/java_learning/data.txt").getFile();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static class Letter{
        public static String addHeader(String text) {
            return "From Raoul, Mario and Alan: " + text;
        }
        public static String addFooter(String text) {
            return text + " Kind regards";
        }
        public static String checkSpelling(String text) {
            return text.replaceAll("labda", "lambda");
        }
    }
}
