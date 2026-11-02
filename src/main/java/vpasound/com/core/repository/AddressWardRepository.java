package vpasound.com.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vpasound.com.core.entity.AddressWardEntity;

public interface AddressWardRepository extends JpaRepository<AddressWardEntity, Long>{
	List<AddressWardEntity> findByDistrictId(Long districtId);
}
