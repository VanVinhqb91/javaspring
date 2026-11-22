package vpasound.com.fontend.product.service;

import java.util.List;

import vpasound.com.core.entity.ProductEntity;

public interface ProductService {
	public ProductEntity getProductById(Long id);
	public ProductEntity getProductBySlug(String slug);
	public List<ProductEntity> getBestProductHomePage();
}
