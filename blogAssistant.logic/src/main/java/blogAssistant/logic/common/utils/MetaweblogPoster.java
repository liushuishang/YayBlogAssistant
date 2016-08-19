package blogAssistant.logic.common.utils;

import blogAssistant.logic.model.Blog;
import blogAssistant.logic.poster.utils.CustomTypeFactoryImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.XmlRpcWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class MetaweblogPoster {
    public static String WINDOWS_LIVE_WRITER_UserAgent = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Windows Live Writer 1.0)";

    private static Logger logger = LoggerFactory.getLogger(MetaweblogPoster.class);

    private URL blogUrl;
    private XmlRpcClient blogClient;
    private String blogId = null;

    public MetaweblogPoster(URL blogUrl) {
        this(blogUrl, "default");
    }

    public MetaweblogPoster(URL blogUrl, String blogId) {
        this.blogUrl = blogUrl;
        this.blogId = blogId;
        this.initBlogClient();
    }

    public URL getBlogUrl() {
        return blogUrl;
    }

    /*
     * 初始化博客发送客户端
     */
    private void initBlogClient()  {
        if (this.blogUrl != null) {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setUserAgent(WINDOWS_LIVE_WRITER_UserAgent);
            config.setServerURL(this.blogUrl);
            if (blogUrl.getProtocol().toLowerCase().startsWith("https")) {
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

    public Map getUsersBlog(String username, String password) {
        try {
            String appKey = "11111111111xxxxxxx";
            Object[] array = (Object[]) this.blogClient.execute("blogger.getUsersBlogs", new Object[]{appKey, username, password});
            return (Map) array[0];
        } catch (XmlRpcException e) {
            throw new RuntimeException("blogger.getUsersBlogs error,username:" + username + " url:" + blogUrl, e);
        }
    }

    /*
     * @username 发博文时需要的用户名
     * @password 发博文时需要的密码
     * @blogUrl 发博文时对应的metaweblog url
     *
     * @blog 要发送的博文对象,它存储了博文的标题,分类,标签,内容等信息
     */
    public String newPost(String username, String password, Blog blog) {

        Assert.notNull(blog, "blog must be not null");
        Assert.notNull(blog.getCategories(), "blog.getCategories() must be not null");

        // Set up parameters required by newPost method
        Map<String, Object> post = new HashMap<String, Object>();
        post.put("title", blog.getTitle());// 标题
        post.put("categories", blog.getCategories());// 分类
        post.put("description", blog.getContent());// 内容
        post.put("mt_keywords", "");
        post.put("mt_excerpt", "");
        Object[] params = new Object[]{blogId, username, password, post, Boolean.TRUE};

        try {
            // 发布新博文
            String result = String.valueOf(this.blogClient.execute("metaWeblog.newPost", params));
            logger.info("Created_with_blogid " + result + " on rpc url:" + blogUrl);
            return result;
        } catch (Exception e) {
            String xmlString = toXmlString(params);
            throw new RuntimeException("fail newPost:" + blog.getTitle() + " username:" + username + " url:" + blogUrl + " \n" + xmlString, e);
        }
    }

    private String toXmlString(Object[] params) {
        try {
            if (params == null) return null;
            String xmlString = null;
            XmlRpcClientRequestImpl request = new XmlRpcClientRequestImpl(blogClient.getClientConfig(), "metaWeblog.newPost", params);
            ReqWriterImpl reqWriter = new ReqWriterImpl(request);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            reqWriter.write(output);
            xmlString = output.toString();
            return xmlString;
        } catch (Exception e) {
            logger.error("toXmlString() error params:" + StringUtils.join(params, ","), e);
            return "";
        }
    }

    protected class ReqWriterImpl {
        private final XmlRpcRequest request;

        protected ReqWriterImpl(XmlRpcRequest pRequest) {
            request = pRequest;
        }

        /**
         * Writes the requests uncompressed XML data to the given
         * output stream. Ensures, that the output stream is being
         * closed.
         */
        public void write(OutputStream pStream)
                throws XmlRpcException, IOException, SAXException {
            final XmlRpcStreamConfig config = (XmlRpcStreamConfig) request.getConfig();
            try {
                ContentHandler h = blogClient.getXmlWriterFactory().getXmlWriter(config, pStream);
                XmlRpcWriter xw = new XmlRpcWriter(config, h, blogClient.getTypeFactory());
                xw.write(request);
                pStream.close();
                pStream = null;
            } finally {
                if (pStream != null) {
                    try {
                        pStream.close();
                    } catch (Throwable ignore) {
                    }
                }
            }
        }
    }

    /*
     * 此方法用于获取博客网站支持的日志分类,根据xml-rpc定义,获取回来的对象实际上是一个Object数组
     * 每个数组里面包含的是一个HashMap,这个HashMap中存储的是blogcategory的信息
     */
    public Object[] getCategories(String username, String password)
            throws MalformedURLException, XmlRpcException {
        Object[] params = new Object[]{blogId, username, password};
        Object[] resultObj = (Object[]) this.blogClient.execute("metaWeblog.getCategories", params);
        return resultObj;
    }

    /*
     * 打印博客支持的分类信息
     */
    public void printBlogCategory(Object[] blogCategory) {
        if (blogCategory != null) {
            System.out.println("开始打印博客支持的分类");
            for (Object blogCategoryItem : blogCategory) {
                printBlogCategoryItem((HashMap<String, String>) blogCategoryItem);
                System.out
                        .println("-------------------------------------------");
            }
        }
    }

    /*
     * 打印每个blogCategoryItem信息
     * 每个blogCategoryItem由categoryid,title,htmlUrl,description,rssUrl构成
     * 至少cnblogs的博客文章分类是如此
     */
    private void printBlogCategoryItem(HashMap<String, String> blogItem) {
        if (blogItem != null & blogItem.isEmpty() == false) {
            for (String key : blogItem.keySet()) {
                System.out.println(key + ":" + blogItem.get(key));
            }
        } else {
            System.out.println("blogItem is empty!");
        }

    }

    /*
     * 获取博客支持的所有博文分类名,返回的是一个String数组
     */
    public String[] getBlogCategoryTitle(Object[] blogCategory) {
        if (blogCategory != null) {
            ArrayList<String> categoryTitleList = new ArrayList<String>(20);
            for (Object categoryItem : blogCategory) {
                categoryTitleList.add((String) ((HashMap) categoryItem)
                        .get("title"));
            }
            return (String[]) categoryTitleList.toArray();
        }
        return null;
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
