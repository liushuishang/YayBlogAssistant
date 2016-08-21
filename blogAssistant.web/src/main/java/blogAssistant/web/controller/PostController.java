package blogAssistant.web.controller;

import blogAssistant.logic.crawler.BlogPageCrawler;
import blogAssistant.logic.model.Blog;
import blogAssistant.logic.service.CNBlogService;
import blogAssistant.web.model.RestResult;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
@Controller
public class PostController {

    @Autowired
    private BlogPageCrawler pageCrawler;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping(value = {"/", "/post"}, method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("post");
    }

    @RequestMapping(value = "/fetchPage", method = RequestMethod.GET)
    @ResponseBody
    public Object fetchPage(String targetUrl) {
        try {
            Blog blog = pageCrawler.resolveBlog(targetUrl);
            return RestResult.success(blog);
        } catch (Exception ex) {
            return RestResult.failure(ex.getLocalizedMessage());
        }
    }

    @RequestMapping(value = "/newPost", method = RequestMethod.POST)
    @ResponseBody
    public Object newPost(@RequestBody Map params) {
        try {
            String userName = MapUtils.getString(params, "userName");
            String password = MapUtils.getString(params, "password");
            String title = MapUtils.getString(params, "title");
            String content = MapUtils.getString(params, "editorValue");
//            content=StringEscapeUtils.unescapeHtml4(content);

            Assert.notNull(userName);
            Assert.notNull(password);
            Assert.notNull(title);
            Assert.notNull(content);

            Blog blog = new Blog(title, content);
            CNBlogService blogService = new CNBlogService(userName, password);
            if (blogService.newPost(blog, true) != null)
                return RestResult.success(true);
            else return RestResult.failure("发表博客失败！");
        } catch (Exception ex) {
            return RestResult.failure(ex.getLocalizedMessage());
        }
    }

}
