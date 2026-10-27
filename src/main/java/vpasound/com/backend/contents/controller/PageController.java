package vpasound.com.backend.contents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/backend/contents")
public class PageController {

	@GetMapping("page")
	public String listPage() {
		return "/backend/contents/page/list";
	}
}
