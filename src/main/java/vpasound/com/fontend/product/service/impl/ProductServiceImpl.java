package vpasound.com.fontend.product.service.impl;

import java.util.Objects;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vpasound.com.core.entity.ProductEntity;
import vpasound.com.core.repository.ProductRepository;
import vpasound.com.fontend.product.service.ProductService;

@Service("fontendProductServiceImpl")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

	private final ProductRepository productRepository;
	
	@Override
	@Cacheable(value = "vpa_products_cache", key = "#id")
	public ProductEntity getProductById(Long id) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay id: " + id));
		if (Objects.isNull(product)) return null;
		return product;
	}
}
