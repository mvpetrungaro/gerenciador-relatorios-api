package br.gov.ibge.gracapi.relatorio.services;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.ibge.gracapi.relatorio.dto.SolicitacaoRelatoriosDTO;
import br.gov.ibge.gracapi.relatorio.dto.SolicitacaoRelatoriosParamsDTO;
import br.gov.ibge.gracapi.relatorio.dto.TerritorioRelatoriosParamsDTO;
import br.gov.ibge.gracapi.relatorio.enumerators.FormatoDadoEnum;
import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import br.gov.ibge.gracapi.relatorio.enumerators.TipoDadoEnum;
import br.gov.ibge.gracapi.relatorio.models.FormatoDadoRelatorios;
import br.gov.ibge.gracapi.relatorio.models.Relatorio;
import br.gov.ibge.gracapi.relatorio.models.SolicitacaoRelatorios;
import br.gov.ibge.gracapi.relatorio.models.TerritorioRelatorios;
import br.gov.ibge.gracapi.relatorio.models.TipoDadoRelatorios;
import br.gov.ibge.gracapi.relatorio.queue.FilaRelatorios;
import br.gov.ibge.gracapi.relatorio.repositories.SolicitacaoRelatoriosRepository;

@Service
public class SolicitacaoRelatoriosService {
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private FilaRelatorios filaRelatorios;
	
	@Autowired
	private SolicitacaoRelatoriosRepository solicitacaoRepository;
	
	public SolicitacaoRelatoriosDTO buscarSolicitacaoRelatorios(Integer idSolicitacao) {
		
		return solicitacaoRepository.findById(idSolicitacao)
				.map(solicitacao -> modelMapper.map(solicitacao, SolicitacaoRelatoriosDTO.class))
				.orElse(null);
	}

	@Transactional
	public SolicitacaoRelatorios criarSolicitacaoRelatorios(SolicitacaoRelatoriosParamsDTO dto) {
		
		SolicitacaoRelatorios solicitacao = new SolicitacaoRelatorios();
		
		solicitacao.setDataSolicitacao(new Date());
		solicitacao.setIdProjetoEdata(dto.getIdProjetoEdata());
		
		for (Long idTabelaEdata : dto.getIdsTabelasEdata()) {
			
			Relatorio relatorio = new Relatorio();
			
			relatorio.setIdTabelaEdata(idTabelaEdata);
			relatorio.setStatusExecucao(StatusExecucaoEnum.AGUARDANDO_EXECUCAO);
			
			solicitacao.addRelatorio(relatorio);
		}
		
		for (TerritorioRelatoriosParamsDTO territorioDTO : dto.getTerritorios()) {
			
			TerritorioRelatorios territorio = new TerritorioRelatorios();
			
			territorio.setIdTerritorioEdata(territorioDTO.getIdTerritorioEdata());
			territorio.setPosicao(territorioDTO.getPosicao());
			
			solicitacao.addTerritorio(territorio);
		}
		
		if (dto.getTiposDado() != null) {
			for (TipoDadoEnum tipoDadoEnum : dto.getTiposDado()) {
				
				TipoDadoRelatorios tipoDado = new TipoDadoRelatorios();
				
				tipoDado.setTipoDado(tipoDadoEnum);
				
				solicitacao.addTipoDado(tipoDado);
			}
		}
		
		if (dto.getFormatosDado() != null) {
			for (FormatoDadoEnum formatoDadoEnum : dto.getFormatosDado()) {
				
				FormatoDadoRelatorios formatoDado = new FormatoDadoRelatorios();
				
				formatoDado.setFormatoDado(formatoDadoEnum);
				
				solicitacao.addFormatoDado(formatoDado);
			}
		}
		
		solicitacao.setFormatoArquivo(dto.getFormatoArquivo());
		solicitacao.setPaginacao(dto.getPaginacao());
		
		return solicitacaoRepository.save(solicitacao);
	}
	
	public SolicitacaoRelatoriosDTO solicitarRelatorios(SolicitacaoRelatoriosParamsDTO dto) {
		
		SolicitacaoRelatorios solicitacao = criarSolicitacaoRelatorios(dto);
		
		LogManager.getLogger().info("Solicitação {} criada (ID Projeto: {}).", solicitacao.getId(), solicitacao.getIdProjetoEdata());
		
		solicitacao.getRelatorios().stream().sorted().forEach(filaRelatorios::addRelatorio);
		
		return modelMapper.map(solicitacao, SolicitacaoRelatoriosDTO.class);
	}
}
