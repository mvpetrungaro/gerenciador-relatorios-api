package br.gov.ibge.gracapi.relatorio.queue;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
import br.gov.ibge.gracapi.relatorio.repositories.SolicitacaoRelatoriosRepository;

@Component
public class FilaRelatorios {

	private static final String[] MENSAGENS_ERRO = { "Erro de modelagem na Tabela EDATA",
			"Tabela EDATA bloqueada por outro usuário", "Erro ao carregar os dados do relatório" };

	@Value("${grac.relatorios.taxa_sucesso}")
	private int TAXA_SUCESSO;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private SolicitacaoRelatoriosRepository solicitacaoRepository;

	@Autowired
	private RelatorioRepository relatorioRepository;

	private ExecutorService fila = Executors.newSingleThreadExecutor();

	public void interromperFila(Integer idSolicitacao) {

		fila.shutdownNow();

		try {
			fila.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		fila = Executors.newSingleThreadExecutor();

		solicitacaoRepository.findById(idSolicitacao).ifPresent(solicitacao -> {
			solicitacao.getRelatorios().stream()
					.filter(r -> r.getStatusExecucao().equals(StatusExecucaoEnum.AGUARDANDO_EXECUCAO)).forEach(r -> {
						r.setStatusExecucao(StatusExecucaoEnum.ABORTADO);
						r.setDataExecucao(null);
						r.setDuracaoExecucao(null);
						relatorioRepository.save(r);

						template.convertAndSend("/topic/relatorios", modelMapper.map(r, RelatorioDTO.class));
					});
		});
	}

	public void addRelatorio(Relatorio relatorio) {

		LogManager.getLogger().info("Relatório {} incluído na fila (ID Tabela: {}).", relatorio.getId(),
				relatorio.getIdTabelaEdata());

		Runnable task = () -> {
			try {
				LogManager.getLogger().info("Relatório {} em execução (ID Tabela: {}).", relatorio.getId(),
						relatorio.getIdTabelaEdata());

				relatorio.setStatusExecucao(StatusExecucaoEnum.EM_EXECUCAO);
				relatorio.setDataExecucao(new Date());

				relatorioRepository.save(relatorio);

				template.convertAndSend("/topic/relatorios", modelMapper.map(relatorio, RelatorioDTO.class));

				// Simulação da interação com o Sistema EDATA.
				// Se o valor aleatório calculado estiver dentro da faixa de sucesso, considerar
				// uma execução de sucesso, senão, considerar uma falha.
				Thread.sleep(5000 + (int) (Math.random() * 5000));
				boolean sucesso = (Math.random() * 100) <= TAXA_SUCESSO;

				if (sucesso) {
					relatorio.setStatusExecucao(StatusExecucaoEnum.SUCESSO);
				} else {
					relatorio.setStatusExecucao(StatusExecucaoEnum.FALHA);

					// Simulação de um caso de erro.
					int idxErro = (int) (Math.random() * MENSAGENS_ERRO.length);
					relatorio.setMensagemErro(MENSAGENS_ERRO[idxErro]);
				}
				
				Duration duracao = Duration.between(relatorio.getDataExecucao().toInstant(), new Date().toInstant());
				relatorio.setDuracaoExecucao(Math.abs(duracao.toMillis() / 1000));

				LogManager.getLogger().info("Relatório {} concluído como {} (ID Tabela: {}).", relatorio.getId(),
						relatorio.getStatusExecucao(), relatorio.getIdTabelaEdata());
			} catch (InterruptedException e) {

				relatorio.setStatusExecucao(StatusExecucaoEnum.ABORTADO);
				relatorio.setDuracaoExecucao(null);

				LogManager.getLogger().info("Relatório {} abortado (ID Tabela: {}).", relatorio.getId(),
						relatorio.getStatusExecucao(), relatorio.getIdTabelaEdata());
			} finally {

				relatorioRepository.save(relatorio);

				template.convertAndSend("/topic/relatorios", modelMapper.map(relatorio, RelatorioDTO.class));
			}
		};

		fila.execute(task);
	}
}
