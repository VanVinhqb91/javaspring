package vpasound.com.core.dto.request.backend;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderTotalBlockDto {
	
	private Long id;
	
	@NotBlank(message = "Status Order Not Bland.")
    private String status;
	
    private String note;
}
