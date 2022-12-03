package br.gov.ibge.gracapi.relatorio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.ibge.gracapi.relatorio.models.Relatorio;

public interface RelatorioRepository extends JpaRepository<Relatorio, Integer> {
}
