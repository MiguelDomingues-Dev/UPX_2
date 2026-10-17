package br.com.devnexus.reduza.util;

/** Erro de dado digitado pelo usuário. A mensagem é exibida como está, em português. */
public class ErroValidacao extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErroValidacao(String mensagem) {
        super(mensagem);
    }
}
