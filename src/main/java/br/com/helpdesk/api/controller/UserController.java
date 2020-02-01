package br.com.helpdesk.api.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.response.Response;
import br.com.helpdesk.api.service.UserService;

/**
 * 
 * @author felipe
 *
 *	Controller RESTful do usuário.
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passEncoder;
	
	/**
	 * Método responsável por criar um usuário.
	 * @return
	 */
	@PostMapping()
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<User>> create(HttpServletRequest request, @RequestBody User user, BindingResult result){
		
		Response<User> response = new Response<>();
		
		try {
			validateCreateUser(user, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			
			user.setPassword(passEncoder.encode(user.getPassword()));
			User userPersisted = userService.createOrUpdate(user);
			response.setData(userPersisted);
			
		} catch (DuplicateKeyException d) {
			response.getErrors().add("E-mail already registered!");
			return ResponseEntity.badRequest().body(response);
		}
		catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
		
	}
	
	/**
	 * Método responsável por validar os dados ao criar um usuário.
	 * @param user
	 * @param result
	 */
	private void validateCreateUser(User user, BindingResult result) {
		
		if(user.getEmail() == null)
			result.addError(new ObjectError("User", "E-mail no information."));
	}
	
	/**
	 * Método responsável por atualizar um usuário.
	 * @param request
	 * @param user
	 * @param result
	 * @return
	 */
	@PutMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user, BindingResult result){
		Response<User> response = new Response<>();
		
		try {
			validateUpdateUser(user, result);
			
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			
			user.setPassword(passEncoder.encode(user.getPassword()));
			User userPersisted = userService.createOrUpdate(user);
			response.setData(userPersisted);
			
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Método responsável por validar dados ao atualizar um usuário.
	 * @param user
	 * @param result
	 */
	private void validateUpdateUser(User user, BindingResult result) {
		
		if(user.getId() == null)
			result.addError(new ObjectError("User", "Id no information."));
		
		if(user.getEmail() == null)
			result.addError(new ObjectError("User", "E-mail no information."));
	}
	
	/**
	 * Método responsável por consultar um usuário através do ID.
	 * @param id
	 * @return
	 */
	@GetMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<User>> findById(@PathVariable("id") String id ){
		
		Response<User> response = new Response<>();
		
		Optional<User> user = userService.findById(id);
		if(user.orElse(null) == null) {
			response.getErrors().add("Register not found id: "+id);
			return ResponseEntity.badRequest().body(response);
		}
		
		response.setData(user.get());
		
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Método responsável por deletar um usuário através do ID.
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") String id ){
		
		Response<String> response = new Response<>();
		
		Optional<User> user = userService.findById(id);
		if(user.orElse(null) == null) {
			response.getErrors().add("Register not found id: "+id);
			return ResponseEntity.badRequest().body(response);
		}
		
		userService.delete(id);
		return ResponseEntity.ok(new Response<String>());
	}
	
	/**
	 * Método responsável por consultar todos os usuários, utilizando paginação..
	 * @param page
	 * @param count
	 * @return
	 */
	@GetMapping(value = "{page}/{count}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count ){
		
		Response<Page<User>> response = new Response<>();
		Page<User> users = userService.findAll(page, count);
		response.setData(users);
		
		return ResponseEntity.ok(response);
	}
	
}
