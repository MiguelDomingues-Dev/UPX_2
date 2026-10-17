package br.com.devnexus.reduza.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Regra única de metas: os mesmos números da view vw_progresso_meta. */
class RegrasMetaTest {

    @Test
    void alvoDescontaAReducaoDaLinhaDeBase() {
        assertEquals(68.0, RegrasMeta.alvo(85, 20), 0.001);
        assertEquals(2220.608, RegrasMeta.alvo(2612.48, 15), 0.001);
    }

    @Test
    void progressoDeExemploDasNotasTecnicas() {
        // base 85 kg, reduzir 20% (alvo 68 kg), média atual 71,5 kg -> 79,4%
        assertEquals(79.4, RegrasMeta.progresso(85, 20, 71.5), 0.05);
    }

    @Test
    void limitesDaEscala() {
        assertEquals(0.0, RegrasMeta.progresso(100, 10, 100), 0.001);
        assertEquals(100.0, RegrasMeta.progresso(100, 10, 90), 0.001);
        assertEquals(150.0, RegrasMeta.progresso(100, 10, 85), 0.001);
        assertEquals(-50.0, RegrasMeta.progresso(100, 10, 105), 0.001);
    }

    @Test
    void situacaoEmTexto() {
        assertEquals("Meta atingida", RegrasMeta.situacao(100.0));
        assertEquals("Perto da meta", RegrasMeta.situacao(80.0));
        assertEquals("Em andamento", RegrasMeta.situacao(10.0));
        assertEquals("Resíduo aumentou", RegrasMeta.situacao(-5.0));
        assertEquals("Sem registros no período", RegrasMeta.situacao(null));
    }
}
