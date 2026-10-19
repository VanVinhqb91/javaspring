package vpasound.com.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import vpasound.com.core.dto.request.backend.CategoryRequestDto;
import vpasound.com.core.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "children", ignore = true)
	@Mapping(target = "parent", source = "parent", qualifiedByName = "idToCategoryEntity")
	CategoryEntity toEntity(CategoryRequestDto cateDto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "children", ignore = true)
	@Mapping(target = "parent", source = "parent", qualifiedByName = "idToCategoryEntity") // Áp dụng hàm map ở dưới
	void updateCategory(@MappingTarget CategoryEntity category, CategoryRequestDto categoryDTO);

	// Hàm chuyển đổi từ Long sang CategoryEntity
	@Named("idToCategoryEntity")
	default CategoryEntity idToCategoryEntity(Integer parentId) {
		if (parentId == null) {
			return null;
		}
		CategoryEntity parentEntity = new CategoryEntity();
		parentEntity.setId(parentId);
		return parentEntity;
	}
}
