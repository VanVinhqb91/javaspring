package vpasound.com.backend.catalogs.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.catalogs.service.BrandService;
import vpasound.com.core.dto.request.backend.BrandRequestDto;
import vpasound.com.core.entity.BrandEntity;
import vpasound.com.core.mapper.BrandMapper;
import vpasound.com.core.repository.BrandRepository;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService{
	
	@Value("${upload.path_brands}")
	private String uploadDirBrand;
	
	private final BrandMapper brandMapper;
	private final BrandRepository brandRepository;
	
	@Override
	public List<BrandEntity> getAllBrand() {
		List<BrandEntity> brands = brandRepository.findAll();
		return brands;
	}
	
	@Override
	public BrandEntity findBrandById(int id) {
		BrandEntity brand = brandRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay Brand ID: " + id));
		return brand;
	}
	
	@Override
	public void createBrand(BrandEntity brandEntity, MultipartFile file) {
		
		this.uploadFileLogoBrand(file);
		
		// Save Brand to Database
		int status = brandEntity.getStatus() == null ? 0 : 1;
		String imgFile = file.getOriginalFilename().isEmpty() ? "brand.png" : file.getOriginalFilename();
		brandEntity.setStatus(status);
		brandEntity.setUrlLogo(imgFile);
					
		brandRepository.save(brandEntity);
	}
	
	// Function upload Logo Brand
	public void uploadFileLogoBrand(MultipartFile file) {
		try {
			StringBuilder fileNames = new StringBuilder();
	        Path fileNameAndPath = Paths.get(uploadDirBrand, file.getOriginalFilename());
	        fileNames.append(file.getOriginalFilename());
	        Files.write(fileNameAndPath, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateBrand(
		int id, 
		BrandRequestDto brandRequestDto
	) {
		BrandEntity brand = this.findBrandById(id);
		int status = brandRequestDto.getStatus() == null ? 0 : 1;
		
		if (Objects.nonNull(brand)) {
			brandMapper.updateBrandMapper(brand, brandRequestDto);
			brand.setStatus(status);
			brandRepository.save(brand);
		}
	}
	
	public void deleteBrand(int id) {
		BrandEntity brand = this.findBrandById(id);
		if (Objects.nonNull(brand))
			brandRepository.delete(brand);
	}
}
