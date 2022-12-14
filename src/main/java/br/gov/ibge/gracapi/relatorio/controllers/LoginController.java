package br.gov.ibge.gracapi.relatorio.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	@GetMapping
	@ResponseBody
	public UserDetails getSolicitacoesRelatorios(Authentication authentication) {
		return (UserDetails) authentication.getPrincipal();
	}
}
