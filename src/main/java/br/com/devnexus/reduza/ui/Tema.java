package br.com.devnexus.reduza.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Cores, fontes e o desenho da marca. Toda a identidade visual do sistema fica aqui. */
public final class Tema {

    public static final Color VERDE = new Color(0x127A63);
    public static final Color VERDE_HOVER = new Color(0x0E6652);
    public static final Color VERDE_ESCURO = new Color(0x10302A);
    public static final Color VERDE_MENU_ATIVO = new Color(0x1E4A41);
    public static final Color VERDE_CLARO = new Color(0xD6EBE2);
    public static final Color FUNDO = new Color(0xF3F6F4);
    public static final Color BRANCO = Color.WHITE;
    public static final Color TEXTO = new Color(0x10302A);
    public static final Color TEXTO_SUAVE = new Color(0x5A6B65);
    public static final Color TEXTO_MENU = new Color(0xB9CEC6);
    public static final Color LINHA = new Color(0xE1E8E4);
    public static final Color BORDA_CAMPO = new Color(0xC9D5CF);
    public static final Color ERRO = new Color(0xB3261E);
    public static final Color ERRO_CLARO = new Color(0xFBE9E7);
    public static final Color ALERTA = new Color(0xB86E00);
    public static final Color SUCESSO_CLARO = new Color(0xE3F2EC);

    /** Cores dos coletores segundo a Resolução CONAMA nº 275/2001. */
    private static final Map<String, Color> CORES_CONAMA = Map.of(
            "vermelho", new Color(0xD9473F),
            "azul", new Color(0x2F6FD0),
            "cinza", new Color(0x7A8187),
            "laranja", new Color(0xE8862A),
            "marrom", new Color(0x8B5A2B),
            "verde", new Color(0x2E9E4F),
            "amarelo", new Color(0xE5B800),
            "preto", new Color(0x333333),
            "branco", new Color(0xB8C2BE),
            "roxo", new Color(0x7D4CA8));

    private static final Color[] RESERVA = {new Color(0x127A63), new Color(0x6B5BD2), new Color(0xC2558B),
            new Color(0x3F8FA8)};

    private static final String FAMILIA = escolherFonte();

    private Tema() {
    }

    private static String escolherFonte() {
        List<String> instaladas = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String f : new String[]{"Segoe UI", "Inter", "Roboto", "Noto Sans", "DejaVu Sans"}) {
            if (instaladas.contains(f)) {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font fonte(int estilo, float tamanho) {
        return new Font(FAMILIA, estilo, Math.round(tamanho));
    }

    public static Font normal(float tamanho) {
        return fonte(Font.PLAIN, tamanho);
    }

    public static Font negrito(float tamanho) {
        return fonte(Font.BOLD, tamanho);
    }

    /** Cor do coletor a partir do texto cadastrado ("Vermelho", "Azul"...). */
    public static Color corColetor(String nomeCor, int indiceReserva) {
        if (nomeCor != null) {
            Color c = CORES_CONAMA.get(nomeCor.trim().toLowerCase());
            if (c != null) {
                return c;
            }
        }
        return RESERVA[Math.floorMod(indiceReserva, RESERVA.length)];
    }

    public static Color misturar(Color a, Color b, double t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /** Ícone do Reduza: quadrado verde com três barras que diminuem (redução). */
    public static void desenharLogo(Graphics2D g, double x, double y, double t) {
        g.setColor(VERDE);
        g.fill(new RoundRectangle2D.Double(x, y, t, t, t * 0.32, t * 0.32));
        g.setColor(Color.WHITE);
        double bl = t * 0.13;
        double base = y + t * 0.74;
        double[] alt = {0.50, 0.36, 0.22};
        for (int i = 0; i < 3; i++) {
            double bx = x + t * 0.22 + i * (bl + t * 0.08);
            g.fill(new RoundRectangle2D.Double(bx, base - t * alt[i], bl, t * alt[i], bl * 0.6, bl * 0.6));
        }
    }
}
