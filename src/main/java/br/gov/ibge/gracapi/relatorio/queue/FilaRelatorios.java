package br.gov.ibge.gracapi.relatorio.queue;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import br.gov.ibge.gracapi.relatorio.dto.RelatorioDTO;
import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import br.gov.ibge.gracapi.relatorio.models.Relatorio;
import br.gov.ibge.gracapi.relatorio.repositories.RelatorioRepository;

@Component
public class FilaRelatorios {
	
	private static final String[] MENSAGENS_ERRO = {
		"Erro de modelagem na Tabela EDATA",
		"Tabela EDATA bloqueada por outro usuário",
		"Erro ao carregar os dados do relatório"
	};
	
	@Value("${grac.relatorios.taxa_sucesso}")
	private int TAXA_SUCESSO;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	private RelatorioRepository relatorioRepository;
	
	private ExecutorService fila = Executors.newSingleThreadExecutor();

	public void addRelatorio(Relatorio relatorio) {
		
		LogManager.getLogger().info("Relatório {} incluído na fila (ID Tabela: {}).", relatorio.getId(), relatorio.getIdTabelaEdata());
		
		Runnable task = () -> {
			try {
				LogManager.getLogger().info("Relatório {} em execução (ID Tabela: {}).", relatorio.getId(), relatorio.getIdTabelaEdata());
				
				relatorio.setStatusExecucao(StatusExecucaoEnum.EM_EXECUCAO);
				relatorio.setDataExecucao(new Date());
				
				relatorioRepository.save(relatorio);
				
				template.convertAndSend("/topic/relatorios", modelMapper.map(relatorio, RelatorioDTO.class));
				
				Thread.sleep(5000);
				
				double resultado = Math.random() * 100;
				
				//Se o resultado estiver dentro da faixa de sucesso, considerar uma execução de sucesso, senão, considerar uma falha.
				if (resultado <= TAXA_SUCESSO) {
					relatorio.setStatusExecucao(StatusExecucaoEnum.SUCESSO);
				} else {
					relatorio.setStatusExecucao(StatusExecucaoEnum.FALHA);
					
					int idxErro = (int) Math.random() * 3;
					relatorio.setMensagemErro(MENSAGENS_ERRO[idxErro]);
				}
			} catch (InterruptedException e) {
				
				relatorio.setStatusExecucao(StatusExecucaoEnum.ABORTADO);
				e.printStackTrace();
			} finally {
				
				Duration duracao = Duration.between(relatorio.getDataExecucao().toInstant(), new Date().toInstant());
				relatorio.setDuracaoExecucao(Math.abs(duracao.toSeconds()));
				
				relatorioRepository.save(relatorio);
				
				template.convertAndSend("/topic/relatorios", modelMapper.map(relatorio, RelatorioDTO.class));
				
				LogManager.getLogger().info("Relatório {} concluído como {} (ID Tabela: {}).", relatorio.getId(), relatorio.getStatusExecucao(), relatorio.getIdTabelaEdata());
			}
		};
		
		fila.execute(task);
	}
}
