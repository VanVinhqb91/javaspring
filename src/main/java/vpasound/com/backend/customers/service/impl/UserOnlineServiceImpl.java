package vpasound.com.backend.customers.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import vpasound.com.backend.customers.service.UserOnlineService;
import vpasound.com.core.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class UserOnlineServiceImpl implements UserOnlineService{
	
	private final SessionRegistry sessionRegistry;
	
	@Override
	public List<UserEntity> listUserOnline() {
		
		return sessionRegistry.getAllPrincipals().stream()
                // Lọc ra các thực thể đăng nhập có kiểu dữ liệu là UserEntity
                .filter(principal -> principal instanceof UserEntity)
                // Ép kiểu trực tiếp về UserEntity mượt mà nhờ đa hình
                .map(principal -> (UserEntity) principal)
                .collect(Collectors.toList());
	}
}
