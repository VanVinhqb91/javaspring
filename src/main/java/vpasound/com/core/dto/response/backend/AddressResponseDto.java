package vpasound.com.core.dto.response.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {
	private Long id;
    private String code;
    private String name;
}
