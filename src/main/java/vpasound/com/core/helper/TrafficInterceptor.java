package vpasound.com.core.helper;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TrafficInterceptor implements HandlerInterceptor{
	private final StringRedisTemplate redisTemplate;
    public TrafficInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    		String visitorId = null;

        if (visitorId == null) {
        		visitorId = java.util.UUID.randomUUID().toString();
        		jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("VISITOR_TRACKING_ID", visitorId);
        		cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
        
        String uri = request.getRequestURI();
        if (uri.contains(".") || 
            uri.startsWith("/css/") || 
            uri.startsWith("/js/") || 
            uri.startsWith("/images/") || 
            uri.contains("favicon.ico") ||
            uri.startsWith("/actuator")) {
            return true;
        }

        // 3. Ghi nhận mã định danh này vào bộ đếm ngày của Redis
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String redisKey = "Traffic:" + today;
        redisTemplate.opsForHyperLogLog().add(redisKey, visitorId);
        System.out.println("🚀 [REDIS TRAFFIC] Ghi nhận thêm 1 lượt truy cập vào Key: " + redisKey);

        return true;
    }
}
