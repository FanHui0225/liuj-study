package com.stereo.study.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuj-ai on 2022/5/6.
 */
public class Parser {


    public static String parse(String openToken, String closeToken, String text, Map<String, String> args) {
        if (args == null || args.isEmpty()) {
            return text;
        }
        if (text == null || text.isEmpty()) {
            return "";
        }
        char[] src = text.toCharArray();
        int offset = 0;
        int start = text.indexOf(openToken, offset);
        if (start == -1) {
            return text;
        }
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                if (end == -1) {
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    String value;
                    if (args.containsKey(expression.toString())) {
                        value = args.get(expression.toString());
                    } else {
                        value = "";
                    }
                    builder.append(value);
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

    public static String parse0(String text, Map<String, String> args) {
        return Parser.parse("${", "}", text, args);
    }

    public static void main(String[] args) {
        String input = "sentry-op-${product}-${app}";
        System.out.println(parse0(input, new HashMap<String, String>() {
            {
                put("product", "1");
                put("app", "1");
            }
        }));
    }
}
