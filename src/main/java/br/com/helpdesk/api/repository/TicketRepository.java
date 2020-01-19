package br.com.helpdesk.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.helpdesk.api.entity.Ticket;

/**
 * 
 * @author felipe
 *	
 *	Interface que armazena consultas relacionadas a Ticket.
 */
public interface TicketRepository extends MongoRepository<Ticket, String>{
	
	/**
	 * Retorna Tickets através do usuário passado por parâmetro.
	 * 
	 * @param pages
	 * @param userId
	 * @return
	 */
	Page<Ticket> findByUserIdOrderByDateDesc(Pageable pages, String userId);
	
	/**
	 *  Retorna Tickets através dos título, status e prioridade, passados por parâmetro.
	 * 
	 * @param title
	 * @param status
	 * @param priority
	 * @param pages
	 * @return
	 */
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(String title, String status, String priority, Pageable pages);
	
	/**
	 * Retorna Tickets através dos título, status, prioridade e usuário, passados por parâmetro.
	 * 
	 * @param title
	 * @param status
	 * @param priority
	 * @param userId
	 * @param pages
	 * @return
	 */
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(String title, String status, String priority, String userId, Pageable pages);
	
	/**
	 * Retorna Tickets através dos título, status, prioridade e usuário que o assinou, passados por parâmetro.
	 * 
	 * @param title
	 * @param status
	 * @param priority
	 * @param assignedUserId
	 * @param pages
	 * @return
	 */
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDesc(String title, String status, String priority, String assignedUserId, Pageable pages);
	
	/**
	 * Retorna Tickets através do número passado por parâmetro.
	 * 
	 * @param pages
	 * @param number
	 * @return
	 */
	Page<Ticket> findByNumber(Pageable pages, Integer number);
}
