package vpasound.com.core.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
	
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        
        // Kết nối thẳng qua IP số localhost ra Docker
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Tạo một Group ID mới
        //props.put(ConsumerConfig.GROUP_ID_CONFIG, "vpa-analytics-kafka-group-120");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); 
        
        // Tong so goi hang, kich thuoc toi da byte, thoi gian cap nhat ms
        //props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 2);
        //props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1000);
        //props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 5000);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true); // Gom lo nhieu tin nhan
        factory.setConcurrency(3); // Tong so consumer
        
        factory.getContainerProperties().getKafkaConsumerProperties().put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);
        factory.getContainerProperties().getKafkaConsumerProperties().put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1000);
        factory.getContainerProperties().getKafkaConsumerProperties().put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 3600000);
        
        return factory;
    }
    
    @Bean(name = "ElasticRealtimeListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> elasticRealtimeListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true); // Gom lo nhieu tin nhan
        factory.setConcurrency(2); // Tong so consumer
        
        factory.getContainerProperties().getKafkaConsumerProperties().put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        factory.getContainerProperties().getKafkaConsumerProperties().put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        factory.getContainerProperties().getKafkaConsumerProperties().put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 10);
        
        return factory;
    }
}
