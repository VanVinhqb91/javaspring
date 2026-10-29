package vpasound.com.core.mapper;

import org.mapstruct.Mapper;

import vpasound.com.core.dto.response.backend.PageResponseDto;
import vpasound.com.core.entity.PageEntity;

@Mapper(componentModel = "spring")
public interface PageMapper {
	
	PageResponseDto.CreatePage mapCreatePageToEntity(PageEntity pageEntity);
}
