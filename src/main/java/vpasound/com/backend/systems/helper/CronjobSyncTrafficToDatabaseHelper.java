package vpasound.com.backend.systems.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import vpasound.com.core.entity.TrafficStatsEntity;
import vpasound.com.core.repository.SystemCronjobRepository;
import vpasound.com.core.repository.TrafficStatsRepository;

@Component
public class CronjobSyncTrafficToDatabaseHelper extends CronjobBaseDynamicHelper {

    private final StringRedisTemplate redisTemplate;
    private final TrafficStatsRepository trafficStatsRepository;

    public static final String CRON_KEY = "sync_traffic_redis_to_database";
    
    public CronjobSyncTrafficToDatabaseHelper(
    		SystemCronjobRepository systemCronjobRepository,
    		StringRedisTemplate redisTemplate, 
    		TrafficStatsRepository trafficStatsRepository
	) {
    	super(systemCronjobRepository);
    	this.redisTemplate = redisTemplate;
    	this.trafficStatsRepository = trafficStatsRepository;
    }

    @Override
    public String getCronKey() {
        return CRON_KEY;
    }

    @Override
    public void executeTask() {
        try {
            LocalDate nowDate = LocalDate.now(VIETNAM_ZONE);
            // Nếu chạy lúc 00:00 sáng thì chốt số liệu của ngày hôm qua
            LocalDate targetDate = (LocalDateTime.now(VIETNAM_ZONE).getHour() == 0)
                    ? nowDate.minusDays(1)
                    : nowDate;

            String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
            String redisKey = "Traffic:" + dateStr;

            Long count = redisTemplate.opsForHyperLogLog().size(redisKey);

            if (count != null && count > 0) {
                TrafficStatsEntity stat = new TrafficStatsEntity();
                stat.setRecordDate(targetDate);
                stat.setVisitorCount(count);
                trafficStatsRepository.save(stat);

                redisTemplate.delete(redisKey);
                System.out.println("✅ [CRON] Đã đồng bộ " + count + " traffic ngày " + dateStr + " vào DB.");
            }
        } catch (Exception e) {
            System.err.println("❌ [CRON] Lỗi đồng bộ traffic từ Redis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
