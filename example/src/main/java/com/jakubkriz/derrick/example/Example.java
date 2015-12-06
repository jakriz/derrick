package com.jakubkriz.derrick.example;

import com.jakubkriz.derrick.SourceFrom;

public class Example {

    public static void main(String... args) {
        System.out.println("Imma call myMethod");
        new A().myMethod();
    }

    public static class A {
        @SourceFrom(url = "localhost:8080/tutorial.html", selector = ".codeForMyMethod")
        public void myMethod() {
            System.out.println("I don't wanna be here tbqh");
        }
    }

}
