package vpasound.com.backend.contents.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import vpasound.com.core.entity.MediaFolderEntity;
import vpasound.com.core.repository.MediaFolderRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/backend/contents")
public class MediaGalleryController {
	
	private final MediaFolderRepository mediaFolderRepository;
	
	@GetMapping("/media_gallery")
	public String getMediaGallery(Model model) {
		List<MediaFolderEntity> listFolder = mediaFolderRepository.findAll();
		model.addAttribute("folders", listFolder);
		return "backend/contents/media_gallery/list";
	}
}
