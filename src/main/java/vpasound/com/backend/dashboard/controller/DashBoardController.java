package vpasound.com.backend.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.systems.service.SettingService;

@Controller
@RequestMapping("/backend")
@RequiredArgsConstructor
public class DashBoardController {

	public final SettingService settingService;
	
	@GetMapping("/dashboard")
	public String dashBoardPage(Model model) {

		return "backend/dashboard";
	}
}
