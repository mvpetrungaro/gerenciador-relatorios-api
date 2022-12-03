package br.gov.ibge.gracapi.relatorio.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import br.gov.ibge.gracapi.relatorio.enumerators.FormatoArquivoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "solicitacao_relatorios")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SolicitacaoRelatorios {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;
	
	@Column(name = "id_projeto_edata")
	private Long idProjetoEdata;
	
	@Column(name = "data_solicitacao")
	private Date dataSolicitacao;
	
	@OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL)
	@OrderBy("idTabelaEdata")
	private Set<Relatorio> relatorios;
	
	@OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL)
	@OrderBy("idTerritorioEdata")
	private Set<TerritorioRelatorios> territorios;
	
	@OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL)
	private Set<TipoDadoRelatorios> tiposDado;
	
	@OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL)
	private Set<FormatoDadoRelatorios> formatosDado;
	
	@Column(name = "formato_arquivo")
	@Enumerated(EnumType.STRING)
	private FormatoArquivoEnum formatoArquivo;
	
	@Column
	private String paginacao;
	
	public void addRelatorio(Relatorio relatorio) {
		if (relatorio == null) return;
		
		if (getRelatorios() == null) {
			setRelatorios(new HashSet<>());
		}
		
		getRelatorios().add(relatorio);
		relatorio.setSolicitacao(this);
	}
	
	public void addTerritorio(TerritorioRelatorios territorio) {
		if (territorio == null) return;
		
		if (getTerritorios() == null) {
			setTerritorios(new HashSet<>());
		}
		
		getTerritorios().add(territorio);
		territorio.setSolicitacao(this);
	}
	
	public void addTipoDado(TipoDadoRelatorios tipoDado) {
		if (tipoDado == null) return;
		
		if (getTiposDado() == null) {
			setTiposDado(new HashSet<>());
		}
		
		getTiposDado().add(tipoDado);
		tipoDado.setSolicitacao(this);
	}
	
	public void addFormatoDado(FormatoDadoRelatorios formatoDado) {
		if (formatoDado == null) return;
		
		if (getFormatosDado() == null) {
			setFormatosDado(new HashSet<>());
		}
		
		getFormatosDado().add(formatoDado);
		formatoDado.setSolicitacao(this);
	}
}
