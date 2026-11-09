package com.bikerent.ui.comp;

import com.bikerent.util.Theme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class Banner extends JPanel {

    private String judul   = "Selamat Datang!";
    private String subJudul = "Sistem Informasi Pengelolaan Rental Sepeda";
    private String catatan  = "Nikmati perjalananmu dengan sepeda terbaik!";

    public Banner() {
        setOpaque(false);
        setPreferredSize(new Dimension(700, 140));
    }

    public void teks(String judul, String subJudul, String catatan) {
        this.judul = judul; this.subJudul = subJudul; this.catatan = catatan;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.aa(g);
        int w = getWidth(), h = getHeight();

        g.clip(new RoundRectangle2D.Double(0, 0, w, h, 14, 14));

        g.setPaint(new GradientPaint(0, 0, new Color(0x2B1B3D), 0, h, new Color(0xF0843C)));
        g.fillRect(0, 0, w, h);

        int sx = (int) (w * 0.74), sy = (int) (h * 0.62);
        g.setColor(new Color(0xFFC98A));
        g.fill(new Ellipse2D.Double(sx - 34, sy - 34, 68, 68));
        g.setColor(new Color(255, 214, 150, 70));
        g.fill(new Ellipse2D.Double(sx - 54, sy - 54, 108, 108));

        gunung(g, w, h, 0.58, new Color(0x6B3E56), 0.0);
        gunung(g, w, h, 0.70, new Color(0x4A2A44), 0.35);
        gunung(g, w, h, 0.82, new Color(0x2B1930), 0.7);

        g.setColor(new Color(0x1B1020));
        g.fillRect(0, (int) (h * 0.88), w, h);

        pesepeda(g, (int) (w * 0.62), (int) (h * 0.90), h * 0.62);

        g.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 165), w * 0.72f, 0, new Color(0, 0, 0, 0)));
        g.fillRect(0, 0, w, h);

        g.setColor(Color.WHITE);
        g.setFont(Theme.fontBold(26));
        g.drawString(judul, 26, 48);
        g.setFont(Theme.fontBold(13));
        g.drawString(subJudul, 26, 72);
        g.setColor(new Color(255, 255, 255, 205));
        g.setFont(Theme.font(12));
        g.drawString(catatan, 26, 98);

        g.dispose();
    }

    private void gunung(Graphics2D g, int w, int h, double dasar, Color c, double geser) {
        Path2D.Double p = new Path2D.Double();
        double y0 = h * dasar;
        p.moveTo(-10, h);
        p.lineTo(-10, y0);
        int puncak = 5;
        for (int i = 0; i <= puncak; i++) {
            double x = w * (i / (double) puncak) + geser * 40;
            double tinggi = (i % 2 == 0 ? 0.16 : 0.09) * h;
            p.lineTo(x - w / (puncak * 2.0), y0);
            p.lineTo(x, y0 - tinggi);
        }
        p.lineTo(w + 10, y0);
        p.lineTo(w + 10, h);
        p.closePath();
        g.setColor(c);
        g.fill(p);
    }

    private void pesepeda(Graphics2D g, int x, int y, double tinggi) {
        Graphics2D s = (Graphics2D) g.create();
        double k = tinggi / 120.0;
        s.translate(x, y);
        s.scale(k, k);
        s.setColor(new Color(0x140A10));

        s.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        s.draw(new Ellipse2D.Double(-81, -52, 52, 52));
        s.draw(new Ellipse2D.Double(-21, -52, 52, 52));

        Path2D.Double rangka = new Path2D.Double();
        rangka.moveTo(-55, -26); rangka.lineTo(-45, -58);
        rangka.lineTo(-8, -62);
        rangka.lineTo(5, -26);
        rangka.moveTo(-45, -58); rangka.lineTo(-28, -26);
        rangka.moveTo(-28, -26); rangka.lineTo(-55, -26);
        rangka.moveTo(-28, -26); rangka.lineTo(-8, -62);
        s.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        s.draw(rangka);
        s.draw(new java.awt.geom.Line2D.Double(-16, -66, -2, -60));

        Path2D.Double badan = new Path2D.Double();
        badan.moveTo(-44, -66); badan.lineTo(-22, -92);
        badan.moveTo(-22, -92); badan.lineTo(-8, -64);
        badan.moveTo(-44, -66); badan.lineTo(-34, -42);
        badan.lineTo(-27, -24);
        s.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        s.draw(badan);
        s.fill(new Ellipse2D.Double(-24, -110, 19, 19));
        s.dispose();
    }
}
