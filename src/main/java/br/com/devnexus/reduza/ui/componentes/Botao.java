package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

/** Botão desenhado à mão para ter a mesma aparência em Windows, Linux e macOS. */
public class Botao extends JButton {

    private static final long serialVersionUID = 1L;

    public enum Estilo { PRIMARIO, SECUNDARIO, PERIGO, TEXTO }

    private final Estilo estilo;

    public Botao(String texto, Estilo estilo) {
        super(texto);
        this.estilo = estilo;
        setFont(Tema.negrito(14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Botao(String texto) {
        this(texto, Estilo.PRIMARIO);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(getText()) + 36, 40);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.suavizar(g);
        boolean sobre = getModel().isRollover() && isEnabled();
        boolean pressionado = getModel().isPressed();
        Color fundo;
        Color texto;
        Color borda = null;
        switch (estilo) {
            case PRIMARIO -> {
                fundo = !isEnabled() ? Tema.BORDA_CAMPO : sobre || pressionado ? Tema.VERDE_HOVER : Tema.VERDE;
                texto = Color.WHITE;
            }
            case PERIGO -> {
                fundo = sobre ? Tema.ERRO_CLARO : Tema.BRANCO;
                texto = Tema.ERRO;
                borda = new Color(0xE8B4AF);
            }
            case TEXTO -> {
                fundo = sobre ? Tema.SUCESSO_CLARO : new Color(0, 0, 0, 0);
                texto = Tema.VERDE;
            }
            default -> {
                fundo = sobre ? Tema.SUCESSO_CLARO : Tema.BRANCO;
                texto = Tema.VERDE;
                borda = Tema.BORDA_CAMPO;
            }
        }
        int w = getWidth();
        int h = getHeight();
        g.setColor(fundo);
        g.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));
        if (borda != null) {
            g.setColor(borda);
            g.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 2, 10, 10));
        }
        if (hasFocus() && estilo != Estilo.PRIMARIO) {
            g.setColor(Tema.VERDE);
            g.draw(new RoundRectangle2D.Double(1.5, 1.5, w - 4, h - 4, 9, 9));
        }
        g.setFont(getFont());
        g.setColor(isEnabled() ? texto : Tema.TEXTO_SUAVE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(getText(), (w - fm.stringWidth(getText())) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
    }
}
