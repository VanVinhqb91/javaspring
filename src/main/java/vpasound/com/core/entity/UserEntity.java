package vpasound.com.core.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vha_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") 
public class UserEntity implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Long id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "image")
	private String image;
	
	@Column(name = "birthday")
	private String birthday;
	
	@Column(name = "locale")
	private String locale;

	@Column(name = "status")
	private Integer status;

	@Column(name = "user_role")
	private Integer user_role;
	
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Chuyển đổi user_role (Integer) của bạn thành Quyền của Spring Security
        // Ví dụ: user_role = 1 tương ứng với quyền "ROLE_1" giống cấu hình SecurityConfig của bạn
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.user_role));
    }

    @Override
    public String getUsername() {
        return this.email; // Hoặc return this.username tùy thuộc bạn dùng trường nào để đăng nhập
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Tài khoản không bị hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Mật khẩu không bị hết hạn chứng chỉ
    }

    @Override
    public boolean isEnabled() {
        // Nếu status = 1 là hoạt động, bạn có thể viết: return this.status == 1;
        return true; 
    }
}
