package com.ajeff.minhasfinancas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ajeff.minhasfinancas.dto.AtualizaStatusDto;
import com.ajeff.minhasfinancas.dto.LancamentoDTO;
import com.ajeff.minhasfinancas.exceptions.RegraNegocioException;
import com.ajeff.minhasfinancas.model.entity.Lancamento;
import com.ajeff.minhasfinancas.model.entity.Usuario;
import com.ajeff.minhasfinancas.model.enums.StatusLancamento;
import com.ajeff.minhasfinancas.model.enums.TipoLancamento;
import com.ajeff.minhasfinancas.service.LancamentoService;
import com.ajeff.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizaStatus(@PathVariable Long id, @RequestBody AtualizaStatusDto dto) {
		return service.findLancamentoById(id).map(entity ->{
			StatusLancamento lancamentoSalvo = StatusLancamento.valueOf(dto.getStatus());
			if(lancamentoSalvo == null) {
				return ResponseEntity.badRequest().body("Informe um status válido");
			}
			try {
				entity.setStatus(lancamentoSalvo);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> 
			new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}
	
	
	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao, @RequestParam(value= "mes", required = false) Integer mes,
				@RequestParam(value="ano", required= false) Integer ano, @RequestParam(value="usuario") Long idUsuario) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		Optional<Usuario> user = usuarioService.findByUsuarioId(idUsuario);
		
		if(user.isPresent()) {
			lancamentoFiltro.setUsuario(user.get());
		}else {
			return ResponseEntity.badRequest().body("Usuário não encontrado com este id!");
		}
			
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamento = converter(dto);
			lancamento = service.salvar(lancamento);
			return new ResponseEntity(lancamento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	@PutMapping("{id}")
	public ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.findLancamentoById(id).map( entity -> {
			try {
				Lancamento lanc = converter(dto);
				lanc.setId(entity.getId());
				service.atualizar(lanc);
				return ResponseEntity.ok(lanc);
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () -> 
			new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}
	
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable Long id) {
		return service.findLancamentoById(id).map( entity -> {
			service.deletar(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet( () -> 
			new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}
	
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setAno(dto.getAno());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setId(dto.getId());
		lancamento.setMes(dto.getMes());
		
		if(dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}

		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}

		lancamento.setValor(dto.getValor());
		Usuario usuario = usuarioService.findByUsuarioId(dto.getUsuario())
					.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado com este id!"));
		lancamento.setUsuario(usuario);
		return lancamento;
	}

}
