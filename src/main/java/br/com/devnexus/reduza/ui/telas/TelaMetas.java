package br.com.devnexus.reduza.ui.telas;

import br.com.devnexus.reduza.dao.CategoriaDAO;
import br.com.devnexus.reduza.dao.MetaDAO;
import br.com.devnexus.reduza.dao.SetorDAO;
import br.com.devnexus.reduza.model.Categoria;
import br.com.devnexus.reduza.model.Meta;
import br.com.devnexus.reduza.model.ProgressoMeta;
import br.com.devnexus.reduza.model.Setor;
import br.com.devnexus.reduza.servico.RegrasMeta;
import br.com.devnexus.reduza.servico.Sessao;
import br.com.devnexus.reduza.ui.Tela;
import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.ui.componentes.Aviso;
import br.com.devnexus.reduza.ui.componentes.Botao;
import br.com.devnexus.reduza.ui.componentes.Campo;
import br.com.devnexus.reduza.ui.componentes.Cartao;
import br.com.devnexus.reduza.ui.componentes.CartaoTitulado;
import br.com.devnexus.reduza.ui.componentes.Pagina;
import br.com.devnexus.reduza.ui.componentes.RenderizadorCategoria;
import br.com.devnexus.reduza.ui.componentes.Ui;
import br.com.devnexus.reduza.ui.graficos.BarraProgresso;
import br.com.devnexus.reduza.util.ErroValidacao;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Metas de redução por setor e categoria, com o progresso calculado pela regra única. */
public class TelaMetas extends Tela {

    private static final long serialVersionUID = 1L;

    private final MetaDAO metas = new MetaDAO();
    private final JPanel cartoes = new JPanel(new GridLayout(0, 2, 16, 16));
    private final Campo descricao = new Campo("Ex.: Reduzir plástico da Produção");
    private final JComboBox<Setor> setor = Ui.combo();
    private final JComboBox<Categoria> categoria = Ui.combo();
    private final Campo inicio = new Campo("dd/mm/aaaa");
    private final Campo fim = new Campo("dd/mm/aaaa");
    private final Campo base = new Campo("kg por mês");
    private final Campo reducao = new Campo("Ex.: 15");
    private final JLabel previa = Ui.texto(" ");
    private final Aviso aviso = new Aviso();
    private final CartaoTitulado cartaoNova;

    public TelaMetas() {
        cartoes.setOpaque(false);
        categoria.setRenderer(new RenderizadorCategoria("Todas as categorias"));
        setor.setRenderer(new javax.swing.DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> l, Object v, int i,
                                                                   boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                setText(v == null ? "Empresa toda" : v.toString());
                setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return this;
            }
        });

        JPanel form = Ui.coluna();
        form.add(Ui.campoRotulado("Descrição", descricao));
        form.add(Ui.espaco(12));
        JPanel l1 = new JPanel(new GridLayout(1, 2, 12, 0));
        l1.setOpaque(false);
        l1.add(Ui.campoRotulado("Setor", setor));
        l1.add(Ui.campoRotulado("Categoria", categoria));
        form.add(l1);
        form.add(Ui.espaco(12));
        JPanel l2 = new JPanel(new GridLayout(1, 2, 12, 0));
        l2.setOpaque(false);
        l2.add(Ui.campoRotulado("Início", inicio));
        l2.add(Ui.campoRotulado("Fim", fim));
        form.add(l2);
        form.add(Ui.espaco(12));
        JPanel l3 = new JPanel(new GridLayout(1, 2, 12, 0));
        l3.setOpaque(false);
        l3.add(Ui.campoRotulado("Linha de base (kg/mês)", base));
        l3.add(Ui.campoRotulado("Redução desejada (%)", reducao));
        form.add(l3);
        form.add(Ui.espaco(8));
        Botao sugerir = new Botao("Usar a média dos 3 meses anteriores", Botao.Estilo.TEXTO);
        sugerir.addActionListener(e -> sugerirBase());
        JPanel ls = Ui.linha(0, sugerir);
        ls.setAlignmentX(LEFT_ALIGNMENT);
        form.add(ls);
        form.add(Ui.espaco(6));
        previa.setAlignmentX(LEFT_ALIGNMENT);
        form.add(previa);
        form.add(Ui.espaco(14));
        Botao salvar = new Botao("Criar meta");
        salvar.addActionListener(e -> salvar());
        JPanel lb = Ui.linha(12, salvar, aviso);
        lb.setAlignmentX(LEFT_ALIGNMENT);
        form.add(lb);
        DocumentListener ouvinte = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarPrevia();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarPrevia();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarPrevia();
            }
        };
        base.getDocument().addDocumentListener(ouvinte);
        reducao.getDocument().addDocumentListener(ouvinte);
        cartaoNova = new CartaoTitulado("Nova meta", form);

        JLabel regra = new JLabel("<html><div style='width:300px'>"
                + "<b>Alvo</b> = linha de base × (1 − redução %).<br><br>"
                + "<b>Progresso</b> = quanto da redução planejada já foi alcançada, usando a média dos meses "
                + "fechados dentro do período.<br><br>"
                + "0% = nada reduzido · 100% = meta atingida · acima de 100% = superada · "
                + "negativo = o resíduo aumentou.<br><br>"
                + "O mês em andamento só entra na conta quando termina, para não dar a falsa impressão de queda."
                + "</div></html>");
        regra.setFont(Tema.normal(13));
        regra.setVerticalAlignment(JLabel.TOP);
        regra.setForeground(Tema.TEXTO_SUAVE);

        Pagina p = new Pagina();
        p.linha(cartoes);
        p.linha(new double[]{1.4, 1}, cartaoNova, new CartaoTitulado("Como o progresso é calculado", regra));
        p.fim();
        add(p, BorderLayout.CENTER);
    }

    @Override
    public String titulo() {
        return "Metas";
    }

    @Override
    public String subtitulo() {
        return "Metas de redução por setor e categoria";
    }

    @Override
    public void atualizar() {
        cartaoNova.setVisible(Sessao.usuario().perfil().podeCadastrar());
        setor.removeAllItems();
        setor.addItem(null);
        new SetorDAO().listar(true).forEach(setor::addItem);
        categoria.removeAllItems();
        categoria.addItem(null);
        new CategoriaDAO().listar(true).forEach(categoria::addItem);
        if (inicio.getText().isBlank()) {
            LocalDate prox = LocalDate.now().withDayOfMonth(1).plusMonths(1);
            inicio.setText(Formatos.data(prox));
            fim.setText(Formatos.data(prox.plusMonths(3).minusDays(1)));
        }
        carregarCartoes();
    }

    private void carregarCartoes() {
        cartoes.removeAll();
        List<ProgressoMeta> lista = metas.progresso();
        if (lista.isEmpty()) {
            Cartao vazio = new Cartao();
            vazio.add(Ui.texto("Nenhuma meta ativa. Crie a primeira no formulário abaixo."));
            cartoes.add(vazio);
        }
        for (ProgressoMeta m : lista) {
            cartoes.add(new CartaoMeta(m));
        }
        cartoes.revalidate();
        cartoes.repaint();
    }

    private void atualizarPrevia() {
        try {
            double b = Formatos.lerDecimal(base.getText(), "x").doubleValue();
            double r = Formatos.lerDecimal(reducao.getText(), "x").doubleValue();
            previa.setText("Alvo: " + Formatos.kgComUnidade(RegrasMeta.alvo(b, r)) + " por mês ("
                    + Formatos.kgComUnidade(b - RegrasMeta.alvo(b, r)) + " a menos que a linha de base)");
        } catch (ErroValidacao e) {
            previa.setText(" ");
        }
    }

    private void sugerirBase() {
        try {
            LocalDate ini = Formatos.lerData(inicio.getText(), "a data de início");
            Setor s = (Setor) setor.getSelectedItem();
            Categoria c = (Categoria) categoria.getSelectedItem();
            double media = metas.mediaMensalAntes(s == null ? null : s.id(), c == null ? null : c.id(), ini, 3);
            if (media <= 0) {
                aviso.erro("Não há registros nos 3 meses antes do início.");
                return;
            }
            base.setText(Formatos.decimal(media, 2).replace(".", ""));
        } catch (ErroValidacao ex) {
            aviso.erro(ex.getMessage());
        }
    }

    private void salvar() {
        try {
            if (descricao.getText().isBlank()) {
                throw new ErroValidacao("Dê um nome para a meta.");
            }
            LocalDate ini = Formatos.lerData(inicio.getText(), "a data de início");
            LocalDate f = Formatos.lerData(fim.getText(), "a data de fim");
            if (!f.isAfter(ini)) {
                throw new ErroValidacao("O fim precisa ser depois do início.");
            }
            BigDecimal b = Formatos.lerDecimal(base.getText(), "a linha de base");
            BigDecimal r = Formatos.lerDecimal(reducao.getText(), "a redução");
            if (b.signum() <= 0) {
                throw new ErroValidacao("A linha de base precisa ser maior que zero.");
            }
            if (r.signum() <= 0 || r.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new ErroValidacao("A redução deve ficar entre 1% e 100%.");
            }
            Setor s = (Setor) setor.getSelectedItem();
            Categoria c = (Categoria) categoria.getSelectedItem();
            metas.inserir(new Meta(0, s == null ? null : s.id(), c == null ? null : c.id(), descricao.getText(), ini,
                    f, b, r));
            aviso.sucesso("Meta criada.");
            descricao.setText("");
            base.setText("");
            reducao.setText("");
            carregarCartoes();
        } catch (ErroValidacao ex) {
            aviso.erro(ex.getMessage());
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    /** Cartão de uma meta: percentual grande, barra e os três números que explicam o cálculo. */
    private final class CartaoMeta extends Cartao {
        private static final long serialVersionUID = 1L;

        CartaoMeta(ProgressoMeta m) {
            super(new BorderLayout());
            Double prog = m.progressoPercentual() == null ? null : m.progressoPercentual().doubleValue();
            JPanel col = Ui.coluna();

            JPanel topo = new JPanel(new BorderLayout(12, 0));
            topo.setOpaque(false);
            JPanel nomes = Ui.coluna();
            nomes.add(Ui.titulo(m.descricao(), 16));
            nomes.add(Ui.espaco(3));
            String escopo = m.setor() + (m.categoria() == null ? "" : " · " + m.categoria());
            nomes.add(Ui.texto(escopo + " · reduzir " + Formatos.percentual(Formatos.d(m.reducaoPercentual()))));
            nomes.add(Ui.texto(Formatos.data(m.inicio()) + " a " + Formatos.data(m.fim())));
            topo.add(nomes, BorderLayout.CENTER);
            JLabel pct = new JLabel(prog == null ? "—" : Formatos.percentual(Math.round(prog)));
            pct.setFont(Tema.negrito(30));
            pct.setForeground(BarraProgresso.cor(prog));
            topo.add(pct, BorderLayout.EAST);
            col.add(topo);
            col.add(Ui.espaco(12));

            BarraProgresso barra = new BarraProgresso();
            barra.definir(prog);
            barra.setPreferredSize(new Dimension(200, 14));
            barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
            col.add(barra);
            col.add(Ui.espaco(12));

            String atual = m.mediaMensalKg() == null ? "sem mês fechado"
                    : Formatos.kgComUnidade(Formatos.d(m.mediaMensalKg()));
            col.add(Ui.texto("Linha de base " + Formatos.kgComUnidade(Formatos.d(m.linhaBaseKg()))
                    + "  ·  Média atual " + atual + "  ·  Alvo " + Formatos.kgComUnidade(Formatos.d(m.alvoKg()))));
            col.add(Ui.espaco(8));

            JPanel rodape = new JPanel(new BorderLayout());
            rodape.setOpaque(false);
            JLabel sit = Ui.rotulo(RegrasMeta.situacao(prog));
            sit.setForeground(BarraProgresso.cor(prog));
            rodape.add(sit, BorderLayout.WEST);
            if (Sessao.usuario().perfil().podeCadastrar()) {
                Botao encerrar = new Botao("Encerrar meta", Botao.Estilo.TEXTO);
                encerrar.setFont(Tema.negrito(12));
                encerrar.setPreferredSize(new Dimension(120, 28));
                encerrar.addActionListener(e -> {
                    if (Ui.confirmar(TelaMetas.this, "Encerrar a meta \"" + m.descricao() + "\"?")) {
                        metas.desativar(m.id());
                        carregarCartoes();
                    }
                });
                rodape.add(encerrar, BorderLayout.EAST);
            }
            col.add(rodape);
            add(col, BorderLayout.CENTER);
        }
    }
}
