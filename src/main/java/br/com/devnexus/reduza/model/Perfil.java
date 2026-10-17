package br.com.devnexus.reduza.model;

/** Perfis de acesso. ADMIN e GESTOR podem cadastrar; OPERADOR só registra. */
public enum Perfil {
    ADMIN("Administrador"),
    GESTOR("Gestor"),
    OPERADOR("Operador");

    private final String rotulo;

    Perfil(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public boolean podeCadastrar() {
        return this != OPERADOR;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
