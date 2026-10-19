package vpasound.com.backend.customers.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.customers.service.UserService;

@Controller
@RequestMapping("/backend")
@RequiredArgsConstructor
public class LoginController {
	
	public final UserService userService;
	
	@GetMapping("/login")
	public String getLogin() {
		return "/backend/customers/user/login";
	}
}
