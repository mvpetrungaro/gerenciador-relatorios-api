package br.gov.ibge.gracapi.relatorio.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReexecucaoRelatoriosParamsDTO {
	private List<Integer> idsRelatorios;
}
