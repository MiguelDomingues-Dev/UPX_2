package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.config.ConexaoBanco;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Funções comuns de acesso ao banco (JDBC puro).
 * Todas as consultas usam PreparedStatement, o que impede SQL injection.
 */
final class Banco {

    /** Converte uma linha do ResultSet em um objeto. */
    @FunctionalInterface
    interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    private Banco() {
    }

    static <T> List<T> lista(String sql, Mapeador<T> mapeador, Object... parametros) {
        try (Connection c = ConexaoBanco.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            preencher(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapeador.mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new ErroBanco(e);
        }
    }

    static <T> Optional<T> um(String sql, Mapeador<T> mapeador, Object... parametros) {
        List<T> l = lista(sql, mapeador, parametros);
        return l.isEmpty() ? Optional.empty() : Optional.of(l.get(0));
    }

    static double numero(String sql, Object... parametros) {
        return um(sql, rs -> rs.getDouble(1), parametros).orElse(0.0);
    }

    /** INSERT que devolve o id gerado. */
    static int inserir(String sql, Object... parametros) {
        try (Connection c = ConexaoBanco.abrir();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, parametros);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new ErroBanco(e);
        }
    }

    /** UPDATE ou DELETE; devolve quantas linhas mudaram. */
    static int executar(String sql, Object... parametros) {
        try (Connection c = ConexaoBanco.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            preencher(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroBanco(e);
        }
    }

    private static void preencher(PreparedStatement ps, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object p = parametros[i];
            int pos = i + 1;
            if (p == null) {
                ps.setNull(pos, Types.NULL);
            } else if (p instanceof LocalDate d) {
                ps.setDate(pos, Date.valueOf(d));
            } else if (p instanceof BigDecimal b) {
                ps.setBigDecimal(pos, b);
            } else if (p instanceof Integer n) {
                ps.setInt(pos, n);
            } else if (p instanceof Boolean b) {
                ps.setBoolean(pos, b);
            } else if (p instanceof Enum<?> e) {
                ps.setString(pos, e.name());
            } else {
                ps.setString(pos, p.toString());
            }
        }
    }

    static LocalDate data(ResultSet rs, String coluna) throws SQLException {
        Date d = rs.getDate(coluna);
        return d == null ? null : d.toLocalDate();
    }

    static Integer inteiroOuNulo(ResultSet rs, String coluna) throws SQLException {
        int v = rs.getInt(coluna);
        return rs.wasNull() ? null : v;
    }
}
