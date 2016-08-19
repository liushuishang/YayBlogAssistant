package blogAssistant.logic.model.metaweblog;

import java.util.Date;
import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class Post {

    /**
     * dateCreated - Required when posting.
     */
    private Date dateTime;

    /**
     * Required when posting.
     */
    private String description;

    /**
     * Required when posting.
     */
    private String title;

    /**
     * 可选
     */
    private List<String> categories;

    /**
     * 可选
     */
    private Enclosure enclosure;

    /**
     * 可选
     */
    private String link;

    /**
     * 可选
     *
     * @return
     */
    private String permalink;
    /**
     * 可选
     *
     * @return
     */
    private String postid;
    /**
     * 可选
     *
     * @return
     */
    private Source source;
    /**
     * 可选
     *
     * @return
     */
    private String userid;
    /**
     * 可选
     *
     * @return
     */
    boolean mt_allow_comments;
    /**
     * 可选
     *
     * @return
     */
    boolean mt_allow_pings;
    /**
     * 可选
     *
     * @return
     */
    boolean mt_convert_breaks;
    /**
     * 可选
     *
     * @return
     */
    private String mt_text_more;
    /**
     * 可选
     *
     * @return
     */
    private String mt_excerpt;
    /**
     * 可选
     *
     * @return
     */
    private String mt_keywords;
    /**
     * 可选
     *
     * @return
     */
    private String wp_slug;


    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Enclosure getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isMt_allow_comments() {
        return mt_allow_comments;
    }

    public void setMt_allow_comments(boolean mt_allow_comments) {
        this.mt_allow_comments = mt_allow_comments;
    }

    public boolean isMt_allow_pings() {
        return mt_allow_pings;
    }

    public void setMt_allow_pings(boolean mt_allow_pings) {
        this.mt_allow_pings = mt_allow_pings;
    }

    public boolean isMt_convert_breaks() {
        return mt_convert_breaks;
    }

    public void setMt_convert_breaks(boolean mt_convert_breaks) {
        this.mt_convert_breaks = mt_convert_breaks;
    }

    public String getMt_text_more() {
        return mt_text_more;
    }

    public void setMt_text_more(String mt_text_more) {
        this.mt_text_more = mt_text_more;
    }

    public String getMt_excerpt() {
        return mt_excerpt;
    }

    public void setMt_excerpt(String mt_excerpt) {
        this.mt_excerpt = mt_excerpt;
    }

    public String getMt_keywords() {
        return mt_keywords;
    }

    public void setMt_keywords(String mt_keywords) {
        this.mt_keywords = mt_keywords;
    }

    public String getWp_slug() {
        return wp_slug;
    }

    public void setWp_slug(String wp_slug) {
        this.wp_slug = wp_slug;
    }
}
