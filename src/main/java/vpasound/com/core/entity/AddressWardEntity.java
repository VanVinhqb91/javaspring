package vpasound.com.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "vpa_ward",
	indexes = {
	        @Index(name = "idx_ward_district_id", columnList = "district_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressWardEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    private String code;

    private String name;
    
    @Column(name = "district_id", insertable = false, updatable = false)
    private Long districtId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = true)
    private AddressDistrictEntity vpa_district;
}
