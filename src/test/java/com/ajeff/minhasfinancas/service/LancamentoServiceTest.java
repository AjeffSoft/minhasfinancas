package com.ajeff.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import com.ajeff.minhasfinancas.exceptions.RegraNegocioException;
import com.ajeff.minhasfinancas.model.entity.Lancamento;
import com.ajeff.minhasfinancas.model.entity.Usuario;
import com.ajeff.minhasfinancas.model.enums.StatusLancamento;
import com.ajeff.minhasfinancas.model.enums.TipoLancamento;
import com.ajeff.minhasfinancas.repository.LancamentoRepository;
import com.ajeff.minhasfinancas.repository.LancamentoRepositoryTest;
import com.ajeff.minhasfinancas.services.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@Profile("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamento);
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamento)).thenReturn(lancamentoSalvo);
		Lancamento lancamentoEfetivado = service.salvar(lancamento);
		Assertions.assertThat(lancamentoEfetivado.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamentoEfetivado.getStatus()).isEqualTo(lancamentoSalvo.getStatus());
	}
	
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		Mockito.doNothing().when(service).validar(lancamento);
		Mockito.when(repository.save(lancamento)).thenReturn(lancamento);
		service.atualizar(lancamento);
		Mockito.verify(repository, Mockito.times(1)).save(lancamento);
	}
	
	@Test
	public void deveLancarUmErroQuandoOLancamentoNaoTiverSidoSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroValidacao() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamento);
		Assertions.catchThrowableOfType( () -> service.salvar(lancamento),RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		service.deletar(lancamento);
		Mockito.verify(repository, Mockito.times(1)).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoDeletarUmLancamentoSemId() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Lancamento> buscado = service.buscar(lancamento);
		Assertions.assertThat(buscado).hasSize(1).isNotEmpty().contains(lancamento);
	}
	
	@Test
	public void deveAtualizarUmStatusLancamento() {
		StatusLancamento novoStatus = StatusLancamento.CANCELADO;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
	
		service.atualizarStatus(lancamento, novoStatus);
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	
	@Test
	public void deveRetornarUmLancamentoPorId() {
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		Optional<Lancamento> resultado = service.findLancamentoById(id);
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	
	@Test
	public void deveRetornarVazioQuandoTentarProcurarPorIdInexistente() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		Mockito.when(repository.findById(1l)).thenReturn(Optional.empty());
		Optional<Lancamento> resultado = service.findLancamentoById(1l);
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErroAoValidarLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um lançamento válido!");
		
		lancamento.setDescricao("");
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um lançamento válido!");

		lancamento.setDescricao("Qualquer descrição");
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");

		lancamento.setMes(0);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");
		
		lancamento.setMes(13);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");

		lancamento.setMes(1);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");
		
		lancamento.setAno(202);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");
		
		lancamento.setAno(20201);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");

		lancamento.setAno(2020);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário existente!");

		lancamento.setUsuario(new Usuario());
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário existente!");

		lancamento.getUsuario().setId(1l);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido!");
		
		lancamento.setValor(BigDecimal.TEN);
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento!");
	}
	
	
	@Test
	public void deveRetornarSaldoCliente() {
		Long idUser = 1l;
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUser, TipoLancamento.RECEITA)).thenReturn(BigDecimal.valueOf(300));
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUser, TipoLancamento.DESPESA)).thenReturn(BigDecimal.valueOf(100));
		BigDecimal resultado = service.obterSaldoPorUsuario(idUser);
		Assertions.assertThat(resultado).isEqualTo(BigDecimal.valueOf(200));
	}

	@Test
	public void deveRetornarSaldoClienteComReceitaZerada() {
		Long idUser = 1l;
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUser, TipoLancamento.RECEITA)).thenReturn(null);
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUser, TipoLancamento.DESPESA)).thenReturn(BigDecimal.valueOf(100));
		BigDecimal resultado = service.obterSaldoPorUsuario(idUser);
		Assertions.assertThat(resultado).isEqualTo(BigDecimal.valueOf(-100));
	}

	@Test
	public void deveRetornarSaldoClienteComDespesaZerada() {
		Long idUser = 1l;
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUser, TipoLancamento.RECEITA)).thenReturn(BigDecimal.valueOf(300));
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(idUser, TipoLancamento.DESPESA)).thenReturn(null);
		BigDecimal resultado = service.obterSaldoPorUsuario(idUser);
		Assertions.assertThat(resultado).isEqualTo(BigDecimal.valueOf(300));
	}
	
	
	
}
