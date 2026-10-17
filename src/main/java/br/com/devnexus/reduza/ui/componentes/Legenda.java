package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

/** Pequena legenda de duas cores (ex.: mês anterior × mês atual). */
public class Legenda extends JComponent {

    private static final long serialVersionUID = 1L;
    private String a;
    private String b;
    private final Color corA;
    private final Color corB;

    public Legenda(String a, Color corA, String b, Color corB) {
        this.a = a;
        this.b = b;
        this.corA = corA;
        this.corB = corB;
        setPreferredSize(new Dimension(220, 18));
    }

    public void definir(String a, String b) {
        this.a = a;
        this.b = b;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.suavizar(g);
        g.setFont(Tema.normal(12));
        FontMetrics fm = g.getFontMetrics();
        int x = getWidth() - fm.stringWidth(b) - 16 - fm.stringWidth(a) - 34;
        g.setColor(corA);
        g.fill(new RoundRectangle2D.Double(x, 3, 12, 12, 4, 4));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString(a, x + 17, 14);
        x += 17 + fm.stringWidth(a) + 16;
        g.setColor(corB);
        g.fill(new RoundRectangle2D.Double(x, 3, 12, 12, 4, 4));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString(b, x + 17, 14);
        g.dispose();
    }
}
