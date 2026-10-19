package vpasound.com.fontend.home.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import vpasound.com.core.entity.UserEntity;
import vpasound.com.core.repository.SettingRepository;
import vpasound.com.core.repository.UserRepository;
import vpasound.com.fontend.home.service.HomeService;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService{
	
	private final SettingRepository settingRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	
	@Override
	public String getConfigByKey(String key) {
		String value = settingRepository.findById(key)
				.orElseThrow(() -> new RuntimeException("Khong tim thay Setting Key: " + key))
				.getSettingValue();
				
		if (value == null) return null;
		return value;
	}
	
	@Override
	public String login(String email, String password) {

		Optional<UserEntity> user = userRepository.findUserByEmail(email);
		if (user.isPresent()) {
			UserEntity userEntity = user.get();
			boolean check = this.checkLogin(password, userEntity.getPassword());
			if (check == true) return userEntity.getUsername();
		}
		else {
			System.out.println("Khong tim thay");
		}
		return null;
	}
	
	// Check password security is boolean
	public boolean checkLogin(String rawPassword, String hashedPasswordFromDB) {
	  // Dùng hàm matches để kiểm tra mật khẩu nhập vào có đúng với mật khẩu đã băm trong DB không
	  // Tuyệt đối KHÔNG dùng hàm .equals() thông thường
	  return passwordEncoder.matches(rawPassword, hashedPasswordFromDB);
	}
}
