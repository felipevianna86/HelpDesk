package br.com.helpdesk.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.helpdesk.api.entity.ChangeStatus;
import br.com.helpdesk.api.entity.Ticket;
import br.com.helpdesk.api.repository.ChangeStatusRepository;
import br.com.helpdesk.api.repository.TicketRepository;
import br.com.helpdesk.api.service.TicketService;
/**
 * 
 * @author felipe
 *
 *Implementação do serviço relacionado ao Ticket.
 */
@Service
public class TicketServiceImpl implements TicketService {
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private ChangeStatusRepository changeStatusRepository;
	
	@Override
	public Ticket createOrUpdate(Ticket ticket) {
		return this.ticketRepository.save(ticket);
	}

	@Override
	public Ticket findById(String id) {
		if(this.ticketRepository.findById(id).isPresent())		
			return this.ticketRepository.findById(id).get();
		
		return null;
	}

	@Override
	public void delete(String id) {
		this.ticketRepository.deleteById(id);
		
	}

	@Override
	public Page<Ticket> listTicket(int page, int count) {
		return this.ticketRepository.findAll(PageRequest.of(page, count));
	}

	@Override
	public ChangeStatus createChangeStatus(ChangeStatus changeStatus) {
		return this.changeStatusRepository.save(changeStatus);
	}

	@Override
	public Iterable<ChangeStatus> listaChangeStatus(String ticketId) {
		// TODO Auto-generated method stub
		return this.changeStatusRepository.findByTicketIdOrderByDateChangeDesc(ticketId);
	}

	@Override
	public Page<Ticket> findByCurrentUser(int page, int count, String userId) {
		// TODO Auto-generated method stub
		return this.ticketRepository.findByUserIdOrderByDateDesc(PageRequest.of(page, count), userId);
	}

	@Override
	public Page<Ticket> findByParameters(int page, int count, String title, String status, String priority) {
		// TODO Auto-generated method stub
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(title, status, priority, PageRequest.of(page, count));
	}

	@Override
	public Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status,
			String priority, String userId) {
		// TODO Auto-generated method stub
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(title, status, priority, userId, PageRequest.of(page, count));
	}

	@Override
	public Page<Ticket> findByNumber(int page, int count, Integer number) {
		// TODO Auto-generated method stub
		return this.ticketRepository.findByNumber(PageRequest.of(page, count), number);
	}

	@Override
	public Iterable<Ticket> findAll() {
		// TODO Auto-generated method stub
		return this.ticketRepository.findAll();
	}

	@Override
	public Page<Ticket> findByParametersAndAssignature(int page, int count, String title, String status,
			String priority, String assignedUser) {
		// TODO Auto-generated method stub
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDesc(title, status, priority, assignedUser, PageRequest.of(page, count));
	}

}
