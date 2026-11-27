package vpasound.com.backend.systems.helper;

import java.time.ZoneId;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import vpasound.com.core.entity.SystemCronjobEntity;
import vpasound.com.core.repository.SystemCronjobRepository;

@RequiredArgsConstructor
public abstract class CronjobBaseDynamicHelper {

    protected final SystemCronjobRepository systemCronjobRepository;

    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private ScheduledFuture<?> scheduledTask;

    protected static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final TimeZone VIETNAM_TZ = TimeZone.getTimeZone(VIETNAM_ZONE);
    private static final String DEFAULT_CRON = "0 0 0 * * ?";

    // Mỗi file con sẽ tự khai báo cron_key và hàm chạy riêng
    public abstract String getCronKey();
    public abstract void executeTask();

    @PostConstruct
    public void init() {
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("cron-" + getCronKey() + "-");
        taskScheduler.initialize();
        reloadSchedule();
    }

    // Tự động hủy lịch cũ và nạp lịch mới ngay lập tức
    public synchronized void reloadSchedule() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
        }

        Optional<SystemCronjobEntity> cronSetting = systemCronjobRepository.findByCronKey(getCronKey());

        if (cronSetting.isEmpty() || !Boolean.TRUE.equals(cronSetting.get().getStatus())) {
            System.out.println("⏸️ [CRON] Job [" + getCronKey() + "] đang TẮT.");
            return;
        }

        String cronExpression = cronSetting.get().getCronValue();
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            cronExpression = DEFAULT_CRON;
        }

        try {
            CronTrigger trigger = new CronTrigger(cronExpression.trim(), VIETNAM_TZ);
            scheduledTask = taskScheduler.schedule(this::executeTask, trigger);
            System.out.println("✅ [CRON] Đã kích hoạt [" + getCronKey() + "] lịch: " + cronExpression);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ [CRON] Sai cú pháp cron cho [" + getCronKey() + "]: " + cronExpression);
        }
    }
}
