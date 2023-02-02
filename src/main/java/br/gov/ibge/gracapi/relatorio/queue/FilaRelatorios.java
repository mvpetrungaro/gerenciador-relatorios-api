package br.gov.ibge.gracapi.relatorio.queue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import br.gov.ibge.gracapi.relatorio.dto.RelatorioDTO;
import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import br.gov.ibge.gracapi.relatorio.models.Relatorio;
import br.gov.ibge.gracapi.relatorio.repositories.RelatorioRepository;
import lombok.AllArgsConstructor;

public class FilaRelatorios {

	private static final String[] MENSAGENS_ERRO = { "Erro de modelagem na Tabela EDATA",
			"Tabela EDATA bloqueada por outro usuário", "Erro ao carregar os dados do relatório" };

	@Value("${grac.relatorios.taxa_sucesso}")
	private int TAXA_SUCESSO;

	private String loginUsuario;
	private ModelMapper modelMapper;
	private SimpMessagingTemplate template;
	private RelatorioRepository relatorioRepository;
	
	private ExecutorService executorService;
	
	public FilaRelatorios(String loginUsuario, ModelMapper modelMapper, SimpMessagingTemplate template,
			RelatorioRepository relatorioRepository) {

		this.loginUsuario = loginUsuario;
		this.modelMapper = modelMapper;
		this.template = template;
		this.relatorioRepository = relatorioRepository;

		iniciar();
	}
	
	private void iniciar() {
		executorService = Executors.newSingleThreadExecutor();
	}
	
	public void executar(Relatorio relatorio) {
		LogManager.getLogger().info("Relatório {} incluído na fila (ID Tabela: {}).", relatorio.getId(),
				relatorio.getIdTabelaEdata());
		
		relatorio.changeStatus(StatusExecucaoEnum.AGUARDANDO_EXECUCAO);
		atualizarAndamento(relatorio);
		
		executorService.execute(new RelatorioTask(relatorio));
	}
	
	public void interromper() {
		if (executorService != null) {
			List<Runnable> tasksAbortadas = executorService.shutdownNow();

			try {
				executorService.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Runnable task : tasksAbortadas) {
				RelatorioTask relatorioTask = (RelatorioTask) task;
				relatorioTask.abort();
			}
		}
		
		iniciar();
	}
	
	private void atualizarAndamento(Relatorio relatorio) {
		relatorioRepository.save(relatorio);
		template.convertAndSendToUser(loginUsuario, "/queue/relatorios", modelMapper.map(relatorio, RelatorioDTO.class));
	}
	
	@AllArgsConstructor
	private class RelatorioTask implements Runnable {
		private Relatorio relatorio;
		
		@Override
		public void run() {
			LogManager.getLogger().info("Relatório {} em execução (ID Tabela: {}).", relatorio.getId(),
					relatorio.getIdTabelaEdata());

			relatorio.changeStatus(StatusExecucaoEnum.EM_EXECUCAO);
			atualizarAndamento(relatorio);

			try {
				// Simulação da interação com o Sistema EDATA.
				// Se o valor aleatório calculado estiver dentro da faixa de sucesso, considerar
				// uma execução de sucesso, senão, considerar uma falha.
				Thread.sleep(5000 + (int) (Math.random() * 5000));
				boolean sucesso = (Math.random() * 100) <= TAXA_SUCESSO;

				// Simulação de um caso de erro.
				if (!sucesso) {
					int idxErro = (int) (Math.random() * MENSAGENS_ERRO.length);
					throw new Exception(MENSAGENS_ERRO[idxErro]);
				}
				
				relatorio.changeStatus(StatusExecucaoEnum.SUCESSO);
			} catch (Exception e) {
				
				relatorio.changeStatus(StatusExecucaoEnum.FALHA);
				relatorio.setMensagemErro(e.getMessage());
			} finally {
				
				atualizarAndamento(relatorio);
			}
			
			LogManager.getLogger().info("Relatório {} concluído como {} (ID Tabela: {}).",
					relatorio.getId(), relatorio.getStatusExecucao(), relatorio.getIdTabelaEdata());
		};
		
		public void abort() {
			relatorio.changeStatus(StatusExecucaoEnum.ABORTADO);
			atualizarAndamento(relatorio);
		}
	}
}
