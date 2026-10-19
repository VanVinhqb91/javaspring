package vpasound.com.backend.customers.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.customers.service.UserService;
import vpasound.com.core.entity.UserEntity;
import vpasound.com.core.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserEntity findUserByEmail(String email, String password) throws SQLException, Exception{
		Optional<UserEntity> user = userRepository.findUserByEmail(email);
		
		if (user.isPresent()) {
			String passwordUser = user.get().getPassword();
			if (passwordEncoder.matches(password, passwordUser)) {
				return user.get();
			}
		}
		return null;
	}
	
	@Override
	public List<UserEntity> getAllUser() {
		if (Objects.isNull(userRepository.findAll())) return null;
		else return userRepository.findAll();
	}
	
	@Override
	public void createUser(UserEntity userEntity) {
		userEntity.setPassword(this.resultPasswordEncoder(userEntity.getPassword()));
		userRepository.save(userEntity);
	}
	
	@Override
	public void updateUser(UserEntity userEntity, Long id) {
		UserEntity update = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay User ID: " + id));
		if (Objects.isNull(update)) {
			new RuntimeException("Không tìm thấy User với ID: " + id);
		}
		update.setUsername(userEntity.getUsername());
		update.setBirthday(userEntity.getBirthday());
		update.setImage("/image.png");
		update.setLocale(userEntity.getLocale());
		update.setStatus(userEntity.getStatus());
		update.setUser_role(userEntity.getUser_role());
		if (userEntity.getPassword() != "")
			update.setPassword(this.resultPasswordEncoder(userEntity.getPassword()));
		
		userRepository.save(update);
	}
	
	@Override
	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}
	
	@Override
	public UserEntity findUserById(Long id) {
		UserEntity userEntity = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Khong tim thay User ID: " + id));
		return userEntity;
	}
	
	private String resultPasswordEncoder(String password) {
		String newPassword = passwordEncoder.encode(password);
		return newPassword;
	}
	
	 // 2. Khi User ĐĂNG NHẬP
//    public boolean checkLogin(String rawPassword, String hashedPasswordFromDB) {
//        // Dùng hàm matches để kiểm tra mật khẩu nhập vào có đúng với mật khẩu đã băm trong DB không
//        // Tuyệt đối KHÔNG dùng hàm .equals() thông thường
//        return passwordEncoder.matches(rawPassword, hashedPasswordFromDB);
//    }
}
