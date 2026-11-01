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

        // 1. Kiểm tra xem người dùng đã có Cookie định danh chưa
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("VISITOR_TRACKING_ID".equals(cookie.getName())) {
                    visitorId = cookie.getValue();
                    break;
                }
            }
        }

        // 2. Nếu chưa có (Tức là mới vào web hoặc vừa tắt trình duyệt mở lại)
        if (visitorId == null) {
            // Tạo một chuỗi định danh ngẫu nhiên duy nhất
            visitorId = java.util.UUID.randomUUID().toString();
            
            // Tạo cookie mới
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("VISITOR_TRACKING_ID", visitorId);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            // KHÔNG dùng setMaxAge -> Cookie này sẽ tự xóa ngay khi user TẮT TRÌNH DUYỆT (Session Cookie)
            
            response.addCookie(cookie);
        }

        // 3. Ghi nhận mã định danh này vào bộ đếm ngày của Redis
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = "traffic:" + today;
        redisTemplate.opsForHyperLogLog().add(redisKey, visitorId);

        return true;
    }
}
