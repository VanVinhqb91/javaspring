package vpasound.com.backend.dashboard.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
		Map<String, Object> metrics = dashboardService.getMetrics();
        model.addAttribute("metrics", metrics);
		return "backend/dashboard";
	}
	
	// 2. API phụ trả về JSON giúp JavaScript gọi lấy số liệu mới mỗi 3 giây
    @GetMapping("/api/metrics")
    @ResponseBody
    public Map<String, Object> getMetricsApi() {
        return dashboardService.getMetrics();
    }
}
