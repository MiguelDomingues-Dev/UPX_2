package br.com.devnexus.reduza.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Formatação e leitura de números, valores e datas no padrão brasileiro. */
public final class Formatos {

    public static final Locale BR = Locale.forLanguageTag("pt-BR");
    private static final DecimalFormatSymbols SIMBOLOS = DecimalFormatSymbols.getInstance(BR);
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DIA_MES = DateTimeFormatter.ofPattern("dd/MM");
    private static final String[] MESES = {"jan.", "fev.", "mar.", "abr.", "maio", "jun.", "jul.", "ago.",
            "set.", "out.", "nov.", "dez."};
    private static final String[] MESES_LONGOS = {"janeiro", "fevereiro", "março", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};

    private Formatos() {
    }

    private static DecimalFormat padrao(String p) {
        return new DecimalFormat(p, SIMBOLOS);
    }

    /** 2110 -> "2.110"; 12.5 -> "12,5" */
    public static String kg(double valor) {
        return (Math.abs(valor) >= 100 ? padrao("#,##0") : padrao("#,##0.#")).format(valor);
    }

    public static String kgComUnidade(double valor) {
        return kg(valor) + " kg";
    }

    public static String inteiro(double valor) {
        return padrao("#,##0").format(valor);
    }

    public static String decimal(double valor, int casas) {
        return padrao("#,##0." + "0".repeat(Math.max(1, casas))).format(valor);
    }

    public static String moeda(double valor) {
        return "R$ " + padrao("#,##0.00").format(valor);
    }

    /** Valores pequenos, como custo por uso: R$ 0,065 */
    public static String moedaFina(double valor) {
        return "R$ " + padrao("#,##0.00#").format(valor);
    }

    public static String percentual(double valor) {
        return padrao("#,##0.#").format(valor) + "%";
    }

    public static String data(LocalDate d) {
        return d == null ? "" : DATA.format(d);
    }

    public static String diaMes(LocalDate d) {
        return d == null ? "" : DIA_MES.format(d);
    }

    public static String mesCurto(YearMonth m) {
        return MESES[m.getMonthValue() - 1];
    }

    public static String mesAno(YearMonth m) {
        String nome = MESES_LONGOS[m.getMonthValue() - 1];
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1) + " de " + m.getYear();
    }

    public static String mesLongo(YearMonth m) {
        return MESES_LONGOS[m.getMonthValue() - 1];
    }

    /** Lê "12,5", "1.250,75" ou "12.5". Lança ErroValidacao com mensagem amigável. */
    public static BigDecimal lerDecimal(String texto, String campo) {
        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) {
            throw new ErroValidacao("Informe " + campo + ".");
        }
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(t).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new ErroValidacao(campo.substring(0, 1).toUpperCase() + campo.substring(1)
                    + " deve ser um número, como 12,5.");
        }
    }

    public static int lerInteiro(String texto, String campo) {
        String t = texto == null ? "" : texto.trim().replace(".", "");
        if (t.isEmpty()) {
            throw new ErroValidacao("Informe " + campo + ".");
        }
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            throw new ErroValidacao(campo.substring(0, 1).toUpperCase() + campo.substring(1)
                    + " deve ser um número inteiro.");
        }
    }

    public static LocalDate lerData(String texto, String campo) {
        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) {
            throw new ErroValidacao("Informe " + campo + ".");
        }
        try {
            return LocalDate.parse(t, DATA);
        } catch (DateTimeParseException e) {
            throw new ErroValidacao(campo.substring(0, 1).toUpperCase() + campo.substring(1)
                    + " deve estar no formato dd/mm/aaaa.");
        }
    }

    /** Converte para double sem perder o sinal; nulo vira 0. */
    public static double d(BigDecimal v) {
        return v == null ? 0 : v.doubleValue();
    }

    public static NumberFormat numeroBr() {
        return NumberFormat.getNumberInstance(BR);
    }
}
