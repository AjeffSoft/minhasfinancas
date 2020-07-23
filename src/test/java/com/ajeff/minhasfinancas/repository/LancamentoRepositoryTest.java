package com.ajeff.minhasfinancas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.ajeff.minhasfinancas.model.entity.Lancamento;
import com.ajeff.minhasfinancas.model.enums.StatusLancamento;
import com.ajeff.minhasfinancas.model.enums.TipoLancamento;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager manager;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = repository.save(lancamento);
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}
	
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarPersistirLancamento();
		lancamento.setAno(2019);
		lancamento.setStatus(StatusLancamento.CANCELADO);
		repository.save(lancamento);
		Lancamento lancamentoAtualizado = manager.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2019);
		Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}
	
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarPersistirLancamento();
		Optional<Lancamento> lancamentoBuscado = repository.findById(lancamento.getId());
		Assertions.assertThat(lancamentoBuscado.isPresent()).isTrue();
	}
	
	
	@Test
	public void deveExcluirUmLancamento() {
		Lancamento lancamento = criarPersistirLancamento();
		lancamento = manager.find(Lancamento.class, lancamento.getId());
		repository.delete(lancamento);
		Lancamento lancamentoExcluido = manager.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(lancamentoExcluido).isNull();
	}


	private Lancamento criarPersistirLancamento() {
		Lancamento lancamento = criarLancamento();
		manager.persist(lancamento);
		return lancamento;
	}
	
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder().ano(2020).dataCadastro(LocalDate.now())
				.descricao("Qualquer").mes(7).status(StatusLancamento.PENDENTE)
				.tipo(TipoLancamento.RECEITA).valor(BigDecimal.valueOf(10)).build();
	}
}
