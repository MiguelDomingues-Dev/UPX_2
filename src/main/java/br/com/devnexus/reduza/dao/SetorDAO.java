package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.model.Setor;
import java.util.List;

public class SetorDAO {

    private static final String CAMPOS = "SELECT id, nome, descricao, qtd_colaboradores, ativo FROM setor ";

    public List<Setor> listar(boolean apenasAtivos) {
        return Banco.lista(CAMPOS + (apenasAtivos ? "WHERE ativo = TRUE " : "") + "ORDER BY nome",
                rs -> new Setor(rs.getInt("id"), rs.getString("nome"), rs.getString("descricao"),
                        rs.getInt("qtd_colaboradores"), rs.getBoolean("ativo")));
    }

    public int inserir(String nome, String descricao, int colaboradores) {
        return Banco.inserir("INSERT INTO setor (nome, descricao, qtd_colaboradores) VALUES (?, ?, ?)",
                nome.trim(), vazioParaNulo(descricao), colaboradores);
    }

    public void atualizar(int id, String nome, String descricao, int colaboradores) {
        Banco.executar("UPDATE setor SET nome = ?, descricao = ?, qtd_colaboradores = ? WHERE id = ?",
                nome.trim(), vazioParaNulo(descricao), colaboradores, id);
    }

    public void definirAtivo(int id, boolean ativo) {
        Banco.executar("UPDATE setor SET ativo = ? WHERE id = ?", ativo, id);
    }

    static String vazioParaNulo(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
