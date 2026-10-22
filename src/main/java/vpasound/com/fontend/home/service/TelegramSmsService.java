package vpasound.com.fontend.home.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramSmsService {
	@Value("${telegram.bot_token}")
    private String botToken;

    @Value("${telegram.chat_id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendNotificationSms(String guestName, String content) {
        try {
            // Nội dung thông báo hiển thị trên ứng dụng Telegram (hỗ trợ cả tiếng Việt có dấu thoải mái)
            String messageText = "🔔 <b> Hệ thống: vpasound.com | ["+guestName+"]</b>\n"
            		 + "Nội dung: " + content + "\n"
            		 + "Hãy vào trang Quản trị để trả lời.";

            // ĐÚNG: Sửa thành api.telegram.org và thêm chữ /bot trước mã token
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + messageText + "&parse_mode=HTML";

            // Kích hoạt gọi link URL ngầm để gửi thông báo đi
            restTemplate.getForObject(url, String.class);
            
            System.out.println(">>> Đã kích hoạt Telegram Bot gửi thông báo thành công cho Admin!");
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo qua Telegram: " + e.getMessage());
        }
    }
}
