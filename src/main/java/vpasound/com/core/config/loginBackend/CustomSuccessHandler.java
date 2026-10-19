package vpasound.com.core.config.loginBackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	// Object của Jackson dùng để chuyển đổi Map thành chuỗi JSON 
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// Lấy danh sách các quyền (roles) của user hiện tại từ thông tin xác thực
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		// Kiểm tra xem trong danh sách quyền có chứa "ROLE_1" (Admin) hay không
		boolean isAdmin = authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_1"));

		// Xác định URL đích dựa vào quyền:
		// Nếu là Admin (role = 1) -> /backend/index
		// Nếu là user thường -> về trang chủ /
		String targetUrl = isAdmin ? "/backend/dashboard" : "/";

		// Khởi tạo Map dữ liệu trả về khớp 100% với cấu trúc AJAX đang đợi
		// (response.status và response.data)
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("status", true);
		responseData.put("data", targetUrl);
		
		// Http Session
		String username = authentication.getName();
		HttpSession session = request.getSession();
		session.setAttribute("username", username);

		// Thiết lập header phản hồi là định dạng JSON và encoding UTF-8 để không lỗi
		// font tiếng Việt
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		// Ghi dữ liệu JSON trả về cho phía JavaScript xử lý
		response.getWriter().write(objectMapper.writeValueAsString(responseData));
	}
}
