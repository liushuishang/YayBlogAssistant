package blogAssistant.logic.common;

import blogAssistant.logic.model.metaweblog.*;

import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public interface  IBlogService {

    /**
     * 删除一条博客
     * @param appKey
     * @param postId
     * @param userName
     * @param password
     * @param publish Where applicable, this specifies whether the blog should be republished after the post has been deleted.
     * @return
     */
    boolean deletePost(String appKey,String postId,String userName,String password,boolean publish);

    /**
     * 获取某个用户的博客列表
     * @param appKey
     * @param userName
     * @param password
     * @return
     */
    List<BlogInfo> getUsersBlogs(String appKey, String userName, String password);

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
    boolean editPost(String postId, String userName, String password, Post post, boolean publish);

    /**
     * 获取某个博客账户的所属分类
     * @param blogId
     * @param userName
     * @param password
     * @return
     */
    List<CategoryInfo> getCategories(String blogId,String userName,String password);

    /**
     * 获取某个发表的博客
     * @param postId
     * @param userName
     * @param password
     * @return
     */
    Post getPost(String postId,String userName,String password);

    /**
     * 获取最近的博客发表记录
     * @param blogId
     * @param userName
     * @param password
     * @param number
     * @return
     */
    List<Post> getRecentPosts(String blogId,String userName,String password,int number);


    /**
     * Makes a new file to a designated blog using the metaWeblog API. Returns url as a string of a struct.
     * @param blogId
     * @param userName
     * @param password
     * @param file
     * @return
     */
    UrlData newMediaObject(String blogId, String userName, String password, FileData file);

    /**
     * Makes a new post to a designated blog using the metaWeblog API. Returns postid as a string.
     * @param blogId
     * @param userName
     * @param password
     * @param post
     * @param publish
     * @return
     */
    String newPost(String blogId,String userName,String password,Post post,boolean publish);

    /**
     * Create a new category
     * @param blogId
     * @param userName
     * @param password
     * @param category
     * @return
     */
    Integer newCategory(String blogId, String userName, String password, WpCategory category);

}
