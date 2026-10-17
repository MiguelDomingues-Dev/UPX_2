package br.com.devnexus.reduza.model;

/** Taxa de descarte correto de um setor no mês (view vw_qualidade_descarte). */
public record QualidadeSetor(String setor, int inspecoes, int corretas, int parciais, int incorretas,
                             double taxaCorreto) {
}
