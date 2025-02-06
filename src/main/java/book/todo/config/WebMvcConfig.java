package book.todo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//모든 요청에 대해
                .allowedOrigins("http://localhost:3000")//허용할 오리진들
                .allowedMethods("GET", "POST", "PUT", "DELETE")//허용할 메소드들
                .allowedHeaders("*")//허용할 헤더들
                .allowCredentials(true)//인증정보 허용
                .maxAge(MAX_AGE_SECS);//캐싱시간
    }



}
