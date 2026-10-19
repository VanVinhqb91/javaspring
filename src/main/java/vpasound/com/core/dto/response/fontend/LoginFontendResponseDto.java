package vpasound.com.core.dto.response.fontend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFontendResponseDto {
	private Long id;
	private String username;
	private String email;
	private String password;
}
