package br.com.devnexus.reduza.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Resultado da view vw_progresso_meta. mediaMensalKg e progresso ficam nulos sem registros no período. */
public record ProgressoMeta(int id, String descricao, String setor, String categoria, LocalDate inicio,
                            LocalDate fim, BigDecimal linhaBaseKg, BigDecimal reducaoPercentual,
                            BigDecimal alvoKg, BigDecimal mediaMensalKg, BigDecimal progressoPercentual) {
}
