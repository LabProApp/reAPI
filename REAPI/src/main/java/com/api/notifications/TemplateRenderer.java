package com.api.notifications;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that renders notification template strings by substituting
 * {@code {{varName}}} placeholders with values from a provided variable map.
 *
 * <p>Placeholders follow the pattern {@code {{word}}} where {@code word} consists of
 * word characters ({@code \w+}). Any placeholder whose key is absent from the variable
 * map is replaced with an empty string.
 *
 * <p>This class is stateless and all methods are static; it is not intended to be instantiated.
 */
public class TemplateRenderer {

    private static final Pattern VAR = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    /**
     * Renders a template string by replacing all {@code {{varName}}} placeholders with
     * the corresponding values from the {@code vars} map.
     *
     * <p>If the template is {@code null} or blank, an empty string is returned.
     * If {@code vars} is {@code null} or empty, the template is returned unchanged.
     * Missing keys are substituted with an empty string.
     *
     * @param template the template string containing zero or more {@code {{varName}}} placeholders
     * @param vars     a map of variable names to their replacement values
     * @return the rendered string with all placeholders substituted
     */
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
