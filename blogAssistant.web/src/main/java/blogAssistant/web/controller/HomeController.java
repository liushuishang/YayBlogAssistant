package blogAssistant.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
@Controller
public class HomeController {

//    @Autowired
//    private IPageCrawler pageCrawler;
    @RequestMapping(value = "/getPage",method = RequestMethod.GET)
    public ModelAndView getPage(@RequestParam(name = "url") String targetUrl)
    {


        return new ModelAndView("");
    }

}
