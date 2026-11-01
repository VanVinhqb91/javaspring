package vpasound.com.backend.dashboard.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import vpasound.com.backend.dashboard.service.DashboardService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private final MeterRegistry meterRegistry;
	private final DockerClient dockerClient;
	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${rabbitmq.management.url}")
	private String rabbitMqUrl;

	@Value("${rabbitmq.management.username}")
	private String rabbitUsername;

	@Value("${rabbitmq.management.password}")
	private String rabbitPassword;

	@Override
	public Map<String, Object> getMetrics() {
		Map<String, Object> metrics = new HashMap<>();

		// 1. LẤY SỐ LIỆU CPU & RAM CỦA HỆ THỐNG
		double cpuSystem = 0;
		var cpuMeter = meterRegistry.find("system.cpu.usage").gauge();
		if (cpuMeter != null)
			cpuSystem = cpuMeter.value() * 100; // Đổi sang %

		double ramUsed = 0;
		var ramMeter = meterRegistry.find("jvm.memory.used").gauge();
		if (ramMeter != null)
			ramUsed = ramMeter.value() / (1024 * 1024); // Đổi sang MB

		Map<String, Object> systemMetrics = new HashMap<>();
		systemMetrics.put("cpuUsagePercent", Math.round(cpuSystem * 100.0) / 100.0);
		systemMetrics.put("ramUsedMB", Math.round(ramUsed));
		metrics.put("system", systemMetrics);

		// 2. LẤY TRẠNG THÁI DOCKER CONTAINERS
		List<Map<String, Object>> dockerContainers = new ArrayList<>();
		try {
			List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
			for (Container container : containers) {
				Map<String, Object> cMap = new HashMap<>();

				if (container.getNames() != null && container.getNames().length > 0) {
					cMap.put("name", container.getNames()[0]);
				} else if (container.getId() != null) {
					cMap.put("name", container.getId().substring(0, 12)); // Nếu container không có tên, lấy tạm 12 ký
																			// tự ID ngắn
				} else {
					cMap.put("name", "Unknown Container");
				}

				cMap.put("status", container.getStatus());
				cMap.put("state", container.getState());

				// TÍNH TOÁN RAM TIÊU THỤ CỦA CONTAINER
				String memoryUsageStr = "0 MB";
				if ("running".equals(container.getState())) {
					try {
						// Định nghĩa một Class tường minh để hứng dữ liệu Stats một cách hợp lệ
						class CustomStatsCallback extends
								com.github.dockerjava.api.async.ResultCallback.Adapter<com.github.dockerjava.api.model.Statistics> {
							private com.github.dockerjava.api.model.Statistics latestStats;

							@Override
							public void onNext(com.github.dockerjava.api.model.Statistics object) {
								this.latestStats = object;
								try {
									this.close();
								} catch (Exception ignored) {
								} // Đóng stream ngay khi có dữ liệu
							}

							public com.github.dockerjava.api.model.Statistics getLatestStats() {
								return latestStats;
							}
						}

						// Khởi tạo instance từ Class tường minh vừa tạo
						CustomStatsCallback statsCallback = new CustomStatsCallback();
						dockerClient.statsCmd(container.getId()).exec(statsCallback);

						// Chờ tối đa 1 giây để Docker trả về dữ liệu RAM
						statsCallback.awaitCompletion(1, java.util.concurrent.TimeUnit.SECONDS);
						com.github.dockerjava.api.model.Statistics stats = statsCallback.getLatestStats();

						if (stats != null && stats.getMemoryStats() != null
								&& stats.getMemoryStats().getUsage() != null) {
							long usageBytes = stats.getMemoryStats().getUsage();
							double usageMB = (double) usageBytes / (1024 * 1024);
							memoryUsageStr = String.format("%.1f MB", usageMB);
						}
					} catch (Exception ignored) {
						memoryUsageStr = "N/A";
					}
				} else {
					memoryUsageStr = "0 MB (Stopped)";
				}

				cMap.put("memory", memoryUsageStr);
				dockerContainers.add(cMap);
			}
		} catch (Exception e) {
			metrics.put("dockerError", "Không kết nối được Docker: " + e.getMessage());
			Map<String, Object> emptyRabbit = new HashMap<>();
			emptyRabbit.put("messages_ready", 0);
			emptyRabbit.put("messages_unacknowledged", 0);
			metrics.put("rabbitmq", emptyRabbit);
		}
		metrics.put("docker", dockerContainers);

		// 3. LẤY CHỈ SỐ RABBITMQ (Gọi qua REST API của Management Plugin)
		try {
			// Sử dụng HTTP Basic Auth để gọi API của RabbitMQ
			// Để đơn giản ở đây truyền trực tiếp, thực tế nên dùng HttpHeaders của Spring
			String urlWithAuth = rabbitMqUrl.replace("http://",
					"http://" + rabbitUsername + ":" + rabbitPassword + "@");
			Map<String, Object> rabbitResponse = restTemplate.getForObject(urlWithAuth, Map.class);

			if (rabbitResponse != null && rabbitResponse.containsKey("queue_totals")) {
				metrics.put("rabbitmq", rabbitResponse.get("queue_totals"));
				// Sẽ trả về các trường như: messages_ready, messages_unacknowledged, messages
			}
		} catch (Exception e) {
			metrics.put("rabbitmqError", "Không kết nối được RabbitMQ API: " + e.getMessage());
		}

		return metrics;
	}
}
