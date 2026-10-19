package vpasound.com.backend.catalogs.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.catalogs.service.BrandService;
import vpasound.com.backend.catalogs.service.CategoryService;
import vpasound.com.backend.catalogs.service.ProductService;
import vpasound.com.backend.systems.service.SettingService;
import vpasound.com.core.dto.response.backend.CategoryResponseDto;
import vpasound.com.core.entity.BrandEntity;
import vpasound.com.core.entity.ProductEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/backend/catalogs/product")
@RequiredArgsConstructor
public class ProductController {
	
	@Value("${upload.path_products}")
	private String uploadDirProduct;
	
	private final SettingService settingService;
	private final ProductService productService;
	private final CategoryService categoryService;
	private final BrandService brandService;
	
	@GetMapping("")
	public String getProduct(
		@RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
        @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
		Model model
	) {
		String webName = settingService.getSettingByKey("web_name").get().getSettingValue();
		String webVersion = settingService.getSettingByKey("web_version").get().getSettingValue();
		
		Page<ProductEntity> proPage = productService.getAllProduct(page, size, sortBy, sortDir);
		model.addAttribute("web_name", webName);
		model.addAttribute("web_version", webVersion);
		model.addAttribute("products", proPage);
		model.addAttribute("total_pro", productService.totalProduct());
		
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", proPage.getTotalPages());
		model.addAttribute("totalItems", proPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("size", size);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");
		
		return "/backend/catalogs/product/list";
	}
	
	@GetMapping("/create")
	public String addProduct(Model model) {
		String webName = settingService.getSettingByKey("web_name").get().getSettingValue();
		String webVersion = settingService.getSettingByKey("web_version").get().getSettingValue();
		
		model.addAttribute("web_name", webName);
		model.addAttribute("web_version", webVersion);
		model.addAttribute("createProduct", new ProductEntity());
		
		List<CategoryResponseDto> flatCategory = categoryService.getFlattenedCategoryTree();
		model.addAttribute("flatCate", flatCategory);
		
		List<BrandEntity> brands = brandService.getAllBrand();
		model.addAttribute("brands", brands);
		
		return "/backend/catalogs/product/create";
	}
	
	@PostMapping("/create")
	public String createProduct(
			@RequestParam(name="image-file") MultipartFile file,
			@RequestParam(value="action", required = true) String action, 
			@ModelAttribute("createProduct") ProductEntity product
	) throws IOException {
		try {
			StringBuilder fileNames = new StringBuilder();
	        Path fileNameAndPath = Paths.get(uploadDirProduct, file.getOriginalFilename());
	        fileNames.append(file.getOriginalFilename());
	        Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
        	e.printStackTrace();
        }
		
		String imgFile = file.getOriginalFilename().isEmpty() ? "vanhyaudio.png" : file.getOriginalFilename();
		int status = product.getStatus() == null ? 0 : 1;
		product.setUrlImage(imgFile);
		product.setStatus(status);
		productService.createProduct(product);
		
		if ("saveAndClose".equals(action)) {
			return "redirect:/backend/catalogs/product";
        }
		return "redirect:/backend/catalogs/product/add";
	}
	
	@GetMapping("/update/{id}")
	public String updateProduct(@PathVariable Long id, Model model) {
		ProductEntity productUpdate = productService.findById(id);
		model.addAttribute("updateProduct", productUpdate);
		List<CategoryResponseDto> flatCategory = categoryService.getFlattenedCategoryTree();
		model.addAttribute("flatCate", flatCategory);
		model.addAttribute("id", id);
		
		List<BrandEntity> brands = brandService.getAllBrand();
		model.addAttribute("brands", brands);
		
		return "/backend/catalogs/product/update";
	}
	
	@PostMapping("/update/{id}")
	public String postUpdateProduct(
			@PathVariable Long id,
			@RequestParam(name="image-file") MultipartFile file,
			@RequestParam(value = "action", required = true) String action,
			@ModelAttribute("updateProduct") ProductEntity product,
			@RequestParam(name="status-product", defaultValue = "0") String statusProduct
	) throws IOException {
		try {
			// Upload image of product
			StringBuilder fileNames = new StringBuilder();
	        Path fileNameAndPath = Paths.get(uploadDirProduct, file.getOriginalFilename());
	        fileNames.append(file.getOriginalFilename());
	        Files.write(fileNameAndPath, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		String imgFile = file.getOriginalFilename().isEmpty() ? product.getUrlImage() : file.getOriginalFilename();
		product.setUrlImage(imgFile);
		product.setStatus("1".equals(statusProduct) ? 1 : 0);
		productService.updateProduct(id, product);
		
		if ("updateAndClose".equals(action)) {
			return "redirect:/backend/catalogs/product";
		}
		return "redirect:/backend/catalogs/product/update/" + id;
	}
	
	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return "redirect:/backend/catalogs/product";
	}
}
