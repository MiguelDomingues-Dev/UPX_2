package br.com.devnexus.reduza.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Uma movimentação de resíduo: quanto, de qual categoria, em qual setor e quando. */
public record RegistroResiduo(int id, int setorId, String setor, int categoriaId, String categoria,
                              int usuarioId, String usuario, LocalDate data, BigDecimal pesoKg,
                              String observacao) {
}
