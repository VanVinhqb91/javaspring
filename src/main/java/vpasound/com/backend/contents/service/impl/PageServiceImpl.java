package vpasound.com.backend.contents.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vpasound.com.backend.contents.service.PageService;
import vpasound.com.core.entity.PageEntity;
import vpasound.com.core.repository.PageRepository;

@Service
@AllArgsConstructor
public class PageServiceImpl implements PageService{

	private final PageRepository pageRepository;
	
	@Override
	public List<PageEntity> listAllPage() {
		List<PageEntity> listPages = pageRepository.findAll();
		return listPages;
	}
	
	public String formatDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String formattedDate = now.format(formatter);
		return formattedDate;
	}
	
	@Override
	public void createPage(PageEntity pageEntity) {
		
		pageEntity.setCreatedAt(this.formatDateTime());
		pageEntity.setUpdatedAt(this.formatDateTime());
		pageRepository.save(pageEntity);
	}
	
	@Override
	public PageEntity findPageById(Integer id) {
		PageEntity pageEntity = pageRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay Page Id: " + id));
		if (Objects.nonNull(pageEntity))
			return pageEntity;
		return null;
	}
	
	@Override
	public void updatePageById(PageEntity pageEntity, Integer id) {
		PageEntity pEntity = this.findPageById(id);
		PageEntity updatePage = pEntity.toBuilder()
				.pageName(pageEntity.getPageName())
				.slug(pageEntity.getSlug())
				.status(pageEntity.getStatus())
				.title(pageEntity.getTitle())
				.codeHtml(pageEntity.getCodeHtml())
				.updatedAt(this.formatDateTime())
				.build();
		pageRepository.save(updatePage);
	}
}
