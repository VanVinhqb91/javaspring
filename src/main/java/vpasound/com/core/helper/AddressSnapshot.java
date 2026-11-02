package vpasound.com.core.helper;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressSnapshot implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer provinceId;
    private Integer districtId;
    private Integer wardId;
    private String provinceName;
    private String districtName;
    private String wardName;
    private String detailAddress;
    private String fullAddress;
}
