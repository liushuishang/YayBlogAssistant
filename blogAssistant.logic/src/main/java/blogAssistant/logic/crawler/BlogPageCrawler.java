package blogAssistant.logic.crawler;

import blogAssistant.logic.common.IBlogPageCrawler;
import blogAssistant.logic.common.utils.RequestHelper;
import blogAssistant.logic.model.Blog;
import blogAssistant.logic.model.HttpMethod;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.selector.Html;

import java.util.Map;
import java.util.UUID;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
@Component
public class BlogPageCrawler implements IBlogPageCrawler {
    private static HttpClientDownloader downloader=new HttpClientDownloader();

    @Override
    public Blog resolveBlog(String targetUrl, String method, Map<String,Object> params) {
        Request request = RequestHelper.createRequest(targetUrl, method, params);
        Page page = downloader.download(request, new Task() {
            @Override
            public String getUUID() {
                return UUID.randomUUID().toString();
            }

            @Override
            public Site getSite() {
                return Site.me();
            }
        });
        if(page==null) return null;
        Html html = page.getHtml();
        String title = html.regex("<title>(.*?)</title>").get().trim();
        String content = html.smartContent().get();
        content = StringEscapeUtils.escapeHtml4(content);
        return new Blog(title, content);

    }

    @Override
    public Blog resolveBlog(String targetUrl) {
        return resolveBlog(targetUrl, HttpMethod.GET,null);
    }

}
