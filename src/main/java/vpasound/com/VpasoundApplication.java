package vpasound.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import lombok.NonNull;

@SpringBootApplication(scanBasePackages = "vpasound.com")
@EnableKafka
public class VpasoundApplication {

	public static void main(@NonNull String[] args) {
		SpringApplication.run(VpasoundApplication.class, args);
	}
}
