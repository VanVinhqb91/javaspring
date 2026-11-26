package vpasound.com.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vpasound.com.core.entity.MediaFileEntity;

public interface MediaFileRepository extends JpaRepository<MediaFileEntity, Long>{
	List<MediaFileEntity> findAllByOrderByCreatedAtDesc();
	List<MediaFileEntity> findByFolder(String folder);
}
