package vpasound.com.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CoreConfig implements WebMvcConfigurer{
	
	private final BackendInterceptor backendInterceptor;
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image_product/**")
                .addResourceLocations("classpath:/static/images/products");
        
        registry.addResourceHandler("/image_brand/**")
        .addResourceLocations("classpath:/static/images/brands");
        
        registry.addResourceHandler("/image_user/**")
        .addResourceLocations("classpath:/static/images/users");
    }
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Áp dụng bộ lọc này cho toàn bộ các đường dẫn quản trị bắt đầu bằng /backend/**
        // Tránh áp dụng cho các file tĩnh như css, js, images bằng cách loại trừ nếu cần
        registry.addInterceptor(backendInterceptor)
                .addPathPatterns("/backend/**"); 
    }
}
