package ohhhhhh.dc.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fzh
 * @since 1.0
 */
public final class PlaceholderUtils extends Utils {

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<ph>\\$\\{(?<phk>[\\w,.\\-_\\d]+)(:(?<default>[\\w.,\\-_\\d/]+))?})");

    private static final int NICE = -1;

    private static final int CONFUSED = 0;

    public static String resolve(String source, Function<String, String> resolver) {
        if (!PLACEHOLDER_PATTERN.matcher(source).find()) return source;
        StringBuilder builder = new StringBuilder();
        char[] chars = source.toCharArray();
        int status = NICE;
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '$':
                    status++;
                    break;
                case '{':
                    if (status == CONFUSED) {
                        status++;
                    } else {
                        throw new IllegalStateException("invalid placeholder string [" + source + "], invalid '{' at position " + (i + 1) + ".");
                    }
                    break;
                case '}':
                    if (status > CONFUSED) {
                        builder.append(resolver.apply(new String(chars, i - status + 1, status - 1)));
                        status = NICE;
                    } else {
                        throw new IllegalStateException("invalid placeholder string [" + source + "], unclosed placeholder.");
                    }
                    break;
                default:
                    if (status == CONFUSED) {
                        throw new IllegalStateException("invalid placeholder string [" + source + "], invalid '{' at position " + i + ".");
                    } else if (status > CONFUSED) {
                        status++;
                    } else {
                        builder.append(chars[i]);
                    }
            }
        }
        if (status != NICE) {
            throw new IllegalStateException("invalid placeholder string [" + source + "], unclosed placeholder.");
        }
        return builder.toString();
    }

    public static String resolveByPattern(String source, Function<String, String> resolver) {
        Matcher matcher;
        while ((matcher = PLACEHOLDER_PATTERN.matcher(source)).find()) {
            source = matcher.replaceFirst(resolver.apply(matcher.group("phk")));
        }
        return source;
    }

    public static boolean containsPlaceholder(String string) {
        return PLACEHOLDER_PATTERN.matcher(string).find();
    }

    public static String getPlaceholder(String string) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(string);
        if (matcher.find()) {
            return matcher.group("phk");
        }
        return null;
    }

    public static String getDefault(String string) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(string);
        if (matcher.find()) {
            return matcher.group("default");
        }
        return null;
    }

}
