package vpasound.com;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.catalogs.service.ProductService;
import vpasound.com.core.entity.ProductEntity;
import vpasound.com.fontend.elastic.ProductElastic;
import vpasound.com.fontend.elastic.ProductElasticRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSyncMySqlToElasticSearch implements CommandLineRunner {

	private final ProductService productService; // Đọc từ MySQL của bạn
	private final ProductElasticRepository productElasticRepository; // Ghi vào Elastic Docker
	
	@Override
	public void run(String... args) throws Exception {
		// 1. Kiểm tra xem Elasticsearch đã có dữ liệu chưa
		long countInElastic = productElasticRepository.count();

		if (countInElastic == 0) {
			System.out.println("-> [Elasticsearch] Phát hiện dữ liệu trống. Bắt đầu đồng bộ từ MySQL XAMPP...");

			// 2. Lấy toàn bộ danh sách sản phẩm từ MySQL (Lưu ý: Nếu hàm này chưa có, bạn
			// hãy viết trong ProductService nhé)
			List<ProductEntity> allProductsInMySQL = productService.findAll();

			if (allProductsInMySQL != null && !allProductsInMySQL.isEmpty()) {
				for (ProductEntity mysqlProd : allProductsInMySQL) {
					ProductElastic elasticDoc = new ProductElastic();
					elasticDoc.setId(String.valueOf(mysqlProd.getId())); // Chuyển Long ID sang String
					elasticDoc.setName(mysqlProd.getName());

					// Xử lý ép kiểu giá tiền an toàn (đề phòng giá bên MySQL bị Null)
					if (mysqlProd.getPrice() != null) {
						elasticDoc.setPrice(mysqlProd.getPrice().intValue());
					} else {
						elasticDoc.setPrice(0);
					}

					// Lưu sản phẩm vào Elasticsearch Docker
					productElasticRepository.save(elasticDoc);
				}
				System.out.println("-> [Elasticsearch] Đồng bộ thành công " + allProductsInMySQL.size() + " sản phẩm!");
			} else {
				System.out.println("-> [Elasticsearch] Không có dữ liệu sản phẩm nào trong MySQL XAMPP để đồng bộ.");
			}
		} else {
			System.out.println("-> [Elasticsearch] Đã có sẵn " + countInElastic + " sản phẩm. Bỏ qua bước đồng bộ ban đầu.");
		}
	}
}