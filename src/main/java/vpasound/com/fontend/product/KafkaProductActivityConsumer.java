package vpasound.com.fontend.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vpasound.com.core.dto.response.fontend.ProductViewKafkaResponseDto;
import vpasound.com.core.entity.UserProductActivityEntity;
import vpasound.com.core.repository.UserProductActivityRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProductActivityConsumer {
    
    private final UserProductActivityRepository activityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
        topics = "vpa_tracking_activity", 
        groupId = "vpa-activity-product-click-detail",
        containerFactory = "kafkaListenerContainerFactory",
        concurrency = "3"
    )
    public void listenProductActivity(List<String> listRawJson) {
    		String threadName = Thread.currentThread().getName();
    		log.info("📥 [THREAD: " + threadName + "] Nhận được lô gồm: " + listRawJson.size() + " tin nhan.");
        System.out.println("==================================================");
        System.out.println("📥 [THREAD: " + threadName + "] Nhận được lô gồm: " + listRawJson.size() + " tin nhan.");

        List<UserProductActivityEntity> entityList = new ArrayList<>();
        for (String rawJson : listRawJson) {
	        try {
	            ProductViewKafkaResponseDto event = objectMapper.readValue(rawJson, ProductViewKafkaResponseDto.class);
	            
	            UserProductActivityEntity entity = UserProductActivityEntity.builder()
	                    .productId(event.getId())
	                    .productName(event.getName())
	                    .price(event.getPrice())
	                    .qty(event.getQty())
	                    .activityType(event.getActivityType())
	                    .timestamp(event.getTimestamp())
	                    .build();
	            entityList.add(entity);
	        } catch (Exception e) {
	        		log.error("Lỗi giải mã JSON lẻ: " + e.getMessage());
	        		System.err.println("Lỗi giải mã JSON lẻ: " + e.getMessage());
	        }
        }
        if (!entityList.isEmpty()) {
            activityRepository.saveAllAndFlush(entityList);
            System.out.println("✅ MYSQL: Đã ghi gom cụm thành công " + entityList.size() + " dòng vào Database.");
        }
    }
}
