package edu.usst.jayce.server.configation;

import edu.usst.jayce.server.util.ConfigureReader;
//import edu.usst.jayce.server.webdav.KiftdWebDAVServlet;
import org.springframework.web.servlet.resource.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.web.servlet.config.annotation.*;

import java.io.*;

import javax.servlet.*;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.*;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.*;
import org.springframework.util.unit.DataSize;

// Web功能-MVC相关配置类 "edu.usst.jayce.server.webdav.util"
@AutoConfiguration
@ComponentScan({ "edu.usst.jayce.server.controller", "edu.usst.jayce.server.service.impl", "edu.usst.jayce.server.util"})
@ServletComponentScan({ "edu.usst.jayce.server.listener", "edu.usst.jayce.server.filter" })
@Import({ DataAccess.class })
public class MVC extends ResourceHttpRequestHandler implements WebMvcConfigurer {
	
	// 注册DefaultServlet
	@Bean
	WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
	    return (factory) -> factory.setRegisterDefaultServlet(true);
	}

	// 启用DefaultServlet用以处理可直接请求的静态资源
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	// 设置Web静态资源映射路径
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		// 将静态页面资源所在文件夹加入至资源路径中
		registry.addResourceHandler(new String[] { "/**" }).addResourceLocations(new String[] {
				"file:" + ConfigureReader.instance().getPath() + File.separator + "webContext" + File.separator });
	}

	// 生成上传管理器，用于接收/缓存上传文件
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		final MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.ofBytes(-1L));
		factory.setLocation(ConfigureReader.instance().getTemporaryfilePath());
		return factory.createMultipartConfig();
	}

	// 生成Gson实例，用于服务Json序列化和反序列化
	@Bean
	public Gson gson() {
		return new GsonBuilder().create();
	}

}
