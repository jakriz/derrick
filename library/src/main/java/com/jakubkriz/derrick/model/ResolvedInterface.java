package com.jakubkriz.derrick.model;

import java.util.Set;

public class ResolvedInterface {

    private String generatedPackage;
    private String originalSimpleName;
    private String generatedSimpleName;
    private Set<String> imports;

    public String getGeneratedPackage() {
        return generatedPackage;
    }

    public void setGeneratedPackage(String generatedPackage) {
        this.generatedPackage = generatedPackage;
    }

    public String getGeneratedSimpleName() {
        return generatedSimpleName;
    }

    public void setGeneratedSimpleName(String generatedSimpleName) {
        this.generatedSimpleName = generatedSimpleName;
    }

    public String getOriginalSimpleName() {
        return originalSimpleName;
    }

    public void setOriginalSimpleName(String originalSimpleName) {
        this.originalSimpleName = originalSimpleName;
    }

    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }
}
