package br.gov.ibge.gracapi.relatorio.dto;

import java.util.List;

import br.gov.ibge.gracapi.relatorio.enumerators.FormatoArquivoEnum;
import br.gov.ibge.gracapi.relatorio.enumerators.FormatoDadoEnum;
import br.gov.ibge.gracapi.relatorio.enumerators.TipoDadoEnum;
import lombok.Data;

@Data
public class SolicitacaoRelatoriosParamsDTO {
	private Long idProjetoEdata;
	private List<Long> idsTabelasEdata;
	private List<TerritorioRelatoriosParamsDTO> territorios;
	private List<TipoDadoEnum> tiposDado;
	private List<FormatoDadoEnum> formatosDado;
	private FormatoArquivoEnum formatoArquivo;
	private String paginacao;
}
