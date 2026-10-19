package vpasound.com.backend.sales.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import vpasound.com.backend.sales.service.OrderService;
import vpasound.com.core.dto.request.backend.UpdateOrderTotalBlockDto;
import vpasound.com.core.dto.response.backend.OrderResponseDto;
import vpasound.com.core.entity.OrderEntity;
import vpasound.com.core.mapper.OrderMapper;
import vpasound.com.core.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
	
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	
	@Override
	public List<OrderResponseDto.ViewAllOrder> getAllOrder() {
		List<OrderEntity> orderEntity = orderRepository.findAll();
		List<OrderResponseDto.ViewAllOrder> viewOrders = orderMapper.mapViewAllOrderDtoList(orderEntity);
		if (Objects.nonNull(viewOrders)) return viewOrders;
		return null;
	}
	
	@Override
	public Optional<OrderEntity> findOrderById(@NonNull Long id) {
		Optional<OrderEntity> order = orderRepository.findById(id);
		if (order.isPresent()) return order;
		return null;
	}
	
	@Override
	public void updateTotalBlock(
			Long id, 
			UpdateOrderTotalBlockDto orderTotalBlockDto
	) {
		OrderEntity orderEntity = orderRepository.findById(id).
				orElseThrow(() -> new RuntimeException("Khong tim thay id: " + id));
		
		if (Objects.nonNull(orderEntity)) {
			orderTotalBlockDto.setId(id);
			orderMapper.updateOrderTotalBlock(orderEntity, orderTotalBlockDto);
			orderRepository.save(orderEntity);
		}
	}
}
