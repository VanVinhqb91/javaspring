package vpasound.com;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

import lombok.NonNull;

@SpringBootApplication(scanBasePackages = "vpasound.com")
@EnableKafka
@EnableCaching
public class VpasoundApplication {
	public static void main(@NonNull String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		SpringApplication.run(VpasoundApplication.class, args);
	}
}
