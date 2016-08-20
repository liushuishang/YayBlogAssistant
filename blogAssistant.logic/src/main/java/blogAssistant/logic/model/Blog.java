package blogAssistant.logic.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class Blog {
    private String username;
    private String password;
    private String title;
    private String content;
    private String metaDescription;
    private String[] categories = new String[0];
    private Map<String, String> ext = new HashMap<String, String>();

    public Blog() {
    }

    public Blog(String title, String content) {
        super();
        this.title = title;
        this.content = content;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String... categories) {
        this.categories = categories;
    }

    public Map<String, String> getExt() {
        return ext;
    }

    public void setExt(Map<String, String> ext) {
        this.ext = ext;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public Map<String,Object> toPost()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("categories", getCategories());
        map.put("description", getContent());
        map.put("mt_allow_comments","closed");// "open" or "closed"
        map.put("mt_allow_pings ","closed");// "open" or "closed"
        map.put("mt_convert_breaks",true);
        map.put("mt_keywords", "");
        map.put("mt_excerpt", "");
        map.put("dateCreated ", new Date());
        return map;
    }
}
