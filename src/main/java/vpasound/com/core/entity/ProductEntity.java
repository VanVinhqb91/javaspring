package vpasound.com.core.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vha_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "url_image")
	private String urlImage;
	
	@Column(name = "qty")
	private Integer qty;
	
	@Column(name = "qty_sold")
	private Integer qtySold;
	
	@Column(name = "sku")
	private String sku;
	
	@Column(name = "url_product")
	private String urlProduct;
	
	@Column(name = "category_id")
	private Integer categoryId;
	
	@Column(name = "price")
	private Integer price;
	
	@Column(name = "brand_id")
	private String brandId;
	
	@Column(name = "url_video")
	private String urlVideo;
	
	@Column(name = "short_description", length = 3000)
	private String shortDescription;
	
	@Column(name = "description", columnDefinition = "LONGTEXT")
	private String description;
	
	@Column(name = "discount")
	private Integer discount;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "warranty_months")
	private Integer warrantyMonths;
	
	@Column(name = "stock_status")
	private Integer stockStatus;
	
	@Column(name = "seo_url_key")
	private String seoUrlKey;
	
	@Column(name = "seo_meta_title")
	private String seoMetaTitle;
	
	@Column(name = "seo_meta_keywords")
	private String seoMetaKeywords;
	
	@Column(name = "seo_meta_description")
	private String seoMetaDescription;
	
	@Column(name = "status")
	private Integer status;
}
