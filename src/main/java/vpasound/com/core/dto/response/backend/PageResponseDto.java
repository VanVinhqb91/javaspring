package vpasound.com.core.dto.response.backend;

import lombok.Data;

@Data
public class PageResponseDto {

	@Data
	public static class CreatePage {
		private Integer id;
		private String status;
		private String pageName;
		private String slug;
		private String codeHtml;
		private String title;
	}
}
