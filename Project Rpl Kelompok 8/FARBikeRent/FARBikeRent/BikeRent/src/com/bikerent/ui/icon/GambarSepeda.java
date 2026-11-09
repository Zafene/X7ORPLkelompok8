package com.bikerent.ui.icon;

import com.bikerent.util.Theme;

import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class GambarSepeda extends JPanel {

    public static final String MTB   = "MTB";
    public static final String ROAD  = "Road";
    public static final String CITY  = "City";
    public static final String EBIKE = "E-Bike";

    private String tipe;
    private byte[] gambar;

    public GambarSepeda(String tipe)              { this(tipe, null); }
    public GambarSepeda(String tipe, byte[] gambar) {
        this.tipe = tipe; this.gambar = gambar;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 120));
    }

    public void setTipe(String t)   { this.tipe = t; this.gambar = null; repaint(); }
    public void setGambar(byte[] g) { this.gambar = g; repaint(); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.aa(g);
        if (gambar != null) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(gambar));
                if (img != null) {
                    double ratio = Math.min((double)getWidth()/img.getWidth(), (double)getHeight()/img.getHeight());
                    int dw = (int)(img.getWidth()*ratio), dh = (int)(img.getHeight()*ratio);
                    int dx = (getWidth()-dw)/2, dy = (getHeight()-dh)/2;
                    g.drawImage(img, dx, dy, dw, dh, null);
                    g.dispose(); return;
                }
            } catch (java.io.IOException ignored) {}
        }
        lukis(g, tipe, 0, 0, getWidth(), getHeight(), new Color(0x1F2937));
        g.dispose();
    }

    public static void lukis(Graphics2D g0, String tipe, int x, int y, int w, int h, Color warna) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.aa(g);
        double k = Math.min(w / 200.0, h / 120.0);
        g.translate(x + (w - 200 * k) / 2, y + (h - 120 * k) / 2);
        g.scale(k, k);
        g.setColor(warna);

        if (MTB.equals(tipe))        mtb(g);
        else if (ROAD.equals(tipe))  road(g);
        else if (CITY.equals(tipe))  city(g);
        else                          ebike(g);

        g.dispose();
    }

    private static void roda(Graphics2D g, double cx, double cy, double r, float tebalBan, int jumlahJari) {
        g.setStroke(new BasicStroke(tebalBan, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        g.setStroke(new BasicStroke(1f));
        for (int i = 0; i < jumlahJari; i++) {
            double a = Math.PI * 2 * i / jumlahJari;
            g.draw(new Line2D.Double(cx, cy, cx + Math.cos(a) * (r - 4), cy + Math.sin(a) * (r - 4)));
        }
        g.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Ellipse2D.Double(cx - 3, cy - 3, 6, 6));
    }

    private static void rantai(Graphics2D g, double cx, double cy) {
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Ellipse2D.Double(cx - 9, cy - 9, 18, 18));
        g.draw(new Line2D.Double(cx, cy, cx + 12, cy + 7));
    }

    private static void garis(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private static Path2D.Double jalur(double... xy) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(xy[0], xy[1]);
        for (int i = 2; i < xy.length; i += 2) p.lineTo(xy[i], xy[i + 1]);
        return p;
    }

    private static void mtb(Graphics2D g) {
        roda(g, 45, 80, 30, 5.0f, 10);
        roda(g, 155, 80, 30, 5.0f, 10);
        g.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(jalur(45, 80, 72, 40, 128, 40, 155, 80));
        garis(g, 72, 40, 100, 80);
        garis(g, 100, 80, 45, 80);
        garis(g, 128, 40, 100, 80);
        garis(g, 128, 40, 155, 80);
        g.setStroke(new BasicStroke(5.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        garis(g, 140, 58, 152, 74);
        g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        garis(g, 64, 34, 82, 34);
        garis(g, 72, 40, 72, 34);
        garis(g, 120, 32, 138, 32);
        garis(g, 128, 40, 129, 32);
        rantai(g, 100, 80);
    }

    private static void road(Graphics2D g) {
        roda(g, 45, 80, 32, 2.6f, 14);
        roda(g, 155, 80, 32, 2.6f, 14);
        g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(jalur(45, 80, 74, 38, 130, 38, 155, 80));
        garis(g, 74, 38, 102, 80);
        garis(g, 102, 80, 45, 80);
        garis(g, 130, 38, 102, 80);
        garis(g, 66, 32, 84, 32);
        garis(g, 74, 38, 74, 32);

        g.draw(new Arc2D.Double(122, 24, 22, 22, 300, 250, Arc2D.OPEN));
        garis(g, 130, 38, 131, 30);
        rantai(g, 102, 80);
    }

    private static void city(Graphics2D g) {
        roda(g, 45, 82, 28, 3.2f, 12);
        roda(g, 155, 82, 28, 3.2f, 12);
        g.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Arc2D.Double(60, 30, 86, 70, 30, 120, Arc2D.OPEN));
        garis(g, 45, 82, 74, 44);
        garis(g, 74, 44, 100, 82);
        garis(g, 100, 82, 45, 82);
        garis(g, 134, 42, 100, 82);
        garis(g, 134, 42, 155, 82);
        garis(g, 66, 38, 84, 38);
        garis(g, 74, 44, 74, 38);
        garis(g, 134, 42, 136, 28);
        garis(g, 128, 26, 144, 26);

        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new RoundRectangle2D.Double(138, 32, 30, 20, 4, 4));
        garis(g, 146, 32, 146, 52);
        garis(g, 156, 32, 156, 52);
        g.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        rantai(g, 100, 82);
    }

    private static void ebike(Graphics2D g) {
        roda(g, 48, 84, 26, 4.4f, 10);
        roda(g, 152, 84, 26, 4.4f, 10);
        g.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(jalur(48, 84, 70, 62, 118, 62, 136, 44));
        garis(g, 136, 44, 152, 84);
        garis(g, 70, 62, 78, 46);
        garis(g, 100, 84, 48, 84);
        garis(g, 118, 62, 100, 84);
        garis(g, 70, 56, 88, 56);
        garis(g, 78, 46, 78, 40);
        garis(g, 70, 40, 88, 40);
        garis(g, 136, 44, 138, 30);
        garis(g, 130, 28, 146, 28);

        g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new RoundRectangle2D.Double(82, 55, 30, 14, 4, 4));
        Color simpan = g.getColor();
        g.setColor(Theme.ORANGE);
        g.fill(jalur(99, 57, 92, 63, 96, 63, 94, 68, 101, 61, 97, 61, 99, 57));
        g.setColor(simpan);
        g.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        rantai(g, 100, 84);
    }
}
