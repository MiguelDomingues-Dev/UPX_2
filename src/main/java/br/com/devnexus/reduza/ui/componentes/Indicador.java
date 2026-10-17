package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Cartão de indicador (KPI): rótulo, valor grande e uma linha de detalhe. */
public class Indicador extends Cartao {

    private static final long serialVersionUID = 1L;
    private final JLabel valor = new JLabel("—");
    private final JLabel detalhe = new JLabel(" ");

    public Indicador(String rotulo) {
        JPanel col = Ui.coluna();
        JLabel r = Ui.texto(rotulo);
        valor.setFont(Tema.negrito(26));
        valor.setForeground(Tema.TEXTO);
        detalhe.setFont(Tema.normal(12));
        detalhe.setForeground(Tema.TEXTO_SUAVE);
        col.add(r);
        col.add(Ui.espaco(6));
        col.add(valor);
        col.add(Ui.espaco(4));
        col.add(detalhe);
        add(col);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 16));
    }

    public void definir(String v, String d, Color corValor, Color corDetalhe) {
        valor.setText(v);
        valor.setForeground(corValor == null ? Tema.TEXTO : corValor);
        detalhe.setText(d == null || d.isEmpty() ? " " : d);
        detalhe.setForeground(corDetalhe == null ? Tema.TEXTO_SUAVE : corDetalhe);
        detalhe.setFont(corDetalhe == null ? Tema.normal(12) : Tema.negrito(12));
    }
}
