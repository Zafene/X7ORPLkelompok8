package com.bikerent.ui.icon;

import com.bikerent.util.Theme;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class Ikon implements Icon {

    public interface Lukis { void draw(Graphics2D g); }

    private final Lukis lukis;
    private final int size;
    private final Color warna;
    private final float tebal;

    public Ikon(int size, Color warna, float tebal, Lukis lukis) {
        this.size = size;
        this.warna = warna;
        this.tebal = tebal;
        this.lukis = lukis;
    }

    public Ikon(int size, Color warna, Lukis lukis) { this(size, warna, 1.8f, lukis); }

    @Override public int getIconWidth()  { return size; }
    @Override public int getIconHeight() { return size; }

    @Override
    public void paintIcon(Component c, Graphics g0, int x, int y) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.aa(g);
        g.translate(x, y);
        double k = size / 24.0;
        g.scale(k, k);
        g.setColor(warna);
        g.setStroke(new BasicStroke(tebal, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        lukis.draw(g);
        g.dispose();
    }

    public Ikon warna(Color c) { return new Ikon(size, c, tebal, lukis); }
    public Ikon ukuran(int s)  { return new Ikon(s, warna, tebal, lukis); }

    private static void garis(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }
    private static void lingkaran(Graphics2D g, double cx, double cy, double r) {
        g.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
    }
    private static void lingkaranIsi(Graphics2D g, double cx, double cy, double r) {
        g.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
    }
    private static void kotak(Graphics2D g, double x, double y, double w, double h, double r) {
        g.draw(new RoundRectangle2D.Double(x, y, w, h, r, r));
    }
    private static Path2D.Double path(double... xy) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(xy[0], xy[1]);
        for (int i = 2; i < xy.length; i += 2) p.lineTo(xy[i], xy[i + 1]);
        return p;
    }

    public static Ikon beranda(int s, Color c) {
        return new Ikon(s, c, g -> {
            g.draw(path(3, 10.8, 12, 3.4, 21, 10.8));
            g.draw(path(5.3, 10, 5.3, 20.6, 18.7, 20.6, 18.7, 10));
            kotak(g, 9.6, 14.4, 4.8, 6.2, 1.2);
        });
    }

    public static Ikon sepeda(int s, Color c) {
        return new Ikon(s, c, 1.7f, g -> {
            lingkaran(g, 5.4, 16.4, 4.0);
            lingkaran(g, 18.6, 16.4, 4.0);
            g.draw(path(5.4, 16.4, 9.4, 8.6, 16.2, 8.6, 18.6, 16.4));
            garis(g, 9.4, 8.6, 12.6, 16.4);
            garis(g, 8.3, 7.4, 10.4, 7.4);
            garis(g, 15.3, 7.0, 17.4, 8.0);
            lingkaranIsi(g, 12.6, 16.4, 0.9);
        });
    }

    public static Ikon dokumen(int s, Color c) {
        return new Ikon(s, c, g -> {
            g.draw(path(14.2, 2.8, 5.2, 2.8, 5.2, 21.2, 18.8, 21.2, 18.8, 7.4, 14.2, 2.8, 14.2, 7.4, 18.8, 7.4));
            garis(g, 8.4, 12.2, 15.6, 12.2);
            garis(g, 8.4, 15.6, 15.6, 15.6);
            garis(g, 8.4, 18.4, 13.0, 18.4);
        });
    }

    public static Ikon grafik(int s, Color c) {
        return new Ikon(s, c, 2.0f, g -> {
            garis(g, 3.2, 20.6, 20.8, 20.6);
            garis(g, 6.6, 20.6, 6.6, 14.2);
            garis(g, 12.0, 20.6, 12.0, 8.4);
            garis(g, 17.4, 20.6, 17.4, 11.6);
        });
    }

    public static Ikon cari(int s, Color c) {
        return new Ikon(s, c, g -> {
            lingkaran(g, 10.6, 10.6, 6.2);
            garis(g, 15.2, 15.2, 20.4, 20.4);
        });
    }

    public static Ikon kalender(int s, Color c) {
        return new Ikon(s, c, g -> {
            kotak(g, 3.4, 5.2, 17.2, 15.6, 2.6);
            garis(g, 3.4, 10.0, 20.6, 10.0);
            garis(g, 8.2, 2.8, 8.2, 6.6);
            garis(g, 15.8, 2.8, 15.8, 6.6);
        });
    }

    public static Ikon pengguna(int s, Color c) {
        return new Ikon(s, c, g -> {
            lingkaran(g, 12, 8.6, 3.6);
            g.draw(new Arc2D.Double(4.6, 13.6, 14.8, 14.8, 20, 140, Arc2D.OPEN));
        });
    }

    public static Ikon panahBawah(int s, Color c) {
        return new Ikon(s, c, 2.0f, g -> g.draw(path(6.5, 9.5, 12, 15, 17.5, 9.5)));
    }

    public static Ikon panahKanan(int s, Color c) {
        return new Ikon(s, c, 2.0f, g -> g.draw(path(9.5, 5.5, 15.5, 12, 9.5, 18.5)));
    }

    public static Ikon panahKiri(int s, Color c) {
        return new Ikon(s, c, 2.0f, g -> g.draw(path(14.5, 5.5, 8.5, 12, 14.5, 18.5)));
    }

    public static Ikon tambah(int s, Color c) {
        return new Ikon(s, c, 2.0f, g -> { garis(g, 12, 5.5, 12, 18.5); garis(g, 5.5, 12, 18.5, 12); });
    }

    public static Ikon kurang(int s, Color c) {
        return new Ikon(s, c, 2.0f, g -> garis(g, 5.5, 12, 18.5, 12));
    }

    public static Ikon unduh(int s, Color c) {
        return new Ikon(s, c, g -> {
            garis(g, 12, 3.2, 12, 15.4);
            g.draw(path(7.2, 10.6, 12, 15.4, 16.8, 10.6));
            garis(g, 4.6, 20.2, 19.4, 20.2);
        });
    }

    public static Ikon papan(int s, Color c) {
        return new Ikon(s, c, g -> {
            kotak(g, 4.6, 4.4, 14.8, 16.4, 2.6);
            kotak(g, 8.6, 2.4, 6.8, 4.2, 1.6);
            garis(g, 8.2, 12.0, 15.8, 12.0);
            garis(g, 8.2, 16.0, 13.4, 16.0);
        });
    }

    public static Ikon kotak(int s, Color c) {
        return new Ikon(s, c, g -> {
            g.draw(path(12, 2.8, 20.6, 7.4, 20.6, 16.6, 12, 21.2, 3.4, 16.6, 3.4, 7.4, 12, 2.8));
            g.draw(path(3.4, 7.4, 12, 12.0, 20.6, 7.4));
            garis(g, 12, 12, 12, 21.2);
        });
    }

    public static Ikon centang(int s, Color c) {
        return new Ikon(s, c, g -> {
            lingkaran(g, 12, 12, 8.8);
            g.draw(path(7.6, 12.2, 10.8, 15.4, 16.6, 8.8));
        });
    }

    public static Ikon dompet(int s, Color c) {
        return new Ikon(s, c, g -> {
            kotak(g, 3.2, 6.2, 17.6, 13.6, 2.8);
            g.draw(path(3.2, 9.6, 16.4, 9.6, 16.4, 4.4, 6.0, 4.4));
            lingkaranIsi(g, 16.6, 13.0, 1.4);
        });
    }

    public static Ikon jam(int s, Color c) {
        return new Ikon(s, c, g -> {
            lingkaran(g, 12, 12, 8.8);
            g.draw(path(12, 7.0, 12, 12.2, 15.8, 14.4));
        });
    }

    public static Ikon penggaris(int s, Color c) {
        return new Ikon(s, c, g -> {
            kotak(g, 2.6, 8.4, 18.8, 7.2, 1.8);
            garis(g, 7.2, 8.4, 7.2, 11.6);
            garis(g, 12.0, 8.4, 12.0, 12.4);
            garis(g, 16.8, 8.4, 16.8, 11.6);
        });
    }

    public static Ikon label(int s, Color c) {
        return new Ikon(s, c, g -> {
            g.draw(path(11.6, 2.8, 21.2, 12.4, 12.4, 21.2, 2.8, 11.6, 2.8, 2.8, 11.6, 2.8));
            lingkaran(g, 7.2, 7.2, 1.6);
        });
    }

    public static Ikon petir(int s, Color c) {
        return new Ikon(s, c, 1.6f, g -> g.draw(path(13.4, 2.4, 6.2, 13.6, 11.4, 13.6, 10.6, 21.6, 17.8, 10.4, 12.6, 10.4, 13.4, 2.4)));
    }

    public static Ikon titik(int s, Color c) {
        return new Ikon(s, c, g -> { g.setColor(c); lingkaranIsi(g, 12, 12, 8); });
    }

    public static Ikon tunai(int s, Color c) {
        return new Ikon(s, c, g -> {
            kotak(g, 2.6, 6.4, 18.8, 11.2, 2.2);
            lingkaran(g, 12, 12, 2.8);
            garis(g, 6.0, 12, 6.6, 12);
            garis(g, 17.4, 12, 18.0, 12);
        });
    }

    public static Ikon bank(int s, Color c) {
        return new Ikon(s, c, g -> {
            g.draw(path(2.8, 9.4, 12, 3.6, 21.2, 9.4));
            garis(g, 3.6, 20.6, 20.4, 20.6);
            garis(g, 6.6, 12.0, 6.6, 18.4);
            garis(g, 12.0, 12.0, 12.0, 18.4);
            garis(g, 17.4, 12.0, 17.4, 18.4);
        });
    }

    public static Ikon info(int s, Color c) {
        return new Ikon(s, c, g -> {
            lingkaran(g, 12, 12, 8.8);
            garis(g, 12, 11.0, 12, 16.4);
            lingkaranIsi(g, 12, 7.8, 0.95);
        });
    }

    public static Ikon kunci(int s, Color c) {
        return new Ikon(s, c, g -> {
            kotak(g, 5.5, 10.4, 13, 10, 2.2);
            g.draw(new Arc2D.Double(7.6, 3.6, 8.8, 10.4, 0, 180, Arc2D.OPEN));
            lingkaranIsi(g, 12, 14.6, 1.1);
            garis(g, 12, 15.6, 12, 17.6);
        });
    }

    public static Ikon keluar(int s, Color c) {
        return new Ikon(s, c, g -> {
            g.draw(path(13, 4, 6, 4, 6, 20, 13, 20));
            garis(g, 10, 12, 20, 12);
            g.draw(path(16.4, 8.4, 20, 12, 16.4, 15.6));
        });
    }

    public static Ikon minimize(int s, Color c) { return new Ikon(s, c, 1.4f, g -> garis(g, 6, 12, 18, 12)); }
    public static Ikon maximize(int s, Color c) { return new Ikon(s, c, 1.4f, g -> kotak(g, 6.5, 6.5, 11, 11, 1.6)); }
    public static Ikon tutup(int s, Color c) {
        return new Ikon(s, c, 1.4f, g -> { garis(g, 7, 7, 17, 17); garis(g, 17, 7, 7, 17); });
    }
}
