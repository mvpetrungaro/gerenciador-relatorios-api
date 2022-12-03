package br.gov.ibge.gracapi.relatorio.controllers;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import br.gov.ibge.gracapi.relatorio.dto.RelatorioDTO;

@Controller
public class WebSocketController {
	
	@SendTo("/topic/relatorios")
	public RelatorioDTO sendRelatorio(@Payload RelatorioDTO relatorio) {
		return relatorio;
	}
}
