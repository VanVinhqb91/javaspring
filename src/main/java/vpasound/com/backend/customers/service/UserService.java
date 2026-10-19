package vpasound.com.backend.customers.service;

import java.sql.SQLException;
import java.util.List;

import vpasound.com.core.entity.UserEntity;


public interface UserService {
	public UserEntity findUserByEmail(String email, String password) throws SQLException, Exception;
	
	public List<UserEntity> getAllUser();
	
	public void createUser(UserEntity userEntity);
	
	public void updateUser(UserEntity userEntity, Long id);
	
	public void deleteUserById(Long id);
	
	public UserEntity findUserById(Long id);
}
