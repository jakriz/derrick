package com.jakubkriz.derrick.generator;

import com.jakubkriz.derrick.model.ProcessedInterface;
import com.jakubkriz.derrick.model.ProcessedMethod;

import java.io.IOException;
import java.util.List;

public interface ClassGenerator {

    void generate(ProcessedInterface processedInterface, List<ProcessedMethod> processedMethods) throws IOException;
}
