package io.github.jakriz.derrick.generator;

import io.github.jakriz.derrick.model.ProcessedInterface;
import io.github.jakriz.derrick.model.ProcessedMethod;

import java.io.IOException;
import java.util.List;

public interface ClassGenerator {

    void generate(ProcessedInterface processedInterface, List<ProcessedMethod> processedMethods) throws IOException;
}
