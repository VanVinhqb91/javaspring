package vpasound.com.fontend.elastic;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProductSyncElasticConsumer {
	
	private final ProductElasticRepository productElasticRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@KafkaListener(
	        topics = "vpa_product_sync_to_elastic", 
	        groupId = "vpa-product-sync-elastic-crud",
	        containerFactory = "ElasticRealtimeListenerFactory",
	        concurrency = "2"
	    )
	public void listenProductSyncElastic(List<ConsumerRecord<String, String>> listRawJson) {
		String threadName = Thread.currentThread().getName();
	    System.out.println("==================================================");
	    System.out.println("📥 [ELASTIC: " + threadName + "] Nhận được lô gồm: " + listRawJson.size() + " tin nhan.");
	
	    List<ProductElastic> entityList = new ArrayList<>();
	    List<String> idsToDelete = new ArrayList<>();
	    for (ConsumerRecord<String, String> record : listRawJson) {
	    		String keyId = record.key();
            String valueData = record.value();
            if (valueData == null || valueData.trim().isEmpty()) {
                continue; 
            }
            String cleanValue = valueData.trim().replace("\"", "");

            if ("DELETE".equalsIgnoreCase(cleanValue) || "DELETE".equalsIgnoreCase(valueData.trim())) {
                if (keyId != null) {
                    idsToDelete.add(keyId.trim());
                }
                continue;
            }

            try {
                ProductElastic doc = objectMapper.readValue(valueData, ProductElastic.class);
                if (doc != null && doc.getId() != null) {
                    entityList.add(doc);
                }
            } catch (Exception e) {
                System.err.println("❌ [ELASTIC] Bỏ qua 1 tin rác kẹt không đúng định dạng class: " + e.getMessage());
            }
	    }
	    if (!entityList.isEmpty()) {
	    		try {
	            productElasticRepository.saveAll(entityList);
	            System.out.println("✅ ELASTIC: Đã ghi gom cụm thành công " + entityList.size() + " dòng vào Elastic Docker.");
	        } catch (Exception e) {
	            System.err.println("❌ Lỗi Bulk Insert Elastic: " + e.getMessage());
	        }
	    }
	    if (!idsToDelete.isEmpty()) {
	    		try {
	    			for (String del : idsToDelete) {
	    				productElasticRepository.deleteById(del);
	    				System.out.println("-> Đang thực thi xóa ID: " + del);
	    			}
	    			System.out.println("✅ [ELASTICSEARCH] Đã xóa sổ hoàn toàn " + idsToDelete.size() + " sản phẩm khỏi ô tìm kiếm!");
	    		} catch (Exception e) {
	    			System.err.println("❌ Lỗi hủy chỉ mục thực tế trên Elastic Docker: " + e.getMessage());
	        }
	    }
	}
}
