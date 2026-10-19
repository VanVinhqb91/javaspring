package vpasound.com.fontend.home.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import vpasound.com.core.dto.response.fontend.LoginFontendResponseDto;
import vpasound.com.fontend.home.service.HomeService;


@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final HomeService homeService;
	private final RabbitTemplate rabbitTemplate;
	
	@Value("${app.rabbitmq.exchange}")
    private String exchange;
	
	@Value("${app.rabbitmq.routing-key}")
    private String routingKey;
	
	@GetMapping("/")
	public String homePage(HttpSession httpSession, Model model) {
		
		model.addAttribute("webTitle", homeService.getConfigByKey("web_title"));
		model.addAttribute("formLogin", new LoginFontendResponseDto());
		model.addAttribute("title", "TRANG CHỦ");

		return "fontend/homepage";
	}
	
	@PostMapping("/fontend/login")
	public String postLogin(
		@ModelAttribute(value = "formLogin") LoginFontendResponseDto user,
		HttpSession httpSession
	) {
		try {
			String username = homeService.login(user.getEmail(), user.getPassword());
			
			System.out.println("1. [Controller] Nhận yêu cầu đăng ký cho: " + user.getEmail());
			rabbitTemplate.convertAndSend(exchange, routingKey, user);
	        System.out.println("2. [Controller] Đã bắn tin nhắn vào RabbitMQ. Trả kết quả ngay cho User!");
	        
			if (username != null)
				httpSession.setAttribute("username", username);
		} catch (Exception e) {
			e.getMessage();
		}
		return "redirect:/";
	}
	
	@GetMapping("/chatbox")
	public String chatBox() {
		return "/fontend/chatbox";
	}
}
