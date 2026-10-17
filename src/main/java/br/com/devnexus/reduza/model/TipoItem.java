package br.com.devnexus.reduza.model;

public enum TipoItem {
    DESCARTAVEL("Descartável"),
    REUTILIZAVEL("Reutilizável");

    private final String rotulo;

    TipoItem(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
