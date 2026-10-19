package vpasound.com.core.dto.request.fontend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {
	private String sender;   // Mã phòng/Mã khách (ROOM_ID / GUEST_ID)
    private String content;  // Nội dung tin nhắn
    private String type;     // CHAT hoặc JOIN
}
