package blogAssistant.logic.model.metaweblog;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class BlogAccount {
    private String blogid;
    private String url;
    private String blogName;

    public String getBlogid() {
        return blogid;
    }

    public void setBlogid(String blogid) {
        this.blogid = blogid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }
}
