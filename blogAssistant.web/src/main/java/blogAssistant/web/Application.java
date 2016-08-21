package blogAssistant.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication()
@ImportResource(locations = {"classpath*:spring/*.xml"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@Bean(name = "multipartResolver")
//	public CommonsMultipartResolver getMultipartResolver()
//	{
//		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//		multipartResolver.setDefaultEncoding("utf-8");
//		multipartResolver.setMaxInMemorySize(10960);
//		multipartResolver.setMaxUploadSize(1048576000);
//		multipartResolver.setResolveLazily(true);
//		return multipartResolver;
//	}
}
