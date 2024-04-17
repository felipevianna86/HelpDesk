package br.com.helpdesk.api.security.config;

import br.com.helpdesk.api.security.jwt.JWTAuthenticationEntryPoint;
import br.com.helpdesk.api.security.jwt.JWTAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * 
 * @author felipe
 *	
 *
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig{
	
	@Autowired
	private JWTAuthenticationEntryPoint unauthorizedHandler;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		
		authenticationManagerBuilder.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public JWTAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
		return new JWTAuthenticationTokenFilter();
	}

}
