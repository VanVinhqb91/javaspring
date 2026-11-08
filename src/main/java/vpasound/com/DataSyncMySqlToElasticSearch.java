package vpasound.com;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.catalogs.service.ProductService;
import vpasound.com.core.entity.ProductEntity;
import vpasound.com.fontend.elastic.ProductElastic;
import vpasound.com.fontend.elastic.ProductElasticRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSyncMySqlToElasticSearch implements CommandLineRunner {

	private final ProductService productService;
	private final ProductElasticRepository productElasticRepository;
	
	@Override
	public void run(String... args) throws Exception {
		// 1. Kiểm tra xem Elasticsearch đã có dữ liệu chưa
		long countInElastic = productElasticRepository.count();

		if (countInElastic == 0) {
			System.out.println("-> [Elasticsearch] Phát hiện dữ liệu trống. Bắt đầu đồng bộ từ MySQL XAMPP...");

			List<ProductEntity> allProductsInMySQL = productService.findAll();

			if (allProductsInMySQL != null && !allProductsInMySQL.isEmpty()) {
				List<ProductElastic> listProducts = new ArrayList<>();
				for (ProductEntity mysqlProd : allProductsInMySQL) {
					ProductElastic elasticDoc = new ProductElastic();
					elasticDoc.setId(String.valueOf(mysqlProd.getId()));
					elasticDoc.setName(mysqlProd.getName());
					elasticDoc.setPrice(mysqlProd.getPrice() != null ? mysqlProd.getPrice().intValue() : 0);
					listProducts.add(elasticDoc);
				}
				if (!listProducts.isEmpty()) {
					productElasticRepository.saveAll(listProducts);
					System.out.println("-> [Elasticsearch] Đồng bộ thành công " + listProducts.size() + " sản phẩm!");
				}
			} else {
				System.out.println("-> [Elasticsearch] Không có dữ liệu sản phẩm nào trong MySQL XAMPP để đồng bộ.");
			}
		} else {
			System.out.println("-> [Elasticsearch] Đã có sẵn " + countInElastic + " sản phẩm. Bỏ qua bước đồng bộ ban đầu.");
		}
	}
}