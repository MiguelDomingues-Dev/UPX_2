package br.com.devnexus.reduza.model;

/** Departamento da empresa. */
public record Setor(int id, String nome, String descricao, int qtdColaboradores, boolean ativo) {

    @Override
    public String toString() {
        return nome;
    }
}
