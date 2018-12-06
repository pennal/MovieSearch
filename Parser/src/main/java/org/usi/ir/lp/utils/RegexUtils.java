package org.usi.ir.lp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static List<String> executeRegex(String pattern, String body, boolean withDotAll) {
        Pattern p = withDotAll ? Pattern.compile(pattern, Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE) : Pattern.compile(pattern);
        Matcher matcher = p.matcher(body);
        List<String> captureGroups = new ArrayList<>();

        while (matcher.find()) {
            // groupCount does not return the full match, so we need to iterate an extra step
            for (int i = 0; i <= matcher.groupCount(); i++) {
                String capture = matcher.group(i);
                captureGroups.add(capture);
            }
        }
        return captureGroups;
    }
}
