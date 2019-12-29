package br.com.helpdesk.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.helpdesk.api.entity.User;

/**
 * 
 * @author felipe
 *	
 *	Interface que armazena consultas relacionadas ao usuário do sistema.
 */
public interface UserRepository extends MongoRepository<User, String>, PagingAndSortingRepository<User, String>{
	
	/**
	 * Busca um usuário através do e-mail.
	 * @param email
	 * @return
	 */
	User findByEmail(String email);

}
