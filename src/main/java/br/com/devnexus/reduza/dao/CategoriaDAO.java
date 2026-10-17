package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.model.Categoria;
import java.math.BigDecimal;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar(boolean apenasAtivas) {
        return Banco.lista("SELECT * FROM categoria " + (apenasAtivas ? "WHERE ativo = TRUE " : "") + "ORDER BY id",
                rs -> new Categoria(rs.getInt("id"), rs.getString("nome"), rs.getString("descricao"),
                        rs.getString("cor_coletor"), rs.getBoolean("reciclavel"), rs.getBoolean("perigoso"),
                        rs.getBigDecimal("custo_kg"), rs.getBoolean("ativo")));
    }

    public int inserir(String nome, String cor, boolean reciclavel, boolean perigoso, BigDecimal custoKg) {
        return Banco.inserir("INSERT INTO categoria (nome, cor_coletor, reciclavel, perigoso, custo_kg) "
                + "VALUES (?, ?, ?, ?, ?)", nome.trim(), SetorDAO.vazioParaNulo(cor), reciclavel, perigoso, custoKg);
    }

    public void atualizar(int id, String nome, String cor, boolean reciclavel, boolean perigoso, BigDecimal custoKg) {
        Banco.executar("UPDATE categoria SET nome = ?, cor_coletor = ?, reciclavel = ?, perigoso = ?, custo_kg = ? "
                + "WHERE id = ?", nome.trim(), SetorDAO.vazioParaNulo(cor), reciclavel, perigoso, custoKg, id);
    }

    public void definirAtivo(int id, boolean ativo) {
        Banco.executar("UPDATE categoria SET ativo = ? WHERE id = ?", ativo, id);
    }
}
