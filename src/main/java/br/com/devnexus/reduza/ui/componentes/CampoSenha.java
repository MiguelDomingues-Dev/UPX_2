package br.com.devnexus.reduza.ui.componentes;

import br.com.devnexus.reduza.ui.Tema;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPasswordField;

public class CampoSenha extends JPasswordField {

    private static final long serialVersionUID = 1L;

    public CampoSenha() {
        setFont(Tema.normal(14));
        setForeground(Tema.TEXTO);
        setBorder(Campo.borda(Tema.BORDA_CAMPO));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(Campo.borda(Tema.VERDE));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(Campo.borda(Tema.BORDA_CAMPO));
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 120), 38);
    }
}
