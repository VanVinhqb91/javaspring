package vpasound.com.backend.systems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import vpasound.com.backend.systems.service.CronjobService;
import vpasound.com.core.entity.SystemCronjobEntity;
import vpasound.com.core.repository.SystemCronjobRepository;

@Controller
@RequestMapping("/backend/systems/cronjob")
@RequiredArgsConstructor
public class CronjobController {

	private final SystemCronjobRepository systemCronjobRepository;
	private final CronjobService cronjobService;
	
	@GetMapping("")
	public String getCronjob(Model model) {
		model.addAttribute("cronjobs", systemCronjobRepository.findAll());
		
		return "backend/systems/cronjob/list";
	}
	
	@GetMapping("/create")
	public String cronJobCreate(
			Model model
	) {
		model.addAttribute("cronjob", new SystemCronjobEntity());
		return "backend/systems/cronjob/create";
	}
	
	@PostMapping("/create")
	public String postCronJobCreate(
			@ModelAttribute(value = "cronjob") SystemCronjobEntity systemCronjobEntity,
			Model model
	) {
		cronjobService.createCronjob(systemCronjobEntity);
		return "redirect:/backend/systems/cronjob";
	}
	
	@GetMapping("/update/{id}")
	public String cronJobUpdate(
			@PathVariable Integer id,
			Model model
	) {
		model.addAttribute("cronjob", systemCronjobRepository.findById(id));
		return "backend/systems/cronjob/update";
	}
	
	@PostMapping("/update/{id}")
	public String postCronJobUpdate(
			@PathVariable Integer id,
			@ModelAttribute(value = "cronjob") SystemCronjobEntity systemCronjobEntity,
			Model model
	) {
		cronjobService.updateCronjob(id, systemCronjobEntity);
		return "redirect:/backend/systems/cronjob";
	}
}
