package br.gov.ibge.gracapi.relatorio.dto;

import java.util.List;

import br.gov.ibge.gracapi.relatorio.enumerators.FormatoArquivoEnum;
import br.gov.ibge.gracapi.relatorio.enumerators.FormatoDadoEnum;
import br.gov.ibge.gracapi.relatorio.enumerators.TipoDadoEnum;
import lombok.Data;

@Data
public class SolicitacaoRelatoriosDTO {
	private Integer id;
	private Long idProjetoEdata;
	private String dataSolicitacao;
	private List<RelatorioDTO> relatorios;
	private List<TerritorioRelatoriosDTO> territorios;
	private List<TipoDadoEnum> tiposDado;
	private List<FormatoDadoEnum> formatosDado;
	private FormatoArquivoEnum formatoArquivo;
	private String paginacao;
}
