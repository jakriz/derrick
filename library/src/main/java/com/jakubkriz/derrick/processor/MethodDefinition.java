package com.jakubkriz.derrick.processor;

import java.util.Set;

public class MethodDefinition {

    private Set<String> imports;
    private String name;
    private String code;

    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
