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

import br.gov.ibge.gracapi.relatorio.enumerators.PosicaoTerritorioEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "territorio_relatorios")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TerritorioRelatorios {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;
	
	@Column(name = "id_territorio_edata")
	@EqualsAndHashCode.Include
	private Long idTerritorioEdata;
	
	@Column
	@Enumerated(EnumType.STRING)
	private PosicaoTerritorioEnum posicao;
	
	@ManyToOne
	@JoinColumn(name = "id_solicitacao")
	private SolicitacaoRelatorios solicitacao;
}
