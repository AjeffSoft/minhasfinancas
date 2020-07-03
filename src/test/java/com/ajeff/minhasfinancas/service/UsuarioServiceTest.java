package com.ajeff.minhasfinancas.service;

import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ajeff.minhasfinancas.exceptions.ErroValidacao;
import com.ajeff.minhasfinancas.exceptions.RegraNegocioException;
import com.ajeff.minhasfinancas.model.entity.Usuario;
import com.ajeff.minhasfinancas.repository.UsuarioRepository;
import com.ajeff.minhasfinancas.services.impl.UsuarioServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoSalvarEmaiExistente() {
		String email = "usuario@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		service.salvar(usuario);
		
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}
	

	@Test
	public void deveSalvarUsuarioComSucesso() {
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1l).nome("nome").email("email").senha("senha").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		Usuario userSalvo = service.salvar(new Usuario());
		
		Assertions.assertThat(userSalvo).isNotNull();
		Assertions.assertThat(userSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(userSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(userSalvo.getEmail()).isEqualTo("email");
		Assertions.assertThat(userSalvo.getSenha()).isEqualTo("senha");
	}
	
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUsuarioComSucesso() {
		String email = "usuario@email.com";
		String senha = "senha";
		Usuario usuario = Usuario.builder().nome("usuario").email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		Usuario result = service.autenticar(email, senha);
		
		Assertions.assertThat(result).isNotNull();
	}
	
	
	@Test
	public void deveDarErroQuandoNaoEncontraEmail() {
		String email = "usuario@email.com";
		String senha = "senha";
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar(email, senha));
		Assertions.assertThat(exception).isInstanceOf(ErroValidacao.class).hasMessage("O e-mail informado não foi encontrado!");
	}
	
	
	@Test
	public void deveDarErroQuandoSenhaInformadaIncorreta() {
		String email = "usuario@email.com";
		String senha = "senha";
		Usuario usuario = Usuario.builder().nome("usuario").email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar(email, "123"));
		Assertions.assertThat(exception).isInstanceOf(ErroValidacao.class).hasMessage("Senha inválida!");
	}
	
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		service.validarEmail("email@email.com");
	}
	
	
	@Test(expected = RegraNegocioException.class)
	public void deveFalharValidarEmailLancandoException() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		service.validarEmail("email@email.com");
	}

}
