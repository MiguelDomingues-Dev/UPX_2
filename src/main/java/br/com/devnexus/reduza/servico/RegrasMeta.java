package br.com.devnexus.reduza.servico;

/**
 * Regra única de metas (a mesma da view vw_progresso_meta):
 *   alvo      = linha de base × (1 − redução% / 100)
 *   progresso = (linha de base − média mensal) ÷ (linha de base − alvo) × 100
 * 0% = nada reduzido; 100% = meta atingida; acima de 100% = superada; negativo = piorou.
 */
public final class RegrasMeta {

    private RegrasMeta() {
    }

    public static double alvo(double linhaBase, double reducaoPercentual) {
        return linhaBase * (1 - reducaoPercentual / 100.0);
    }

    public static double progresso(double linhaBase, double reducaoPercentual, double mediaMensal) {
        double reducaoPlanejada = linhaBase - alvo(linhaBase, reducaoPercentual);
        if (reducaoPlanejada <= 0) {
            return 0;
        }
        return (linhaBase - mediaMensal) / reducaoPlanejada * 100.0;
    }

    /** Texto curto para o cartão da meta. */
    public static String situacao(Double progresso) {
        if (progresso == null) {
            return "Sem registros no período";
        }
        if (progresso >= 100) {
            return "Meta atingida";
        }
        if (progresso >= 70) {
            return "Perto da meta";
        }
        if (progresso >= 0) {
            return "Em andamento";
        }
        return "Resíduo aumentou";
    }
}
