package blogAssistant.logic.poster;

import blogAssistant.logic.common.IBlogPoster;
import blogAssistant.logic.common.utils.MetaweblogPoster;
import blogAssistant.logic.common.utils.URLUtil;
import blogAssistant.logic.model.Blog;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class MetaWeblogBlogPoster implements IBlogPoster {
    private static Logger logger = LoggerFactory.getLogger(MetaWeblogBlogPoster.class);

    private String username;
    private String password;
    private String blogUrl;
    private MetaweblogPoster poster = null;
    private boolean isInit = false;

    private String[] categories = new String[0];

    public MetaWeblogBlogPoster() {
    }

    public MetaWeblogBlogPoster(String blogUrl, String username, String password) {
        super();
        this.blogUrl = blogUrl;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String... categories) {
        Assert.notNull(categories, "categories must be not null");
        this.categories = categories;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public void init() {
        try {
            String editURI = getEditURI(blogUrl);
            if (StringUtils.isNotBlank(editURI)) {
                logger.info("found blog EditURI:" + editURI);

                Map<String, String> map = getMetaWebLogAPIAttributes(editURI);
                String apiLink = StringUtils.trim(map.get("apilink"));
                String blogId = StringUtils.trim(map.get("blogid"));
                logger.info("found MetalogAPI map:" + map);
                poster = new MetaweblogPoster(URLUtil.newURL(apiLink), blogId);
            } else {
                throw new RuntimeException("not found EditURI by BlogURL:" + blogUrl);
            }
            isInit = true;
        } catch (IOException e) {
            throw new RuntimeException("error on init blogURI:" + blogUrl, e);
        }
    }

    private static Map getMetaWebLogAPIAttributes(String editURI) throws IOException {
        Document doc = getDocument(editURI);
        Elements elms = doc.getElementsByTag("api");
        if (elms.isEmpty()) {
            throw new RuntimeException("not found api");
        }
        for (Element e : elms) {
            Map<String, String> map = toLowerCaseMap(e.attributes());
            if (map.get("name").equalsIgnoreCase("MetaWeblog")) {
                return map;
            }
        }
        throw new RuntimeException("not found api");
    }

    private static Map toLowerCaseMap(Attributes attributes) {
        Map map = new HashMap();
        for (Attribute a : attributes.asList()) {
            map.put(StringUtils.lowerCase(a.getKey()), a.getValue());
        }
        return map;
    }

    private static String getEditURI(String blogUrl) throws IOException {
        Document doc = getDocument(blogUrl);
        Elements elms = doc.getElementsByAttributeValue("rel", "EditURI");
        for (Element e : elms) {
            String result = e.attr("href");
            if (result.startsWith("http:")) {
                return result;
            } else {
                return blogUrl + "/" + result;
            }
        }
        return null;
    }

    private static Document getDocument(String blogUrl) throws IOException {
        Connection conn = Jsoup.connect(blogUrl);
        conn.userAgent(MetaweblogPoster.WINDOWS_LIVE_WRITER_UserAgent);
        conn.timeout(1000 * 6);
        Document doc = conn.get();
        return doc;
    }

    @Override
    public void  postBlog(Blog blog) {
        if (!isInit) {
            init();
        }
        String username = StringUtils.defaultIfBlank(blog.getUsername(), this.username);
        String password = StringUtils.defaultIfBlank(blog.getPassword(), this.password);

        if (ArrayUtils.isEmpty(blog.getCategories())) {
            blog.setCategories(categories);
        }

        poster.newPost(username, password, blog);

    }
}


