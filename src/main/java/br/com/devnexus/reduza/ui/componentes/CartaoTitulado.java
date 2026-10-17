package br.com.devnexus.reduza.ui.componentes;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Cartão com título (e, opcionalmente, um componente à direita do título, como uma legenda). */
public class CartaoTitulado extends Cartao {

    private static final long serialVersionUID = 1L;
    private final JLabel titulo;

    public CartaoTitulado(String texto, JComponent conteudo) {
        this(texto, conteudo, null);
    }

    public CartaoTitulado(String texto, JComponent conteudo, Component direita) {
        super(new BorderLayout(0, 14));
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        titulo = Ui.titulo(texto, 16);
        topo.add(titulo, BorderLayout.WEST);
        if (direita != null) {
            topo.add(direita, BorderLayout.EAST);
        }
        add(topo, BorderLayout.NORTH);
        conteudo.setOpaque(false);
        add(conteudo, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
    }

    public void definirTitulo(String t) {
        titulo.setText(t);
    }
}
