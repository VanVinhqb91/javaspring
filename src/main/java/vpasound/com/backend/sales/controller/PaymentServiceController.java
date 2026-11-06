package vpasound.com.backend.sales.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import vpasound.com.backend.sales.service.impl.PaymentServiceImpl;

@Controller
@RequiredArgsConstructor
@RequestMapping("/backend/sales/payment_services")
public class PaymentServiceController {
	
	private final PaymentServiceImpl paymentService;

	@GetMapping("")
	public String listPaymentService(Model model) {
		return "/backend/sales/payment_service/list";
	}
	
    @GetMapping("/api/pay")
    @ResponseBody
    public ResponseEntity<?> getMomoPayUrl(
    		@RequestParam String orderId, 
    		@RequestParam String amount, 
    		@RequestParam String orderInfo) throws InvalidKeyException, NoSuchAlgorithmException {
        Map<String, Object> result = paymentService.createVietQRPayment(orderId, amount, orderInfo);
        
        return ResponseEntity.ok(result);
    }
}
