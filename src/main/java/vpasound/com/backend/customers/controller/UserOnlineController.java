package vpasound.com.backend.customers.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import vpasound.com.backend.customers.service.UserOnlineService;
import vpasound.com.core.entity.UserEntity;

@Controller
@RequestMapping("/backend/customers/user_online")
@RequiredArgsConstructor
public class UserOnlineController {
	
	private final UserOnlineService userOnlineService;
	
	@GetMapping("")
	public String listUserOnline(Model model) {
		
		List<UserEntity> usersOnline = userOnlineService.listUserOnline();

		model.addAttribute("usersOnline", usersOnline);
		model.addAttribute("countUser", usersOnline.size());
		
		return "/backend/customers/useronline/list";
	}
	
	@GetMapping("/view/{id}")
	public String viewUserOnline(
			@PathVariable Long id,
			Model model
	) {
		return "/backend/customers/useronline/view";
	}
}
