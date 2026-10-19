package vpasound.com.core.dto.response.backend;

import java.util.Date;

import lombok.Data;

@Data
public class OrderResponseDto {

	@Data
	public static class ViewAllOrder {
		private Long id;
		private Date createdAt;
		private String status;
		private Integer paymentMethod;
		private String finalPriceFormated;
	}
}
