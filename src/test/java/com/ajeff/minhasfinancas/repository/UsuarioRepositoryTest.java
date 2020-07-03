package com.ajeff.minhasfinancas.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ajeff.minhasfinancas.model.entity.Usuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);	
		
		boolean result = repository.existsByEmail("usuario@email.com");
		
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHaUsuarioComOEmailCadastrado () {
		repository.deleteAll();
		
		boolean result = repository.existsByEmail("usuario@email.com");
		
		Assertions.assertThat(result).isFalse();
	}
	
	
	@Test
	public void devePersistirUmUsuarioNaBaseDados() {
		Usuario usuario = criarUsuario();
		Usuario result = repository.save(usuario);
		Assertions.assertThat(result.getId()).isNotNull();
	}
	
	
	@Test
	public void deveProcurarUsuarioCadastradoPeloEmailExistente() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarNuloUsuarioNaoEncontradoPeloEmail() {
		
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").email("usuario@email.com").senha("senha").build();
	}
}
