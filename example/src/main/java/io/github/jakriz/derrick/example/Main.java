package io.github.jakriz.derrick.example;

import io.github.jakriz.derrick.Derrick;

public class Main {

    private DocsMethods docsMethods = Derrick.get(DocsMethods.class);

    public void run() {
        System.out.println("We are invoking a method taken from the docs: add()");
        int addResult = docsMethods.add(new MathWizard());
        System.out.println(addResult);
    }

    public static void main(String... args) {
        new Main().run();
    }

}
