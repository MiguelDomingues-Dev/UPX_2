package br.com.devnexus.reduza.ui.graficos;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/** Base dos gráficos: fundo transparente, antisserrilhado e utilitários de texto. */
abstract class Grafico extends JComponent {

    private static final long serialVersionUID = 1L;

    Grafico(int alturaPreferida) {
        setPreferredSize(new Dimension(300, alturaPreferida));
        setOpaque(false);
    }

    @Override
    protected final void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.suavizar(g);
        desenhar(g, getWidth(), getHeight());
        g.dispose();
    }

    protected abstract void desenhar(Graphics2D g, int w, int h);

    static void centralizado(Graphics2D g, String s, double cx, double y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (float) (cx - fm.stringWidth(s) / 2.0), (float) y);
    }

    static void direita(Graphics2D g, String s, double x, double y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (float) (x - fm.stringWidth(s)), (float) y);
    }

    /** Arredonda o máximo da escala para um número "bonito" (1, 2, 2,5 ou 5 × 10^n). */
    static double tetoBonito(double v) {
        if (v <= 0) {
            return 1;
        }
        double p = Math.pow(10, Math.floor(Math.log10(v)));
        for (double m : new double[]{1, 2, 2.5, 5, 10}) {
            if (m * p >= v) {
                return m * p;
            }
        }
        return 10 * p;
    }

    /** Passo "bonito" para as linhas de grade: 1, 2, 2,5 ou 5 × 10^n. */
    static double passoBonito(double aproximado) {
        if (aproximado <= 0) {
            return 1;
        }
        double p = Math.pow(10, Math.floor(Math.log10(aproximado)));
        for (double m : new double[]{1, 2, 2.5, 5, 10}) {
            if (m * p >= aproximado) {
                return m * p;
            }
        }
        return 10 * p;
    }

    void semDados(Graphics2D g, int w, int h) {
        g.setFont(Tema.normal(13));
        g.setColor(Tema.TEXTO_SUAVE);
        centralizado(g, "Sem dados para o período", w / 2.0, h / 2.0);
    }
}
