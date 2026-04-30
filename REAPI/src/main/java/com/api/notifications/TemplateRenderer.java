package com.api.notifications;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateRenderer {

    private static final Pattern VAR = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    
    public static String render(String template, Map<String, String> vars) {
        if (template == null || template.isBlank()) return "";
        if (vars == null || vars.isEmpty()) return template;
        Matcher matcher = VAR.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String value = vars.getOrDefault(matcher.group(1), "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
