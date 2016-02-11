package io.github.jakriz.derrick.model;

public class Argument {
    private String type;
    private String name;

    public Argument(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}