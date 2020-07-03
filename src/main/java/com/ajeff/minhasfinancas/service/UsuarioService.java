package com.ajeff.minhasfinancas.service;

import com.ajeff.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar (String email, String senha);
	
	Usuario salvar (Usuario usuario);
	
	void validarEmail(String email);
	
}
