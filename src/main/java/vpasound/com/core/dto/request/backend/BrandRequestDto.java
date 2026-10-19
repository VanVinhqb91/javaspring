package vpasound.com.core.dto.request.backend;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandRequestDto {
	private Integer id;
	@NotBlank(message = "Ten Brand khong duoc de trong.")
    private String name;
    private String urlLogo;
    private String description;
    private String slug;
    private Integer status;
}
