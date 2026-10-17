package br.com.devnexus.reduza.ui;

import br.com.devnexus.reduza.model.Usuario;
import br.com.devnexus.reduza.servico.Sessao;
import br.com.devnexus.reduza.ui.componentes.Botao;
import br.com.devnexus.reduza.ui.componentes.Ui;
import br.com.devnexus.reduza.ui.telas.TelaCadastros;
import br.com.devnexus.reduza.ui.telas.TelaDescartaveis;
import br.com.devnexus.reduza.ui.telas.TelaHistorico;
import br.com.devnexus.reduza.ui.telas.TelaInspecoes;
import br.com.devnexus.reduza.ui.telas.TelaMetas;
import br.com.devnexus.reduza.ui.telas.TelaPainel;
import br.com.devnexus.reduza.ui.telas.TelaRegistro;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/** Janela principal: menu lateral, cabeçalho com o usuário e a área das telas. */
public class JanelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Map<String, Tela> telas = new LinkedHashMap<>();
    private final CardLayout cartoes = new CardLayout();
    private final JPanel area = new JPanel(cartoes);
    private final JLabel titulo = Ui.titulo("", 24);
    private final JLabel subtitulo = Ui.texto("");
    private final MenuLateral menu;
    private String atual;

    public JanelaPrincipal(Runnable aoSair) {
        super("Reduza · Gestão de Resíduos");
        Usuario u = Sessao.usuario();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setIconImages(Icones.janela());

        menu = new MenuLateral(this::abrir, "Protótipo UPX2 · DevNexus");
        registrar("painel", "Painel", new TelaPainel());
        registrar("registro", "Registrar resíduo", new TelaRegistro());
        registrar("historico", "Histórico", new TelaHistorico());
        registrar("metas", "Metas", new TelaMetas());
        registrar("descartaveis", "Descartáveis", new TelaDescartaveis());
        registrar("inspecoes", "Inspeções", new TelaInspecoes());
        if (u.perfil().podeCadastrar()) {
            registrar("cadastros", "Cadastros", new TelaCadastros());
        }

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Tema.FUNDO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(22, 28, 14, 28));
        JPanel textos = Ui.coluna();
        textos.add(titulo);
        textos.add(Ui.espaco(2));
        textos.add(subtitulo);
        cabecalho.add(textos, BorderLayout.WEST);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setOpaque(false);
        direita.add(new Avatar(u.iniciais()));
        JPanel quem = Ui.coluna();
        JLabel nome = Ui.rotulo(u.nome());
        quem.add(nome);
        quem.add(Ui.texto(u.perfil().rotulo()));
        direita.add(quem);
        Botao senha = new Botao("Trocar senha", Botao.Estilo.TEXTO);
        senha.addActionListener(e -> new DialogoSenha(this).setVisible(true));
        Botao sair = new Botao("Sair", Botao.Estilo.SECUNDARIO);
        sair.addActionListener(e -> {
            Sessao.encerrar();
            dispose();
            aoSair.run();
        });
        direita.add(senha);
        direita.add(sair);
        cabecalho.add(direita, BorderLayout.EAST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Tema.FUNDO);
        centro.add(cabecalho, BorderLayout.NORTH);
        area.setBackground(Tema.FUNDO);
        centro.add(area, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menu, BorderLayout.WEST);
        getContentPane().add(centro, BorderLayout.CENTER);
        abrir("painel");
    }

    private void registrar(String chave, String rotulo, Tela tela) {
        telas.put(chave, tela);
        menu.adicionar(chave, rotulo);
        JScrollPane rolagem = new JScrollPane(tela);
        rolagem.setBorder(null);
        rolagem.getVerticalScrollBar().setUnitIncrement(18);
        rolagem.getViewport().setBackground(Tema.FUNDO);
        area.add(rolagem, chave);
    }

    public void abrir(String chave) {
        Tela t = telas.get(chave);
        if (t == null) {
            return;
        }
        atual = chave;
        menu.selecionar(chave);
        try {
            t.atualizar();
        } catch (RuntimeException e) {
            Ui.erro(this, e.getMessage());
        }
        titulo.setText(t.titulo());
        subtitulo.setText(t.subtitulo().isEmpty() ? " " : t.subtitulo());
        cartoes.show(area, chave);
    }

    /** Atualiza o subtítulo quando a própria tela muda de contexto (ex.: troca de mês). */
    public void atualizarCabecalho() {
        Tela t = telas.get(atual);
        if (t != null) {
            subtitulo.setText(t.subtitulo().isEmpty() ? " " : t.subtitulo());
        }
    }

    public Map<String, Tela> telas() {
        return telas;
    }

    public Tela tela(String chave) {
        return telas.get(chave);
    }

    private static final class Avatar extends JComponent {
        private static final long serialVersionUID = 1L;
        private final String iniciais;

        Avatar(String iniciais) {
            this.iniciais = iniciais;
            setPreferredSize(new Dimension(38, 38));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Tema.suavizar(g);
            g.setColor(Tema.VERDE_CLARO);
            g.fill(new Ellipse2D.Double(0, 0, 37, 37));
            g.setColor(new Color(0x0E6652));
            g.setFont(Tema.negrito(13));
            int w = g.getFontMetrics().stringWidth(iniciais);
            g.drawString(iniciais, (38 - w) / 2, 24);
            g.dispose();
        }
    }
}
