package br.com.devnexus.reduza.model;

public enum ResultadoInspecao {
    CORRETO("Correto"),
    PARCIAL("Parcial"),
    INCORRETO("Incorreto");

    private final String rotulo;

    ResultadoInspecao(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
