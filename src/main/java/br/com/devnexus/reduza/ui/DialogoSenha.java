package br.com.devnexus.reduza.ui;

import br.com.devnexus.reduza.dao.UsuarioDAO;
import br.com.devnexus.reduza.model.Usuario;
import br.com.devnexus.reduza.servico.Senhas;
import br.com.devnexus.reduza.servico.Sessao;
import br.com.devnexus.reduza.ui.componentes.Botao;
import br.com.devnexus.reduza.ui.componentes.CampoSenha;
import br.com.devnexus.reduza.ui.componentes.Ui;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Troca da própria senha (confere a senha atual antes). */
class DialogoSenha extends JDialog {

    private static final long serialVersionUID = 1L;

    DialogoSenha(Frame dono) {
        super(dono, "Trocar senha", true);
        CampoSenha atual = new CampoSenha();
        CampoSenha nova = new CampoSenha();
        CampoSenha repetir = new CampoSenha();
        JLabel erro = new JLabel(" ");
        erro.setForeground(Tema.ERRO);
        erro.setFont(Tema.negrito(13));
        JPanel col = Ui.coluna();
        col.setOpaque(true);
        col.setBackground(Tema.BRANCO);
        col.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        col.add(Ui.campoRotulado("Senha atual", atual));
        col.add(Ui.espaco(12));
        col.add(Ui.campoRotulado("Nova senha (mínimo 8 caracteres, com letras e números)", nova));
        col.add(Ui.espaco(12));
        col.add(Ui.campoRotulado("Repita a nova senha", repetir));
        col.add(Ui.espaco(10));
        col.add(erro);
        Botao salvar = new Botao("Salvar nova senha");
        salvar.addActionListener(e -> {
            Usuario u = Sessao.usuario();
            UsuarioDAO dao = new UsuarioDAO();
            char[] a = atual.getPassword();
            char[] n = nova.getPassword();
            char[] r = repetir.getPassword();
            try {
                String hash = dao.buscarCredencial(u.email()).map(UsuarioDAO.Credencial::senhaHash).orElse(null);
                if (!Senhas.confere(a, hash)) {
                    erro.setText("A senha atual não confere.");
                    return;
                }
                String problema = Senhas.problemaNaSenha(n);
                if (problema != null) {
                    erro.setText(problema);
                    return;
                }
                if (!Arrays.equals(n, r)) {
                    erro.setText("As duas senhas novas são diferentes.");
                    return;
                }
                dao.trocarSenha(u.id(), Senhas.gerarHash(n));
                Ui.erro(this, "Senha alterada com sucesso.");
                dispose();
            } catch (RuntimeException ex) {
                erro.setText(ex.getMessage());
            } finally {
                Arrays.fill(a, '\0');
                Arrays.fill(n, '\0');
                Arrays.fill(r, '\0');
            }
        });
        JPanel rodape = Ui.linha(0, salvar);
        col.add(Ui.espaco(8));
        col.add(rodape);
        rodape.setAlignmentX(LEFT_ALIGNMENT);
        getContentPane().add(col, BorderLayout.CENTER);
        pack();
        setSize(Math.max(getWidth(), 460), getHeight());
        setLocationRelativeTo(dono);
    }
}
