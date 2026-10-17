package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/** Fábrica de componentes com o estilo do sistema. */
public final class Ui {

    private Ui() {
    }

    public static JLabel titulo(String texto, float tamanho) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.negrito(tamanho));
        l.setForeground(Tema.TEXTO);
        return l;
    }

    public static JLabel texto(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.normal(13));
        l.setForeground(Tema.TEXTO_SUAVE);
        return l;
    }

    public static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.negrito(13));
        l.setForeground(Tema.TEXTO);
        return l;
    }

    public static <T> JComboBox<T> combo() {
        JComboBox<T> c = new JComboBox<>();
        c.setFont(Tema.normal(14));
        c.setBackground(Tema.BRANCO);
        c.setForeground(Tema.TEXTO);
        c.setPreferredSize(new Dimension(200, 38));
        c.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel,
                                                          boolean foco) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, sel, foco);
                l.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                if (value == null) {
                    l.setText("Todos");
                }
                return l;
            }
        });
        return c;
    }

    public static JCheckBox caixa(String texto) {
        JCheckBox c = new JCheckBox(texto);
        c.setFont(Tema.normal(14));
        c.setForeground(Tema.TEXTO);
        c.setOpaque(false);
        return c;
    }

    /** Painel transparente em coluna; todos os filhos ficam alinhados à esquerda. */
    public static JPanel coluna() {
        JPanel p = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void addImpl(Component comp, Object restricao, int indice) {
                if (comp instanceof JComponent j) {
                    j.setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(comp, restricao, indice);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }

    /** Componentes lado a lado, alinhados à esquerda, com espaço só entre eles. */
    public static JPanel linha(int espaco, Component... itens) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void addImpl(Component comp, Object restricao, int indice) {
                if (getComponentCount() > 0 && espaco > 0 && !(comp instanceof Box.Filler)) {
                    super.addImpl(Box.createHorizontalStrut(espaco), null, -1);
                }
                super.addImpl(comp, restricao, indice);
            }
        };
        p.setOpaque(false);
        for (Component c : itens) {
            p.add(c);
        }
        return p;
    }

    public static Component espaco(int altura) {
        return Box.createVerticalStrut(altura);
    }

    /** Rótulo em cima, campo embaixo, ocupando a largura. */
    public static JPanel campoRotulado(String rotulo, JComponent campo) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(0, 0, 6, 0);
        p.add(rotulo(rotulo), g);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 0, 0);
        p.add(campo, g);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }

    /** Aparência das tabelas: linhas altas, cabeçalho limpo, números à direita. */
    public static JScrollPane tabela(JTable t, int... colunasNumericas) {
        t.setFont(Tema.normal(13));
        t.setForeground(Tema.TEXTO);
        t.setRowHeight(34);
        t.setShowVerticalLines(false);
        t.setGridColor(Tema.LINHA);
        t.setSelectionBackground(Tema.VERDE_CLARO);
        t.setSelectionForeground(Tema.TEXTO);
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);
        JTableHeader h = t.getTableHeader();
        h.setFont(Tema.negrito(12));
        h.setForeground(Tema.TEXTO_SUAVE);
        h.setBackground(Tema.BRANCO);
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(10, 36));
        DefaultTableCellRenderer cab = new DefaultTableCellRenderer();
        cab.setFont(Tema.negrito(12));
        DefaultTableCellRenderer padrao = new DefaultTableCellRenderer();
        padrao.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foco, int r,
                                                           int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, sel, false, r, c);
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                int modelo = tb.convertColumnIndexToModel(c);
                boolean numerica = false;
                for (int n : colunasNumericas) {
                    numerica |= n == modelo;
                }
                l.setHorizontalAlignment(numerica ? SwingConstants.RIGHT : SwingConstants.LEFT);
                if (!sel) {
                    l.setBackground(r % 2 == 0 ? Tema.BRANCO : new Color(0xF8FAF9));
                }
                return l;
            }
        });
        JScrollPane s = new JScrollPane(t);
        s.setBorder(BorderFactory.createLineBorder(Tema.LINHA));
        s.getViewport().setBackground(Tema.BRANCO);
        return s;
    }

    /** Modelo de tabela somente leitura. */
    public static javax.swing.table.DefaultTableModel modelo(String... colunas) {
        return new javax.swing.table.DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    public static javax.swing.JTextArea areaTexto(int linhas) {
        javax.swing.JTextArea a = new javax.swing.JTextArea(linhas, 20);
        a.setFont(Tema.normal(14));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Tema.BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return a;
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Reduza", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component pai, String mensagem) {
        return JOptionPane.showConfirmDialog(pai, mensagem, "Reduza", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }
}
