package vpasound.com.core.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import lombok.RequiredArgsConstructor;

@ControllerAdvice // 🌟 Khai báo hỗ trợ dữ liệu toàn cục cho mọi Controller
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final SecretUrlKeyAdmin secretUrlKeyAdmin;

    // Hàm này tự động chạy trước khi render bất kỳ file HTML nào
    // Đảm bảo biến "urlSecretKey" luôn luôn có sẵn ngoài Thymeleaf 100%
    @ModelAttribute("urlSecretKey")
    public SecretUrlKeyAdmin addUrlSecretKeyToModel() {
        return secretUrlKeyAdmin;
    }
}
