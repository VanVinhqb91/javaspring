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
import vpasound.com.core.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepository;

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
		productRepository.save(product);
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
		productUpdate.setName(product.getName());
		productUpdate.setBrandId(product.getBrandId());
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
	}
	
	@Override
	public Long totalProduct() {
		return productRepository.count();
	}
	
	@Override
	public List<ProductEntity> findAll() {
	    // Gọi hàm có sẵn của Spring Data JPA để lấy hết sản phẩm từ MySQL
	    return productRepository.findAll(); 
	}
}
