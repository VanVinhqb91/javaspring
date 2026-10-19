package vpasound.com.backend.catalogs.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vpasound.com.core.dto.request.backend.BrandRequestDto;
import vpasound.com.core.entity.BrandEntity;

public interface BrandService {
	public BrandEntity findBrandById(int id);
	
	public List<BrandEntity> getAllBrand();
	
	public void createBrand(BrandEntity brandEntity, MultipartFile file);
	
	public void updateBrand(int id, BrandRequestDto brandRequestDto);
	
	public void deleteBrand(int id);
}
