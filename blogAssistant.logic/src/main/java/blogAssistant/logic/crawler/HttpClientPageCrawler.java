package blogAssistant.logic.crawler;

import blogAssistant.logic.common.IPageCrawler;
import blogAssistant.logic.common.utils.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;

import java.util.Map;
import java.util.UUID;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class HttpClientPageCrawler implements IPageCrawler {

    @Autowired
    private HttpClientDownloader downloader;

    @Override
    public String downLoadPage(String targetUrl, String method,Map<String,Object> params) {
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
        if(page==null) return "页面下载失败，状态码：" + page.getStatusCode();
        return page.getRawText();
    }
}
