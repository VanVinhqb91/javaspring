package vpasound.com.backend.sales.service;

import java.util.List;
import java.util.Optional;

import vpasound.com.core.dto.request.backend.UpdateOrderTotalBlockDto;
import vpasound.com.core.dto.response.backend.OrderResponseDto;
import vpasound.com.core.entity.OrderEntity;

public interface OrderService {
	public List<OrderResponseDto.ViewAllOrder> getAllOrder();
	
	public Optional<OrderEntity> findOrderById(Long id);
	
	public void updateTotalBlock(Long id, UpdateOrderTotalBlockDto updateOrderTotalBlockDto);
}
