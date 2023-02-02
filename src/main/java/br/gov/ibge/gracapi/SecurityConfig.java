package br.gov.ibge.gracapi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
 	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
 		
		http
			.csrf().disable()
			.cors()
		.and()
 			.httpBasic()
   		.and()
 			.authorizeRequests()
 				.anyRequest().authenticated()
		.and()
			.exceptionHandling()
				.authenticationEntryPoint(new AuthenticationEntryPoint() {
					@Override
					public void commence(HttpServletRequest request, HttpServletResponse response,
							AuthenticationException authException) throws IOException, ServletException {

						response.setHeader("WWW-Authenticate", "FormBased");
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
					}
				})
		.and()
			.logout()
				.logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));
 		
 		return http.build();
 	}

 	@Bean
 	public UserDetailsService userDetailsService() {
 		
 		UserDetails user1 = User
     			.withUsername("user1")
     			.password("user1secretpassword")
     			.passwordEncoder(passwordEncoder()::encode)
     			.roles("USER")
     			.build();
 		
 		UserDetails user2 = User
 	 			.withUsername("user2")
 	 			.password("user2secretpassword")
 	 			.passwordEncoder(passwordEncoder()::encode)
 	 			.roles("USER")
 	 			.build();
 		
 		return new InMemoryUserDetailsManager(user1, user2);
 	}

 	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}