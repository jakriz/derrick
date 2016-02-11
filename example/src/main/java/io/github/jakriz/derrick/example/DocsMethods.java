package io.github.jakriz.derrick.example;

import io.github.jakriz.derrick.annotation.DerrickInterface;
import io.github.jakriz.derrick.annotation.SourceFrom;

@DerrickInterface(baseUrl = "http://localhost:8080", imports = {"io.github.jakriz.derrick.example.*"})
public interface DocsMethods {

    @SourceFrom(path = "tutorial.html", selector = ".sample-add", addReturn = true)
    int add(MathWizard mathWizard);

}
