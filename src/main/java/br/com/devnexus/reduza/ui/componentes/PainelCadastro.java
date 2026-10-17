package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.util.ErroValidacao;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Cadastro genérico: lista à esquerda, formulário à direita.
 * Clicar numa linha carrega o item para edição; "Novo" limpa o formulário.
 */
public abstract class PainelCadastro<T> extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final Aviso aviso = new Aviso();
    private final JLabel tituloForm = Ui.titulo("Novo", 16);
    private final Botao alternar = new Botao("Desativar", Botao.Estilo.PERIGO);
    private List<T> itens = List.of();
    private T selecionado;

    protected PainelCadastro(String... colunas) {
        super(new GridBagLayout());
        setOpaque(false);
        modelo = Ui.modelo(colunas);
        tabela = new JTable(modelo);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                selecionado = itens.get(tabela.convertRowIndexToModel(tabela.getSelectedRow()));
                tituloForm.setText("Editar");
                preencher(selecionado);
                alternar.setVisible(temAtivo());
                alternar.setText(ativo(selecionado) ? "Desativar" : "Reativar");
            }
        });
    }

    /** Monta a tela. Chamar no fim do construtor da subclasse, depois de criar os campos. */
    protected void montar() {
        Cartao lista = new Cartao(new BorderLayout());
        lista.add(Ui.tabela(tabela, numericas()), BorderLayout.CENTER);
        lista.setPreferredSize(new Dimension(560, 460));

        Cartao lado = new Cartao(new BorderLayout(0, 14));
        lado.add(tituloForm, BorderLayout.NORTH);
        JPanel form = formulario();
        form.setOpaque(false);
        lado.add(form, BorderLayout.CENTER);
        Botao novo = new Botao("Novo", Botao.Estilo.SECUNDARIO);
        novo.addActionListener(e -> limpar());
        Botao salvar = new Botao("Salvar");
        salvar.addActionListener(e -> salvar());
        alternar.addActionListener(e -> alternarAtivo());
        alternar.setVisible(false);
        JPanel botoes = Ui.coluna();
        botoes.add(Ui.linha(10, salvar, novo, alternar));
        botoes.add(Ui.espaco(10));
        botoes.add(Ui.linha(0, aviso));
        lado.add(botoes, BorderLayout.SOUTH);
        lado.setPreferredSize(new Dimension(380, 460));
        lado.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.weighty = 1;
        g.weightx = 1.5;
        add(lista, g);
        g.gridx = 1;
        g.weightx = 1;
        g.insets = new Insets(0, 16, 0, 0);
        add(lado, g);
    }

    protected int[] numericas() {
        return new int[0];
    }

    protected abstract List<T> carregar();

    protected abstract Object[] linha(T item);

    protected abstract JPanel formulario();

    /** Preenche o formulário; item nulo = limpar. */
    protected abstract void preencher(T item);

    /** Salva; selecionado nulo = inserir. Lança ErroValidacao com mensagem amigável. */
    protected abstract void gravar(T selecionado);

    protected boolean temAtivo() {
        return true;
    }

    protected boolean ativo(T item) {
        return true;
    }

    protected void definirAtivo(T item, boolean ativo) {
    }

    public void atualizar() {
        itens = carregar();
        modelo.setRowCount(0);
        for (T t : itens) {
            modelo.addRow(linha(t));
        }
        limpar();
    }

    private void limpar() {
        selecionado = null;
        tabela.clearSelection();
        tituloForm.setText("Novo");
        alternar.setVisible(false);
        preencher(null);
    }

    private void salvar() {
        try {
            gravar(selecionado);
            aviso.sucesso("Salvo.");
            atualizar();
        } catch (ErroValidacao ex) {
            aviso.erro(ex.getMessage());
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void alternarAtivo() {
        if (selecionado == null) {
            return;
        }
        try {
            definirAtivo(selecionado, !ativo(selecionado));
            aviso.sucesso(ativo(selecionado) ? "Desativado." : "Reativado.");
            atualizar();
        } catch (ErroValidacao ex) {
            aviso.erro(ex.getMessage());
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    protected static JPanel formularioColuna(javax.swing.JComponent... campos) {
        JPanel p = Ui.coluna();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                p.add(Ui.espaco(12));
            }
            campos[i].setAlignmentX(LEFT_ALIGNMENT);
            p.add(campos[i]);
        }
        JPanel base = new JPanel(new BorderLayout());
        base.add(p, BorderLayout.NORTH);
        return base;
    }

    protected static String sim(boolean b) {
        return b ? "Sim" : "Não";
    }

    protected static JLabel status(boolean ativo) {
        JLabel l = new JLabel(ativo ? "Ativo" : "Inativo");
        l.setForeground(ativo ? Tema.VERDE : Tema.TEXTO_SUAVE);
        return l;
    }
}
