package blogAssistant.logic.poster;

import blogAssistant.logic.common.IBlogPoster;
import blogAssistant.logic.common.utils.HttpClientGenerator;
import blogAssistant.logic.model.Blog;
import blogAssistant.logic.poster.utils.BlogPosterHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public abstract class BaseBlogPoster implements IBlogPoster, InitializingBean {
    static Logger logger = LoggerFactory.getLogger(BaseBlogPoster.class);

    private BlogPosterHelper helper = new BlogPosterHelper();
    private String loginUrl = null;
    private String postNewBlogUrl = null;
    private String postNewBlogContentType = null;

    private Map<String, String> postNewBlogHeaders = new HashMap<String, String>();
    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
    private HttpClient client = httpClientGenerator.generateClient();

    {
//  client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//  CookiePolicy.registerCookieSpec("accept_all", AcceptAllCookieSpecBase.class);
//  client.getParams().setCookiePolicy("accept_all");
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = StringUtils.trim(loginUrl);
    }

    public String getPostNewBlogUrl() {
        return postNewBlogUrl;
    }

    public void setPostNewBlogUrl(String postNewBlogUrl) {
        this.postNewBlogUrl = StringUtils.trim(postNewBlogUrl);
    }

    public void setPostNewBlogHeaders(Map<String, String> postNewBlogHeaders) {
        this.postNewBlogHeaders = postNewBlogHeaders;
    }

    @Override
    public void postBlog(Blog blog) {
        try {
            List<BasicClientCookie> cookies = login(blog.getUsername(), blog.getPassword());
            postNewBlog(blog.getTitle(), blog.getContent(), blog.getMetaDescription(), blog.getExt(), cookies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<BasicClientCookie> login(String username, String password) throws Exception, IOException {
        Assert.hasText(getLoginUrl(), "loginUrl must be not empty");
        HttpPost post = helper.newHttpPost(getLoginUrl());
        try {
            logger.info("start login with username:" + username + " loginUrl:" + getLoginUrl());
            setLoginRequestBody(username, password, post);
            HttpResponse response = client.execute(post);

//            //读取cookie并保存文件
//            List<Cookie> cookies =((CloseableHttpClient)client).getCookies();

            InputStream stream = response.getEntity().getContent();
            String responseString = IOUtils.toString(stream, "UTF-8");
            verifyHttpStatusCode(response.getStatusLine().getStatusCode(), "login error,username:" + username + " response:" + responseString);
            Assert.isTrue(verifyLoginResult(responseString), "login error,username:" + username + " response:" + responseString);
            logger.info("login_success with username:" + username + " loginUrl:" + getLoginUrl());

            return fromSetCookieHeader(response.getHeaders("Set-Cookie"));
        } finally {
            post.releaseConnection();
        }
    }

    private static List<BasicClientCookie> fromSetCookieHeader(Header[] responseHeaders) {
        List<BasicClientCookie> cookies = new ArrayList<BasicClientCookie>();
        for (Header h : responseHeaders) {
            BasicClientCookie cookie = parseRawCookie(h.getValue());
            cookies.add(cookie);
        }
        return cookies;
    }


    private static BasicClientCookie parseRawCookie(String rawCookie) {
        String[] rawCookieParams = rawCookie.split(";");

        String[] rawCookieNameAndValue = splitFirst(rawCookieParams[0], '=');
        if (rawCookieNameAndValue.length < 2) {
            throw new RuntimeException("Invalid cookie: missing name and value. " + rawCookieParams[0] + " raw cookie:" + rawCookie);
        }

        String cookieName = rawCookieNameAndValue[0].trim();
        String cookieValue = rawCookieNameAndValue[1].trim();
        BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
        for (int i = 1; i < rawCookieParams.length; i++) {
            String rawCookieParamNameAndValue[] = rawCookieParams[i].trim().split("=");

            String paramName = rawCookieParamNameAndValue[0].trim();

            if (rawCookieParamNameAndValue.length == 1) {
                continue;
            }

            if (paramName.equalsIgnoreCase("secure")) {
                cookie.setSecure(true);
            } else {
                String paramValue = rawCookieParamNameAndValue[1].trim();

                if (paramName.equalsIgnoreCase("expires")) {
                    cookie.setExpiryDate(new Date(paramValue));
                } else if (paramName.equalsIgnoreCase("max-age")) {
                    long maxAge = Long.parseLong(paramValue);
                    Date expiryDate = new Date(System.currentTimeMillis() + maxAge);
                    cookie.setExpiryDate(expiryDate);
                } else if (paramName.equalsIgnoreCase("domain")) {
                    cookie.setDomain(paramValue);
                } else if (paramName.equalsIgnoreCase("path")) {
                    cookie.setPath(paramValue);
                } else if (paramName.equalsIgnoreCase("comment")) {
                    cookie.setPath(paramValue);
                } else {
                    throw new RuntimeException("Invalid cookie: invalid attribute name. cookie name:" + paramName);
                }
            }
        }

        return cookie;
    }

    private static String[] splitFirst(String str, char seperator) {
        int index = str.indexOf(seperator);
        if (index >= 0) {
            return new String[]{str.substring(0, index), str.substring(index + 1, str.length())};
        }
        return new String[]{str};
    }


    private static String toRequestHeaderCookieString(List<BasicClientCookie> cookies) {
        String tmpcookies = "";
        for (BasicClientCookie c : cookies) {
            tmpcookies = tmpcookies + c.getName() + "=" + c.getValue() + "; ";
        }
        return tmpcookies;
    }

    public static void verifyHttpStatusCode(int statusCode, String attachErrorMessage) {
        if (statusCode >= 200 && statusCode < 400) {
            return;
        }
        throw new IllegalStateException("error http statusCode:" + statusCode + "; " + attachErrorMessage);
    }

    protected void postNewBlog(String title, String content, String metaDescription, Map<String, String> ext, List<BasicClientCookie> cookies) throws Exception, IOException, HttpException {
        Assert.hasText(getPostNewBlogUrl(), "postNewBlogUrl must be not empty");
        HttpPost post = helper.newHttpPost(getPostNewBlogUrl());
        if (StringUtils.isNotBlank(getPostNewBlogContentType())) {
            post.setHeader("Content-Type", getPostNewBlogContentType());
        }
        for (String key : postNewBlogHeaders.keySet()) {
            post.setHeader(key, postNewBlogHeaders.get(key));
        }

        String cookieHeader = toRequestHeaderCookieString(cookies);
        post.setHeader("Cookie", cookieHeader);
        logger.debug("postNewBlog() cookieHeader:" + cookieHeader);
        try {
            logger.info("start postNewBlog:" + title + " url:" + getPostNewBlogUrl());
            setPostNewBlogRequestBody(title, content, metaDescription, ext, post);
            HttpResponse response = client.execute(post);

//设置编码
//            String charset = StringUtils.defaultIfBlank(HtmlUtil.getCharsetFromHtml(response.getEntity().getContent()), "UTF-8");
            String charset = "UTF-8";
            InputStream stream = response.getEntity().getContent();
            String responseString = IOUtils.toString(stream, StringUtils.defaultIfBlank(charset, "UTF-8"));
            stream.close();
            verifyHttpStatusCode(response.getStatusLine().getStatusCode(), "post new blog error,blog title:" + title);
            Assert.isTrue(verifyPostNewBlogResult(responseString), "post blog error,title:" + title + " response:" + responseString);
            logger.info("postNewBlog_success:" + title + " url:" + getPostNewBlogUrl());
        } finally {
            post.releaseConnection();
        }
    }

    public String getPostNewBlogContentType() {
        return postNewBlogContentType;
    }

    public void setPostNewBlogContentType(String postNewBlogContentType) {
        this.postNewBlogContentType = postNewBlogContentType;
    }

    /**
     * 验证登录是否成功
     * @param responseString
     * @return
     */
    protected boolean verifyLoginResult(String responseString) {
        return true;
    }

    protected abstract void setLoginRequestBody(String username, String password, HttpPost post) throws Exception;

    protected boolean verifyPostNewBlogResult(String responseString) {
        return true;
    }

    protected abstract void setPostNewBlogRequestBody(String title, String content, String metaDescription, Map<String, String> ext, HttpPost post) throws Exception;

    public static String urlEncode(String content)
            throws UnsupportedEncodingException {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return URLEncoder.encode(content, "UTF-8");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(getLoginUrl(), "loginUrl must be not empty");
        Assert.hasText(getPostNewBlogUrl(), "postNewBlogUrl must be not empty");

        logger.info("loginUrl:" + loginUrl);
        logger.info("postNewBlogUrl:" + postNewBlogUrl);
    }


}
