package vpasound.com.fontend.elastic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductElasticService {

    @Autowired
    private ProductElasticRepository productElasticRepository;
    
    public ProductElasticService(ProductElasticRepository productElasticRepository) {
    	this.productElasticRepository = productElasticRepository;
    }

    public List<ProductElastic> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // Giới hạn lấy tối đa 6 kết quả gợi ý chuẩn nhất
        return productElasticRepository.findByName(keyword, PageRequest.of(0, 6));
    }
}
