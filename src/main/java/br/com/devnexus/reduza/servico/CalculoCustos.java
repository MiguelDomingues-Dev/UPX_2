package br.com.devnexus.reduza.servico;

/**
 * Contas de custo usadas na tela de descartáveis.
 * O custo unitário nunca é digitado: vem do valor do pacote dividido pelas unidades.
 */
public final class CalculoCustos {

    private CalculoCustos() {
    }

    public static double custoUnitario(double valorPacote, int unidadesPorPacote) {
        return unidadesPorPacote <= 0 ? 0 : valorPacote / unidadesPorPacote;
    }

    /** Custo dividido pelo número de usos: compara descartável (1 uso) com reutilizável (centenas). */
    public static double custoPorUso(double custoUnitario, int usosEstimados) {
        return usosEstimados <= 0 ? custoUnitario : custoUnitario / usosEstimados;
    }

    /** Copos que um grupo usa por mês. */
    public static double coposPorMes(int pessoas, double coposPorDia, int diasUteis) {
        return pessoas * coposPorDia * diasUteis;
    }

    /** Meses para o investimento em reutilizáveis se pagar; infinito se não houver economia. */
    public static double mesesParaRetorno(double investimento, double economiaMensal) {
        return economiaMensal <= 0 ? Double.POSITIVE_INFINITY : investimento / economiaMensal;
    }

    /** kg de plástico de uma quantidade de itens. */
    public static double pesoKg(double unidades, double pesoUnitarioG) {
        return unidades * pesoUnitarioG / 1000.0;
    }
}
