package io.github.jakriz.derrick.example;

import io.github.jakriz.derrick.Derrick;

import java.util.List;

public class Main {

    private DocsMethods docsMethods = Derrick.get(DocsMethods.class);

    public void run() {
        System.out.println("We are invoking a method taken from the docs: MathWizard.add()");
        int addResult = docsMethods.add(new MathWizard());
        System.out.println(addResult);

        System.out.println("We are invoking a method taken from the docs: create and add element to list");
        List<String> resultList = docsMethods.addElement();
        System.out.println(resultList);
    }

    public static void main(String... args) {
        new Main().run();
    }

}
