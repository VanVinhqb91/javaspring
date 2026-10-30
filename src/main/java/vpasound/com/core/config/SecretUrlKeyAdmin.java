package vpasound.com.core.config;

import org.springframework.util.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecretUrlKeyAdmin {

    private static final String SECRET_SALT = "vpasound_secret_key_2026";
    
    public String generateUrlAdmin(String targetUri) {
        
        // 1. Tự động lấy Request hiện tại trực tiếp từ bộ nhớ Context của Spring Boot
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return ""; 
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 2. Lấy Session ID và tính toán mã băm an toàn
        String sessionId = request.getSession().getId();
        String urlSalt = sessionId + targetUri + SECRET_SALT;
        
        return DigestUtils.md5DigestAsHex(urlSalt.getBytes());
    }
}
