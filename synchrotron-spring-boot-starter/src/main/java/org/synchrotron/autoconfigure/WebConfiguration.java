package org.synchrotron.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.synchrotron.core.SyncRunner;

@Configuration
@ConditionalOnWebApplication
@EnableWebMvc
@ConditionalOnClass(SyncRunner.class)
@ComponentScan(basePackages = {"org.synchrotron.autoconfigure.controller"})
public class WebConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/synchrotron/**").addResourceLocations("classpath:/static/").setCachePeriod(3600).resourceChain(true)
				.addResolver(new EncodedResourceResolver()).addResolver(new PathResourceResolver());

/*		registry
				.addResourceHandler("/resources/static")
				.addResourceLocations("/src/main/webapp/index.html");*/
	}

}
