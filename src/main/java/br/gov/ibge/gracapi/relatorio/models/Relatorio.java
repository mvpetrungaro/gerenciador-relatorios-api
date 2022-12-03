package br.gov.ibge.gracapi.relatorio.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Relatorio implements Comparable<Relatorio> {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;
	
	@Column(name = "id_tabela_edata")
	@EqualsAndHashCode.Include
	private Long idTabelaEdata;
	
	@Column(name = "status_execucao")
	@Enumerated(EnumType.STRING)
	private StatusExecucaoEnum statusExecucao;
	
	@Column(name = "data_execucao")
	private Date dataExecucao;
	
	@Column(name = "duracao_execucao")
	private Long duracaoExecucao;
	
	@Column(name = "mensagem_erro")
	private String mensagemErro;
	
	@ManyToOne
	@JoinColumn(name = "id_solicitacao")
	@EqualsAndHashCode.Include
	private SolicitacaoRelatorios solicitacao;

	@Override
	public int compareTo(Relatorio o) {
		return this.getIdTabelaEdata().compareTo(o.getIdTabelaEdata());
	}
}
