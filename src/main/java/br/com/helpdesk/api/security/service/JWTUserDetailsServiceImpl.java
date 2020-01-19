package br.com.helpdesk.api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.security.jwt.JWTUserFactory;
import br.com.helpdesk.api.service.UserService;

/**
 * 
 * @author felipe
 *
 * Classe para manipular interface do UserDetails.
 */
@Service
public class JWTUserDetailsServiceImpl implements UserDetailsService  {
	
	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		User user = userService.findByEmail(email);
		if(user == null) {
			throw new UsernameNotFoundException(String.format("No user found with e-mail %s", email));
		}
		
		return JWTUserFactory.create(user);
	}

}
