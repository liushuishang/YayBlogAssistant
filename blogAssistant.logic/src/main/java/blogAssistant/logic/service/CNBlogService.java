package blogAssistant.logic.service;

/**博客园的博客服务
 * Created by yuananyun on 2016/8/21.
 */
public class CNBlogService extends MetaweblogBlogService {
    public CNBlogService(String userName, String password) {
        super("cnblog", "http://www.cnblogs.com/" + userName + "/", userName, password);
    }
}
