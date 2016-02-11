package io.github.jakriz.derrick.example;

import java.util.Arrays;

public class MathWizard {

    public int add(int... numbers) {
        return Arrays.stream(numbers).sum();
    }

}
