package br.gov.ibge.gracapi.relatorio.infra;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RelatoriosReader {

//	private static final Path DIRECTORY = Path
//			.of(RelatoriosReader.class.getClassLoader().getResource("relatorios").getPath());

	public byte[] getRelatorio() throws Exception {
		
		Path DIRECTORY = Paths.get(RelatoriosReader.class.getClassLoader().getResource("relatorios").toURI());
		
		int files = DIRECTORY.toFile().list().length;
		
		int idxRelatorio = new Random().nextInt(files);
		Path relatorio = null;
		
		byte[] conteudoRelatorio = null;
		
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(DIRECTORY)) {
			
			Iterator<Path> it = ds.iterator();
			
			for (int i = 0; i <= idxRelatorio; i++) {
				relatorio = it.next();
			}
			
			if (relatorio != null) {
				
				try (InputStream is = Files.newInputStream(relatorio);
						ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
					
					int nRead;
				    byte[] data = new byte[4];

				    while ((nRead = is.read(data, 0, data.length)) != -1) {
				        buffer.write(data, 0, nRead);
				    }

				    buffer.flush();
				    conteudoRelatorio = buffer.toByteArray();
				}
			}
		}
		
		if (conteudoRelatorio == null) {
			throw new Exception("Erro ao mockar relatório");
		}
		
		return conteudoRelatorio;
	}
}
