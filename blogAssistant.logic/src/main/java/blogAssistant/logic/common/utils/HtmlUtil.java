package blogAssistant.logic.common.utils;

import org.springframework.util.StringUtils;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class HtmlUtil {

    public static String getCharsetFromHtml(String html) {
        String charset = RegexUtil.findByRegexGroup(html, "(?s)<meta.*?charset\\s*=\\s*([\\w-]+)", 1);
        if(StringUtils.hasText(charset)) {
            return charset;
        }
        charset = RegexUtil.findByRegexGroup(html, "(?s)encoding=['\"]([\\w-]+)['\"]", 1);
        if(StringUtils.hasText(charset)) {
            return charset;
        }
        return null;
    }

}