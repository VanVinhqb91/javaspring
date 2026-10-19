package vpasound.com.fontend.home.controller;

import vpasound.com.core.dto.request.fontend.ChatMessageRequest;
import vpasound.com.core.dto.response.fontend.ChatMessageResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.toRoom/{roomId}")
    public void sendMessageToRoom(@DestinationVariable String roomId, ChatMessageRequest request) {
        // Chuyển đổi dữ liệu từ Request sang Response để thêm mốc thời gian hệ thống
        ChatMessageResponse response = new ChatMessageResponse(
                request.getSender(),
                request.getContent(),
                request.getType()
        );
        
        // Bắn dữ liệu Response về cho phòng chat
        messagingTemplate.convertAndSend("/topic/room." + roomId, response);
    }
    
    @GetMapping("/admin-chatbox")
    public String viewAdminChatPage() {
        // Trả về đúng tên file admin-dashboard (Spring tự tìm file .html tương ứng)
        return "/fontend/adminchatbox"; 
    }
}
