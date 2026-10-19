package vpasound.com.backend.catalogs.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import vpasound.com.backend.catalogs.service.CategoryService;
import vpasound.com.core.dto.request.backend.CategoryRequestDto;
import vpasound.com.core.dto.response.backend.CategoryResponseDto;
import vpasound.com.core.entity.CategoryEntity;
import vpasound.com.core.mapper.CategoryMapper;
import vpasound.com.core.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	
	public final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
	
	@Override
	public List<CategoryResponseDto> getCategoryTree() {
		List<CategoryEntity> category = categoryRepository.findByParentIsNull();
		
		return category.stream().map(this::convertToDTO).collect(Collectors.toList());
	}
	
	private CategoryResponseDto convertToDTO(CategoryEntity category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        
        if (category.getChildren() != null) {
            dto.setChildren(category.getChildren().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
	
	@Override
	public List<CategoryResponseDto> getFlattenedCategoryTree() {
		List<CategoryResponseDto> flatList = new ArrayList<>();
		// Lấy các danh mục gốc từ repo
		List<CategoryEntity> rootCategory = categoryRepository.findByParentIsNull();
		
		// Gọi hàm đệ quy để làm phẳng từ tầng Java
		for (CategoryEntity root : rootCategory) {
			flatten(root, flatList, "");
		}
		return flatList;
	}
	
	private void flatten(CategoryEntity category, List<CategoryResponseDto> flatList, String prefix) {
	    CategoryResponseDto dto = new CategoryResponseDto();
	    dto.setId(category.getId());
	    // Cộng chuỗi prefix (ví dụ: "-- ") trực tiếp vào tên từ Java
	    dto.setName(prefix + category.getName());
	    flatList.add(dto);

	    if (category.getChildren() != null) {
	        for (CategoryEntity child : category.getChildren()) {
	            // Mỗi tầng con đi xuống sâu hơn thì cộng thêm dấu "--"
	            flatten(child, flatList, prefix + "--- ");
	        }
	    }
	}
	
	@Override
	@Transactional
	public void saveCategory(@NotNull CategoryRequestDto categoryDto) {
		CategoryEntity categoryEntity = categoryMapper.toEntity(categoryDto);
		if (Objects.nonNull(categoryDto) && categoryDto.getParent() != 0) {
			CategoryEntity parentCategory = categoryRepository.findById(categoryDto.getParent())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục cha có ID: " + categoryDto.getParent()));
			categoryEntity.setParent(parentCategory);
        }
		categoryRepository.save(categoryEntity);
	}
	
	@Override
	public CategoryEntity findById(int id) {
		CategoryEntity category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay Category co Id: " + id));
		return category;
	}
	
	@Override
	@Transactional
	public void updateCategory(int id, @NonNull CategoryRequestDto categoryDto) {
		CategoryEntity category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay Category co Id: " + id));
		categoryMapper.updateCategory(category, categoryDto);
		if (categoryDto.getParent() != null && categoryDto.getParent() != 0) {
	        CategoryEntity parentCategory = categoryRepository.findById(categoryDto.getParent())
	                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục cha có ID: " + categoryDto.getParent()));
	        category.setParent(parentCategory);
	    } else {
	        category.setParent(null); // Nếu chọn Root Category (id = 0) thì đưa về danh mục gốc
	    }
		
		categoryRepository.save(category);
	}
	
	@Override
	@Transactional
	public void deleteCategory(int id) {
		CategoryEntity category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay Category co Id: " + id));
		categoryRepository.delete(category);
	}
}
