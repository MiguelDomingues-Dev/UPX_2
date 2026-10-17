package br.com.devnexus.reduza.model;

/** Estação de coletores de um setor. */
public record PontoColeta(int id, int setorId, String setor, String nome, String localizacao, boolean ativo) {

    @Override
    public String toString() {
        return setor + " · " + nome;
    }
}
