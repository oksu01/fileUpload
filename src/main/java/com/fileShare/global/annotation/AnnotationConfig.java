package com.fileShare.global.annotation;


import com.querydsl.core.annotations.Config;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Config
public class AnnotationConfig implements WebMvcConfigurer {

    private final LoginArgsResolver loginArgsResolver;

    public AnnotationConfig(LoginArgsResolver loginArgsResolver) {
        this.loginArgsResolver = loginArgsResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginArgsResolver);
    }
}