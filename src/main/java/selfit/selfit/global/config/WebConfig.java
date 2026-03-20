package selfit.selfit.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// react 연동 설정
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")               // API 전용 경로
                .allowedOrigins(frontendUrl, "http://localhost:8080")  // 프론트 URL & Swagger UI
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")                // 모든 헤더 허용
                .allowCredentials(true)             // 쿠키, Authorization 헤더 허용
                .maxAge(3600);                      // pre-flight 캐시 1시간
    }

}
