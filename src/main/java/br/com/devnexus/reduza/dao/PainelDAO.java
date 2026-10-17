package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.model.ValorRotulado;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Consultas de indicadores do painel. Todas recebem o mês de referência. */
public class PainelDAO {

    private static LocalDate inicio(YearMonth m) {
        return m.atDay(1);
    }

    private static LocalDate fim(YearMonth m) {
        return m.atEndOfMonth();
    }

    /** Meses que têm registros, do mais recente para o mais antigo. */
    public List<YearMonth> mesesComDados() {
        return Banco.lista("SELECT DISTINCT YEAR(data_registro) AS a, MONTH(data_registro) AS m "
                + "FROM registro_residuo ORDER BY a DESC, m DESC", rs -> YearMonth.of(rs.getInt("a"), rs.getInt("m")));
    }

    public double totalKg(YearMonth mes) {
        return Banco.numero("SELECT COALESCE(SUM(peso_kg), 0) FROM registro_residuo "
                + "WHERE data_registro BETWEEN ? AND ?", inicio(mes), fim(mes));
    }

    /** Total até o mesmo dia do mês (para comparar um mês em andamento de forma justa). */
    public double totalKgAteDia(YearMonth mes, int dia) {
        LocalDate limite = mes.atDay(Math.min(dia, mes.lengthOfMonth()));
        return Banco.numero("SELECT COALESCE(SUM(peso_kg), 0) FROM registro_residuo "
                + "WHERE data_registro BETWEEN ? AND ?", inicio(mes), limite);
    }

    public LocalDate ultimoRegistro(YearMonth mes) {
        return Banco.um("SELECT MAX(data_registro) FROM registro_residuo WHERE data_registro BETWEEN ? AND ?",
                rs -> Banco.data(rs, "MAX(data_registro)"), inicio(mes), fim(mes)).orElse(null);
    }

    public double custoDestinacao(YearMonth mes) {
        return Banco.numero("SELECT COALESCE(SUM(r.peso_kg * COALESCE(c.custo_kg, 0)), 0) "
                + "FROM registro_residuo r JOIN categoria c ON c.id = r.categoria_id "
                + "WHERE r.data_registro BETWEEN ? AND ?", inicio(mes), fim(mes));
    }

    /** kg por categoria no mês, na ordem das categorias ativas (inclui as zeradas). */
    public Map<String, Double> porCategoria(YearMonth mes) {
        Map<String, Double> mapa = new LinkedHashMap<>();
        Banco.lista("SELECT c.nome, COALESCE(SUM(r.peso_kg), 0) AS kg FROM categoria c "
                + "LEFT JOIN registro_residuo r ON r.categoria_id = c.id AND r.data_registro BETWEEN ? AND ? "
                + "WHERE c.ativo = TRUE GROUP BY c.id, c.nome ORDER BY c.id",
                rs -> new ValorRotulado(rs.getString("nome"), rs.getDouble("kg")), inicio(mes), fim(mes))
                .forEach(v -> mapa.put(v.rotulo(), v.valor()));
        return mapa;
    }

    public List<ValorRotulado> porSetor(YearMonth mes) {
        return Banco.lista("SELECT s.nome, SUM(r.peso_kg) AS kg FROM registro_residuo r "
                + "JOIN setor s ON s.id = r.setor_id WHERE r.data_registro BETWEEN ? AND ? "
                + "GROUP BY s.id, s.nome ORDER BY kg DESC",
                rs -> new ValorRotulado(rs.getString("nome"), rs.getDouble("kg")), inicio(mes), fim(mes));
    }

    /** Totais dos últimos n meses até o mês informado (meses sem dados aparecem com zero). */
    public List<ValorRotulado> serieMensal(YearMonth ate, int meses) {
        Map<YearMonth, Double> mapa = new LinkedHashMap<>();
        for (int i = meses - 1; i >= 0; i--) {
            mapa.put(ate.minusMonths(i), 0.0);
        }
        YearMonth primeiro = ate.minusMonths(meses - 1L);
        Banco.lista("SELECT YEAR(data_registro) AS a, MONTH(data_registro) AS m, SUM(peso_kg) AS kg "
                + "FROM registro_residuo WHERE data_registro BETWEEN ? AND ? GROUP BY a, m",
                rs -> Map.entry(YearMonth.of(rs.getInt("a"), rs.getInt("m")), rs.getDouble("kg")),
                inicio(primeiro), fim(ate)).forEach(e -> mapa.put(e.getKey(), e.getValue()));
        return mapa.entrySet().stream()
                .map(e -> new ValorRotulado(e.getKey().toString(), e.getValue())).toList();
    }

    /** Percentual de inspeções corretas no mês; -1 se não houve inspeção. */
    public double taxaDescarteCorreto(YearMonth mes) {
        return Banco.um("SELECT COUNT(*) AS n, SUM(resultado = 'CORRETO') AS ok FROM inspecao_descarte "
                + "WHERE data_inspecao BETWEEN ? AND ?",
                rs -> rs.getInt("n") == 0 ? -1.0 : rs.getDouble("ok") * 100.0 / rs.getInt("n"),
                inicio(mes), fim(mes)).orElse(-1.0);
    }
}
