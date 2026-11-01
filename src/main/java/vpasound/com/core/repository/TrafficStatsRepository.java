package vpasound.com.core.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vpasound.com.core.entity.TrafficStatsEntity;

public interface TrafficStatsRepository extends JpaRepository<TrafficStatsEntity, Long>{
	@Query("SELECT SUM(t.visitorCount) FROM TrafficStatsEntity t WHERE t.recordDate BETWEEN :startDate AND :endDate")
	Long sumVisitorCountBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
