package com.jakubkriz.derrick.model;

import java.util.List;

public class ResolvedMethod {

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

    private String returnType;
    private String name;
    private List<Argument> arguments;
    private String code;

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
