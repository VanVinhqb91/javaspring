package vpasound.com.backend.sales.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vpasound.com.backend.sales.service.AddressService;
import vpasound.com.core.dto.response.backend.AddressResponseDto;
import vpasound.com.core.repository.AddressDistrictRepository;
import vpasound.com.core.repository.AddressProvinceRepository;
import vpasound.com.core.repository.AddressWardRepository;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
	private final AddressProvinceRepository addressProvinceRepository;
	private final AddressDistrictRepository addressDistrictRepository;
	private final AddressWardRepository addressWardRepository;
	
	@Override
	public List<AddressResponseDto> getAllProvinces() {
        return addressProvinceRepository.findAll().stream()
                .map(p -> new AddressResponseDto(p.getId(), p.getCode(), p.getName()))
                .collect(Collectors.toList());
    }

	@Override
    public List<AddressResponseDto> getDistrictsByProvince(Long provinceId) {
        return addressDistrictRepository.findByProvinceId(provinceId).stream()
                .map(d -> new AddressResponseDto(d.getId(), d.getCode(), d.getName()))
                .collect(Collectors.toList());
    }

	@Override
    public List<AddressResponseDto> getWardsByDistrict(Long districtId) {
        return addressWardRepository.findByDistrictId(districtId).stream()
                .map(w -> new AddressResponseDto(w.getId(), w.getCode(), w.getName()))
                .collect(Collectors.toList());
    }
}
