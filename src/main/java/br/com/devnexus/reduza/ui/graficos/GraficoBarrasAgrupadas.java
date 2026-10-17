package br.com.devnexus.reduza.ui.graficos;

import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/** Duas barras por categoria: mês anterior (clara) e mês atual (cor cheia do coletor). */
public class GraficoBarrasAgrupadas extends Grafico {

    private static final long serialVersionUID = 1L;

    private List<String> categorias = new ArrayList<>();
    private List<Color> cores = new ArrayList<>();
    private double[] anterior = new double[0];
    private double[] atual = new double[0];

    public GraficoBarrasAgrupadas() {
        super(240);
    }

    public void definir(List<String> categorias, List<Color> cores, double[] anterior, double[] atual) {
        this.categorias = categorias;
        this.cores = cores;
        this.anterior = anterior;
        this.atual = atual;
        repaint();
    }

    @Override
    protected void desenhar(Graphics2D g, int w, int h) {
        int n = categorias.size();
        double max = 0;
        for (int i = 0; i < n; i++) {
            max = Math.max(max, Math.max(anterior[i], atual[i]));
        }
        if (n == 0 || max <= 0) {
            semDados(g, w, h);
            return;
        }
        max = max * 1.12;
        double topo = 26;
        double base = h - 30;
        double altura = base - topo;
        double grupo = w / (double) n;
        double larg = Math.min(44, grupo * 0.28);
        g.setStroke(new BasicStroke(1f));
        g.setColor(Tema.LINHA);
        g.draw(new Line2D.Double(0, base, w, base));
        for (int i = 0; i < n; i++) {
            double cx = grupo * i + grupo / 2;
            double hA = anterior[i] / max * altura;
            double hB = atual[i] / max * altura;
            Color cor = cores.get(i);
            g.setColor(Tema.misturar(cor, Color.WHITE, 0.58));
            g.fill(new RoundRectangle2D.Double(cx - larg - 3, base - hA, larg, hA, 8, 8));
            g.setColor(cor);
            g.fill(new RoundRectangle2D.Double(cx + 3, base - hB, larg, hB, 8, 8));
            g.setFont(Tema.normal(11));
            g.setColor(Tema.TEXTO_SUAVE);
            centralizado(g, Formatos.kg(anterior[i]), cx - larg / 2 - 3, base - hA - 6);
            g.setFont(Tema.negrito(11));
            g.setColor(Tema.TEXTO);
            centralizado(g, Formatos.kg(atual[i]), cx + larg / 2 + 3, base - hB - 6);
            g.setFont(Tema.normal(12));
            g.setColor(Tema.TEXTO_SUAVE);
            centralizado(g, categorias.get(i), cx, base + 20);
        }
    }
}
