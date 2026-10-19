package vpasound.com.backend.systems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.systems.service.SettingService;
import vpasound.com.core.entity.SettingEntity;

@Controller
@RequestMapping("/backend/systems/setting")
@RequiredArgsConstructor
public class SettingController {
	
	private final SettingService settingService;
	
	@GetMapping("")
	public String getSetting(Model model) {
		model.addAttribute("settings", settingService.getSettings());
		return "/backend/systems/setting/list";
	}
	
	@GetMapping("/update/{key}")
	public String updateSetting(
		@PathVariable String key,
		Model model
	) {
		model.addAttribute("formCreateSetting", settingService.getSettingByKey(key));
		return "/backend/systems/setting/update";
	}
	
	@PostMapping("/update/{key}")
	public String postCreateSetting(
		@PathVariable String key,
		@ModelAttribute(value = "formCreateSetting") SettingEntity settingEntity,
		Model model
	) {
		settingService.updateSettingByKey(key, settingEntity);
		
		return "redirect:/backend/systems/setting";
	}
}
