package com.ajeff.minhasfinancas.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class UsuarioDTO {

	private String nome;
	private String senha;
	private String email;
}
