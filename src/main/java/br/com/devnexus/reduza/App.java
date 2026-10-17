package br.com.devnexus.reduza;

import br.com.devnexus.reduza.config.ConexaoBanco;
import br.com.devnexus.reduza.servico.ServicoAutenticacao;
import br.com.devnexus.reduza.ui.JanelaPrincipal;
import br.com.devnexus.reduza.ui.Tela;
import br.com.devnexus.reduza.ui.TelaLogin;
import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.ui.telas.TelaCadastros;
import br.com.devnexus.reduza.ui.telas.TelaPainel;
import br.com.devnexus.reduza.ui.telas.TelaRegistro;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada do Reduza.
 *
 *   java -jar reduza.jar                      abre o sistema
 *   java -jar reduza.jar --capturas pasta     gera imagens das telas (para a documentação)
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        configurarAparencia();
        if (args.length >= 2 && args[0].equals("--capturas")) {
            capturas(new File(args[1]));
            return;
        }
        SwingUtilities.invokeLater(App::iniciar);
    }

    private static void configurarAparencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // segue com o padrão
        }
        UIManager.put("OptionPane.messageFont", Tema.normal(14));
        UIManager.put("OptionPane.buttonFont", Tema.negrito(13));
        UIManager.put("TabbedPane.selected", Tema.BRANCO);
        UIManager.put("TabbedPane.contentAreaColor", Tema.FUNDO);
        UIManager.put("ComboBox.selectionBackground", Tema.VERDE_CLARO);
        UIManager.put("ComboBox.selectionForeground", Tema.TEXTO);
        UIManager.put("ToolTip.font", Tema.normal(12));
    }

    /** Testa a conexão antes da tela de login, com mensagem clara se o banco não estiver pronto. */
    private static void iniciar() {
        while (true) {
            try (Connection c = ConexaoBanco.abrir()) {
                break;
            } catch (SQLException e) {
                Object[] opcoes = {"Tentar novamente", "Sair"};
                int r = JOptionPane.showOptionDialog(null,
                        "<html><b>Não foi possível conectar ao banco de dados.</b><br><br>"
                                + "Tentativa: " + ConexaoBanco.descricao() + "<br>"
                                + "Erro: " + e.getMessage() + "<br><br>"
                                + "Verifique se o MySQL está ligado, se o script database/reduza_db.sql foi executado<br>"
                                + "e se o arquivo reduza.properties tem o usuário e a senha corretos.</html>",
                        "Reduza", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, opcoes, opcoes[0]);
                if (r != 0) {
                    System.exit(1);
                }
            }
        }
        abrirLogin();
    }

    private static void abrirLogin() {
        new TelaLogin(() -> new JanelaPrincipal(App::abrirLogin).setVisible(true)).setVisible(true);
    }

    // ------------------------------------------------------------------ capturas de tela

    private static void capturas(File pasta) throws Exception {
        if (!pasta.isDirectory() && !pasta.mkdirs()) {
            throw new IllegalStateException("Não foi possível criar " + pasta);
        }
        TelaLogin[] login = new TelaLogin[1];
        SwingUtilities.invokeAndWait(() -> {
            login[0] = new TelaLogin(() -> { });
            login[0].preencher("camila@exemplo.com.br", "reduza123");
            login[0].setVisible(true);
        });
        salvar(login[0].getContentPane(), new File(pasta, "01_login.png"));
        SwingUtilities.invokeAndWait(() -> login[0].dispose());

        new ServicoAutenticacao().entrar("camila@exemplo.com.br", "reduza123".toCharArray());
        JanelaPrincipal[] j = new JanelaPrincipal[1];
        SwingUtilities.invokeAndWait(() -> {
            j[0] = new JanelaPrincipal(() -> { });
            j[0].setVisible(true);
        });
        String[][] ordem = {{"painel", "02_painel"}, {"registro", "04_registro"}, {"historico", "05_historico"},
                {"metas", "06_metas"}, {"descartaveis", "07_descartaveis"}, {"inspecoes", "08_inspecoes"},
                {"cadastros", "09_cadastros"}};
        for (String[] o : ordem) {
            SwingUtilities.invokeAndWait(() -> {
                j[0].abrir(o[0]);
                if (o[0].equals("painel")) {
                    ((TelaPainel) j[0].tela("painel")).mostrarMes(YearMonth.of(2026, 9));
                }
                if (o[0].equals("registro")) {
                    ((TelaRegistro) j[0].tela("registro")).exemplo("12,5");
                }
            });
            salvar(j[0].getContentPane(), new File(pasta, o[1] + ".png"));
            salvarPaginaInteira(j[0].tela(o[0]), new File(pasta, o[1] + "_completa.png"));
            if (o[0].equals("painel")) {
                SwingUtilities.invokeAndWait(() -> ((TelaPainel) j[0].tela("painel"))
                        .mostrarMes(YearMonth.now()));
                salvar(j[0].getContentPane(), new File(pasta, "03_painel_mes_atual.png"));
            }
            if (o[0].equals("cadastros")) {
                SwingUtilities.invokeAndWait(() -> ((TelaCadastros) j[0].tela("cadastros")).abrirAba(1));
                salvar(j[0].getContentPane(), new File(pasta, "10_cadastros_categorias.png"));
            }
        }
        SwingUtilities.invokeAndWait(() -> j[0].dispose());
        System.out.println("Capturas salvas em " + pasta.getAbsolutePath());
        System.exit(0);
    }

    private static void salvar(Container c, File arquivo) throws Exception {
        Thread.sleep(500);
        BufferedImage[] img = new BufferedImage[1];
        SwingUtilities.invokeAndWait(() -> {
            c.validate();
            img[0] = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img[0].createGraphics();
            c.printAll(g);
            g.dispose();
        });
        ImageIO.write(img[0], "png", arquivo);
    }

    /** Desenha a tela inteira, inclusive a parte que fica abaixo da rolagem. */
    private static void salvarPaginaInteira(Tela t, File arquivo) throws Exception {
        BufferedImage[] img = new BufferedImage[1];
        SwingUtilities.invokeAndWait(() -> {
            Component pai = t.getParent();
            int w = pai.getWidth();
            int h = Math.max(t.getPreferredSize().height, pai.getHeight());
            JScrollPane rolagem = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, t);
            t.setSize(w, h);
            t.doLayout();
            validarTudo(t);
            img[0] = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img[0].createGraphics();
            t.printAll(g);
            g.dispose();
            rolagem.revalidate();
        });
        ImageIO.write(img[0], "png", arquivo);
    }

    private static void validarTudo(Container c) {
        c.doLayout();
        for (Component filho : c.getComponents()) {
            if (filho instanceof Container ct) {
                validarTudo(ct);
            }
        }
    }
}
