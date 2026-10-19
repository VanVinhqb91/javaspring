package vpasound.com.backend.catalogs.service;

import java.util.List;

import org.springframework.data.domain.Page;

import vpasound.com.core.entity.ProductEntity;

public interface ProductService {
	
	public Page<ProductEntity> getAllProduct(int page, int size, String sortBy, String sortDir);
	
	public void createProduct(ProductEntity productEntity);
	
	public ProductEntity findById(Long id);
	
	public void updateProduct(Long id, ProductEntity product);
	
	public void deleteProduct(Long id);
	
	public Long totalProduct();
	
	List<ProductEntity> findAll();
}
