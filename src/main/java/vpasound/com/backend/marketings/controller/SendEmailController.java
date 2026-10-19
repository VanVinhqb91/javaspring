package vpasound.com.backend.marketings.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import vpasound.com.backend.marketings.service.SendEmailService;

@Controller
@RequestMapping("/backend")
@AllArgsConstructor
public class SendEmailController {
	
	private final SendEmailService sendEmailService;
	
	@GetMapping("/sales/order/email/{id}")
	public String sendOrderEmail(
			@PathVariable int id,
			Model model
	) {
		String toEmail = "vanvinhqb91.it@gmail.com";
		String customerName = "Ho Van Vinh";
		String totalPrice = "12500000";
		String paymentStatus = "1";
		model.addAttribute("orderId", Integer.toString(id));
		model.addAttribute("customerName", customerName);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentStatus", paymentStatus);
		
		sendEmailService.sendOrderConfirmationEmail(toEmail, customerName, Integer.toString(id), totalPrice, paymentStatus);
		return "/backend/marketings/email/order_email";
	}
}
