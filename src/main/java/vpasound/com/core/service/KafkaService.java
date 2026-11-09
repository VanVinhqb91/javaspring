package vpasound.com.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void sendActivity(String topic, String key, Object data) {
        try {
            // Chủ động chuyển đổi Object DTO thành chuỗi JSON thô, loại bỏ hoàn toàn Type Headers gây nhiễu
            String jsonString = objectMapper.writeValueAsString(data);
            kafkaTemplate.send(topic, key, jsonString).whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("🚀 [PRODUCER PRODUCT] Đã gửi chuỗi thô lên Kafka thành công! Offset: " 
                            + result.getRecordMetadata().offset());
                } else {
                    System.err.println("❌ [PRODUCER PRODUCT] Gửi lên Kafka THẤT BẠI! " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("❌ [PRODUCER PRODUCT] Lỗi khi chuyển đổi Json String: " + e.getMessage());
        }
    }
    
    public void sendProductSyncElastic(String topic, String key, Object data) {
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            kafkaTemplate.send(topic, key, jsonString).whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("🚀 [PRODUCER ELASTIC] Đã tạo thành công 1 sản phẩm và gửi lên Kafka thành công!." 
                            + result.getRecordMetadata().offset());
                } else {
                    System.err.println("❌ [PRODUCER ELASTIC] Gửi lên Kafka THẤT BẠI! " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("❌ [PRODUCER ELASTIC] Lỗi khi chuyển đổi Json String: " + e.getMessage());
        }
    }
}
