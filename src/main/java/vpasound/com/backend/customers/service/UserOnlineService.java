package vpasound.com.backend.customers.service;

import java.util.List;

import vpasound.com.core.entity.UserEntity;

public interface UserOnlineService {
	
	public List<UserEntity> listUserOnline();
}
