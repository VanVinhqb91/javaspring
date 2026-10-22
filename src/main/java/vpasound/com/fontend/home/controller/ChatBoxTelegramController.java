package vpasound.com.fontend.home.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.AllArgsConstructor;
import vpasound.com.core.dto.request.fontend.ChatMessageRequest;
import vpasound.com.core.dto.response.fontend.ChatMessageResponse;
import vpasound.com.fontend.home.service.TelegramSmsService;

@Controller
@AllArgsConstructor
public class ChatBoxTelegramController {

    private final TelegramSmsService smsService;
    private final SimpMessagingTemplate messagingTemplate;
    
    // 1. RAM 1: Quản lý danh sách các phòng khách đang chờ hỗ trợ công khai (Lưu: RoomID -> Tên khách)
    private static final Map<String, String> activeRoomsOnRam = new ConcurrentHashMap<>();

    // 2. RAM 2: Quản lý liên kết nội bộ mạng WebSocket để xóa chính xác khi thoát trang (Lưu: SessionID -> RoomID)
    private static final Map<String, String> sessionToRoomMapping = new ConcurrentHashMap<>();

    @MessageMapping("/chat.toRoom/{roomId}")
    public void sendMessageToRoom(
            @DestinationVariable String roomId, 
            @Payload ChatMessageRequest request, 
            SimpMessageHeaderAccessor headerAccessor) {
        
        String sender = request.getSender();
        String content = request.getContent();
        String type = request.getType();

        // CHỈ XỬ LÝ NẾU ĐÂY LÀ TIN NHẮN CHAT THẬT (Bỏ qua gói tin JOIN trống)
        if ("CHAT".equals(type) && content != null && !content.trim().isEmpty()) {
            
            ChatMessageResponse response = new ChatMessageResponse(sender, content, type);
            messagingTemplate.convertAndSend("/topic/room." + roomId, response);

            String sessionId = headerAccessor.getSessionId();
            if (sender != null && sender.startsWith("GUEST_") && sessionId != null) {
                
                // Đồng bộ lưu liên kết mạng nội bộ để nhận diện khi thiết bị này F5/Thoát trang
                sessionToRoomMapping.put(sessionId, roomId);

                // Nếu phòng này chưa gõ câu chat thật nào vào hệ thống từ trước
                if (!activeRoomsOnRam.containsKey(roomId)) {
                    
                    // Ghi nhận mã phòng vào danh sách tổng
                    activeRoomsOnRam.put(roomId, sender);
                    
                    // Bắn thông báo Telegram chạy ngầm chứa cả tên khách và nội dung chat
                    CompletableFuture.runAsync(() -> {
                        smsService.sendNotificationSms(sender, content);
                    });
                }

                // ĐỒNG BỘ THỜI GIAN THỰC CHO ADMIN: Bắn gói tin HashMap (tương thích Java 8) để sidebar tự nổ phòng
                java.util.Map<String, String> adminPayload = new java.util.HashMap<>();
                adminPayload.put("roomId", roomId);
                adminPayload.put("customerName", sender);
                
                messagingTemplate.convertAndSend("/topic/admin.newRoom", adminPayload);
            }
        } else if ("JOIN".equals(type)) {
            // Khi khách vừa load trang thành công, lưu lại cặp kết nối mạng ngay lập tức
            String sessionId = headerAccessor.getSessionId();
            if (sessionId != null && roomId != null && sender != null && sender.startsWith("GUEST_")) {
                sessionToRoomMapping.put(sessionId, roomId);
            }
            System.out.println(">>> Thiết bị [" + sender + "] đã kết nối vào phòng chat: " + roomId);
        }
    }

    /**
     * API phục vụ cho giao diện Admin gọi lấy danh sách phòng ngay khi vừa đăng nhập vào hệ thống
     */
    @GetMapping("/api/chat/active-rooms")
    @ResponseBody
    public Map<String, String> getActiveRooms() {
        return activeRoomsOnRam;
    }

    /**
     * GIẢI PHÁP ĐỒNG BỘ MỚI: Chỉ xóa đúng khách hàng ngắt kết nối, giữ lại toàn bộ khách hàng khác
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        if (sessionId != null) {
            // Tìm xem Session bị đứt này thuộc về mã phòng (Room ID) nào
            String targetRoomId = sessionToRoomMapping.remove(sessionId);
            
            if (targetRoomId != null) {
                // Chỉ xóa đúng duy nhất phòng của người vừa thoát/reload ra khỏi RAM
                activeRoomsOnRam.remove(targetRoomId);
                System.out.println(">>> Khách vãng lai ở phòng [" + targetRoomId + "] đã ngắt kết nối. Đã xóa riêng phòng này, giữ nguyên các phòng khác!");
            }
        }
    }
}
