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
                    System.out.println("🚀 PRODUCER SUCCESS: Đã gửi chuỗi thô lên Kafka thành công! Offset: " 
                            + result.getRecordMetadata().offset());
                } else {
                    System.err.println("❌ PRODUCER ERROR: Gửi lên Kafka THẤT BẠI! " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("❌ LỖI KHI CHUYỂN ĐỔI JSON STRING: " + e.getMessage());
        }
    }
    
    public void sendProductSyncElastic(String topic, String key, Object data) {
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            kafkaTemplate.send(topic, key, jsonString).whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("🚀 PRODUCT: Đã tạo thành công 1 sản phẩm và gửi lên Kafka thành công!." 
                            + result.getRecordMetadata().offset());
                } else {
                    System.err.println("❌ PRODUCT ERROR: Gửi lên Kafka thất bại! " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("❌ LỖI KHI CHUYỂN ĐỔI JSON STRING: " + e.getMessage());
        }
    }
}
