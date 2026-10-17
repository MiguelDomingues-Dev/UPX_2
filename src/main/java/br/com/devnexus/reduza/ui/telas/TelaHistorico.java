package br.com.devnexus.reduza.ui.telas;

import br.com.devnexus.reduza.dao.CategoriaDAO;
import br.com.devnexus.reduza.dao.RegistroDAO;
import br.com.devnexus.reduza.dao.SetorDAO;
import br.com.devnexus.reduza.model.Categoria;
import br.com.devnexus.reduza.model.RegistroResiduo;
import br.com.devnexus.reduza.model.Setor;
import br.com.devnexus.reduza.ui.Tela;
import br.com.devnexus.reduza.ui.componentes.Botao;
import br.com.devnexus.reduza.ui.componentes.Campo;
import br.com.devnexus.reduza.ui.componentes.Cartao;
import br.com.devnexus.reduza.ui.componentes.Pagina;
import br.com.devnexus.reduza.ui.componentes.RenderizadorCategoria;
import br.com.devnexus.reduza.ui.componentes.Ui;
import br.com.devnexus.reduza.util.ErroValidacao;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Consulta de todos os registros com filtros, total do período e exportação para planilha (CSV). */
public class TelaHistorico extends Tela {

    private static final long serialVersionUID = 1L;

    private final RegistroDAO registros = new RegistroDAO();
    private final Campo de = new Campo("dd/mm/aaaa");
    private final Campo ate = new Campo("dd/mm/aaaa");
    private final JComboBox<Setor> setor = Ui.combo();
    private final JComboBox<Categoria> categoria = Ui.combo();
    private final JLabel resumo = Ui.rotulo(" ");
    private final DefaultTableModel modelo = Ui.modelo("Data", "Setor", "Categoria", "kg", "Observação",
            "Registrado por");
    private final JTable tabela = new JTable(modelo);
    private List<RegistroResiduo> atuais = List.of();

    public TelaHistorico() {
        categoria.setRenderer(new RenderizadorCategoria("Todas"));
        JPanel filtros = new JPanel(new GridLayout(1, 4, 14, 0));
        filtros.setOpaque(false);
        filtros.add(Ui.campoRotulado("De", de));
        filtros.add(Ui.campoRotulado("Até", ate));
        filtros.add(Ui.campoRotulado("Setor", setor));
        filtros.add(Ui.campoRotulado("Categoria", categoria));
        Botao filtrar = new Botao("Filtrar");
        filtrar.addActionListener(e -> buscar());
        Botao exportar = new Botao("Exportar CSV", Botao.Estilo.SECUNDARIO);
        exportar.addActionListener(e -> exportar());
        Cartao topo = new Cartao(new BorderLayout(0, 14));
        topo.add(filtros, BorderLayout.CENTER);
        topo.add(Ui.linha(10, filtrar, exportar), BorderLayout.SOUTH);

        Cartao lista = new Cartao(new BorderLayout(0, 12));
        lista.add(resumo, BorderLayout.NORTH);
        lista.add(Ui.tabela(tabela, 3), BorderLayout.CENTER);
        lista.setPreferredSize(new Dimension(800, 520));
        tabela.getColumnModel().getColumn(4).setPreferredWidth(220);

        Pagina p = new Pagina();
        p.linha(topo);
        p.linha(lista);
        p.fim();
        add(p, BorderLayout.CENTER);
    }

    @Override
    public String titulo() {
        return "Histórico";
    }

    @Override
    public String subtitulo() {
        return "Todos os registros, com filtros por período, setor e categoria";
    }

    @Override
    public void atualizar() {
        Setor s = (Setor) setor.getSelectedItem();
        Categoria c = (Categoria) categoria.getSelectedItem();
        setor.removeAllItems();
        setor.addItem(null);
        new SetorDAO().listar(false).forEach(setor::addItem);
        categoria.removeAllItems();
        categoria.addItem(null);
        new CategoriaDAO().listar(false).forEach(categoria::addItem);
        if (s != null) {
            for (int i = 1; i < setor.getItemCount(); i++) {
                if (setor.getItemAt(i).id() == s.id()) {
                    setor.setSelectedIndex(i);
                }
            }
        }
        if (c != null) {
            for (int i = 1; i < categoria.getItemCount(); i++) {
                if (categoria.getItemAt(i).id() == c.id()) {
                    categoria.setSelectedIndex(i);
                }
            }
        }
        if (de.getText().isBlank()) {
            LocalDate hoje = LocalDate.now();
            de.setText(Formatos.data(hoje.withDayOfMonth(1).minusMonths(5)));
            ate.setText(Formatos.data(hoje));
        }
        buscar();
    }

    private void buscar() {
        try {
            LocalDate d1 = de.getText().isBlank() ? null : Formatos.lerData(de.getText(), "a data inicial");
            LocalDate d2 = ate.getText().isBlank() ? null : Formatos.lerData(ate.getText(), "a data final");
            if (d1 != null && d2 != null && d2.isBefore(d1)) {
                throw new ErroValidacao("A data final é anterior à inicial.");
            }
            Setor s = (Setor) setor.getSelectedItem();
            Categoria c = (Categoria) categoria.getSelectedItem();
            atuais = registros.buscar(d1, d2, s == null ? null : s.id(), c == null ? null : c.id());
            modelo.setRowCount(0);
            double total = 0;
            for (RegistroResiduo r : atuais) {
                total += r.pesoKg().doubleValue();
                modelo.addRow(new Object[]{Formatos.data(r.data()), r.setor(), r.categoria(),
                        Formatos.decimal(r.pesoKg().doubleValue(), 1), r.observacao() == null ? "" : r.observacao(),
                        r.usuario()});
            }
            resumo.setText(Formatos.inteiro(atuais.size()) + " registros · total de "
                    + Formatos.kgComUnidade(total) + " no período");
        } catch (RuntimeException ex) {
            Ui.erro(this, ex.getMessage());
        }
    }

    /** CSV com ponto e vírgula e BOM: abre direto no Excel em português. */
    private void exportar() {
        if (atuais.isEmpty()) {
            Ui.erro(this, "Não há registros para exportar com esses filtros.");
            return;
        }
        JFileChooser escolha = new JFileChooser();
        escolha.setSelectedFile(new File("reduza_historico_" + LocalDate.now() + ".csv"));
        if (escolha.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File arquivo = escolha.getSelectedFile();
        try (PrintWriter w = new PrintWriter(arquivo, StandardCharsets.UTF_8)) {
            w.print('﻿');
            w.println("Data;Setor;Categoria;Peso (kg);Observação;Registrado por");
            for (RegistroResiduo r : atuais) {
                w.println(String.join(";", Formatos.data(r.data()), csv(r.setor()), csv(r.categoria()),
                        Formatos.decimal(r.pesoKg().doubleValue(), 2).replace(".", ""),
                        csv(r.observacao()), csv(r.usuario())));
            }
            Ui.erro(this, "Arquivo salvo: " + arquivo.getAbsolutePath());
        } catch (IOException ex) {
            Ui.erro(this, "Não foi possível salvar o arquivo: " + ex.getMessage());
        }
    }

    private static String csv(String s) {
        if (s == null) {
            return "";
        }
        return s.contains(";") || s.contains("\"") ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }
}
