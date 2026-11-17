package com.example.palmeiras_api.config;
import org.springframework.context.annotation.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.web.cors.*; import org.springframework.web.filter.CorsFilter; import org.springframework.boot.web.servlet.FilterRegistrationBean;
import java.util.List;
@Configuration public class CorsConfig {
 @Bean public FilterRegistrationBean<CorsFilter> cors(@Value("${app.cors-origins:http://localhost:3000,http://localhost:5173}") String origins){
 CorsConfiguration config=new CorsConfiguration();config.setAllowedOrigins(List.of(origins.split(",")));config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));config.setAllowedHeaders(List.of("Content-Type","Accept","X-API-Key","X-Idempotency-Key","X-API-Version"));config.setExposedHeaders(List.of("Location","Idempotent-Replayed","X-Rate-Limit-Limit","X-Rate-Limit-Remaining","Retry-After"));config.setMaxAge(3600L);
 UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",config);var bean=new FilterRegistrationBean<>(new CorsFilter(source));bean.setOrder(0);return bean; }
}
