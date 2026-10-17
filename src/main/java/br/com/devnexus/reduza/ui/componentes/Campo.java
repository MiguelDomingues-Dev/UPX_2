package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

/** Campo de texto com dica cinza (placeholder) e borda verde quando em foco. */
public class Campo extends JTextField {

    private static final long serialVersionUID = 1L;
    private final String dica;

    public Campo(String dica) {
        this.dica = dica;
        setFont(Tema.normal(14));
        setForeground(Tema.TEXTO);
        setBorder(borda(Tema.BORDA_CAMPO));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(borda(Tema.VERDE));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(borda(Tema.BORDA_CAMPO));
            }
        });
    }

    public Campo(String dica, String valor) {
        this(dica);
        setText(valor);
    }

    static Border borda(Color cor) {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(cor, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 120), 38);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (getText().isEmpty() && dica != null && !dica.isEmpty()) {
            Graphics2D g = (Graphics2D) g0.create();
            Tema.suavizar(g);
            g.setColor(new Color(0x93A39D));
            g.setFont(getFont());
            g.drawString(dica, getInsets().left, (getHeight() + g.getFontMetrics().getAscent()
                    - g.getFontMetrics().getDescent()) / 2);
            g.dispose();
        }
    }
}
