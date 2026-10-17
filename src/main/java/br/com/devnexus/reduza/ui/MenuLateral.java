package br.com.devnexus.reduza.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Barra lateral verde-escura com a marca e os itens do menu. */
class MenuLateral extends JPanel {

    private static final long serialVersionUID = 1L;
    private final List<Item> itens = new ArrayList<>();
    private final Consumer<String> aoEscolher;

    MenuLateral(Consumer<String> aoEscolher, String rodape) {
        this.aoEscolher = aoEscolher;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Tema.VERDE_ESCURO);
        setPreferredSize(new Dimension(220, 600));
        setBorder(BorderFactory.createEmptyBorder(20, 14, 18, 14));
        add(new Marca());
        add(Box.createVerticalStrut(26));
        JLabel r = new JLabel(rodape);
        r.setFont(Tema.normal(11));
        r.setForeground(new Color(0x7FA096));
        r.setAlignmentX(LEFT_ALIGNMENT);
        add(Box.createVerticalGlue());
        add(r);
    }

    void adicionar(String chave, String texto) {
        Item i = new Item(chave, texto);
        itens.add(i);
        add(i, getComponentCount() - 2); // antes do espaço flexível e do rodapé
        add(Box.createVerticalStrut(4), getComponentCount() - 2);
    }

    void selecionar(String chave) {
        for (Item i : itens) {
            i.ativo = i.chave.equals(chave);
            i.repaint();
        }
    }

    private static final class Marca extends JComponent {
        private static final long serialVersionUID = 1L;

        Marca() {
            setPreferredSize(new Dimension(190, 40));
            setMaximumSize(new Dimension(190, 40));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Tema.suavizar(g);
            Tema.desenharLogo(g, 4, 4, 32);
            g.setColor(Color.WHITE);
            g.setFont(Tema.negrito(21));
            g.drawString("Reduza", 46, 28);
            g.dispose();
        }
    }

    private final class Item extends JComponent {
        private static final long serialVersionUID = 1L;
        private final String chave;
        private final String texto;
        private boolean ativo;
        private boolean sobre;

        Item(String chave, String texto) {
            this.chave = chave;
            this.texto = texto;
            setPreferredSize(new Dimension(190, 38));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setAlignmentX(LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusable(true);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    aoEscolher.accept(chave);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    sobre = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    sobre = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Tema.suavizar(g);
            if (ativo || sobre) {
                g.setColor(ativo ? Tema.VERDE_MENU_ATIVO : new Color(0x173D35));
                g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
            }
            if (ativo) {
                g.setColor(new Color(0x3CC29B));
                g.fill(new RoundRectangle2D.Double(0, 9, 4, getHeight() - 18, 4, 4));
            }
            g.setColor(ativo ? Color.WHITE : Tema.TEXTO_MENU);
            g.fill(new RoundRectangle2D.Double(16, getHeight() / 2.0 - 4, 8, 8, 3, 3));
            g.setFont(ativo ? Tema.negrito(14) : Tema.normal(14));
            g.drawString(texto, 36, getHeight() / 2 + 5);
            g.dispose();
        }
    }
}
