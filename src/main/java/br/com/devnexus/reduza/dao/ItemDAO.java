package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.model.CompraLote;
import br.com.devnexus.reduza.model.CustoItem;
import br.com.devnexus.reduza.model.Item;
import br.com.devnexus.reduza.model.TipoItem;
import br.com.devnexus.reduza.model.ValorRotulado;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Itens contados em unidades (piloto de descartáveis): cadastro, compras e consumo. */
public class ItemDAO {

    public List<Item> listar() {
        return Banco.lista("SELECT * FROM item ORDER BY tipo, nome",
                rs -> new Item(rs.getInt("id"), rs.getInt("categoria_id"), rs.getString("nome"),
                        TipoItem.valueOf(rs.getString("tipo")), rs.getBigDecimal("peso_unitario_g"),
                        rs.getInt("usos_estimados"), rs.getString("fonte_peso")));
    }

    public int inserir(int categoriaId, String nome, TipoItem tipo, BigDecimal pesoG, int usos, String fonte) {
        return Banco.inserir("INSERT INTO item (categoria_id, nome, tipo, peso_unitario_g, usos_estimados, fonte_peso) "
                + "VALUES (?, ?, ?, ?, ?, ?)", categoriaId, nome.trim(), tipo, pesoG, usos, SetorDAO.vazioParaNulo(fonte));
    }

    public void atualizar(int id, int categoriaId, String nome, TipoItem tipo, BigDecimal pesoG, int usos, String fonte) {
        Banco.executar("UPDATE item SET categoria_id = ?, nome = ?, tipo = ?, peso_unitario_g = ?, usos_estimados = ?, "
                + "fonte_peso = ? WHERE id = ?", categoriaId, nome.trim(), tipo, pesoG, usos,
                SetorDAO.vazioParaNulo(fonte), id);
    }

    // ---------- compras ----------

    public int registrarCompra(int itemId, int usuarioId, LocalDate data, int pacotes, int unidadesPorPacote,
                               BigDecimal valorPacote, String fornecedor) {
        return Banco.inserir("INSERT INTO compra_lote (item_id, usuario_id, data_compra, qtd_pacotes, "
                + "unidades_por_pacote, valor_pacote, fornecedor) VALUES (?, ?, ?, ?, ?, ?, ?)", itemId, usuarioId, data,
                pacotes, unidadesPorPacote, valorPacote, SetorDAO.vazioParaNulo(fornecedor));
    }

    public List<CompraLote> compras(int itemId) {
        return Banco.lista("SELECT cl.*, i.nome FROM compra_lote cl JOIN item i ON i.id = cl.item_id "
                + "WHERE cl.item_id = ? ORDER BY cl.data_compra DESC, cl.id DESC",
                rs -> new CompraLote(rs.getInt("id"), rs.getInt("item_id"), rs.getString("nome"),
                        Banco.data(rs, "data_compra"), rs.getInt("qtd_pacotes"), rs.getInt("unidades_por_pacote"),
                        rs.getBigDecimal("valor_pacote"), rs.getString("fornecedor")), itemId);
    }

    /** Custo médio do item segundo todas as compras (vazio se ainda não houve compra). */
    public Optional<CustoItem> custo(int itemId) {
        return Banco.um("SELECT * FROM vw_custo_item WHERE item_id = ?",
                rs -> new CustoItem(rs.getInt("item_id"), rs.getString("item"), TipoItem.valueOf(rs.getString("tipo")),
                        rs.getInt("usos_estimados"), rs.getLong("unidades_compradas"), rs.getBigDecimal("valor_total"),
                        rs.getBigDecimal("custo_unitario"), rs.getBigDecimal("custo_por_uso")), itemId);
    }

    // ---------- consumo ----------

    public int registrarConsumo(int setorId, int itemId, int usuarioId, LocalDate data, int quantidade) {
        return Banco.inserir("INSERT INTO consumo_item (setor_id, item_id, usuario_id, data_consumo, quantidade) "
                + "VALUES (?, ?, ?, ?, ?)", setorId, itemId, usuarioId, data, quantidade);
    }

    public double consumoNoMes(int itemId, Integer setorId, YearMonth mes) {
        return Banco.numero("SELECT COALESCE(SUM(quantidade), 0) FROM consumo_item WHERE item_id = ? "
                + "AND (? IS NULL OR setor_id = ?) AND data_consumo BETWEEN ? AND ?", itemId, setorId, setorId,
                mes.atDay(1), mes.atEndOfMonth());
    }

    /** Unidades consumidas por mês nos últimos n meses. */
    public List<ValorRotulado> serieConsumo(int itemId, Integer setorId, YearMonth ate, int meses) {
        Map<YearMonth, Double> mapa = new LinkedHashMap<>();
        for (int i = meses - 1; i >= 0; i--) {
            mapa.put(ate.minusMonths(i), 0.0);
        }
        YearMonth primeiro = ate.minusMonths(meses - 1L);
        Banco.lista("SELECT YEAR(data_consumo) AS a, MONTH(data_consumo) AS m, SUM(quantidade) AS q "
                + "FROM consumo_item WHERE item_id = ? AND (? IS NULL OR setor_id = ?) "
                + "AND data_consumo BETWEEN ? AND ? GROUP BY a, m",
                rs -> Map.entry(YearMonth.of(rs.getInt("a"), rs.getInt("m")), rs.getDouble("q")),
                itemId, setorId, setorId, primeiro.atDay(1), ate.atEndOfMonth())
                .forEach(e -> mapa.put(e.getKey(), e.getValue()));
        return mapa.entrySet().stream().map(e -> new ValorRotulado(e.getKey().toString(), e.getValue())).toList();
    }

    public Optional<YearMonth> ultimoMesConsumo(int itemId) {
        return Banco.um("SELECT MAX(data_consumo) AS d FROM consumo_item WHERE item_id = ?",
                rs -> {
                    LocalDate d = Banco.data(rs, "d");
                    return d == null ? null : YearMonth.from(d);
                }, itemId).filter(m -> m != null);
    }
}
