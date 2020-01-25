package br.com.helpdesk.api.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.security.jwt.JWTAuthenticationRequest;
import br.com.helpdesk.api.security.jwt.JWTTokenUtil;
import br.com.helpdesk.api.security.model.CurrentUser;
import br.com.helpdesk.api.service.UserService;

@RestController
@CrossOrigin(origins = "*")
@ResponseBody
public class AuthenticationRestController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/api/auth")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JWTAuthenticationRequest authenticationRequest) throws AuthenticationException{
		
		String email = authenticationRequest.getEmail();
		String password = authenticationRequest.getPassword();
		
		final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				email, password));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		final String token = jwtTokenUtil.generateToken(userDetails);
		final User user = userService.findByEmail(email);
		
		user.setPassword(null);
		
		return ResponseEntity.ok(new CurrentUser(token, user));
	}
	
	@PostMapping(value = "/api/refresh")
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request){
		
		String token = request.getHeader("Authorization");
		String username = jwtTokenUtil.getUsernameFromToken(token);		
		final User user = userService.findByEmail(username);
		
		if(jwtTokenUtil.canTokenBeRefreshed(token)) {
			String refreshedToken = jwtTokenUtil.refreshToken(token);
			return ResponseEntity.ok(new CurrentUser(refreshedToken, user));
		}
		return ResponseEntity.badRequest().body(null);
	}
	
	
	
	
}
