package vpasound.com.backend.systems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import vpasound.com.backend.systems.service.SettingService;

@Controller
@RequestMapping("/backend/systems/cronjob")
@RequiredArgsConstructor
public class CronjobController {

	private final SettingService settingService;
	
	@GetMapping("")
	public String getCronjob(Model model) {
		model.addAttribute("cronjobs", settingService.getCronJobSetting());
		return "backend/systems/cronjob/list";
	}
	
	@PostMapping("/update")
	public String cronJobUpdateSetting(
			@RequestParam(name = "product_cron_time_select") String productValue,
			@RequestParam(name = "price_cron_time_select") String priceValue,
			@RequestParam(name = "cron_advertising_campaign_select") String adCampaign,
			Model model
	) {
		settingService.updateCronJobSetting(productValue, priceValue, adCampaign);
		return "redirect:/backend/systems/cronjob";
	}
}
