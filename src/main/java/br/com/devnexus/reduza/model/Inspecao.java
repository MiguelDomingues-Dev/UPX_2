package br.com.devnexus.reduza.model;

import java.time.LocalDate;

/** Inspeção amostral de um coletor: o conteúdo estava separado corretamente? */
public record Inspecao(int id, int pontoId, String ponto, int categoriaId, String categoria, LocalDate data,
                       ResultadoInspecao resultado, String observacao) {
}
