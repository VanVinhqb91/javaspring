package vpasound.com.backend.marketings.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/backend/marketings")
@AllArgsConstructor
public class SiteMapController {

	@GetMapping("/sitemap")
	public String listSiteMap() {
		return "/backend/marketings/sitemap/list";
	}
}
