package vpasound.com.core.config.loginBackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {

    // Sử dụng ObjectMapper của thư viện Jackson (mặc định của Spring Boot) để build JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        // Khởi tạo Map dữ liệu trả về khớp với cấu trúc AJAX đang đợi (response.status == false)
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", false);

        // Thiết lập header phản hồi là định dạng JSON và encoding UTF-8
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); // Trả về mã 200 OK để AJAX nhận được dữ liệu và nhảy vào nhánh success của jQuery
        
        // Ghi dữ liệu JSON trả về cho phía JavaScript xử lý
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }
}
