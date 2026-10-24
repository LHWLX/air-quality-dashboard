package com.example.airquality.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
                .allowedMethods("GET")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Bean
    public OpenAPI airQualityOpenApi() {
        return new OpenAPI().info(new Info()
                .title("城市空气质量监测 API")
                .description("提供城市列表、空气质量明细、分组统计和单条记录查询")
                .version("1.0.0"));
    }
}

