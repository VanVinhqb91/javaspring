package vpasound.com.backend.sales.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import vpasound.com.backend.sales.service.OrderService;
import vpasound.com.core.dto.request.backend.UpdateOrderTotalBlockDto;
import vpasound.com.core.entity.OrderEntity;

@Controller
@RequestMapping("/backend/sales/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	
	@GetMapping("")
	public String getOrder(Model model) {
		model.addAttribute("orders", orderService.getAllOrder());
		return "/backend/sales/order/list";
	}
	
	@GetMapping("/view/{id}")
	public String viewOrder(
			@PathVariable @NonNull Long id,
			Model model
	) {
		Optional<OrderEntity> orderEntity = orderService.findOrderById(id);
		if (orderEntity.isPresent()) 
			model.addAttribute("order", orderEntity.get());
		model.addAttribute("orderId", id);
		
		return "/backend/sales/order/view";
	}
	
	@PostMapping("/view/{id}")
	public String updateOrderTotalBlock(
			@PathVariable @NonNull Long id,
			@RequestParam(name = "status") String status,
			@RequestParam(name = "note") String note,
			RedirectAttributes redirectAttributes
	) {
		try {
			UpdateOrderTotalBlockDto orderTotalBlockDto = new UpdateOrderTotalBlockDto();
			orderTotalBlockDto.setStatus(status);
			orderTotalBlockDto.setNote(note);
			orderService.updateTotalBlock(id, orderTotalBlockDto);
			redirectAttributes.addFlashAttribute("successMessage", "Update Status and Note Order Successed");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error when to Update Status / Note Order: " + e.getMessage());
		}
		
		return "redirect:/backend/sales/order/view/" + id;
	}
}
