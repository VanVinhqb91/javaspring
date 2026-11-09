package vpasound.com.backend.systems.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import lombok.RequiredArgsConstructor;

import vpasound.com.core.entity.SettingEntity;
import vpasound.com.core.entity.TrafficStatsEntity;
import vpasound.com.core.repository.ProductRepository;
import vpasound.com.core.repository.SettingRepository;
import vpasound.com.core.repository.TrafficStatsRepository;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class DynamicCronjobScheduler implements SchedulingConfigurer{
	
	private final ProductRepository productRepository;
	private final SettingRepository settingRepository;
	private final StringRedisTemplate redisTemplate;
	private final TrafficStatsRepository trafficStatsRepository;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(
			() -> {
					System.out.println("Cron Job bat dau chay");
					int updateCount = productRepository.updateProductStatusFromZeroToOne();
					System.out.println("Da tu dong cap nhat: " + updateCount + " San pham.");
			},
			triggerContext -> {
					Optional<SettingEntity> cronSetting = settingRepository.findById("product_cron_time");
					String cronExpression = cronSetting.isPresent() ? cronSetting.get().getSettingValue() 
							: "0 0 0 * * ?";
					System.out.println("Lịch chạy tiếp theo dựa trên cấu hình database: " + cronExpression);
					CronTrigger trigger = new CronTrigger(cronExpression);
					return trigger.nextExecution(triggerContext);
			}
		);
	}
	
	@Scheduled(cron = "0 59 23 * * *")
	public void syncTrafficToDatabase() {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String redisKey = "Traffic:" + todayStr;

        // Lấy tổng số lượng unique visitor từ HyperLogLog của Redis
        Long count = redisTemplate.opsForHyperLogLog().size(redisKey);

        if (count != null && count > 0) {
            TrafficStatsEntity stat = new TrafficStatsEntity();
            stat.setRecordDate(today);
            stat.setVisitorCount(count);
            trafficStatsRepository.save(stat);
            
            redisTemplate.delete(redisKey);
        }
    }
}
