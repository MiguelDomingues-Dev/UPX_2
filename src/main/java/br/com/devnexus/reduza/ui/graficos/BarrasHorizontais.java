package br.com.devnexus.reduza.ui.graficos;

import br.com.devnexus.reduza.model.ValorRotulado;
import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/** Ranking de setores: barra proporcional, kg e participação no total. */
public class BarrasHorizontais extends Grafico {

    private static final long serialVersionUID = 1L;
    private List<ValorRotulado> itens = new ArrayList<>();
    private final boolean percentual;

    public BarrasHorizontais() {
        this(false);
    }

    /** percentual = true: valores de 0 a 100, sem coluna de participação. */
    public BarrasHorizontais(boolean percentual) {
        super(160);
        this.percentual = percentual;
    }

    public void definir(List<ValorRotulado> itens) {
        this.itens = itens;
        setPreferredSize(new Dimension(300, Math.max(60, itens.size() * 34 + 6)));
        revalidate();
        repaint();
    }

    @Override
    protected void desenhar(Graphics2D g, int w, int h) {
        if (itens.isEmpty()) {
            semDados(g, w, h);
            return;
        }
        double total = itens.stream().mapToDouble(ValorRotulado::valor).sum();
        double max = percentual ? 100 : itens.stream().mapToDouble(ValorRotulado::valor).max().orElse(1);
        double xBarra = 130;
        double largura = Math.max(60, w - xBarra - (percentual ? 70 : 130));
        for (int i = 0; i < itens.size(); i++) {
            ValorRotulado v = itens.get(i);
            double y = 6 + i * 34;
            g.setFont(Tema.normal(13));
            g.setColor(Tema.TEXTO);
            g.drawString(v.rotulo(), 0, (float) (y + 15));
            g.setColor(new java.awt.Color(0xE8EEEB));
            g.fill(new RoundRectangle2D.Double(xBarra, y + 4, largura, 14, 14, 14));
            g.setColor(Tema.VERDE);
            g.fill(new RoundRectangle2D.Double(xBarra, y + 4, Math.max(14, largura * v.valor() / max), 14, 14, 14));
            g.setFont(Tema.negrito(13));
            g.setColor(Tema.TEXTO);
            if (percentual) {
                direita(g, Formatos.percentual(v.valor()), w - 4, y + 16);
                continue;
            }
            direita(g, Formatos.kgComUnidade(v.valor()), xBarra + largura + 82, y + 16);
            g.setFont(Tema.normal(12));
            g.setColor(Tema.TEXTO_SUAVE);
            direita(g, Formatos.percentual(Math.round(v.valor() / total * 100)), w - 4, y + 16);
        }
    }
}
