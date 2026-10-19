package vpasound.com.core.mapper;

import org.mapstruct.Mapper;

import vpasound.com.core.dto.response.fontend.LoginFontendResponseDto;
import vpasound.com.core.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	// Map tu UserEntity sang LoginFontendResponseDto de dua data vao Form Login
	LoginFontendResponseDto toResponseFormLogin(UserEntity userEntity);
	
	// 2. Map từ Entity sang Response DTO để trả về cho Client
    // Nếu tên trường khác nhau (status -> active), sử dụng @Mapping để chỉ định rõ nguồn (source) và đích (target)
    // @Mapping(source = "status", target = "active", qualifiedByName = "statusToBoolean")
    // UserResponse toResponse(User entity);

    // Hàm bổ trợ tự viết để đổi kiểu dữ liệu từ String "ACTIVE" sang boolean true/false
    // @org.mapstruct.Named("statusToBoolean")
    // default boolean statusToBoolean(String status) {
    //    return "ACTIVE".equalsIgnoreCase(status);
    //}
}
