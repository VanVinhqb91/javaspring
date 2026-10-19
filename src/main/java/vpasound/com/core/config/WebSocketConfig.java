package vpasound.com.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker // Bật tính năng Message Broker cho WebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Điểm kết nối đầu tiên để Client (Frontend) gọi vào thiết lập WebSocket
        registry.addEndpoint("/ws-chat")
        .setAllowedOriginPatterns("*")
        .withSockJS(); 
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Các tin nhắn có tiền tố /app sẽ được định tuyến đến các hàm xử lý (@MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");

        // Bật một Broker trong bộ nhớ để quản lý các topic nhận tin nhắn (Client sẽ subscribe các kênh này)
        registry.enableStompBrokerRelay("/topic", "queue", "/amq/queue")
        .setRelayHost("localhost") // Đổi thành IP của server nếu RabbitMQ chạy khác máy với Spring Boot
        .setRelayPort(61613)       // Cổng kết nối STOMP của RabbitMQ
        .setClientLogin("guest")   // Tài khoản mặc định RabbitMQ
        .setClientPasscode("guest")
        .setSystemLogin("guest")      // Bổ sung thêm tài khoản hệ thống của Spring
        .setSystemPasscode("guest")
        .setVirtualHost("/");
        registry.setUserDestinationPrefix("/user");
    }
}
