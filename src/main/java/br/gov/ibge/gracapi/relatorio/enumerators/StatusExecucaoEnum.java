package br.gov.ibge.gracapi.relatorio.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StatusExecucaoEnum {
	AGUARDANDO_EXECUCAO("Aguardando Execução"),
	EM_EXECUCAO("Em Execução"),
	SUCESSO("Sucesso"),
	FALHA("Falha"),
	ABORTADO("Abortado");
	
	@Getter
	private String descricao;
}
