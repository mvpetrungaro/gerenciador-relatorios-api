package br.gov.ibge.gracapi.relatorio.queue;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import br.gov.ibge.gracapi.relatorio.repositories.RelatorioRepository;

@Component
public class FilaRelatoriosProvider {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private RelatorioRepository relatorioRepository;

	private Map<String, FilaRelatorios> filas = new HashMap<>();

	public FilaRelatorios getFila(String loginUsuario) {
		if (!filas.containsKey(loginUsuario)) {
			filas.put(loginUsuario, new FilaRelatorios(loginUsuario, modelMapper, template, relatorioRepository));
		}

		return filas.get(loginUsuario);
	}
}
