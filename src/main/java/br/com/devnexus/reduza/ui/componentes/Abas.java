package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** Abas simples em forma de "pílulas", com o mesmo visual em qualquer sistema. */
public class Abas extends JPanel {

    private static final long serialVersionUID = 1L;
    private final JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    private final CardLayout cartoes = new CardLayout();
    private final JPanel conteudo = new JPanel(cartoes);
    private final List<Pilula> pilulas = new ArrayList<>();
    private final IntConsumer aoTrocar;
    private int selecionada;

    public Abas(IntConsumer aoTrocar) {
        super(new BorderLayout(0, 14));
        this.aoTrocar = aoTrocar;
        setOpaque(false);
        barra.setOpaque(false);
        conteudo.setOpaque(false);
        add(barra, BorderLayout.NORTH);
        add(conteudo, BorderLayout.CENTER);
    }

    public void adicionar(String titulo, JComponent painel) {
        int indice = pilulas.size();
        Pilula p = new Pilula(titulo, indice);
        pilulas.add(p);
        barra.add(p);
        conteudo.add(painel, String.valueOf(indice));
        if (indice == 0) {
            p.ativa = true;
        }
    }

    public void selecionar(int indice) {
        selecionada = indice;
        for (Pilula p : pilulas) {
            p.ativa = p.indice == indice;
            p.repaint();
        }
        cartoes.show(conteudo, String.valueOf(indice));
        aoTrocar.accept(indice);
    }

    public int selecionada() {
        return selecionada;
    }

    private final class Pilula extends JComponent {
        private static final long serialVersionUID = 1L;
        private final String titulo;
        private final int indice;
        private boolean ativa;

        Pilula(String titulo, int indice) {
            this.titulo = titulo;
            this.indice = indice;
            setFont(Tema.negrito(13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selecionar(Pilula.this.indice);
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(getFontMetrics(getFont()).stringWidth(titulo) + 32, 34);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Tema.suavizar(g);
            g.setColor(ativa ? Tema.VERDE : Tema.BRANCO);
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
            if (!ativa) {
                g.setColor(Tema.BORDA_CAMPO);
                g.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, getHeight(), getHeight()));
            }
            g.setFont(getFont());
            g.setColor(ativa ? Tema.BRANCO : Tema.TEXTO);
            int w = g.getFontMetrics().stringWidth(titulo);
            g.drawString(titulo, (getWidth() - w) / 2, getHeight() / 2 + 5);
            g.dispose();
        }
    }
}
