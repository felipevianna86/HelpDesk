package br.com.helpdesk.api.service;

import org.springframework.data.domain.Page;

import br.com.helpdesk.api.entity.User;

/**
 * 
 * Interface de serviço do usuário.
 * 
 * @author felipe
 *
 */
public interface UserService {
	
	/**
	 * Retorna um usuário através do e-mail passado por parâmetro.
	 * 
	 * @param email
	 * @return
	 */
	User findByEmail(String email);
	
	/**
	 * Cria ou atualizar um usuário.
	 * 
	 * @param user
	 * @return
	 */
	User createOrUpdate(User user);
	
	/**
	 * Busca um usuário através do seu ID.
	 * @param userId
	 * @return
	 */
	User findById(String userId);
	
	/**
	 * Remove um usuário através do seu ID.
	 * @param userId
	 */
	void delete(String userId);
	
	/**
	 * Busca todos os usuários.
	 * @param page
	 * @param count
	 * @return
	 */
	Page<User> findAll(int page, int count);
}
