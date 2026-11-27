package vpasound.com.backend.systems.service.impl;

import java.util.Objects;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vpasound.com.backend.systems.helper.CronjobBaseDynamicHelper;
import vpasound.com.backend.systems.service.SystemCronjobService;
import vpasound.com.core.entity.SystemCronjobEntity;
import vpasound.com.core.repository.SystemCronjobRepository;

@Service
@RequiredArgsConstructor
public class SystemCronjobServiceImpl implements SystemCronjobService{

	private final SystemCronjobRepository systemCronjobRepository;
	private final List<CronjobBaseDynamicHelper> cronjobHelpers;

	@Override
	public void updateCronjob(Integer id, SystemCronjobEntity cronjob) {
	    SystemCronjobEntity systemCronjob = systemCronjobRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Khong tim thay cron id: " + id));

	    String oldCronValue = systemCronjob.getCronValue();
	    Boolean oldStatus = systemCronjob.getStatus();

	    systemCronjob.setCronValue(cronjob.getCronValue());
	    systemCronjob.setName(cronjob.getName());
	    systemCronjob.setStatus(cronjob.getStatus());
	    systemCronjobRepository.save(systemCronjob);

	    boolean isCronChanged = !Objects.equals(oldCronValue, cronjob.getCronValue());
	    boolean isStatusChanged = !Objects.equals(oldStatus, cronjob.getStatus());

	    // Tự động tìm đúng Helper theo cronKey và reload ngay lập tức
	    if (isCronChanged || isStatusChanged) {
	        cronjobHelpers.stream()
	                .filter(helper -> helper.getCronKey().equals(systemCronjob.getCronKey()))
	                .findFirst()
	                .ifPresent(CronjobBaseDynamicHelper::reloadSchedule);
	    }
	}

	@Override
	public void createCronjob(SystemCronjobEntity systemCronjobEntity) {
		if (!Objects.isNull(systemCronjobEntity)) {
			systemCronjobRepository.save(systemCronjobEntity);
		}
	}
}
