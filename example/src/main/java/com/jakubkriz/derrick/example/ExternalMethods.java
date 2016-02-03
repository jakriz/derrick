package com.jakubkriz.derrick.example;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.annotation.SourceFrom;

@DerrickInterface
public interface ExternalMethods {

    @SourceFrom(url = "http://localhost:8080/tutorial.html", selector = ".codeForMyMethod")
    void myMethod();

}
