package vpasound.com.fontend.product.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import vpasound.com.core.dto.response.fontend.ProductViewKafkaResponseDto;
import vpasound.com.core.entity.ProductEntity;
import vpasound.com.core.service.KafkaService;
import vpasound.com.fontend.product.service.ProductService;

@Controller("frontendProductController")
@RequiredArgsConstructor
public class ProductController {
	public final ProductService productService;
	public final KafkaService kafkaService;
	
	@GetMapping("/san-pham/{slug}-sid{id}.html")
	public String viewProduct(
			@PathVariable String slug,
			@PathVariable Long id,
			Model model
	) {
		ProductEntity product = productService.getProductById(id);
		model.addAttribute("product", product);
		model.addAttribute("currentUser", "user_003");
		
		return "fontend/products/product_detail";
	}
	
	@PostMapping("/product/api/tracking/activity")
	@ResponseBody
	public String trackingActivity(@RequestBody ProductViewKafkaResponseDto event) {
		if (event.getTimestamp() == null) {
			event.setTimestamp(LocalDateTime.now().toString());
		}
		String stringKey = event.getUserId() != null ? String.valueOf(event.getUserId()) : "GUEST";

		kafkaService.sendActivity("vpa_tracking_activity", stringKey, event);
		return "Da gui len Kafka thanh cong";
	}
}
