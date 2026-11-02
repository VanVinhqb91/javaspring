package vpasound.com.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vpasound.com.core.entity.AddressDistrictEntity;

public interface AddressDistrictRepository extends JpaRepository<AddressDistrictEntity, Long>{
	List<AddressDistrictEntity> findByProvinceId(Long provinceId);
}
