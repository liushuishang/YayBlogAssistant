package blogAssistant.logic.model.metaweblog;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class WpCategory {
    private String name;
    /**
     * 可选
     */
    private String slug;
    private int parent_id;
    /**
     * 可选
     */
    private String description;

    public WpCategory(String name,  String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
