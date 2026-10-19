package vpasound.com.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vpasound.com.core.entity.SettingEntity;

public interface SettingRepository extends JpaRepository<SettingEntity, String>{
	
	List<SettingEntity> findBySettingDefault(String def);
}
