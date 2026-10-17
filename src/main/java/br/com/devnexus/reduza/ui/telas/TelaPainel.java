package br.com.devnexus.reduza.ui.telas;

import br.com.devnexus.reduza.dao.CategoriaDAO;
import br.com.devnexus.reduza.dao.MetaDAO;
import br.com.devnexus.reduza.dao.PainelDAO;
import br.com.devnexus.reduza.model.Categoria;
import br.com.devnexus.reduza.model.ProgressoMeta;
import br.com.devnexus.reduza.model.ValorRotulado;
import br.com.devnexus.reduza.ui.JanelaPrincipal;
import br.com.devnexus.reduza.ui.Tela;
import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.ui.componentes.CartaoTitulado;
import br.com.devnexus.reduza.ui.componentes.Indicador;
import br.com.devnexus.reduza.ui.componentes.Legenda;
import br.com.devnexus.reduza.ui.componentes.Pagina;
import br.com.devnexus.reduza.ui.componentes.Ui;
import br.com.devnexus.reduza.ui.graficos.BarrasHorizontais;
import br.com.devnexus.reduza.ui.graficos.GraficoBarrasAgrupadas;
import br.com.devnexus.reduza.ui.graficos.GraficoLinha;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Painel do mês: indicadores, comparação por categoria, evolução e ranking de setores. */
public class TelaPainel extends Tela {

    private static final long serialVersionUID = 1L;

    private final PainelDAO painel = new PainelDAO();
    private final CategoriaDAO categorias = new CategoriaDAO();
    private final MetaDAO metas = new MetaDAO();

    private final JComboBox<YearMonth> seletorMes = Ui.combo();
    private final Indicador kpiTotal = new Indicador("Resíduos no mês");
    private final Indicador kpiCusto = new Indicador("Custo de destinação");
    private final Indicador kpiDescarte = new Indicador("Descarte correto");
    private final Indicador kpiMetas = new Indicador("Metas ativas");
    private final GraficoBarrasAgrupadas barras = new GraficoBarrasAgrupadas();
    private final Legenda legenda = new Legenda("mês anterior", new Color(0xC4CDC9), "mês", Tema.TEXTO);
    private final GraficoLinha linha = new GraficoLinha();
    private final BarrasHorizontais setores = new BarrasHorizontais();
    private final JLabel leitura = new JLabel();
    private final CartaoTitulado cartaoSetores;
    private YearMonth mes = YearMonth.now();
    private boolean carregando;

    public TelaPainel() {
        Pagina p = new Pagina();
        JPanel filtro = Ui.linha(10, Ui.rotulo("Mês de referência"), seletorMes);
        seletorMes.setPreferredSize(new Dimension(220, 38));
        seletorMes.setRenderer(new javax.swing.DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> l, Object v, int i,
                                                                   boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                setText(v instanceof YearMonth m ? Formatos.mesAno(m) : "");
                setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return this;
            }
        });
        seletorMes.addActionListener(e -> {
            if (!carregando && seletorMes.getSelectedItem() instanceof YearMonth m) {
                mes = m;
                carregar();
            }
        });
        p.linha(filtro);
        p.linha(kpiTotal, kpiCusto, kpiDescarte, kpiMetas);
        barras.setPreferredSize(new Dimension(500, 250));
        linha.setPreferredSize(new Dimension(300, 250));
        p.linha(new double[]{1.65, 1}, new CartaoTitulado("Resíduos por categoria (kg)", barras, legenda),
                new CartaoTitulado("Total por mês (kg)", linha));
        leitura.setFont(Tema.normal(14));
        leitura.setForeground(Tema.TEXTO);
        leitura.setVerticalAlignment(JLabel.TOP);
        cartaoSetores = new CartaoTitulado("Setores no mês", setores);
        p.linha(new double[]{1.65, 1}, cartaoSetores, new CartaoTitulado("Leitura rápida", leitura));
        p.fim();
        add(p, BorderLayout.CENTER);
    }

    @Override
    public String titulo() {
        return "Painel do mês";
    }

    @Override
    public String subtitulo() {
        LocalDate hoje = LocalDate.now();
        if (mes.equals(YearMonth.from(hoje))) {
            return Formatos.mesAno(mes) + " · mês em andamento · todos os setores";
        }
        return Formatos.mesAno(mes) + " · todos os setores";
    }

    @Override
    public void atualizar() {
        carregando = true;
        try {
            List<YearMonth> meses = painel.mesesComDados();
            YearMonth escolhido = (YearMonth) seletorMes.getSelectedItem();
            seletorMes.removeAllItems();
            if (meses.isEmpty()) {
                meses = List.of(YearMonth.now());
            }
            meses.forEach(seletorMes::addItem);
            mes = escolhido != null && meses.contains(escolhido) ? escolhido : meses.get(0);
            seletorMes.setSelectedItem(mes);
        } finally {
            carregando = false;
        }
        carregar();
    }

    /** Permite abrir o painel já num mês específico (usado nas capturas de tela). */
    public void mostrarMes(YearMonth m) {
        seletorMes.setSelectedItem(m);
    }

    private void carregar() {
        YearMonth anterior = mes.minusMonths(1);
        LocalDate hoje = LocalDate.now();
        boolean emAndamento = mes.equals(YearMonth.from(hoje));

        // ---- indicadores
        double total = painel.totalKg(mes);
        double base;
        String comparacao;
        if (emAndamento) {
            // compara com o mesmo número de dias do mês anterior, para não parecer queda
            int dia = hoje.getDayOfMonth();
            base = painel.totalKgAteDia(anterior, dia);
            comparacao = " vs. 1–" + dia + " de " + Formatos.mesLongo(anterior);
        } else {
            base = painel.totalKg(anterior);
            comparacao = " vs. " + Formatos.mesLongo(anterior);
        }
        if (base > 0) {
            double var = (total - base) / base * 100;
            boolean caiu = var <= 0;
            kpiTotal.definir(Formatos.kgComUnidade(total), (caiu ? "↓ " : "↑ ") + Formatos.percentual(Math.abs(var))
                    + comparacao, null, caiu ? Tema.VERDE : Tema.ERRO);
        } else {
            kpiTotal.definir(Formatos.kgComUnidade(total), "sem dados do mês anterior", null, null);
        }

        double custo = painel.custoDestinacao(mes);
        double custoAnt = painel.custoDestinacao(anterior);
        String detCusto = custo == 0 ? "cadastre o custo por kg nas categorias"
                : emAndamento ? "acumulado até hoje"
                : custoAnt > 0 ? (custo <= custoAnt ? "economia de " + Formatos.moeda(custoAnt - custo)
                : "aumento de " + Formatos.moeda(custo - custoAnt)) + " vs. " + Formatos.mesLongo(anterior)
                : "estimado pelo custo por kg";
        kpiCusto.definir(Formatos.moeda(custo), detCusto, Tema.VERDE,
                custo > 0 && !emAndamento && custoAnt > custo ? Tema.VERDE : null);

        double taxa = painel.taxaDescarteCorreto(mes);
        if (taxa < 0) {
            kpiDescarte.definir("—", "nenhuma inspeção no mês", null, null);
        } else {
            double taxaAnt = painel.taxaDescarteCorreto(anterior);
            String det = taxaAnt < 0 ? "das inspeções do mês"
                    : (taxa >= taxaAnt ? "↑ " : "↓ ") + Formatos.decimal(Math.abs(taxa - taxaAnt), 0)
                    + " pontos vs. " + Formatos.mesLongo(anterior);
            kpiDescarte.definir(Formatos.percentual(Math.round(taxa)), det, null,
                    taxaAnt < 0 ? null : taxa >= taxaAnt ? Tema.VERDE : Tema.ERRO);
        }

        List<ProgressoMeta> ms = metas.progresso();
        long atingidas = ms.stream().filter(m -> m.progressoPercentual() != null
                && m.progressoPercentual().doubleValue() >= 100).count();
        double media = ms.stream().filter(m -> m.progressoPercentual() != null)
                .mapToDouble(m -> Math.min(100, m.progressoPercentual().doubleValue())).average().orElse(-1);
        kpiMetas.definir(String.valueOf(ms.size()), ms.isEmpty() ? "cadastre metas na tela Metas"
                : media < 0 ? "aguardando o primeiro mês fechado"
                : atingidas + " atingida(s) · progresso médio de " + Formatos.percentual(Math.round(media)), null, null);

        // ---- categorias: mês anterior × mês
        Map<String, Double> atual = painel.porCategoria(mes);
        Map<String, Double> ant = painel.porCategoria(anterior);
        List<String> nomes = new ArrayList<>();
        List<Color> cores = new ArrayList<>();
        int i = 0;
        for (Categoria c : categorias.listar(true)) {
            nomes.add(c.nome().equals("Resíduos perigosos") ? "Perigosos" : c.nome());
            cores.add(Tema.corColetor(c.corColetor(), i++));
        }
        double[] a = new double[nomes.size()];
        double[] b = new double[nomes.size()];
        int k = 0;
        for (Categoria c : categorias.listar(true)) {
            a[k] = ant.getOrDefault(c.nome(), 0.0);
            b[k] = atual.getOrDefault(c.nome(), 0.0);
            k++;
        }
        barras.definir(nomes, cores, a, b);
        legenda.definir(Formatos.mesLongo(anterior), Formatos.mesLongo(mes) + (emAndamento ? " (parcial)" : ""));

        // ---- evolução de 6 meses
        List<ValorRotulado> serie = ValorRotulado.semZerosIniciais(painel.serieMensal(mes, 6));
        List<String> rot = new ArrayList<>();
        double[] val = new double[serie.size()];
        for (int j = 0; j < serie.size(); j++) {
            rot.add(Formatos.mesCurto(YearMonth.parse(serie.get(j).rotulo())));
            val[j] = serie.get(j).valor();
        }
        int marco = -1;
        LocalDate inicioMetas = ms.stream().map(ProgressoMeta::inicio).min(LocalDate::compareTo).orElse(null);
        if (inicioMetas != null) {
            for (int j = 0; j < serie.size(); j++) {
                if (YearMonth.parse(serie.get(j).rotulo()).equals(YearMonth.from(inicioMetas))) {
                    marco = j;
                }
            }
        }
        linha.definir(rot, val, marco, "início das metas", "kg");
        linha.marcarUltimoParcial(emAndamento);

        // ---- setores
        List<ValorRotulado> porSetor = painel.porSetor(mes);
        setores.definir(porSetor);

        leitura.setText(leituraRapida(nomes, a, b, porSetor, total, base, emAndamento, anterior));
        if (SwingUtilities.getWindowAncestor(this) instanceof JanelaPrincipal j) {
            j.atualizarCabecalho();
        }
        revalidate();
        repaint();
    }

    /** Frases curtas que destacam o que mudou: ajudam quem não lê gráfico. */
    private String leituraRapida(List<String> nomes, double[] ant, double[] atu, List<ValorRotulado> setoresMes,
                                 double total, double base, boolean emAndamento, YearMonth anterior) {
        if (total <= 0) {
            return "<html>Ainda não há registros neste mês.<br>Use <b>Registrar resíduo</b> para começar.</html>";
        }
        StringBuilder sb = new StringBuilder("<html><div style='width:280px'>");
        if (!emAndamento) {
            int maior = -1;
            double maiorQueda = 0;
            for (int i = 0; i < nomes.size(); i++) {
                if (ant[i] > 0) {
                    double v = (atu[i] - ant[i]) / ant[i] * 100;
                    if (v < maiorQueda) {
                        maiorQueda = v;
                        maior = i;
                    }
                }
            }
            if (maior >= 0) {
                sb.append("• <b>").append(nomes.get(maior)).append("</b> teve a maior queda: ")
                        .append(Formatos.percentual(Math.abs(Math.round(maiorQueda)))).append(" a menos que em ")
                        .append(Formatos.mesLongo(anterior)).append(".<br><br>");
            }
        } else {
            sb.append("• Mês em andamento: os números ainda vão crescer até o fim do mês.<br><br>");
        }
        if (!setoresMes.isEmpty()) {
            ValorRotulado topo = setoresMes.get(0);
            sb.append("• <b>").append(topo.rotulo()).append("</b> gera ")
                    .append(Formatos.percentual(Math.round(topo.valor() / total * 100)))
                    .append(" do resíduo do mês: é onde uma ação tem mais efeito.<br><br>");
        }
        if (base > 0 && !emAndamento) {
            double dif = base - total;
            sb.append(dif >= 0 ? "• A empresa gerou <b>" + Formatos.kgComUnidade(dif) + " a menos</b>"
                    : "• A empresa gerou <b>" + Formatos.kgComUnidade(-dif) + " a mais</b>")
                    .append(" que no mês anterior.");
        }
        return sb.append("</div></html>").toString();
    }
}
