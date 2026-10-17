package br.com.devnexus.reduza.ui.graficos;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

/** Barra de progresso de meta. A cor indica a situação (atingida, perto, em andamento, piorou). */
public class BarraProgresso extends Grafico {

    private static final long serialVersionUID = 1L;
    private double progresso;
    private boolean semDados = true;

    public BarraProgresso() {
        super(16);
    }

    public void definir(Double progresso) {
        this.semDados = progresso == null;
        this.progresso = progresso == null ? 0 : progresso;
        repaint();
    }

    public static Color cor(Double p) {
        if (p == null) {
            return Tema.TEXTO_SUAVE;
        }
        if (p >= 100) {
            return Tema.VERDE;
        }
        if (p >= 70) {
            return new Color(0x2E9E7F);
        }
        if (p >= 0) {
            return Tema.ALERTA;
        }
        return Tema.ERRO;
    }

    @Override
    protected void desenhar(Graphics2D g, int w, int h) {
        g.setColor(new Color(0xE8EEEB));
        g.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
        if (semDados) {
            return;
        }
        double p = Math.max(0, Math.min(100, progresso));
        g.setColor(cor(progresso));
        g.fill(new RoundRectangle2D.Double(0, 0, Math.max(h, w * p / 100), h, h, h));
    }
}
