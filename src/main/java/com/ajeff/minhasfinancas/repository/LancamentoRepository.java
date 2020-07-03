package com.ajeff.minhasfinancas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ajeff.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
