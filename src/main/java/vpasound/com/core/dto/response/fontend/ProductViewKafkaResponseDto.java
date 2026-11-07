package vpasound.com.core.dto.response.fontend;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewKafkaResponseDto {
	private Long id;
	private String name;
	private Integer qty;
	private Integer price;
	private String timestamp;
	private String activityType;
	private String userId;
}
