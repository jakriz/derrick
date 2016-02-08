package com.jakubkriz.derrick.generator;

import com.jakubkriz.derrick.model.ResolvedInterface;
import com.jakubkriz.derrick.model.ResolvedMethod;

import java.io.IOException;
import java.util.List;

public interface ClassGenerator {

    void generate(ResolvedInterface resolvedInterface, List<ResolvedMethod> resolvedMethods) throws IOException;
}
