package vpasound.com.core.config.loginBackend;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler{
	
	@Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // Khi người dùng đã login nhưng sai Role cố truy cập /backend/** 
        // -> Ép trình duyệt redirect (302) trực tiếp về trang chủ "/"
        response.sendRedirect("/");
    }
}
