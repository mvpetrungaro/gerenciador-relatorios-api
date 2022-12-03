package br.gov.ibge.gracapi.relatorio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.ibge.gracapi.relatorio.models.SolicitacaoRelatorios;

@Repository
public interface SolicitacaoRelatoriosRepository extends JpaRepository<SolicitacaoRelatorios, Integer> {
}
