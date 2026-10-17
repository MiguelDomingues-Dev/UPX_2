package br.com.devnexus.reduza.model;

import java.math.BigDecimal;

/** Resultado da view vw_custo_item: custo médio ponderado de todas as compras do item. */
public record CustoItem(int itemId, String item, TipoItem tipo, int usosEstimados, long unidadesCompradas,
                        BigDecimal valorTotal, BigDecimal custoUnitario, BigDecimal custoPorUso) {
}
