package com.jakubkriz.derrick.example;

import com.jakubkriz.derrick.annotation.DerrickInterface;
import com.jakubkriz.derrick.annotation.SourceFrom;

@DerrickInterface(baseUrl = "http://localhost:8080", imports = {"com.jakubkriz.derrick.example.*"})
public interface DocsMethods {

    @SourceFrom(path = "tutorial.html", selector = ".sample-add", addReturn = true)
    int add(MathWizard mathWizard);

}
