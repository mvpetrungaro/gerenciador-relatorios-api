package br.gov.ibge.gracapi.relatorio.dto;

import java.util.List;

import lombok.Data;

@Data
public class SolicitacaoRelatoriosDTO {
	private Integer id;
	private Long idProjetoEdata;
	private String dataSolicitacao;
	private List<RelatorioDTO> relatorios;
	private List<TerritorioRelatoriosDTO> territorios;
	private List<String> tiposDado;
	private List<String> formatosDado;
	private String formatoArquivo;
	private String paginacao;
}
