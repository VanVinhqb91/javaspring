package vpasound.com.backend.contents.service;

import java.util.List;

import vpasound.com.core.entity.PageEntity;

public interface PageService {

	public List<PageEntity> listAllPage();
	
	public void createPage(PageEntity pageEntity);
	
	public PageEntity findPageById(Integer id);
	
	public void updatePageById(PageEntity pageEntity, Integer id);
}
