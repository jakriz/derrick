package io.github.jakriz.derrick.example;

import io.github.jakriz.derrick.annotation.DerrickInterface;
import io.github.jakriz.derrick.annotation.SourceFrom;

@DerrickInterface(baseUrl = "https://github.com/jakriz/derrick", imports = {"io.github.jakriz.derrick.example.*"})
public interface DocsMethods {

    @SourceFrom(selector = "#user-content-sample-math-wizard-add", addReturn = true)
    int add(MathWizard mathWizard);

}
