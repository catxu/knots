package com.knots.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!uploadDir.endsWith("/")) {
            uploadDir += "/";
        }
        // 自动加上 file: 前缀，保证跨平台
        registry.addResourceHandler(Constants.IMG_URL_PREFIX + "**")
                .addResourceLocations(Constants.FILE_PREFIX + uploadDir)
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());
    }
}
