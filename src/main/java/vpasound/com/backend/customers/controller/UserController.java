package vpasound.com.backend.customers.controller;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.systems.service.SettingService;
import vpasound.com.backend.customers.service.UserService;
import vpasound.com.core.entity.UserEntity;

@Controller
@RequestMapping("/backend/customers/user")
@RequiredArgsConstructor
public class UserController {

	public final SettingService settingService;
	public final UserService userService;
	public final PasswordEncoder passwordEncoder;
	
	@GetMapping("")
	public String getUser(Model model) {
		List<UserEntity> user = userService.getAllUser();
		
		String webName = settingService.getSettingByKey("web_name").get().getSettingValue();
		String webVersion = settingService.getSettingByKey("web_version").get().getSettingValue();
		model.addAttribute("web_name", webName);
		model.addAttribute("web_version", webVersion);
		
		model.addAttribute("users", user);
		return "/backend/customers/user/list";
	}
	
	@GetMapping("/create") // Create Template Add User
	public String addUser(Model model) {

		String webName = settingService.getSettingByKey("web_name").get().getSettingValue();
		String webVersion = settingService.getSettingByKey("web_version").get().getSettingValue();
		model.addAttribute("web_name", webName);
		model.addAttribute("web_version", webVersion);
		model.addAttribute("createUser", new UserEntity());
		
		return "/backend/customers/user/create";
	}
	
	@PostMapping("/save") // Action Create New User
	public String createUser(@ModelAttribute("createUser") UserEntity user) {
		user.setUsername(user.getUsername());
		user.setBirthday(user.getBirthday());
		user.setEmail(user.getEmail());
		user.setImage("/image.png");
		user.setLocale(user.getLocale());
		user.setStatus(user.getStatus());
		user.setUser_role(user.getUser_role());
		user.setPassword(user.getPassword());
		
		userService.createUser(user);
		return "redirect:/backend/customers/user";
	}
	
	@GetMapping("/delete/{id}") // Action Deleted User
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUserById(id);
		return "redirect:/backend/customers/user";
	}
	
	@GetMapping("/update/{id}") // Show Template Update User
	public String updateUser(@PathVariable Long id, Model model) {
		UserEntity userEntity = userService.findUserById(id);
		
		model.addAttribute("updateUser", userEntity);
		
		return "/backend/customers/user/update";
	}
	
	@PostMapping("/update/{id}") // Action Update User By Id
	public String updateUserById(@PathVariable Long id, @ModelAttribute("updateUser") UserEntity user) {
		userService.updateUser(user, id);

		return "redirect:/backend/customers/user";
	}
}
