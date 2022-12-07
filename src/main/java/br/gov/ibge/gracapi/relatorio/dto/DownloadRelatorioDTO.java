package br.gov.ibge.gracapi.relatorio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadRelatorioDTO {

	private String nomeRelatorio;
	private byte[] conteudoRelatorio;
}
