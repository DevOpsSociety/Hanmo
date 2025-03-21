package org.example.hanmo.config;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.util.TempTokenAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final TempTokenAuthInterceptor tempTokenAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tempTokenAuthInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/login", "/user/signup");
    }
}
