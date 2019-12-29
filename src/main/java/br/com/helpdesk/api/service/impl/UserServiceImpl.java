package br.com.helpdesk.api.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.repository.UserRepository;
import br.com.helpdesk.api.service.UserService;

/**
 * 
 * @author felipe
 *	
 * Implementação do serviço relacionado ao usuário.
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User findByEmail(String email) {
		
		return this.userRepository.findByEmail(email);
	}

	@Override
	public User createOrUpdate(User user) {
		
		return this.userRepository.save(user);
	}

	@Override
	public Optional<User> findById(String userId) {
		
		return this.userRepository.findById(userId);
	}

	@Override
	public void delete(String userId) {
		
		this.userRepository.deleteById(userId);
		
	}

	@Override
	public Page<User> findAll(int page, int count) {
		
		return this.userRepository.findAll(PageRequest.of(page, count));
	}
	
	
}
