package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.model.Inspecao;
import br.com.devnexus.reduza.model.PontoColeta;
import br.com.devnexus.reduza.model.QualidadeSetor;
import br.com.devnexus.reduza.model.ResultadoInspecao;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Pontos de coleta e inspeções da qualidade do descarte. */
public class InspecaoDAO {

    public List<PontoColeta> pontos(boolean apenasAtivos) {
        return Banco.lista("SELECT p.*, s.nome AS setor FROM ponto_coleta p JOIN setor s ON s.id = p.setor_id "
                + (apenasAtivos ? "WHERE p.ativo = TRUE " : "") + "ORDER BY s.nome, p.nome",
                rs -> new PontoColeta(rs.getInt("id"), rs.getInt("setor_id"), rs.getString("setor"),
                        rs.getString("nome"), rs.getString("localizacao"), rs.getBoolean("ativo")));
    }

    public int inserirPonto(int setorId, String nome, String localizacao) {
        return Banco.inserir("INSERT INTO ponto_coleta (setor_id, nome, localizacao) VALUES (?, ?, ?)",
                setorId, nome.trim(), SetorDAO.vazioParaNulo(localizacao));
    }

    public void atualizarPonto(int id, int setorId, String nome, String localizacao) {
        Banco.executar("UPDATE ponto_coleta SET setor_id = ?, nome = ?, localizacao = ? WHERE id = ?",
                setorId, nome.trim(), SetorDAO.vazioParaNulo(localizacao), id);
    }

    public void definirPontoAtivo(int id, boolean ativo) {
        Banco.executar("UPDATE ponto_coleta SET ativo = ? WHERE id = ?", ativo, id);
    }

    public int registrar(int pontoId, int categoriaId, int usuarioId, LocalDate data, ResultadoInspecao resultado,
                         String observacao) {
        return Banco.inserir("INSERT INTO inspecao_descarte (ponto_id, categoria_id, usuario_id, data_inspecao, "
                + "resultado, observacao) VALUES (?, ?, ?, ?, ?, ?)", pontoId, categoriaId, usuarioId, data,
                resultado, SetorDAO.vazioParaNulo(observacao));
    }

    public List<Inspecao> recentes(int quantidade) {
        return Banco.lista("SELECT i.*, CONCAT(s.nome, ' · ', p.nome) AS ponto, c.nome AS categoria "
                + "FROM inspecao_descarte i JOIN ponto_coleta p ON p.id = i.ponto_id JOIN setor s ON s.id = p.setor_id "
                + "JOIN categoria c ON c.id = i.categoria_id ORDER BY i.data_inspecao DESC, i.id DESC LIMIT ?",
                rs -> new Inspecao(rs.getInt("id"), rs.getInt("ponto_id"), rs.getString("ponto"),
                        rs.getInt("categoria_id"), rs.getString("categoria"), Banco.data(rs, "data_inspecao"),
                        ResultadoInspecao.valueOf(rs.getString("resultado")), rs.getString("observacao")), quantidade);
    }

    public List<QualidadeSetor> qualidadePorSetor(YearMonth mes) {
        return Banco.lista("SELECT setor, inspecoes, corretas, parciais, incorretas, taxa_correto "
                + "FROM vw_qualidade_descarte WHERE mes = ? ORDER BY taxa_correto DESC",
                rs -> new QualidadeSetor(rs.getString("setor"), rs.getInt("inspecoes"), rs.getInt("corretas"),
                        rs.getInt("parciais"), rs.getInt("incorretas"), rs.getDouble("taxa_correto")), mes.atDay(1));
    }

    public List<YearMonth> mesesComInspecao() {
        return Banco.lista("SELECT DISTINCT YEAR(data_inspecao) AS a, MONTH(data_inspecao) AS m "
                + "FROM inspecao_descarte ORDER BY a DESC, m DESC", rs -> YearMonth.of(rs.getInt("a"), rs.getInt("m")));
    }
}
