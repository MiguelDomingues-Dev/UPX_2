package br.com.devnexus.reduza.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * Base das telas do menu. Cada tela recarrega seus dados em atualizar().
 * Implementa Scrollable para acompanhar a largura da janela (só rola na vertical).
 */
public abstract class Tela extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    protected Tela() {
        super(new BorderLayout());
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(4, 28, 28, 28));
    }

    /** Título exibido no cabeçalho da janela. */
    public abstract String titulo();

    /** Linha de apoio abaixo do título. */
    public String subtitulo() {
        return "";
    }

    /** Chamado sempre que a tela é aberta. */
    public abstract void atualizar();

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle r, int orientacao, int direcao) {
        return 18;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle r, int orientacao, int direcao) {
        return r.height - 40;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getParent() != null && getParent().getHeight() > getPreferredSize().height;
    }
}
