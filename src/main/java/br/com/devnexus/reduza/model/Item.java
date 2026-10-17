package br.com.devnexus.reduza.model;

import java.math.BigDecimal;

/** Item contado em unidades (copo, caneca...). usosEstimados = 1 para descartável. */
public record Item(int id, int categoriaId, String nome, TipoItem tipo, BigDecimal pesoUnitarioG,
                  int usosEstimados, String fontePeso) {

    @Override
    public String toString() {
        return nome;
    }
}
