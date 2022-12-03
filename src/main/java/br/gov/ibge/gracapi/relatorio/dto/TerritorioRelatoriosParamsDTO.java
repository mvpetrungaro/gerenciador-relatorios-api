package br.gov.ibge.gracapi.relatorio.dto;

import br.gov.ibge.gracapi.relatorio.enumerators.PosicaoTerritorioEnum;
import lombok.Data;

@Data
public class TerritorioRelatoriosParamsDTO {
	private Long idTerritorioEdata;
	private PosicaoTerritorioEnum posicao;
}
