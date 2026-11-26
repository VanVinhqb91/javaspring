package vpasound.com.backend.dashboard.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import vpasound.com.backend.dashboard.service.DashboardService;
import vpasound.com.backend.systems.service.SettingService;

@Controller
@RequestMapping("/backend")
@RequiredArgsConstructor
public class DashBoardController {

	public final SettingService settingService;
	public final DashboardService dashboardService;
	
	@GetMapping("/dashboard")
	public String dashBoardPage(Model model) {
		Map<String, Long> trafficStats = dashboardService.getTrafficStats();

        // 2. Đẩy từng chỉ số vào Model để Thymeleaf có thể đọc được
        model.addAttribute("todayCount", trafficStats.get("today"));
        model.addAttribute("weekCount", trafficStats.get("week"));
        model.addAttribute("monthCount", trafficStats.get("month"));
        model.addAttribute("yearCount", trafficStats.get("year"));
		return "backend/dashboard";
	}
}
