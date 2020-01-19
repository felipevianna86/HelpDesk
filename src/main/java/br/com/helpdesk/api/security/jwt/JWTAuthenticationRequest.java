package br.com.helpdesk.api.security.jwt;

import java.io.Serializable;

/**
 * 
 * @author felipe
 *
 */
public class JWTAuthenticationRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1315192093120541415L;
	
	private String email;
	
	private String password;
	
	public JWTAuthenticationRequest() {
		
	}
	
	public JWTAuthenticationRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
	
}
