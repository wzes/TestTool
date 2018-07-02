package com.tongji.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Create by xuantang
 * @date on 7/1/18
 */
public class GenerateUrlUtil {
    public String generateUrl(String template, String value) {
        Pattern pattern = Pattern.compile("\\{-?\\w+\\}");
        Matcher matcher = pattern.matcher(template);
        boolean matches = matcher.find();
        try {
            if (!matches || Integer.parseInt(value) < 0) {
                return "error";
            }
        } catch (Exception e) {
            return "error";
        }
        // Generate the url
        return template.replaceAll("\\{[^}]*\\}", value);
    }
}
