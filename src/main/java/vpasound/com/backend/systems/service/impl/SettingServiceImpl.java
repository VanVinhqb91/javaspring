package vpasound.com.backend.systems.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.systems.service.SettingService;
import vpasound.com.core.entity.SettingEntity;
import vpasound.com.core.repository.SettingRepository;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService{
	
	public final SettingRepository settingRepository;
	
	@Override
	public List<SettingEntity> getSettings() {
		List<SettingEntity> setEntity = settingRepository.findBySettingDefault("default");
		if (Objects.isNull(setEntity)) return null;
		return setEntity;
	}
	
	@Override
	public Optional<SettingEntity> getSettingByKey(String key) {
		Optional<SettingEntity> settingEntity = settingRepository.findById(key);
		
		if (!settingEntity.isPresent()) return null;
		return settingEntity;
	}
	
	@Override
	public void updateSettingByKey(String key, SettingEntity settingEntity) {
		SettingEntity setting = settingRepository.findById(key)
				.orElseThrow(() -> new RuntimeException("Khong tim thay key: " + key));
		
		setting.setSettingValue(settingEntity.getSettingValue());
		settingRepository.save(setting);
	}
	
	@Override
	public List<SettingEntity> getCronJobSetting() {
		List<SettingEntity> list = settingRepository.findBySettingDefault("cronjob");
		if (Objects.isNull(list)) return null;
		return list;
	}
	
	@Override
	public void updateCronJobSetting(String productValue, String priceValue, String adCampaign) {
		SettingEntity productEntity = settingRepository.findById("product_cron_time")
				.orElseThrow(() -> new RuntimeException("Khong tim thay key: product_cron_time"));
		
		SettingEntity priceEntity = settingRepository.findById("price_cron_time")
				.orElseThrow(() -> new RuntimeException("Khong tim thay key: price_cron_time"));
		
		SettingEntity campaignEntity = settingRepository.findById("cron_advertising_campaign")
				.orElseThrow(() -> new RuntimeException("Khong tim thay key: cron_advertising_campaign"));
		
		if (!Objects.isNull(productEntity)) {
			productEntity.setSettingValue(productValue);
			settingRepository.save(productEntity);
		}
		
		if (!Objects.isNull(priceEntity)) {
			priceEntity.setSettingValue(priceValue);
			settingRepository.save(priceEntity);
		}
		
		if (!Objects.isNull(campaignEntity)) {
			campaignEntity.setSettingValue(adCampaign);
			settingRepository.save(campaignEntity);
		}
	}
}
