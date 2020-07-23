package com.ajeff.minhasfinancas.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ajeff.minhasfinancas.exceptions.ErroValidacao;
import com.ajeff.minhasfinancas.exceptions.RegraNegocioException;
import com.ajeff.minhasfinancas.model.entity.Usuario;
import com.ajeff.minhasfinancas.repository.UsuarioRepository;
import com.ajeff.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	@Autowired
	private UsuarioRepository repository;
	
	
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		if(!usuario.isPresent()) {
			throw new ErroValidacao("O e-mail informado não foi encontrado!");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroValidacao("Senha inválida!");
		}
		return usuario.get();
	}

	
	@Override
	@Transactional
	public Usuario salvar(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário com este e-mail cadastrado!");
		}
		
	}

	@Override
	public Optional<Usuario> findByUsuarioId(Long id) {
		return repository.findById(id);
	}

}
