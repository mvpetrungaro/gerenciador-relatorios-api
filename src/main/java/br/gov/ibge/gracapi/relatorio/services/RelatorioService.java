package br.gov.ibge.gracapi.relatorio.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.ibge.gracapi.relatorio.dto.ReexecucaoRelatoriosParamsDTO;
import br.gov.ibge.gracapi.relatorio.dto.RelatorioDTO;
import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import br.gov.ibge.gracapi.relatorio.infra.RelatoriosReader;
import br.gov.ibge.gracapi.relatorio.models.Relatorio;
import br.gov.ibge.gracapi.relatorio.queue.FilaRelatorios;
import br.gov.ibge.gracapi.relatorio.repositories.RelatorioRepository;

@Service
public class RelatorioService {
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private FilaRelatorios filaRelatorios;
	
	@Autowired
	private RelatorioRepository relatorioRepository;
	
	@Autowired
	private RelatoriosReader relatoriosReader;
	
	@Transactional
	public List<Relatorio> atualizarRelatorios(List<Relatorio> relatorios) {
		return relatorioRepository.saveAll(relatorios);
	}
	
	public List<RelatorioDTO> reexecutarRelatorios(ReexecucaoRelatoriosParamsDTO reexecucaoDTO) {
		
		List<Relatorio> relatorios = relatorioRepository.findAllById(reexecucaoDTO.getIdsRelatorios());
		
		relatorios.forEach(r -> r.setStatusExecucao(StatusExecucaoEnum.AGUARDANDO_EXECUCAO));
		
		List<Relatorio> relatoriosAtualizados = atualizarRelatorios(relatorios);
		
		relatoriosAtualizados.stream().sorted().forEach(filaRelatorios::addRelatorio);
		
		return relatoriosAtualizados.stream().map(r -> modelMapper.map(r, RelatorioDTO.class)).collect(Collectors.toList());
	}
	
	public byte[] gerarRelatorio(Integer idRelatorio) {
		
		return relatoriosReader.getRelatorio();
	}
}
