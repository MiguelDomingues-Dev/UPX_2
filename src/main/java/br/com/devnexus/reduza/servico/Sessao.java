package br.com.devnexus.reduza.servico;

import br.com.devnexus.reduza.model.Usuario;

/** Guarda quem está usando o sistema neste momento. */
public final class Sessao {

    private static Usuario usuario;

    private Sessao() {
    }

    public static void iniciar(Usuario u) {
        usuario = u;
    }

    public static Usuario usuario() {
        if (usuario == null) {
            throw new IllegalStateException("Nenhum usuário conectado");
        }
        return usuario;
    }

    public static boolean ativa() {
        return usuario != null;
    }

    public static void encerrar() {
        usuario = null;
    }
}
