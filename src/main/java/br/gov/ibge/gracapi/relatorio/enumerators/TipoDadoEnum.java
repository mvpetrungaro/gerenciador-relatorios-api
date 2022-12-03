package br.gov.ibge.gracapi.relatorio.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TipoDadoEnum {
	SEM_EXPANSAO("Sem Expansão"),
	CV("CV"),
	IC("IC");
	
	@Getter
	private String descricao;
}
