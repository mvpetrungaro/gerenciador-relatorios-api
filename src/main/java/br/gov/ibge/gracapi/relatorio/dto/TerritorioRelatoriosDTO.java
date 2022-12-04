package br.gov.ibge.gracapi.relatorio.dto;

import br.gov.ibge.gracapi.relatorio.enumerators.PosicaoTerritorioEnum;
import lombok.Data;

@Data
public class TerritorioRelatoriosDTO {
	private Long idTerritorioEdata;
	private PosicaoTerritorioEnum posicao;
}
