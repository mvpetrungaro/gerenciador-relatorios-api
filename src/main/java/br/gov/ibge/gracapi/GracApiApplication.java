package br.gov.ibge.gracapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import br.gov.ibge.gracapi.relatorio.dto.TerritorioRelatoriosDTO;
import br.gov.ibge.gracapi.relatorio.enumerators.PosicaoTerritorioEnum;
import br.gov.ibge.gracapi.relatorio.enumerators.StatusExecucaoEnum;
import br.gov.ibge.gracapi.relatorio.models.FormatoDadoRelatorios;
import br.gov.ibge.gracapi.relatorio.models.Relatorio;
import br.gov.ibge.gracapi.relatorio.models.SolicitacaoRelatorios;
import br.gov.ibge.gracapi.relatorio.models.TerritorioRelatorios;
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
				registry.addMapping("/**");//.allowedOrigins("http://localhost:3000");
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
	    
	    Converter<Set<TipoDadoRelatorios>, List<String>> tipoDadoConverter = c -> Optional.ofNullable(c.getSource())
	    		.map(s -> s.stream()
		    		.map(td -> td.getTipoDado().getDescricao())
		    		.toList()
				).orElse(null);
	    
	    Converter<Set<FormatoDadoRelatorios>, List<String>> formatoDadoConverter = c -> Optional.ofNullable(c.getSource())
	    		.map(s -> s.stream()
	    				.map(fd -> fd.getFormatoDado().getDescricao())
	    				.toList()
	    		).orElse(null);
	    
	    Converter<PosicaoTerritorioEnum, String> posicaoTerritorioConverter = c -> c.getSource().getDescricao();
	    Converter<StatusExecucaoEnum, String> statusExecucaoConverter = c -> c.getSource().getDescricao();
	    
	    TypeMap<SolicitacaoRelatorios, SolicitacaoRelatoriosDTO> solicitacaoRelatoriosMapper = modelMapper.typeMap(SolicitacaoRelatorios.class, SolicitacaoRelatoriosDTO.class);
	    solicitacaoRelatoriosMapper.addMappings(mapper -> mapper.using(dateConverter).map(SolicitacaoRelatorios::getDataSolicitacao, SolicitacaoRelatoriosDTO::setDataSolicitacao));
	    solicitacaoRelatoriosMapper.addMappings(mapper -> mapper.using(tipoDadoConverter).map(SolicitacaoRelatorios::getTiposDado, SolicitacaoRelatoriosDTO::setTiposDado));
	    solicitacaoRelatoriosMapper.addMappings(mapper -> mapper.using(formatoDadoConverter).map(SolicitacaoRelatorios::getFormatosDado, SolicitacaoRelatoriosDTO::setFormatosDado));
	    
	    TypeMap<TerritorioRelatorios, TerritorioRelatoriosDTO> territorioRelatoriosMapper = modelMapper.typeMap(TerritorioRelatorios.class, TerritorioRelatoriosDTO.class);
	    territorioRelatoriosMapper.addMappings(mapper -> mapper.using(posicaoTerritorioConverter).map(TerritorioRelatorios::getPosicao, TerritorioRelatoriosDTO::setPosicao));
	    
	    TypeMap<Relatorio, RelatorioDTO> relatorioMapper = modelMapper.typeMap(Relatorio.class, RelatorioDTO.class);
	    relatorioMapper.addMappings(mapper -> mapper.using(dateConverter).map(Relatorio::getDataExecucao, RelatorioDTO::setDataExecucao));
		relatorioMapper.addMappings(mapper -> mapper.using(statusExecucaoConverter).map(Relatorio::getStatusExecucao, RelatorioDTO::setStatusExecucao));
		
	    return modelMapper;
	}
}
