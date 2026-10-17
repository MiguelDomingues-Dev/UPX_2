package br.com.devnexus.reduza.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Compra registrada como está na nota: pacotes, unidades por pacote e valor do pacote. */
public record CompraLote(int id, int itemId, String item, LocalDate data, int qtdPacotes,
                         int unidadesPorPacote, BigDecimal valorPacote, String fornecedor) {

    public int unidades() {
        return qtdPacotes * unidadesPorPacote;
    }

    public BigDecimal valorTotal() {
        return valorPacote.multiply(BigDecimal.valueOf(qtdPacotes));
    }
}
