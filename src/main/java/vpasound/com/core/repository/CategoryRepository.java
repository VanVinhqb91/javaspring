package vpasound.com.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vpasound.com.core.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer>{
	List<CategoryEntity> findByParentIsNull();
}
