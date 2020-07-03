package com.ajeff.minhasfinancas.exceptions;

public class ErroValidacao extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ErroValidacao (String msg) {
		super(msg);
	}

}
