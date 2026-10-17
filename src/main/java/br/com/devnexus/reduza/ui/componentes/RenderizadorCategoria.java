package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.model.Categoria;
import br.com.devnexus.reduza.ui.Tema;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

/** Mostra a categoria com uma bolinha da cor do coletor, como no cartaz "Separe certo". */
public class RenderizadorCategoria extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;
    private final String textoNulo;

    public RenderizadorCategoria(String textoNulo) {
        this.textoNulo = textoNulo;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean foco) {
        super.getListCellRendererComponent(list, value, index, sel, foco);
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        if (value instanceof Categoria c) {
            setText(c.nome());
            setIcon(new Bolinha(Tema.corColetor(c.corColetor(), c.id())));
        } else {
            setText(textoNulo);
            setIcon(null);
        }
        return this;
    }

    private record Bolinha(Color cor) implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            Tema.suavizar(g);
            g.setColor(cor);
            g.fill(new Ellipse2D.Double(x, y + 1, 10, 10));
            g.dispose();
        }

        @Override
        public int getIconWidth() {
            return 14;
        }

        @Override
        public int getIconHeight() {
            return 12;
        }
    }
}
