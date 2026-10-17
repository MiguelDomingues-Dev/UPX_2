package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Painel branco de cantos arredondados, com sombra discreta. Base de todas as telas. */
public class Cartao extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int RAIO = 16;

    public Cartao() {
        this(new BorderLayout());
    }

    public Cartao(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(16, 48, 42, 7));
            g.fill(new RoundRectangle2D.Double(-i + 3, -i + 5, w - 6 + 2 * i, h - 7 + 2 * i, RAIO + i, RAIO + i));
        }
        g.setColor(Tema.BRANCO);
        g.fill(new RoundRectangle2D.Double(3, 2, w - 6, h - 6, RAIO, RAIO));
        g.setColor(Tema.LINHA);
        g.draw(new RoundRectangle2D.Double(3, 2, w - 7, h - 7, RAIO, RAIO));
        g.dispose();
        super.paintComponent(g0);
    }
}
