package br.gov.ibge.gracapi.relatorio.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FormatoDadoEnum {
	PROPORCAO("Proporção"),
	DISTRIBUICAO("Distribuição");
	
	@Getter
	private String descricao;
}
