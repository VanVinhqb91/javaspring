package vpasound.com.backend.systems.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.RequiredArgsConstructor;
import vpasound.com.backend.dashboard.service.DashboardService;
import vpasound.com.backend.systems.service.SettingService;
import vpasound.com.core.entity.SettingEntity;

@Controller
@RequestMapping("/backend/systems")
@RequiredArgsConstructor
public class SettingController {
	
	private final SettingService settingService;
	public final DashboardService dashboardService;
	
	@GetMapping("/setting")
	public String getSetting(Model model) {
		model.addAttribute("settings", settingService.getSettings());
		return "/backend/systems/setting/list";
	}
	
	@GetMapping("/setting/update/{key}")
	public String updateSetting(
		@PathVariable String key,
		Model model
	) {
		model.addAttribute("formCreateSetting", settingService.getSettingByKey(key));
		return "/backend/systems/setting/update";
	}
	
	@PostMapping("/setting/update/{key}")
	public String postCreateSetting(
		@PathVariable String key,
		@ModelAttribute(value = "formCreateSetting") SettingEntity settingEntity,
		Model model
	) {
		settingService.updateSettingByKey(key, settingEntity);
		
		return "redirect:/backend/systems/setting";
	}
	
	@GetMapping("/ram_docker")
	public String getDocker(Model model) {
		Map<String, Object> metrics = dashboardService.getMetrics();
		model.addAttribute("metrics", metrics);
		return "/backend/systems/setting/docker";
	}
	
	// 2. API phụ trả về JSON giúp JavaScript gọi lấy số liệu mới mỗi 3 giây
    @GetMapping("/api/metrics")
    @ResponseBody
    public Map<String, Object> getMetricsApi() {
        return dashboardService.getMetrics();
    }
}
