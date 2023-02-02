package br.gov.ibge.gracapi.relatorio.services;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.ibge.gracapi.relatorio.dto.DownloadRelatorioDTO;
import br.gov.ibge.gracapi.relatorio.dto.ReexecucaoRelatoriosParamsDTO;
import br.gov.ibge.gracapi.relatorio.dto.RelatorioDTO;
import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import br.gov.ibge.gracapi.relatorio.exception.RecursoNaoEncontradoException;
import br.gov.ibge.gracapi.relatorio.infra.RelatoriosReader;
import br.gov.ibge.gracapi.relatorio.models.Relatorio;
import br.gov.ibge.gracapi.relatorio.queue.FilaRelatoriosProvider;
import br.gov.ibge.gracapi.relatorio.repositories.RelatorioRepository;

@Service
public class RelatorioService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private FilaRelatoriosProvider filaRelatoriosProvider;

	@Autowired
	private RelatorioRepository relatorioRepository;

	@Autowired
	private RelatoriosReader relatoriosReader;

	@Transactional
	public List<Relatorio> atualizarRelatorios(List<Relatorio> relatorios) {
		return relatorioRepository.saveAll(relatorios);
	}

	public List<RelatorioDTO> reexecutarRelatorios(ReexecucaoRelatoriosParamsDTO reexecucaoDTO, String loginUsuario) {

		List<Relatorio> relatorios = relatorioRepository.findAllById(reexecucaoDTO.getIdsRelatorios());

		relatorios.forEach(r -> {
			r.setStatusExecucao(StatusExecucaoEnum.AGUARDANDO_EXECUCAO);
			r.setDataExecucao(null);
			r.setDuracaoExecucao(null);
		});

		List<Relatorio> relatoriosAtualizados = atualizarRelatorios(relatorios);

		relatoriosAtualizados.stream().sorted().forEach(r -> filaRelatoriosProvider.getFila(loginUsuario).executar(r));

		return relatoriosAtualizados.stream().map(r -> modelMapper.map(r, RelatorioDTO.class))
				.collect(Collectors.toList());
	}

	public DownloadRelatorioDTO gerarRelatorio(Integer idRelatorio) throws Exception {

		Optional<Relatorio> optRelatorio = relatorioRepository.findById(idRelatorio);

		if (!optRelatorio.isPresent()) {
			throw new RecursoNaoEncontradoException("Relatório não encontrado");
		}

		Relatorio relatorio = optRelatorio.get();

		// "." + rel.getSolicitacao().getFormatoArquivo().name().toLowerCase();
		String nomeRelatorio = String.valueOf(relatorio.getIdTabelaEdata())
				+ ".html";
		byte[] conteudoRelatorio = relatoriosReader.getRelatorio(nomeRelatorio);

		return new DownloadRelatorioDTO(nomeRelatorio, conteudoRelatorio);
	}

	public DownloadRelatorioDTO gerarRelatorios(Integer idSolicitacao) throws Exception {

		DownloadRelatorioDTO zipDTO;

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			try (ZipOutputStream zos = new ZipOutputStream(out)) {

				List<Relatorio> relatorios = relatorioRepository.findByIdSolicitacaoAndStatusExecucao(idSolicitacao,
						StatusExecucaoEnum.SUCESSO);
				
				if (relatorios.isEmpty()) {
					throw new RecursoNaoEncontradoException("Nenhum relatório executado com sucesso encontrado");
				}

				for (Relatorio relatorio : relatorios) {

					// "." + rel.getSolicitacao().getFormatoArquivo().name().toLowerCase();
					String nomeRelatorio = String.valueOf(relatorio.getIdTabelaEdata())
							+ ".html";
					byte[] conteudoRelatorio = relatoriosReader.getRelatorio(nomeRelatorio);

					DownloadRelatorioDTO dto = new DownloadRelatorioDTO(nomeRelatorio, conteudoRelatorio);

					ZipEntry entry = new ZipEntry(dto.getNomeRelatorio());

					zos.putNextEntry(entry);
					zos.write(dto.getConteudoRelatorio());
					zos.closeEntry();
				}
			}

			String nomeRelatorio = "relatorios.zip";
			byte[] conteudoRelatorio = out.toByteArray();

			zipDTO = new DownloadRelatorioDTO(nomeRelatorio, conteudoRelatorio);
		}

		return zipDTO;
	}
}
