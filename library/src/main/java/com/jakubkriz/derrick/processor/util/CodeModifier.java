package com.jakubkriz.derrick.processor.util;

import java.util.Scanner;

public class CodeModifier {
    private static final String NL = System.lineSeparator();
    private static final String ASSIGNMENT = "^[^=]+=";

    public String removeTopLevelMethod(String code) {
        // TODO this is hard, will do later
        return code;
    }

    public String changeToAddReturnOnLastLine(String code) {
        StringBuilder sb = new StringBuilder();

        Scanner scanner = new Scanner(code);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (scanner.hasNextLine()) {
                sb.append(line).append(NL);
            } else {
                line = removeAssignment(line);
                sb.append("return ").append(line);
            }
        }
        scanner.close();

        return sb.toString();
    }

    private String removeAssignment(String line) {
        return line.replaceFirst(ASSIGNMENT, "").trim();
    }
}
