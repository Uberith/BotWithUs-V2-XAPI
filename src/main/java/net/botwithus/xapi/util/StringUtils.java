package net.botwithus.xapi.util;

public class StringUtils {
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        input = input.toLowerCase();
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    public static boolean isNumeric(String string) {
        return string.matches("-?\\d+");
    }
}