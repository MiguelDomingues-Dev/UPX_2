package br.com.devnexus.reduza.model;

/** Par genérico rótulo/valor, usado pelos gráficos e indicadores. */
public record ValorRotulado(String rotulo, double valor) {

    /** Remove os meses iniciais sem dados (antes de o sistema começar a ser usado), mantendo ao menos 2. */
    public static java.util.List<ValorRotulado> semZerosIniciais(java.util.List<ValorRotulado> serie) {
        int i = 0;
        while (i < serie.size() - 2 && serie.get(i).valor() == 0) {
            i++;
        }
        return serie.subList(i, serie.size());
    }
}
