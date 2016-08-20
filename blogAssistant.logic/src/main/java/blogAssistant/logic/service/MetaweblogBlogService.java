package blogAssistant.logic.service;

import blogAssistant.logic.common.utils.Base64Util;
import blogAssistant.logic.common.utils.MetaweblogPoster;
import blogAssistant.logic.common.utils.URLUtil;
import blogAssistant.logic.model.Blog;
import blogAssistant.logic.model.metaweblog.*;
import org.apache.commons.beanutils.BeanUtils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class MetaweblogBlogService {
    private static Logger logger = LoggerFactory.getLogger(MetaweblogBlogService.class);

    /**
     * 获取用户博客平台的地址
     * 如：http://www.cnblogs.com/lhdwr/
     *
     * @return
     */
    private String blogServerUrl;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;

    /**
     * 区分同类型的不同的博客平台
     */
    private String appKey;

    private MetaweblogPoster myPoster = null;


    public static void main(String[] args) {
        String blogAddress = "http://www.cnblogs.com/lhdwr/";
        String userName = "lhdwr";
        String password = "lhdwr@123";

        MetaweblogBlogService blogService = new MetaweblogBlogService("cnblog", blogAddress, userName, password);

        //获取博客帐号
//        List<BlogAccount> blogAccountList = blogService.getUsersBlogs();
//        BlogAccount blogAccount = blogAccountList.get(0);

        //上传文件
//        System.setProperty("http.proxyHost", "localhost");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("https.proxyHost", "localhost");
//        System.setProperty("https.proxyPort", "8888");
        FileData fileData = new FileData();
        fileData.setName("测试图片" + System.currentTimeMillis() + ".jpg");
        fileData.setType(FileMIME.JPEG);
        fileData.setBits(Base64Util.convertFileToBase64("http://s1.dwstatic.com/group1/M00/E4/2A/68f52a7c2a8c7012c4fd6e8594674478.jpg"));
        UrlData urlData = blogService.newMediaObject(fileData);


        //获取博客分类
//        List<CategoryInfo> categories = blogService.getCategories(blogAccount.getBlogid());
        //新建博客分类
//        blogService.newCategory(blogAccount.getBlogid(),"转载"+System.currentTimeMillis(),"转载的美文");
        //发表博客
        Blog blog = new Blog("测试博客" + System.currentTimeMillis(), "我是一只小小小小鸟<img src='" + urlData.getUrl() + "' />");
        blog.setCategories("默认分类");
        blog.setCategories(new String[]{"转载1471686768262"});//已经存在的分类的名称
        String postId = blogService.newPost(blog, true);
        //获取博客
//        Map<String, Object> post = blogService.getPost(postId);
        //获取最忌发表的博客
//        Object[] postList = blogService.getRecentPosts(5);
        //删除博客
//        blogService.deletePost(postId, false);

    }


    public MetaweblogBlogService(String appKey, String blogServerUrl, String userName, String password) {
        this.blogServerUrl = blogServerUrl;
        this.appKey = appKey;
        this.userName = userName;
        this.password = password;

        if (blogServerUrl == null) throw new RuntimeException("BlogServerUrl can not be null!");
        try {
            String editURI = getEditURI(blogServerUrl);
            if (StringUtils.isNotBlank(editURI)) {
                logger.info("found blog EditURI:" + editURI);

                Map<String, String> map = getMetaWebLogAPIAttributes(editURI);
                String apiLink = StringUtils.trim(map.get("apilink"));
                String blogAccountId = StringUtils.trim(map.get("blogid"));
                logger.info("found MetalogAPI map:" + map);
                myPoster = new MetaweblogPoster(appKey, URLUtil.newURL(apiLink), blogAccountId, userName, password);
            } else {
                throw new RuntimeException("not found EditURI by BlogURL:" + blogServerUrl);
            }
        } catch (IOException e) {
            throw new RuntimeException("error on init blogURI:" + blogServerUrl, e);
        }

    }


    /**
     * 删除一条博客
     *
     * @param postId
     * @param publish 删除后是否存为草稿
     * @return
     */
    public boolean deletePost(String postId, boolean publish) {
        return myPoster.deletePost(postId, publish);
    }

    /**
     * 获取某个用户在该博客平台的博客账号列表
     *
     * @return
     */
    public List<BlogAccount> getUsersBlogs() {
        Object[] blogObjectList = myPoster.getUsersBlogs();
        if (blogObjectList == null) return null;
        List<BlogAccount> blogAccountList = new ArrayList<>();
        for (Object o : blogObjectList) {
            BlogAccount blogAccount = new BlogAccount();
            try {
                BeanUtils.populate(blogAccount, (Map) o);
            } catch (Exception e) {
                continue;
            }
            blogAccountList.add(blogAccount);
        }
        return blogAccountList;
    }

    /**
     * 更新一个存在的博客
     *
     * @param postId
     * @param userName
     * @param password
     * @param post
     * @param publish
     * @return
     */
    public boolean editPost(String postId, String userName, String password, Post post, boolean publish) {
        return false;
    }

    /**
     * 获取某个博客账户拥有的分类
     *
     * @return
     */
    public List<CategoryInfo> getCategories() {
        try {
            Object[] categories = myPoster.getCategories();
            List<CategoryInfo> categoryInfoList = new ArrayList<>(categories.length);
            for (Object o : categories) {
                CategoryInfo categoryInfo = new CategoryInfo();
                BeanUtils.populate(categoryInfo, (Map<String, ? extends Object>) o);
                categoryInfoList.add(categoryInfo);
            }
            return categoryInfoList;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * 获取某个发表的博客
     *
     * @param postId
     * @return
     */
    public Map<String, Object> getPost(String postId) {
        return myPoster.getPost(postId);
    }

    /**
     * 获取最近的博客发表记录
     *
     * @param number
     * @return
     */
    public Object[] getRecentPosts(int number) {
        return myPoster.getRecentPosts(number);
    }

    /**
     * Makes a new file to a designated blog using the metaWeblog API. Returns url as a string of a struct.
     * only allow (\.jpg|\.gif|\.png|\.jpeg)$
     *
     * @param file
     * @return
     */
    public UrlData newMediaObject(FileData file) {
        Map d = myPoster.newMediaObject(file);
        UrlData urlData = new UrlData();
        try {
            BeanUtils.populate(urlData, d);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
        return urlData;
    }

    /**
     * Makes a new post to a designated blog using the metaWeblog API. Returns postid as a string.
     *
     * @param publish 立即发表还是存为草稿
     * @return
     */
    public String newPost(Blog blog, boolean publish) {
        return myPoster.newPost(blog, publish);
    }

    /**
     * Create a new category
     *
     * @param blogId
     * @return
     */
    public Integer newCategory(String blogId, String categoryName, String categoryDescription) {
        WpCategory category = new WpCategory(categoryName, categoryDescription);
        return myPoster.newCategory(category);
    }


    /***************************************
     * 工具方法
     ****************************************/
    private Document getDocument(String blogUrl) throws IOException {
        Connection conn = Jsoup.connect(blogUrl);
        conn.userAgent(MetaweblogPoster.USER_AGENT);
        conn.timeout(1000 * 6);
        Document doc = conn.get();
        return doc;
    }

    private String getEditURI(String blogUrl) throws IOException {
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

    private Map getMetaWebLogAPIAttributes(String editURI) throws IOException {
        Document doc = getDocument(editURI);
        Elements elms = doc.getElementsByTag("api");
        if (elms.isEmpty()) {
            throw new RuntimeException("not found api");
        }
        for (Element e : elms) {
            Attributes attributes = e.attributes();
            if (attributes == null) continue;
            if (attributes.get("name").equalsIgnoreCase("MetaWeblog")) {
                Map map = new HashMap();
                for (Attribute a : attributes.asList()) {
                    map.put(org.apache.commons.lang3.StringUtils.lowerCase(a.getKey()), a.getValue());
                }
                return map;
            }
        }
        throw new RuntimeException("not found api");
    }

}
