package vpasound.com.fontend.elastic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ProductSearchApiController {
	
	@Autowired
    private ProductElasticService productElasticService;
	
	public ProductSearchApiController(ProductElasticService productElasticService) {
		this.productElasticService = productElasticService;
	}

    // URL: /api/public/products/suggest?keyword=loa
    @GetMapping("/api/public/products/suggest")
    public List<ProductElastic> suggest(@RequestParam("keyword") String keyword) {
        return productElasticService.getSearchSuggestions(keyword);
    }
}
