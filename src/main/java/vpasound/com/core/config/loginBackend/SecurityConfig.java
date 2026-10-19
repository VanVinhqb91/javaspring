package vpasound.com.core.config.loginBackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomSuccessHandler successHandler;
	private final CustomFailureHandler failureHandler;
	private final CustomAccessDeniedHandler accessDeniedHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
	}
	
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
	
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable()) // Tắt CSRF nếu làm API thuần
	        .authorizeHttpRequests(auth -> auth
	        		.requestMatchers("/backend/login/ajax", "/", "/backend/login").permitAll()
	        		.requestMatchers("/backend/**").hasAuthority("ROLE_1")
	        		.requestMatchers("/error", "/404.html").permitAll()
	        		.anyRequest().permitAll()
	        		)
	        .formLogin(form -> form
	                .loginPage("/login")
	                .loginProcessingUrl("/backend/login/ajax") 
	                .usernameParameter("email") 
	                .passwordParameter("password")
	                .successHandler(successHandler) 
	                .failureHandler(failureHandler) 
	            )
	        .exceptionHandling(exception -> exception
	                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/backend/login"))
	                .accessDeniedHandler(accessDeniedHandler)
	            )
	        .sessionManagement(session -> session
	                .maximumSessions(-1) // -1 nghĩa là không giới hạn số thiết bị đăng nhập đồng thời của 1 user
	                .sessionRegistry(sessionRegistry()) // Ép Spring Security lưu vết session vào registry
	        )
	        .logout(logout -> logout
	                .logoutUrl("/backend/logout") // Đường dẫn để kích hoạt đăng xuất
	                .logoutSuccessUrl("/backend/login") // Định hướng về trang login kèm parameter thông báo sau khi thoát
	                .invalidateHttpSession(true) // Xóa sạch HTTP Session hiện tại của User
	                .deleteCookies("JSESSIONID") // Xóa Cookie định danh phiên làm việc trên trình duyệt
	                .permitAll()
	            );
	    return http.build();
	}

}
