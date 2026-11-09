package com.bikerent.ui.comp;

import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.geom.RoundRectangle2D;

public class Kartu extends JPanel {

    private int radius = 14;
    private Color isi = Theme.CARD;
    private Color garisTepi = Theme.BORDER;
    private boolean bayangan = false;

    public Kartu() { this(new BorderLayout()); }

    public Kartu(LayoutManager lm) {
        super(lm);
        setOpaque(false);
    }

    public Kartu radius(int r)        { this.radius = r; return this; }
    public Kartu isi(Color c)         { this.isi = c; return this; }
    public Kartu garisTepi(Color c)   { this.garisTepi = c; return this; }
    public Kartu bayangan(boolean b)  { this.bayangan = b; return this; }
    public Kartu padding(int t, int l, int b, int r) {
        setBorder(BorderFactory.createEmptyBorder(t, l, b, r));
        return this;
    }
    public Kartu padding(int semua) { return padding(semua, semua, semua, semua); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.aa(g);
        int w = getWidth(), h = getHeight();

        if (bayangan) {
            for (int i = 4; i >= 1; i--) {
                g.setColor(new Color(17, 24, 39, 4));
                g.fill(new RoundRectangle2D.Double(i, i + 1, w - i * 2, h - i * 2, radius, radius));
            }
        }
        if (isi != null) {
            g.setColor(isi);
            g.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, radius, radius));
        }
        if (garisTepi != null) {
            g.setColor(garisTepi);
            g.setStroke(new BasicStroke(1f));
            g.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 2, radius, radius));
        }
        g.dispose();
        super.paintComponent(g0);
    }

    public static JLabel judul(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(Theme.fontBold(14));
        l.setForeground(Theme.TEXT);
        return l;
    }

    public static JLabel label(String teks, int size, Color warna, boolean tebal) {
        JLabel l = new JLabel(teks);
        l.setFont(tebal ? Theme.fontBold(size) : Theme.font(size));
        l.setForeground(warna);
        return l;
    }

    public static JLabel labelIkon(String teks, Icon ikon, int size, Color warna, boolean tebal) {
        JLabel l = label(teks, size, warna, tebal);
        l.setIcon(ikon);
        l.setIconTextGap(8);
        return l;
    }

    public static JPanel baris(Component kiri, Component kanan) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        if (kiri != null)  p.add(kiri, BorderLayout.WEST);
        if (kanan != null) p.add(kanan, BorderLayout.EAST);
        return p;
    }
}
