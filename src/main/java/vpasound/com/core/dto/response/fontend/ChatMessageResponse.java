package vpasound.com.core.dto.response.fontend;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageResponse {
	private String sender;
    private String content;
    private String type;
    private LocalDateTime timestamp; // Thêm thời gian hệ thống

    public ChatMessageResponse(String sender, String content, String type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = LocalDateTime.now(); // Tự động lấy giờ hiện tại
    }
}
