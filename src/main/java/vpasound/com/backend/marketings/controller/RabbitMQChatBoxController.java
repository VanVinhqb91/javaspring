package vpasound.com.backend.marketings.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/backend/marketings")
public class RabbitMQChatBoxController {

	@GetMapping("/admin_chatbox")
	public String adminChatBoxSupport() {
		
		return "";
	}
}
