package br.com.devnexus.reduza.ui.telas;

import br.com.devnexus.reduza.dao.CategoriaDAO;
import br.com.devnexus.reduza.dao.PainelDAO;
import br.com.devnexus.reduza.dao.RegistroDAO;
import br.com.devnexus.reduza.dao.SetorDAO;
import br.com.devnexus.reduza.model.Categoria;
import br.com.devnexus.reduza.model.RegistroResiduo;
import br.com.devnexus.reduza.model.Setor;
import br.com.devnexus.reduza.model.Usuario;
import br.com.devnexus.reduza.servico.Sessao;
import br.com.devnexus.reduza.ui.Tela;
import br.com.devnexus.reduza.ui.componentes.Aviso;
import br.com.devnexus.reduza.ui.componentes.Botao;
import br.com.devnexus.reduza.ui.componentes.Campo;
import br.com.devnexus.reduza.ui.componentes.CartaoTitulado;
import br.com.devnexus.reduza.ui.componentes.Pagina;
import br.com.devnexus.reduza.ui.componentes.RenderizadorCategoria;
import br.com.devnexus.reduza.ui.componentes.Ui;
import br.com.devnexus.reduza.util.ErroValidacao;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Registro de uma movimentação de resíduo, em poucos segundos. */
public class TelaRegistro extends Tela {

    private static final long serialVersionUID = 1L;
    private static final BigDecimal LIMITE_ALERTA_KG = new BigDecimal("2000");

    private final RegistroDAO registros = new RegistroDAO();
    private final JComboBox<Setor> setor = Ui.combo();
    private final JComboBox<Categoria> categoria = Ui.combo();
    private final Campo peso = new Campo("Ex.: 12,5");
    private final Campo data = new Campo("dd/mm/aaaa");
    private final Campo observacao = new Campo("Opcional");
    private final Aviso aviso = new Aviso();
    private final DefaultTableModel modelo = Ui.modelo("Data", "Setor", "Categoria", "kg", "Registrado por");
    private final JTable tabela = new JTable(modelo);
    private List<RegistroResiduo> recentes = List.of();

    public TelaRegistro() {
        categoria.setRenderer(new RenderizadorCategoria(""));
        JPanel form = Ui.coluna();
        form.add(Ui.campoRotulado("Setor", setor));
        form.add(Ui.espaco(14));
        form.add(Ui.campoRotulado("Categoria do resíduo", categoria));
        form.add(Ui.espaco(14));
        form.add(Ui.campoRotulado("Quantidade (kg)", peso));
        form.add(Ui.espaco(14));
        form.add(Ui.campoRotulado("Data", data));
        form.add(Ui.espaco(14));
        form.add(Ui.campoRotulado("Observação", observacao));
        form.add(Ui.espaco(18));
        Botao salvar = new Botao("Salvar registro");
        salvar.addActionListener(e -> salvar());
        peso.addActionListener(e -> salvar());
        JPanel botoes = Ui.linha(12, salvar, aviso);
        botoes.setAlignmentX(LEFT_ALIGNMENT);
        form.add(botoes);

        JPanel lista = new JPanel(new BorderLayout(0, 10));
        lista.add(Ui.tabela(tabela, 3), BorderLayout.CENTER);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(70);
        Botao excluir = new Botao("Excluir selecionado", Botao.Estilo.PERIGO);
        excluir.addActionListener(e -> excluir());
        lista.add(Ui.linha(0, excluir), BorderLayout.SOUTH);
        lista.setPreferredSize(new Dimension(500, 470));

        Pagina p = new Pagina();
        p.linha(new double[]{1, 1.25}, new CartaoTitulado("Dados do resíduo", form),
                new CartaoTitulado("Últimos registros", lista));
        p.fim();
        add(p, BorderLayout.CENTER);
    }

    @Override
    public String titulo() {
        return "Registrar resíduo";
    }

    @Override
    public String subtitulo() {
        return "Pese o resíduo do coletor e registre aqui; os totais do mês são calculados sozinhos";
    }

    @Override
    public void atualizar() {
        Setor s = (Setor) setor.getSelectedItem();
        Categoria c = (Categoria) categoria.getSelectedItem();
        setor.removeAllItems();
        new SetorDAO().listar(true).forEach(setor::addItem);
        categoria.removeAllItems();
        new CategoriaDAO().listar(true).forEach(categoria::addItem);
        if (s != null) {
            selecionarPorId(setor, s.id());
        }
        if (c != null) {
            selecionarCategoria(c.id());
        }
        if (data.getText().isBlank()) {
            data.setText(Formatos.data(LocalDate.now()));
        }
        carregarRecentes();
    }

    private static void selecionarPorId(JComboBox<Setor> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id() == id) {
                combo.setSelectedIndex(i);
            }
        }
    }

    private void selecionarCategoria(int id) {
        for (int i = 0; i < categoria.getItemCount(); i++) {
            if (categoria.getItemAt(i).id() == id) {
                categoria.setSelectedIndex(i);
            }
        }
    }

    private void carregarRecentes() {
        recentes = registros.ultimos(40);
        modelo.setRowCount(0);
        for (RegistroResiduo r : recentes) {
            modelo.addRow(new Object[]{Formatos.data(r.data()), r.setor(), r.categoria(),
                    Formatos.decimal(r.pesoKg().doubleValue(), 1), r.usuario()});
        }
    }

    /** Preenche o formulário (usado nas capturas de tela para mostrar um exemplo). */
    public void exemplo(String kg) {
        peso.setText(kg);
    }

    private void salvar() {
        try {
            Setor s = (Setor) setor.getSelectedItem();
            Categoria c = (Categoria) categoria.getSelectedItem();
            if (s == null || c == null) {
                throw new ErroValidacao("Cadastre ao menos um setor e uma categoria antes de registrar.");
            }
            BigDecimal kg = Formatos.lerDecimal(peso.getText(), "a quantidade em kg");
            if (kg.signum() <= 0) {
                throw new ErroValidacao("A quantidade precisa ser maior que zero.");
            }
            LocalDate d = Formatos.lerData(data.getText(), "a data");
            if (d.isAfter(LocalDate.now())) {
                throw new ErroValidacao("A data não pode estar no futuro.");
            }
            if (kg.compareTo(LIMITE_ALERTA_KG) > 0 && !Ui.confirmar(this,
                    "São " + Formatos.kgComUnidade(kg.doubleValue()) + " em um só registro. Está correto?")) {
                return;
            }
            Usuario u = Sessao.usuario();
            registros.inserir(s.id(), c.id(), u.id(), d, kg, observacao.getText());
            YearMonth m = YearMonth.from(d);
            double totalMes = new PainelDAO().totalKg(m);
            aviso.sucesso("Registro salvo · Total de " + Formatos.mesLongo(m) + ": " + Formatos.kgComUnidade(totalMes));
            peso.setText("");
            observacao.setText("");
            peso.requestFocusInWindow();
            carregarRecentes();
        } catch (ErroValidacao ex) {
            aviso.erro(ex.getMessage());
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso.erro("Selecione um registro na lista.");
            return;
        }
        RegistroResiduo r = recentes.get(tabela.convertRowIndexToModel(linha));
        Usuario u = Sessao.usuario();
        if (!u.perfil().podeCadastrar() && r.usuarioId() != u.id()) {
            aviso.erro("Você só pode excluir registros feitos por você.");
            return;
        }
        if (Ui.confirmar(this, "Excluir o registro de " + Formatos.kgComUnidade(r.pesoKg().doubleValue()) + " ("
                + r.categoria() + ", " + r.setor() + ", " + Formatos.data(r.data()) + ")?")) {
            try {
                registros.excluir(r.id());
                aviso.sucesso("Registro excluído.");
                carregarRecentes();
            } catch (RuntimeException ex) {
                Ui.erro(this, ex.getMessage());
            }
        }
    }
}
