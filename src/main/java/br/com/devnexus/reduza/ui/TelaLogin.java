package br.com.devnexus.reduza.ui;

import br.com.devnexus.reduza.servico.ServicoAutenticacao;
import br.com.devnexus.reduza.ui.componentes.Botao;
import br.com.devnexus.reduza.ui.componentes.Campo;
import br.com.devnexus.reduza.ui.componentes.CampoSenha;
import br.com.devnexus.reduza.ui.componentes.Ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

/** Tela de entrada: e-mail e senha. Ao entrar, abre a janela principal. */
public class TelaLogin extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Campo email = new Campo("seu.email@empresa.com.br");
    private final CampoSenha senha = new CampoSenha();
    private final JLabel erro = new JLabel(" ");
    private final Botao entrar = new Botao("Entrar");
    private final Runnable aoEntrar;

    public TelaLogin(Runnable aoEntrar) {
        super("Reduza · Entrar");
        this.aoEntrar = aoEntrar;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(980, 620);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        setIconImages(Icones.janela());

        JPanel raiz = new JPanel(new GridLayout(1, 2));
        raiz.add(new PainelMarca());

        JPanel lado = new JPanel(new GridBagLayout());
        lado.setBackground(Tema.BRANCO);
        JPanel form = Ui.coluna();
        form.setPreferredSize(new Dimension(340, 360));
        JLabel t = Ui.titulo("Entrar", 28);
        t.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = Ui.texto("<html><div style='width:250px'>Use o e-mail e a senha cadastrados pelo "
                + "administrador do sistema.</div></html>");
        s.setAlignmentX(LEFT_ALIGNMENT);
        form.add(t);
        form.add(Ui.espaco(6));
        form.add(s);
        form.add(Ui.espaco(26));
        form.add(Ui.campoRotulado("E-mail", email));
        form.add(Ui.espaco(14));
        form.add(Ui.campoRotulado("Senha", senha));
        form.add(Ui.espaco(10));
        erro.setFont(Tema.negrito(13));
        erro.setForeground(Tema.ERRO);
        erro.setAlignmentX(LEFT_ALIGNMENT);
        form.add(erro);
        form.add(Ui.espaco(12));
        entrar.setAlignmentX(LEFT_ALIGNMENT);
        entrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(entrar);
        lado.add(form);
        raiz.add(lado);
        setContentPane(raiz);

        entrar.addActionListener(e -> tentar());
        senha.addActionListener(e -> tentar());
        email.addActionListener(e -> senha.requestFocusInWindow());
        getRootPane().setDefaultButton(entrar);
    }

    /** Usado pelo modo de capturas de tela e por testes. */
    public void preencher(String e, String s) {
        email.setText(e);
        senha.setText(s);
    }

    private void tentar() {
        entrar.setEnabled(false);
        erro.setText(" ");
        String e = email.getText();
        char[] s = senha.getPassword();
        // Verificar a senha leva alguns décimos de segundo (PBKDF2): roda fora da tela para não travar
        new SwingWorker<Void, Void>() {
            private RuntimeException falha;

            @Override
            protected Void doInBackground() {
                try {
                    new ServicoAutenticacao().entrar(e, s);
                } catch (RuntimeException ex) {
                    falha = ex;
                } finally {
                    Arrays.fill(s, '\0');
                }
                return null;
            }

            @Override
            protected void done() {
                entrar.setEnabled(true);
                if (falha != null) {
                    erro.setText(falha.getMessage());
                    senha.setText("");
                    senha.requestFocusInWindow();
                } else {
                    dispose();
                    aoEntrar.run();
                }
            }
        }.execute();
    }

    /** Lado esquerdo: fundo verde-escuro com a marca e a proposta do sistema. */
    private static final class PainelMarca extends JPanel {
        private static final long serialVersionUID = 1L;

        PainelMarca() {
            setBackground(Tema.VERDE_ESCURO);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(48, 48, 40, 48));
            JPanel col = Ui.coluna();
            col.add(Ui.espaco(70));
            JLabel l1 = new JLabel("<html>Meça. Compare.<br>Reduza.</html>");
            l1.setFont(Tema.negrito(32));
            l1.setForeground(Color.WHITE);
            JLabel l2 = new JLabel("<html><div style='width:330px'>Registre os resíduos de cada setor, "
                    + "acompanhe as metas e veja em reais quanto a empresa gasta e economiza.</div></html>");
            l2.setFont(Tema.normal(16));
            l2.setForeground(new Color(0xB9CEC6));
            col.add(l1);
            col.add(Ui.espaco(14));
            col.add(l2);
            add(col, BorderLayout.CENTER);
            JLabel rod = new JLabel("Grupo DevNexus · UPX2 · Centro Universitário Facens");
            rod.setFont(Tema.normal(12));
            rod.setForeground(new Color(0x7FA096));
            add(rod, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            Tema.suavizar(g);
            g.setColor(new Color(255, 255, 255, 10));
            g.fill(new Ellipse2D.Double(getWidth() - 260, getHeight() - 300, 420, 420));
            g.fill(new Ellipse2D.Double(getWidth() - 160, -120, 260, 260));
            Tema.desenharLogo(g, 48, 48, 44);
            g.setColor(Color.WHITE);
            g.setFont(Tema.negrito(26));
            g.drawString("Reduza", 104, 80);
            g.dispose();
        }
    }
}
