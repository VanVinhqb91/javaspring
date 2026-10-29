package vpasound.com.backend.contents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.AllArgsConstructor;
import vpasound.com.backend.contents.service.PageService;
import vpasound.com.core.entity.PageEntity;

@Controller
@AllArgsConstructor
@RequestMapping("/backend/contents/page")
public class PageController {

	private final PageService pageService;
	
	@GetMapping("")
	public String listPage(Model model) {
		
		model.addAttribute("pages", pageService.listAllPage());
		
		return "backend/contents/page/list";
	}
	
	@GetMapping("/create")
	public String pageCreate(
			Model model
	) {
		model.addAttribute("pageObjects", new PageEntity());
		return "backend/contents/page/create";
	}
	
	@PostMapping("/create")
	public String pageCreatePost(
			@RequestParam(value = "action", required = true) String action,
			@ModelAttribute("pageObjects") PageEntity pageEntity,
			Model model
	) {
		pageService.createPage(pageEntity);
		if ("saveAndClose".equals(action)) {
			return "redirect:/backend/contents/page";
		}
		return "redirect:/backend/contents/page/create";
	}
	
	@GetMapping("/update/{id}")
	public String updatePage(
			@PathVariable Integer id, 
			Model model	
	) {
		model.addAttribute("pageObjects", pageService.findPageById(id));
		return "backend/contents/page/update";
	}
	
	@PostMapping("/update/{id}")
	public String updatePagePost(
			@PathVariable Integer id, 
			@RequestParam(value = "action", required = true) String action,
			@ModelAttribute("pageObjects") PageEntity pageEntity,
			Model model
	) {
		pageService.updatePageById(pageEntity, id);
		if ("saveAndClose".equals(action)) {
			return "redirect:/backend/contents/page";
		}
		return "redirect:/backend/contents/page/update/" + id;
	}
}
