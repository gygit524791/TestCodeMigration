package com.test.migration.service.translate.replace;

import java.util.List;

public class MethodInvocationLfPrimaryReplace {

    /**
     * '.' typeArguments? Identifier '(' argumentList? ')'
     *
     * @param originals
     * @return
     */
    public String replaceStructure1(List<String> originals) {
        String typeArguments = originals.get(1);
        String methodNameIdentifier = originals.get(3);
        String argumentList = originals.get(5);

        return "->" + typeArguments + methodNameIdentifier + "(" + argumentList + ")";
    }
}
