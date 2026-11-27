package vpasound.com.backend.systems.service;

import vpasound.com.core.entity.SystemCronjobEntity;

public interface CronjobService {

	public void updateCronjob(Integer id, SystemCronjobEntity systemCronjobEntity);
	public void createCronjob(SystemCronjobEntity systemCronjobEntity);
}
