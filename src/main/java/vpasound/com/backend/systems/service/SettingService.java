package vpasound.com.backend.systems.service;

import java.util.List;
import java.util.Optional;

import vpasound.com.core.entity.SettingEntity;

public interface SettingService {
	
	public List<SettingEntity> getSettings();
	
	public Optional<SettingEntity> getSettingByKey(String key);
	
	public void updateSettingByKey(String key, SettingEntity settingEntity);
	
	public List<SettingEntity> getCronJobSetting();
	
	public void updateCronJobSetting(String product, String price, String adCampaign);
}
