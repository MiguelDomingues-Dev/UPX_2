package br.com.devnexus.reduza.ui.telas;

import br.com.devnexus.reduza.dao.CategoriaDAO;
import br.com.devnexus.reduza.dao.InspecaoDAO;
import br.com.devnexus.reduza.dao.ItemDAO;
import br.com.devnexus.reduza.dao.SetorDAO;
import br.com.devnexus.reduza.dao.UsuarioDAO;
import br.com.devnexus.reduza.model.Categoria;
import br.com.devnexus.reduza.model.Item;
import br.com.devnexus.reduza.model.Perfil;
import br.com.devnexus.reduza.model.PontoColeta;
import br.com.devnexus.reduza.model.Setor;
import br.com.devnexus.reduza.model.TipoItem;
import br.com.devnexus.reduza.model.Usuario;
import br.com.devnexus.reduza.servico.Senhas;
import br.com.devnexus.reduza.servico.Sessao;
import br.com.devnexus.reduza.ui.Tela;
import br.com.devnexus.reduza.ui.Tema;
import br.com.devnexus.reduza.ui.componentes.Abas;
import br.com.devnexus.reduza.ui.componentes.Campo;
import br.com.devnexus.reduza.ui.componentes.CampoSenha;
import br.com.devnexus.reduza.ui.componentes.PainelCadastro;
import br.com.devnexus.reduza.ui.componentes.RenderizadorCategoria;
import br.com.devnexus.reduza.ui.componentes.Ui;
import br.com.devnexus.reduza.util.ErroValidacao;
import br.com.devnexus.reduza.util.Formatos;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/** Cadastros da empresa: setores, categorias, itens, pontos de coleta e usuários. */
public class TelaCadastros extends Tela {

    private static final long serialVersionUID = 1L;
    private static final String[] CORES = {"Vermelho", "Azul", "Cinza", "Laranja", "Marrom", "Verde", "Amarelo",
            "Preto", "Branco", "Roxo"};

    private final Abas abas = new Abas(this::atualizarAba);
    private final List<PainelCadastro<?>> paineis;

    public TelaCadastros() {
        Setores setores = new Setores();
        Categorias categorias = new Categorias();
        Itens itens = new Itens();
        Pontos pontos = new Pontos();
        abas.adicionar("Setores", aba(setores));
        abas.adicionar("Categorias", aba(categorias));
        abas.adicionar("Itens do piloto", aba(itens));
        abas.adicionar("Pontos de coleta", aba(pontos));
        if (Sessao.usuario().perfil() == Perfil.ADMIN) {
            Usuarios usuarios = new Usuarios();
            abas.adicionar("Usuários", aba(usuarios));
            paineis = List.of(setores, categorias, itens, pontos, usuarios);
        } else {
            paineis = List.of(setores, categorias, itens, pontos);
        }
        add(abas, BorderLayout.CENTER);
    }

    private static JPanel aba(JPanel conteudo) {
        return conteudo;
    }

    @Override
    public String titulo() {
        return "Cadastros";
    }

    @Override
    public String subtitulo() {
        return "Dados que alimentam os registros e os cálculos";
    }

    @Override
    public void atualizar() {
        atualizarAba(abas.selecionada());
    }

    private void atualizarAba(int i) {
        if (i >= 0 && i < paineis.size()) {
            try {
                paineis.get(i).atualizar();
            } catch (RuntimeException e) {
                Ui.erro(this, e.getMessage());
            }
        }
    }

    public void abrirAba(int indice) {
        abas.selecionar(indice);
    }

    private static String obrigatorio(Campo c, String nome) {
        if (c.getText().isBlank()) {
            throw new ErroValidacao("Informe " + nome + ".");
        }
        return c.getText().trim();
    }

    // ------------------------------------------------------------------ setores

    private static final class Setores extends PainelCadastro<Setor> {
        private static final long serialVersionUID = 1L;
        private final SetorDAO dao = new SetorDAO();
        private final Campo nome = new Campo("Ex.: Produção");
        private final Campo descricao = new Campo("Opcional");
        private final Campo colaboradores = new Campo("Ex.: 25");

        Setores() {
            super("Setor", "Descrição", "Colaboradores", "Situação");
            montar();
        }

        @Override
        protected int[] numericas() {
            return new int[]{2};
        }

        @Override
        protected List<Setor> carregar() {
            return dao.listar(false);
        }

        @Override
        protected Object[] linha(Setor s) {
            return new Object[]{s.nome(), s.descricao() == null ? "" : s.descricao(), s.qtdColaboradores(),
                    s.ativo() ? "Ativo" : "Inativo"};
        }

        @Override
        protected JPanel formulario() {
            return formularioColuna(Ui.campoRotulado("Nome do setor", nome), Ui.campoRotulado("Descrição", descricao),
                    Ui.campoRotulado("Nº de colaboradores (para médias por pessoa)", colaboradores));
        }

        @Override
        protected void preencher(Setor s) {
            nome.setText(s == null ? "" : s.nome());
            descricao.setText(s == null || s.descricao() == null ? "" : s.descricao());
            colaboradores.setText(s == null ? "" : String.valueOf(s.qtdColaboradores()));
        }

        @Override
        protected void gravar(Setor s) {
            String n = obrigatorio(nome, "o nome do setor");
            int c = colaboradores.getText().isBlank() ? 0 : Formatos.lerInteiro(colaboradores.getText(),
                    "o número de colaboradores");
            if (c < 0) {
                throw new ErroValidacao("O número de colaboradores não pode ser negativo.");
            }
            if (s == null) {
                dao.inserir(n, descricao.getText(), c);
            } else {
                dao.atualizar(s.id(), n, descricao.getText(), c);
            }
        }

        @Override
        protected boolean ativo(Setor s) {
            return s.ativo();
        }

        @Override
        protected void definirAtivo(Setor s, boolean a) {
            dao.definirAtivo(s.id(), a);
        }
    }

    // ------------------------------------------------------------------ categorias

    private static final class Categorias extends PainelCadastro<Categoria> {
        private static final long serialVersionUID = 1L;
        private final CategoriaDAO dao = new CategoriaDAO();
        private final Campo nome = new Campo("Ex.: Vidro");
        private final JComboBox<String> cor = Ui.combo();
        private final JCheckBox reciclavel = Ui.caixa("Reciclável");
        private final JCheckBox perigoso = Ui.caixa("Perigoso (Classe I)");
        private final Campo custo = new Campo("Ex.: 0,35 (deixe vazio se não souber)");

        Categorias() {
            super("Categoria", "Cor do coletor", "Reciclável", "Custo/kg", "Situação");
            Arrays.stream(CORES).forEach(cor::addItem);
            montar();
        }

        @Override
        protected int[] numericas() {
            return new int[]{3};
        }

        @Override
        protected List<Categoria> carregar() {
            return dao.listar(false);
        }

        @Override
        protected Object[] linha(Categoria c) {
            return new Object[]{c.nome(), c.corColetor() == null ? "" : c.corColetor(), sim(c.reciclavel()),
                    c.custoKg() == null ? "—" : Formatos.moeda(c.custoKg().doubleValue()), c.ativo() ? "Ativa" : "Inativa"};
        }

        @Override
        protected JPanel formulario() {
            JPanel caixas = Ui.linha(16, reciclavel, perigoso);
            return formularioColuna(Ui.campoRotulado("Nome", nome),
                    Ui.campoRotulado("Cor do coletor (CONAMA 275/2001)", cor), caixas,
                    Ui.campoRotulado("Custo de destinação por kg (R$)", custo));
        }

        @Override
        protected void preencher(Categoria c) {
            nome.setText(c == null ? "" : c.nome());
            cor.setSelectedItem(c == null || c.corColetor() == null ? "Vermelho" : c.corColetor());
            reciclavel.setSelected(c != null && c.reciclavel());
            perigoso.setSelected(c != null && c.perigoso());
            custo.setText(c == null || c.custoKg() == null ? "" : Formatos.decimal(c.custoKg().doubleValue(), 2));
        }

        @Override
        protected void gravar(Categoria c) {
            String n = obrigatorio(nome, "o nome da categoria");
            BigDecimal k = custo.getText().isBlank() ? null : Formatos.lerDecimal(custo.getText(), "o custo por kg");
            if (k != null && k.signum() < 0) {
                throw new ErroValidacao("O custo por kg não pode ser negativo.");
            }
            String co = (String) cor.getSelectedItem();
            if (c == null) {
                dao.inserir(n, co, reciclavel.isSelected(), perigoso.isSelected(), k);
            } else {
                dao.atualizar(c.id(), n, co, reciclavel.isSelected(), perigoso.isSelected(), k);
            }
        }

        @Override
        protected boolean ativo(Categoria c) {
            return c.ativo();
        }

        @Override
        protected void definirAtivo(Categoria c, boolean a) {
            dao.definirAtivo(c.id(), a);
        }
    }

    // ------------------------------------------------------------------ itens

    private static final class Itens extends PainelCadastro<Item> {
        private static final long serialVersionUID = 1L;
        private final ItemDAO dao = new ItemDAO();
        private final Campo nome = new Campo("Ex.: Copo plástico 180 ml");
        private final JComboBox<Categoria> categoria = Ui.combo();
        private final JComboBox<TipoItem> tipo = Ui.combo();
        private final Campo peso = new Campo("Ex.: 1,8");
        private final Campo usos = new Campo("1 para descartável");
        private final Campo fonte = new Campo("De onde veio o peso (norma, catálogo, pesagem)");

        Itens() {
            super("Item", "Tipo", "Peso (g)", "Usos estimados");
            categoria.setRenderer(new RenderizadorCategoria(""));
            tipo.addItem(TipoItem.DESCARTAVEL);
            tipo.addItem(TipoItem.REUTILIZAVEL);
            montar();
        }

        @Override
        protected int[] numericas() {
            return new int[]{2, 3};
        }

        @Override
        protected boolean temAtivo() {
            return false;
        }

        @Override
        protected List<Item> carregar() {
            categoria.removeAllItems();
            new CategoriaDAO().listar(true).forEach(categoria::addItem);
            return dao.listar();
        }

        @Override
        protected Object[] linha(Item i) {
            return new Object[]{i.nome(), i.tipo().toString(), Formatos.decimal(i.pesoUnitarioG().doubleValue(), 1),
                    i.usosEstimados()};
        }

        @Override
        protected JPanel formulario() {
            return formularioColuna(Ui.campoRotulado("Nome", nome), Ui.campoRotulado("Vira resíduo de qual categoria", categoria),
                    Ui.campoRotulado("Tipo", tipo), Ui.campoRotulado("Peso por unidade (g)", peso),
                    Ui.campoRotulado("Usos estimados na vida útil", usos), Ui.campoRotulado("Fonte do peso", fonte));
        }

        @Override
        protected void preencher(Item i) {
            nome.setText(i == null ? "" : i.nome());
            tipo.setSelectedItem(i == null ? TipoItem.DESCARTAVEL : i.tipo());
            peso.setText(i == null ? "" : Formatos.decimal(i.pesoUnitarioG().doubleValue(), 2));
            usos.setText(i == null ? "1" : String.valueOf(i.usosEstimados()));
            fonte.setText(i == null || i.fontePeso() == null ? "" : i.fontePeso());
            if (i != null) {
                for (int k = 0; k < categoria.getItemCount(); k++) {
                    if (categoria.getItemAt(k).id() == i.categoriaId()) {
                        categoria.setSelectedIndex(k);
                    }
                }
            }
        }

        @Override
        protected void gravar(Item i) {
            String n = obrigatorio(nome, "o nome do item");
            Categoria c = (Categoria) categoria.getSelectedItem();
            if (c == null) {
                throw new ErroValidacao("Escolha a categoria.");
            }
            BigDecimal p = Formatos.lerDecimal(peso.getText(), "o peso por unidade");
            int u = Formatos.lerInteiro(usos.getText(), "os usos estimados");
            TipoItem t = (TipoItem) tipo.getSelectedItem();
            if (p.signum() <= 0 || u < 1) {
                throw new ErroValidacao("O peso deve ser maior que zero e os usos, pelo menos 1.");
            }
            if (t == TipoItem.DESCARTAVEL && u != 1) {
                throw new ErroValidacao("Um item descartável tem 1 uso.");
            }
            if (i == null) {
                dao.inserir(c.id(), n, t, p, u, fonte.getText());
            } else {
                dao.atualizar(i.id(), c.id(), n, t, p, u, fonte.getText());
            }
        }
    }

    // ------------------------------------------------------------------ pontos de coleta

    private static final class Pontos extends PainelCadastro<PontoColeta> {
        private static final long serialVersionUID = 1L;
        private final InspecaoDAO dao = new InspecaoDAO();
        private final JComboBox<Setor> setor = Ui.combo();
        private final Campo nome = new Campo("Ex.: Estação da copa");
        private final Campo local = new Campo("Ex.: ao lado da cafeteira");

        Pontos() {
            super("Setor", "Ponto de coleta", "Localização", "Situação");
            montar();
        }

        @Override
        protected List<PontoColeta> carregar() {
            setor.removeAllItems();
            new SetorDAO().listar(true).forEach(setor::addItem);
            return dao.pontos(false);
        }

        @Override
        protected Object[] linha(PontoColeta p) {
            return new Object[]{p.setor(), p.nome(), p.localizacao() == null ? "" : p.localizacao(),
                    p.ativo() ? "Ativo" : "Inativo"};
        }

        @Override
        protected JPanel formulario() {
            return formularioColuna(Ui.campoRotulado("Setor", setor), Ui.campoRotulado("Nome", nome),
                    Ui.campoRotulado("Localização", local));
        }

        @Override
        protected void preencher(PontoColeta p) {
            nome.setText(p == null ? "" : p.nome());
            local.setText(p == null || p.localizacao() == null ? "" : p.localizacao());
            if (p != null) {
                for (int k = 0; k < setor.getItemCount(); k++) {
                    if (setor.getItemAt(k).id() == p.setorId()) {
                        setor.setSelectedIndex(k);
                    }
                }
            }
        }

        @Override
        protected void gravar(PontoColeta p) {
            String n = obrigatorio(nome, "o nome do ponto");
            Setor s = (Setor) setor.getSelectedItem();
            if (s == null) {
                throw new ErroValidacao("Escolha o setor.");
            }
            if (p == null) {
                dao.inserirPonto(s.id(), n, local.getText());
            } else {
                dao.atualizarPonto(p.id(), s.id(), n, local.getText());
            }
        }

        @Override
        protected boolean ativo(PontoColeta p) {
            return p.ativo();
        }

        @Override
        protected void definirAtivo(PontoColeta p, boolean a) {
            dao.definirPontoAtivo(p.id(), a);
        }
    }

    // ------------------------------------------------------------------ usuários (só administrador)

    private static final class Usuarios extends PainelCadastro<Usuario> {
        private static final long serialVersionUID = 1L;
        private final UsuarioDAO dao = new UsuarioDAO();
        private final Campo nome = new Campo("Nome completo");
        private final Campo email = new Campo("email@empresa.com.br");
        private final JComboBox<Perfil> perfil = Ui.combo();
        private final CampoSenha senha = new CampoSenha();

        Usuarios() {
            super("Nome", "E-mail", "Perfil", "Situação");
            for (Perfil p : Perfil.values()) {
                perfil.addItem(p);
            }
            montar();
        }

        @Override
        protected List<Usuario> carregar() {
            return dao.listar();
        }

        @Override
        protected Object[] linha(Usuario u) {
            return new Object[]{u.nome(), u.email(), u.perfil().rotulo(), u.ativo() ? "Ativo" : "Inativo"};
        }

        @Override
        protected JPanel formulario() {
            return formularioColuna(Ui.campoRotulado("Nome", nome), Ui.campoRotulado("E-mail (login)", email),
                    Ui.campoRotulado("Perfil", perfil),
                    Ui.campoRotulado("Senha (na edição, deixe vazio para manter)", senha),
                    Ui.texto("<html><div style='width:300px'>Operador registra resíduos e inspeções. Gestor também "
                            + "cria metas e cadastros. Administrador também gerencia usuários.</div></html>"));
        }

        @Override
        protected void preencher(Usuario u) {
            nome.setText(u == null ? "" : u.nome());
            email.setText(u == null ? "" : u.email());
            perfil.setSelectedItem(u == null ? Perfil.OPERADOR : u.perfil());
            senha.setText("");
        }

        @Override
        protected void gravar(Usuario u) {
            String n = obrigatorio(nome, "o nome");
            String e = obrigatorio(email, "o e-mail");
            if (!e.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
                throw new ErroValidacao("E-mail inválido.");
            }
            Perfil p = (Perfil) perfil.getSelectedItem();
            char[] s = senha.getPassword();
            try {
                if (u == null || s.length > 0) {
                    String problema = Senhas.problemaNaSenha(s);
                    if (problema != null) {
                        throw new ErroValidacao(problema);
                    }
                }
                if (u == null) {
                    dao.inserir(n, e, Senhas.gerarHash(s), p);
                } else {
                    if (u.id() == Sessao.usuario().id() && p != Perfil.ADMIN) {
                        throw new ErroValidacao("Você não pode tirar o seu próprio perfil de administrador.");
                    }
                    dao.atualizar(u.id(), n, e, p);
                    if (s.length > 0) {
                        dao.trocarSenha(u.id(), Senhas.gerarHash(s));
                    }
                }
            } finally {
                Arrays.fill(s, '\0');
            }
        }

        @Override
        protected boolean ativo(Usuario u) {
            return u.ativo();
        }

        @Override
        protected void definirAtivo(Usuario u, boolean a) {
            if (!a && u.id() == Sessao.usuario().id()) {
                throw new ErroValidacao("Você não pode desativar o próprio usuário.");
            }
            if (!a && u.perfil() == Perfil.ADMIN && dao.contarAdminsAtivos() <= 1) {
                throw new ErroValidacao("É preciso manter pelo menos um administrador ativo.");
            }
            dao.definirAtivo(u.id(), a);
        }
    }
}
