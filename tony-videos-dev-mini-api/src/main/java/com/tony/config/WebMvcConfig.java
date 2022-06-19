package com.tony.config;


import com.tony.client.impl.ZKCuratorClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                //.addResourceLocations("file:D:/tony_videos_dev/");
                .addResourceLocations("file:" + System.getProperties().getProperty("user.home") + "/"
                + "webapps" + "/" + "tony_video_dev_data/");
    }


    @Bean(initMethod = "init")
    public ZKCuratorClient zkCuratorClient(){
        return new ZKCuratorClient();
    }

}
