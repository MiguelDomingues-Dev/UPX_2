package br.com.devnexus.reduza.ui.graficos;

import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/** Colunas simples por mês (ex.: copos consumidos). Meses a partir do marco ficam em verde. */
public class GraficoColunas extends Grafico {

    private static final long serialVersionUID = 1L;
    private List<String> rotulos = new ArrayList<>();
    private double[] valores = new double[0];
    private int destaqueDesde = Integer.MAX_VALUE;

    public GraficoColunas() {
        super(200);
    }

    public void definir(List<String> rotulos, double[] valores, int destaqueDesde) {
        this.rotulos = rotulos;
        this.valores = valores;
        this.destaqueDesde = destaqueDesde < 0 ? Integer.MAX_VALUE : destaqueDesde;
        repaint();
    }

    @Override
    protected void desenhar(Graphics2D g, int w, int h) {
        int n = valores.length;
        double max = 0;
        for (double v : valores) {
            max = Math.max(max, v);
        }
        if (n == 0 || max <= 0) {
            semDados(g, w, h);
            return;
        }
        max = max * 1.15;
        double topo = 22;
        double base = h - 28;
        double col = w / (double) n;
        double larg = Math.min(46, col * 0.55);
        g.setColor(Tema.LINHA);
        g.draw(new Line2D.Double(0, base, w, base));
        for (int i = 0; i < n; i++) {
            double cx = col * i + col / 2;
            double alt = valores[i] / max * (base - topo);
            g.setColor(i >= destaqueDesde ? Tema.VERDE : new Color(0xC4CDC9));
            g.fill(new RoundRectangle2D.Double(cx - larg / 2, base - alt, larg, alt, 8, 8));
            g.setFont(Tema.negrito(11));
            g.setColor(Tema.TEXTO);
            centralizado(g, Formatos.inteiro(valores[i]), cx, base - alt - 6);
            g.setFont(Tema.normal(11));
            g.setColor(Tema.TEXTO_SUAVE);
            centralizado(g, rotulos.get(i), cx, base + 18);
        }
    }
}
