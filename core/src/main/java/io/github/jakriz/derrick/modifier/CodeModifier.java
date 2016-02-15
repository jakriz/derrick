package io.github.jakriz.derrick.modifier;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeModifier {
    private static final String NL = System.lineSeparator();
    private static final String ASSIGNMENT = "^[^=]+=";
    private static final Pattern TOP_LEVEL_METHOD_BEGIN = Pattern.compile("\\A\\s*([^ \\(\\)]+\\s+){1,3}[^ \\(\\)]+ *\\([^\\)]*\\) *\\{");
    private static final Pattern TOP_LEVEL_METHOD_END = Pattern.compile("\\}\\s*\\z");

    public String removeTopLevelMethod(String code) {
        Matcher beginMatcher = TOP_LEVEL_METHOD_BEGIN.matcher(code);
        if (beginMatcher.find()) {
            code = beginMatcher.replaceFirst("");
            code = TOP_LEVEL_METHOD_END.matcher(code).replaceFirst("");
        }
        return code;
    }

    public String changeToReturnLastLine(String code) {
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

    public String changeToReturnSpecified(String code, String toReturn) {
        return code + NL + "return " + toReturn + ";";
    }

    private String removeAssignment(String line) {
        return line.replaceFirst(ASSIGNMENT, "").trim();
    }
}
