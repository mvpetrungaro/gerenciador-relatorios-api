package br.gov.ibge.gracapi.relatorio.models;

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

import br.gov.ibge.gracapi.relatorio.enumerators.FormatoDadoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "formato_dado_relatorios")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FormatoDadoRelatorios {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;
	
	@Column(name = "formato_dado")
	@Enumerated(EnumType.STRING)
	@EqualsAndHashCode.Include
	private FormatoDadoEnum formatoDado;
	
	@ManyToOne
	@JoinColumn(name = "id_solicitacao")
	private SolicitacaoRelatorios solicitacao;
}
