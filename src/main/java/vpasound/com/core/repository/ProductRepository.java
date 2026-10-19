package vpasound.com.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import vpasound.com.core.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
	
	Page<ProductEntity> findByStatus(Integer status, Pageable pageable);
	
	Page<ProductEntity> findByNameContaining(String name, Pageable pageable);
	
	@Modifying
    @Transactional
	@Query(value = "UPDATE vha_products SET status = 1 WHERE status = 0", nativeQuery = true)
	public int updateProductStatusFromZeroToOne();
}
