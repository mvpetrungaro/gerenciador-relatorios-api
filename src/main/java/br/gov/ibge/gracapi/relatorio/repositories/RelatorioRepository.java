package br.gov.ibge.gracapi.relatorio.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import br.gov.ibge.gracapi.relatorio.models.Relatorio;

public interface RelatorioRepository extends JpaRepository<Relatorio, Integer> {

	@Query("SELECT r FROM Relatorio r"
			+ " WHERE r.solicitacao.id = :idSolicitacao"
			+ " AND r.statusExecucao = :statusExecucao")
	public List<Relatorio> findByIdSolicitacaoAndStatusExecucao(Integer idSolicitacao,
			StatusExecucaoEnum statusExecucao);
}
