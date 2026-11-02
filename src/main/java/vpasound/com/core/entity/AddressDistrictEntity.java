package vpasound.com.core.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "vpa_district",
	indexes = {
	        @Index(name = "idx_district_province_id", columnList = "province_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDistrictEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    private String code;

    private String name;
    
    @Column(name = "province_id", insertable = false, updatable = false)
    private Long provinceId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = true)
    private AddressProvinceEntity vpa_province;
    
    @OneToMany(mappedBy = "vpa_district", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AddressWardEntity> wardEntities;
}
