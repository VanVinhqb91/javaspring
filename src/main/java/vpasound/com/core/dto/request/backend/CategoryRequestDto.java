package vpasound.com.core.dto.request.backend;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class CategoryRequestDto {
	@NotBlank(message = "Ten danh muc khong duoc de trong.")
	private String name;
	
	private Integer parent;
	
	private String description;
	private String cateUrl;
	private String metaTitle;
	private Integer status;
	private String metaKeywords;
}
