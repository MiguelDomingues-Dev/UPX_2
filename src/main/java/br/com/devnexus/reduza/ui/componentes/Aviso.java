package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Timer;

/** Mensagem curta de confirmação ou erro que some sozinha depois de alguns segundos. */
public class Aviso extends JLabel {

    private static final long serialVersionUID = 1L;
    private Color fundo = Tema.SUCESSO_CLARO;
    private final Timer ocultar = new Timer(5000, e -> setVisible(false));

    public Aviso() {
        setFont(Tema.negrito(13));
        setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        setOpaque(false);
        setVisible(false);
        ocultar.setRepeats(false);
    }

    public void sucesso(String texto) {
        mostrar(texto, Tema.SUCESSO_CLARO, Tema.VERDE);
    }

    public void erro(String texto) {
        mostrar(texto, Tema.ERRO_CLARO, Tema.ERRO);
    }

    private void mostrar(String texto, Color f, Color t) {
        fundo = f;
        setForeground(t);
        setText(texto);
        setVisible(true);
        ocultar.restart();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.suavizar(g);
        g.setColor(fundo);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
        g.dispose();
        super.paintComponent(g0);
    }
}
