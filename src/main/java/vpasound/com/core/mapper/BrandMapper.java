package vpasound.com.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import vpasound.com.core.dto.request.backend.BrandRequestDto;
import vpasound.com.core.entity.BrandEntity;

@Mapper(componentModel = "spring")
public interface BrandMapper {
	BrandEntity requestDtoToEntity(BrandRequestDto brandRequestDto);
	
	void updateBrandMapper(@MappingTarget BrandEntity brand, BrandRequestDto brandRequestDto);
}
