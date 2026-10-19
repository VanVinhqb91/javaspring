package vpasound.com.fontend.elastic;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductElasticRepository extends ElasticsearchRepository<ProductElastic, String>{
	// Tìm kiếm gợi ý theo tên sản phẩm dựa trên bộ analyzer đã cấu hình
    List<ProductElastic> findByName(String name, Pageable pageable);
}
