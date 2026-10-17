package br.com.devnexus.reduza.dao;

import br.com.devnexus.reduza.model.Perfil;
import br.com.devnexus.reduza.model.Usuario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    /** Usuário e o hash da senha, usados só no login. */
    public record Credencial(Usuario usuario, String senhaHash) {
    }

    private static Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id"), rs.getString("nome"), rs.getString("email"),
                Perfil.valueOf(rs.getString("perfil")), rs.getBoolean("ativo"));
    }

    public Optional<Credencial> buscarCredencial(String email) {
        return Banco.um("SELECT id, nome, email, perfil, ativo, senha_hash FROM usuario WHERE email = ?",
                rs -> new Credencial(mapear(rs), rs.getString("senha_hash")), email.trim().toLowerCase());
    }

    public List<Usuario> listar() {
        return Banco.lista("SELECT id, nome, email, perfil, ativo FROM usuario ORDER BY nome", UsuarioDAO::mapear);
    }

    public int inserir(String nome, String email, String senhaHash, Perfil perfil) {
        return Banco.inserir("INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES (?, ?, ?, ?)",
                nome.trim(), email.trim().toLowerCase(), senhaHash, perfil);
    }

    public void atualizar(int id, String nome, String email, Perfil perfil) {
        Banco.executar("UPDATE usuario SET nome = ?, email = ?, perfil = ? WHERE id = ?",
                nome.trim(), email.trim().toLowerCase(), perfil, id);
    }

    public void trocarSenha(int id, String senhaHash) {
        Banco.executar("UPDATE usuario SET senha_hash = ? WHERE id = ?", senhaHash, id);
    }

    public void definirAtivo(int id, boolean ativo) {
        Banco.executar("UPDATE usuario SET ativo = ? WHERE id = ?", ativo, id);
    }

    public int contarAdminsAtivos() {
        return (int) Banco.numero("SELECT COUNT(*) FROM usuario WHERE perfil = 'ADMIN' AND ativo = TRUE");
    }
}
