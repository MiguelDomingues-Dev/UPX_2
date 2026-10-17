package br.com.devnexus.reduza.ui.graficos;

import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/** Evolução mensal: linha com pontos, área suave e destaque no último valor. */
public class GraficoLinha extends Grafico {

    private static final long serialVersionUID = 1L;

    private List<String> rotulos = new ArrayList<>();
    private double[] valores = new double[0];
    private int marco = -1;
    private String textoMarco = "";
    private String unidade = "kg";
    private boolean ultimoParcial;

    public GraficoLinha() {
        super(240);
    }

    /** marco = índice do mês a destacar com linha tracejada (ex.: início das metas); -1 = nenhum. */
    public void definir(List<String> rotulos, double[] valores, int marco, String textoMarco, String unidade) {
        this.rotulos = rotulos;
        this.valores = valores;
        this.marco = marco;
        this.textoMarco = textoMarco;
        this.unidade = unidade;
        this.ultimoParcial = false;
        repaint();
    }

    /** O último ponto é um mês em andamento: linha tracejada e rótulo "parcial". */
    public void marcarUltimoParcial(boolean parcial) {
        this.ultimoParcial = parcial;
        repaint();
    }

    @Override
    protected void desenhar(Graphics2D g, int w, int h) {
        int n = valores.length;
        double max = 0;
        for (double v : valores) {
            max = Math.max(max, v);
        }
        if (n < 2 || max <= 0) {
            semDados(g, w, h);
            return;
        }
        // escala a partir de zero: a queda aparece no tamanho real, sem exagero visual
        double passo = passoBonito(max / 3);
        double vMax = passo * Math.ceil(max * 1.08 / passo);
        double vMin = 0;
        double esq = 46;
        double dir = w - 18;
        double topo = 26;
        double base = h - 30;
        g.setFont(Tema.normal(11));
        for (double v = 0; v <= vMax + 1e-6; v += passo) {
            double y = base - (base - topo) * v / vMax;
            g.setColor(Tema.LINHA);
            g.draw(new Line2D.Double(esq, y, dir, y));
            g.setColor(Tema.TEXTO_SUAVE);
            direita(g, Formatos.inteiro(v), esq - 8, y + 4);
        }
        double[] xs = new double[n];
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = esq + (dir - esq) * i / (n - 1);
            ys[i] = base - (Math.max(valores[i], vMin) - vMin) / (vMax - vMin) * (base - topo);
        }
        if (marco >= 0 && marco < n) {
            g.setColor(Tema.VERDE);
            g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                    new float[]{4f, 4f}, 0f));
            double xm = marco > 0 ? (xs[marco] + xs[marco - 1]) / 2 : xs[marco];
            g.draw(new Line2D.Double(xm, topo - 6, xm, base));
            g.setFont(Tema.negrito(11));
            centralizado(g, textoMarco, Math.min(xm, dir - 50), topo - 10);
        }
        Path2D area = new Path2D.Double();
        Path2D linha = new Path2D.Double();
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                linha.moveTo(xs[i], ys[i]);
                area.moveTo(xs[i], base);
            }
            if (i > 0) {
                linha.lineTo(xs[i], ys[i]);
            }
            area.lineTo(xs[i], ys[i]);
        }
        area.lineTo(xs[n - 1], base);
        area.closePath();
        g.setPaint(new GradientPaint(0, (float) topo, new Color(18, 122, 99, 60), 0, (float) base,
                new Color(18, 122, 99, 0)));
        g.fill(area);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Tema.VERDE);
        if (ultimoParcial && n > 2) {
            Path2D cheia = new Path2D.Double();
            for (int i = 0; i < n - 1; i++) {
                if (i == 0) {
                    cheia.moveTo(xs[i], ys[i]);
                } else {
                    cheia.lineTo(xs[i], ys[i]);
                }
            }
            g.draw(cheia);
            g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f,
                    new float[]{5f, 6f}, 0f));
            g.draw(new Line2D.Double(xs[n - 2], ys[n - 2], xs[n - 1], ys[n - 1]));
        } else {
            g.draw(linha);
        }
        for (int i = 0; i < n; i++) {
            g.setColor(Color.WHITE);
            g.fill(new Ellipse2D.Double(xs[i] - 5.5, ys[i] - 5.5, 11, 11));
            g.setColor(Tema.VERDE);
            g.setStroke(new BasicStroke(2.4f));
            g.draw(new Ellipse2D.Double(xs[i] - 5.5, ys[i] - 5.5, 11, 11));
            g.setFont(Tema.normal(11));
            g.setColor(Tema.TEXTO_SUAVE);
            centralizado(g, rotulos.get(i), xs[i], base + 20);
        }
        g.setFont(Tema.negrito(12));
        g.setColor(Tema.TEXTO);
        String ultimo = Formatos.kg(valores[n - 1]) + (unidade.isEmpty() ? "" : " " + unidade)
                + (ultimoParcial ? " (parcial)" : "");
        boolean desceu = valores[n - 2] > valores[n - 1];
        direita(g, ultimo, dir + 6, desceu ? ys[n - 1] + 24 : ys[n - 1] - 12);
    }
}
