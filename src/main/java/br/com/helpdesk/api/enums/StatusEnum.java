package br.com.helpdesk.api.enums;

/**
 * 
 * @author felipe
 *
 *	Enum definido para o status de um Ticket.
 */
public enum StatusEnum {
	
	NEW,
	SOLVED,
	ASSIGNED,
	APPROVED,
	DISAPPROVED,
	CLOSED;
	
	public static StatusEnum getStatus(String status) {
		
		switch (status) {
		case "NEW": return NEW;
		case "SOLVED": return SOLVED;
		case "ASSIGNED": return ASSIGNED;
		case "APPROVED": return APPROVED;
		case "DISAPPROVED": return DISAPPROVED;
		case "CLOSED": return CLOSED;			

		default: return NEW;
		}
	}
}
