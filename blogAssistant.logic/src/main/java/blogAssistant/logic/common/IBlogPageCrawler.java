package blogAssistant.logic.common;

import blogAssistant.logic.model.Blog;

import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public interface IBlogPageCrawler {

    /**
     * 抓取一个博客
     *
     * @param targetUrl
     * @return 页面的字符串内容
     */
    Blog resolveBlog(String targetUrl, String method, Map<String, Object> params);

    Blog resolveBlog(String targetUrl);

}
