package vpasound.com.core.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vpasound.com.core.entity.SystemCronjobEntity;


public interface SystemCronjobRepository extends JpaRepository<SystemCronjobEntity, Integer> {
	
	public Optional<SystemCronjobEntity> findByCronKey(String cronKey);
}
