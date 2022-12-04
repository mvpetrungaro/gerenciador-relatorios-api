package br.gov.ibge.gracapi.relatorio.infra;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RelatoriosReader {

	private static final Path DIRECTORY = Path
			.of(RelatoriosReader.class.getClassLoader().getResource("relatorios").getPath());

	public byte[] getRelatorio() {
		
		int files = DIRECTORY.toFile().list().length;
		
		int idxRelatorio = new Random().nextInt(files);
		Path relatorio = null;
		
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(DIRECTORY)) {
			
			Iterator<Path> it = ds.iterator();
			
			for (int i = 0; i <= idxRelatorio; i++) {
				relatorio = it.next();
			}
			
			if (relatorio != null) {
				
				try (InputStream is = Files.newInputStream(relatorio)) {
					
					return is.readAllBytes();
				}
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
}
