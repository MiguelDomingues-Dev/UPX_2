package br.com.devnexus.reduza.ui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** Ícone da janela gerado em código (não depende de arquivos de imagem). */
final class Icones {

    private Icones() {
    }

    static List<Image> janela() {
        List<Image> l = new ArrayList<>();
        for (int t : new int[]{16, 32, 48, 64, 128}) {
            BufferedImage img = new BufferedImage(t, t, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            Tema.suavizar(g);
            Tema.desenharLogo(g, 0, 0, t);
            g.dispose();
            l.add(img);
        }
        return l;
    }
}
