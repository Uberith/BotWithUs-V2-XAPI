package net.botwithus.xapi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class Regex {
    private static final Pattern NULL_PATTERN;
    private static final Pattern ITEM_NAME_PATTERN = Pattern.compile("(.*?)(?:|\\s\\()(?:.(?!\\s\\())+$");

    static {
        NULL_PATTERN = Pattern.compile("");
    }

    public static Pattern getPatternForExactString(String string) {
        if (string == null) {
            return NULL_PATTERN;
        } else {
            return Pattern.compile("^(" + escapeSpecialCharacters(string) + ")$");
        }
    }

    private static String escapeSpecialCharacters(String string) {
        StringBuilder escaped_string = new StringBuilder();
        char[] chars = string.toCharArray();
        int charLength = chars.length;

        for (int i = 0; i < charLength; ++i) {
            char c = chars[i];
            if (c == '(' || c == ')' || c == '^' || c == '$' || c == '.' || c == '*' || c == '?' || c == '|' || c == '[' || c == '{' || c == '+') {
                escaped_string.append('\\');
            }

            escaped_string.append(c);
        }

        return escaped_string.toString();
    }

    public static Pattern getPatternForContainsString(String string) {
        if (string == null) {
            return NULL_PATTERN;
        } else {
            return Pattern.compile(".*" + escapeSpecialCharacters(string) + ".*");
        }
    }

    public static Pattern getPatternForExactStrings(String... strings) {
        StringBuilder pattern_builder = (new StringBuilder()).append("^(");
        boolean first = true;
        String[] stringArray = strings;
        int stringsLength = strings.length;

        for (int i = 0; i < stringsLength; ++i) {
            String s = stringArray[i];
            if (first) {
                first = false;
            } else {
                pattern_builder.append('|');
            }

            pattern_builder.append(escapeSpecialCharacters(s));
        }

        String regex = pattern_builder.append(")$").toString();
        return Pattern.compile(regex);
    }

    public static Pattern getPatternForContainingOneOf(String... strings) {
        StringBuilder pattern_builder = (new StringBuilder()).append('^');
        boolean first = true;
        String[] stringArray = strings;
        int stringsLength = strings.length;

        for (int i = 0; i < stringsLength; ++i) {
            String s = stringArray[i];
            if (first) {
                first = false;
            } else {
                pattern_builder.append('|');
            }

            pattern_builder.append(".*").append(escapeSpecialCharacters(s)).append(".*");
        }

        return Pattern.compile(pattern_builder.append('$').toString());
    }

    public static List<Pattern> getPatternsForExactStrings(String... strings) {
        List<Pattern> list = new ArrayList(strings.length);
        String[] stringArray = strings;
        int stringLength = strings.length;

        for (int i = 0; i < stringLength; ++i) {
            String string = stringArray[i];
            list.add(getPatternForExactString(string));
        }

        return list;
    }

    public static List<Pattern> getPatternsForContainsStrings(String... strings) {
        List<Pattern> list = new ArrayList(strings.length);
        String[] stringArray = strings;
        int stringsLength = strings.length;

        for (int i = 0; i < stringsLength; ++i) {
            String string = stringArray[i];
            list.add(getPatternForContainsString(string));
        }

        return list;
    }

    public static Pattern getPatternForNotContainingAnyString(String... strings) {
        StringBuilder pattern_builder = (new StringBuilder()).append('^');
        boolean first = true;
        String[] stringArray = strings;
        int stringsLength = strings.length;

        pattern_builder.append("(?!.*(?:");
        for (int i = 0; i < stringsLength; ++i) {
            String s = stringArray[i];
            if (first) {
                first = false;
            } else {
                pattern_builder.append('|');
            }

            pattern_builder.append(escapeSpecialCharacters(s));
        }
        pattern_builder.append(")).*");
        return Pattern.compile(pattern_builder.append('$').toString());
    }

    public static Pattern getPattern(String regex) {
        return Pattern.compile(regex);
    }
}
