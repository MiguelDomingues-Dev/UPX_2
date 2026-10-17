package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.model.RegistroResiduo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {

    private static final String CONSULTA = "SELECT r.id, r.setor_id, s.nome AS setor, r.categoria_id, "
            + "c.nome AS categoria, r.usuario_id, u.nome AS usuario, r.data_registro, r.peso_kg, r.observacao "
            + "FROM registro_residuo r JOIN setor s ON s.id = r.setor_id "
            + "JOIN categoria c ON c.id = r.categoria_id JOIN usuario u ON u.id = r.usuario_id ";

    private static RegistroResiduo mapear(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new RegistroResiduo(rs.getInt("id"), rs.getInt("setor_id"), rs.getString("setor"),
                rs.getInt("categoria_id"), rs.getString("categoria"), rs.getInt("usuario_id"),
                rs.getString("usuario"), Banco.data(rs, "data_registro"), rs.getBigDecimal("peso_kg"),
                rs.getString("observacao"));
    }

    public int inserir(int setorId, int categoriaId, int usuarioId, LocalDate data, BigDecimal pesoKg, String obs) {
        return Banco.inserir("INSERT INTO registro_residuo (setor_id, categoria_id, usuario_id, data_registro, "
                + "peso_kg, observacao) VALUES (?, ?, ?, ?, ?, ?)", setorId, categoriaId, usuarioId, data, pesoKg,
                SetorDAO.vazioParaNulo(obs));
    }

    public void excluir(int id) {
        Banco.executar("DELETE FROM registro_residuo WHERE id = ?", id);
    }

    public List<RegistroResiduo> ultimos(int quantidade) {
        return Banco.lista(CONSULTA + "ORDER BY r.data_registro DESC, r.id DESC LIMIT ?", RegistroDAO::mapear,
                quantidade);
    }

    /** Filtro do histórico. Parâmetros nulos são ignorados. */
    public List<RegistroResiduo> buscar(LocalDate de, LocalDate ate, Integer setorId, Integer categoriaId) {
        StringBuilder sql = new StringBuilder(CONSULTA).append("WHERE 1 = 1 ");
        List<Object> p = new ArrayList<>();
        if (de != null) {
            sql.append("AND r.data_registro >= ? ");
            p.add(de);
        }
        if (ate != null) {
            sql.append("AND r.data_registro <= ? ");
            p.add(ate);
        }
        if (setorId != null) {
            sql.append("AND r.setor_id = ? ");
            p.add(setorId);
        }
        if (categoriaId != null) {
            sql.append("AND r.categoria_id = ? ");
            p.add(categoriaId);
        }
        sql.append("ORDER BY r.data_registro DESC, r.id DESC");
        return Banco.lista(sql.toString(), RegistroDAO::mapear, p.toArray());
    }
}
