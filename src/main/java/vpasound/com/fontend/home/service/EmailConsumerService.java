package vpasound.com.fontend.home.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import vpasound.com.core.dto.response.fontend.LoginFontendResponseDto;

@Service
public class EmailConsumerService {
	// Lắng nghe từ tên Queue đã cấu hình trong file properties
    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumeMessage(LoginFontendResponseDto userDto) {
        
    	System.out.println("3. [Consumer] Nhận được dữ liệu từ RabbitMQ: " + userDto.getEmail());
        
        // Mô phỏng thời gian gửi email thật bị chậm (ví dụ mất 3 giây)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("4. [Consumer] Đã gửi email chào mừng thành công tới: " + userDto.getEmail());
    }
}
