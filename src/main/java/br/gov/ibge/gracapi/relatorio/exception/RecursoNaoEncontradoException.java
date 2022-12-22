package br.gov.ibge.gracapi.relatorio.exception;

public class RecursoNaoEncontradoException extends Exception {

	private static final long serialVersionUID = -6349450984824031772L;

	public RecursoNaoEncontradoException() {
	}

	public RecursoNaoEncontradoException(String message) {
		super(message);
	}

	public RecursoNaoEncontradoException(Throwable cause) {
		super(cause);
	}

	public RecursoNaoEncontradoException(String message, Throwable cause) {
		super(message, cause);
	}
}
