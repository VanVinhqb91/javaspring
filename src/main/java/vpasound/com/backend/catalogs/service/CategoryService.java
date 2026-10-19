package vpasound.com.backend.catalogs.service;

import java.util.List;

import vpasound.com.core.dto.request.backend.CategoryRequestDto;
import vpasound.com.core.dto.response.backend.CategoryResponseDto;
import vpasound.com.core.entity.CategoryEntity;

public interface CategoryService {
	public List<CategoryResponseDto> getCategoryTree();
	
	public List<CategoryResponseDto> getFlattenedCategoryTree();
	
	public void saveCategory(CategoryRequestDto categoryRequestDTO);
	
	public CategoryEntity findById(int id);
	
	public void updateCategory(int id, CategoryRequestDto categoryRequestDTO);
	
	public void deleteCategory(int id);
}
