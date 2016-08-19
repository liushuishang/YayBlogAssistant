package blogAssistant.logic.common;

import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public interface IPageCrawler {

    /**
     * 下载一个页面
     *
     * @param targetUrl
     * @return 页面的字符串内容
     */
    String downLoadPage(String targetUrl, String method, Map<String, Object> params);

}
