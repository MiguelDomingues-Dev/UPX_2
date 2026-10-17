package br.com.devnexus.reduza.ui.componentes;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** Organiza a tela em linhas de cartões lado a lado, com pesos de largura. */
public class Pagina extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int ESPACO = 16;
    private int linha;

    public Pagina() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    /** Adiciona uma linha; pesos define a proporção de largura de cada componente. */
    public void linha(double[] pesos, JComponent... itens) {
        JPanel l = new JPanel(new GridBagLayout());
        l.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.fill = GridBagConstraints.BOTH;
        g.weighty = 1;
        for (int i = 0; i < itens.length; i++) {
            g.gridx = i;
            g.weightx = pesos == null ? 1 : pesos[i];
            g.insets = new Insets(0, i == 0 ? 0 : ESPACO, 0, 0);
            l.add(itens[i], g);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = linha++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(0, 0, ESPACO, 0);
        add(l, c);
    }

    public void linha(JComponent... itens) {
        linha(null, itens);
    }

    /** Empurra o conteúdo para cima. Chamar depois da última linha. */
    public void fim() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = linha++;
        c.weighty = 1;
        c.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), c);
    }

    public void limpar() {
        removeAll();
        linha = 0;
    }
}
