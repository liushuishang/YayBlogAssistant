package blogAssistant.web;

import blogAssistant.logic.model.Blog;
import blogAssistant.logic.poster.MetaWeblogBlogPoster;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}



	@Test
	public void testMetaWeblogBlogPoster()
	{
		String blogAddress = "http://www.cnblogs.com/lhdwr/";
		String userName = "lhdwr";
		String password = "lhdwr@123";
		MetaWeblogBlogPoster metaWeblogBlogPoster=new MetaWeblogBlogPoster(blogAddress, userName, password);

		//获取博客分类
		String[] blogCategories = metaWeblogBlogPoster.getCategories();

		//发表博客
		metaWeblogBlogPoster.postBlog(newBlog());




	}


	protected   Blog newBlog() {
		Blog blog = new Blog();
		blog.setTitle("test_title 测试标题:" + System.currentTimeMillis());
		blog.setContent(StringUtils.repeat("中国人民\t<p>test_content</p> \n",50));
		return blog;
	}

}
