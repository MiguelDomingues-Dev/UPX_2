package br.com.devnexus.reduza.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Meta de redução. setorId nulo = empresa toda; categoriaId nulo = todas as categorias. */
public record Meta(int id, Integer setorId, Integer categoriaId, String descricao, LocalDate inicio,
                   LocalDate fim, BigDecimal linhaBaseKg, BigDecimal reducaoPercentual) {
}
