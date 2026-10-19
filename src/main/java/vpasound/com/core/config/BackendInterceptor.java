package vpasound.com.core.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import vpasound.com.core.entity.SettingEntity;
import vpasound.com.core.repository.SettingRepository;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class BackendInterceptor implements HandlerInterceptor{
	
	private final SettingRepository settingRepository;
	
	@Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        
        // Chỉ xử lý nếu request hợp lệ và có trả về giao diện HTML (ModelAndView)
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
            
            // Lấy đường dẫn URL hiện tại (Ví dụ: /backend/category/update/1)
            String uri = request.getRequestURI();
            String activeMenu = "";

            // Phân tích cú pháp URL để định nghĩa giá trị activeMenu tương ứng
            if (uri.startsWith("/backend/catalogs")) {
                activeMenu = "catalogs";
            } 
            else if (uri.startsWith("/backend/sales")) {
                activeMenu = "sales";
            } 
            else if (uri.startsWith("/backend/customers")) {
                activeMenu = "customers";
            } 
            else if (uri.startsWith("/backend/marketings")) {
                activeMenu = "marketing";
            } 
            else if (uri.startsWith("/backend/reports")) {
                activeMenu = "reports";
            } 
            else if (uri.startsWith("/backend/contents")) {
                activeMenu = "contents";
            }
            else if (uri.startsWith("/backend/systems")) {
                activeMenu = "systems";
            } 
            else if (uri.equals("/backend/dashboard") || uri.equals("/backend")) {
                activeMenu = "dashboard";
            }

            // Tự động đẩy biến activeMenu vào Model cho Thymeleaf sử dụng
            modelAndView.addObject("activeMenu", activeMenu);
            
            // Lấy toàn bộ danh sách cấu hình từ Database lên
            modelAndView.addObject("webName", this.getSetingValueByKey("web_name"));
            modelAndView.addObject("webVersion", this.getSetingValueByKey("web_version"));
        }
    }
	
	public String getSetingValueByKey(String key) {
		SettingEntity settingEntity = settingRepository.findById(key)
        		.orElseThrow(() -> new RuntimeException("Loi khong tim thay Key: " + key));
		
		return settingEntity.getSettingValue();
	}
}
