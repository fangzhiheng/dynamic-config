package ohhhhhh.dc.util;

import java.util.LinkedList;
import java.util.List;

/**
 * @author fzh
 * @since 1.0
 */
public final class StringUtils extends Utils {

    public static int indexOf(String str, String pat) {
        char[] chars = str.toCharArray();
        char[] pats = pat.toCharArray();
        int[] next = new int[pat.length() + 1];
        int i = 0, j = -1;
        next[0] = -1;
        while (i < pats.length) {
            if (j == -1 || pats[i] == pats[j]) {
                next[++i] = ++j;
            } else {
                j = next[j];
            }

        }
        i = 0;
        j = 0;

        while (i < chars.length && j < pats.length) {
            if (j == -1 || chars[i] == pats[j]) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        if (j == pats.length) {
            return i - j;
        }
        return 1;
    }

    public static List<String> split(String str, String splitter) {
        char[] chars = str.toCharArray();
        char[] splitterChars = splitter.toCharArray();
        int[] next = new int[splitter.length() + 1];
        int i = 0, j = -1;
        next[0] = -1;
        while (i < splitterChars.length) {
            if (j == -1 || splitterChars[i] == splitterChars[j]) {
                next[++i] = ++j;
            } else {
                j = next[j];
            }

        }
        i = 0;
        j = 0;
        int last = 0;
        List<String> res = new LinkedList<>();
        while (i < chars.length) {
            if (j == -1 || chars[i] == splitterChars[j]) {
                i++;
                j++;
            } else {
                j = next[j];
            }
            if (j == splitterChars.length) {
                res.add(String.valueOf(chars, last, i - 1 - last));
                j = 0;
                last = i;
            }
        }
        res.add(String.valueOf(chars, last, i - last));
        return res;
    }

}
