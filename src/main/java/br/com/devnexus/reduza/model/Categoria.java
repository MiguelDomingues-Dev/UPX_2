package br.com.devnexus.reduza.model;

import java.math.BigDecimal;

/** Tipo de resíduo, com a cor do coletor (CONAMA 275/2001) e o custo de destinação por kg. */
public record Categoria(int id, String nome, String descricao, String corColetor, boolean reciclavel,
                        boolean perigoso, BigDecimal custoKg, boolean ativo) {

    @Override
    public String toString() {
        return nome;
    }
}
