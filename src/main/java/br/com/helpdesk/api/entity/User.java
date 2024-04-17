package br.com.helpdesk.api.entity;

import br.com.helpdesk.api.enums.ProfileEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author felipe
 *
 *	Classe que representa um usuário do sistema.
 */
@Document
public class User {
	
	@Id
	private String id;
	
	@Indexed(unique = true)
	@NotBlank(message = "E-mail required")
	@Email(message = "E-mail invalid")
	private String email;
	
	@NotBlank(message = "Password required")
	@Size(min = 6)
	private String password;
	
	private ProfileEnum profile;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public ProfileEnum getProfile() {
		return profile;
	}

	public void setProfile(ProfileEnum profile) {
		this.profile = profile;
	}	
	
}
