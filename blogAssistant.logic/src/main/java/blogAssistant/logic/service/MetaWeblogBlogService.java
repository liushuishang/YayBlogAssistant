package blogAssistant.logic.service;

import blogAssistant.logic.common.IBlogService;
import blogAssistant.logic.model.metaweblog.*;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class MetaWeblogBlogService implements IBlogService {
    @Override
    public boolean deletePost(String appKey, String postId, String userName, String password, boolean publish) {
        return false;
    }

    @Override
    public List<BlogInfo> getUsersBlogs(String appKey, String userName, String password) {
        return null;
    }

    @Override
    public boolean editPost(String postId, String userName, String password, Post post, boolean publish) {
        return false;
    }

    @Override
    public List<CategoryInfo> getCategories(String blogId, String userName, String password) {
        return null;
    }

    @Override
    public Post getPost(String postId, String userName, String password) {
        return null;
    }

    @Override
    public List<Post> getRecentPosts(String blogId, String userName, String password, int number) {
        return null;
    }

    @Override
    public UrlData newMediaObject(String blogId, String userName, String password, FileData file) {
        return null;
    }

    @Override
    public String newPost(String blogId, String userName, String password, Post post, boolean publish) {
        return null;
    }

    @Override
    public Integer newCategory(String blogId, String userName, String password, WpCategory category) {
        return null;
    }
}
