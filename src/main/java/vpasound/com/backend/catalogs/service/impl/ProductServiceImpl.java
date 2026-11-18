package vpasound.com.backend.catalogs.service.impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import vpasound.com.backend.catalogs.service.ProductService;
import vpasound.com.core.entity.ProductEntity;
import vpasound.com.core.helper.SlugUtils;
import vpasound.com.core.repository.ProductRepository;
import vpasound.com.core.service.KafkaService;
import vpasound.com.fontend.elastic.ProductElastic;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepository;
	private final KafkaService kafkaService;
	private final SlugUtils slugUtils;

	@Override
	public Page<ProductEntity> getAllProduct(int page, int size, String sortBy, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		
		Pageable pageable = PageRequest.of(page, size,sort);
		Page<ProductEntity> proPage = productRepository.findAll(pageable);
		return proPage;
	}
	
	@Override
	public void createProduct(@NonNull ProductEntity product) {
		if (product.getSlug() == null)
			product.setSlug(slugUtils.toSlug(product.getName()));
		else product.setSlug(slugUtils.toSlug(product.getSlug()));
		
		ProductEntity productEntity = productRepository.save(product);
		// Ban event len Kafka de consumer lang nghe va dong bo len Elastich Search
		String productTopic = "vpa_product_sync_to_elastic";
		String productKey = String.valueOf(productEntity.getId());
		try {
			ProductElastic elasticDoc = new ProductElastic();
			elasticDoc.setId(productKey);
			elasticDoc.setName(product.getName());
			elasticDoc.setSlug(product.getSlug());
			elasticDoc.setPrice(product.getPrice() != null ? product.getPrice().intValue() : 0);
			kafkaService.sendProductSyncElastic(productTopic, productKey, elasticDoc);
		} catch (Exception e) {
			System.out.println("["+this.getClass().getSimpleName()+"] Loi khi gui Product data len Kafka: " + e.getMessage());
		}
	}
	
	@Override
	public ProductEntity findById(@NonNull Long id) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("Khong tim thay san pham co id: = " + id));
		return product;
	}
	
	@Override
	public void updateProduct(@NonNull Long id, ProductEntity product) {
		ProductEntity productUpdate = productRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("Khong tim thay san pham co id: = " + id));

		if (!product.getName().equalsIgnoreCase(productUpdate.getName()) || 
				!java.util.Objects.equals(product.getPrice(), productUpdate.getPrice()) ||
				!product.getSlug().equalsIgnoreCase(productUpdate.getSlug())) {
			String productTopic = "vpa_product_sync_to_elastic";
			String productKey = String.valueOf(id);
			try {
				ProductElastic elasticDoc = new ProductElastic();
				elasticDoc.setId(productKey);
				elasticDoc.setName(product.getName());
				elasticDoc.setSlug(product.getSlug());
				elasticDoc.setPrice(product.getPrice() != null ? product.getPrice().intValue() : 0);

				kafkaService.sendProductSyncElastic(productTopic, productKey, elasticDoc);
			} catch (Exception e) {
				System.out.println("["+this.getClass().getSimpleName()+"][KAFKA-PRODUCER] Loi khi gui Product data len Kafka: " + e.getMessage());
			}
		}
		
		if (product.getSlug() == null || product.getSlug().isBlank())
			product.setSlug(slugUtils.toSlug(product.getName()));
		else product.setSlug(slugUtils.toSlug(product.getSlug()));
		
		productUpdate.setName(product.getName());
		productUpdate.setSlug(product.getSlug());
		productUpdate.setBrandId(product.getBrandId());
		productUpdate.setWarrantyMonths(product.getWarrantyMonths());
		productUpdate.setDescription(product.getDescription());
		productUpdate.setSeoMetaDescription(product.getSeoMetaDescription());
		productUpdate.setSeoMetaKeywords(product.getSeoMetaKeywords());
		productUpdate.setSeoMetaTitle(product.getSeoMetaTitle());
		productUpdate.setShortDescription(product.getShortDescription());
		productUpdate.setSku(product.getSku());
		productUpdate.setCategoryId(product.getCategoryId());
		productUpdate.setPrice(product.getPrice());
		productUpdate.setQty(product.getQty());
		productUpdate.setQtySold(product.getQtySold());
		productUpdate.setStatus(product.getStatus());
		productUpdate.setStockStatus(product.getStockStatus());
		if (product.getUrlImage() != null) 
			productUpdate.setUrlImage(product.getUrlImage());
		else
			productUpdate.setUrlImage(productUpdate.getUrlImage());
		productRepository.save(productUpdate);
	}
	
	@Override
	public void deleteProduct(@NonNull Long id) {
		productRepository.deleteById(id);
		try {
			kafkaService.sendProductSyncElastic("vpa_product_sync_to_elastic", String.valueOf(id), "DELETE");
		    System.out.println("["+this.getClass().getSimpleName()+"][KAFKA-PRODUCER] Đã bắn sự kiện XÓA sản phẩm ID: "+id);
		} catch (Exception e) {
		    System.err.println("["+this.getClass().getSimpleName()+"][KAFKA-PRODUCER] Lỗi phát sự kiện xóa Elastic: "+e.getMessage());
		}
	}
	
	@Override
	public Long totalProduct() {
		return productRepository.count();
	}
	
	@Override
	public List<ProductEntity> findAll() {
	    return productRepository.findAll(); 
	}
}
