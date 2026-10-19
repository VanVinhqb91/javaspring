package vpasound.com.backend.catalogs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.catalogs.service.BrandService;
import vpasound.com.core.dto.request.backend.BrandRequestDto;
import vpasound.com.core.entity.BrandEntity;

@Controller
@RequestMapping("/backend/catalogs/brand")
@RequiredArgsConstructor
public class BrandController {
	
	private final BrandService brandService;
	
	@GetMapping("")
	public String showBrand(Model model) {
		model.addAttribute("brands", brandService.getAllBrand());
		
		return "/backend/catalogs/brand/list";
	}
	
	@GetMapping("/create")
	public String getCreateBrand(Model model) {
		model.addAttribute("createBrandForm", new BrandEntity());
		return "/backend/catalogs/brand/create";
	}
	
	@PostMapping("/create")
	public String postCreateBrand(
			@ModelAttribute("createBrandForm") BrandEntity brandEntity,
			@RequestParam(value = "action") String action,
			@RequestParam(name = "image-file") MultipartFile file,
			RedirectAttributes redirectAttributes
	) {
		try {
			brandService.createBrand(brandEntity, file);
			redirectAttributes.addFlashAttribute("successMessage", "Tao Moi Brand Thanh Cong!.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Da co loi khi Tao moi Brand: " + e.getMessage());
		}
		if ("saveAndClose".equals(action)) {
			return "redirect:/backend/catalogs/brand";
		}
		return "redirect:/backend/catalogs/brand/add";
	}
	
	@GetMapping("/update/{id}")
	public String getUpdateBrand(
			@PathVariable Integer id,
			Model model
	) {
		BrandEntity brand = brandService.findBrandById(id);
		model.addAttribute("createBrandForm", brand);
		return "/backend/catalogs/brand/update";
	}
	
	@PostMapping("/update/{id}")
	public String postUpdateBrand(
		@PathVariable Integer id,
		@ModelAttribute("createBrandForm") BrandRequestDto brandDto,
		@RequestParam(value = "action") String action,
		RedirectAttributes redirectAttributes
	) {
		try {
			brandService.updateBrand(id, brandDto);
			redirectAttributes.addFlashAttribute("successMessage", "Update Brand Thanh Cong ID: " + id);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Loi Update Brand " + e.getMessage());
		}
		if ("saveAndClose".equals(action)) {
			return "redirect:/backend/catalogs/brand";
		}
		return "redirect:/backend/catalogs/brand/update/" + id;
	}
	
	@GetMapping("/delete/{id}")
	public String deleteBrand(
		@PathVariable Integer id,
		RedirectAttributes redirectAttributes
	) {
		try {
			brandService.deleteBrand(id);
			redirectAttributes.addFlashAttribute("successMessage", "Deleted Brand Thanh Cong ID: " + id);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Loi Deleted Brand " + e.getMessage());
		}
		
		return "redirect:/backend/catalogs/brand";
	}
}
