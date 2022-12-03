package br.gov.ibge.gracapi.relatorio.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PosicaoTerritorioEnum {
	CABECALHO("Cabeçalho"),
	LINHA("Linha"),
	CABECALHO_LINHA("Cabeçalho/Linha"),
	AUTO("Auto");
	
	@Getter
	private String descricao;
}
