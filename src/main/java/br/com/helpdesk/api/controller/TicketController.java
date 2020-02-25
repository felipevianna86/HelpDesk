package br.com.helpdesk.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import br.com.helpdesk.api.dto.Summary;
import br.com.helpdesk.api.entity.ChangeStatus;
import br.com.helpdesk.api.entity.Ticket;
import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.enums.ProfileEnum;
import br.com.helpdesk.api.enums.StatusEnum;
import br.com.helpdesk.api.response.Response;
import br.com.helpdesk.api.security.jwt.JWTTokenUtil;
import br.com.helpdesk.api.service.TicketService;
import br.com.helpdesk.api.service.UserService;

/**
 * 
 * @author felipe
 *
 * Classe responsável por operações do Ticket via API RESTful
 */
@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	protected JWTTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> createOrUpdate(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result ){
		
		Response<Ticket> response = new Response<>();
		
		try {
			validateCreateTicket(ticket, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			
			ticket.setStatus(StatusEnum.NEW);
			ticket.setUser(userFromRequest(request));
			ticket.setDate(new Date());
			ticket.setNumber(generateNumber());
			
			Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
			response.setData(ticketPersisted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
		
	}
	
	@PutMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result ){
		
		Response<Ticket> response = new Response<>();
		
		try {
			validateUpdateTicket(ticket, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}			
			
			Ticket ticketCurrent = ticketService.findById(ticket.getId());
			ticket.setStatus(ticketCurrent.getStatus());
			ticket.setUser(ticketCurrent.getUser());
			ticket.setDate(ticketCurrent.getDate());
			ticket.setNumber(ticketCurrent.getNumber());
			
			if(ticketCurrent.getAssignedUser() != null)
				ticket.setAssignedUser(ticketCurrent.getAssignedUser());
			
			Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
			response.setData(ticketPersisted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Ticket>> findById(@PathVariable("id") String id){
		
		Response<Ticket> response = new Response<>();
		
		Ticket ticket = ticketService.findById(id);
		
		if(ticket == null) {
			response.getErrors().add("Register not found id: "+id);
			return ResponseEntity.badRequest().body(response);
		}
		
		List<ChangeStatus> changes = new ArrayList<>();
		Iterable<ChangeStatus> changesCurrent = ticketService.listaChangeStatus(id);
		for(Iterator<ChangeStatus> iterator = changesCurrent.iterator(); iterator.hasNext();) {
			
			ChangeStatus change = iterator.next();
			change.setTicket(null);
			changes.add(change);
		}
		
		ticket.setChanges(changes);
		response.setData(ticket);
		
		return ResponseEntity.ok(response);
	}
	

	@GetMapping(value = "{page}/{count}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("count") int count){
		
		Response<Page<Ticket>> response = new Response<>();
		
		Page<Ticket> tickets = null;
		User userRequest = userFromRequest(request);
		if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN))
			tickets = ticketService.listTicket(page, count);
		else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER))
			tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
		
		response.setData(tickets);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
		
		Response<String> response = new Response<>();
		
		Ticket ticket = ticketService.findById(id);
		
		if(ticket == null) {
			response.getErrors().add("Register not found id: "+id);
			return ResponseEntity.badRequest().body(response);
		}
		
		ticketService.delete(ticket.getId());		
		
		return ResponseEntity.ok(new Response<>());
	}
	
	@GetMapping(value = "{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request, @PathVariable("page") int page,
			@PathVariable("count") int count, @PathVariable("count") Integer number, @PathVariable("count") String title, 
			@PathVariable("count") String status, @PathVariable("count") String priority, @PathVariable("count") boolean assigned){
		
		title = title.equals("uninformed") ? "" : title;
		status = status.equals("uninformed") ? "" : status;
		priority = priority.equals("uninformed") ? "" : priority;		
		
		Response<Page<Ticket>> response = new Response<>();
		Page<Ticket> tickets = null;
		
		if(number > 0)
			tickets = ticketService.findByNumber(page, count, number);
		else {
			User userRequest = userFromRequest(request);
			if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
				if(assigned)
					tickets = ticketService.findByParametersAndAssignature(page, count, title, status, priority, userRequest.getId());
				else
					tickets = ticketService.findByParameters(page, count, title, status, priority);
			}
			else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER))			
					tickets = ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId());
	
		}
		
		response.setData(tickets);
		return ResponseEntity.ok(response);
			
	}
	
	/**
	 * Método para alterar status do ticket.
	 * @param id
	 * @param status
	 * @param request
	 * @param ticket
	 * @param result
	 * @return
	 */
	@PutMapping(value = "{id}/{status}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Ticket>> changeStatus(@PathVariable("id") String id, @PathVariable("status") String status, HttpServletRequest request,
			@RequestBody Ticket ticket, BindingResult result){
		
		Response<Ticket> response = new Response<>();
		User userLogged = userFromRequest(request);
		StatusEnum newStatus = StatusEnum.getStatus(status);
		try {
			validateChangeStatus(id, status, result);
			
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}	
			
			Ticket ticketCurrent = ticketService.findById(id);
			ticketCurrent.setStatus(newStatus);
			
			if(status.equals("Assigned"))
				ticketCurrent.setAssignedUser(userLogged);
						
			Ticket ticketPersisted = ticketService.createOrUpdate(ticketCurrent);
			ChangeStatus changeStatus = new ChangeStatus();
			changeStatus.setUserChange(userLogged);
			changeStatus.setDateChange(new Date());
			changeStatus.setStatus(newStatus);
			changeStatus.setTicket(ticketPersisted);
			ticketService.createChangeStatus(changeStatus);
			
			response.setData(ticketPersisted);			
			
		}catch (Exception e) {
			response.getErrors().add(e.getMessage());			
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
		
	}
	
	/**
	 * Método que contabiliza os tickets por status.
	 * @return
	 */
	@GetMapping(value = "/summary")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Summary>> findSummary(){
		
		Response<Summary> response = new Response<>();
		
		Summary summary = new Summary();
		
		Integer amountNew = 0;
		Integer amountResolved = 0;
		Integer amountApproved = 0;
		Integer amountDisapproved = 0;
		Integer amountAssigned = 0;
		Integer amountClosed = 0;
		
		Iterable<Ticket> tickets = ticketService.findAll();
		
		if(tickets != null) {
			for(Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext();) {
				Ticket ticket = iterator.next();
				
				if(ticket.getStatus().equals(StatusEnum.NEW))
					amountNew++;
				else if(ticket.getStatus().equals(StatusEnum.SOLVED))
					amountResolved++;
				else if(ticket.getStatus().equals(StatusEnum.APPROVED))
					amountApproved++;
				else if(ticket.getStatus().equals(StatusEnum.DISAPPROVED))
					amountDisapproved++;
				else if(ticket.getStatus().equals(StatusEnum.ASSIGNED))
					amountAssigned++;
				else if(ticket.getStatus().equals(StatusEnum.CLOSED))
					amountClosed++;
			}
		}
		summary.setAmountNew(amountNew);
		summary.setAmountResolved(amountResolved);
		summary.setAmountApproved(amountApproved);
		summary.setAmountDisapproved(amountDisapproved);
		summary.setAmountAssigned(amountAssigned);
		summary.setAmountClosed(amountClosed);
		
		response.setData(summary);
		
		return ResponseEntity.ok(response);
	}
		
	
	private void validateCreateTicket(Ticket ticket, BindingResult result) {
		if(ticket.getTitle() == null)
			result.addError(new ObjectError("Ticket", "Title no information"));
	}
	
	private void validateUpdateTicket(Ticket ticket, BindingResult result) {
		if(ticket.getId() == null)
			result.addError(new ObjectError("Ticket", "ID no information"));
		if(ticket.getTitle() == null)
			result.addError(new ObjectError("Ticket", "Title no information"));
	}
	
	/**
	 * Método para validar alteração de status de um Ticket.
	 * @param id
	 * @param status
	 * @param result
	 */
	private void validateChangeStatus(String id, String status, BindingResult result) {
		if(id == null || id.trim().equals(""))
			result.addError(new ObjectError("Ticket", "ID no information"));
		if(status == null || status.trim().equals(""))
			result.addError(new ObjectError("Ticket", "Status no information"));
	}
	
	public User userFromRequest(HttpServletRequest request) {
		
		String token = request.getHeader("Authorization");
		String email = jwtTokenUtil.getUsernameFromToken(token);
		
		return userService.findByEmail(email);
		
	}
	
	private Integer generateNumber() {
				
		return new Random().nextInt(9999);
	}
	
}
