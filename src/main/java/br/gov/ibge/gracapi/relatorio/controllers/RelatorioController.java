package br.gov.ibge.gracapi.relatorio.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.gov.ibge.gracapi.relatorio.dto.ReexecucaoRelatoriosParamsDTO;
import br.gov.ibge.gracapi.relatorio.dto.RelatorioDTO;
import br.gov.ibge.gracapi.relatorio.dto.SolicitacaoRelatoriosDTO;
import br.gov.ibge.gracapi.relatorio.dto.SolicitacaoRelatoriosParamsDTO;
import br.gov.ibge.gracapi.relatorio.services.RelatorioService;
import br.gov.ibge.gracapi.relatorio.services.SolicitacaoRelatoriosService;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {
	
	@Autowired
	private SolicitacaoRelatoriosService solicitacaoRelatoriosService;
	
	@Autowired
	private RelatorioService relatorioService;
	
	@GetMapping("/solicitacao/{idSolicitacao}")
	@ResponseBody
	public SolicitacaoRelatoriosDTO getSolicitacaoRelatorios(@PathVariable Integer idSolicitacao) {
		return solicitacaoRelatoriosService.buscarSolicitacaoRelatorios(idSolicitacao);
	}
	
	@PostMapping("/solicitacao")
	@ResponseBody
	public SolicitacaoRelatoriosDTO solicitarRelatorios(@RequestBody SolicitacaoRelatoriosParamsDTO solicitacaoDTO) {
		return solicitacaoRelatoriosService.solicitarRelatorios(solicitacaoDTO);
	}
	
	@PostMapping("/reexecucao")
	@ResponseBody
	public List<RelatorioDTO> reexecutarRelatorios(@RequestBody ReexecucaoRelatoriosParamsDTO reexecucaoDTO) {
		return relatorioService.reexecutarRelatorios(reexecucaoDTO);
	}
}
