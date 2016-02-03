package com.jakubkriz.derrick.example;

import com.jakubkriz.derrick.Derrick;

public class Main {

    private ExternalMethods externalMethods = Derrick.get(ExternalMethods.class);

    public void myTest() {
        System.out.println("We are invoking an external method myMethod()");
        externalMethods.myMethod();
    }

    public static void main(String... args) {
        System.out.println("We are running myTest()");
        new Main().myTest();
    }

}
