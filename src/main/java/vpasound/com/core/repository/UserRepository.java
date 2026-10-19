package vpasound.com.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vpasound.com.core.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
	
	@Query(value = "SELECT * FROM vha_user WHERE username = ?1 and password = ?2", nativeQuery = true)
	UserEntity findUserByUserNameAndPasswordQuery(String username, String password);
	
	Optional<UserEntity> findUserByEmail(String email);

	Optional<UserEntity> findByEmail(String email);
}
