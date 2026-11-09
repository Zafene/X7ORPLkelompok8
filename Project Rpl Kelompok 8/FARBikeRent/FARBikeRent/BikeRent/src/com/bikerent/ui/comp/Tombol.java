package com.bikerent.ui.comp;

import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

public class Tombol extends JButton {

    public enum Gaya { ISI, GARIS, POLOS }

    private Gaya gaya = Gaya.ISI;
    private Color warna = Theme.ORANGE;
    private int radius = 9;
    private boolean hover, tekan;

    public Tombol(String teks) { this(teks, null); }

    public Tombol(String teks, Icon ikon) {
        super(teks);
        if (ikon != null) setIcon(ikon);
        setFont(Theme.fontBold(13));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setIconTextGap(8);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        setForeground(Color.WHITE);

        getModel().addChangeListener(e -> {
            boolean h = getModel().isRollover();
            boolean t = getModel().isPressed();
            if (h != hover || t != tekan) { hover = h; tekan = t; repaint(); }
        });
    }

    public Tombol gaya(Gaya g) {
        this.gaya = g;
        setForeground(g == Gaya.ISI ? Color.WHITE : Theme.TEXT);
        return this;
    }
    public Tombol warna(Color c)    { this.warna = c; return this; }
    public Tombol radius(int r)     { this.radius = r; return this; }
    public Tombol teksWarna(Color c){ setForeground(c); return this; }
    public Tombol padding(int t, int l, int b, int r) {
        setBorder(BorderFactory.createEmptyBorder(t, l, b, r));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.aa(g);
        int w = getWidth(), h = getHeight();
        RoundRectangle2D r = new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 2, radius, radius);

        switch (gaya) {
            case ISI:
                Color c = warna;
                if (tekan)      c = Theme.mix(warna, Color.BLACK, 0.18f);
                else if (hover) c = Theme.mix(warna, Color.BLACK, 0.08f);
                g.setColor(c);
                g.fill(r);
                break;
            case GARIS:
                g.setColor(hover ? Theme.mix(Color.WHITE, warna, 0.08f) : Color.WHITE);
                g.fill(r);
                g.setColor(hover ? warna : Theme.BORDER);
                g.setStroke(new BasicStroke(1.2f));
                g.draw(r);
                break;
            case POLOS:
                if (hover) {
                    g.setColor(Theme.alpha(warna, 26));
                    g.fill(r);
                }
                break;
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
