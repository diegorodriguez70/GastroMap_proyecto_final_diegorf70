package com.proyecto.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.proyecto.services.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http,
		                                       CustomUserDetailsService customUserDetailsService) throws Exception {
	
		    http
		        
		        .authenticationProvider(authenticationProvider(customUserDetailsService))
	
		        .authorizeHttpRequests(auth -> auth
		            .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
		            .requestMatchers("/admin/**").hasRole("ADMIN")
		            .anyRequest().authenticated()
		        )
	
		        .formLogin(form -> form
		            .loginPage("/login")
		            .loginProcessingUrl("/login")
		            .defaultSuccessUrl("/index", true)
		            .permitAll()
		        )
	
		        .logout(logout -> logout
		            .logoutUrl("/logout")
		            .logoutSuccessUrl("/login?logout")
		            .permitAll()
		        )
	
		        .csrf(csrf -> csrf.disable());
	
		    return http.build();
		}


	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new PasswordEncoder() {
	            @Override
	            public String encode(CharSequence rawPassword) {
	                return rawPassword.toString();
	            }

	            @Override
	            public boolean matches(CharSequence rawPassword, String encodedPassword) {
	                return rawPassword.toString().equals(encodedPassword);
	            }
	        };
	    }
	    
	    @Bean
	    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	        authProvider.setUserDetailsService(customUserDetailsService);
	        authProvider.setPasswordEncoder(passwordEncoder());
	        return authProvider;
	    }

}
