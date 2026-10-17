package br.com.devnexus.reduza.ui.telas;

import br.com.devnexus.reduza.dao.CategoriaDAO;
import br.com.devnexus.reduza.dao.InspecaoDAO;
import br.com.devnexus.reduza.model.Categoria;
import br.com.devnexus.reduza.model.Inspecao;
import br.com.devnexus.reduza.model.PontoColeta;
import br.com.devnexus.reduza.model.QualidadeSetor;
import br.com.devnexus.reduza.model.ResultadoInspecao;
import br.com.devnexus.reduza.model.ValorRotulado;
import br.com.devnexus.reduza.servico.Sessao;
import br.com.devnexus.reduza.ui.Tela;
import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.ui.componentes.Aviso;
import br.com.devnexus.reduza.ui.componentes.Botao;
import br.com.devnexus.reduza.ui.componentes.Campo;
import br.com.devnexus.reduza.ui.componentes.CartaoTitulado;
import br.com.devnexus.reduza.ui.componentes.Pagina;
import br.com.devnexus.reduza.ui.componentes.RenderizadorCategoria;
import br.com.devnexus.reduza.ui.componentes.Ui;
import br.com.devnexus.reduza.ui.graficos.BarrasHorizontais;
import br.com.devnexus.reduza.util.ErroValidacao;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Inspeção amostral do descarte: alguém olha o coletor e marca se o conteúdo estava separado.
 * Mede o comportamento sem nenhum sensor.
 */
public class TelaInspecoes extends Tela {

    private static final long serialVersionUID = 1L;

    private final InspecaoDAO inspecoes = new InspecaoDAO();
    private final JComboBox<PontoColeta> ponto = Ui.combo();
    private final JComboBox<Categoria> categoria = Ui.combo();
    private final JRadioButton correto = radio("Correto — só o material certo");
    private final JRadioButton parcial = radio("Parcial — poucos itens fora do lugar");
    private final JRadioButton incorreto = radio("Incorreto — misturado");
    private final Campo data = new Campo("dd/mm/aaaa");
    private final Campo observacao = new Campo("Opcional");
    private final Aviso aviso = new Aviso();
    private final JComboBox<YearMonth> mes = Ui.combo();
    private final BarrasHorizontais taxa = new BarrasHorizontais(true);
    private final JLabel resumoMes = Ui.texto(" ");
    private final DefaultTableModel modelo = Ui.modelo("Data", "Ponto de coleta", "Coletor", "Resultado");
    private final JTable tabela = new JTable(modelo);
    private boolean carregando;

    public TelaInspecoes() {
        categoria.setRenderer(new RenderizadorCategoria(""));
        ButtonGroup g = new ButtonGroup();
        g.add(correto);
        g.add(parcial);
        g.add(incorreto);
        correto.setSelected(true);

        JPanel form = Ui.coluna();
        form.add(Ui.campoRotulado("Ponto de coleta", ponto));
        form.add(Ui.espaco(12));
        form.add(Ui.campoRotulado("Coletor inspecionado", categoria));
        form.add(Ui.espaco(12));
        JLabel r = Ui.rotulo("Resultado");
        r.setAlignmentX(LEFT_ALIGNMENT);
        form.add(r);
        form.add(Ui.espaco(4));
        for (JRadioButton b : new JRadioButton[]{correto, parcial, incorreto}) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            form.add(b);
        }
        form.add(Ui.espaco(10));
        JPanel l = new JPanel(new GridLayout(1, 2, 12, 0));
        l.setOpaque(false);
        l.add(Ui.campoRotulado("Data", data));
        l.add(Ui.campoRotulado("Observação", observacao));
        l.setAlignmentX(LEFT_ALIGNMENT);
        form.add(l);
        form.add(Ui.espaco(16));
        Botao salvar = new Botao("Salvar inspeção");
        salvar.addActionListener(e -> salvar());
        JPanel lb = Ui.linha(12, salvar, aviso);
        lb.setAlignmentX(LEFT_ALIGNMENT);
        form.add(lb);

        mes.setRenderer(new javax.swing.DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> li, Object v, int i,
                                                                   boolean s, boolean f) {
                super.getListCellRendererComponent(li, v, i, s, f);
                setText(v instanceof YearMonth m ? Formatos.mesAno(m) : "");
                setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return this;
            }
        });
        mes.setPreferredSize(new Dimension(200, 34));
        mes.addActionListener(e -> {
            if (!carregando) {
                carregarTaxa();
            }
        });
        JPanel qual = new JPanel(new BorderLayout(0, 12));
        qual.add(resumoMes, BorderLayout.NORTH);
        qual.add(taxa, BorderLayout.CENTER);

        JPanel lista = new JPanel(new BorderLayout());
        lista.add(Ui.tabela(tabela), BorderLayout.CENTER);
        lista.setPreferredSize(new Dimension(500, 300));

        Pagina p = new Pagina();
        p.linha(new double[]{1, 1}, new CartaoTitulado("Nova inspeção", form),
                new CartaoTitulado("Descarte correto por setor", qual, mes));
        p.linha(new CartaoTitulado("Últimas inspeções", lista));
        p.fim();
        add(p, BorderLayout.CENTER);
    }

    private static JRadioButton radio(String texto) {
        JRadioButton b = new JRadioButton(texto);
        b.setFont(Tema.normal(14));
        b.setForeground(Tema.TEXTO);
        b.setOpaque(false);
        return b;
    }

    @Override
    public String titulo() {
        return "Inspeções de descarte";
    }

    @Override
    public String subtitulo() {
        return "Confira os coletores e registre se o descarte está separado corretamente";
    }

    @Override
    public void atualizar() {
        ponto.removeAllItems();
        inspecoes.pontos(true).forEach(ponto::addItem);
        categoria.removeAllItems();
        new CategoriaDAO().listar(true).forEach(categoria::addItem);
        if (data.getText().isBlank()) {
            data.setText(Formatos.data(LocalDate.now()));
        }
        carregando = true;
        try {
            Object atual = mes.getSelectedItem();
            mes.removeAllItems();
            List<YearMonth> meses = inspecoes.mesesComInspecao();
            if (meses.isEmpty()) {
                meses = List.of(YearMonth.now());
            }
            meses.forEach(mes::addItem);
            // abre no último mês fechado, se houver
            int indice = meses.size() > 1 && meses.get(0).equals(YearMonth.now()) ? 1 : 0;
            mes.setSelectedItem(atual != null && meses.contains(atual) ? atual : meses.get(indice));
        } finally {
            carregando = false;
        }
        carregarTaxa();
        carregarLista();
    }

    private void carregarTaxa() {
        YearMonth m = (YearMonth) mes.getSelectedItem();
        if (m == null) {
            return;
        }
        List<QualidadeSetor> q = inspecoes.qualidadePorSetor(m);
        taxa.definir(q.stream().map(x -> new ValorRotulado(x.setor(), x.taxaCorreto())).toList());
        int total = q.stream().mapToInt(QualidadeSetor::inspecoes).sum();
        int ok = q.stream().mapToInt(QualidadeSetor::corretas).sum();
        resumoMes.setText(total == 0 ? "Nenhuma inspeção neste mês."
                : total + " inspeções · " + Formatos.percentual(Math.round(ok * 100.0 / total))
                + " com descarte correto na empresa");
    }

    private void carregarLista() {
        modelo.setRowCount(0);
        for (Inspecao i : inspecoes.recentes(30)) {
            modelo.addRow(new Object[]{Formatos.data(i.data()), i.ponto(), i.categoria(), i.resultado().toString()});
        }
    }

    private void salvar() {
        try {
            PontoColeta pc = (PontoColeta) ponto.getSelectedItem();
            Categoria c = (Categoria) categoria.getSelectedItem();
            if (pc == null) {
                throw new ErroValidacao("Cadastre um ponto de coleta em Cadastros.");
            }
            if (c == null) {
                throw new ErroValidacao("Escolha o coletor inspecionado.");
            }
            LocalDate d = Formatos.lerData(data.getText(), "a data");
            ResultadoInspecao r = correto.isSelected() ? ResultadoInspecao.CORRETO
                    : parcial.isSelected() ? ResultadoInspecao.PARCIAL : ResultadoInspecao.INCORRETO;
            inspecoes.registrar(pc.id(), c.id(), Sessao.usuario().id(), d, r, observacao.getText());
            aviso.sucesso("Inspeção salva.");
            observacao.setText("");
            carregarTaxa();
            carregarLista();
        } catch (ErroValidacao ex) {
            aviso.erro(ex.getMessage());
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }
}
