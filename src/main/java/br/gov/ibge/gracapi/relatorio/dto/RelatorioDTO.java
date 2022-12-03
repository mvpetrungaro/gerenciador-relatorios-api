package br.gov.ibge.gracapi.relatorio.dto;

import lombok.Data;

@Data
public class RelatorioDTO {
	private Integer id;
	private Long idTabelaEdata;
	private String statusExecucao;
	private String dataExecucao;
	private Long duracaoExecucao;
	private String mensagemErro;
}
