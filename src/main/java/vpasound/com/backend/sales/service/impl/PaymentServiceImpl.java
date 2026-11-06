package vpasound.com.backend.sales.service.impl;

import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl {

    private final String BANK_ID = "vietcombank";
    private final String ACCOUNT_NO = "0331000455272";
    private final String ACCOUNT_NAME = "HO VAN VINH"; 

    public Map<String, Object> createVietQRPayment(String orderId, String amount, String orderInfo) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 2. Định dạng lại nội dung chuyển khoản bảo mật (Mã hóa khoảng trắng thành %20)
            // Định dạng nội dung: VCB_DONHANG_[Mã đơn hàng]_[Thông tin] để tí nữa mình check lịch sử cho dễ
            String encodedInfo = URLEncoder.encode("VCB DONHANG " + orderId, StandardCharsets.UTF_8.toString());

         // Sư phụ thay thế đoạn String qrUrl cũ bằng đoạn chuẩn chỉnh này:
            String qrUrl = String.format("https://img.vietqr.io/image/VCB-0311000710292-vietqr_pro.svg?amount=5200000&addInfo=Thanh+Toan+Hoa+Don&accountName=Ho+Van+Vinh",
                    BANK_ID, ACCOUNT_NO, amount, encodedInfo, URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8.toString()));

            // 4. Trả kết quả sạch sẽ về cho Controller dưới dạng JSON
            response.put("status", "success");
            response.put("orderId", orderId);
            response.put("amount", amount);
            response.put("qrCodeUrl", qrUrl); // Trả về link ảnh QR để Frontend hiển thị lên thẻ <img>
            response.put("message", "Tạo mã VietQR thành công!");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }
}
