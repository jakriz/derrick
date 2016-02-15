package io.github.jakriz.derrick.example;

import io.github.jakriz.derrick.annotation.DerrickInterface;
import io.github.jakriz.derrick.annotation.SourceFrom;

import java.util.List;

@DerrickInterface(baseUrl = "https://github.com/jakriz/derrick", imports = {"io.github.jakriz.derrick.example.*", "java.util.*"})
public interface DocsMethods {

    @SourceFrom(selector = "#user-content-sample-math-wizard-add", returnLast = true)
    int add(MathWizard mathWizard);

    @SourceFrom(selector = "#user-content-sample-list-add-element", addReturn = "list")
    List<String> addElement();

}
