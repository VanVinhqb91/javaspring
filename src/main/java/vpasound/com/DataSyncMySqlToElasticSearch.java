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
		
		long countInElastic = productElasticRepository.count();
		long totalProductDatabase = productService.totalProduct();
		
		if (countInElastic != totalProductDatabase) {
			productElasticRepository.deleteAll();
			System.out.println("[Elastic - Product] Phát hiện dữ liệu trống. Bắt đầu đồng bộ từ MySQL");
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
					System.out.println("[Elastic - Product] Đồng bộ thành công " + listProducts.size() + " sản phẩm!");
				}
			} else {
				System.out.println("[Elastic - Product] Không có dữ liệu sản phẩm nào trong MySQL để đồng bộ.");
			}
		} else {
			System.out.println("[Elastic - Product] Database trong Mysql và Elasticsearch đã giống nhau => Bỏ qua đồng bộ.");
		}
	}
}
