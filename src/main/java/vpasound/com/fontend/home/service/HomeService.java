package vpasound.com.fontend.home.service;

public interface HomeService {
	
	public String getConfigByKey(String key);
	
	public String login(String email, String password);
}
