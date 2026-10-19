package vpasound.com.core.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;

import lombok.RequiredArgsConstructor;

import vpasound.com.core.entity.UserEntity;
import vpasound.com.core.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserBackendDetails implements UserDetailsService{

	private final UserRepository userRepository;

    @Override
    @Nonnull
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	// 1. Tìm kiếm User trong database bằng Email
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + email));

        // 2. Kiểm tra trạng thái tài khoản trực tiếp (Thay cho biến enabled cũ)
        if (userEntity.getStatus() == null || userEntity.getStatus() != 1) {
            throw new UsernameNotFoundException("Tài khoản với email: " + email + " đã bị khóa hoặc chưa kích hoạt!");
        }

        // 3. Trả về TRỰC TIẾP userEntity (Vì UserEntity đã implements UserDetails ở bước trước)
        return userEntity;
    }
}
