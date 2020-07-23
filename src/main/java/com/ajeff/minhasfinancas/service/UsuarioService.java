package com.ajeff.minhasfinancas.service;

import java.util.Optional;

import com.ajeff.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar (String email, String senha);
	
	Usuario salvar (Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> findByUsuarioId(Long id);
	
}
