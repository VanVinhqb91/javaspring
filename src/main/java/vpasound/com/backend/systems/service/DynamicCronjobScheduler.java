package vpasound.com.backend.systems.service;

import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import lombok.RequiredArgsConstructor;

import vpasound.com.core.entity.SettingEntity;
import vpasound.com.core.repository.ProductRepository;
import vpasound.com.core.repository.SettingRepository;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class DynamicCronjobScheduler implements SchedulingConfigurer{
	
	private final ProductRepository productRepository;
	private final SettingRepository settingRepository;
	
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
}
