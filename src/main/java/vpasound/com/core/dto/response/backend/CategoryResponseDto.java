package vpasound.com.core.dto.response.backend;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDto {
	private Integer id;
    private String name;
    private List<CategoryResponseDto> children;
}
