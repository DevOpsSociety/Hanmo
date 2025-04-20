package org.example.hanmo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hanmo.util.TempTokenAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
  private final TempTokenAuthInterceptor tempTokenAuthInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
            .addInterceptor(tempTokenAuthInterceptor)
            .addPathPatterns("/user/**")
            .excludePathPatterns("/user/login", "/user/signup");

    registry
            .addInterceptor(new CacheControlInterceptor())
            .addPathPatterns("/api/**")         // API 응답에만 캐시 헤더 적용
            .order(Ordered.LOWEST_PRECEDENCE);
  }

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry
            .addMapping("/**")
            .allowedOrigins(
                    "http://localhost:3000",
                    "https://localhost:3000",
                    "https://hanmo.store",
                    "https://www.hanmo.store",
                    "http://hanmo.store",
                    "http://www.hanmo.store",
                    "https://hanmo-front-r22cegd8m-leegyeonghwans-projects.vercel.app",
                    "http://hanmo-front-r22cegd8m-leegyeonghwans-projects.vercel.app",
                    "https://hanmo-front-git-lee-leegyeonghwans-projects.vercel.app",
                    "http://hanmo-front-git-lee-leegyeonghwans-projects.vercel.app",
                    "https://hanmo-front.vercel.app",
                    "https://hanmo-front-5n9y.vercel.app")
            .exposedHeaders("temptoken")
            .allowedHeaders("*")
            .allowedMethods("*")
            .allowCredentials(true);
  }

  private static class CacheControlInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
      response.setHeader("Cache-Control", "public, max-age=420");
    }
  }
}
