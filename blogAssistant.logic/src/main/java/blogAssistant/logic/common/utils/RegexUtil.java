package blogAssistant.logic.common.utils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class RegexUtil {
    private static WeakHashMap<String, Pattern> regexCache = new WeakHashMap<String, Pattern>();

    public static String findByRegexGroup(String input,String regex,int regexGroup) {
        if(input == null) return null;
        if(regex == null || regex.isEmpty()) throw new IllegalArgumentException("'regex' must be not null");
        Pattern p = getPatternFromCache(regex);

        Matcher m = p.matcher(input);
        if(m.find()) {
            return m.group(regexGroup);
        }
        return null;
    }


    public static Set<String> findAllByRegexGroup(String input, String regex, int regexGroup) {
        if(input == null) return Collections.EMPTY_SET;
        if(regex == null || regex.isEmpty()) throw new IllegalArgumentException("'regex' must be not null");
        Pattern p = getPatternFromCache(regex);

        Matcher m = p.matcher(input);
        Set<String> result = new LinkedHashSet();
        while(m.find()) {
            result.add(m.group(regexGroup));
        }
        return result;
    }


    public static Pattern getPatternFromCache(String regex) {
        Pattern p = regexCache.get(regex);
        if(p == null) {
            p = Pattern.compile(regex);
            synchronized (regexCache) {
                regexCache.put(regex, p);
            }
        }
        return p;
    }
}
