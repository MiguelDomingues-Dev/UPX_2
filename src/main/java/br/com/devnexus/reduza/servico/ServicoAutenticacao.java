package br.com.devnexus.reduza.servico;

import br.com.devnexus.reduza.dao.UsuarioDAO;
import br.com.devnexus.reduza.model.Usuario;
import br.com.devnexus.reduza.util.ErroValidacao;

/** Login: confere o e-mail, a senha (hash PBKDF2) e se o usuário está ativo. */
public class ServicoAutenticacao {

    private final UsuarioDAO usuarios = new UsuarioDAO();

    public Usuario entrar(String email, char[] senha) {
        if (email == null || email.isBlank() || senha.length == 0) {
            throw new ErroValidacao("Informe o e-mail e a senha.");
        }
        UsuarioDAO.Credencial c = usuarios.buscarCredencial(email)
                .orElseThrow(() -> new ErroValidacao("E-mail ou senha incorretos."));
        // Mesma mensagem para e-mail inexistente e senha errada: não revela quais e-mails existem.
        if (!Senhas.confere(senha, c.senhaHash())) {
            throw new ErroValidacao("E-mail ou senha incorretos.");
        }
        if (!c.usuario().ativo()) {
            throw new ErroValidacao("Este usuário está desativado. Procure o administrador.");
        }
        Sessao.iniciar(c.usuario());
        return c.usuario();
    }
}
