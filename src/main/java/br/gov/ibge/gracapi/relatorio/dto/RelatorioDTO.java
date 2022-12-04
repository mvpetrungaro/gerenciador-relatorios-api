package br.gov.ibge.gracapi.relatorio.dto;

import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import lombok.Data;

@Data
public class RelatorioDTO {
	private Integer id;
	private Long idTabelaEdata;
	private StatusExecucaoEnum statusExecucao;
	private String dataExecucao;
	private Long duracaoExecucao;
	private String mensagemErro;
}
