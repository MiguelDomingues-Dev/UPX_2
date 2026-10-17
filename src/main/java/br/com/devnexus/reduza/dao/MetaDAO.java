package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.model.Meta;
import br.com.devnexus.reduza.model.ProgressoMeta;
import java.time.LocalDate;
import java.util.List;

public class MetaDAO {

    public List<ProgressoMeta> progresso() {
        return Banco.lista("SELECT * FROM vw_progresso_meta ORDER BY periodo_fim, id",
                rs -> new ProgressoMeta(rs.getInt("id"), rs.getString("descricao"), rs.getString("setor"),
                        rs.getString("categoria"), Banco.data(rs, "periodo_inicio"), Banco.data(rs, "periodo_fim"),
                        rs.getBigDecimal("linha_base_kg"), rs.getBigDecimal("reducao_percentual"),
                        rs.getBigDecimal("alvo_kg"), rs.getBigDecimal("media_mensal_kg"),
                        rs.getBigDecimal("progresso_percentual")));
    }

    public int inserir(Meta m) {
        return Banco.inserir("INSERT INTO meta (setor_id, categoria_id, descricao, periodo_inicio, periodo_fim, "
                + "linha_base_kg, reducao_percentual) VALUES (?, ?, ?, ?, ?, ?, ?)", m.setorId(), m.categoriaId(),
                m.descricao().trim(), m.inicio(), m.fim(), m.linhaBaseKg(), m.reducaoPercentual());
    }

    public void desativar(int id) {
        Banco.executar("UPDATE meta SET ativa = FALSE WHERE id = ?", id);
    }

    /**
     * Média mensal de kg nos últimos meses FECHADOS antes do início da meta:
     * serve para sugerir a linha de base. O mês em andamento nunca entra.
     */
    public double mediaMensalAntes(Integer setorId, Integer categoriaId, LocalDate inicio, int meses) {
        LocalDate fimMesFechado = LocalDate.now().withDayOfMonth(1).minusDays(1);
        LocalDate ate = inicio.withDayOfMonth(1).minusDays(1);
        if (ate.isAfter(fimMesFechado)) {
            ate = fimMesFechado;
        }
        LocalDate de = ate.withDayOfMonth(1).minusMonths(meses - 1L);
        return Banco.numero("SELECT COALESCE(SUM(peso_kg) / NULLIF(COUNT(DISTINCT DATE_FORMAT(data_registro, "
                + "'%Y-%m')), 0), 0) FROM registro_residuo WHERE data_registro BETWEEN ? AND ? "
                + "AND (? IS NULL OR setor_id = ?) AND (? IS NULL OR categoria_id = ?)",
                de, ate, setorId, setorId, categoriaId, categoriaId);
    }
}
