package vpasound.com.core.mapper;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import vpasound.com.core.dto.request.backend.UpdateOrderTotalBlockDto;
import vpasound.com.core.dto.response.backend.OrderResponseDto;
import vpasound.com.core.entity.OrderEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {
	
	// Response Dto Mapper
	@Mapping(target = "finalPriceFormated", source = "finalPrice", qualifiedByName = "formatCurrency")
	OrderResponseDto.ViewAllOrder mapToViewAllDto(OrderEntity orderEntity);
	List<OrderResponseDto.ViewAllOrder> mapViewAllOrderDtoList(List<OrderEntity> orderEntity);
	
	// Request Dto Mapper
	void updateOrderTotalBlock(@MappingTarget OrderEntity order, UpdateOrderTotalBlockDto orderTotalBlockDto);
	
	@Named("formatCurrency")
	default String formatCurrency(Double finalPrice) {
		if (finalPrice == null) return "0 VND";
		Locale localeVN = Locale.of("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(finalPrice);
	}
	
	@Named("createdFormated")
    default String formatDateTime(java.time.LocalDateTime createdAt) {
        if (createdAt == null) return "";
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt.format(formatter);
    }
}
