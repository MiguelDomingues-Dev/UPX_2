package br.com.devnexus.reduza.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CalculoCustosTest {

    @Test
    void custoUnitarioVemDoPacote() {
        assertEquals(0.065, CalculoCustos.custoUnitario(6.50, 100), 1e-9);
        assertEquals(0.0, CalculoCustos.custoUnitario(6.50, 0), 1e-9);
    }

    @Test
    void custoPorUsoComparaDescartavelEReutilizavel() {
        assertEquals(0.03, CalculoCustos.custoPorUso(15.00, 500), 1e-9);
        assertEquals(0.065, CalculoCustos.custoPorUso(0.065, 1), 1e-9);
    }

    @Test
    void simuladorDeTrocaPorCanecas() {
        double copos = CalculoCustos.coposPorMes(25, 4, 22);
        assertEquals(2200, copos, 1e-9);
        double gasto = copos * 0.0655;
        assertEquals(2.6, CalculoCustos.mesesParaRetorno(25 * 15.00, gasto), 0.01);
        assertEquals(3.96, CalculoCustos.pesoKg(copos, 1.8), 1e-9);
        assertTrue(Double.isInfinite(CalculoCustos.mesesParaRetorno(375, 0)));
    }
}
