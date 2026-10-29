package vpasound.com.core.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name = "vha_pages")
@NoArgsConstructor
@AllArgsConstructor
public class PageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer id;
	
	@Column(name = "page_name")
	public String pageName;
	
	@Column(name = "page_key")
	public String pageKey;
	
	@Column(name = "title")
	public String title;
	
	@Column(name = "slug")
	public String slug;
	
	@Column(name = "code_html")
	public String codeHtml;
	
	@Column(name = "status")
	public String status;
	
	@Column(name = "created_at")
	public String createdAt;
	
	@Column(name = "updated_at")
	public String updatedAt;
}
