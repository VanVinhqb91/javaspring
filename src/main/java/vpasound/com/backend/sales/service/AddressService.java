package vpasound.com.backend.sales.service;

import java.util.List;

import vpasound.com.core.dto.response.backend.AddressResponseDto;

public interface AddressService {
	public List<AddressResponseDto> getAllProvinces();
	public List<AddressResponseDto> getDistrictsByProvince(Long provinceId);
	public List<AddressResponseDto> getWardsByDistrict(Long districtId);
}
