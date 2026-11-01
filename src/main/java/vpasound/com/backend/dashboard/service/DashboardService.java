package vpasound.com.backend.dashboard.service;

import java.util.Map;

public interface DashboardService {
	public Map<String, Object> getMetrics();
	public Map<String, Long> getTrafficStats();
}
