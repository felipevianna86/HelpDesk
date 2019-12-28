package br.com.helpdesk.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.helpdesk.api.entity.ChangeStatus;

/**
 * 
 * @author felipe
 *
 *	Interface que armazena consultas relacionadas a alteração de status de um Ticket.
 */
public interface ChangeStatusRepository extends MongoRepository<ChangeStatus, String> {
	
	/**
	 * Busca uma lista de alterações de status através do Ticket.
	 * 
	 * @param ticketId
	 * @return
	 */
	Iterable<ChangeStatus> findByTicketIdOrderByDateChangeDesc(String ticketId);
}
