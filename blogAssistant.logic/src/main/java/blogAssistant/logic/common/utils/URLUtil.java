package blogAssistant.logic.common.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class URLUtil {
    static Logger logger = LoggerFactory.getLogger(URLUtil.class);
    static String LOCALHOST = "localhost";

    public static URL newURL(String url) {
        if(StringUtils.isBlank(url)) {
            return null;
        }
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("MalformedURLException:"+url+" cause:"+e);
        }
    }

    public static String getHostSite(String url) {
        if(StringUtils.isBlank(url)) {
            return LOCALHOST;
        }
        try {
            String host = new URL(url).getHost();
            if(host.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                return LOCALHOST;
            }
            return host;
        }catch(Exception e) {
            logger.error("getHostSite error,url:"+url,e);
            return LOCALHOST;
        }
    }

    public static void assertURL(String url,String message) {
        if(StringUtils.isBlank(url)) {
            return;
        }
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(message+",MalformedURLException:"+url+" cause:"+e);
        }
    }

}