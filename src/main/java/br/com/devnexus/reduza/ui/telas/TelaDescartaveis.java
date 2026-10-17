package br.com.devnexus.reduza.ui.telas;

import br.com.devnexus.reduza.dao.ItemDAO;
import br.com.devnexus.reduza.dao.SetorDAO;
import br.com.devnexus.reduza.model.CustoItem;
import br.com.devnexus.reduza.model.Item;
import br.com.devnexus.reduza.model.Setor;
import br.com.devnexus.reduza.model.TipoItem;
import br.com.devnexus.reduza.model.ValorRotulado;
import br.com.devnexus.reduza.servico.CalculoCustos;
import br.com.devnexus.reduza.servico.Sessao;
import br.com.devnexus.reduza.ui.Tela;
import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.ui.componentes.Aviso;
import br.com.devnexus.reduza.ui.componentes.Botao;
import br.com.devnexus.reduza.ui.componentes.Campo;
import br.com.devnexus.reduza.ui.componentes.CartaoTitulado;
import br.com.devnexus.reduza.ui.componentes.Indicador;
import br.com.devnexus.reduza.ui.componentes.Pagina;
import br.com.devnexus.reduza.ui.componentes.Ui;
import br.com.devnexus.reduza.ui.graficos.GraficoColunas;
import br.com.devnexus.reduza.util.ErroValidacao;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Piloto de descartáveis: consumo, gasto, custo por uso (descartável × reutilizável),
 * simulador de troca por canecas e registro de compras e consumo.
 */
public class TelaDescartaveis extends Tela {

    private static final long serialVersionUID = 1L;

    private final ItemDAO itens = new ItemDAO();
    private final JComboBox<Item> item = Ui.combo();
    private final JComboBox<Setor> setor = Ui.combo();
    private final Indicador kpiConsumo = new Indicador("Consumo no mês");
    private final Indicador kpiPeso = new Indicador("Plástico gerado");
    private final Indicador kpiGasto = new Indicador("Gasto no mês");
    private final Indicador kpiUso = new Indicador("Custo por uso");
    private final GraficoColunas colunas = new GraficoColunas();
    private final JLabel comparacao = new JLabel();

    // simulador
    private final Campo simPessoas = new Campo("pessoas", "25");
    private final Campo simCopos = new Campo("copos/dia", "4");
    private final Campo simDias = new Campo("dias", "22");
    private final Campo simCaneca = new Campo("R$", "15,00");
    private final JLabel simResultado = new JLabel();

    // compra
    private final JComboBox<Item> compraItem = Ui.combo();
    private final Campo compraData = new Campo("dd/mm/aaaa");
    private final Campo compraPacotes = new Campo("Ex.: 20");
    private final Campo compraUnidades = new Campo("Ex.: 100");
    private final Campo compraValor = new Campo("Ex.: 6,50");
    private final Campo compraFornecedor = new Campo("Opcional");
    private final JLabel compraPrevia = Ui.texto(" ");
    private final Aviso compraAviso = new Aviso();

    // consumo
    private final JComboBox<Setor> consumoSetor = Ui.combo();
    private final JComboBox<Item> consumoItem = Ui.combo();
    private final Campo consumoData = new Campo("dd/mm/aaaa");
    private final Campo consumoQtd = new Campo("Ex.: 500");
    private final Aviso consumoAviso = new Aviso();

    private boolean carregando;
    private YearMonth mes = YearMonth.now();

    public TelaDescartaveis() {
        setor.setRenderer(new javax.swing.DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> l, Object v, int i,
                                                                   boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                setText(v == null ? "Todos os setores" : v.toString());
                setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return this;
            }
        });
        item.setPreferredSize(new Dimension(240, 38));
        setor.setPreferredSize(new Dimension(220, 38));
        item.addActionListener(e -> {
            if (!carregando) {
                carregarIndicadores();
            }
        });
        setor.addActionListener(e -> {
            if (!carregando) {
                carregarIndicadores();
            }
        });

        Pagina p = new Pagina();
        p.linha(Ui.linha(10, Ui.rotulo("Item"), item, Ui.rotulo("   Setor"), setor));
        p.linha(kpiConsumo, kpiPeso, kpiGasto, kpiUso);
        colunas.setPreferredSize(new Dimension(400, 230));
        comparacao.setFont(Tema.normal(14));
        comparacao.setForeground(Tema.TEXTO);
        comparacao.setVerticalAlignment(JLabel.TOP);
        p.linha(new double[]{1.3, 1}, new CartaoTitulado("Unidades consumidas por mês", colunas),
                new CartaoTitulado("Descartável × reutilizável", comparacao));
        p.linha(simulador());
        p.linha(formCompra(), formConsumo());
        p.fim();
        add(p, BorderLayout.CENTER);
    }

    @Override
    public String titulo() {
        return "Descartáveis";
    }

    @Override
    public String subtitulo() {
        return "Piloto de copos descartáveis · " + Formatos.mesAno(mes);
    }

    // ---------------------------------------------------------------- montagem

    private CartaoTitulado simulador() {
        JPanel campos = new JPanel(new GridLayout(1, 4, 12, 0));
        campos.setOpaque(false);
        campos.add(Ui.campoRotulado("Pessoas no setor", simPessoas));
        campos.add(Ui.campoRotulado("Copos por pessoa por dia", simCopos));
        campos.add(Ui.campoRotulado("Dias úteis no mês", simDias));
        campos.add(Ui.campoRotulado("Preço de uma caneca (R$)", simCaneca));
        DocumentListener d = ouvinte(this::simular);
        for (Campo c : new Campo[]{simPessoas, simCopos, simDias, simCaneca}) {
            c.getDocument().addDocumentListener(d);
        }
        simResultado.setFont(Tema.normal(14));
        simResultado.setForeground(Tema.TEXTO);
        JPanel col = new JPanel(new BorderLayout(0, 14));
        col.setOpaque(false);
        col.add(campos, BorderLayout.NORTH);
        col.add(simResultado, BorderLayout.CENTER);
        return new CartaoTitulado("Simulador: e se o setor trocar os copos por canecas?", col);
    }

    private CartaoTitulado formCompra() {
        JPanel f = Ui.coluna();
        f.add(Ui.campoRotulado("Item", compraItem));
        f.add(Ui.espaco(10));
        JPanel l1 = new JPanel(new GridLayout(1, 2, 12, 0));
        l1.setOpaque(false);
        l1.add(Ui.campoRotulado("Data da compra", compraData));
        l1.add(Ui.campoRotulado("Nº de pacotes", compraPacotes));
        f.add(l1);
        f.add(Ui.espaco(10));
        JPanel l2 = new JPanel(new GridLayout(1, 2, 12, 0));
        l2.setOpaque(false);
        l2.add(Ui.campoRotulado("Unidades por pacote", compraUnidades));
        l2.add(Ui.campoRotulado("Valor do pacote (R$)", compraValor));
        f.add(l2);
        f.add(Ui.espaco(10));
        f.add(Ui.campoRotulado("Fornecedor", compraFornecedor));
        f.add(Ui.espaco(8));
        compraPrevia.setAlignmentX(LEFT_ALIGNMENT);
        f.add(compraPrevia);
        f.add(Ui.espaco(10));
        Botao salvar = new Botao("Registrar compra");
        salvar.addActionListener(e -> salvarCompra());
        JPanel b = Ui.linha(12, salvar, compraAviso);
        b.setAlignmentX(LEFT_ALIGNMENT);
        f.add(b);
        DocumentListener d = ouvinte(this::previaCompra);
        compraPacotes.getDocument().addDocumentListener(d);
        compraUnidades.getDocument().addDocumentListener(d);
        compraValor.getDocument().addDocumentListener(d);
        return new CartaoTitulado("Registrar compra (como está na nota)", f);
    }

    private CartaoTitulado formConsumo() {
        consumoSetor.setPreferredSize(new Dimension(200, 38));
        JPanel f = Ui.coluna();
        f.add(Ui.campoRotulado("Setor", consumoSetor));
        f.add(Ui.espaco(10));
        f.add(Ui.campoRotulado("Item", consumoItem));
        f.add(Ui.espaco(10));
        JPanel l1 = new JPanel(new GridLayout(1, 2, 12, 0));
        l1.setOpaque(false);
        l1.add(Ui.campoRotulado("Data", consumoData));
        l1.add(Ui.campoRotulado("Quantidade (unidades)", consumoQtd));
        f.add(l1);
        f.add(Ui.espaco(8));
        JLabel dica = Ui.texto("<html><div style='width:330px'>Registre o que foi reposto ou retirado do estoque "
                + "do setor. Comparando antes e depois da troca por canecas, o sistema mostra o efeito.</div></html>");
        dica.setAlignmentX(LEFT_ALIGNMENT);
        f.add(dica);
        f.add(Ui.espaco(14));
        Botao salvar = new Botao("Registrar consumo");
        salvar.addActionListener(e -> salvarConsumo());
        JPanel b = Ui.linha(12, salvar, consumoAviso);
        b.setAlignmentX(LEFT_ALIGNMENT);
        f.add(b);
        return new CartaoTitulado("Registrar consumo", f);
    }

    private static DocumentListener ouvinte(Runnable r) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                r.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                r.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                r.run();
            }
        };
    }

    // ---------------------------------------------------------------- dados

    @Override
    public void atualizar() {
        carregando = true;
        try {
            Item i = (Item) item.getSelectedItem();
            List<Item> todos = itens.listar();
            item.removeAllItems();
            compraItem.removeAllItems();
            consumoItem.removeAllItems();
            for (Item it : todos) {
                if (it.tipo() == TipoItem.DESCARTAVEL) {
                    item.addItem(it);
                    consumoItem.addItem(it);
                }
                compraItem.addItem(it);
            }
            if (i != null) {
                for (int k = 0; k < item.getItemCount(); k++) {
                    if (item.getItemAt(k).id() == i.id()) {
                        item.setSelectedIndex(k);
                    }
                }
            }
            Setor s = (Setor) setor.getSelectedItem();
            List<Setor> setores = new SetorDAO().listar(true);
            setor.removeAllItems();
            setor.addItem(null);
            consumoSetor.removeAllItems();
            for (Setor st : setores) {
                setor.addItem(st);
                consumoSetor.addItem(st);
            }
            if (s != null) {
                for (int k = 1; k < setor.getItemCount(); k++) {
                    if (setor.getItemAt(k).id() == s.id()) {
                        setor.setSelectedIndex(k);
                    }
                }
            }
        } finally {
            carregando = false;
        }
        String hoje = Formatos.data(LocalDate.now());
        if (compraData.getText().isBlank()) {
            compraData.setText(hoje);
        }
        if (consumoData.getText().isBlank()) {
            consumoData.setText(hoje);
        }
        carregarIndicadores();
    }

    private void carregarIndicadores() {
        Item i = (Item) item.getSelectedItem();
        if (i == null) {
            kpiConsumo.definir("—", "cadastre um item descartável em Cadastros", null, null);
            return;
        }
        Setor s = (Setor) setor.getSelectedItem();
        Integer setorId = s == null ? null : s.id();
        // mostra o último mês fechado: um mês pela metade pareceria uma queda que ainda não aconteceu
        mes = itens.ultimoMesConsumo(i.id()).orElse(YearMonth.now());
        if (mes.equals(YearMonth.now()) && itens.consumoNoMes(i.id(), setorId, mes.minusMonths(1)) > 0) {
            mes = mes.minusMonths(1);
        }
        YearMonth ant = mes.minusMonths(1);
        boolean emAndamento = mes.equals(YearMonth.now());

        double qtd = itens.consumoNoMes(i.id(), setorId, mes);
        double qtdAnt = itens.consumoNoMes(i.id(), setorId, ant);
        if (qtdAnt > 0 && !emAndamento) {
            double var = (qtd - qtdAnt) / qtdAnt * 100;
            kpiConsumo.definir(Formatos.inteiro(qtd), (var <= 0 ? "↓ " : "↑ ") + Formatos.percentual(Math.abs(Math.round(var)))
                    + " vs. " + Formatos.mesLongo(ant), null, var <= 0 ? Tema.VERDE : Tema.ERRO);
        } else {
            kpiConsumo.definir(Formatos.inteiro(qtd), emAndamento ? "mês em andamento" : "unidades", null, null);
        }
        double kg = CalculoCustos.pesoKg(qtd, i.pesoUnitarioG().doubleValue());
        kpiPeso.definir(Formatos.kgComUnidade(kg), "≈ " + Formatos.kgComUnidade(kg * 12) + " por ano neste ritmo",
                null, null);

        Optional<CustoItem> custo = itens.custo(i.id());
        double unit = custo.map(c -> c.custoUnitario().doubleValue()).orElse(0.0);
        kpiGasto.definir(Formatos.moeda(qtd * unit), unit > 0 ? Formatos.moedaFina(unit) + " por unidade"
                : "registre uma compra para calcular", Tema.VERDE, null);

        // melhor reutilizável cadastrado, para comparar o custo por uso
        Optional<CustoItem> reutil = itens.listar().stream().filter(x -> x.tipo() == TipoItem.REUTILIZAVEL)
                .map(x -> itens.custo(x.id())).flatMap(Optional::stream)
                .min((a, b) -> a.custoPorUso().compareTo(b.custoPorUso()));
        kpiUso.definir(unit > 0 ? Formatos.moedaFina(unit) : "—",
                reutil.map(r -> "caneca: " + Formatos.moedaFina(r.custoPorUso().doubleValue())).orElse("cadastre um reutilizável"),
                null, reutil.isPresent() ? Tema.VERDE : null);

        List<ValorRotulado> serie = ValorRotulado.semZerosIniciais(itens.serieConsumo(i.id(), setorId, mes, 6));
        List<String> rot = new ArrayList<>();
        double[] val = new double[serie.size()];
        int destaque = -1;
        Optional<LocalDate> compraReutil = itens.listar().stream().filter(x -> x.tipo() == TipoItem.REUTILIZAVEL)
                .flatMap(x -> itens.compras(x.id()).stream()).map(c -> c.data()).min(LocalDate::compareTo);
        for (int k = 0; k < serie.size(); k++) {
            YearMonth m = YearMonth.parse(serie.get(k).rotulo());
            rot.add(Formatos.mesCurto(m));
            val[k] = serie.get(k).valor();
            if (destaque < 0 && compraReutil.isPresent() && m.isAfter(YearMonth.from(compraReutil.get()))) {
                destaque = k;
            }
        }
        colunas.definir(rot, val, destaque);

        StringBuilder sb = new StringBuilder("<html><div style='width:320px'>");
        if (unit > 0 && reutil.isPresent()) {
            CustoItem r = reutil.get();
            double porUso = r.custoPorUso().doubleValue();
            sb.append("Cada <b>uso</b> de um copo custa <b>").append(Formatos.moedaFina(unit))
                    .append("</b>. Uma ").append(r.item().toLowerCase()).append(" custa ")
                    .append(Formatos.moeda(r.custoUnitario().doubleValue())).append(", mas dura cerca de ")
                    .append(r.usosEstimados()).append(" usos: <b>").append(Formatos.moedaFina(porUso))
                    .append(" por uso</b>.<br><br>");
            if (porUso < unit) {
                sb.append("O reutilizável sai <b>").append(Formatos.percentual(Math.round((1 - porUso / unit) * 100)))
                        .append(" mais barato por uso</b>, e não vira resíduo a cada café.");
            }
            if (destaque >= 0) {
                sb.append("<br><br>Colunas em verde: meses depois da compra das canecas.");
            }
        } else {
            sb.append("Registre as compras do descartável e de um reutilizável para comparar o custo por uso.");
        }
        comparacao.setText(sb.append("</div></html>").toString());

        reutil.ifPresent(r -> {
            if (simCaneca.getText().isBlank()) {
                simCaneca.setText(Formatos.decimal(r.custoUnitario().doubleValue(), 2));
            }
        });
        simular();
        revalidate();
        repaint();
    }

    private void simular() {
        try {
            int pessoas = Formatos.lerInteiro(simPessoas.getText(), "x");
            double copos = Formatos.lerDecimal(simCopos.getText(), "x").doubleValue();
            int dias = Formatos.lerInteiro(simDias.getText(), "x");
            double caneca = Formatos.lerDecimal(simCaneca.getText(), "x").doubleValue();
            Item i = (Item) item.getSelectedItem();
            double unit = i == null ? 0 : itens.custo(i.id()).map(c -> c.custoUnitario().doubleValue()).orElse(0.0);
            double pesoG = i == null ? 1.8 : i.pesoUnitarioG().doubleValue();
            double porMes = CalculoCustos.coposPorMes(pessoas, copos, dias);
            double gastoMes = porMes * unit;
            double investimento = pessoas * caneca;
            double meses = CalculoCustos.mesesParaRetorno(investimento, gastoMes);
            String retorno = Double.isInfinite(meses) ? "registre uma compra de copos para calcular o retorno"
                    : "as canecas se pagam em <b>" + (meses < 1 ? "menos de 1 mês" : Formatos.decimal(meses, 1) + " meses")
                    + "</b>";
            simResultado.setText("<html>Hoje: <b>" + Formatos.inteiro(porMes) + " copos/mês</b> · "
                    + Formatos.moeda(gastoMes) + " por mês · " + Formatos.kgComUnidade(CalculoCustos.pesoKg(porMes * 12, pesoG))
                    + " de plástico por ano.<br>Com canecas: investimento de <b>" + Formatos.moeda(investimento)
                    + "</b>; " + retorno + ". Depois disso, a economia é de " + Formatos.moeda(gastoMes * 12)
                    + " por ano.</html>");
        } catch (ErroValidacao e) {
            simResultado.setText("Preencha os quatro campos com números.");
        }
    }

    private void previaCompra() {
        try {
            int pac = Formatos.lerInteiro(compraPacotes.getText(), "x");
            int un = Formatos.lerInteiro(compraUnidades.getText(), "x");
            double valor = Formatos.lerDecimal(compraValor.getText(), "x").doubleValue();
            compraPrevia.setText("Total: " + Formatos.inteiro(pac * (double) un) + " unidades · "
                    + Formatos.moeda(pac * valor) + " · custo unitário calculado: "
                    + Formatos.moedaFina(CalculoCustos.custoUnitario(valor, un)));
        } catch (ErroValidacao e) {
            compraPrevia.setText(" ");
        }
    }

    private void salvarCompra() {
        try {
            Item i = (Item) compraItem.getSelectedItem();
            if (i == null) {
                throw new ErroValidacao("Cadastre o item antes, em Cadastros.");
            }
            LocalDate d = Formatos.lerData(compraData.getText(), "a data");
            int pac = Formatos.lerInteiro(compraPacotes.getText(), "o número de pacotes");
            int un = Formatos.lerInteiro(compraUnidades.getText(), "as unidades por pacote");
            BigDecimal valor = Formatos.lerDecimal(compraValor.getText(), "o valor do pacote");
            if (pac <= 0 || un <= 0 || valor.signum() < 0) {
                throw new ErroValidacao("Pacotes e unidades devem ser maiores que zero.");
            }
            itens.registrarCompra(i.id(), Sessao.usuario().id(), d, pac, un, valor, compraFornecedor.getText());
            compraAviso.sucesso("Compra registrada.");
            compraPacotes.setText("");
            compraValor.setText("");
            carregarIndicadores();
        } catch (ErroValidacao ex) {
            compraAviso.erro(ex.getMessage());
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    private void salvarConsumo() {
        try {
            Setor s = (Setor) consumoSetor.getSelectedItem();
            Item i = (Item) consumoItem.getSelectedItem();
            if (s == null || i == null) {
                throw new ErroValidacao("Escolha o setor e o item.");
            }
            LocalDate d = Formatos.lerData(consumoData.getText(), "a data");
            int q = Formatos.lerInteiro(consumoQtd.getText(), "a quantidade");
            if (q <= 0) {
                throw new ErroValidacao("A quantidade deve ser maior que zero.");
            }
            itens.registrarConsumo(s.id(), i.id(), Sessao.usuario().id(), d, q);
            consumoAviso.sucesso("Consumo registrado.");
            consumoQtd.setText("");
            carregarIndicadores();
        } catch (ErroValidacao ex) {
            consumoAviso.erro(ex.getMessage());
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }
}
