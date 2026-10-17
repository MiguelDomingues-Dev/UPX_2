package br.com.devnexus.reduza.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class FormatosTest {

    @Test
    void numerosNoPadraoBrasileiro() {
        assertEquals("2.110", Formatos.kg(2110));
        assertEquals("12,5", Formatos.kg(12.5));
        assertEquals("R$ 1.729,20", Formatos.moeda(1729.2));
        assertEquals("R$ 0,065", Formatos.moedaFina(0.065));
    }

    @Test
    void leituraDeDecimais() {
        assertEquals(12.5, Formatos.lerDecimal("12,5", "x").doubleValue(), 1e-9);
        assertEquals(1250.75, Formatos.lerDecimal("1.250,75", "x").doubleValue(), 1e-9);
        assertEquals(12.5, Formatos.lerDecimal("12.5", "x").doubleValue(), 1e-9);
        assertThrows(ErroValidacao.class, () -> Formatos.lerDecimal("abc", "o peso"));
        assertThrows(ErroValidacao.class, () -> Formatos.lerDecimal("", "o peso"));
    }

    @Test
    void datasValidasEInvalidas() {
        assertEquals(LocalDate.of(2026, 10, 19), Formatos.lerData("19/10/2026", "a data"));
        assertThrows(ErroValidacao.class, () -> Formatos.lerData("31/02/2026", "a data"));
        assertThrows(ErroValidacao.class, () -> Formatos.lerData("2026-10-19", "a data"));
    }

    @Test
    void nomesDeMeses() {
        assertEquals("Outubro de 2026", Formatos.mesAno(YearMonth.of(2026, 10)));
        assertEquals("set.", Formatos.mesCurto(YearMonth.of(2026, 9)));
    }
}
