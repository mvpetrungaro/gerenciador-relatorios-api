package br.gov.ibge.gracapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.gov.ibge.gracapi.relatorio.dto.RelatorioDTO;
import br.gov.ibge.gracapi.relatorio.dto.SolicitacaoRelatoriosDTO;
import br.gov.ibge.gracapi.relatorio.enumerators.FormatoDadoEnum;
import br.gov.ibge.gracapi.relatorio.enumerators.TipoDadoEnum;
import br.gov.ibge.gracapi.relatorio.models.FormatoDadoRelatorios;
import br.gov.ibge.gracapi.relatorio.models.Relatorio;
import br.gov.ibge.gracapi.relatorio.models.SolicitacaoRelatorios;
import br.gov.ibge.gracapi.relatorio.models.TipoDadoRelatorios;

@SpringBootApplication
public class GracApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GracApiApplication.class, args);
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
					.addMapping("/**")
    				.allowedOrigins("http://localhost:3000")
    				.allowCredentials(true);
			}
		};
	}
	
	@Bean
	public ModelMapper modelMapper() {
		
	    ModelMapper modelMapper = new ModelMapper();
	    
	    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    
	    Converter<Date, String> dateConverter = c -> Optional.ofNullable(c.getSource())
	    		.map(d -> df.format(d))
	    		.orElse(null);
	    
	    Converter<Set<TipoDadoRelatorios>, List<TipoDadoEnum>> tipoDadoConverter = c -> Optional.ofNullable(c.getSource())
	    		.map(s -> s.stream()
		    		.map(td -> td.getTipoDado())
		    		.collect(Collectors.toList())
				).orElse(null);
	    
	    Converter<Set<FormatoDadoRelatorios>, List<FormatoDadoEnum>> formatoDadoConverter = c -> Optional.ofNullable(c.getSource())
	    		.map(s -> s.stream()
    				.map(fd -> fd.getFormatoDado())
    				.collect(Collectors.toList())
	    		).orElse(null);
	    
	    TypeMap<SolicitacaoRelatorios, SolicitacaoRelatoriosDTO> solicitacaoRelatoriosMapper = modelMapper.typeMap(SolicitacaoRelatorios.class, SolicitacaoRelatoriosDTO.class);
	    solicitacaoRelatoriosMapper.addMappings(mapper -> mapper.using(dateConverter).map(SolicitacaoRelatorios::getDataSolicitacao, SolicitacaoRelatoriosDTO::setDataSolicitacao));
	    solicitacaoRelatoriosMapper.addMappings(mapper -> mapper.using(tipoDadoConverter).map(SolicitacaoRelatorios::getTiposDado, SolicitacaoRelatoriosDTO::setTiposDado));
	    solicitacaoRelatoriosMapper.addMappings(mapper -> mapper.using(formatoDadoConverter).map(SolicitacaoRelatorios::getFormatosDado, SolicitacaoRelatoriosDTO::setFormatosDado));
	    
	    TypeMap<Relatorio, RelatorioDTO> relatorioMapper = modelMapper.typeMap(Relatorio.class, RelatorioDTO.class);
	    relatorioMapper.addMappings(mapper -> mapper.using(dateConverter).map(Relatorio::getDataExecucao, RelatorioDTO::setDataExecucao));
		
	    return modelMapper;
	}
}
