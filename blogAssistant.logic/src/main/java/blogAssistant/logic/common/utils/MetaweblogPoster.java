package blogAssistant.logic.common.utils;

import blogAssistant.logic.model.Blog;
import blogAssistant.logic.model.metaweblog.FileData;
import blogAssistant.logic.model.metaweblog.WpCategory;
import blogAssistant.logic.poster.utils.CustomTypeFactoryImpl;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.net.ssl.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class MetaweblogPoster {
    private static Logger logger = LoggerFactory.getLogger(MetaweblogPoster.class);
    public static String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Windows Live Writer 1.0)";

    private String appKey;
    private URL blogServerUrl;
    private String blogAccountId = null;//默认值：default
    private String userName;
    private String password;
    private XmlRpcClient blogClient;

    public MetaweblogPoster(String appKey, URL blogServerUrl, String blogAccountId, String userName, String password) {
        this.appKey = appKey;
        this.blogServerUrl = blogServerUrl;
        this.blogAccountId = blogAccountId;
        this.userName = userName;
        this.password = password;
        this.initBlogClient();
    }

    /**
     * 初始化博客发送客户端
     */
    private void initBlogClient() {
        if (this.blogServerUrl != null) {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setUserAgent(USER_AGENT);
            config.setServerURL(this.blogServerUrl);
            if (blogServerUrl.getProtocol().toLowerCase().startsWith("https")) {
                try {
                    initSSL();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.blogClient = new XmlRpcClient();
            this.blogClient.setTypeFactory(new CustomTypeFactoryImpl(this.blogClient));
            blogClient.setConfig(config);
        }
    }

    /**
     * 获取用户的博客账号
     *
     * @return
     */
    public Object[] getUsersBlogs() {
        try {
            Object[] array = (Object[]) this.blogClient.execute("blogger.getUsersBlogs", new Object[]{appKey, userName, password});
            return array;
        } catch (XmlRpcException e) {
            throw new RuntimeException("blogger.getUsersBlogs error,userName:" + userName + " url:" + blogServerUrl, e);
        }
    }

    /*
     * @userName 发博文时需要的用户名
     * @password 发博文时需要的密码
     * @blogServerUrl 发博文时对应的metaweblog url
     *
     * @blog 要发送的博文对象,它存储了博文的标题,分类,标签,内容等信息
     */
    public String newPost(Blog blog, boolean publish) {
        Assert.notNull(blog, "blog must be not null");
        Assert.notNull(blog.getCategories(), "blog.getCategories() must be not null");

        Map<String, Object> post = blog.toPost();
        Object[] params = new Object[]{blogAccountId, userName, password, post, publish};
        try {
            // 发布新博文
            String result = String.valueOf(this.blogClient.execute("metaWeblog.newPost", params));
            logger.info("Created_with_blogid " + result + " on rpc url:" + blogServerUrl);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("fail newPost:" + blog.getTitle() + " userName:" + userName + " url:" + blogServerUrl + " \n", e);
        }
    }


    /**
     * 新建一个博客分类
     *
     * @param category
     * @return
     */
    public Integer newCategory(WpCategory category) {
        Object[] params = new Object[]{blogAccountId, userName, password, category};
        try {
            Object o = this.blogClient.execute("wp.newCategory", params);
            logger.info("Create Category success:" + o);
            return (Integer) o;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1;
        }

    }

    /**
     * 删除博客文章
     *
     * @param postId
     * @param publish
     * @return
     */
    public Boolean deletePost(String postId, boolean publish) {
        try {
            Object[] params = new Object[]{appKey, postId, userName, password, publish};
            Object o = this.blogClient.execute("blogger.deletePost", params);
            return (Boolean) o;
        } catch (Exception e) {
            logger.error("[deletePost] " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据Id获取博客文章
     *
     * @param postId
     * @return
     */
    public Map<String, Object> getPost(String postId) {
        try {
            Object[] params = new Object[]{postId, userName, password};
            Object o = this.blogClient.execute("metaWeblog.getPost", params);
            Map postMap = (Map) o;
            return postMap;
        } catch (Exception e) {
            logger.error("[getPost] " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取最近发表的博客文章列表
     *
     * @param number
     * @return
     */
    public Object[] getRecentPosts(int number) {
        try {
            Object[] params = new Object[]{blogAccountId, userName, password, number};
            Object o = this.blogClient.execute("metaWeblog.getRecentPosts", params);
            return (Object[]) o;
        } catch (Exception e) {
            logger.error("[getRecentPosts] " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 上传夹在博客中的图片
     * only allow (\.jpg|\.gif|\.png|\.jpeg)$
     *
     * @param file
     * @return
     */
    public Map newMediaObject(FileData file) {
        try {
            Object[] params = new Object[]{blogAccountId, userName, password, file};
            Object o = this.blogClient.execute("metaWeblog.newMediaObject", params);
            return (Map) o;
        } catch (Exception e) {
            logger.error("[newMediaObject] " + e.getMessage(), e);
            return null;
        }
    }

    /*
     * 此方法用于获取博客网站支持的日志分类,根据xml-rpc定义,获取回来的对象实际上是一个Object数组
     * 每个数组里面包含的是一个HashMap,这个HashMap中存储的是blogcategory的信息
     */
    public Object[] getCategories()
            throws MalformedURLException, XmlRpcException {
        Object[] params = new Object[]{blogAccountId, userName, password};
        Object[] resultObj = (Object[]) this.blogClient.execute("metaWeblog.getCategories", params);
        return resultObj;
    }

    private static void initSSL() throws Exception {
        // Hinweis: Die folgenden Zeilen Code sind nur fuer Testzwecke gedacht.
        // Im produktiven Einsatz sollte man selbstverstaendlich pruefen, ob
        // die Zertifikate in Ordnung sind und ob der Hostname des Rechners
        // mit dem des Zertifikates uebereinstimmt. Um den Code hier jedoch
        // zu verkuerzen, akzeptieren wir alle Zertifikate.

        // Wenn SSL aktiv ist, muessen wir dem SSL-Zertifikat von Jameica vertrauen.
        // Da Java das nicht von allein macht, muessen wir das tun: Wir implementieren
        // hierzu einen TrustManager, der alle Zertifikate akzeptiert und verwenden
        // diesen, um den SSLContext zu initialisieren.
        TrustManager trustAll = new X509TrustManager() {
            /**
             * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
             */
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            /**
             * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
             */
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            /**
             * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
             */
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };

        // Initialisieren des SSLContext mit unserem eigenen TrustManager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{trustAll}, new SecureRandom());

        // Jetzt muessen wir Java noch mitteilen, dass fuer HTTPS-Verbindungen
        // bitte unser SSLContext verwendet werden soll.
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Und als ob das alles noch nicht geung ist, muss der Hostname, der
        // in dem Zertifikat steht auch noch mit dem tatsaechlichen Hostnamen
        // des Rechners uebereinstimmen. Fuer diesen Fall kann man auch
        // noch den HostnameVerifier in Java ersetzen, damit dieser auch
        // dann Zertifikate akzeptiert, wenn der Hostname nicht mit dem
        // CN-Namen des Zertifikats uebereinstimmt.

        HostnameVerifier verifier = new HostnameVerifier() {

            public boolean verify(String arg0, SSLSession arg1) {
                // Wir sagen immer ja.
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(verifier);
    }


}
