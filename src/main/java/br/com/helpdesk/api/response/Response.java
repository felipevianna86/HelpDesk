package br.com.helpdesk.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abastrata de Response.
 * @author felipe
 *
 * @param <T>
 */
public class Response<T> {
	
	private T data;
	
	private List<String> errors;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<String> getErrors() {
		if(errors == null)
			errors = new ArrayList<>();
		
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	
	
}
