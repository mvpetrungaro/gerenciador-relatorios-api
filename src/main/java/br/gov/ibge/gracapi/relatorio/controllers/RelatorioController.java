package br.gov.ibge.gracapi.relatorio.controllers;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import br.gov.ibge.gracapi.relatorio.dto.DownloadRelatorioDTO;
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
	
	@GetMapping("/download/{idRelatorio}")
	public void downloadRelatorio(@PathVariable Integer idRelatorio, HttpServletResponse response) {
		
		try {
    		DownloadRelatorioDTO relatorio = relatorioService.gerarRelatorio(idRelatorio);
    		
    		response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream; charset=utf-8");
    		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + relatorio.getNomeRelatorio() + "; filename*=" + relatorio.getNomeRelatorio());
    		response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(relatorio.getConteudoRelatorio().length));
    		
    		try (OutputStream out = response.getOutputStream()) {
    			out.write(relatorio.getConteudoRelatorio());
    		}
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/solicitacao/download/{idSolicitacao}")
	public void downloadRelatorios(@PathVariable Integer idSolicitacao, HttpServletResponse response) {
		
		try {
    		DownloadRelatorioDTO relatorio = relatorioService.gerarRelatorios(idSolicitacao);
    		
    		response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream; charset=utf-8");
    		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + relatorio.getNomeRelatorio() + "; filename*=" + relatorio.getNomeRelatorio());
    		response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(relatorio.getConteudoRelatorio().length));
    		
    		try (OutputStream out = response.getOutputStream()) {
    			out.write(relatorio.getConteudoRelatorio());
    		}
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/solicitacao/interrupcao/{idSolicitacao}")
	public void interromperSolicitacao(@PathVariable Integer idSolicitacao) {
		solicitacaoRelatoriosService.interromperSolicitacao(idSolicitacao);
	}
}
