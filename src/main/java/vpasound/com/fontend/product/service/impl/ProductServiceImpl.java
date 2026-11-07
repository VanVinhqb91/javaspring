package vpasound.com.fontend.product.service.impl;

import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vpasound.com.core.entity.ProductEntity;
import vpasound.com.core.repository.ProductRepository;

@Service("fontendProductServiceImpl")
@RequiredArgsConstructor
public class ProductServiceImpl {

	private final ProductRepository productRepository;
	
	public ProductEntity getProductById(Long id) {
		
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay id: " + id));
		if (Objects.isNull(product)) return null;
		
		return product;
	}
}
