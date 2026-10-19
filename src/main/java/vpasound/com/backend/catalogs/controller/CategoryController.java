package vpasound.com.backend.catalogs.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.catalogs.service.CategoryService;
import vpasound.com.core.dto.request.backend.CategoryRequestDto;
import vpasound.com.core.dto.response.backend.CategoryResponseDto;
import vpasound.com.core.entity.CategoryEntity;

@Controller
@RequestMapping("/backend/catalogs/category")
@RequiredArgsConstructor
public class CategoryController {
	
	public final CategoryService categoryService;
	
	@GetMapping("")
	public String getCategory(Model model) {
		List<CategoryResponseDto> flatCategory = categoryService.getFlattenedCategoryTree();
		model.addAttribute("categories", categoryService.getCategoryTree());
		model.addAttribute("flatCate", flatCategory);
		model.addAttribute("categoryForm", new CategoryEntity());
		
		return "/backend/catalogs/category/list";
	}
	
	@PostMapping("/create")
	public String createCategory(
			@Valid @ModelAttribute("categoryForm") CategoryRequestDto categoryDTO,
			BindingResult bindingResult,
			Model model
	) {
		if (bindingResult.hasErrors()) {
			List<CategoryResponseDto> flatCategory = categoryService.getFlattenedCategoryTree();
			model.addAttribute("categories", categoryService.getCategoryTree());
			model.addAttribute("flatCate", flatCategory);
			return "/backend/catalogs/category";
		}
		int status = categoryDTO.getStatus() == null ? 0 : 1;
		categoryDTO.setStatus(status);
		categoryService.saveCategory(categoryDTO);
		return "redirect:/backend/catalogs/category/list";
	}
	
	@GetMapping("/update/{id}")
	public String updateCategory(@PathVariable int id, Model model) {
		List<CategoryResponseDto> flatCategory = categoryService.getFlattenedCategoryTree();
		model.addAttribute("categories", categoryService.getCategoryTree());
		model.addAttribute("flatCate", flatCategory);
		model.addAttribute("cateUpdateForm", new CategoryEntity());
		
		CategoryEntity cateUpdate = categoryService.findById(id);
		model.addAttribute("cateUpdateForm", cateUpdate);
		
		return "/backend/catalogs/category/update";
	}
	
	@PostMapping("/update/{id}")
	public String postUpdateCategory(
			@PathVariable int id,
			@Valid @ModelAttribute("cateUpdateForm") CategoryRequestDto categoryDto,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes
	) {
		if (bindingResult.hasErrors()) {
			List<CategoryResponseDto> flatCategory = categoryService.getFlattenedCategoryTree();
			model.addAttribute("categories", categoryService.getCategoryTree());
			model.addAttribute("flatCate", flatCategory);
			model.addAttribute("id", id);
			return "backend/catalogs/category/update";
		}
		int status = categoryDto.getStatus() == null ? 0 : 1;
		categoryDto.setStatus(status);
		try {
			categoryService.updateCategory(id, categoryDto);
			redirectAttributes.addFlashAttribute("successMessage", "Updated Danh Mục Co ID: " + id + " Thành Công!.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi Khi Update Danh Mục: ID - " + id + e.getMessage());
		}
		return "redirect:/backend/catalogs/category";
	}
	
	@GetMapping("/delete/{id}")
	public String deleteCategory(
			@PathVariable int id,
			RedirectAttributes redirectAttributes
	) {
		try {
			categoryService.deleteCategory(id);
			redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa danh mục: " + e.getMessage());
		}
		return "redirect:/backend/catalogs/category";
	}
}
